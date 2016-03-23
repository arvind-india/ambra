/*
 * Copyright (c) 2006-2013 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.search;

import org.ambraproject.service.ned.NedService;
import org.ambraproject.models.Journal;
import org.ambraproject.models.SavedSearch;
import org.ambraproject.models.SavedSearchType;
import org.ambraproject.models.UserProfile;
import org.ambraproject.service.hibernate.HibernateServiceImpl;
import org.ambraproject.service.journal.JournalService;
import org.ambraproject.service.ned.NedService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.plos.ned_client.ApiException;
import org.plos.ned_client.api.IndividualsApi;
import org.plos.ned_client.model.Email;
import org.plos.ned_client.model.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.ambraproject.email.TemplateMailer;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Send saved searches
 *
 * @author Joe Osowski
 */
public class SavedSearchSenderImpl implements SavedSearchSender {
  private static final Logger log = LoggerFactory.getLogger(SavedSearchSenderImpl.class);

  private NedService nedService;

  public void setNedService(NedService nedService) {
    this.nedService = nedService;
  }

  protected static final String WEEKLY_FREQUENCY = "WEEKLY";
  protected static final String PRODUCTION_MODE = "PRODUCTION";
  protected static final String QA_MODE = "QA";

  protected JournalService journalService;
  protected TemplateMailer mailer;
  protected String mailFromAddress;
  protected String sendMode;
  protected String sendModeQAEMail;
  protected String alertHtmlEmail;
  protected String alertTextEmail;
  protected String savedSearchHtmlEmail;
  protected String savedSearchTextEmail;
  protected String imagePath;
  protected int resultLimit;

  /**
   * @inheritDoc
   */
  public void sendSavedSearch(SavedSearchJob searchJob) {

    log.debug("Received thread Name: {}", Thread.currentThread().getName());
    log.debug("Send emails for search ID: {}. {}", searchJob.getSavedSearchQueryID(), searchJob.getFrequency());

    final Map<String, Object> context = new HashMap<String, Object>();

    context.put("searchParameters", searchJob.getSearchParams());
    context.put("searchHitList", searchJob.getSearchHitList());
    context.put("startTime", searchJob.getStartDate());
    context.put("endTime", searchJob.getEndDate());
    context.put("imagePath", this.imagePath);
    context.put("resultLimit", this.resultLimit);

    //Create message
    Multipart content = createContent(context, searchJob.getType());

    String fromAddress = this.mailFromAddress;

    String toAddress = getAddressToSend(searchJob.getUserProfileID());

    String subject;

    if ( searchJob.getType().equals(SavedSearchType.USER_DEFINED) ) {
      subject = "Search Alert - " + searchJob.getSearchName();

      log.debug("Job result count: {}", searchJob.getSearchHitList().size());

      if ( searchJob.getSearchHitList().size() > 0 ) {
        log.debug("Sending mail: {}", toAddress);
        mail(toAddress, fromAddress, subject, context, content);
      } else {
        log.debug("Not sending mail: {}", toAddress);
      }
    } else {
      String[] journals = searchJob.getSearchParams().getFilterJournals();

      //Each alert can only be for one journal
      if(journals.length != 1) {
        throw new RuntimeException("Journal alert defined for multiple journals or journal filter not defined");
      }

      Journal j = journalService.getJournal(journals[0]);
      subject = j.getTitle() + " Journal Alert";

      log.debug("Job Result count: {}", searchJob.getSearchHitList().size());
      log.debug("Sending mail: {}", toAddress);

      mail(toAddress, fromAddress, subject, context, content);
    }

    log.debug("Completed thread Name: {}", Thread.currentThread().getName());
    log.debug("Completed send request for search ID: {}. {}", searchJob.getSavedSearchQueryID(), searchJob.getFrequency());
  }

  protected void mail(String toAddress, String fromAddress, String subject, Map<String, Object> context,
    Multipart content) {

    //If sendMode empty, do nothing
    if(sendMode != null) {
      if(sendMode.toUpperCase().equals(PRODUCTION_MODE)) {
        mailer.mail(toAddress, fromAddress, subject, context, content);
        log.debug("Mail sent, mode: {}, address: {}", new Object[] { PRODUCTION_MODE, toAddress});
      }

      if(sendMode.toUpperCase().equals(QA_MODE)) {
        mailer.mail(sendModeQAEMail, fromAddress, "(" + toAddress + ")" + subject, context, content);
        log.debug("Mail sent, mode: {}, address: {}", new Object[] { QA_MODE, sendModeQAEMail});
      }
    }
  }

  protected Multipart createContent(Map<String, Object> context, SavedSearchType type) {
    try {
      if(type.equals(SavedSearchType.JOURNAL_ALERT)) {
        return mailer.createContent(this.alertTextEmail, this.alertHtmlEmail, context);
      } else {
        return mailer.createContent(this.savedSearchTextEmail, this.savedSearchHtmlEmail, context);
      }
    } catch(IOException ex) {
      throw new RuntimeException(ex);
    } catch(MessagingException ex) {
      throw new RuntimeException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  protected String getAddressToSend(Long userProfileID) {
    String emailAddress = null;
    List<Email> emails = nedService.getEmailAddresses(userProfileID.intValue());
    for ( Email email : emails ) {
      if ( email.getIsactive() ) {
        emailAddress = email.getEmailaddress();
        break;
      }
    }
    return(emailAddress);
  }

  @Required
  public void setMailer(TemplateMailer mailer) {
    this.mailer = mailer;
  }

  @Required
  public void setMailFromAddress(String mailFromAddress) {
    this.mailFromAddress = mailFromAddress;
  }

  @Required
  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  @Required
  public void setAlertHtmlEmail(String alertHtmlEmail) {
    this.alertHtmlEmail = alertHtmlEmail;
  }

  @Required
  public void setAlertTextEmail(String alertTextEmail) {
    this.alertTextEmail = alertTextEmail;
  }

  @Required
  public void setSavedSearchHtmlEmail(String savedSearchHtmlEmail) {
    this.savedSearchHtmlEmail = savedSearchHtmlEmail;
  }

  @Required
  public void setSavedSearchTextEmail(String savedSearchTextEmail) {
    this.savedSearchTextEmail = savedSearchTextEmail;
  }

  @Required
  public void setSendMode(String sendMode) {
    this.sendMode = sendMode;
  }

  @Required
  public void setSendModeQAEMail(String sendModeQAEMail) {
    this.sendModeQAEMail = sendModeQAEMail;
  }

  @Required
  public void setResultLimit(int resultLimit) {
    this.resultLimit = resultLimit;
  }

  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }
}
