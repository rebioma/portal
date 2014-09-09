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
package org.rebioma.server.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.EmailException;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;
import org.rebioma.server.services.OccurrenceDb.AttributeValue;
import org.rebioma.server.upload.Traitement;
import org.rebioma.server.util.CsvUtil;
import org.rebioma.server.util.EmailUtil;
import org.rebioma.server.util.ManagedSession;
import org.rebioma.server.util.OccurrenceUtil;
import org.rebioma.server.util.StringUtil;

import au.com.bytecode.opencsv.CSVReader;

import com.google.inject.Guice;

/**
 * Default implementation of {@link FileValidationService} that is intended to
 * validate an uploaded CSV file from the client.
 * 
 * File format: Standard CSV where each line contains data delineated by a
 * single character delimeter into a set of tokens.
 * 
 * Things to catch:
 * 
 * <pre>
 * Missing required headers. 
 * Line tokens do not match header tokens. 
 * Line cannot be split on delimeter.
 * Token that contains consecutive delimeter characters not an error.
 * </pre>
 */
public class FileValidationServiceImpl implements FileValidationService {

  // private static class Reviewer {
  // private final String userEmail;
  // private int occurrenceId;
  //
  // public Reviewer(String userEmail, int occurrenceId) {
  // this.userEmail = userEmail;
  // // / this.
  // }
  // }

  public static void main(String args[]) throws IOException {
//    if (args.length < 1) {
//      System.out
//          .println("FileValidationServiceImpl csvFileLocation [delimiter (default ,)] [is cleear old assignments (default false)] ");
//      System.exit(1);
//    }
//    String fileLocation = args[0];
//    char delimiter = ',';
//    boolean isClearOldAssignments = false;
//    if (args.length > 1) {
//      delimiter = args[1].charAt(0);
//    }
//    if (args.length > 2) {
//      isClearOldAssignments = Boolean.parseBoolean(args[2]);
//    }
    
//    /*
	String fileLocation = "D:\\Users\\Travail\\util\\Assignation TRB(Totondrabesa).csv";
	char delimiter = ',';
	boolean isClearOldAssignments = false;
//	*/
    File file = new File(fileLocation);
    try {
      long startTime = System.currentTimeMillis();
      System.out.println(DBFactory.getFileValidationService().processReviewerAssignmentFile(file,
          delimiter, isClearOldAssignments));
      long endTime = System.currentTimeMillis();

      System.out.println("script takes " + ((endTime - startTime) / (1000 * 60)) + " mins");
    } catch (IOException e) {
      // TODO Auto-generated catch block trbdata
      e.printStackTrace();
    }
    // String test = "\"1\",\"1,23,4,,5\",\"7\"";
    // for (String token : test.split("\"[.]*\",")) {
    // System.out.println(token);
    // }
  }

  AscDataDb ascDataDb = DBFactory.getAscDataDb();

  /**
   * The {@link OccurrenceDb} used to save {@link Occurrence} objects to the
   * database. Injected by {@link Guice}.
   */
  OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();

  /**
   * The {@link SessionIdService} used for getting the {@link User} associated
   * with the sessionId in the upload request. Injected by {@link Guice}.
   */

  SessionIdService sessionService = DBFactory.getSessionIdService();

  /**
   * The service used for updating {@link OccurrenceUpdates} on create, insert,
   * and delete.
   */
  UpdateService updateService = DBFactory.getUpdateService();

  /**
   * The {@link ValidationService} used to validate {@link Occurrence} objects
   * before they are saved to the database. Injected by {@link Guice}
   */
  ValidationService validationService = DBFactory.getValidationService();

  TaxonomicReviewerDb taxnomicReviewDb = DBFactory.getTaxonomicReviewerDb();

  RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();

  UserDb userDb = DBFactory.getUserDb();

  private static final String EMAIL = "email";
  private static final Logger log = Logger.getLogger(FileValidationServiceImpl.class);
  private static final String TAXO_FIELD = "field";
  private static final String TAXO_VALUE = "value";
  private static final String IS_MARINE = "ismarine";
  private static final String IS_TERRESTRIAL = "isterrestrial";
  
