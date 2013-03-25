package org.rebioma.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.rebioma.client.Occurrence;
import org.rebioma.server.services.FileValidationService;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.services.SessionIdService;
import org.rebioma.server.util.CsvUtil;

import com.google.inject.Inject;
import com.google.inject.testing.guiceberry.GuiceBerryEnv;
import com.google.inject.testing.guiceberry.NoOpTestScopeListener;
import com.google.inject.testing.guiceberry.TestScopeListener;
import com.google.inject.testing.guiceberry.junit3.GuiceBerryJunit3Env;
import com.google.inject.testing.guiceberry.junit3.GuiceBerryJunit3TestCase;

/**
 * These tests are reading file that specified in fileUploadTest.properties
 * file. Read fileUploadTest.properties.template for more info. Before run this
 * test rename fileUploadTest.properties.template to fileUploadTest.properties
 * and set all necessary values there.
 * 
 */
@GuiceBerryEnv("org.rebioma.server.FileUploadServletTest$FileUploadEnv")
public class FileUploadServletTest extends GuiceBerryJunit3TestCase {
  public static final class FileUploadEnv extends GuiceBerryJunit3Env {
    @Override
    protected Class<? extends TestScopeListener> getTestScopeListener() {
      return NoOpTestScopeListener.class;
    }
  }

