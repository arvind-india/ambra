/*
 * Copyright (c) 2006-2014 by Public Library of Science
 *
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.service.article;

import org.w3c.dom.Document;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Kudlick
 *         Date: 7/5/12
 */
public class DummyArticleClassifier implements ArticleClassifier {
  @Override
  public Map<String, Integer> classifyArticle(Document articleXml) throws Exception {
    return new LinkedHashMap<String, Integer>() {{
      put("/TopLevel1/term1", 5);
      put("/TopLevel2/term2", 10);
    }};
  }

  public void testThesaurus(OutputStream os, String doi, String thesaurus) throws Exception
  {
    throw new Exception("not implemented.");
  }
}
