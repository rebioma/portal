/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.server.util;

import java.security.Security;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.rebioma.client.Email;
import org.rebioma.client.EmailException;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;

/**
 * This class is a utility for sending email from one user to another user
 * 
 * @author Tri
 * 
 */
public class EmailUtil {

  private static class PasswordAuthenticator extends Authenticator {
    private final String username;
    private final String password;

    public PasswordAuthenticator(String username, String password) {
      super();
      this.username = username;
      this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(username, password);
    }
  }

  private static final Logger logger = Logger.getLogger(EmailUtil.class);

  public static final String ADMIN_EMAIL = "Aaron D. Steele";

  private static Properties mailProperties = null;

  static {
    ResourceBundle rb = ResourceBundle.getBundle("SendMail");

    if (rb != null) {
      mailProperties = new Properties();
      Enumeration<String> keys = rb.getKeys();

      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        mailProperties.put(key, rb.getObject(key));
      }
    } else {
      logger.error("unable to open properties file " + "SendMail.properties"
          + " for send mail. Make sure it existed.", null);
    }
  }
  
  public static void adminSendEmailTo2(String recipientAddress, String subject,
	      String body) throws EmailException {
	    if (mailProperties == null) {

	      EmailException e = new EmailException("open properties file can't"
	          + "SendMail.properties" + " for send mail. Make sure it existed.");
	      logger.error(e.getMessage(), e);
	      throw e;
	    }
	    if (true) {
	      String senderAddress = "\""
	          + mailProperties.getProperty("mail.displayname", "Admin") + "\" <"
	          + mailProperties.getProperty("mail.from", "admin@gmail.com") + ">";
	      String username = mailProperties.getProperty("mail.from", "fail");
	      String password = mailProperties.getProperty("mail.password", "fail");
	      try {
	        sendMail2(subject, body, senderAddress, recipientAddress, username,
	            password);
	      } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(e.getMessage(), e);
	        throw new EmailException(senderAddress + " unable to send email to "
	            + recipientAddress);
	      }
	      return;
	    }
  }
  public static void adminSendEmailTo(String recipientAddress, String subject,
      String body) throws EmailException {
    if (mailProperties == null) {

      EmailException e = new EmailException("open properties file can't"
          + "SendMail.properties" + " for send mail. Make sure it existed.");
      logger.error(e.getMessage(), e);
      throw e;
    }
    if (true) {
      String senderAddress = "\""
          + mailProperties.getProperty("mail.displayname", "Admin") + "\" <"
          + mailProperties.getProperty("mail.from", "admin@gmail.com") + ">";
      String username = mailProperties.getProperty("mail.from", "fail");
      String password = mailProperties.getProperty("mail.password", "fail");
      try {
        sendMail(subject, body, senderAddress, recipientAddress, username,
            password);
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage(), e);
        throw new EmailException(senderAddress + " unable to send email to "
            + recipientAddress);
      }
      return;
    }
    // String senderAddress = "\""
    // + mailProperties.getProperty("mail.displayname", "Admin") + "\" <"
    // + mailProperties.getProperty("mail.from", "admin@gmail.com") + ">";
    // try {
    // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    //
    // Session session = Session.getDefaultInstance(mailProperties,
    // new PasswordAuthenticator(mailProperties.getProperty("mail.from",
    // "fail"), mailProperties.getProperty("mail.password", "fail")));
    // // MimeMessage message = new MimeMessage(session);
    // // message.setSender(new InternetAddress(sender));
    // // message.setSubject(subject);
    // // message.setContent(body, "text/plain");
    //
    // Message msg = new MimeMessage(session);
    //
    // InternetAddress sender = new InternetAddress(senderAddress);
    // InternetAddress recipient = new InternetAddress(recipientAddress);
    // msg.setFrom(sender);
    // // msg.setFrom();
    // msg.setRecipient(Message.RecipientType.TO, recipient);
    // msg.setSubject(subject);
    // msg.setContent(body, "text/plain");
    // msg.setSentDate(new Date());
    // // Charset s;
    // // msg.setText(body);
    // // final Transport transport = session.getTransport("smtp");
    // // transport.connect();
    // // transport.connect(mailProperties.getProperty("mail.smtp.host",
    // // "smtp.gmail.com"), Integer.parseInt(mailProperties.getProperty(
    // // "mail.smtp.port", "25")), mailProperties.getProperty("mail.from",
    // // "fail"), mailProperties.getProperty("mail.password", "fail"));
    // // transport.sendMessage(msg, new InternetAddress[] { recipient });
    // // transport.close();
    // Transport.send(msg);
    // } catch (MessagingException e) {
    // logger.error(e.getMessage(), e);
    // e.printStackTrace();
    // throw new EmailException(senderAddress + " unable to send email to "
    // + recipientAddress);
    // }
  }

  /**
   * quick test
   * 
   * @param agrs
   */
  public static void main(String agrs[]) {
    try {
      adminSendEmailTo("wilfried@rebioma.net", "test", "test");
      System.out.println("mail envoyé");
    } catch (EmailException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void notifyUserAssignmentNewOrChanged(User user,
      Set<Occurrence> occurrences) throws EmailException {
    // if (!RecordReviewUtil.isDevMode()) {
    // logger.info("not in development mode");
    // return;
    // }
    if (!occurrences.isEmpty()) {
      String subject = "[REBIOMA PORTAL] New record assignment notification";
      StringBuilder bodyBuilder = new StringBuilder("Dear "
          + user.getFirstName() + " " + user.getLastName() + ",\n\n");

      bodyBuilder
          .append("You are receiving this email as a member of the REBIOMA Taxonomic Review Board. New or changed occurrence records have been assigned to you for review. \n");
      //for (Occurrence occurrence : occurrences) {
      //  bodyBuilder.append(occurrence.getId() + ", ");
      //}
      bodyBuilder.delete(bodyBuilder.length() - 2, bodyBuilder.length());
      bodyBuilder.append("\n\n");
      bodyBuilder
          .append("To find your occurrences to review, sign into the REBIOMA data portal (data.rebioma.net) with your email and password and select \"My Occurrences to Review\", or use \"Advanced Search\" to search for specific records.");
      bodyBuilder.append("\n\n");
      bodyBuilder.append(Email.REBIOMA_TEAM);
      String userEmail = user.getEmail();
      userEmail = RecordReviewUtil.getSentEmail(userEmail);
      adminSendEmailTo(userEmail, subject, bodyBuilder.toString());
      logger.info("successfully send nofication email to " + userEmail);
    } else {
      logger.info("no email was sent to " + user.getEmail()
          + " because no assignment match");
    }

  }

  public static void notifyUserNewAssignment(User user,
      Integer assignmentCount, Map<String, Set<String>> userToTaxoAssignMap)
      throws EmailException {
    // if (!RecordReviewUtil.isDevMode()) {
    // logger.info("not in development mode");
    // return;
    // }
    String subject = "[REBIOMA PORTAL] New reviewer assignment notification";
    StringBuilder bodyBuilder = new StringBuilder("Dear " + user.getFirstName()
        + " " + user.getLastName() + ",\n\n");
    bodyBuilder
        .append("You are receiving this email as a member of the REBIOMA Taxonomic Review Board. The following taxonomic fields and values have been assigned to you: \n");
    for (String field : userToTaxoAssignMap.keySet()) {
      bodyBuilder.append("taxonomic field: " + field
          + " with associated value(s) " + userToTaxoAssignMap.get(field)
          + "\n");
    }
    bodyBuilder.append("\n");
    if (assignmentCount > 0) {
      bodyBuilder
          .append("There are "
              + assignmentCount
              + " records awaiting your review based on the above taxonomic assignment.\n\n");
      bodyBuilder
          .append("To find your occurrences to review, sign into the REBIOMA data portal (data.rebioma.net) with your email and password and select \"My Occurrences to Review\", or use \"Advanced Search\" to search for specific records by taxonomy.");

    } else {
      bodyBuilder
          .append("no existing occurrence yet match with your assignment.\n");
    }
    bodyBuilder.append("\n\n");
    bodyBuilder.append(Email.REBIOMA_TEAM);
    String userEmail = user.getEmail();
    userEmail = RecordReviewUtil.getSentEmail(userEmail);
    adminSendEmailTo(userEmail, subject, bodyBuilder.toString());
    logger.info("successfully sent notification email to " + userEmail);
  }

  public static void notifyUserReviewedChangeToNeg(User owner,
      Set<Integer> occurrenceIds) throws EmailException {
    String subject = "[REBIOMA PORTAL] Record(s) Questionably Reviewed notification";
    StringBuilder bodyBuilder = new StringBuilder("Dear "
        + owner.getFirstName() + " " + owner.getLastName() + ",\n\n");
    bodyBuilder.append("There are " + occurrenceIds.size()
        + " records that have been questionably reviewed.\n\n");
    bodyBuilder
        .append("To find your reviewed records, sign into the REBIOMA data portal (data.rebioma.net) with your email and password and select \"My Positively Reviews\" or \"My Negatively Reviews\", or use \"Advanced Search\" to search for specific records by REBIOMA id.");
    bodyBuilder.append("\n\n");
    bodyBuilder.append(Email.REBIOMA_TEAM);
    String userEmail = owner.getEmail();
    userEmail = RecordReviewUtil.getSentEmail(userEmail);
    adminSendEmailTo(userEmail, subject, bodyBuilder.toString());
    logger.info("successfully sent nofication email to " + userEmail);
  }
  
  public static void notifyUserForRevalidation(User owner,
	      Set<Integer> occurrenceIds, String subjects, String body) throws EmailException {
	    String subject = "[REBIOMA PORTAL] Record(s) Questionably for Revalidation :"+subjects;
	    StringBuilder bodyBuilder = new StringBuilder("Dear "
	        + owner.getFirstName() + " " + owner.getLastName() + ",\n\n");
	    //bodyBuilder.append("There are " + occurrenceIds.size()+" "+ body);
	    bodyBuilder.append(body);
	    bodyBuilder.append("\n These are the ID of all records affected:  "+buildOclist(occurrenceIds));
	   // bodyBuilder
	      //  .append(" \n To find your  records, sign into the REBIOMA data portal (data.rebioma.net) with your email and password and select \"My Positively Reviews\" or \"My Negatively Reviews\", or use \"Advanced Search\" to search for specific records by REBIOMA id.");
	    
	    bodyBuilder.append(" \n\n To find your  records, sign into the REBIOMA data portal (data.rebioma.net) with your email and password and select use \"Advanced Search\" to search for specific records by REBIOMA id.");
	    bodyBuilder.append(" Then, once you found out affected records, please check comments left by the TRB members and update your records. To update some informations in your records, you can download it then re-upload it. Or you can modify it directly on the data portal.");
	    bodyBuilder.append("\n\n");
	    bodyBuilder.append(Email.REBIOMA_TEAM);
	    String userEmail = owner.getEmail();
	    userEmail = RecordReviewUtil.getSentEmail(userEmail);
	    adminSendEmailTo(userEmail, subject, bodyBuilder.toString());
	    logger.info("successfully sent nofication email to " + userEmail);
	  }
  
  
  private static String buildOclist(Set<Integer> occurrenceIds){
	  String list="";

	  for(int id:occurrenceIds){
		list+=""+id+" ,";  
	  }	  
	  return list;	  
  }
  /**
   * This method sends an email from sender address to recipient address with a
   * given subject and body content. It throws and SendEmailException if there
   * is an error occurs during mail sending process. If the sender address is
   * null, it uses ADMIN_EMAIL instead. This method doesn't check for
   * non-existing email address since the user can only log in to the Rebioma
   * Portal using a password that is sent in an welcome email.
   * 
   * @param senderAddress the sender email address.
   * @param recipientAddress the recipient email address.
   * @param subject subject of a sending email.
   * @param body content of a sending email.
   * @throws EmailException if there is an error occurs during mail sending
   *           process.
   */
  public static void sendEmailTo(String senderAddress, String recipientAddress,
      String subject, String body) throws EmailException {
    // try {
    // if (senderAddress == null) {
    // senderAddress = ADMIN_EMAIL;
    // }
    // Properties props = new Properties();
    // props.put("mail.smtp.host", "smtp.gmail.com");
    // // props.put("mail.smtp.user", "tri282");
    // props.put("mail.smtp.port", 25);
    // // props.put("mail.from", "tri282@gmail.com");
    // props.put("mail.smtp.auth", "true");
    // props.put("mail.smtp.starttls.enable", "true");
    //
    // Session session = Session.getInstance(props, new
    // PasswordAuthenticator());
    //
    // Message msg = new SMTPMessage(session);
    //
    // InternetAddress sender = new InternetAddress(senderAddress);
    // InternetAddress recipient = new InternetAddress(recipientAddress);
    // msg.setFrom(sender);
    // msg.setRecipient(Message.RecipientType.TO, recipient);
    // msg.setSubject(subject);
    // msg.setSentDate(new Date());
    // msg.setFlag(Flag.RECENT, true);
    // // Charset s;
    // msg.setText(body);
    // Transport.send(msg);
    //
    // } catch (MessagingException e) {
    // logger.error(e.getMessage(), e);
    // e.printStackTrace();
    // throw new EmailException(senderAddress + " unable to send email to "
    // + recipientAddress);
    // }
  }

  public static synchronized void sendMail(String subject, String body,
      String sender, String recipients, final String username,
      final String password) throws Exception {
    //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    Properties props = new Properties();
    props.setProperty("mail.transport.protocol", "smtp");
    props.setProperty("mail.host", "smtp.gmail.com");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.socketFactory.port", "465");
    props
        .put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.quitwait", "false");

    Session session = Session.getDefaultInstance(props,
        new javax.mail.Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });
    MimeMessage message = new MimeMessage(session);
    message.setSender(new InternetAddress(sender));
    message.setSubject(subject);
    message.setContent(body, "text/plain");
    //verify devmode
    recipients = RecordReviewUtil.getSentEmail(recipients);
    if (recipients.indexOf(',') > 0) {
      message.setRecipients(Message.RecipientType.TO, InternetAddress
          .parse(recipients));
    } else {
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(
          recipients));
    }
    Transport.send(message);
  }
  
  public static synchronized void sendMail2(String subject, String body,
	      String sender, String recipients, final String username,
	      final String password) throws Exception {
	    //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	    Properties props = new Properties();
	    props.setProperty("mail.transport.protocol", "smtp");
	    props.setProperty("mail.host", "smtp.gmail.com");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.port", "465");
	    props.put("mail.smtp.socketFactory.port", "465");
	    props
	        .put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.socketFactory.fallback", "false");
	    props.setProperty("mail.smtp.quitwait", "false");

	    Session session = Session.getDefaultInstance(props,
	        new javax.mail.Authenticator() {
	          @Override
	          protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	          }
	        });
	    MimeMessage message = new MimeMessage(session);
	    message.setSender(new InternetAddress(sender));
	    message.setSubject(subject);
	    message.setContent(body, "text/html");
	    //verify devmode
	    recipients = RecordReviewUtil.getSentEmail(recipients);
	    if (recipients.indexOf(',') > 0) {
	      message.setRecipients(Message.RecipientType.TO, InternetAddress
	          .parse(recipients));
	    } else {
	      message.setRecipient(Message.RecipientType.TO, new InternetAddress(
	          recipients));
	    }
	    Transport.send(message);
	  }
}
