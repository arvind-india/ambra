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

import org.springframework.dao.DataIntegrityViolationException;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Calendar;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick 3/7/12
 */
public class FlagTest extends BaseHibernateTest {

  @Test
  public void testSaveFlag() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    Annotation annotation = new Annotation(AnnotationType.COMMENT, 123l);
    hibernateTemplate.save(annotation);

    Flag flag = new Flag(FlagReasonCode.INAPPROPRIATE, annotation);
    flag.setComment("Mary Margaret teams up with Leroy, Storybrooke's resident trouble maker, " +
        "to help him sell candles during the Miner's Day Festival. Emma investigates Kathryn's " +
        "sudden disapperance. Meanwhile, in the fairy-tale land that was, the Seven Dwarfs is made " +
        "known as well as Grumpy's romance with the beautiful yet clumsy fairy Nova.");
    Serializable id = hibernateTemplate.save(flag);

    Flag storedFlag = (Flag) hibernateTemplate.get(Flag.class, id);
    assertNotNull(storedFlag, "Didn't store flag");

    assertNotNull(storedFlag.getFlaggedAnnotation(), "Didn't associate flag to an annotation");
    assertEquals(storedFlag.getFlaggedAnnotation().getID(), annotation.getID(), "associated flag to an annotation");

    assertEquals(storedFlag.getReason(), flag.getReason(), "stored incorrect reason code");
    assertEquals(storedFlag.getComment(), flag.getComment(), "stored incorrect comment");
    assertNotNull(storedFlag.getCreated(), "flag didn't get created date set");
    assertTrue(storedFlag.getCreated().getTime() >= testStart, "created date wasn't after test start");
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testSaveWithNullAnnotation() {

    hibernateTemplate.save(new Flag(FlagReasonCode.SPAM, null));
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testSaveWithNullReason() {

    Annotation annotation = new Annotation(AnnotationType.COMMENT, 123l);
    hibernateTemplate.save(annotation);

    hibernateTemplate.save(new Flag(null, annotation));
  }

  @Test
  public void testDoesNotCascadeDelete() {

    Annotation annotation = new Annotation(AnnotationType.COMMENT, 23l);
    Serializable annotationId = hibernateTemplate.save(annotation);

    Flag flag = new Flag(FlagReasonCode.INAPPROPRIATE, annotation);
    hibernateTemplate.save(flag);
    hibernateTemplate.delete(flag);
    assertNotNull(hibernateTemplate.get(Annotation.class, annotationId), "Flag deleted annotation");
  }

}
