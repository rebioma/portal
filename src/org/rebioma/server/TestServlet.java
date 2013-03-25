package org.rebioma.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.services.OccurrenceDbImpl;
import org.rebioma.server.services.SessionIdService;

import com.google.inject.Guice;

public class TestServlet extends HttpServlet {

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
      // List<FileItem> items = upload.parseRequest(request);
      // OccurrenceQuery query = new OccurrenceQuery(0, 100);
      // String sid = null;
      // String delimiter = ","; // default delimiter
      // for (FileItem item : items) {
      // System.out.println(item.getName() + "=" + item.getString());
      // // String fieldName = item.getFieldName();
      // // if (fieldName.equals("query")) {
      // // String[] filters = item.getString().split(",");
      // // for (String filter : filters) {
      // // query.addBaseFilter(filter);
      // // }
      // // } else if (fieldName.equals("sessionId")) {
      // // sid = item.getString();
      // // } else if (fieldName.equals("delimiter")) {
      // // String value = item.getString();
      // // if (!value.trim().equals("")) {
      // // delimiter = value;
      // // }
      // // } else if (fieldName.equals("extra")) {
      // // String value = item.getString();
      // // ResultFilter resultFilter;
      // // if (value == null || value.equalsIgnoreCase("null")) {
      // // resultFilter = null;
      // // } else if (value.equalsIgnoreCase("private")) {
      // // resultFilter = ResultFilter.PRIVATE;
      // // } else if (value.equalsIgnoreCase("public")) {
      // // resultFilter = ResultFilter.PUBLIC;
      // // } else if (value.equalsIgnoreCase("both")) {
      // // resultFilter = ResultFilter.BOTH;
      // // } else {
      // // resultFilter = ResultFilter.PUBLIC;
      // // }
      // // query.setResultFilter(resultFilter);
      // // }
      //
      // }
      // log.info("download query: " + query.getBaseFilters());
      // log.info("download sid: " + sid);
      // log.info("download delimiter: " + delimiter);
      // User user = null;
      // if (sid != null && !sid.trim().equals("")
      // && !sid.equalsIgnoreCase("null")) {
      // user = sessionService.getUserBySessionId(sid);
      // }
      // List<Occurrence> results = new ArrayList<Occurrence>();
      // for (int start = query.getStart();;) {
      // query.setStart(start);
      // List<Occurrence> newResults = occurrenceDb.findByOccurrenceQuery(query,
      // user);
      // if (newResults.isEmpty()) {
      // break;
      // } else {
      // results.addAll(newResults);
      // start = query.getStart() + query.getLimit();
      // }
      // }
      // String queryCsv = new String(QueryToCsv.ReturnCsvString(results,
      // delimiter).getBytes(), "UTF-8");
      // System.out.println(queryCsv);
      // // response.setContentType("text/plain");
      // // response.setHeader("Content-Disposition",
      // // "attachment; filename=occurrences.csv");
      // // response.setCharacterEncoding("utf-8");
      // response.setContentType("application/octet-stream");
      // // response.setHeader("Content-Disposition",
      // // "attachment; filename=occurrences.zip");
      // // response.setCharacterEncoding("utf-8");
      // File csvFile = File.createTempFile("occurreces", ".csv");
      // BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFile));
      // csvWriter.write(queryCsv);
      // DBFactory.getFileValidationService().zipFiles(new File[] { csvFile },
      // "occ_download.zip", response.getOutputStream());
      // csvFile.delete();
      // response.getOutputStream().close();
      // response.getWriter().append(queryCsv);
      response.setContentType("application/zip");
      response
          .setHeader("Content-Disposition", "attachment; filename=test.zip");
      response.setCharacterEncoding("utf-8");
      // response.setContentType("text/plain");
      // System.out.println("running test servlet");
      File file = new File("test.txt");
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write("testing");
      writer.close();
      OutputStream out = response.getOutputStream();
      // writer = new BufferedWriter(new OutputStreamWriter(out));
      // writer.write("testing");
      // writer.close();
      DBFactory.getFileValidationService().zipFiles(new File[] { file },
          out);
      // System.out.println("done running test servlet");
      // } catch (FileUploadException e) {
      // log.error(e.getMessage(), e);
      // response.setContentType("text/html");
      // response.setCharacterEncoding("utf-8");
      // response.sendError(500, e.getMessage().replaceAll("\n", "<br>") +
      // "<br>"
      // + getStackTrace(e));
      // e.printStackTrace();
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
