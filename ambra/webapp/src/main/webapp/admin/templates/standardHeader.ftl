<#--
  $HeadURL$
  $Id$

  Copyright (c) 2006-2009 by Topaz, Inc.
  http://topazproject.org

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<@s.url id="adminTop" namespace="/admin" action="adminTop"/>
<@s.url id="manageFlags" namespace="/admin" action="manageFlags"/>
<@s.url id="manageAnnotation" namespace="/admin" action="manageAnnotation"/>
<@s.url id="manageUsersURL" namespace="/admin" action="findUser" />
<@s.url id="manageVirtualJournalsURL" namespace="/admin" action="manageVirtualJournals" />
<@s.url id="manageCaches" namespace="/admin" action="manageCaches" />
<#if journal??><@s.url id="crossPubManagement" namespace="/admin" action="crossPubManagement" journalKey="${journal.key}" journalEIssn="${journal.eIssn}" /></#if>
<p style="text-align: right">
  <@s.a href="${adminTop}">Admin Top</@s.a>&nbsp;|&nbsp;
  <@s.a href="${manageFlags}">Manage Flags</@s.a>&nbsp;|&nbsp;
  <@s.a href="${manageAnnotation}">Manage Annotations</@s.a>&nbsp;|&nbsp;
  <@s.a href="${manageUsersURL}">Manage Users</@s.a>&nbsp;|&nbsp;
  <@s.a href="${manageVirtualJournalsURL}">Manage Virtual Journals</@s.a>&nbsp;|&nbsp;
  <#if journal??><@s.a href="${crossPubManagement}">Cross Publish Articles</@s.a> &nbsp;|&nbsp;</#if>
  <@s.a href="${manageCaches}">Manage Caches</@s.a>
</p>
<hr/>
<#if journal??><h2>${journal.key} (${journal.eIssn!""})</h2></#if>