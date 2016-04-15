
/*
 * Copyright (c) 2006-2014 by Public Library of Science
 *
 *    http://plos.org
 *    http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.amendment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ambraproject.views.ArticleAmendment;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AmendmentServiceImpl implements AmendmentService {

  private static final Logger LOG = LoggerFactory.getLogger(AmendmentServiceImpl.class);

  private String rhinoServer;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ArticleAmendment> fetchAmendmentsFromRhino(String articleDoi) {
    String json = "";

    if ("info:doi/".equals(articleDoi.substring(0, 9))) {
      articleDoi = articleDoi.substring(9);
    }

    try {
      URL url = new URL(rhinoServer + "/articles/" + articleDoi);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.connect();
      InputStream in = conn.getInputStream();
      try {
        json = IOUtils.toString(in).trim();
      } finally {
        in.close();
      }
    } catch (IOException e) {
      LOG.error("Failed to fetch amendments from rhino", e);
    }
    return parseJsonFromRhino(json);
  }

  public List<ArticleAmendment> parseJsonFromRhino(String json) {
    List<ArticleAmendment>  amendments = new ArrayList<ArticleAmendment>();

    JsonParser parser = new JsonParser();
    JsonObject article = parser.parse(json).getAsJsonObject();
    JsonArray relatedAttr = article.getAsJsonArray("relatedArticles");
    for (JsonElement element : relatedAttr) {
      JsonObject relatedArticle = element.getAsJsonObject();
      amendments.add(ArticleAmendment
          .builder()
          .setParentArticleURI(article.get("doi").getAsString())
          .setOtherArticleDoi(relatedArticle.get("doi").getAsString())
          .setRelationshipType(relatedArticle.get("type").getAsString())
          .build());
    }
    return amendments;
  }

  public void setRhinoServer(String rhinoServer) {
    this.rhinoServer = rhinoServer;
  }
}