  public String proccessOccurrenceFile(File file, User loggedinUser, boolean showEmail,
      boolean isPublic, boolean isVettable, boolean clearReview, char delimiter, String userIdsCSV,Traitement traitement) throws IOException {
	  traitement.setTraitement("loading occurrences...", 100*1024, 100*1024);
	  Set<Occurrence> occurrences = CsvUtil.loadOccurrences(file, delimiter);
	  int totalOccurrences = occurrences.size();
	  Map<User, Set<Occurrence>> userOccurrencesMap = new HashMap<User, Set<Occurrence>>();
	  String removeBadIdMessage = occurrenceDb.removeBadId(occurrences, loggedinUser);
	  int newOccurrenceSize = 0;
	  boolean sAdmin = new RoleDbImpl().isSAdmin(loggedinUser.getId());
	  
	  //List<Occurrence> newOccurrences = new ArrayList<Occurrence>();
	  if(!sAdmin)
	  for (Occurrence occurrence : occurrences) {
		  // String createdDate = (new Timestamp(System.currentTimeMillis()))
	      // .toString();
	      // System.out.println(occurrence.getTimeCreated());
	      // System.out.println(Timestamp.parse(occurrence.getTimeCreated()));
	      occurrence.setOwnerEmail(loggedinUser.getEmail());
	      occurrence.setOwner(loggedinUser.getId());
	      occurrence.setEmailVisible(showEmail);
	      occurrence.setPublic_(isPublic);
	      occurrence.setVettable(isVettable);
	      occurrence.setVetted(false);
	      occurrence.setTapirAccessible(false);
	      occurrence.setSharedUsersCSV(userIdsCSV);
	      occurrence.setReviewed(null);
	      if (occurrence.isObfuscated() == null) {
	        occurrence.setObfuscated(false);
	      }
	      OccurrenceUtil.populateScientificName(occurrence, false);
	      // AscDataUtil.setLayerValuesToOccurrence(occurrence);
	      // occurrence.setTimeCreated(createdDate);
	      // occurrence.setLastUpdated(createdDate);
	      if (occurrence.getId() == null) {
	        newOccurrenceSize++;
	        // newOccurrences.add(occurrence);
	      }
	  }
	  List<RecordReview> rcdrv = recordReviewDb.findByProperty();
	  validationService.validate(occurrences, traitement);
	  occurrenceDb.attachDirty(occurrences, traitement, rcdrv, clearReview, sAdmin);
	  traitement.setTraitement("Updating occurrences...", 100*1024, 0);
//    if(traitement.getCancel())return "{\"Uploaded\":\"canceled\"}";
	  updateService.update();
	  traitement.setTraitement("Updating occurrences...", 100*1024, 100*1024);
	  List<TaxonomicReviewer> txrv = taxnomicReviewDb.findByProperty();    
	  try {
		  log.info("sending notification emails");
		  //Session session = HibernateUtil.getCurrentSession();
		  //HibernateUtil.beginTransaction(session);
		  Session session = ManagedSession.createNewSessionAndTransaction();
      int j=0;
      for (Occurrence occurrence : occurrences) {
    	  traitement.setTraitement("emails notification", occurrences.size()*1024, 1024*++j);    
    	  if(traitement.getCancel()){
    		  ManagedSession.rollbackTransaction(session);
    		  //HibernateUtil.rollbackTransaction();
    		  return "{\"Uploaded\":\"canceled2\"}";
    	  }
    	  if (occurrence.isValidated()) {
    		  continue;
    	  }
    	  //Long debut = new Date().getTime();
    	  //List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(occurrence.getId());
    	  List<RecordReview> recordReviews = recordReviewDb.findByProperty(occurrence.getId(),rcdrv);        
    	  //Long fin = new Date().getTime();
        
    	  for (RecordReview recordReview : recordReviews) {
    		  int userId = recordReview.getUserId();
    		  List<TaxonomicReviewer> reviewers = taxnomicReviewDb.findByProperty(userId,txrv);
    		  //List<TaxonomicReviewer> reviewers = tReviewers.get(userId+"");
    		  for (TaxonomicReviewer reviewer : reviewers) {
    			  String taxoField = reviewer.getTaxonomicField();
    			  String taxoValue = reviewer.getTaxonomicValue();
    			  if (isTaxonomicMatch(new AttributeValue(taxoField, taxoValue), occurrence)) {
    				  User u = userDb.findById(userId);
    				  Set<Occurrence> notifyingOccs = userOccurrencesMap.get(u);
    				  if (notifyingOccs == null) {
    					  notifyingOccs = new HashSet<Occurrence>();
    					  userOccurrencesMap.put(u, notifyingOccs);
    				  }
    				  notifyingOccs.add(occurrence);
    			  }
    		  }
    	  }
      }
      log.info("committing transaction");
      ManagedSession.commitTransaction(session);
      //HibernateUtil.commitCurrentTransaction();
      log.info("Commit succes");
    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      log.error("Error: while checking for occurrence to be reviewed", e);
    }
    if (userOccurrencesMap.isEmpty()) {
      log.info("no email was sent because there is no matches");
    }
    for (User u : userOccurrencesMap.keySet()) {
      try {
        EmailUtil.notifyUserAssignmentNewOrChanged(u, userOccurrencesMap.get(u));
        log.error("email send to " + u.getEmail());
      } catch (EmailException e) {
        log.error("can't send email to " + u.getEmail(), e);
      }
    }
    log.info("notification emails sent");
    // occurrenceDb.assignReviewer(userEmail, taxoFieldName, taxoFieldValue)
    int failedUploads = totalOccurrences - occurrences.size();
    int updated = totalOccurrences - newOccurrenceSize - failedUploads;
    return "{\"Uploaded\": " + newOccurrenceSize + ",\"Updated\": " + updated + ",\"Failed\":"
        + failedUploads + ",\"badIdMessage\":" + removeBadIdMessage + "}";
  }

