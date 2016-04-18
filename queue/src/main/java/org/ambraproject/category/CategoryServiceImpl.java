/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2013 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.category;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

/**
 * {@inheritDoc}
 */
public class CategoryServiceImpl implements CategoryService {

  private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

  private String rhinoServer;

  public void setRhinoServer(String rhinoServer) {
    this.rhinoServer = rhinoServer;
  }

  /**
   * Extracts the categories from the JSON returned by the rhino server.
   *
   * @param json response from the rhino server to a get categories call
   * @return List of category Strings
   */
  List<String> parseJsonFromRhino(String json, String doi) {

    JsonParser parser = new JsonParser();
    JsonObject obj = parser.parse(json).getAsJsonObject();
    JsonArray categories;
    try {
      categories = obj.getAsJsonArray("categories");
    } catch (ClassCastException cce) {
      log.error(String.format("Exception processing category JSON for %s.  "
          + "This probably means that this is not a valid DOI.", doi), cce);
      return new ArrayList<String>();
    }
    List<String> result = new ArrayList<String>(categories.size());
    for (JsonElement category : categories) {
      String path = category.getAsJsonObject().get("path").getAsString();
      result.add(path);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public List<String> fetchCategoriesFromRhino(String doi) {
    String json;

    if ("info:doi/".equals(doi.substring(0, 9))) {
      doi = doi.substring(9);
    }

    try {
      URL url = new URL(rhinoServer + "/articles/" + doi);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.connect();
      InputStream is = conn.getInputStream();
      try {
        json = IOUtils.toString(is).trim();
      } finally {
        is.close();
      }
    } catch (IOException ioe) {

      // Log the exception, but carry on.
      // TODO: we've seen a problem where if we throw an exception here,
      // the queue just stops and fails to make forward progress.  Look
      // into our error handling strategy in detail to make sure we're
      // doing the right thing.
      log.error("Failed to fetch categories from rhino", ioe);
      return new ArrayList<String>();
    }
    return parseJsonFromRhino(json, doi);
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getTopLevelCategories(List<String> categories) {

    // Since we want to return the top-level categories in the order they
    // are listed, we can't just use a HashSet.  Using a TreeSet with a
    // custom comparator is a possibility, but seems like more trouble
    // than it's worth for small lists.  Instead, we just use an n-squared
    // list approach here.
    List<String> results = new ArrayList<String>(categories.size());
    for (String category : categories) {
      if ('/' != category.charAt(0)) {
        throw new IllegalArgumentException("Categories must begin with '/': " + category);
      }
      category = category.split("\\/")[1];
      boolean alreadyExists = false;
      for (String existing : results) {
        if (category.equals(existing)) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        results.add(category);
      }
    }
    return results;
  }
}
