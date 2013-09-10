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
import java.util.Calendar;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.server.upload.Traitement;
import org.taxonomy.Classification;
import org.taxonomy.ClassificationOracle;
import org.taxonomy.CsvClassificationOracle;

/**
 * The default implementation of {@link ValidationService}.
 * 
 */
public class ValidationServiceImpl implements ValidationService {

  /**
   * The set of valid basis of record descriptions that are used to validate an
   * {@link Occurrence} basisOfRecord property.
   * 
   * @see http://wiki.tdwg.org/twiki/bin/view/DarwinCore/BasisOfRecord
   */
  protected final static Set<String> BASIS_OF_RECORD;

  /**
   * The set of valid nomenclatural codes that are used to validate an
   * {@link Occurrence} nomenclaturalCode property.
   * 
   * @see http://wiki.tdwg.org/twiki/bin/view/DarwinCore/NomenclaturalCode
   */
  protected final static Set<String> NOMENCLATURAL_CODES;

  /**
   * Regular expression pattern that matches a four digit year (i.e. 1888 but
   * not 188).
   */
  protected static final Pattern YEAR_PATTERN = Pattern.compile("^[\\d]{4,4}$");

  protected static final Logger log = Logger
          .getLogger(ValidationServiceImpl.class);

  /**
   * This property file solves the problem of confusion over the use of Basis Of
   * Record. (Issue 333)
   * 
   * @see http://rs.tdwg.org/dwc/terms/type-vocabulary/index.htm#theterms
   */
  private static final String PROPERTY_FILE = "ValidBasicOfRecords";
  /**
   * Adds all nomenclatural codes to the NOMENCLATURAL_CODES set.
   */
  static {
    String[] codes = { "ICBN", "ICZN", "BC", "ICNCP", "BioCode" };
    NOMENCLATURAL_CODES = new HashSet<String>();
    for (String code : codes) {
      NOMENCLATURAL_CODES.add(code.toLowerCase());
    }
  }

  /**
   * Adds all basis of record descriptions to the BASIS_OF_RECORD property.
   */
  static {
    String[] descriptions = { "PreservedSpecimen", "FossilSpecimen",
        "LivingSpecimen", "HumanObservation", "MachineObservation",
        "StillImage", "MovingImage", "SoundRecording", "OtherSpecimen",
        "Voucher", "Specimen" };
    BASIS_OF_RECORD = new HashSet<String>();
    for (String d : descriptions) {
      BASIS_OF_RECORD.add(d.toLowerCase());
    }
  }

  /**
   * Updates the {@link Occurrence} validation errors by appending the msg to
   * it. Also sets the {@link Occurrence} validated flag to false.
   * 
   * @param occurrence the occurrence to update
   * @param msg the error message
   */
  protected static void updateValidationErrors(Occurrence occurrence, String msg) {
    String errors = occurrence.getValidationError();
    if (errors == null) {
      errors = "";
    }
    errors += msg + "- ";// Error parsing from download
    occurrence.setValidationError(errors);
    occurrence.setValidated(false);
  }

  /**
   * @return
   */
  private static Integer getCurrentYear() {
    Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
    return currentYear;
  }

  private ResourceBundle basicOfRecordProps = null;

  /**
   * A taxonomic authority used to validate {@link Occurrence} taxonomy.
   */
  ClassificationOracle taxonomicAuthority;

  /**
   * Costructs a new {@link ValidationServiceImpl}.
   */
  public ValidationServiceImpl() {
    try {
      File taxonomy = new File(ValidationServiceImpl.class.getResource(
              "taxonomy.csv").getFile());
      taxonomicAuthority = new CsvClassificationOracle(taxonomy);
    } catch (IOException e) {
      log.info("Unable to create taxonomic authority.");
    }
  }

