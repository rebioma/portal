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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.bean.Occurrence;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

/**
 * The {@link CsvUtil} class provides methods for loading {@link Occurrence}
 * objects from a CSV file.
 * 
 */
public class CsvUtil {

  /**
   * The required CSV header column names.
   */
  private static final String[] REQUIRED_HEADER_COLUMNS = {
      "CoordinateUncertaintyInMeters", "DecimalLatitude", "DecimalLongitude",
      "Genus", "GeodeticDatum", "NomenclaturalCode", "SpecificEpithet",
      "YearCollected" };

  /**
   * The complete list of valid CSV header column names from darwin core, the
   * curatorial extension, and the geospatial extention.
   */
  private static final String[] VALID_HEADER_COLUMNS = { "Attributes",
      "AuthorYearOfScientificName", "BasisOfRecord", "CatalogNumber",
      "CatalogNumberNumeric", "Class", "CollectingMethod", "CollectionCode",
      "Collector", "CollectorNumber", "Continent",
      "CoordinateUncertaintyInMeters", "Country", "County", "DateIdentified",
      "DateLastModified", "DayCollected", "DayOfYear", "DecimalLatitude",
      "DecimalLongitude", "Disposition", "EarliestDateCollected", "Family",
      "FieldNotes", "FieldNumber", "FootprintSpatialFit", "FootprintWKT",
      "GenBankNumber", "Genus", "GeodeticDatum", "GeoreferenceProtocol",
      "GeoreferenceRemarks", "GeoreferenceSources",
      "GeoreferenceVerificationStatus", "GlobalUniqueIdentifier",
      "HigherGeography", "HigherTaxon", "IdentificationQualifer",
      "IdentifiedBy", "ImageURL", "IndividualCount", "InformationWithheld",
      "InfraspecificEpithet", "InfraspecificRank", "InstitutionCode", "Island",
      "IslandGroup", "Kingdom", "LatestDateCollected", "LifeStage", "Locality",
      "MaximumDepthInMeters", "MaximumElevationInMeters",
      "MinimumDepthInMeters", "MinimumElevationInMeters", "MonthCollected",
      "NomenclaturalCode", "Order", "OtherCatalogNumbers", "Phylum",
      "PointRadiusSpatialFit", "Preparations", "RelatedCatalogedItems",
      "RelatedInformation", "Remarks", "ScientificName", "Sex",
      "SpecificEpithet", "StateProvince", "TypeStatus",
      "ValidDistributionFlag", "VerbatimCollectingDate",
      "VerbatimCoordinateSystem", "VerbatimCoordinates", "VerbatimDepth",
      "VerbatimElevation", "VerbatimLatitude", "VerbatimLongitude",
      "WaterBody", "YearCollected" };

  /**
   * The max bytes per CSV file. 30MB.
   */
  private static final long MAX_BYTES_FILE = 120720000;

  public static Map<String, Integer> getHeaderColIndexes(String headers[]) {
    Map<String, Integer> map = new HashMap<String, Integer>();
    for (int i = 0; i < headers.length; i++) {
      map.put(headers[i].trim().toLowerCase(), i);
    }
    return map;
  }

  /**
   * Returns a mapping of CSV header column names (exactly as they appear in a
   * CSV file) to their corresponding {@link Occurrence} property names.
   * 
   * @param header the CSV file header
   * @return the map
   */
  public static Map<String, String> getHeaderColumnMap(String[] header) {
    Map<String, String> map = new HashMap<String, String>();
    for (String name : header) {
      if (name.equalsIgnoreCase("class")) {
        map.put(name, "class_");
        continue;
      } else if (name.equalsIgnoreCase("public")) {
    	  map.put(name, "public_");
    	  continue;
      } else if (name.equalsIgnoreCase("order")) {
    	  map.put(name, "order_");
    	  continue;
      } else if (name.equalsIgnoreCase("reviewed") || name.equalsIgnoreCase("stability")) {
        continue;
      }

      map.put(name, name.substring(0, 1).toLowerCase() + name.substring(1));
    }
    return map;
  }

  /**
   * Returns null if the header contains all required header column names,
   * otherwise returns a list of missing header column names.
   * 
   * @param header a CSV file header
   * @param requiredHeader TODO
   */
  public static List<String> isHeaderValid(String[] header,
      String[] requiredHeader) {
    if (requiredHeader == null) {
      requiredHeader = REQUIRED_HEADER_COLUMNS;
    }
    List<String> csvHeader = new ArrayList<String>();
    for (String column : header) {
      csvHeader.add(column.toLowerCase());
    }
    List<String> requiredHeaders = new ArrayList<String>();
    for (String required : requiredHeader) {
      requiredHeaders.add(required.toLowerCase());
    }
    csvHeader.retainAll(requiredHeaders);
    requiredHeaders.removeAll(csvHeader);
    return requiredHeaders.size() == 0 ? null : requiredHeaders;
  }