  public String processReviewerAssignmentFile(File file, char delimiter, boolean clearOldAssignment)
      throws IOException {
    CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file),
        Charset.forName("UTF-8")), delimiter);
    String headers[] = csvReader.readNext();
    StringBuilder errorMsgBuilder = new StringBuilder("Error: ");
    StringBuilder inforMsgBuilder = new StringBuilder("Info: ");

    Map<String, Integer> headerColMap = CsvUtil.getHeaderColIndexes(headers);
    String line[] = null;
    Map<String, Integer> occurrenceAssignmentMap = new HashMap<String, Integer>();
    Map<String, Map<String, Set<String>>> userToTaxoAssignMap = new HashMap<String, Map<String, Set<String>>>();
    try {
      // Session session = HibernateUtil.getCurrentSession();
      // boolean isFirst = HibernateUtil.beginTransaction(session);
      // Set<String> removedReviewRecordIds = null;
      if (clearOldAssignment) {
        taxnomicReviewDb.clearExistenceAssignments();
        recordReviewDb.clear();
        occurrenceDb.resetReviews();
      }
      // Set<Occurrence> assignedOccurrences = new HashSet<Occurrence>();
      int lineNumber = 1; // skip header line count
      while ((line = csvReader.readNext()) != null) {
        lineNumber++;
        Integer emailCol = headerColMap.get(EMAIL);
        Integer taxoFieldCol = headerColMap.get(TAXO_FIELD);
        Integer taxoValueCol = headerColMap.get(TAXO_VALUE);
        if (line.length <= emailCol || line.length <= taxoFieldCol || line.length <= taxoValueCol) {
          errorMsgBuilder.append("no record assign for reviewer at line " + lineNumber
              + " because this line is not correctly format.");
          continue;

        }
        String userEmail = line[headerColMap.get(EMAIL)].trim();
        String taxoField = line[headerColMap.get(TAXO_FIELD)].trim();
        String taxoValue = line[headerColMap.get(TAXO_VALUE)].trim();
        boolean isMarine	= line[headerColMap.get(IS_MARINE)].trim().equals("1");
        boolean isTerretrial= line[headerColMap.get(IS_TERRESTRIAL)].trim().equals("1");
        String missingEmail	= userEmail.equals("") ? "email," : "";
        String missingTaxoField = taxoField.equals("") ? TAXO_FIELD + "," : "";
        String missingTaxoValue = taxoValue.equals("") ? TAXO_VALUE : "";
        if (!missingEmail.equals("") || !missingTaxoField.equals("")
            || !missingTaxoValue.equals("")) {
          errorMsgBuilder.append("no record assign for reviewer at line " + lineNumber
              + " because of the following missing fields " + missingEmail + missingTaxoField
              + missingTaxoValue + ".\n");
          continue;
        }
        System.out.println("processing reviewer " + userEmail + " at line " + lineNumber
            + "with assignment: " + taxoField + "=" + taxoValue);
        Integer reviewerOccsCount = occurrenceAssignmentMap.get(userEmail);
        Map<String, Set<String>> taxoFieldValueMap = userToTaxoAssignMap.get(userEmail);
        if (reviewerOccsCount == null) {
          reviewerOccsCount = 0;
          occurrenceAssignmentMap.put(userEmail, reviewerOccsCount);
        }
        if (taxoFieldValueMap == null) {
          taxoFieldValueMap = new HashMap<String, Set<String>>();
          userToTaxoAssignMap.put(userEmail, taxoFieldValueMap);
        }
        Set<String> taxoValues = taxoFieldValueMap.get(taxoField);
        if (taxoValues == null) {
          taxoValues = new HashSet<String>();
          taxoFieldValueMap.put(taxoField, taxoValues);
        }
        taxoValues.add(taxoValue);
        int assignmentCount = occurrenceDb.assignReviewer(userEmail, taxoField, taxoValue, isMarine, isTerretrial);
        if (assignmentCount == 0) {
          errorMsgBuilder.append("no record assign for reviewer at line " + lineNumber
              + " because of assignment is already existed or " + userEmail
              + " user is not yet existed.\n");
        } else {
          reviewerOccsCount += assignmentCount;
          occurrenceAssignmentMap.put(userEmail, reviewerOccsCount);
          // assignedOccurrences.addAll(occs);
          // reviewerOccs.addAll(occs);
          // inforMsgBuilder.append(occs.size() +
          // " records was assigned to user "
          // + userEmail + "\n");
        }
        System.out.println("done assignment to " + userEmail);
      }
      csvReader.close();
      // if (clearOldAssignment) {
      // for (Occurrence occ : assignedOccurrences) {
      // removedReviewRecordIds.remove(occ.getId());
      // }
      // // RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();
      // for (String occId : removedReviewRecordIds) {
      // Integer id = Integer.parseInt(occId);
      // occurrenceDb.checkForReviewedChanged(id);
      // }
      // }
      // if (isFirst) {
      // HibernateUtil.commitCurrentTransaction();
      // }
      sendingNotificationEmail(occurrenceAssignmentMap, inforMsgBuilder, errorMsgBuilder,
          userToTaxoAssignMap);
    } catch (OccurrenceServiceException e) {
      // HibernateUtil.rollbackTransaction();
      e.printStackTrace();
    }
    return inforMsgBuilder.toString() + "\n" + errorMsgBuilder.toString();
  }

  public List<String> validateCsvInputStream(File csvDestination, InputStream csvSource,
      char delimiter, String[] requiredHeaders,Traitement traitement, int lineNbr) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(csvDestination));
    BufferedReader reader = new BufferedReader(new InputStreamReader(csvSource));
    StringBuilder builder;
    traitement.setTraitement("Headear verification", lineNbr*1024, 0);
    String[] tokens;
    boolean isFirst;
    boolean isFirstLine = true;
    List<String> missingHeaders = null;
    long i=0;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
    	builder = new StringBuilder();
    	tokens = line.split(delimiter + "");
    	isFirst = true;    	
    	i++;
    	for (String token : tokens) {
        // if (token.equals("")) {
        // token = "\"\"";
        // }
    	traitement.setTraitement("Headear verification", lineNbr*1024,i*1024); 
    	if(traitement.getCancel())return new ArrayList<String>();
        if (isFirst) {
          token = token.trim();
          if (!token.equals("")) {
            if (token.charAt(0) == 0xFEFF) {
              builder.append(token.substring(1));
            } else {
              builder.append(token);
            }
          }
          isFirst = false;
          continue;
        }
        builder.append(delimiter + token.trim());
      }      
      /**
       * Checks whether the first line contains all the required headers.
       */
      if (isFirstLine) {
        isFirstLine = false;
        // remove all quoted value in the headers to check with requires
        // headers list.
        String headerLine = builder.toString().replaceAll("\"", "");
        missingHeaders = CsvUtil.isHeaderValid(headerLine.split(delimiter + ""), requiredHeaders);
        if (missingHeaders != null) {
          break;
        }
      }
      writer.write(builder.toString() + "\n");
    }
    // this line is important for file writer to save to file system
    // correctly. Never delete these lines
    reader.close();
    writer.close();
    return missingHeaders;
  }

  public List<FileValidationError> validateFile(File csvStoreFile, InputStream csvSource,
      char delimiter, Traitement traitement) {
    BufferedWriter writer;    
    try {
      writer = new BufferedWriter(new FileWriter(csvStoreFile));
      traitement.setTraitement("Validate file", 100*1024, 0);      
      BufferedReader reader = new BufferedReader(new InputStreamReader(csvSource));
      StringBuilder builder;
      String[] tokens;
      boolean isFirst;
      long i = 0;
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        builder = new StringBuilder();
        tokens = line.split(delimiter + "");
        isFirst = true;     
        i++;
        traitement.setTraitement("Validate file", line.length()*1024, 1024*i);    	
        for (String token : tokens) {
        	i++;
        	traitement.setTraitement("Validate file", line.length()*1024, 1024*i);       
        	System.out.println("traitement "+ (1024*(i-1))+" "+line.length()*1024);
          if (token.equals("")) {
            token = "\"\"";
          }
          if (isFirst) {
            token = token.trim();
            if (token.charAt(0) == 0xFEFF) {
              builder.append(token.substring(1));
            } else {
              builder.append(token);
            }
            isFirst = false;
            continue;
          }
          builder.append(delimiter + token.trim());
        }
        writer.write(builder.toString() + "\n");
      }
      // this line is important for file writer to save to file system
      // correctly. Never delete these lines
      reader.close();
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public void zipFiles(File[] files, OutputStream outputStream) throws IOException {
    int buffer = 2048;
    BufferedInputStream sources;
    ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(outputStream));
    byte data[] = new byte[buffer];
    for (File file : files) {
      FileInputStream fi = new FileInputStream(file);
      sources = new BufferedInputStream(fi, buffer);
      ZipEntry entry = new ZipEntry(file.getName());
      zipStream.putNextEntry(entry);
      int count;
      while ((count = sources.read(data, 0, buffer)) != -1) {
        zipStream.write(data, 0, count);
      }
      sources.close();
    }
    zipStream.close();
  }

  private boolean isTaxonomicMatch(AttributeValue attributeValue, Occurrence occurrence) {
    String propertyName = StringUtil.capFirstLetter(attributeValue.getAttribute());
    Class<Occurrence> oClass = Occurrence.class;
    try {
      Method getMethod = oClass.getMethod("get" + propertyName);
      Object obj = getMethod.invoke(occurrence);
      if (obj != null) {
        return obj.toString().equals(attributeValue.getValue());
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
    } catch (IllegalArgumentException e) {
    } catch (IllegalAccessException e) {

    } catch (InvocationTargetException e) {

    }
    return false;
  }

  private void loadTestOccurrence(String occurrenceFileLocation, int totalLoop) throws IOException {
    User user = DBFactory.getUserDb().findByEmail("quang7721@yahoo.com");
    File file = new File(occurrenceFileLocation);
    for (int i = 1; i <= totalLoop; i++) {
      System.out.println("loading batch number " + i);
      proccessOccurrenceFile(file, user, true, true, true, ',', "");
      System.out.println("batch number " + i + " loaded");
    }
  }

  private void sendingNotificationEmail(Map<String, Integer> occurrenceAssignmentMap,
      StringBuilder inforMsgBuilder, StringBuilder errorBuilder,
      Map<String, Map<String, Set<String>>> userToTaxoAssignMap) {
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstSession = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      // UserDb userDb = DBFactory.getUserDb();
      for (String userEmail : occurrenceAssignmentMap.keySet()) {
        Integer assignmentCount = occurrenceAssignmentMap.get(userEmail);
        inforMsgBuilder.append(assignmentCount + " records was assigned to user " + userEmail
            + "\n");
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("email", userEmail));
        User user = (User) criteria.uniqueResult();
        try {
          EmailUtil.notifyUserNewAssignment(user, assignmentCount,
              userToTaxoAssignMap.get(userEmail));
        } catch (EmailException e) {
          e.printStackTrace();
          errorBuilder.append("error while sending email notification to " + userEmail);
        }
      }
      //if (isFirstSession) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      e.printStackTrace();
      errorBuilder.append(e.getMessage());
    }
  }

