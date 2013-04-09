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
package org.rebioma.server;

import static gwtupload.shared.UConsts.TAG_CANCELED;
import static gwtupload.shared.UConsts.TAG_ERROR;
import static gwtupload.shared.UConsts.TAG_FINISHED;
import gwtupload.server.AbstractUploadListener;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import gwtupload.server.exceptions.UploadCanceledException;
import gwtupload.shared.UConsts;

import javax.activation.URLDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.FileValidationService;
import org.rebioma.server.services.SessionIdService;
import org.rebioma.server.services.UserDb;
import org.rebioma.server.upload.Traitement;
import org.rebioma.server.upload.TraitementAdapter;
import org.rebioma.server.upload.TraitementListener;

import BCrypt.BCrypt;

/**
 * The FileUploadServlet suppports file uploading from the client.
 * 
 */
public class FileUploadServlet extends UploadAction {

  public static void main(String args[]) {
    try {
      URLDataSource csvSource = new URLDataSource(
          new URL(
              "https://sites.google.com/site/rebiomahelp/home/english#datause"));
      BufferedReader stream = new BufferedReader(new InputStreamReader(
          csvSource.getInputStream()));
      String line;
      while ((line = stream.readLine()) != null) {
        System.out.println(line);
      }
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Required for constructing the {@link ServletFileUpload} object.
   */
  private final FileItemFactory factory = new DiskFileItemFactory();

  /**
   * Used to access the uploaded file.
   */
  private final ServletFileUpload upload = new ServletFileUpload(factory);
  /**
   * Delimiter that is fed into CSVReader
   */
  private final String DEFAULT_DELIMITER = ",";

  // /**
  // * The {@link OccurrenceDb} used to save {@link Occurrence} objects to the
  // * database. Injected by {@link Guice}.
  // */
  // @Inject
  // OccurrenceDb occurrenceService;
  //
  // /**
  // * The {@link ValidationService} used to validate {@link Occurrence} objects
  // * before they are saved to the database. Injected by {@link Guice}
  // */
  // @Inject
  // ValidationService validationService;
  //
  // /**
  // * The {@link SessionIdService} used for getting the {@link User} associated
  // * with the sessionId in the upload request. Injected by {@link Guice}.
  // */
  // @Inject
  // SessionIdService sessionService;
  //
  // /**
  // * The service used for updating {@link OccurrenceUpdates} on create,
  // insert,
  // * and delete.
  // */
  // @Inject
  // UpdateService updateService;

  private String delimiter = DEFAULT_DELIMITER; // default delimiter

  private Traitement traitement = new Traitement("Uploading file...", 100*1024,0);
  
  FileValidationService fileValidation = DBFactory.getFileValidationService();

  UserDb userDb = DBFactory.getUserDb();

  SessionIdService sessionService = DBFactory.getSessionIdService();

  /**
   * Extracts upload form values and saves the uploaded file to a temporary file
   * and throws an exception if problems are found.
   * 
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
   *      , javax.servlet.http.HttpServletResponse)
   */
  	@Override
	protected Map<String, String> getUploadStatus(HttpServletRequest request, String fieldname, Map<String, String> ret) {

		    perThreadRequest.set(request);

		    HttpSession session = request.getSession();

		    if (ret == null) {
		      ret = new HashMap<String, String>();
		    }
		    
		    long currentBytes = 0;
		    long totalBytes = 0;
		    long percent = 0;
		    String tache = "Uploading file";
		    AbstractUploadListener listener = getCurrentListener(request);
		    TraitementListener tListener = traitement.getTraitementListener();
		    if (listener != null) {
		      if (listener.getException() != null) {
		        if (listener.getException() instanceof UploadCanceledException) {
		          ret.put(TAG_CANCELED, "true");
		          ret.put(TAG_FINISHED, TAG_CANCELED);
		          logger.error("UPLOAD-SERVLET (" + session.getId() + ") getUploadStatus: " + fieldname + " canceled by the user after " + listener.getBytesRead() + " Bytes");
		        } else {
		          String errorMsg = getMessage("server_error", listener.getException().getMessage());
		          ret.put(TAG_ERROR, errorMsg);
		          ret.put(TAG_FINISHED, TAG_ERROR);
		          logger.error("UPLOAD-SERVLET (" + session.getId() + ") getUploadStatus: " + fieldname + " finished with error: " + listener.getException().getMessage());
		        }
		      } else {
		        currentBytes = listener.getBytesRead();
		        totalBytes = listener.getContentLength();
		        percent = totalBytes != 0 ? currentBytes * 100 / totalBytes : 0;
		        if(tListener!=null)
		        	tache = tListener.getTraitement();
		        logger.debug("UPLOAD-SERVLET (" + session.getId() + ") getUploadStatus: " + fieldname + " " + currentBytes + "/" + totalBytes + " " + percent + "%");
		        if(tListener!=null&&!tListener.getTraitement().startsWith("Upl")){
		        	currentBytes = tListener.getDone();
			        totalBytes = tListener.getTotal();
			        percent = totalBytes != 0 ? currentBytes * 100 / totalBytes : 0;			        		
		        }
		        ret.put("percent", "" + percent);
		        ret.put("traitement", tache);
		        ret.put("currentBytes", "" + currentBytes);
		        ret.put("totalBytes", "" + totalBytes);
		        if (listener.isFinished()) {
			        ret.put(TAG_FINISHED, "ok");
		        }
		      }
		    } else if (getSessionFileItems(request) != null) {
		      if (fieldname == null) {
		        ret.put(TAG_FINISHED, "Ok");
		        logger.debug("UPLOAD-SERVLET (" + session.getId() + ") getUploadStatus: " + request.getQueryString() + " finished with files: " + session.getAttribute(SESSION_FILES));
		      } else {
		        Vector<FileItem> sessionFiles = (Vector<FileItem>) getSessionFileItems(request);
		        for (FileItem file : sessionFiles) {
		          if (file.isFormField() == false && file.getFieldName().equals(fieldname)) {
		            ret.put(TAG_FINISHED, "ok");
		            ret.put(UConsts.PARAM_FILENAME, fieldname);
		            logger.debug("UPLOAD-SERVLET (" + session.getId() + ") getUploadStatus: " + fieldname + " finished with files: " + session.getAttribute(SESSION_FILES));
		          }
		        }
		      }
		    } else {
		      logger.debug("UPLOAD-SERVLET (" + session.getId() + ") getUploadStatus: no listener in session");
		      ret.put("wait", "listener is null");
		    }
		    if (ret.containsKey(TAG_FINISHED)) {
		      removeCurrentListener(request);
		      traitement.removeListeners(listenerAdapteur);
		    }
		    perThreadRequest.set(null);
		    return ret;
		  }
  private TraitementAdapter listenerAdapteur = new TraitementAdapter(){};;
  @Override
  public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles)throws UploadActionException {

    FileItem uploadItem = null;
    File file = null;
    String fieldName, val, sessionId = null;
    String errorMsg = null;
    String successMsg = null;
    String csvUrl = null;
    String username = null;
    String password = null;
    traitement = new Traitement("Uploading file...", 100*1024,0);
    traitement.addTraitementListener(listenerAdapteur);
    try {
      boolean isPublic = true, isVettable = false, showEmail = false;
      String collaboratorsCSV = "";
      for (FileItem item : sessionFiles) {
        fieldName = item.getFieldName();
        if (item.isFormField()) {
          val = item.getString();
          if (fieldName.equals("sessionId")) {
            sessionId = val;
          } else if (fieldName.equals("show_email")) {
            showEmail = val.equalsIgnoreCase("on") ? true : false;
          } else if (fieldName.equals("private_vetter")) {
            isPublic = val.equalsIgnoreCase("on") ? false : true;
          } else if (fieldName.equals("public_vetter")) {
            isPublic = val.equalsIgnoreCase("on") ? true : false;
          } else if (fieldName.equals("modeling")) {
            isVettable = val.equalsIgnoreCase("on") ? true : false;
          } else if (fieldName.equals("delimiter")) {
            delimiter = val;
          } else if (fieldName.equals("collaborators")) {
            collaboratorsCSV = val;
          } else if (fieldName.equals("csvUrl")) {
            csvUrl = val;
          } else if (fieldName.equals("username")) {
            username = val;
          } else if (fieldName.equals("password")) {
            password = val;
          }
        } else if (fieldName.startsWith("GWTU-")) {
          uploadItem = item;
        }
      }
      InputStream uploadInputStream = null;
      String fileName = null;
      User user = null;
      if (sessionId != null && !sessionId.trim().equals("")) {
        user = sessionService.getUserBySessionId(sessionId);
        if (user == null) {
          errorMsg = "{\"onFailure\": {\"Invalid\" : \"sessionId\"}}";
        }
      } else if (username != null && password != null) {
        user = userDb.findByEmail(username);
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
          errorMsg = "{\"onFailure\": {\"Invalid\" : \"wrong username or password\"}}";
          user = null;
        }
      }
      if (csvUrl != null) {
        try {
          uploadInputStream = getUrlInputStream(csvUrl);
          fileName = "occurrence";
        } catch (IOException io) {
          errorMsg = "{\"onFailure\": {\"No File\" : \"" + io.getMessage()
              + "\"}}";
        }
      } else {
        if (uploadItem == null) {
          errorMsg = "{\"onFailure\": {\"No File\" : \"\"}}";
        } else if (uploadItem.getSize() == 0) {
          errorMsg = "{\"onFailure\": {\"Invalid file\": \"\"}}";
        } else {
          fileName = uploadItem.getName();
          uploadInputStream = uploadItem.getInputStream();
        }
      }
      if (errorMsg == null) {
        // Handles a windows server:
        if (fileName.contains("\\")) {
          int endIndex = fileName.lastIndexOf(".");
          int beginIndex = fileName.lastIndexOf("\\") + 1;
          if (beginIndex >= endIndex) {
            endIndex = fileName.length();
          }
          fileName = fileName.substring(beginIndex, endIndex);
        }
        // File.createTempFile requires a fileName at least 3 characters long:
        if (fileName.length() < 3) {
          fileName += "xx";
        }
        file = /*new File("f:/"+fileName);//*/File.createTempFile(fileName, ".csv");
        logger.info(file.getAbsolutePath());
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(uploadInputStream));
        int lineNbr = 0;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        	writer.write(line + "\n");lineNbr++;
        }
        writer.close();
        File _file = /*new File("f:/_"+fileName);//*/File.createTempFile("_"+fileName, ".csv");
        logger.info(_file.getAbsolutePath());
        List<String> missingHeaders = fileValidation.validateCsvInputStream(
            _file, new FileInputStream(file), delimiter.charAt(0), null,traitement,lineNbr);
        if (missingHeaders != null) {
          StringBuilder sb = new StringBuilder("[");
          for (String header : missingHeaders) {
            sb.append("\"" + header + "\",");
          }
          sb.replace(sb.length() - 1, sb.length(), "]");
          errorMsg = "{\"onFailure\": {\"Missing Required Headers\": " + sb
              + "}}";
          //fixing issue 394
          //response.getWriter().write(errorMsg);
        }else{
        	String out = fileValidation.proccessOccurrenceFile(file, user,
                    showEmail, isPublic, isVettable, delimiter.charAt(0),
                    collaboratorsCSV,traitement);
        	successMsg = "{\"onSuccess\": " + out + "}";
            //response.getWriter().write("{\"onSuccess\": " + out + "}");
        }
        //end issue 394
      } else {
        //response.getWriter().write(errorMsg);
      }
      delimiter = DEFAULT_DELIMITER; // restore delimiter to default value.

    } catch (Exception e) {
      e.printStackTrace();
      errorMsg = "{\"onFailure\": {\"Upload failed\": \""
          + e.getLocalizedMessage() + "\"}}";
      //response.getWriter().write(errorMsg);
    }
    traitement.removeListeners(listenerAdapteur);
    return successMsg!=null?successMsg:errorMsg;
  }

  private InputStream getUrlInputStream(String url) throws IOException {
    URLDataSource csvSource = new URLDataSource(new URL(url));
    return csvSource.getInputStream();

  }

  // /**
  // * Processes the uploaded {@link File} by loading the {@link Occurrence}
  // * objects from it, setting the required fields, validating it, and saving
  // * them to the database. If the sessionId is invalid, this method fails.
  // *
  // * @param file the uploaded file
  // * @param sessionId the upload request session id
  // * @param isPublic if the records are public
  // * @param isVettable if the records are vettable
  // * @return status message
  // * @throws IOException if loading CSV data encounters a problem
  // */
  // public String proccessFile(File file, String sessionId, boolean isPublic,
  // boolean isVettable) throws IOException {
  // User user = sessionService.getUserBySessionId(sessionId);
  // if (user == null) {
  // return "Upload failed: Invalid session ID";
  // }
  // Set<Occurrence> occurrences = CsvUtil.loadOccurrences(file, delimiter
  // .charAt(0));
  // int totalOccurrences = occurrences.size();
  // int newOccurrenceSize = 0;
  // for (Occurrence occurrence : occurrences) {
  // String createdDate = (new Timestamp(System.currentTimeMillis()))
  // .toString();
  // occurrence.setOwner(user.getId());
  // occurrence.setOwnerEmail(user.getEmail());
  // occurrence.setPublic_(isPublic);
  // occurrence.setVettable(isVettable);
  // occurrence.setVetted(false);
  // occurrence.setTapirAccessible(false);
  // occurrence.setOwnerEmail(user.getEmail());
  // occurrence.setTimeCreated(createdDate);
  // occurrence.setLastUpdated(createdDate);
  //
  // if (occurrence.getId() == null) {
  // newOccurrenceSize++;
  // }
  // }
  // occurrenceService.removeBadId(occurrences);
  // validationService.validate(occurrences);
  // occurrenceService.attachDirty(occurrences);
  // updateService.update();
  // int failedUploads = totalOccurrences - occurrences.size();
  // int updated = (totalOccurrences - newOccurrenceSize - failedUploads);
  // return "{\"Uploaded\": " + newOccurrenceSize + ",\"Updated\": " + updated
  // + ",\"Failed\":" + failedUploads + "}";
  // }
  //
  // /**
  // * Checks whether the input stream contain a valid requires header. At the
  // * same time, trims all spaces between fields.
  // *
  // * @param csvDestination a file will be write to
  // * @param csvSource an input stream contains the csv content
  // * @param delimiter the delimiter that use in this file
  // * @return List of missing required headers.
  // * @throws IOException
  // */
  // public List<String> validateInputStream(File csvDestination,
  // InputStream csvSource, char delimiter) throws IOException {
  // BufferedWriter writer = new BufferedWriter(new FileWriter(csvDestination));
  // BufferedReader reader = new BufferedReader(new
  // InputStreamReader(csvSource));
  // StringBuilder builder;
  // String[] tokens;
  // boolean isFirst;
  // boolean isFirstLine = true;
  // List<String> missingHeaders = null;
  // for (String line = reader.readLine(); line != null; line = reader
  // .readLine()) {
  // builder = new StringBuilder();
  // tokens = line.split(delimiter + "");
  // isFirst = true;
  // for (String token : tokens) {
  // // if (token.equals("")) {
  // // token = "\"\"";
  // // }
  // if (isFirst) {
  // token = token.trim();
  // if (token.charAt(0) == 0xFEFF) {
  // builder.append(token.substring(1));
  // } else {
  // builder.append(token);
  // }
  // isFirst = false;
  // continue;
  // }
  // builder.append(delimiter + token.trim());
  // }
  // /**
  // * Checks whether the first line contains all the required headers.
  // */
  // if (isFirstLine) {
  // isFirstLine = false;
  // // remove all quoted value in the headers to check with requires
  // // headers list.
  // String headerLine = builder.toString().replaceAll("\"", "");
  // missingHeaders = CsvUtil
  // .isHeaderValid(headerLine.split(delimiter + ""), null);
  // if (missingHeaders != null) {
  // break;
  // }
  // }
  // writer.write(builder.toString() + "\n");
  // }
  // // this line is important for file writer to save to file system
  // // correctly. Never delete these lines
  // reader.close();
  // writer.close();
  // return missingHeaders;
  // }

}
