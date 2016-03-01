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

package org.ambraproject.search;

import org.ambraproject.models.SavedSearch;
import org.ambraproject.models.SavedSearchType;
import org.ambraproject.service.ned.NedService;
import org.plos.ned_client.ApiException;
import org.plos.ned_client.api.QueriesApi;
import org.plos.ned_client.model.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @inheritDoc
 */
public class SavedSearchRetrieverImpl implements SavedSearchRetriever {
  private static final Logger log = LoggerFactory.getLogger(SavedSearchRetrieverImpl.class);
  private NedService nedService;

  public void setNedService(NedService nedService) {
    this.nedService = nedService;
  }

  /**
   * @inheritDoc
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SavedSearchJob> retrieveSearchAlerts(AlertType alertType, Date startTime, Date endTime) {

    List<SavedSearchJob> searchJobs = new ArrayList<SavedSearchJob>();

    List<Alert> alertList = nedService.getSearchAlerts(alertType, null);

    if ( (alertList != null) && (alertList.size() > 0) ) {
      for (Alert alert : alertList) {
        Long alertId = new Long(alert.getId());
        Long userProfileId = new Long(alert.getNedid());

        SavedSearchType sst = null;
        if ( alert.getName().equals("PLoSONE") ) {
          sst = SavedSearchType.JOURNAL_ALERT;
        } else {
          sst = SavedSearchType.USER_DEFINED;
        }

        searchJobs.add(SavedSearchJob.builder()
              .setUserProfileID(userProfileId)
              .setSavedSearchQueryID(alertId)
              .setSearchName(alert.getName())
              .setSearchString(alert.getQuery())
              .setHash(null)
              .setType(sst)
              .setFrequency(alert.getFrequency())
              .setStartDate(startTime)
              .setEndDate(endTime)
              .build());
      }
    }

    log.debug("Returning {} saved search(es) for type {}", searchJobs.size(), alertType);

    return searchJobs;
  }

}