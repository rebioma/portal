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
 
package org.rebioma.server.services;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.rebioma.client.Occurrence;
import org.rebioma.server.util.CsvUtil;
import org.rebioma.server.util.CsvUtilTest;

*//**
 * Unit tests for {@link ValidationServiceImpl}.
 * 
 *//*
public class ValidationServiceTest extends TestCase {

  *//**
   * The {@link Logger} used to log test messages.
   *//*
  private static final Logger log = Logger
          .getLogger(ValidationServiceTest.class);

  *//**
   * Set of {@link Occurrence} objects to use for testing.
   *//*
  private static Set<Occurrence> INVALID_TEST_DATA;

  *//**
   * The {@link ValidationService}.
   *//*
  ValidationService validationService;

  OccurrenceDb occurrenceService;

  private Set<Occurrence> VALID_TEST_DATA;

  *//**
   * Creates the {@link ValidationService} and data used for testing.
   *//*
  @Override
  public void setUp() {
    validationService = new ValidationServiceImpl();
    occurrenceService = new OccurrenceDbImpl();

    INVALID_TEST_DATA = new HashSet<Occurrence>();
    Occurrence occurrence = new Occurrence();
    occurrence.setBasisOfRecord("Foo");
    occurrence.setYearCollected("19999");
    occurrence.setGenus(null);
    occurrence.setGeodeticDatum(null);
    occurrence.setSpecificEpithet(null);
    occurrence.setDecimalLatitude("-90.0000001");
    occurrence.setDecimalLongitude("-180.0000001");
    occurrence.setCoordinateUncertaintyInMeters("0.0");

    INVALID_TEST_DATA.add(occurrence);

    VALID_TEST_DATA = new HashSet<Occurrence>();
    occurrence = new Occurrence();
    occurrence.setBasisOfRecord("LivingSpecimen");
    occurrence.setYearCollected("1888");
    occurrence.setGenus("Coeliades");
    occurrence.setGeodeticDatum("EPSG-foo");
    occurrence.setSpecificEpithet("ernesti");
    occurrence.setNomenclaturalCode("ICZN");
    occurrence.setDecimalLatitude("-90.0000000");
    occurrence.setDecimalLongitude("-180.0000000");
    occurrence.setCoordinateUncertaintyInMeters("0.1");
    VALID_TEST_DATA.add(occurrence);
  }

  public void testFromCsv() {
    File csvFile = new File(CsvUtilTest.class.getResource("100.csv").getFile());
    log.info("Test CSV data size: " + csvFile.length());
    long start = System.currentTimeMillis();
    try {
      Set<Occurrence> set = CsvUtil.loadOccurrences(csvFile);
      log.info(((System.currentTimeMillis() - start) / 1000.0)
              + " seconds to load " + set.size() + " occurrences.");
      assertNotNull(set);
      start = System.currentTimeMillis();
      validationService.validate(set);
      log.info(((System.currentTimeMillis() - start) / 1000.0)
              + " seconds to validate " + set.size() + " occurrences.");
      for (Occurrence o : set) {
        o.setOwner("1");
        o.setPublic_(true);
        o.setVettable(true);
        o.setVetted(false);
        o.setTapirAccessible(false);
        if (!o.isValidated()) {
          System.out.println(o.getGlobalUniqueIdentifier() + " failed:");
          System.out.println(o.getValidationError());
          System.out.println();
        }
      }
      occurrenceService.attachDirty(set);

    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  *//**
   * Tests invalid {@link Occurrence} data by validating it and then checking
   * that the occurrence validation error is not null.
   *//*
  public void testInvalidData() {
    validationService.validate(INVALID_TEST_DATA);
    for (Occurrence occurrence : INVALID_TEST_DATA) {
      assertNotNull(occurrence.getValidationError());
      assertFalse(occurrence.isValidated());
      log.info(occurrence.getValidationError());
    }
    log.info("Passed testInvalidData()");
  }

  *//**
   * Tests taxonomic validation by adding a known genus, specific epithet, and
   * nomenclatural code to the occurence prior to validation.
   *//*
  public void testTaxonomicValidation() {
    Occurrence occurrence = VALID_TEST_DATA.iterator().next();
    occurrence.setGenus("Coeliades");
    occurrence.setSpecificEpithet("ernesti");
    occurrence.setNomenclaturalCode("ICZN");
    validationService.validate(VALID_TEST_DATA);
    assertNull(occurrence.getValidationError());
    assertNotNull(occurrence.getAcceptedSpecies());
    log.info("Passed testTaxonomicValidation()");
  }

  *//**
   * Tests valid {@link Occurrence} data by validating it and then checking that
   * the occurrence validation error is null.
   *//*
  public void testValidData() {
    validationService.validate(VALID_TEST_DATA);
    for (Occurrence occurrence : VALID_TEST_DATA) {
      assertNull(occurrence.getValidationError());
      assertTrue(occurrence.isValidated());
    }
    log.info("Passed testValidData()");
  }
}
*/