  /**
   * Validates a set of {@link Occurrence} objects by validating required fields
   * (using {@link ValidationService}), numeric fields, and taxonomy (using
   * {@link ClassificationOracle}).
   * 
   * @see org.rebioma.server.services.ValidationService#validate(java.util.Set)
   */
  public void validate(Set<Occurrence> occurrences, Traitement traitement) {
    if (occurrences == null || occurrences.size() == 0) {
      return;
    }
    int cent = occurrences.size();
    int i = 0;
    for (Occurrence occurrence : occurrences) {
    	traitement.setTraitement("Validate Occurrences ", cent*1024, ++i*1024);
    	if(traitement.getCancel())return;
    	occurrence.setValidationError(null);
    	validateRequiredFields(occurrence);
    	validateNumericFields(occurrence);
    	validateTaxonomy(occurrence);
    	String validationError = occurrence.getValidationError();
    	if (validationError != null && !validationError.equals("")) {
    		validationError = validationError.trim();
    		int lastComma = validationError.lastIndexOf("-");
    		if (lastComma > 0 && lastComma == validationError.length() - 1) {
    			validationError = validationError.substring(0, lastComma);
    		}
    		occurrence.setValidationError(validationError);
    	}
    }
  }
  
  public void validate(Set<Occurrence> occurrences) {
	    if (occurrences == null || occurrences.size() == 0) {
	      return;
	    }
	    int cent = occurrences.size();
	    int i = 0;
	    for (Occurrence occurrence : occurrences) {	    	
	      occurrence.setValidationError(null);
	      validateRequiredFields(occurrence);
	      validateNumericFields(occurrence);
	      validateTaxonomy(occurrence);
	      String validationError = occurrence.getValidationError();
	      if (validationError != null && !validationError.equals("")) {
	        validationError = validationError.trim();
	        int lastComma = validationError.lastIndexOf("-");
	        if (lastComma > 0 && lastComma == validationError.length() - 1) {
	          validationError = validationError.substring(0, lastComma);
	        }
	        occurrence.setValidationError(validationError);
	      }
	    }
	  }

