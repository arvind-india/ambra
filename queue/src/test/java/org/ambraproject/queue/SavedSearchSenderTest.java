/*
 * Copyright (c) 2006-2013 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.queue;

import org.ambraproject.ApplicationException;
import org.ambraproject.action.BaseTest;
import org.ambraproject.models.SavedSearch;
import org.ambraproject.models.SavedSearchQuery;
import org.ambraproject.models.SavedSearchType;
import org.ambraproject.models.UserProfile;
import org.ambraproject.search.SavedSearchJob;
import org.ambraproject.search.SavedSearchRetriever;
import org.ambraproject.search.SavedSearchRunner;
import org.ambraproject.search.SavedSearchSender;
import org.ambraproject.testutils.EmbeddedSolrServerFactory;
import org.ambraproject.util.TextUtils;
import org.junit.AfterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for testing the mark sent functionality
 *
 * @author Joe Osowski
 */
@ContextConfiguration
public class SavedSearchSenderTest extends BaseTest {
  private static final Logger log = LoggerFactory.getLogger(SavedSearchSenderTest.class);

  private static final String DOI_1         = "10.1371/journal.pone.1000596";
  private static final String DOI_2         = "10.1371/journal.pone.1000500";
  private static final String DOI_3         = "10.1371/journal.pone.1000501";
  private static final String DOI_4         = "10.1371/journal.pone.1000301";
  private static final String JOURNAL_KEY_1 = "PLoSONE";
  private static final String CATEGORY_1    = "Category1";
  private static final String CATEGORY_2    = "Category2";

  @Autowired
  protected EmbeddedSolrServerFactory solrServerFactory;

  @Autowired
  protected SavedSearchSender savedSearchSender;

  @Autowired
  protected SavedSearchRunner savedSearchRunner;

  @Autowired
  protected SavedSearchRetriever savedSearchRetriever;

  /**
   * Seed the solr server with some articles to test
   *
   * For this test, it's not so important that SOLR has data as this just
   * tests to see if the search was executed, not that anything was found.  Regardless
   * It doesn't hurt to have the data here
   *
   * @throws Exception - if there's a problem
   */
  @BeforeMethod
  public void setupTestData() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //5 days into the past
    Calendar topdayMinus5 = Calendar.getInstance();
    topdayMinus5.add(Calendar.DAY_OF_MONTH, -5);

    //20 days into the past
    Calendar topdayMinus20 = Calendar.getInstance();
    topdayMinus20.add(Calendar.DAY_OF_MONTH, -20);

    //30 days into the past
    Calendar topdayMinus35 = Calendar.getInstance();
    topdayMinus35.add(Calendar.DAY_OF_MONTH, -35);

    // 2 occurrences in "everything": "Spleen"
    Map<String, String[]> document1 = new HashMap<String, String[]>();
    document1.put("id", new String[]{DOI_1});
    document1.put("title", new String[]{"The First Title, with Spleen testing"});
    document1.put("title_display", new String[]{"The First Title, with Spleen testing"});
    document1.put("author", new String[]{"alpha delta epsilon"});
    document1.put("body", new String[]{"Body of the first document: Yak and Spleen test"});
    document1.put("everything", new String[]{
      "body first document yak spleen first title with spleen test"});
    document1.put("elocation_id", new String[]{"111"});
    document1.put("volume", new String[]{"1"});
    document1.put("doc_type", new String[]{"full"});
    document1.put("publication_date", new String[]{sdf.format(topdayMinus5.getTime()) + "T00:00:00Z"});
    document1.put("cross_published_journal_key", new String[]{JOURNAL_KEY_1});
    document1.put("subject", new String[]{CATEGORY_1, CATEGORY_2});
    document1.put("article_type_facet", new String[]{"Not an issue image"});
    document1.put("author", new String[]{"doc1 author"});
    document1.put("author_display", new String[]{"doc1 creator"});

    // 2 occurrences in "everything": "Yak"
    Map<String, String[]> document2 = new HashMap<String, String[]>();
    document2.put("id", new String[]{DOI_2});
    document2.put("title", new String[]{"The Second Title, with Yak YEk"});
    document2.put("title_display", new String[]{"The Second Title, with Yak YAK"});
    document2.put("author", new String[]{"beta delta epsilon"});
    document2.put("body", new String[]{"Description of the second document: Yak and Islets of Langerhans testing"});
    document2.put("everything", new String[]{
      "description second document yak islets Langerhans second title with yak testing"});
    document2.put("elocation_id", new String[]{"222"});
    document2.put("volume", new String[]{"2"});
    document2.put("doc_type", new String[]{"full"});
    document2.put("publication_date", new String[]{sdf.format(topdayMinus5.getTime()) + "T00:00:00Z"});
    document2.put("cross_published_journal_key", new String[]{JOURNAL_KEY_1});
    document2.put("subject", new String[]{CATEGORY_2});
    document2.put("article_type_facet", new String[]{"Not an issue image"});
    document2.put("author", new String[]{"doc2 author"});
    document2.put("author_display", new String[]{"doc2 creator"});

