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
package org.ambraproject.action;

import org.ambraproject.models.Annotation;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.ArticleEditor;
import org.ambraproject.models.ArticleRelationship;
import org.ambraproject.models.Category;
import org.ambraproject.models.CitedArticle;
import org.ambraproject.models.CitedArticleAuthor;
import org.ambraproject.models.CitedArticleEditor;
import org.ambraproject.models.Journal;
import org.ambraproject.models.UserProfileRoleJoinTable;
import org.ambraproject.models.UserRole;
import org.ambraproject.testutils.DummyDataStore;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.ArticleCategory;
import org.ambraproject.views.article.ArticleInfo;
import org.ambraproject.views.article.RelatedArticleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.testng.Assert.*;
import static org.testng.Assert.assertNotNull;

/**
 * Base class for tests of Ambra Service Beans.  This is provided just so they can all use the same applicationContext
 * xml file; Bean tests should just test methods of the interface and have an instance autowired (see {@link
 * org.ambraproject.service.annotation.AnnotationServiceTest} for an example.
 *
 * @author Alex Kudlick Date: 4/29/11
 *         <p/>
 *         org.ambraproject
 */
@ContextConfiguration(locations = "nonWebApplicationContext.xml")
@Test(singleThreaded = true)
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

  protected static final String IMAGE_DOI_IN_FILESTORE = "info:doi/10.1371/journal.pntd.0001646.g001";
  /**
   * Instance provided so that tests can store dummy data in the same test database that the autowired beans are using.
   * Tests should use this to seed the database with data to test.
   */
  @Autowired
  protected DummyDataStore dummyDataStore;

  public static final Journal defaultJournal = new Journal();

  public static final String DEFAULT_ADMIN_AUTHID = "AdminAuthorizationID";
  public static final String DEFAULT_EDITORIAL_AUTHID = "EditorialAuthorizationID";
  public static final String DEFAULT_USER_AUTHID = "DummyTestUserAuthorizationID";
  public static final Long USER_PROFILE_ID_ADMIN = 1L;
  public static final Long USER_PROFILE_ID_EDITORIAL = 2L;
  public static final Long USER_PROFILE_ID_NONADMIN = 1001L;

  static {
    defaultJournal.setJournalKey("journal");
    defaultJournal.seteIssn("1234");
  }

  /**
   * Helper method to compare dates.  This compares down to the minute, and checks that the seconds are within 1, since
   * rounding can occur when storing to an hsql db
   *
   * @param actual   - the date from mysql to compare
   * @param expected - the date from Ambra to compare
   */
  protected static void assertMatchingDates(Date actual, Date expected) {
    if (actual == null || expected == null) {
      assertTrue(actual == null && expected == null, "one date was null and the other wasn't");
    } else {
      Calendar actualCal = new GregorianCalendar();
      actualCal.setTime(actual);
      Calendar expectedCal = new GregorianCalendar();
      expectedCal.setTime(expected);
      assertEquals(actualCal.get(Calendar.YEAR), expectedCal.get(Calendar.YEAR), "Dates didn't have matching years");
      assertEquals(actualCal.get(Calendar.MONTH), expectedCal.get(Calendar.MONTH), "dates didn't have matching months");
      assertEquals(actualCal.get(Calendar.DAY_OF_MONTH), expectedCal.get(Calendar.DAY_OF_MONTH), "dates didn't have matching days of month");
      assertEquals(actualCal.get(Calendar.DAY_OF_WEEK), expectedCal.get(Calendar.DAY_OF_WEEK), "dates didn't have matching days of week");
      assertEquals(actualCal.get(Calendar.HOUR), expectedCal.get(Calendar.HOUR), "dates didn't have matching hours");
      assertEquals(actualCal.get(Calendar.MINUTE), expectedCal.get(Calendar.MINUTE), "dates didn't have matching minutes");
      int secondMin = expectedCal.get(Calendar.SECOND) - 1;
      int secondMax = expectedCal.get(Calendar.SECOND) + 1;
      int actualSecond = actualCal.get(Calendar.SECOND);
      assertTrue(secondMin <= actualSecond && actualSecond <= secondMax,
          "date didn't have correct second; expected something in [" + secondMin + "," + secondMax +
              "]; but got " + actualSecond);
    }
  }

  /**
   * Helper method to compare article properties
   *
   * @param actual   - actual article
   * @param expected - article with expected properties
   */
  protected void compareArticles(ArticleInfo actual, Article expected) {
    assertNotNull(actual, "returned null article");
    assertEquals(actual.getDoi(), expected.getDoi(), "Article had incorrect doi");
    assertEquals(actual.geteIssn(), expected.geteIssn(), "returned incorrect eIssn");
    assertEquals(actual.getTitle(), expected.getTitle(), "Article had incorrect Title");
    assertEquals(actual.getDescription(), expected.getDescription(), "Article had incorrect description");
    assertEqualsNoOrder(actual.getCategories().toArray(), expected.getCategories().keySet().toArray(), "Incorrect categories");
  }

  /**
   * Helper method to compare article properties
   *
   * @param actual   - actual article
   * @param expected - article with expected properties
   */
  protected void compareArticles(Article actual, Article expected) {
    assertNotNull(actual, "returned null article");
    assertEquals(actual.getDoi(), expected.getDoi(), "Article had incorrect doi");

    assertEquals(actual.geteIssn(), expected.geteIssn(), "returned incorrect eIssn");
    assertEquals(actual.getUrl(), expected.getUrl(), "returned incorrect url");


    assertEquals(actual.getRights(), expected.getRights(),
        "Article had incorrect rights");
    assertEquals(actual.getLanguage(), expected.getLanguage(),
        "Article had incorrect language");
    assertEquals(actual.getPublisherLocation(), expected.getPublisherLocation(),
        "Article had incorrect publisher");
    assertEquals(actual.getFormat(), expected.getFormat(),
        "Article had incorrect format");
    assertEquals(actual.getTitle(), expected.getTitle(),
        "Article had incorrect Title");
    assertEquals(actual.getDescription(), expected.getDescription(),
        "Article had incorrect description");
    assertEquals(actual.getArchiveName(), expected.getArchiveName(), "Article had incorrect archive name");

    assertEqualsNoOrder(actual.getCategories().keySet().toArray(),
      expected.getCategories().keySet().toArray(), "Incorrect categories");
    assertEqualsNoOrder(actual.getCategories().values().toArray(),
      expected.getCategories().values().toArray(), "Incorrect category weights");

    if (expected.getAssets() != null) {
      assertEquals(actual.getAssets().size(), expected.getAssets().size(), "incorrect number of assets");
      for (int i = 0; i < actual.getAssets().size(); i++) {
        compareAssets(actual.getAssets().get(i), expected.getAssets().get(i));
      }
    }
    if (expected.getCitedArticles() != null) {
      assertEquals(actual.getCitedArticles().size(), expected.getCitedArticles().size(), "Returned incorrect number of references");
      for (int i = 0; i < actual.getCitedArticles().size(); i++) {
        compareCitedArticles(actual.getCitedArticles().get(i), expected.getCitedArticles().get(i));
      }
    } else {
      assertTrue(actual.getCitedArticles() == null || actual.getCitedArticles().size() == 0,
          "Returned non-empty references when none were expected");
    }


    if (expected.getAuthors() != null) {
      assertNotNull(actual.getAuthors(), "returned null author list");
      assertEquals(actual.getAuthors().size(), expected.getAuthors().size(),
          "returned incorrect number of authors");
      for (int i = 0; i < expected.getAuthors().size(); i++) {
        ArticleAuthor actualAuthor = actual.getAuthors().get(i);
        ArticleAuthor expectedAuthor = expected.getAuthors().get(i);
        assertEquals(actualAuthor.getFullName(), expectedAuthor.getFullName(), "Article Author had incorrect Real Name");
        assertEquals(actualAuthor.getGivenNames(), expectedAuthor.getGivenNames(), "Article Author had incorrect given name");
        assertEquals(actualAuthor.getSurnames(), expectedAuthor.getSurnames(), "Article Author had incorrect surname");
      }
    }
    if (expected.getEditors() != null) {
      assertNotNull(actual.getEditors(), "returned null editor list");
      assertEquals(actual.getEditors().size(), expected.getEditors().size(),
          "returned incorrect number of editors");
      for (int i = 0; i < expected.getEditors().size(); i++) {
        ArticleEditor actuaEditor = actual.getEditors().get(i);
        ArticleEditor expectedEditor = expected.getEditors().get(i);
        assertEquals(actuaEditor.getFullName(), expectedEditor.getFullName(), "Article Editor had incorrect Real Name");
        assertEquals(actuaEditor.getGivenNames(), expectedEditor.getGivenNames(), "Article Editor had incorrect given name");
        assertEquals(actuaEditor.getSurnames(), expectedEditor.getSurnames(), "Article Editor had incorrect surname");
      }
    }
    if (expected.getRelatedArticles() != null) {
      assertNotNull(actual.getRelatedArticles(), "null list of related articles");
      assertEquals(actual.getRelatedArticles().size(), expected.getRelatedArticles().size(), "Incorrect number of related articles");
      for (int i = 0; i < actual.getRelatedArticles().size(); i++) {
        ArticleRelationship actualRelatedArticle = actual.getRelatedArticles().get(i);
        ArticleRelationship expectedRelatedArticle = expected.getRelatedArticles().get(i);
        assertEquals(actualRelatedArticle.getOtherArticleDoi(), expectedRelatedArticle.getOtherArticleDoi(),
            "related article " + i + " had incorrect otherArticleDoi");
        assertEquals(actualRelatedArticle.getType(), expectedRelatedArticle.getType(),
            "related article " + i + " had incorrect type");
        assertTrue(actualRelatedArticle.getParentArticle() == actual, "related article had incorrect parent article");
      }
    }
  }

  protected void compareCitedArticles(CitedArticle actual, CitedArticle expected) {
    assertNotNull(actual, "Returned null citation");
    assertEquals(actual.getKey(), expected.getKey(), "Returned incorrect citation Key");

    assertEquals(actual.getYear(), expected.getYear(), "Returned incorrect citation Year; key: " + expected.getKey());
    assertEquals(actual.getDisplayYear(), expected.getDisplayYear(), "Returned incorrect citation Display Year; key: " + expected.getKey());
    assertEquals(actual.getMonth(), expected.getMonth(), "Returned incorrect citation Month; key: " + expected.getKey());
    assertEquals(actual.getDay(), expected.getDay(), "Returned incorrect citation Day; key: " + expected.getKey());

    assertEquals(actual.getVolumeNumber(), expected.getVolumeNumber(), "Returned incorrect citation Volume Number; key: " + expected.getKey());
    assertEquals(actual.getVolume(), expected.getVolume(), "Returned incorrect citation Volume; key: " + expected.getKey());
    assertEquals(actual.getIssue(), expected.getIssue(), "Returned incorrect citation Issue; key: " + expected.getKey());
    assertEquals(actual.getTitle(), expected.getTitle(), "Returned incorrect citation Title; key: " + expected.getKey());
    assertEquals(actual.getPublisherLocation(), expected.getPublisherLocation(), "Returned incorrect citation Publisher Location; key: " + expected.getKey());
    assertEquals(actual.getPublisherName(), expected.getPublisherName(), "Returned incorrect citation Publisher name; key: " + expected.getKey());

    assertEquals(actual.getPages(), expected.getPages(), "Returned incorrect citation Page; key: " + expected.getKey());
    assertEquals(actual.geteLocationID(), expected.geteLocationID(), "Returned incorrect citation eLocationId; key: " + expected.getKey());

    assertEquals(actual.getJournal(), expected.getJournal(), "Returned incorrect citation Journal; key: " + expected.getKey());
    assertEquals(actual.getNote(), expected.getNote(), "Returned incorrect citation Note; key: " + expected.getKey());

    if (expected.getAuthors() != null) {
      assertNotNull(actual.getAuthors(), "Citation had null editors list when non-null was expected");
      assertEquals(actual.getAuthors().size(), expected.getAuthors().size(), "returned incorrect number of editors");
      for (int i = 0; i < actual.getAuthors().size(); i++) {
        CitedArticleAuthor actualAuthor = actual.getAuthors().get(i);
        CitedArticleAuthor expectedUserProfile = expected.getAuthors().get(i);
        assertEquals(actualAuthor.getFullName(), expectedUserProfile.getFullName(), "Editor had incorrect Real Name");
        assertEquals(actualAuthor.getGivenNames(), expectedUserProfile.getGivenNames(), "Editor had incorrect given name");
        assertEquals(actualAuthor.getSurnames(), expectedUserProfile.getSurnames(), "Editor had incorrect surname");
      }
    }
    if (expected.getEditors() != null) {
      assertNotNull(actual.getEditors(), "Citation had null editors list when non-null was expected");
      assertEquals(actual.getEditors().size(), expected.getEditors().size(), "returned incorrect number of editors");
      for (int i = 0; i < actual.getEditors().size(); i++) {
        CitedArticleEditor actualEditor = actual.getEditors().get(i);
        CitedArticleEditor expectedEditor = expected.getEditors().get(i);
        assertEquals(actualEditor.getFullName(), expectedEditor.getFullName(), "Editor had incorrect Real Name");
        assertEquals(actualEditor.getGivenNames(), expectedEditor.getGivenNames(), "Editor had incorrect given name");
        assertEquals(actualEditor.getSurnames(), expectedEditor.getSurnames(), "Editor had incorrect surname");
      }
    }

    if (expected.getCollaborativeAuthors() != null) {
      assertEquals(actual.getCollaborativeAuthors().toArray(), expected.getCollaborativeAuthors().toArray(),
          "returned incorrect collaborative authors");
    }
    assertEquals(actual.getUrl(), expected.getUrl(), "Returned incorrect citation URL; key: " + expected.getKey());
    assertEquals(actual.getDoi(), expected.getDoi(), "Returned incorrect citation doi'; key: " + expected.getKey());
    assertEquals(actual.getSummary(), expected.getSummary(), "Returned incorrect citation Summary; key: " + expected.getKey());
    assertEquals(actual.getCitationType(), expected.getCitationType(), "Returned incorrect citation Citation Type; key: " + expected.getKey());

  }

  protected void compareAssets(ArticleAsset actual, ArticleAsset expected) {
    assertEquals(actual.getDoi(), expected.getDoi(),
        "asset had incorrect doi");
    assertEquals(actual.getContentType(), expected.getContentType(),
        "asset had incorrect content type");
    assertEquals(actual.getExtension(), expected.getExtension(),
        "asset had incorrect name");
    assertEquals(actual.getSize(), expected.getSize(),
        "asset had incorrect size");
    assertEquals(actual.getTitle(), expected.getTitle(),
        "asset had incorrect title");
    assertEquals(actual.getDescription(), expected.getDescription(),
        "asset had incorrect description");
  }

  protected void checkArticleInfo(ArticleInfo actual, Article expectedArticle,
                                  Article[] expectedRelatedArticles) {
    //basic properties
    assertEquals(actual.getDoi(), expectedArticle.getDoi(), "returned article info with incorrect id");
    assertEquals(actual.getTitle(), expectedArticle.getTitle(),
        "returned article info with incorrect title");
    assertEquals(actual.getDate(), expectedArticle.getDate(),
        "returned article info with incorrect date");
    assertEquals(actual.getDescription(), expectedArticle.getDescription(), "incorrect description");
    assertEquals(actual.getRights(), expectedArticle.getRights(), "incorrect rights");
    //check collaborative authors
    if (expectedArticle.getCollaborativeAuthors() != null) {
      assertNotNull(actual.getCollaborativeAuthors(), "returned null collaborative authors");
      assertEqualsNoOrder(actual.getCollaborativeAuthors().toArray(),
          expectedArticle.getCollaborativeAuthors().toArray(),
          "incorrect collaborative authors");
    }
    //check categories
    if (expectedArticle.getCategories() == null || expectedArticle.getCategories().size() == 0) {
      assertTrue(actual.getCategories() == null || actual.getCategories().size() == 0,
          "returned subjects when none were expected");
    } else {
      assertNotNull(actual.getCategories(), "returned null subjects");

      Set<String> collapsedCategories = new HashSet<String>(expectedArticle.getCategories().size());

      for (Category category : expectedArticle.getCategories().keySet()) {
        boolean foundCategory = false;

        collapsedCategories.add(category.getMainCategory());
        collapsedCategories.add(category.getSubCategory());

        for(ArticleCategory cat : actual.getCategories())
        {
          if(cat.getMainCategory().equals(category.getMainCategory())) {
            if(cat.getSubCategory().equals(category.getSubCategory())) {
              foundCategory = true;
            }
          }
        }

        assertTrue(foundCategory, "Didn't return category: " + category.getMainCategory());
      }
    }

    //check authors
    if (expectedArticle.getAuthors() != null) {
      assertNotNull(actual.getAuthors(), "returned null list of authors");
      assertEquals(actual.getAuthors().size(), expectedArticle.getAuthors().size(), "returned incorrect number of authors");
      for (ArticleAuthor author : expectedArticle.getAuthors()) {
        assertTrue(actual.getAuthors().contains(author.getFullName()), "didn't return author: " + author.getFullName());
      }
    }

    //check related articles
    if (expectedRelatedArticles == null || expectedRelatedArticles.length == 0) {
      assertTrue(actual.getRelatedArticles() == null || actual.getRelatedArticles().size() == 0,
          "returned related articles when none were expected");
    } else {
      assertNotNull(actual.getRelatedArticles(), "returned null list of related articles");
      assertEquals(actual.getRelatedArticles().size(), expectedRelatedArticles.length,
          "returned incorrect number of related articles");
      for (Article otherArticle : expectedRelatedArticles) {
        boolean foundMatch = false;

        for (RelatedArticleInfo actualRelatedArticle : actual.getRelatedArticles()) {

          assertNotNull(otherArticle.getTitle(), "Title value of other article is null");
          assertNotNull(otherArticle.getDoi(), "DOI value of other article is null");
          assertNotNull(otherArticle.getTypes(), "Types value of other article is null");

          assertNotNull(actualRelatedArticle.getTitle(), "Title value of actual article is null");
          assertNotNull(actualRelatedArticle.getUri(), "URI value of actual article is null");
          assertNotNull(actualRelatedArticle.getTypes(), "Types value of actual article is null");

          if (otherArticle.getTitle().equals(actualRelatedArticle.getTitle()) &&
              otherArticle.getDoi().equals(actualRelatedArticle.getUri().toString())&&
              otherArticle.getTypes().equals(actualRelatedArticle.getTypes())) {
            foundMatch = true;
            break;
          }
        }
        if (!foundMatch) {
          fail("Didn't include an entry for related article: " + otherArticle.getDoi());
        }
      }
    }
  }

  protected void checkAnnotationProperties(AnnotationView result, Annotation expected) {
    assertEquals(result.getBody(), "<p>" + expected.getBody() + "</p>", "Annotation view had incorrect body");
    assertEquals(result.getCompetingInterestStatement(),
        expected.getCompetingInterestBody() == null ? "" : expected.getCompetingInterestBody(),
        "Annotation view had incorrect ci statement");
    assertEquals(result.getAnnotationUri(), expected.getAnnotationUri(), "Annotation view had incorrect annotation uri");
    assertEquals(result.getCreatorID(), expected.getUserProfileID(), "Annotation view had incorrect creator id");
  }

  protected void setUpArticleForImageFromFilestore() {
    Article article = new Article(IMAGE_DOI_IN_FILESTORE.substring(0, IMAGE_DOI_IN_FILESTORE.lastIndexOf('.')));
    article.seteIssn(defaultJournal.geteIssn());
    article.setTitle("title");
    article.setJournal("journal");
    article.setDate(Calendar.getInstance().getTime());
    article.setAssets(Arrays.asList(
        new ArticleAsset(IMAGE_DOI_IN_FILESTORE, "PNG_S"),
        new ArticleAsset(IMAGE_DOI_IN_FILESTORE, "PNG_M"),
        new ArticleAsset(IMAGE_DOI_IN_FILESTORE, "PNG_L"),
        new ArticleAsset(IMAGE_DOI_IN_FILESTORE, "TIF")
    ));
    article.setAuthors(Arrays.asList(
        new ArticleAuthor("John","Smith","PhD"),
        new ArticleAuthor("Harry","Potter","Dr."),
        new ArticleAuthor("Emma","Swan","M.S.")
    ));
    dummyDataStore.store(article);
  }

  /**
   * Some unit tests delete all users.  This is a way to restore them.
   * This logic is very similar to logic in HibernateTestSessionFactory afterPropertiesSet function
   *
   * No longer needed.
   */
  protected void restoreDefaultUsers() {

    try {
      // Create an admin user to test admin functions
      UserRole adminRole = new UserRole("admin");

      Set<UserRole.Permission> perms = new HashSet<UserRole.Permission>();
      perms.add(UserRole.Permission.ACCESS_ADMIN);
      perms.add(UserRole.Permission.INGEST_ARTICLE);
      perms.add(UserRole.Permission.MANAGE_FLAGS);
      perms.add(UserRole.Permission.MANAGE_ANNOTATIONS);
      perms.add(UserRole.Permission.MANAGE_USERS);
      perms.add(UserRole.Permission.MANAGE_ROLES);
      perms.add(UserRole.Permission.MANAGE_JOURNALS);
      perms.add(UserRole.Permission.MANAGE_SEARCH);
      perms.add(UserRole.Permission.MANAGE_CACHES);
      perms.add(UserRole.Permission.CROSS_PUB_ARTICLES);
      perms.add(UserRole.Permission.DELETE_ARTICLES);
      perms.add(UserRole.Permission.VIEW_UNPUBBED_ARTICLES);

      adminRole.setPermissions(perms);
      dummyDataStore.store(adminRole);

      UserProfileRoleJoinTable ur1 = new UserProfileRoleJoinTable();
      ur1.setUserRoleID(adminRole.getID());
      ur1.setUserProfileID(BaseTest.USER_PROFILE_ID_ADMIN);
      dummyDataStore.store(ur1);

      UserRole editorialRole = new UserRole("editorial");
      perms = new HashSet<UserRole.Permission>();
      perms.add(UserRole.Permission.ACCESS_ADMIN);
      perms.add(UserRole.Permission.VIEW_UNPUBBED_ARTICLES);
      editorialRole.setPermissions(perms);
      dummyDataStore.store(editorialRole);

      UserProfileRoleJoinTable ur2 = new UserProfileRoleJoinTable();
      ur2.setUserRoleID(editorialRole.getID());
      ur2.setUserProfileID(BaseTest.USER_PROFILE_ID_EDITORIAL);
      dummyDataStore.store(ur2);

    } catch (DataAccessException ex) {
      //must've already inserted the users
    }

  }
}