  /**
   * Validates the following required property values:
   * 
   * <pre>
   * BasisOfRecord
   * YearCollected
   * Genus
   * SpecificEpithet
   * DecimalLatitude
   * DecimalLongitude
   * GeodeticDatum
   * CoordinateUncertaintyInMeters
   * NomenclaturalCode
   * </pre>
   * 
   * If any of the property values fail validation, the occurrence's validated
   * property is set to false.
   * 
   * @see http://code.google.com/p/rebioma/wiki/Validation
   * 
   * @param occurrence the occurrence object to validate
   */
  protected void validateRequiredFields(Occurrence occurrence) {
    boolean isError = false;
    occurrence.setValidated(true);

    // Validates BasisOfRecord by comparing to BASIS_OF_RECORD set:
    
    // basisOfRecord is no longer required (see issue 202)
    // however, this breaks if basisOfRecord is missing from a
    // record set, so I've commented out the following 
    //String basisOfRecord = removeSpaces(occurrence.getBasisOfRecord());
    //if (basisOfRecord == null || basisOfRecord.equals("")) {
    //  isError = true;
    //} else {
    //  String propertyBasisOfRecord = getPropertyValues(basisOfRecord
    //          .toLowerCase());
    //  if (propertyBasisOfRecord != null) {
    //    occurrence.setBasisOfRecord(propertyBasisOfRecord);
    //  } else {
    //    isError = true;
    //  }
    //}
    //if (isError) {
    //  updateValidationErrors(occurrence, "BasisOfRecord Invalid");
    //}

    // Validates YearCollected by checking for four digits:
    isError = false;
    Integer yearCollected;
    try {
      yearCollected = Integer.parseInt(occurrence.getYearCollected());
    } catch (Exception e) {
      yearCollected = null;
      occurrence.setYearCollected(null);
    }
    if (yearCollected == null) {
      isError = true;
    } else if (!YEAR_PATTERN.matcher(yearCollected.toString()).matches()) {
      isError = true;
    } else if (yearCollected > getCurrentYear()) {
      isError = true;
    }
    if (isError) {
      updateValidationErrors(occurrence, "Invalid YearCollected: "
              + yearCollected);
    }

    // Validates Genus:
    isError = false;
    String genus = occurrence.getGenus();
    if (genus == null || genus.equals("")) {
      isError = true;
    }
    if (isError) {
      updateValidationErrors(occurrence, "Genus required");
    }

    // Validates SpecificEpithet:
    isError = false;
    String specificEpithet = occurrence.getGenus();
    if (specificEpithet == null || specificEpithet.trim().equals("")) {
      isError = true;
    }
    if (isError) {
      updateValidationErrors(occurrence, "SpecificEpithet required");
    }

    // TODO: validate Datum. need list of EPSG codes.
    // @see http://code.google.com/p/rebioma/issues/detail?id=59

    // Validates DecimalLatitude:
    isError = false;
    String errorMsg = "";
    Double lat;
    try {
      lat = Double.parseDouble(occurrence.getDecimalLatitude());
    } catch (Exception e) {
      lat = null;
      occurrence.setDecimalLatitude(null);
    }
    if (lat == null) {
      isError = true;
      errorMsg = "DecimalLatitude required";
    } else if (lat < -30 || lat > 10) {
      isError = true;
      errorMsg = "DecimalLatitude (-30<y<10): " + lat;
    }
    if (isError) {
      updateValidationErrors(occurrence, errorMsg);
    }

    // Validates DecimalLongitude:
    isError = false;
    errorMsg = "";
    Double lng;
    try {
      lng = Double.parseDouble(occurrence.getDecimalLongitude());
    } catch (Exception e) {
      lng = null;
      occurrence.setDecimalLongitude(null);
    }
    if (lng == null) {
      isError = true;
      errorMsg = "DecimalLongitude required";
    } else if (lng < 40 || lng > 60) {
      isError = true;
      errorMsg = "DecimalLongitude (40<x<60): " + lng;
    }
    if (isError) {
      updateValidationErrors(occurrence, errorMsg);
    }

    // // Validates CoordinateUncertaintyInMeters:
    // isError = false;
    // Double meters;
    // try {
    // meters = Double
    // .parseDouble(occurrence.getCoordinateUncertaintyInMeters());
    // } catch (Exception e) {
    // meters = null;
    // occurrence.setCoordinateUncertaintyInMeters("-1");
    // }
    // if (meters == null) {
    // isError = true;
    // errorMsg = "CoordinateUncertaintyInMeters required";
    // } else if (meters <= 0 || meters > 1250000) { // 1250000 specific to
    // // Madagascar.
    // isError = true;
    // errorMsg = "CoordinateUncertaintyInMeters invalid: " + meters;
    // }
    // if (isError) {
    // updateValidationErrors(occurrence, errorMsg);
    // }
    //
    // // Validates NomenclaturalCode:
    // isError = false;
    // String nc = occurrence.getNomenclaturalCode();
    // if (nc == null || nc.equals("")) {
    // isError = true;
    // errorMsg = "NomenclaturalCode required";
    // } else if (!NOMENCLATURAL_CODES.contains(nc.trim().toLowerCase())) {
    // isError = true;
    // errorMsg = "NomenclaturalCode unknown: " + nc;
    // }
    // if (isError) {
    // updateValidationErrors(occurrence, errorMsg);
    // }

  }

