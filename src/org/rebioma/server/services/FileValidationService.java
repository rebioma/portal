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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.server.upload.Traitement;

/**
 * A service interface for validating a CSV file.
 * 
 */
// @ImplementedBy(FileValidationServiceImpl.class)
public interface FileValidationService {

  /**
   * Error types can happen in a csv file;
   * 
   */
  public enum ErrorType {
    /**
     * Line cannot be split on delimiter.
     */
    DELIMITER,
    /**
     * Line tokens do not match header tokens.
     */
    HEADER_MISSMATCH,
    /**
     * Error where there is an close quote without open quote
     */
    CLOSE_QUOTE
  }

  /**
   * Each error in the CSV file is represented by a {@link FileValidationError}.
   */
  public interface FileValidationError {
    /**
     * @return the error message
     */
    public ErrorType getErrorMsg();

    /**
     * @return the line number in the file
     */
    public int getLineNumber();
  }

  /**
   * Processes the uploaded {@link File} by loading the {@link Occurrence}
   * objects from it, setting the required fields, validating it, and saving
   * them to the database. If the sessionId is invalid, this method fails.
   * 
   * For correct behavior of this service, call
   * {@link #validateCsvInputStream(File, InputStream, char, String[])} first
   * and make sure there is no missing required header before call this method.
   * 
   * @param file the uploaded file
   * @param user the upload request user
   * @param showEmail if you wish to have records stamped with the user's email
   * @param isPublic if the records are public
   * @param isVettable if the records are vettable
   * @param delimeter TODO
   * @param userIdsCSV TODO
   * @return JSON status message in the following format: {"Updated" : 2,
   *         "Uploaded" : 3, "Fail" : 4}
   * @throws IOException if loading CSV data encounters a problem
   */
  public String proccessOccurrenceFile(File file, User user, boolean showEmail,
	      boolean isPublic, boolean isVettable, char delimeter, String userIdsCSV)
	      throws IOException;

  public String proccessOccurrenceFile(File file, User user, boolean showEmail,
	      boolean isPublic, boolean isVettable, boolean clearReview, char delimeter, String userIdsCSV,Traitement traitement)
	      throws IOException;

  public String processReviewerAssignmentFile(File file, char delimeter,
      boolean clearOldAssignment) throws IOException;

  /**
   * Validates a input stream which contains an csv content of some data format
   * (i.e Occurrence). It returns a list of missing required headers from the
   * input stream, and null otherwise. Furthermore, it save the content of the
   * input stream into a file if it find no error.
   * 
   * @param csvDestination a file to be saved the content of the source csv if
   *          there is no error.
   * @param csvSource a csv source which content the data information.
   * @param delimiter a single delimiter that uses in this file.
   * @param requiredHeaders list of required headers.
   * @return List of missing required headers, null if there is no missing
   *         header.
   */
  public List<String> validateCsvInputStream(File csvDestination,
	      InputStream csvSource, char delimiter, String[] requiredHeaders)
	      throws IOException;

  public List<String> validateCsvInputStream(File csvDestination,
	      InputStream csvSource, char delimiter, String[] requiredHeaders,Traitement traitement,int lineNbr)
	      throws IOException;

  /**
   * Validates a file with data delimited by a delimeter and returns a list of
   * validation errors or null if no errors were found.
   * 
   * @param csvStoreFile the csv file that store the correct csv format
   * @param csvSource an an Input stream contain the csv source to be validated.
   * @param delimiter the file delimiter character
   * @return list of validation errors or null if no errors were found
   */
  public List<FileValidationError> validateFile(File csvStoreFile,
	      InputStream csvSource, char delimiter);

  public List<FileValidationError> validateFile(File csvStoreFile,
	      InputStream csvSource, char delimiter,Traitement traitement);

  public void zipFiles(File files[], OutputStream outputStream) throws IOException;
}