@Override
public List<String> validateCsvInputStream(File csvDestination, InputStream csvSource,
	      char delimiter, String[] requiredHeaders) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(csvDestination));
	    BufferedReader reader = new BufferedReader(new InputStreamReader(csvSource));
	    StringBuilder builder;	    
	    String[] tokens;
	    boolean isFirst;
	    boolean isFirstLine = true;
	    List<String> missingHeaders = null;
	    long i=0;
	    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
	    	builder = new StringBuilder();
	    	tokens = line.split(delimiter + "");
	    	isFirst = true;    	
	    	for (String token : tokens) {
	        // if (token.equals("")) {
	        // token = "\"\"";
	        // }	    	  
	        if (isFirst) {
	          token = token.trim();
	          if (!token.equals("")) {
	            if (token.charAt(0) == 0xFEFF) {
	              builder.append(token.substring(1));
	            } else {
	              builder.append(token);
	            }
	          }
	          isFirst = false;
	          continue;
	        }
	        builder.append(delimiter + token.trim());
	      }      
	      /**
	       * Checks whether the first line contains all the required headers.
	       */
	      if (isFirstLine) {
	        isFirstLine = false;
	        // remove all quoted value in the headers to check with requires
	        // headers list.
	        String headerLine = builder.toString().replaceAll("\"", "");
	        missingHeaders = CsvUtil.isHeaderValid(headerLine.split(delimiter + ""), requiredHeaders);
	        if (missingHeaders != null) {
	          break;
	        }
	      }
	      writer.write(builder.toString() + "\n");
	    }
	    // this line is important for file writer to save to file system
	    // correctly. Never delete these lines
	    reader.close();
	    writer.close();
	    return missingHeaders;
}

