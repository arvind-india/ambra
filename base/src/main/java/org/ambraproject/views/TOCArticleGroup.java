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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.ambraproject.views.article.ArticleType;

/**
 * Represents a group of articles for display in the presentation layer. 
 *  
 * @author Alex
 */
public class TOCArticleGroup {
  ArticleType type;
  public ArrayList<TOCArticle> articles = new ArrayList<TOCArticle>();
  private String id = null;
  private String heading = null;
  private String pluralHeading = null;

  public TOCArticleGroup(ArticleType type) {
    this.type = type;
  }

  /**
   * The heading displayed for this article group. 
   *
   * @return the heading, or the string "Undefined"
   */
  public String getHeading() {
    if (heading != null) {
      return heading;
    } else if (type != null) {
      return type.getHeading();
    } else {
      return "Undefined";
    }
  }

  /**
   * The plural heading displayed for this article group.
   *
   * @return the plural heading, or the string "Undefined"
   */
  public String getPluralHeading() {
    if (pluralHeading != null) {
      return pluralHeading;
    } else if (type != null) {
      return type.getPluralHeading();
    } else {
      return "Undefined";
    }
  }

  /**
   * Set the article group heading
   * @param h the new heading
   */
  public void setHeading(String h) {
    heading = h;
  }

  /**
   * 'set the article group plural heading
   * @param ph the new plural heading
   */
  public void setPluralHeading(String ph) {
    pluralHeading = ph;
  }
  /**
   * An id for the group to be used in html. May be set by the action for each ArticleGroup
   * and should be unique within that group. 
   * 
   * @param i   id
   */
  public void setId(String i) {
    this.id = i;
  }

  public String getId() {
    if (id != null) {
      return id;
    } else {
      return "id_"+getHeading();
    }
  }

  public void addArticle(TOCArticle article) {
    articles.add(article);
  }

  public ArticleType getArticleType() {
    return type;
  }

  public ArrayList<TOCArticle> getArticles() {
    return articles;
  }

  /**
   * Returns the count of articles in the group
   * @return the count of articles in the group
   */
  public int getCount() {
    return articles.size();
  }

  public void sortArticles() {
    Collections.sort(articles, new Comparator<TOCArticle>() {
      @Override
      public int compare(TOCArticle o1, TOCArticle o2) {
        if (o1.getDate().after(o2.getDate())) {
          return -1;
        }
        if (o1.getDate().before(o2.getDate())) {
          return 1;
        }

        return o1.getDoi().compareTo(o2.getDoi());
      }
    });
  }
}