  public static <E> List<E> loadEntities(String csvFile, Class<E> entityClass) {
    File file = new File(csvFile);
    try {
      String[] header = getHeader(file);
      HeaderColumnNameTranslateMappingStrategy strat = new HeaderColumnNameTranslateMappingStrategy();
      strat.setType(entityClass);
      Map<String, String> map = getHeaderColumnMap(header);
      strat.setColumnMapping(map);
      CsvToBean csv = new CsvToBean();
      return csv.parse(strat, new FileReader(csvFile));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;

  }

  /**
   * Returns CSV data as a {@link Set} of {@link Occurrence} objects.
   * 
   * @param csvFile the CSV file
   * @throws IOException if there are problems reading the CSV file or if it is
   *           too big
   */
  public static Set<Occurrence> loadOccurrences(File csvFile)
      throws IOException {
    if (!isFileSizeOk(csvFile)) {
      throw new IOException("File size too big");
    }
    String[] header = getHeader(csvFile);
    List<String> missingHeaders = isHeaderValid(header, null);
    if (missingHeaders != null) {
      throw new IOException("Missing required headers: " + missingHeaders);
    }
    HeaderColumnNameTranslateMappingStrategy strat = new HeaderColumnNameTranslateMappingStrategy();
    strat.setType(Occurrence.class);
    Map<String, String> map = getHeaderColumnMap(header);
    strat.setColumnMapping(map);
    CsvToBean csv = new CsvToBean();
    List<Occurrence> occurrences = csv.parse(strat, new FileReader(csvFile));
    Set<Occurrence> set = new HashSet<Occurrence>();
    set.addAll(occurrences);
    return set;
  }

  public static Set<Occurrence> loadOccurrences(File csvFile, char delimiter)
      throws IOException {
    if (!isFileSizeOk(csvFile)) {
      throw new IOException("File size too big");
    }
    String[] header = getHeader(csvFile, delimiter);
    // List<String> missingHeaders = isHeaderValid(header);
    // if (missingHeaders != null) {
    // throw new IOException("Missing required headers: " + missingHeaders);
    // }
    HeaderColumnNameTranslateMappingStrategy strat = new HeaderColumnNameTranslateMappingStrategy();
    strat.setType(Occurrence.class);
    Map<String, String> map = getHeaderColumnMap(header);
    strat.setColumnMapping(map);
    CsvToBean csv = new CsvToBean();
    List<Occurrence> occurrences = csv.parse(strat, new CSVReader(
        new FileReader(csvFile), delimiter));
    System.out.println("public: " + occurrences.get(0).isPublic_());
    Set<Occurrence> set = new HashSet<Occurrence>();
    set.addAll(occurrences);
    return set;
  }

  private static String[] getHeader(File csvFile) throws IOException {
    CSVReader reader;
    reader = new CSVReader(new FileReader(csvFile));
    return reader.readNext();
  }

  /**
   * Returns a CSV file header which is the first row in the file.
   * 
   * @param csvFile the CSV file
   * @return the csvFile header
   * @throws IOException
   */
  private static String[] getHeader(File csvFile, char delimiter)
      throws IOException {
    CSVReader reader;
    reader = new CSVReader(new FileReader(csvFile), delimiter);
    return reader.readNext();
  }

  /**
   * Returns true if the file size in bytes is less than MAX_BYTES_FILE.
   * 
   * @param csvFile a csv file
   * @return true if file size is ok
   */
  private static boolean isFileSizeOk(File csvFile) {
    long nBytes = csvFile.length();
    return nBytes < MAX_BYTES_FILE;
  }

  private static File replaceDelimiters(File csvFile) {

    File newFile = null;
    try {
      newFile = File.createTempFile("temporaryCSV", ".csv");

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return newFile;
  }
  
  public static void main(String[] args) throws IOException {
//	  Set<Occurrence> list = CsvUtil.loadOccurrences(new File("C:\\DOCUME~1\\CONSUL~1\\LOCALS~1\\Temp\\_occurrences_Croco.csv7030749315007702413.csv"), ';');
//	  Set<Occurrence> list = CsvUtil.loadOccurrences(new File("F:\\Consultant\\Mes documents\\REBIOMADATA\\csv\\occurrences_Croco.csv"), ',');
//	  System.out.println(list.size()); 
	 
  }
}
