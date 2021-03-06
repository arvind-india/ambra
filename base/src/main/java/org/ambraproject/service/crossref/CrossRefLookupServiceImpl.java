/*
 * Copyright (c) 2006-2014 by Public Library of Science
 *
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.service.crossref;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ambraproject.filestore.FileStoreException;
import org.ambraproject.filestore.FileStoreService;
import org.ambraproject.service.hibernate.HibernateServiceImpl;
import org.ambraproject.service.xml.XMLServiceImpl;
import org.ambraproject.util.XPathUtil;
import org.ambraproject.views.CrossRefSearch;
import org.ambraproject.xml.transform.cache.CachedSource;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query crossref for article details
 *
 * @author Joe Osowski
 */
public class CrossRefLookupServiceImpl extends HibernateServiceImpl implements CrossRefLookupService {

  private static final Logger log = LoggerFactory.getLogger(CrossRefLookupServiceImpl.class);

  private String crossRefUrl;
  private HttpClient httpClient;
  private FileStoreService fileStoreService;

  /**
   * Store the harvested citation data
   *
   * @param articleDOI
   * @param keyColumn
   * @param citationDOI
   */
  @Transactional
  private void setCitationDoi(final String articleDOI, final long keyColumn, final String citationDOI) {
    hibernateTemplate.execute(new HibernateCallback<Object>() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Query query = session.createSQLQuery("select articleID from article where doi = :doi")
          .setString("doi", articleDOI);

        long articleID = ((BigInteger)query.uniqueResult()).longValue();

        query = session.createSQLQuery("update citedArticle set doi = :doi, lastModified = NOW()" +
          " where articleID = :articleID and keyColumn = :keyColumn")
          .setString("doi", citationDOI)
          .setLong("articleID", articleID)
          .setLong("keyColumn", keyColumn);

        if(query.executeUpdate() == 0) {
          log.error("Error setting articleID: {}, Key: {} to value: {}", new Object[] { articleID, keyColumn, citationDOI });
          //throw new HibernateException("No rows updated for articleID: " + articleID + " key: " + keyColumn);
        } else {
          log.debug("Set articleID: {}, Key: {} to value: {}", new Object[] { articleID, keyColumn, citationDOI });
        }

        return null;
      }
    });
  }

  private Document getArticle(String doi) throws FileStoreException {
    String fsid = fileStoreService.objectIDMapper().doiTofsid(doi, "XML");
    Document doc;

    InputStream is = fileStoreService.getFileInStream(fsid);

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(false);

      DocumentBuilder builder = factory.newDocumentBuilder();
      EntityResolver resolver = CachedSource.getResolver(XMLServiceImpl.NLM_DTD_URL);
      builder.setEntityResolver(resolver);

      doc = builder.parse(is);
    } catch (Exception e) {
      log.error("Error parsing the article xml for article " + doi, e);
      return null;
    }

    return doc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void refreshCitedArticles(String articleDOI) throws Exception {
    log.info("refreshArticleCitation for article DOI: {}", articleDOI);

    Document article = getArticle(articleDOI);
    CrossRefSearch crossRefSearches[] = getCrossRefSearchTerms(article);

    for(int a = 0; a < crossRefSearches.length; a++) {
      CrossRefSearch crossRefSearch = crossRefSearches[a];
      String searchTerms = crossRefSearch.buildQuery();

      if(searchTerms.length() == 0) {
        log.info("No data for citation, not searching for DOI");
      } else {
        String crossrefDoi = findDoi(searchTerms);

        if (crossrefDoi != null && !crossrefDoi.isEmpty()) {
          //A fix for FEND-1077. crossref seems to append a URL to the DOI
          crossrefDoi = crossrefDoi.replace("http://dx.doi.org/","");

          String label = crossRefSearch.getLabel();
          long keyColumn;

          if(label != null) {
            keyColumn = Long.valueOf(label);
          } else {
            //Not able to determine value for key column, take a guess here
            //Based on the order of the element found in the XML
            //Some articles do not contain well structured XML
            keyColumn = crossRefSearch.getOriginalOrder() + 1;
          }

          log.info("refreshArticleCitation doi found: {}", crossrefDoi);
          setCitationDoi(articleDOI, keyColumn, crossrefDoi);
        } else {
          log.info("refreshArticleCitation nothing found");
        }
      }
    }
  }

  /**
   * Generate a list of CrossRefSearch pojos from the article DOM to be used for looking up DOIs for cited articles
   *
   * @param article the article DOM
   *
   * @return a list of pojos parsed out of the article DOM
   *
   * @throws Exception
   */
  protected CrossRefSearch[] getCrossRefSearchTerms(Document article) throws Exception {
    if(article == null) {
      throw new Exception("Article can not be null");
    } else {
      XPathUtil xPathUtil = new XPathUtil();
      NodeList nodes = xPathUtil.selectNodes(article, ".//back/ref-list/ref");
      List<CrossRefSearch> terms = new ArrayList<CrossRefSearch>(nodes.getLength());

      for(int a = 0; a < nodes.getLength(); a++) {
        Node node = nodes.item(a);

        Node pubtypeNode = xPathUtil.selectNode(node, ".//*[@publication-type='journal']");

        if(pubtypeNode != null) {
          //Keep track of the order the elements are found in the XML (the 'a' value)
          terms.add(new CrossRefSearch(node, a));
        }
      }

      return terms.toArray(new CrossRefSearch[terms.size()]);
    }
  }


  @Override
  @Transactional(readOnly = true)
  public String findDoi(String searchString) throws Exception {
    CrossRefResponse response = queryCrossRef(searchString);

    if(response != null && response.results.length > 0) {
      return response.results[0].doi;
    } else {
      return null;
    }
  }

  private CrossRefResponse queryCrossRef(String searchString)
  {
    PostMethod post = createCrossRefPost(searchString);

    try {
      long timestamp = System.currentTimeMillis();
      int response = httpClient.executeMethod(post);

      log.debug("Http post finished in {} ms", System.currentTimeMillis() - timestamp);

      if (response == 200) {
        String result = post.getResponseBodyAsString();
        if(result != null) {
          log.trace("JSON response received: {}", result);
          return parseJSON(result);
        }
        log.error("Received empty response, response code {}, when executing query  {}", response, crossRefUrl);
      } else {
        log.error("Received response code {} when executing query {}", response, crossRefUrl);
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    } finally {
      // be sure the connection is released back to the connection manager
      post.releaseConnection();
    }
    return null;
  }

  /**
   * Parse the JSON into native types
   *
   * @param json the JSON string to convert to a java native type
   *
   * @return a CrossRefResponse object
   */
  private CrossRefResponse parseJSON(final String json) {
    return new CrossRefResponse() {{
      JsonParser parser = new JsonParser();
      JsonObject responseObject = parser.parse(json).getAsJsonObject();

      queryOK = (responseObject.getAsJsonPrimitive("query_ok")).getAsBoolean();

      List<CrossRefResult> resultTemp = new ArrayList<CrossRefResult>();

      for(final JsonElement resultElement : responseObject.getAsJsonArray("results")) {
        JsonObject resultObj = resultElement.getAsJsonObject();
        CrossRefResult res = new CrossRefResult();

        if(resultObj.getAsJsonPrimitive("text") != null) {
          res.text = resultObj.getAsJsonPrimitive("text").getAsString();
        }

        if(resultObj.getAsJsonPrimitive("match") != null) {
          res.match = resultObj.getAsJsonPrimitive("match").getAsBoolean();
        }

        if(resultObj.getAsJsonPrimitive("doi") != null) {
          res.doi = resultObj.getAsJsonPrimitive("doi").getAsString();
        }

        if(resultObj.getAsJsonPrimitive("score") != null) {
          res.score = resultObj.getAsJsonPrimitive("score").getAsString();
        }

        //Some results aren't actually valid
        if(res.doi != null) {
          resultTemp.add(res);
        }
      }

      this.results = resultTemp.toArray(new CrossRefResult[resultTemp.size()]);
    }};
  }

  private PostMethod createCrossRefPost(String searchString)
  {
    StringBuilder builder = new StringBuilder();

    //Example query to post:
    //["Young GC,Analytical methods in palaeobiogeography, and the role of early vertebrate studies;Palaeoworld;19;160-173"]

    //Use toJSON to encode strings with proper escaping
    final String json = "[" + (new Gson()).toJson(searchString) + "]";

    if(this.crossRefUrl == null) {
      throw new RuntimeException("ambra.services.crossref.query.url value not found in configuration.");
    }

    return new PostMethod(this.crossRefUrl) {{
      addRequestHeader("Content-Type","application/json");
      setRequestEntity(new RequestEntity() {
        @Override
        public boolean isRepeatable() {
          return false;
        }

        @Override
        public void writeRequest(OutputStream outputStream) throws IOException {
          outputStream.write(json.getBytes());
        }

        @Override
        public long getContentLength() {
          return json.getBytes().length;
        }

        @Override
        public String getContentType() {
          return "application/json";
        }
      });
    }};
  }

  /* utility class for internally tracking data */
  private class CrossRefResult {
    public String text;
    public Boolean match;
    public String doi;
    public String score;
  }

  /* utility class for internally tracking data */
  private class CrossRefResponse {
    public CrossRefResult[] results;
    public Boolean queryOK;
  }

  @Required
  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Required
  public void setCrossRefUrl(String crossRefUrl) {
    this.crossRefUrl = crossRefUrl;
  }

  @Required
  public void setFileStoreService(FileStoreService fileStoreService) {
    this.fileStoreService = fileStoreService;
  }
}


