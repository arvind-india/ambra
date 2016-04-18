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

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Tests for {@link CategoryService}.
 */
public class CategoryServiceTest {

  @Test
  public void testParseJsonFromRhino() throws Exception {
    String json = "{\"doi\":\"info:doi\\/10.1371\\/journal.pmed.0030099\",\"categories\":"
        + "[{\"path\":\"Virology\"},{\"path\":\"Non-Clinical Medicine\\/Bioethics\"},"
        + "{\"path\":\"Non-Clinical Medicine\\/Health Economics\"},{\"path\":\"Non-Clinical Medicine\\/Health Policy\"},"
        + "{\"path\":\"Public Health and Epidemiology\"},{\"path\":\"Science Policy\"},{\"path\":\"Hematology\"},"
        + "{\"path\":\"Non-Clinical Medicine\\/Medical Law\"},{\"path\":\"Infectious Diseases\"}]}";
    List<String> expected = Arrays.asList(
        "Virology",
        "Non-Clinical Medicine/Bioethics",
        "Non-Clinical Medicine/Health Economics",
        "Non-Clinical Medicine/Health Policy",
        "Public Health and Epidemiology",
        "Science Policy",
        "Hematology",
        "Non-Clinical Medicine/Medical Law",
        "Infectious Diseases");
    CategoryServiceImpl service = new CategoryServiceImpl();
    assertEquals(expected, service.parseJsonFromRhino(json, "info:doi/10.1371/journal.ppat.1002020"));
  }

  @Test
  public void testParseJsonFromRhino_error() throws Exception {
    String json = "{\"doi\":\"info:doi\\/10.1371\\/journal.pmed.1001357\","
        + "\"categories\":[]}";
    CategoryServiceImpl service = new CategoryServiceImpl();
    assertEquals(new ArrayList<String>(),
        service.parseJsonFromRhino(json, "info:doi/10.1371/journal.ppat.1002020"));
  }

  @Test
  public void testGetTopLevelCategories() throws Exception {
    CategoryService service = new CategoryServiceImpl();
    List<String> categories = Arrays.asList(
        "/top2/foo/bar/blaz",
        "/top3/lsjkd/thjwe/xvblj",
        "/top2/big",
        "/top4",
        "/top1/bam",
        "/top2",
        "/top3/blammo",
        "/top17/bomb"
    );
    assertEquals(service.getTopLevelCategories(categories),
        Arrays.asList("top2", "top3", "top4", "top1", "top17"));

    List<String> empty = new ArrayList<String>();
    assertEquals(service.getTopLevelCategories(empty), empty);

    List<String> trivial = Arrays.asList("/foo");
    assertEquals(service.getTopLevelCategories(trivial), Arrays.asList("foo"));

    trivial = Arrays.asList("/foo/bar");
    assertEquals(service.getTopLevelCategories(trivial), Arrays.asList("foo"));
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testGetTopLevelCategories_error() {
    CategoryService service = new CategoryServiceImpl();

    // No leading slash.
    List<String> categories = Arrays.asList("top2/foo/bar/blaz", "top3/lsjkd/thjwe/xvblj");
    service.getTopLevelCategories(categories);
  }
}