  /**
   * Validates an {@link Occurrence} taxonomy by using a
   * {@link ClassificationOracle} to update the following fields:
   * 
   * <pre>
   * NomenclaturalCode 
   * AcceptedKingdom
   * AcceptedPhylum
   * AcceptedClass
   * AcceptedOrder
   * AcceptedSuborder
   * AcceptedFamily
   * AcceptedSubfamily
   * AcceptedGenus
   * AcceptedSubgenus
   * AcceptedSpecificEpithet
   * AcceptedSpecies
   * VerbatimSpecies
   * </pre>
   * 
   * Genus, SpecificEpithet, and NomenclaturalCode properties are required to
   * classify an {@link Occurrence}.
   * 
   * @see http://code.google.com/p/rebioma/wiki/Validation
   * 
   * @param occurrence the occurrence to validate
   */
  protected void validateTaxonomy(Occurrence occurrence) {
    String genus = occurrence.getGenus();
    String se = occurrence.getSpecificEpithet();
    String nc = occurrence.getNomenclaturalCode();
    String ir = occurrence.getInfraspecificRank();
	String ie = occurrence.getInfraspecificEpithet();
    Classification c = taxonomicAuthority.classify(genus, se, ir, ie, nc); /* ie before nc */
    if (c != null) {
    String ok = c.getValidation();  
    	if (ok.equals("OK")){	
      occurrence.setAcceptedKingdom(c.getKingdom());
      occurrence.setAcceptedPhylum(c.getPhylum());
      occurrence.setAcceptedClass(c.getClazz());/* Fix Issue 296 */
      occurrence.setAcceptedOrder(c.getOrder());
      occurrence.setAcceptedSuborder(c.getSuborder());
      occurrence.setAcceptedFamily(c.getFamily());
      occurrence.setAcceptedSubfamily(c.getSubfamily());
      occurrence.setAcceptedGenus(c.getGenus());
      occurrence.setAcceptedSubgenus(c.getSubgenus());
      occurrence.setAcceptedSpecificEpithet(c.getSpecificEpithet());
      occurrence.setAcceptedSpecies(c.getAcceptedSpecies());
      occurrence.setVerbatimSpecies(c.getVerbatimSpecies()); /* Fix Issue 329 */
    	} else if (ok.equals("KO")){
    		String notes;
    		notes = c.getNotes();
    		updateValidationErrors(occurrence, notes);
    	} else { 
    		updateValidationErrors(occurrence, "Taxonomic classification not validated yet");
    	}
    	
    } else {
      updateValidationErrors(occurrence, "Taxonomic classification unknown");
    }
  }

  private String getPropertyValues(String propertyKey) {
    loadBasicOfRecordProperties();
    try {
      String propertyValues = basicOfRecordProps.getString(propertyKey);
      return propertyValues;
    } catch (MissingResourceException m) {
      return null;
    }
  }

  private void loadBasicOfRecordProperties() {
    if (basicOfRecordProps == null) {
      try {
        basicOfRecordProps = ResourceBundle.getBundle(PROPERTY_FILE);
      } catch (Exception e) {
        log.error("error while loading " + PROPERTY_FILE + ".properties file",
                e);
      }
    }
  }

  private String removeSpaces(String s) {
    StringBuilder noSpaceString = new StringBuilder();
    for (String val : s.split(" ")) {
      if (!val.equals(" ")) {
        noSpaceString.append(val);
      }
    }
    return noSpaceString.toString();
  }

