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

import org.ambraproject.action.BaseTest;
import org.ambraproject.search.SavedSearchJob;
import org.ambraproject.search.SavedSearchRetriever;
import org.ambraproject.service.ned.NedServiceMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.plos.ned_client.model.Alert;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Unit test for the SavedSearchRetriever class
 *
 * @author Kyle Pang
 */
@ContextConfiguration(locations = "SavedSearchRetrieverTest-context.xml")
public class SavedSearchRetrieverTest extends BaseTest {

  @Autowired
  private NedServiceMock nedService;

  @Autowired
  protected SavedSearchRetriever savedSearchRetriever;

  @Test
  public void testRetrieveSearchAlerts() {

    // Weekly search alerts
    List<SavedSearchJob> savedSearchJobs = savedSearchRetriever.retrieveSearchAlerts(SavedSearchRetriever.AlertType
            .WEEKLY, null, null);
    assertNotNull(savedSearchJobs, "Saved search views List is empty for weekly search");
    assertEquals(savedSearchJobs.size(), 2, "Number of weekly search alerts is off");

    // Monthly search alerts
    savedSearchJobs = savedSearchRetriever.retrieveSearchAlerts(SavedSearchRetriever.AlertType.MONTHLY,
        null, null);

    assertNotNull(savedSearchJobs, "Saved search views List is empty for monthly search");
    assertEquals(savedSearchJobs.size(), 2, "Number of monthly search alerts is off");

    Date then = new Date(1000);
    Date now = new Date();
    savedSearchJobs = savedSearchRetriever.retrieveSearchAlerts(SavedSearchRetriever.AlertType.MONTHLY,
        then, now);

    assertNotNull(savedSearchJobs, "Saved search views List is empty for monthly search");
    assertEquals(savedSearchJobs.size(), 2, "returned incorrect number of results");

    assertEquals(savedSearchJobs.get(0).getStartDate(), then, "Start date specified, but not correct.");
    assertEquals(savedSearchJobs.get(0).getEndDate(), now, "End date specified, but not correct.");
  }
}
