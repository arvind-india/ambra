/*
 * Copyright (c) 2006-2016 by Public Library of Science
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

import java.util.List;

import org.ambraproject.search.SavedSearchRetriever;
import org.plos.ned_client.model.Alert;
import org.plos.ned_client.model.Email;

/**
 * @author Kyle Pang
 */
public interface NedService {
  public List<Alert> getSearchAlerts(SavedSearchRetriever.AlertType alertType, String journal);
  public List<Email> getEmailAddresses(int nedId);
}
