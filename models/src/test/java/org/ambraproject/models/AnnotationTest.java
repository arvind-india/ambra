/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 3/7/12
 */
public class AnnotationTest extends BaseHibernateTest {

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testSaveWithNullCreator() {
    hibernateTemplate.save(new Annotation(null, AnnotationType.COMMENT, 12l));
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testSaveWithNullType() {
    UserProfile creator = new UserProfile("email@nullType.org", "displayNameForNullType", "pass");
    hibernateTemplate.save(creator);
    hibernateTemplate.save(new Annotation(creator, null, 12l));
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testSaveWithNullArticleID() {
    UserProfile creator = new UserProfile("email@nullArticleID.org", "displayNameForNullArticleID", "pass");
    hibernateTemplate.save(creator);
    hibernateTemplate.save(new Annotation(creator, AnnotationType.COMMENT, null));
  }

  @Test
  public void testSaveBasicAnnotation() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    UserProfile creator = new UserProfile(
        "email@InsertAnnotation.org",
        "displayNameForInsertAnnotation",
        "pass");
    hibernateTemplate.save(creator);
    Annotation annotation = new Annotation();
    annotation.setCreator(creator);
    annotation.setAnnotationUri("fakeAnnotationUriForInsert");
    annotation.setArticleID(1l);
    annotation.setType(AnnotationType.COMMENT);
    annotation.setTitle("What Happened to Frederick");
    annotation.setBody("With their love for each other growing stronger, David finally agrees to tell " +
        "Kathryn about his relationship with Mary Margaret and put an end to his loveless marriage. " +
        "Meanwhile, in the fairytale land that was, while runaway groom Prince Charming searches for " +
        "Snow White, he agrees to aid Abigail on a dangerous mission to recover something precious " +
        "that was lost to her.");

    Serializable id = hibernateTemplate.save(annotation);

    Annotation storedAnnotation = (Annotation) hibernateTemplate.get(Annotation.class, id);
    assertNotNull(storedAnnotation, "Didn't store annotation");
    assertEquals(storedAnnotation.getAnnotationUri(), annotation.getAnnotationUri(), "Didn't store annotation uri");
    assertEquals(storedAnnotation.getArticleID(), annotation.getArticleID(), "Didn't store article id");
    assertEquals(storedAnnotation.getType(), annotation.getType(), "Didn't store type");
    assertEquals(storedAnnotation.getTitle(), annotation.getTitle(), "Didn't store correct title");
    assertEquals(storedAnnotation.getBody(), annotation.getBody(), "Didn't store correct body");
    assertNotNull(storedAnnotation.getCreator(), "didn't link to creator");
    assertEquals(storedAnnotation.getCreator().getAuthId(), annotation.getCreator().getAuthId(), "linked to incorrect creator");

    assertNotNull(storedAnnotation.getCreated(), "Annotation didn't get created date set");
    assertTrue(storedAnnotation.getLastModified().getTime() >= testStart, "Created date wasn't after test start");
  }

  @Test
  public void testDoesNotCascadeDeleteToCreator() {
    UserProfile creator = new UserProfile(
        "email@CascadeDelete.org",
        "displayNameForCascadeDelete",
        "pass");
    Serializable creatorId = hibernateTemplate.save(creator);
    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, 23l);
    hibernateTemplate.save(annotation);
    hibernateTemplate.delete(annotation);
    assertNotNull(hibernateTemplate.get(UserProfile.class, creatorId), "Annotation deleted creator");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testLoadTypeFromStringRepresentation() {
    final Long userId = (Long) hibernateTemplate.save(new UserProfile(
        "email@LoadType.org",
        "displayNameForLoadType",
        "pass"));
    final Long articleId = (Long) hibernateTemplate.save(new Article("id:doi-for-LoadType"));

    hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        session.createSQLQuery(
            "insert into annotation (created, lastModified, userProfileID, articleID, type, annotationURI) " +
                "values (?,?,?,?,?,?)")
            .setParameter(0, Calendar.getInstance().getTime(), StandardBasicTypes.DATE)
            .setParameter(1, Calendar.getInstance().getTime(), StandardBasicTypes.DATE)
            .setParameter(2, userId, StandardBasicTypes.LONG)
            .setParameter(3, articleId, StandardBasicTypes.LONG)
            .setParameter(4, "Comment", StandardBasicTypes.STRING)
            .setParameter(5, "unique-annotation-uri-for-loadTypeFromString", StandardBasicTypes.STRING)
            .executeUpdate();
        return null;
      }
    });

    List<Annotation> results = (List<Annotation>) hibernateTemplate.find(
        "from Annotation where annotationUri = ?", "unique-annotation-uri-for-loadTypeFromString");
    assertEquals(results.size(), 1, "didn't store annotation correctly");
    assertEquals(results.get(0).getType(), AnnotationType.COMMENT, "Type wasn't loaded correctly");
  }
}