  private static final Properties parameterProps = new Properties();
  private static final String PROPERTY_FILE = "test\\fileUploadTest.properties";
  private static final String DEFAULT_DELIMETER = ",";
  static {
    try {
      parameterProps.load(new FileInputStream(new File(PROPERTY_FILE)));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  final String sid = "1ohlwkxfmzwaxwmnklgu";

  @Inject
  FileUploadServlet fileUpSrvlt;

  @Inject
  SessionIdService sidService;

  @Inject
  OccurrenceDb occService;
  @Inject
  FileValidationService fileValidation;

  public void testColumnCountMismatch() {
    String columnMismatchFileNames[] = getPropertyValues("ColumnMismatchFiles",
            ",");
    String delimiters[] = getPropertyValues("MissingHeadersDelimiter", "-");
    delimiters = contrainDelimitersArray(delimiters,
            columnMismatchFileNames.length);
    for (int i = 0; i < columnMismatchFileNames.length; i++) {
      String fileName = columnMismatchFileNames[i];
      if (fileName.trim().equals("")) {
        continue;
      }
      File occFile = new File(fileName);

      // calculate column counts for each row
      BufferedReader reader = null;
      List<Integer> columnCounts = new ArrayList<Integer>();
      String row;
      try {
        reader = new BufferedReader(new FileReader(fileName));
        while ((row = reader.readLine()) != null) {
          columnCounts.add(row.split(delimiters[i]).length);
        }
      } catch (FileNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      // TODO: Somehow, compare the occurrence set to the columnMismatchFile
      Set<Occurrence> occSet = null;
      try {
        occSet = CsvUtil.loadOccurrences(occFile, delimiters[i].charAt(0));
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

    }
  }

  public void testContainRequiredHeaderOnly() {
    Properties prop = parameterProps;
    String fileNames[] = getPropertyValues("RequiredHeaderOnlyFiles", ",");
    String delimiters[] = getPropertyValues("RequriedHeaderOnlyDelimeters", "-");
    if (fileNames == null) {
      return;
    }
    delimiters = contrainDelimitersArray(delimiters, fileNames.length);
    for (int i = 0; i < fileNames.length; i++) {
      try {
        File file = new File(fileNames[i] + "_csv_result.csv");
        InputStream inputStream = new FileInputStream(new File(fileNames[i]));
        List<String> missingHeader = fileValidation.validateCsvInputStream(
                file, inputStream, delimiters[i].charAt(0), null);
        assertTrue(missingHeader == null);
        // String statusMessage = fileValidation.proccessFile(file, sid, true,
        // true, delimiters[i].charAt(0));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void testMissingRequiredHeader() {
    String occurrenceFileNameArray[] = getPropertyValues("MissingHeadersFile",
            ",");
    String expectedMissingHeadersArray[] = getPropertyValues("MissingHeaders",
            ";");
    String delimiters[] = getPropertyValues("MissingHeadersDelimiter", "-");
    if (occurrenceFileNameArray == null) {
      return;
    }
    delimiters = contrainDelimitersArray(delimiters,
            occurrenceFileNameArray.length);
    assertTrue(occurrenceFileNameArray.length == expectedMissingHeadersArray.length);

    FileInputStream occInput = null;
    File csvDest = null;
    List<String> returnedMissingHeaderList = null;
    for (int i = 0; i < occurrenceFileNameArray.length; i++) {
      char delimiter = delimiters[i].charAt(0);
      String occurrenceFileName = occurrenceFileNameArray[i];
      String expectedMissingHeaders = expectedMissingHeadersArray[i];
      try {
        occInput = new FileInputStream(occurrenceFileName);
        csvDest = File.createTempFile("temp", ".csv");
        returnedMissingHeaderList = fileValidation.validateCsvInputStream(
                csvDest, occInput, delimiter, null);
        int headerIndex = 0;
        String expectedHeaders[] = expectedMissingHeaders.split(",");
        assertTrue(expectedHeaders.length == returnedMissingHeaderList.size());
        for (String missingHeader : returnedMissingHeaderList) {
          assertEquals(missingHeader.trim().toLowerCase(),
                  expectedHeaders[headerIndex].trim().toLowerCase());
          headerIndex++;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  public void testProcessFile() {
    // User user = sidService.getUserBySessionId(sid);
    // OccurrenceQuery query = new OccurrenceQuery(0, 10);
    // query.addFilter("AcceptedSpecies = ten");
    // List<Occurrence> occList = occService.findByOccurrenceQuery(query, user
    // .getId());
    // assertTrue(occList.size() == 10);
    // File tempFile;
    // BufferedWriter writer;
    // String result;
    //
    // // test ProcessFile with all correct Occurrences
    // try {
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("10 old"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // Occurrences have some problems which processFile must catch
    // try {
    // occList.get(0).setId("ChooChoo");
    // occList.get(1).setId("-1");
    // occList.get(2).setId(Integer.MAX_VALUE + "");
    // occList.get(3).setId(occList.get(4).getId());
    // occList.get(5).setId("");
    // occList.get(6).setId(null);
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // System.err.println(result);
    // assertTrue(result.contains("3 old"));
    // assertTrue(result.contains("7 fail"));
    // assertTrue(result.contains("0 new"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // Occcurrences all have the same Id
    // occList = occService.findByOccurrenceQuery(query, user.getId());
    // assertTrue(occList.size() == 10);
    // try {
    // for (Occurrence occ : occList) {
    // occ.setId(occList.get(0).getId());
    // }
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("10 fail"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // all Occurrences have improperly formatted Id
    // try {
    // for (Occurrence occ : occList) {
    // occ.setId("there is an error... with your face");
    // }
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("10 fail"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // all Occurrences have Id not found in Db
    // try {
    // for (Occurrence occ : occList) {
    // occ.setId(Integer.MAX_VALUE + "");
    // }
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("10 fail"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // all Occurrences have null Id
    // // BUT queryToCsv.ReturnCsvString returns a csv with an Id field
    // // So in effect all Occurrences have emptry string Id
    // try {
    // for (Occurrence occ : occList) {
    // occ.setId(null);
    // }
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("10 fail"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // all Occurrences have emptry string Id
    // try {
    // for (Occurrence occ : occList) {
    // occ.setId("");
    // }
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(QueryToCsv.ReturnCsvString(occList));
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("10 fail"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // 2 new Occurrences are being updated
    // try {
    // StringBuilder stringBuilder = new StringBuilder();
    // stringBuilder
    // .append(
    // "\"GlobalUniqueIdentifier\",\"DateLastModified\",\"BasisOfRecord\",\"InstitutionCode\",\"CollectionCode\",\"CatalogNumber\",\"Remarks\",\"ScientificName\",\"Kingdom\",\"Phylum\",\"Class\",\"Order\",\"Family\",\"Genus\",\"SpecificEpithet\",\"InfraSpecificRank\",\"InfraspecificEpithet\",\"AuthorYearOfScientificName\",\"NomenclaturalCode\",\"IdentificationQualifier\",\"Continent\",\"IslandGroup\",\"Island\",\"Country\",\"StateProvince\",\"County\",\"Locality\",\"MinimumElevationInMeters\",\"MaximumElevationInMeters\",\"MinimumDepthInMeters\",\"MaximumDepthInMeters\",\"DayOfYear\",\"Collector\",\"Sex\",\"LifeStage\",\"CatalogNumberNumeric\",\"IdentifiedBy\",\"CollectorNumber\",\"FieldNumber\",\"FieldNotes\",\"VerbatimCollectingDate\",\"VerbatimElevation\",\"VerbatimDepth\",\"Preparations\",\"TypeStatus\",\"GenbankNumber\",\"OtherCatalogNumbers\",\"RelatedCatalogedItems\",\"IndividualCount\",\"DecimalLatitude\",\"DecimalLongitude\",\"GeodeticDatum\",\"CoordinateUncertaintyInMeters\",\"PointRadiusSpatialFit\",\"VerbatimLatitude\",\"VerbatimLongitude\",\"VerbatimCoordinteSystem\",\"GeoreferenceProtocol\",\"GeoreferenceRemarks\",\"YearCollected\",\"MonthCollected\",\"DayCollected\""
    // );
    // stringBuilder.append("\n");
    // stringBuilder
    // .append(
    // "\"UCM:Mammals:3258\",\"2007-02-27T00:00:00-07:00\",\"PreservedSpecimen\",\"UCM\",\"Mammals\",\"3258\",,\"Lemur\",\"Animalia\",\"Chordata\",\"Mammalia\",\"Primates\",\"Lemuridae\",\"Lemur\",,\"\",,,\"ICZN\",,,,,\"Madagascar\",,,,,,,,,,,,\"3258\",,,,,,,,,,,,,,,,,,\"\",,,,,,\"0\",\"0\",\"0\""
    // );
    // stringBuilder.append("\n");
    // stringBuilder
    // .append(
    // "\"FLMNH:Mammals:5655\",\"08/29/2008\",,\"FLMNH\",\"Mammals\",\"5655\",,\"VARECIA VARIEGATA\",,,\"Mammalia\",\"PRIMATES\",\"LEMURIDAE\",\"VARECIA\",\"VARIEGATA\",\"\",,,\"ICZN\",,,,,\"MADAGASCAR\",,,,,,,,,,,,,,\"ANSP 14235\",,,\"10 JUL 1901\",,,,,,,,,,,,,\"\",,,,,,\"1901\",\"7\",\"10\""
    // );
    // stringBuilder.append("\n");
    //
    // tempFile = File.createTempFile("fubar", ".csv");
    // writer = new BufferedWriter(new FileWriter(tempFile));
    // writer.write(stringBuilder.toString());
    // writer.close();
    //
    // result = fileUpSrvlt.proccessFile(tempFile, sid, true, true);
    // assertTrue(result.contains("2 new"));
    // tempFile.delete();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
  }

  private String[] contrainDelimitersArray(String delimiters[], int length) {
    if (delimiters == null) {
      delimiters = new String[length];
      for (int i = 0; i < length; i++) {
        delimiters[i] = DEFAULT_DELIMETER;
      }
    } else if (delimiters.length < length) {
      String temp[] = new String[length];
      int i;
      for (i = 0; i < delimiters.length; i++) {
        temp[i] = delimiters[i];
      }
      for (; i < length; i++) {
        temp[i] = DEFAULT_DELIMETER;
      }
      return temp;
    }
    return delimiters;
  }

  private InputStream getInputStream(String jmFileType) {
    String fileName = System.getProperty(jmFileType);
    if (fileName != null) {
      try {
        FileInputStream fInputStream = new FileInputStream(new File(fileName));
        return fInputStream;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
      }
    }
    return null;
  }

  private String[] getPropertyValues(String propertyKey, String valueDelimeter) {
    String propertyValues = parameterProps.getProperty(propertyKey);
    if (propertyValues == null) {
      fail("can't find " + propertyKey + " in " + PROPERTY_FILE + " file");
      return null;
    }
    return propertyValues.split(valueDelimeter);
  }
}