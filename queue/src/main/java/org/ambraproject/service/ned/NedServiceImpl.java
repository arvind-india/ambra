/*
 * Copyright (c) 2006-2013 by Public Library of Science
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

import org.ambraproject.search.SavedSearchRetriever;
import org.plos.ned_client.ApiClient;
import org.plos.ned_client.ApiException;
import org.plos.ned_client.api.IndividualsApi;
import org.plos.ned_client.api.QueriesApi;
import org.plos.ned_client.model.Alert;
import org.plos.ned_client.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Kyle Pang
 */
public class NedServiceImpl implements NedService {

  private static final Logger log = LoggerFactory.getLogger(NedServiceImpl.class);

  private String baseUri;
  private String username;
  private String password;
  private ApiClient apiClient;
  private QueriesApi queriesApi;
  private IndividualsApi individualsApi;

  public NedServiceImpl(String baseUri, String username, String password) {
    this.baseUri = baseUri;
    this.username = username;
    this.password = password;

    apiClient = new ApiClient();
    apiClient.setBasePath(baseUri);
    apiClient.setUsername(username);
    apiClient.setPassword(password);
    apiClient.setDebugging(true);

    queriesApi = new QueriesApi(apiClient);
    individualsApi = new IndividualsApi(apiClient);
  }

  public String getBaseUri() {
    return baseUri;
  }

  public void setBaseUri(String baseUri) {
    this.baseUri = baseUri;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public QueriesApi getQueriesApi() {
    return queriesApi;
  }

  public IndividualsApi getIndividualsApi() {
    return individualsApi;
  }

  @Override
  public List<Alert> getSearchAlerts(SavedSearchRetriever.AlertType alertType, String journal) {

    List<Alert> alertList = null;

    try {
      if (alertType == SavedSearchRetriever.AlertType.WEEKLY) {
        alertList = queriesApi.getAlerts("weekly");
      } else if (alertType == SavedSearchRetriever.AlertType.MONTHLY) {
        alertList = queriesApi.getAlerts("monthly");
      }
    } catch (ApiException apiEx) {
      log.error("getSearchAlerts() code: " + apiEx.getCode());
      log.error("getSearchAlerts() responseBody: " + apiEx.getResponseBody());
      log.error("getSearchAlerts() alertType: " + alertType );
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }

    log.debug("Returning {} saved search(es) for type {}", alertList.size(), alertType);

    return alertList;
  }

  @Override
  public List<Email> getEmailAddresses(int nedId) {

    List<Email> emailList = null;

    try {
      emailList = individualsApi.getEmails(nedId);
    } catch (ApiException apiEx) {
      log.error("getEmailAddresses() code: " + apiEx.getCode());
      log.error("getEmailAddresses() responseBody: " + apiEx.getResponseBody());
      log.error("getEmailAddresses() nedId: " + nedId );
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }

    log.debug("Returning {} email(s) for nedID {}", emailList.size(), nedId);

    return emailList;
  }
}
