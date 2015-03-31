<#--
  Copyright (c) 2007-2013 by Public Library of Science
  http://plos.org
  http://ambraproject.org
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licen
  se for the specific language governing permissions and
  limitations under the License.
-->
<@s.url action="privacy" namespace="/static" includeParams="none" id="privacyURL"/>

<#assign moodleUser = false>
<#list currentUser.roles as role>
  <#if role.roleName="AE-PLOSONE" || role.roleName="Editor-BIO" || role.roleName="Editor-CB" || role.roleName="Editor-Gen" || role.roleName="Editor-MED" ||
       role.roleName="Editor-NTDs" || role.roleName="Editor-Pathog">
    <#assign moodleUser = true>
    <#break>
  </#if>
</#list>

<div id="user-forms" class="tab-block" active="${tabID}">
  <div id="profileHeader">
    <div>
      <h1 class="displayName">${displayName}</h1>
      <ul class="info-list">
        <li>Display Name: <b>${displayName}</b>&nbsp;<span class="note">(Display names are permanent)</span></li>
        <li>
          Email: <b>${email}</b> <a href="${freemarker_config.changeEmailURL}"
                                    title="Click here to change your e-mail address">Change your e-mail address</a>
        </li>
        <li class="close-top note">(Your e-mail address will always be kept private. See the <a
          href="${privacyURL}">${freemarker_config.orgName} Privacy Statement</a> for more information.)
        </li>
        <li>
          <a href="${freemarker_config.changePasswordURL}" title="Click here to change your password">Change your
            password</a>
        </li>
      </ul>

      <div id="user-tabs" class="nav tab-nav">
        <ul>
          <li><a href="#profile" url="/user/secure/profile">Profile</a></li>
          <li><a href="#journalAlerts" url="/user/secure/profile/alerts/journal">Journal Alerts</a></li>
          <li><a href="#savedSearchAlerts" url="/user/secure/profile/alerts/search">Search Alerts</a></li>
        </ul>
      </div>
    </div>

    <#if moodleUser = true>
      <div id="edBoardKnowledgeBasePlug">

        <h5> Editorial Board Knowledge Base </h5>

        <p>Learn more about our policies and practices as well as participate in our forums:</p>

        <#assign countRoles = 0>

	<#assign myRoles = []>
        <#list currentUser.roles as role>
	  <#assign myRoles = myRoles + [role.roleName]>
	</#list>

        <#list myRoles?sort as role>

          <#if role = "AE-PLOSONE"> 
            <p><a href="http://one.editors.plos.org">PLOS ONE<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if role = "Editor-BIO"> 
            <p><a href="http://biology.editors.plos.org">PLOS Biology<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if role = "Editor-CB"> 
            <p><a href="http://compbiol.editors.plos.org">PLOS Computational Biology<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if role = "Editor-Gen"> 
            <p><a href="http://genetics.editors.plos.org">PLOS Genetics<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if role = "Editor-MED"> 
            <p><a href="http://medicine.editors.plos.org">PLOS Medicine<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if role = "Editor-NTDs"> 
            <p><a href="http://ntds.editors.plos.org">PLOS NTDs<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if role = "Editor-Pathog"> 
            <p><a href="http://pathogens.editors.plos.org">PLOS Pathogens<img src="/images/transparent.gif" height="13" width="13"/></a></p>
            <#assign countRoles = countRoles + 1>
          </#if>

          <#if countRoles gte 5>
            <#break>
          </#if>

        </#list>

      </div>
    </#if>


    <div class="clear"></div>
  </div>
  <div class="tab-content">
    <div id="profile" class="tab-pane">
      <#include "profileForm.ftl">
    </div>
    <div id="journalAlerts" class="tab-pane">
      <#include "alertsForm.ftl">
    </div>
    <div id="savedSearchAlerts" class="tab-pane">
      <#include "searchAlertsForm.ftl">
    </div>
  </div>
</div>

<div id="save-confirm">
  Your preferences have been saved
</div>
