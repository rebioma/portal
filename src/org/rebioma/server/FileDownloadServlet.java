package org.rebioma.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.MailingServiceImpl;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.services.OccurrenceDbImpl;
import org.rebioma.server.services.RoleDbImpl;
import org.rebioma.server.services.SessionIdService;
import org.rebioma.server.util.QueryToCsv;

import com.google.inject.Guice;

public class FileDownloadServlet extends HttpServlet {

  OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();

  /**
   * The {@link SessionIdService} used for getting the {@link User} associated
   * with the sessionId in the upload request. Injected by {@link Guice}.
   */

  SessionIdService sessionService = DBFactory.getSessionIdService();

  /**
   * Required for constructing the {@link ServletFileUpload} object.
   */
  private final FileItemFactory factory = new DiskFileItemFactory();
  /**
   * Used to access the uploaded file.
   */
  private final ServletFileUpload upload = new ServletFileUpload(factory);

  private final Logger log = Logger.getLogger(OccurrenceDbImpl.class);

  /**
   * Converts all the Occurrences of the request query to csv string and
   * response back to the client as an attachment.
   * 
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
   *      , javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      List<FileItem> items = upload.parseRequest(request);
      OccurrenceQuery query = new OccurrenceQuery(0, 1000);
      String sid = null;
      String delimiter = ","; // default delimiter
      String title = "";
      String firstN = "";
      String lastN = "";
      String activity = "";
      String email = "";
      String institution = "";
      String dataUE = "";
      String userEmail = "";
      for (FileItem item : items) {
        String fieldName = item.getFieldName();
        if (fieldName.equals("query")) {
          String[] filters = item.getString().split(";");
          for (String filter : filters) {
            query.addBaseFilter(filter);
          }
        } else if (fieldName.equals("sessionId")) {
          sid = item.getString();
        } else if (fieldName.equals("delimiter")) {
          String value = item.getString();
          if (!value.trim().equals("")) {
            delimiter = value;
          }
        } else if (fieldName.equals("title")) {
        	title = item.getString();
        } else if (fieldName.equals("firstN")) {
            firstN = item.getString();
        } else if (fieldName.equals("lastN")) {
        	lastN = item.getString();
        } else if (fieldName.equals("activity")) {
            activity = item.getString();
        } else if (fieldName.equals("email")) {
        	email = item.getString();
        } else if (fieldName.equals("institution")) {
        	institution = item.getString();
        } else if (fieldName.equals("dataue")) {
        	dataUE = item.getString();
        } else if (fieldName.equals("useremail")) {
        	userEmail = item.getString();
        } else if (fieldName.equals("extra")) {
          String value = item.getString();
          ResultFilter resultFilter;
          if (value == null || value.equalsIgnoreCase("null")) {
            resultFilter = null;
          } else if (value.equalsIgnoreCase("private")) {
            resultFilter = ResultFilter.PRIVATE;
          } else if (value.equalsIgnoreCase("public")) {
            resultFilter = ResultFilter.PUBLIC;
          } else if (value.equalsIgnoreCase("both")) {
            resultFilter = ResultFilter.BOTH;
          } else {
            resultFilter = ResultFilter.PUBLIC;
          }
          query.setResultFilter(resultFilter);
        }

      }
      log.info("download query: " + query.getBaseFilters());
      log.info("download sid: " + sid);
      log.info("download delimiter: " + delimiter);
      User user = null;
      if (sid != null && !sid.trim().equals("")
          && !sid.equalsIgnoreCase("null")) {
        user = sessionService.getUserBySessionId(sid);
      }
      String rootPath = getServletContext().getRealPath("/");
      File recordReviewedFile = new File(rootPath + "temp",
          "record_reviewed.csv");
      BufferedWriter recordReviewWriter = new BufferedWriter(new FileWriter(
          recordReviewedFile));
      recordReviewWriter
          .write("occurrence id, reviewer name, reviewer email, reviewed status, reviewed date\n");
      List<Occurrence> results = new ArrayList<Occurrence>();
      HashMap ownerMap = new HashMap<Integer, String>();
      boolean sAdmin = user!=null && (new RoleDbImpl().isSAdmin(user.getId()));
      for (int start = query.getStart();;) {
        query.setStart(start);
        List<Occurrence> newResults = occurrenceDb.findByOccurrenceQuery(query,
            user);
        if (newResults.isEmpty()) {
          break;
        } else {
          for (Occurrence occurrence : newResults) {
            results.add(occurrence);
            if(!sAdmin) {
	            if(!occurrence.getOwnerEmail().trim().equalsIgnoreCase(userEmail.trim()))
	            	ownerMap.put(occurrence.getOwner(), occurrence.getOwnerEmail());
            }
            List<OccurrenceReview> occReviews = occurrenceDb
                .getOccurrenceReviewsOf(occurrence.getId());

            for (OccurrenceReview review : occReviews) {
              Boolean reviewed = review.getReviewed();
              String reviewStatus = reviewed == null ? "Waiting"
                  : (reviewed ? "Positive" : "Negative");
              recordReviewWriter.write(review.getOccurrenceId() + ", "
                  + review.getName() + ", " + review.getEmail() + ", "
                  + reviewStatus + ", " + review.getReviewedDate() + "\n");
            }
          }
          start = query.getStart() + query.getLimit();
        }
      }
      recordReviewWriter.close();
      String queryCsv = new String(QueryToCsv.ReturnCsvString(results,
          delimiter).getBytes(), "UTF-8");
      System.out.println(queryCsv);
      // response.setContentType("text/plain");
      // response.setHeader("Content-Disposition",
      // "attachment; filename=occurrences.csv");
      // response.setCharacterEncoding("utf-8");
      response.setContentType("application/zip");
      response.setHeader("Content-Disposition",
          "attachment; filename=occ_download.zip");
      // response.setCharacterEncoding("utf-8");
      File csvFile = new File(rootPath + "temp", "occurrences.csv");
      File citationFile = new File(rootPath + "temp", "citation_DUA.txt");
      File agreement = new File(rootPath + "document",
          "data_usage_agreement.txt");
      log.info("temp csv file " + csvFile.getAbsolutePath() + " created");
      BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFile));
      csvWriter.write(queryCsv);
      csvWriter.close();
      BufferedWriter citationWriter = new BufferedWriter(new FileWriter(
          citationFile));
      BufferedReader agreementReader = new BufferedReader(new FileReader(
          agreement));
      citationWriter
          .write("Occurrence data downloaded from REBIOMA at http://data.rebioma.net on "
              + (new Date()) + ".");
      citationWriter
          .write("Data provided by institutions and data providers listed in the occurrence fields named \"InstitutionCode\", Owner\" and \"OwnerEmail\" where provided for a given record. Data reviewed by reviewers listed in \"record_reviewed.csv\" file where applicable.\r\n\r\n");
      String line;
      while ((line = agreementReader.readLine()) != null) {
        citationWriter.write(line + "\r\n");
      }
      citationWriter.close();
      agreementReader.close();
      MailingServiceImpl.sendDownloadMail(ownerMap, title, firstN, lastN, activity, email, institution, dataUE);
      DBFactory.getFileValidationService().zipFiles(
          new File[] { csvFile, recordReviewedFile, citationFile },
          response.getOutputStream());
      csvFile.delete();
      recordReviewedFile.delete();
      citationFile.delete();
      // response.getOutputStream().close();
      // response.getWriter().append(queryCsv);
    } catch (FileUploadException e) {
      log.error(e.getMessage(), e);
      response.setContentType("text/html");
      response.setCharacterEncoding("utf-8");
      response.sendError(500, e.getMessage().replaceAll("\n", "<br>") + "<br>"
          + getStackTrace(e));
      e.printStackTrace();
    } catch (Exception e) {
      log.error("unexpected error", e);
      response.setContentType("text/html");
      response.setCharacterEncoding("utf-8");
      response.sendError(500, e.getMessage().replaceAll("\n", "<br>") + "<br>"
          + getStackTrace(e));
      e.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

  private String getStackTrace(Exception e) {
    String stackTrace = "";
    for (StackTraceElement element : e.getStackTrace()) {
      stackTrace += element.toString() + "<br>";
    }
    return stackTrace;
  }
}
