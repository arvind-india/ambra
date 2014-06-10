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
package org.ambraproject.views;

import java.io.Serializable;
import java.util.Date;

public class TOCRelatedArticle implements Serializable, Comparable<TOCRelatedArticle> {
  private final String doi;
  private final String title;
  private final String relationType;
  private final Date date;

  public TOCRelatedArticle(String doi, String title, String relationType, Date date) {
    this.doi = doi;
    this.title = title;
    this.relationType = relationType;
    this.date = date;
  }

  public String getDoi() {
    return doi;
  }

  public String getTitle() {
    return title;
  }

  public String getRelationType() {
    return relationType;
  }

  public Date getDate() {
    return date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TOCRelatedArticle)) return false;

    TOCRelatedArticle that = (TOCRelatedArticle) o;

    if(!doi.equals(that.doi)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return doi.hashCode();
  }

  @Override
  public int compareTo(TOCRelatedArticle o) {
    //Sort by date
    if (this.getDate() == null || o.getDate() == null) {
      return 0;
    }

    return o.getDate().compareTo(this.getDate());
  }
}