@Override
public List<FileValidationError> validateFile(File csvStoreFile, InputStream csvSource,
	      char delimiter) {
	    BufferedWriter writer;    
	    try {
	      writer = new BufferedWriter(new FileWriter(csvStoreFile));	            
	      BufferedReader reader = new BufferedReader(new InputStreamReader(csvSource));
	      StringBuilder builder;
	      String[] tokens;
	      boolean isFirst;
	      long i = 0;
	      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
	        builder = new StringBuilder();
	        tokens = line.split(delimiter + "");
	        isFirst = true;        
	        for (String token : tokens) {	        	            
	          if (token.equals("")) {
	            token = "\"\"";
	          }
	          if (isFirst) {
	            token = token.trim();
	            if (token.charAt(0) == 0xFEFF) {
	              builder.append(token.substring(1));
	            } else {
	              builder.append(token);
	            }
	            isFirst = false;
	            continue;
	          }
	          builder.append(delimiter + token.trim());
	        }
	        writer.write(builder.toString() + "\n");
	      }
	      // this line is important for file writer to save to file system
	      // correctly. Never delete these lines
	      reader.close();
	      writer.close();
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return null;
}

@Override
public String proccessOccurrenceFile(File file, User loggedinUser, boolean showEmail,
	      boolean isPublic, boolean isVettable, char delimiter, String userIdsCSV) throws IOException {

		  Set<Occurrence> occurrences = CsvUtil.loadOccurrences(file, delimiter);
		  int totalOccurrences = occurrences.size();
		  Map<User, Set<Occurrence>> userOccurrencesMap = new HashMap<User, Set<Occurrence>>();
		  String removeBadIdMessage = occurrenceDb.removeBadId(occurrences, loggedinUser);
		  int newOccurrenceSize = 0;
		  //List<Occurrence> newOccurrences = new ArrayList<Occurrence>();
		  for (Occurrence occurrence : occurrences) {
	      // String createdDate = (new Timestamp(System.currentTimeMillis()))
	      // .toString();
	      // System.out.println(occurrence.getTimeCreated());
	      // System.out.println(Timestamp.parse(occurrence.getTimeCreated()));
	      occurrence.setOwnerEmail(loggedinUser.getEmail());
	      occurrence.setOwner(loggedinUser.getId());
	      occurrence.setEmailVisible(showEmail);
	      occurrence.setPublic_(isPublic);
	      occurrence.setVettable(isVettable);
	      occurrence.setVetted(false);
	      occurrence.setTapirAccessible(false);
	      occurrence.setSharedUsersCSV(userIdsCSV);
	      occurrence.setReviewed(null);
	      if (occurrence.isObfuscated() == null) {
	        occurrence.setObfuscated(false);
	      }
	      OccurrenceUtil.populateScientificName(occurrence, false);
	      // AscDataUtil.setLayerValuesToOccurrence(occurrence);
	      // occurrence.setTimeCreated(createdDate);
	      // occurrence.setLastUpdated(createdDate);
	      if (occurrence.getId() == null) {
	        newOccurrenceSize++;
	        // newOccurrences.add(occurrence);
	      }
	    }
	    validationService.validate(occurrences);
	    occurrenceDb.attachDirty(occurrences);
	    updateService.update();
	    List<RecordReview> rcdrv = recordReviewDb.findByProperty();
	    List<TaxonomicReviewer> txrv = taxnomicReviewDb.findByProperty();    
	    try {
	      log.info("sending notification emails");
	      //Session session = HibernateUtil.getCurrentSession();
	      //HibernateUtil.beginTransaction(session);
	      Session session = ManagedSession.createNewSessionAndTransaction();
	      int i=0;
	      for (Occurrence occurrence : occurrences) {
	        if (occurrence.isValidated()) {
	          continue;
	        }
	        Long debut = new Date().getTime();
	        //if(i<=3)System.out.println("debut for recirdReviews " + debut);
	        //List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(occurrence.getId());
	        List<RecordReview> recordReviews = recordReviewDb.findByProperty(occurrence.getId(),rcdrv);        
	        Long fin = new Date().getTime();
	        
	        for (RecordReview recordReview : recordReviews) {
	          int userId = recordReview.getUserId();
	          List<TaxonomicReviewer> reviewers = taxnomicReviewDb.findByProperty(userId,txrv);
	          //List<TaxonomicReviewer> reviewers = tReviewers.get(userId+"");
	          for (TaxonomicReviewer reviewer : reviewers) {
	            //if(i==0)System.out.println("debut taxonomicRev" + new Date());
	            String taxoField = reviewer.getTaxonomicField();
	            String taxoValue = reviewer.getTaxonomicValue();
	            if (isTaxonomicMatch(new AttributeValue(taxoField, taxoValue), occurrence)) {
	              User u = userDb.findById(userId);
	              Set<Occurrence> notifyingOccs = userOccurrencesMap.get(u);
	              if (notifyingOccs == null) {
	                notifyingOccs = new HashSet<Occurrence>();
	                userOccurrencesMap.put(u, notifyingOccs);
	              }
	              notifyingOccs.add(occurrence);
	            }
	          }
	        }
	      }
	      //HibernateUtil.commitCurrentTransaction();
	      ManagedSession.commitTransaction(session);
	    } catch (Exception e) {
	      //HibernateUtil.rollbackTransaction();
	      log.error("Error: while checking for occurrence to be reviewed", e);
	    }
	    if (userOccurrencesMap.isEmpty()) {
	      log.info("no email was sent because there is no matches");
	    }
	    for (User u : userOccurrencesMap.keySet()) {
	      try {
	        EmailUtil.notifyUserAssignmentNewOrChanged(u, userOccurrencesMap.get(u));
	      } catch (EmailException e) {
	        log.error("can't send email to " + u.getEmail(), e);
	      }
	    }
	    log.info("notification emails sent");
	    // occurrenceDb.assignReviewer(userEmail, taxoFieldName, taxoFieldValue)
	    int failedUploads = totalOccurrences - occurrences.size();
	    int updated = totalOccurrences - newOccurrenceSize - failedUploads;
	    return "{\"Uploaded\": " + newOccurrenceSize + ",\"Updated\": " + updated + ",\"Failed\":"
	        + failedUploads + ",\"badIdMessage\":" + removeBadIdMessage + "}";
}
}