    // 2 occurrences in "everything": "Gecko"
    Map<String, String[]> document3 = new HashMap<String, String[]>();
    document3.put("id", new String[]{DOI_3});
    document3.put("title", new String[]{"The Third Title, with Gecko  "});
    document3.put("title_display", new String[]{"The Second Title, with Yak Gecko"});
    document3.put("author", new String[]{"gamma delta"});
    document3.put("body", new String[]{"Contents of the second document: Gecko and Islets of Langerhans"});
    document3.put("everything", new String[]{
      "contents of the second document gecko islets langerhans third title with gecko testjournal"});
    document3.put("elocation_id", new String[]{"333"});
    document3.put("volume", new String[]{"3"});
    document3.put("doc_type", new String[]{"full"});
    document3.put("publication_date", new String[]{sdf.format(topdayMinus5.getTime()) + "T00:00:00Z"});
    document3.put("cross_published_journal_key", new String[]{JOURNAL_KEY_1});
    document3.put("subject", new String[]{CATEGORY_1});
    document3.put("article_type_facet", new String[]{"Not an issue image"});
    document3.put("author", new String[]{"doc3 author"});
    document3.put("author_display", new String[]{"doc3 creator"});

    Map<String, String[]> document4 = new HashMap<String, String[]>();
    document4.put("id", new String[]{DOI_4});
    document4.put("title", new String[]{"The Fourth Title, with Gecko  "});
    document4.put("title_display", new String[]{"The Fourth Title, with Yak Gecko"});
    document4.put("author", new String[]{"gamma delta"});
    document4.put("body", new String[]{"Contents of the Fourth document: Gecko and Islets of Langerhans"});
    document4.put("everything", new String[]{
      "contents of the second document gecko islets langerhans third title with gecko testing"});
    document4.put("elocation_id", new String[]{"333"});
    document4.put("volume", new String[]{"3"});
    document4.put("doc_type", new String[]{"full"});
    document4.put("publication_date", new String[]{sdf.format(topdayMinus20.getTime()) + "T00:00:00Z"});
    document4.put("cross_published_journal_key", new String[]{JOURNAL_KEY_1});
    document4.put("subject", new String[]{CATEGORY_1});
    document4.put("article_type_facet", new String[]{"Not an issue image"});
    document4.put("author", new String[]{"doc3 author"});
    document4.put("author_display", new String[]{"doc3 creator"});

    solrServerFactory.addDocument(document1);
    solrServerFactory.addDocument(document2);
    solrServerFactory.addDocument(document3);
    solrServerFactory.addDocument(document4);

    //Let's make sure the database is clean
    this.dummyDataStore.deleteAll(SavedSearch.class);
    this.dummyDataStore.deleteAll(SavedSearchQuery.class);
    this.dummyDataStore.deleteAll(UserProfile.class);

  }

  @Test
  void testSearchSender() throws ApplicationException
  {
    //I don't test here to check for sent emails.  The SavedSearchRouteMonthlyTest does that
    //Check here that the database was updated to reflect the messages were sent
    Date runTime = Calendar.getInstance().getTime();

    //Let a little bit of time pass so time based assertions will check to be true
    try {
      Thread.sleep(2000);
    } catch(InterruptedException ex) {}

    //Execute weekly jobs:
    List<SavedSearchJob> savedSearchJobs = savedSearchRetriever.
      retrieveSearchAlerts(SavedSearchRetriever.AlertType.WEEKLY, null, null);

    //There should be three jobs reflecting three distinct weekly searches
    assertEquals(savedSearchJobs.size(), 2, "Wrong number of saved search jobs returned");

    for(SavedSearchJob savedSearchJob : savedSearchJobs) {
      savedSearchJob = savedSearchRunner.runSavedSearch(savedSearchJob);

      assertNotNull(savedSearchJob.getStartDate(), "startDate failed to populate hitlist");
      assertNotNull(savedSearchJob.getEndDate(), "endDate failed to populate hitlist");
      assertNotNull(savedSearchJob.getSearchHitList(), "savedSearchRunner failed to populate hitlist");

      savedSearchSender.sendSavedSearch(savedSearchJob);
    }

    //Execute monthly jobs:
    savedSearchJobs = savedSearchRetriever.retrieveSearchAlerts(SavedSearchRetriever.AlertType.MONTHLY, null, null);

    assertEquals(savedSearchJobs.size(), 2);

    for(SavedSearchJob savedSearchJob : savedSearchJobs) {
      savedSearchJob = savedSearchRunner.runSavedSearch(savedSearchJob);

      assertNotNull(savedSearchJob.getStartDate(), "startDate failed to populate hitlist");
      assertNotNull(savedSearchJob.getEndDate(), "endDate failed to populate hitlist");
      assertNotNull(savedSearchJob.getSearchHitList(), "savedSearchRunner failed to populate hitlist");

      savedSearchSender.sendSavedSearch(savedSearchJob);
    }

  }

}
