/*
 * Copyright (c) 2007-2014 by Public Library of Science
 *
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.models;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Test the userProfileMetaData data model
 */
public class UserProfileMetaDataTest extends BaseHibernateTest {
  @Test
  public void testProfileMetaData() {
    UserProfileMetaData userProfileMetaData = new UserProfileMetaData();

    Long userProfileID = 1000L;

    userProfileMetaData.setUserProfileID(userProfileID);
    userProfileMetaData.setMetaKey("key");
    userProfileMetaData.setMetaValue("value");

    hibernateTemplate.save(userProfileMetaData);

    UserProfileMetaData userProfileMetaDataStored =
      hibernateTemplate.get(UserProfileMetaData.class, userProfileMetaData.getID());

    assertEquals(userProfileMetaDataStored.getMetaKey(), "key");
    assertEquals(userProfileMetaDataStored.getMetaValue(), "value");
    assertEquals(userProfileMetaDataStored.getUserProfileID(), userProfileID);
  }
}

