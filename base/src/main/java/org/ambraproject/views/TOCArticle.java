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

import org.ambraproject.views.article.ArticleType;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Represents an article for use in a table of contents context
 */
public class TOCArticle {
  final String doi;
  final String title;
  final List<String> authors;
  final List<String> collaborativeAuthors;
  final Set<ArticleType> articleTypes;
  final List<TOCRelatedArticle> relatedArticles;
  final String publishedJournal;
  final Date date;
  final boolean hasFigures;

  public TOCArticle(Builder builder) {
    doi = builder.doi;
    title = builder.title;
    authors = builder.authors;
    collaborativeAuthors = builder.collaborativeAuthors;
    articleTypes = builder.articleTypes;
    relatedArticles = builder.relatedArticles;
    publishedJournal = builder.publishedJournal;
    date = builder.date;
    hasFigures = builder.hasFigures;
  }

  public String getDoi() {
    return doi;
  }

  public String getTitle() {
    return title;
  }

  public List<String> getAuthors() {
    return authors;
  }

  public List<String> getCollaborativeAuthors() {
    return collaborativeAuthors;
  }

  public Set<ArticleType> getArticleTypes() {
    return articleTypes;
  }

  public List<TOCRelatedArticle> getRelatedArticles() {
    return relatedArticles;
  }

  public String getPublishedJournal() {
    return publishedJournal;
  }

  public Date getDate() {
    return date;
  }

  public boolean getHasFigures() {
    return hasFigures;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {
      super();
    }

    String doi;
    String title;
    List<String> authors;
    List<String> collaborativeAuthors;
    Set<ArticleType> articleTypes;
    List<TOCRelatedArticle> relatedArticles;
    String publishedJournal;
    Date date;
    boolean hasFigures;

    public Builder setDoi(String doi) {
      this.doi = doi;
      return this;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setAuthors(List<String> authors) {
      this.authors = authors;
      return this;
    }

    public Builder setCollaborativeAuthors(List<String> collaborativeAuthors) {
      this.collaborativeAuthors = collaborativeAuthors;
      return this;
    }

    public Builder setArticleTypes(Set<ArticleType> articleTypes) {
      this.articleTypes = articleTypes;
      return this;
    }

    public Builder setRelatedArticles(List<TOCRelatedArticle> relatedArticles) {
      this.relatedArticles = relatedArticles;
      return this;
    }

    public Builder setPublishedJournal(String publishedJournal) {
      this.publishedJournal = publishedJournal;
      return this;
    }

    public Builder setDate(Date date) {
      this.date = date;
      return this;
    }

    public Builder setHasFigures(boolean hasFigures) {
      this.hasFigures = hasFigures;
      return this;
    }

    public TOCArticle build() {
      return new TOCArticle(this);
    }
  }
}