  /**
   * Numeric fields are stored as String in {@link Occurrence}. This method sets
   * all numeric fields to null if the field value cannot be converted to the
   * appropriate numeric data type without an exception. This is required so
   * that Hibernate can save these values to the database later on.
   * 
   * NOTE: there might be a way to configure Hibernate to do this.
   * 
   * Here are the values that need numeric validation:
   * 
   * CatalogNumberNumeric, CoordinateUncertaintyInMeters, DEMElevation,
   * EtpTotal1950, EtpTotal2000, EtpTotalfuture, FootprintSpatialFit,
   * GeolStrech, MaxPerc1950, MaxPerc2000, MaxPercfuture, MaxTemp2000,
   * MaxTempfuture, MaximumDepthInMeters, MaximumElevationInMeters, Maxtemp1950,
   * MinPerc1950, MinPerc2000, MinPercfuture, MinTemp1950, MinTemp2000,
   * MinTempfuture, MinimumDepthInMeters, MinimumElevationInMeters, PFC1950,
   * PFC1970, PFC1990, PFC2000, PointRadiusSpatialFit, RealMar1950, RealMar2000,
   * RealMarfuture, RealMat2000, WBPos1950, WBPos2000, WBPosfuture, WBYear1950,
   * WBYear2000, WBYearfuture,
   * 
   * @param occurrence the occurrence to validate
   */
  private void validateNumericFields(Occurrence occurrence) {
    // Integers:
    try {
      Integer.parseInt(occurrence.getDayOfYear());
    } catch (Exception e) {
      occurrence.setDayOfYear(null);
    }
    try {
      Integer.parseInt(occurrence.getYearCollected());
    } catch (Exception e) {
      occurrence.setYearCollected(null);
    }
    try {
      Integer.parseInt(occurrence.getMonthCollected());
    } catch (Exception e) {
      occurrence.setMonthCollected(null);
    }
    try {
      Integer.parseInt(occurrence.getDayCollected());
    } catch (Exception e) {
      occurrence.setDayCollected(null);
    }
    try {
      Integer.parseInt(occurrence.getIndividualCount());
    } catch (Exception e) {
      occurrence.setIndividualCount(null);
    }
    try {
      Integer.parseInt(occurrence.getAdjustedCoordinateUncertaintyInMeters());
    } catch (Exception e) {
      occurrence.setAdjustedCoordinateUncertaintyInMeters(null);
    }

    // Tiny Integers
    try {
      Integer.parseInt(occurrence.getValidDistributionFlag());
    } catch (Exception e) {
      occurrence.setValidDistributionFlag(null);
    }

    // Doubles:
    try {
      Double.parseDouble(occurrence.getCatalogNumberNumeric());
    } catch (Exception e) {
      occurrence.setCatalogNumberNumeric(null);
    }
    try {
      Double.parseDouble(occurrence.getCoordinateUncertaintyInMeters());
    } catch (Exception e) {
      occurrence.setCoordinateUncertaintyInMeters(null);
    }
    try {
      Double.parseDouble(occurrence.getDemelevation());
    } catch (Exception e) {
      occurrence.setDemelevation(null);
    }
    try {
      Double.parseDouble(occurrence.getEtpTotal1950());
    } catch (Exception e) {
      occurrence.setEtpTotal1950(null);
    }
    try {
      Double.parseDouble(occurrence.getEtpTotal2000());
    } catch (Exception e) {
      occurrence.setEtpTotal2000(null);
    }
    try {
      Double.parseDouble(occurrence.getEtpTotalfuture());
    } catch (Exception e) {
      occurrence.setEtpTotalfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getFootprintSpatialFit());
    } catch (Exception e) {
      occurrence.setFootprintSpatialFit(null);
    }
    try {
      Double.parseDouble(occurrence.getGeolStrech());
    } catch (Exception e) {
      occurrence.setGeolStrech(null);
    }
    try {
      Double.parseDouble(occurrence.getMaxPerc1950());
    } catch (Exception e) {
      occurrence.setMaxPerc1950(null);
    }
    try {
      Double.parseDouble(occurrence.getMaxPerc2000());
    } catch (Exception e) {
      occurrence.setMaxPerc2000(null);
    }
    try {
      Double.parseDouble(occurrence.getMaxPercfuture());
    } catch (Exception e) {
      occurrence.setMaxPercfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getMaxTemp2000());
    } catch (Exception e) {
      occurrence.setMaxTemp2000(null);
    }
    try {
      Double.parseDouble(occurrence.getMaxTempfuture());
    } catch (Exception e) {
      occurrence.setMaxTempfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getMaximumDepthInMeters());
    } catch (Exception e) {
      occurrence.setMaximumDepthInMeters(null);
    }
    try {
      Double.parseDouble(occurrence.getMaximumElevationInMeters());
    } catch (Exception e) {
      occurrence.setMaximumElevationInMeters(null);
    }
    try {
      Double.parseDouble(occurrence.getMaxtemp1950());
    } catch (Exception e) {
      occurrence.setMaxtemp1950(null);
    }
    try {
      Double.parseDouble(occurrence.getMinPerc1950());
    } catch (Exception e) {
      occurrence.setMinPerc1950(null);
    }
    try {
      Double.parseDouble(occurrence.getMinPerc2000());
    } catch (Exception e) {
      occurrence.setMinPerc2000(null);
    }
    try {
      Double.parseDouble(occurrence.getMinPercfuture());
    } catch (Exception e) {
      occurrence.setMinPercfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getMinTemp1950());
    } catch (Exception e) {
      occurrence.setMinTemp1950(null);
    }
    try {
      Double.parseDouble(occurrence.getMinTemp2000());
    } catch (Exception e) {
      occurrence.setMinTemp2000(null);
    }
    try {
      Double.parseDouble(occurrence.getMinTempfuture());
    } catch (Exception e) {
      occurrence.setMinTempfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getMinimumDepthInMeters());
    } catch (Exception e) {
      occurrence.setMinimumDepthInMeters(null);
    }
    try {
      Double.parseDouble(occurrence.getMinimumElevationInMeters());
    } catch (Exception e) {
      occurrence.setMinimumElevationInMeters(null);
    }
    try {
      Double.parseDouble(occurrence.getPfc1950());
    } catch (Exception e) {
      occurrence.setPfc1950(null);
    }
    try {
      Double.parseDouble(occurrence.getPfc1970());
    } catch (Exception e) {
      occurrence.setPfc1970(null);
    }
    try {
      Double.parseDouble(occurrence.getPfc1990());
    } catch (Exception e) {
      occurrence.setPfc1990(null);
    }
    try {
      Double.parseDouble(occurrence.getPfc2000());
    } catch (Exception e) {
      occurrence.setPfc2000(null);
    }
    try {
      Double.parseDouble(occurrence.getPointRadiusSpatialFit());
    } catch (Exception e) {
      occurrence.setPointRadiusSpatialFit(null);
    }
    try {
      Double.parseDouble(occurrence.getRealMar1950());
    } catch (Exception e) {
      occurrence.setRealMar1950(null);
    }
    try {
      Double.parseDouble(occurrence.getRealMar2000());
    } catch (Exception e) {
      occurrence.setRealMar2000(null);
    }
    try {
      Double.parseDouble(occurrence.getRealMarfuture());
    } catch (Exception e) {
      occurrence.setRealMarfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getRealMat1950());
    } catch (Exception e) {
      occurrence.setRealMat1950(null);
    }
    try {
      Double.parseDouble(occurrence.getRealMat2000());
    } catch (Exception e) {
      occurrence.setRealMat2000(null);
    }
    try {
      Double.parseDouble(occurrence.getRealMatfuture());
    } catch (Exception e) {
      occurrence.setRealMatfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getWbpos1950());
    } catch (Exception e) {
      occurrence.setWbpos1950(null);
    }
    try {
      Double.parseDouble(occurrence.getWbpos2000());
    } catch (Exception e) {
      occurrence.setWbpos2000(null);
    }
    try {
      Double.parseDouble(occurrence.getWbposfuture());
    } catch (Exception e) {
      occurrence.setWbposfuture(null);
    }
    try {
      Double.parseDouble(occurrence.getWbyear1950());
    } catch (Exception e) {
      occurrence.setWbyear1950(null);
    }
    try {
      Double.parseDouble(occurrence.getWbyear2000());
    } catch (Exception e) {
      occurrence.setWbyear2000(null);
    }
    try {
      Double.parseDouble(occurrence.getWbyearfuture());
    } catch (Exception e) {
      occurrence.setWbyearfuture(null);
    }

    // (10,7) Doubles
    try {
      Double.parseDouble(occurrence.getDecimalLatitude());
    } catch (Exception e) {
      occurrence.setDecimalLatitude(null);
    }
    try {
      Double.parseDouble(occurrence.getDecimalLongitude());
    } catch (Exception e) {
      occurrence.setDecimalLongitude(null);
    }
    try {
      Double.parseDouble(occurrence.getDecLatInWgs84());
    } catch (Exception e) {
      occurrence.setDecLatInWgs84(null);
    }
    try {
      Double.parseDouble(occurrence.getDecLongInWgs84());
    } catch (Exception e) {
      occurrence.setDecLongInWgs84(null);
    }
  }
}
