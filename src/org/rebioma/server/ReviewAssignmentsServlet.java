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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.activation.URLDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.FileValidationService;
import org.rebioma.server.services.SessionIdService;
import org.rebioma.server.services.UserDb;

/**
 * The FileUploadServlet suppports file uploading from the client.
 * 
 */
public class ReviewAssignmentsServlet extends HttpServlet {

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
  private String delimiter = DEFAULT_DELIMITER; // default delimiter

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
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    System.out.println("request locale" + request.getLocale());
    System.out.println("serverInfo ");
    FileItem uploadItem = null;
    String fieldName, val, sessionId = null;
    List<FileItem> items = null;
    try {
      items = upload.parseRequest(request);
      boolean isClearOldAssignments = false;
      for (FileItem item : items) {
        fieldName = item.getFieldName();
        if (item.isFormField()) {
          val = item.getString();
          if (fieldName.equals("sessionId")) {
            sessionId = val;
          } else if (fieldName.equals("clear")) {
            isClearOldAssignments = val.equalsIgnoreCase("on") ? true : false;
          } else if (fieldName.equals("delimiter")) {
            delimiter = val;
          }
        } else if (fieldName.equals("file_upload")) {
          uploadItem = item;

        }
      }
      InputStream uploadInputStream = null;
      String fileName = null;
      User user = null;
      if (sessionId != null && !sessionId.trim().equals("")) {
        user = sessionService.getUserBySessionId(sessionId);
        if (user == null) {
          response.sendError(HttpServletResponse.SC_FORBIDDEN,
              "invalid or bad session");
        } else if (uploadItem == null) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "no file was selected");
        } else if (uploadItem.getSize() == 0) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "uploaded file can't be empty");
        } else {
          fileName = uploadItem.getName();
          if (fileName.contains("\\")) {
            int endIndex = fileName.lastIndexOf(".");
            int beginIndex = fileName.lastIndexOf("\\") + 1;
            if (beginIndex >= endIndex) {
              endIndex = fileName.length();
            }
            fileName = fileName.substring(beginIndex, endIndex);
          }
          // File.createTempFile requires a fileName at least 3 characters
          // long:
          if (fileName.length() < 3) {
            fileName += "xx";
          }
          File reviewerFile = new File("reviewLookupCsvs");
          if (!reviewerFile.exists()) {
            reviewerFile.mkdir();
          }
          reviewerFile = new File(reviewerFile.getName() + "/" + fileName);
          uploadInputStream = uploadItem.getInputStream();
          fileValidation.validateCsvInputStream(reviewerFile,
              uploadInputStream, delimiter.charAt(0), new String[] { "email",
                  "DarwinCore Taxonomic Field", "Taxon" });
          response.getWriter().write(
              fileValidation.processReviewerAssignmentFile(reviewerFile,
                  delimiter.charAt(0), isClearOldAssignments));
        }
      } else {
        response.sendError(HttpServletResponse.SC_FORBIDDEN,
            "invalid or bad session");
      }
      delimiter = DEFAULT_DELIMITER; // restore delimiter to default value.
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_FORBIDDEN,
          "invalid or bad session");
    }
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
