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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ArticleListTest extends BaseHibernateTest {
  private static final Logger log = LoggerFactory.getLogger(ArticleListTest.class);

  private final List<Article> stubArticles = makeStubArticles("doi1", "doi2", "doi3");

  @BeforeTest
  public void createStubArticles() {
    for (Article stubArticle : stubArticles) {
      hibernateTemplate.save(stubArticle);
    }
  }

  @AfterTest
  public void deleteStubArticles() {
    for (Article stubArticle : stubArticles) {
      hibernateTemplate.delete(stubArticle);
    }
  }

  @Test
  public void testSaveBasicCategory() {

    ArticleList articleList1 = new ArticleList("code:testArticleListToSave");
    articleList1.setDisplayName("News");
    articleList1.setArticles(stubArticles);

    Serializable id1 = hibernateTemplate.save(articleList1);

    ArticleList storedList1 = (ArticleList) hibernateTemplate.get(ArticleList.class, id1);
    assertNotNull(storedList1, "didn't save article list");
    assertEquals(storedList1, articleList1, "didn't store correct article list properties");
    assertNotNull(storedList1.getCreated(), "article list didn't get created date set");

  }

  @Test
  public void testUpdateCategory() throws Exception {
    long testStart = Calendar.getInstance().getTimeInMillis();

    ArticleList articleList = new ArticleList("listCode:testarticleListToUpdate");
    articleList.setDisplayName("Old News Article");
    List<Article> articles = makeStubArticles("old doi 1", "old doi 2", "old doi 3");

    articleList.setArticles(articles);

    Serializable id = hibernateTemplate.save(articleList);

    articleList.getArticles().remove(1);
    articleList.getArticles().addAll(makeStubArticles("new doi 4"));
    articleList.setDisplayName("New News Articles");

    //Artificial delay to make sure created time is in the past
    Thread.sleep(250);

    hibernateTemplate.update(articleList);

    ArticleList storedArticleList = (ArticleList) hibernateTemplate.get(ArticleList.class, id);
    assertEquals(storedArticleList, articleList, "didn't update news properties");
    assertNotNull(storedArticleList.getLastModified(), "news didn't get last modified date set");
    assertTrue(storedArticleList.getLastModified().getTime() > testStart, "last modified wasn't after test start");

    log.debug("Last Modified: {}", storedArticleList.getLastModified());
    log.debug("Created: {}", storedArticleList.getCreated());

    assertTrue(storedArticleList.getLastModified().getTime() > storedArticleList.getCreated().getTime(),
        "last modified wasn't after created");
  }
}

