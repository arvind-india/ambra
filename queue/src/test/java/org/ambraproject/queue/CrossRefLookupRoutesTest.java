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
package org.ambraproject.queue;

import org.ambraproject.action.BaseTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.CitedArticle;
import org.ambraproject.models.CitedArticleAuthor;
import org.ambraproject.routes.CrossRefLookupRoutes;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for the cross ref lookup route
 *
 * @author Joe Osowski
 */
@ContextConfiguration
public class CrossRefLookupRoutesTest extends BaseTest {
  @Produce(uri = CrossRefLookupRoutes.UPDATE_CITED_ARTICLES_QUEUE)
  protected ProducerTemplate start;

  @DataProvider(name="testData")
  public Object[][] testData() {
    Article article1 = new Article();
    article1.setDoi("info:doi/10.1371/journal.pone.0047851");
    article1.setTitle("Fake Title for Article 1");
    article1.setState(Article.STATE_ACTIVE);
    article1.setAssets(new ArrayList<ArticleAsset>() {{
      add(new ArticleAsset() {{
        setContentType("text/xml");
        setExtension("XML");
        setDoi("id:doi-for-getXmlAndPdf");
        setDoi("info:doi/10.1371/journal.pone.0047851");
      }});
    }});

    article1.setCitedArticles(new ArrayList<CitedArticle>() {{
      new CitedArticle() {{
        setAuthors(new LinkedList<CitedArticleAuthor>() {{
            add(new CitedArticleAuthor() {{
              setFullName("fullName");
              setGivenNames("Dona");
              setSuffix("suffix");
              setSurnames("surnames");
            }});
          }
        });

        setCitationType("citationType-journal");
        setDisplayYear("displayYear-2009");
        setIssue("issue-1");
        setKey("2");
        setJournal("journal-2");
        setMonth("month-3");
        setPages("pages-203");
        setTitle("Health risks of genetically modified foods");
        setVolume("volume-4");
        setVolumeNumber(4);
        setYear(2013);
      }};
    }});

    article1.setDate(new Date());

    article1.setTypes(new HashSet<String>() {{
      add("http://rdf.plos.org/RDF/articleType/research-article");
    }});

    dummyDataStore.store(article1);

    return new Object[][] { new Article[] { article1 }};
  }

  @Test(dataProvider = "testData")
  @SuppressWarnings("unchecked")
  public void testRoute(final Article article1) throws Exception {
    Long time1 = System.currentTimeMillis();

    start.sendBodyAndHeaders(article1.getDoi(), new HashMap() {{
      put(CrossRefLookupRoutes.HEADER_AUTH_ID, BaseTest.DEFAULT_USER_AUTHID);
    }});

    Long time2 = System.currentTimeMillis();

    assertTrue(time2 - time1 < 2500, "Queuing the jobs took too long, are you sure they are asynchronous?");

    //Let the queue do it's job before checking results
    Thread.sleep(15000);

    Article result = dummyDataStore.get(Article.class, article1.getID());

    //The dummy service returns the same data for all cited articles.
    //Let's just check here that they all match this dummy data
    for(CitedArticle citedArticle : result.getCitedArticles()) {
      assertEquals(citedArticle.getDoi(), "10.1038/176126a0");
    }
  }
}
