/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.service.ned;

import org.ambraproject.models.SavedSearchQuery;
import org.ambraproject.search.SavedSearchRetriever;
import org.ambraproject.util.TextUtils;
import org.plos.ned_client.ApiClient;
import org.plos.ned_client.api.IndividualsApi;
import org.plos.ned_client.api.QueriesApi;
import org.plos.ned_client.model.Alert;
import org.plos.ned_client.model.Email;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by kpang on 2/12/16.
 */
public class NedServiceMock implements NedService {

  @Override
  public List<Alert> getSearchAlerts(SavedSearchRetriever.AlertType alertType, String journal) {

    String query1 = "{\"query\":\"test\",\"unformattedQuery\":\"\",\"volume\":\"\",\"eLocationId\":\"\",\"id\":\"\",\"filterSubjects\":[],\"filterKeyword\":\"\",\"filterArticleType\":[],\"filterJournals\":[\"PLoSONE\"],\"sort\":\"Relevance\",\"startPage\":0,\"pageSize\":10}";
    String query2 = "{\"query\":\"\",\"unformattedQuery\":\"everything:testing\",\"volume\":\"\",\"eLocationId\":\"\",\"id\":\"\",\"filterSubjects\":[],\"filterKeyword\":\"\",\"filterArticleType\":[],\"filterJournals\":[\"PLoSMedicine\"],\"sort\":\"Relevance\",\"startPage\":0,\"pageSize\":10}";
    String query3 = "{\"query\":\"\",\"unformattedQuery\":\"everything:debug\",\"volume\":\"\",\"eLocationId\":\"\",\"id\":\"\",\"filterSubjects\":[],\"filterKeyword\":\"\",\"filterArticleType\":[],\"filterJournals\":[\"PLoSMedicine\"],\"sort\":\"Relevance\",\"startPage\":0,\"pageSize\":10}";

    List<Alert> alertList = new ArrayList<Alert>();
    Alert alert = null;

    if (alertType == SavedSearchRetriever.AlertType.WEEKLY) {
      alert = new Alert();
      alert.setId(101);
      alert.setNedid(1111);
      alert.setFrequency("WEEKLY");
      alert.setName("weekly-0");
      alert.setQuery(query1);
      alertList.add(alert);

      alert = new Alert();
      alert.setId(102);
      alert.setNedid(1111);
      alert.setFrequency("WEEKLY");
      alert.setName("both-0");
      alert.setQuery(query3);
      alertList.add(alert);
    }
    else if (alertType == SavedSearchRetriever.AlertType.MONTHLY) {
      alert = new Alert();
      alert.setId(103);
      alert.setNedid(1111);
      alert.setFrequency("MONTHLY");
      alert.setName("monthly-0");
      alert.setQuery(query2);
      alertList.add(alert);

      alert = new Alert();
      alert.setId(104);
      alert.setNedid(1111);
      alert.setFrequency("MONTHLY");
      alert.setName("both-0");
      alert.setQuery(query3);
      alertList.add(alert);
    }

    return alertList;
  }

  @Override
  public List<Email> getEmailAddresses(int nedId) {
    List<Email> emailList = new ArrayList<Email>();
    if ( nedId == 1111 ) {
      Email email = new Email();
      email.setId(1);
      email.setNedid(nedId);
      email.setEmailaddress("savedSearch0@unittestexample.org");
      email.setIsactive(true);
      emailList.add(email);
    }
    return emailList;
  }
}
