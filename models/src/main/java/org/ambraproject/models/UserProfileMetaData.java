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

/**
 * POJO for the userProfileMetaData table
 */
public class UserProfileMetaData extends AmbraEntity {
  private Long userProfileID;
  private String metaKey;
  private String metaValue;

  public Long getUserProfileID() {
    return userProfileID;
  }

  public void setUserProfileID(Long userProfileID) {
    this.userProfileID = userProfileID;
  }

  public String getMetaKey() {
    return metaKey;
  }

  public void setMetaKey(String metaKey) {
    this.metaKey = metaKey;
  }

  public String getMetaValue() {
    return metaValue;
  }

  public void setMetaValue(String metaValue) {
    this.metaValue = metaValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserProfileMetaData)) return false;

    UserProfileMetaData that = (UserProfileMetaData) o;

    if (!metaKey.equals(that.metaKey)) return false;
    if (metaValue != null ? !metaValue.equals(that.metaValue) : that.metaValue != null) return false;
    if (!userProfileID.equals(that.userProfileID)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = userProfileID.hashCode();
    result = 31 * result + metaKey.hashCode();
    result = 31 * result + (metaValue != null ? metaValue.hashCode() : 0);
    return result;
  }
}
