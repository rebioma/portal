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
package org.rebioma.client.bean;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.DetailView;
import org.rebioma.client.DetailView.FieldConstants;
import org.rebioma.client.i18n.AppConstants;

/**
 * Used in OccurrenceTable to create occurrence's summary in a table with the
 * following data: id, public, validated, vetted, validationError.
 * 
 * @author Tri
 * 
 */
public class OccurrenceSummary implements Comparable<OccurrenceSummary> {
  /**
   * 
   * @author tri
   * 
   */
  public static class OccurrenceFieldItem {
    /**
     * A name of this field.
     */
    private final String name;
    /**
     * A value associated with this field.
     */
    private final String value;

    public OccurrenceFieldItem(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return name + " - " + value;
    }
  }

  /**
   * Constants required for i18n.
   */
  public static final AppConstants constants = ApplicationView.getConstants();

  /**
   * The required header for authenticated's OccurrenceListView.
   * 
   * The first empty String is for the check box column.
   * 
   * Note: this array and {@link #GUEST_HEADER_CSS_STYLES} array is a one to one
   * mapping.
   */

  public static final String GUEST_REQUIRED_HEADERS[] = { " ", "ReBioMa Id",
      constants.Taxonomy(), constants.Validated(), constants.Reviewed(),
      constants.Owner(), constants.Collaborators(), constants.ValidationError() };

  /**
   * The required header for logged in user's OccurrenceListView.
   * 
   * The first empty String is for the check box column.
   * 
   * Note: this array and {@link #USER_HEADER_CSS_STYLES} array is a one to one
   * mapping.
   */
  public static final String USER_REQUIRED_HEADERS[] = { " ", "ReBioMa Id",
      constants.Taxonomy(), constants.Public(), constants.Validated(),
      constants.Reviewed(), constants.Owner(), constants.Collaborators(),
      constants.ValidationError() };
  /**
   * The required header for logged in user's OccurrenceListView.
   * 
   * The first empty String is for the check box column.
   * 
   * Note: this array and {@link #USER_HEADER_CSS_STYLES} array is a one to one
   * mapping.
   */
  public static final String REVIEWER_REQUIRED_HEADERS[] = { " ", "ReBioMa Id",
      constants.Taxonomy(), constants.Public(), constants.Validated(),
      /*constants.MyReviewed(),*/ constants.Reviewed(), constants.Owner(),
      /*constants.Collaborators(), constants.ValidationError()*/
      "Basis of record" , "Institution code",
       /*"Genus","Specific epithet", "Infraspecific epithet", */"StateProvince",
      "County", "Locality", "Collecting method",
      "Collector", "Attributes", "Verbatim collecting Date", 
      "Verbatim elevation", constants.YearCollected().replace(":", ""), "Related information"};

  public static final String MY_REVIEWED = "my reviewed";

  /**
   * 
   * Gets a {@link OccurrenceFieldItem} which contains taxonomic authorities
   * species display and its value.
   * 
   * @param occurrence
   * @return a {@link OccurrenceFieldItem} which contains taxonomic authorities
   *         species display and its value.
   */
  public static OccurrenceFieldItem getDisplayField(Occurrence occurrence) {
    List<OccurrenceFieldItem> taxonomicAuthorities = new ArrayList<OccurrenceFieldItem>();
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.ACCEPTED_SPECIES, occurrence
            .getAcceptedSpecies()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.SCIENTIFIC_NAME, occurrence
            .getScientificName()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.VERBATIM_SPECIES, occurrence
            .getVerbatimSpecies()));
    // GENUS_SPECIES
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.GENUS_SPECIES, getGenusSpecies(occurrence)));
    taxonomicAuthorities
        .add(new OccurrenceFieldItem(DetailView.FieldConstants.ACCEPTED_GENUS,
            occurrence.getAcceptedGenus()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.ACCEPTED_SUBFAMILY, occurrence
            .getAcceptedSubfamily()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.ACCEPTED_FAMILY, occurrence
            .getAcceptedFamily()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.ACCEPTED_SUBORDER, occurrence
            .getAcceptedSuborder()));
    taxonomicAuthorities
        .add(new OccurrenceFieldItem(DetailView.FieldConstants.ACCEPTED_ORDER,
            occurrence.getAcceptedOrder()));
    taxonomicAuthorities
        .add(new OccurrenceFieldItem(DetailView.FieldConstants.ACCEPTED_CLASS,
            occurrence.getAcceptedClass()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.ACCEPTED_PHYLUM, occurrence
            .getAcceptedPhylum()));
    taxonomicAuthorities.add(new OccurrenceFieldItem(
        DetailView.FieldConstants.ACCEPTED_KINGDOM, occurrence
            .getAcceptedKingdom()));
    for (OccurrenceFieldItem taxonomic : taxonomicAuthorities) {
      String value = taxonomic.getValue();
      if (value != null && !value.equals("")) {
        return taxonomic;
      }
    }
    return null;
  }

  public static boolean isAscLayersLoaded(Occurrence o) {
    return !isEmpty(o.getEtpTotal1950()) || !isEmpty(o.getEtpTotal2000())
        || !isEmpty(o.getEtpTotalfuture()) || !isEmpty(o.getPfc1950())
        || !isEmpty(o.getPfc1970()) || !isEmpty(o.getPfc1990())
        || !isEmpty(o.getPfc2000()) || !isEmpty(o.getMaxPerc1950())
        || !isEmpty(o.getMaxPerc2000()) || !isEmpty(o.getMaxPercfuture())
        || !isEmpty(o.getMaxtemp1950()) || !isEmpty(o.getMaxTemp2000())
        || !isEmpty(o.getMaxTempfuture()) || !isEmpty(o.getMinPerc1950())
        || !isEmpty(o.getMinPerc2000()) || !isEmpty(o.getMinPercfuture())
        || !isEmpty(o.getMinTemp1950()) || !isEmpty(o.getMinTemp2000())
        || !isEmpty(o.getMinTempfuture()) || !isEmpty(o.getRealMar1950())
        || !isEmpty(o.getRealMar2000()) || !isEmpty(o.getRealMarfuture())
        || !isEmpty(o.getRealMat1950()) || !isEmpty(o.getRealMat2000())
        || !isEmpty(o.getRealMatfuture()) || !isEmpty(o.getWbyear1950())
        || !isEmpty(o.getWbyear2000()) || !isEmpty(o.getWbyearfuture())
        || !isEmpty(o.getWbpos1950()) || !isEmpty(o.getWbpos2000())
        || !isEmpty(o.getWbposfuture());
  }

  public static boolean isEmailVisisble(Occurrence o) {
    User currentUser = ApplicationView.getAuthenticatedUser();
    String email = o.getOwnerEmail();
    boolean currentUserMatchesOwner = (currentUser != null)
        && (currentUser.getEmail().equals(email));
    return currentUserMatchesOwner || o.isEmailVisible();

  }

  private static String getGenusSpecies(Occurrence occurrence) {
    if (occurrence.getAcceptedGenus() == null
        || occurrence.getAcceptedGenus() == null)
      return null;
    return occurrence.getAcceptedGenus().trim() + " "
        + occurrence.getSpecificEpithet().trim();
  }

  private static boolean isEmpty(String value) {
    return value == null || value.equals("");
  }

  private final Occurrence occurrence;

  /**
   * Initialize OccurrenceSummary with the given occurrence
   * 
   * @param occurrence
   */
  public OccurrenceSummary(Occurrence occurrence) {
    this.occurrence = occurrence;
  }

  public int compareTo(OccurrenceSummary o) {
    String comapreAcceptedSpecies = o.getAcceptedSpecies();
    String thisAcceptedSpecies = getAcceptedSpecies();
    return comapreAcceptedSpecies.compareTo(thisAcceptedSpecies);
  }

  /**
   * Determine whether this OccurrenceSummary and given Occurrence summary by
   * comparing occurrence id.
   * 
   * @return true if their id are equals.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    OccurrenceSummary other = (OccurrenceSummary) obj;
    return other.occurrence.getId().equals(occurrence.getId());
  }

  /**
   * Gets this occurrence accepted species
   * 
   * @return acceptedSpecies if accepted species is null or empty return "----"
   */
  public String getAcceptedSpecies() {
    String acceptedSpecies = occurrence.getAcceptedSpecies();
    if (acceptedSpecies == null || acceptedSpecies.trim().equals("")) {
      acceptedSpecies = "----";
    }
    return acceptedSpecies;
  }

  /**
   * Gets the OccurrenceSummary to display for map infowindow in array of
   * String.
   * 
   * @return new String[] {id, species display info, validated, vetted,
   *         ownerEmail, DecimalLatitude, DecimalLongitude, locality, country,
   *         state, county, validationError}
   */
  public String[] getMapUnauthenticatedSummary() {
    return new String[] {
        occurrence.getId().toString(),
        getTaxonomic(),
        booleanToString(occurrence.isValidated()),
        getReviewedStatus(occurrence.getReviewed()),
        occurrence.getOwnerEmail(),
        occurrence.getDecimalLatitude(),
        occurrence.getDecimalLongitude(),
        occurrence.getLocality(),
        occurrence.getCountry(),
        occurrence.getStateProvince(),
        occurrence.getCounty(),
        occurrence.getValidationError() == null
            || occurrence.getValidationError().equals("") ? constants.None()
            : occurrence.getValidationError() };
  }

  /**
   * Gets the OccurrenceSummary to display for map infowindow in array of
   * String.
   * 
   *@reutrn String[] {id, species display info, isPublic, validated, vetted,
   *         ownerEmail, DecimalLatitude, DecimalLongitude, locality, country,
   *         state, county, validationError}
   */
  public String[] getMapUserSummary() {
    return new String[] {
        occurrence.getId().toString(),
        getTaxonomic(),
        booleanToString(occurrence.isPublic_()),
        booleanToString(occurrence.isValidated()),
        getReviewedStatus(occurrence.getReviewed()),
        occurrence.getOwnerEmail(),
        occurrence.getDecimalLatitude(),
        occurrence.getDecimalLongitude(),
        occurrence.getLocality(),
        occurrence.getCountry(),
        occurrence.getStateProvince(),
        occurrence.getCounty(),
        occurrence.getValidationError() == null
            || occurrence.getValidationError().equals("") ? constants.None()
            : occurrence.getValidationError() };
  }

  /**
   * Gets Occurrence of this OccurrenceSummary.
   * 
   * @return Occurrence
   */
  public Occurrence getOccurrence() {
    return occurrence;
  }

  public String[] getReviewerSummary() {
    return new String[] {
        occurrence.getId().toString(),
        getTaxonomic(),
        booleanToString(occurrence.isPublic_()),
        booleanToString(occurrence.isValidated()),
       /* MY_REVIEWED,*/
        getReviewedStatus(occurrence.getReviewed()),
        occurrence.getOwnerEmail(),/*
        occurrence.getSharedUsersCSV(),
        occurrence.getValidationError() == null
            || occurrence.getValidationError().equals("") ? constants.None()
            : occurrence.getValidationError()*/
        occurrence.getBasisOfRecord(), 
        occurrence.getInstitutionCode(),
        /*occurrence.getGenus(), 
        occurrence.getSpecificEpithet(),
        occurrence.getInfraspecificEpithet(), */
        occurrence.getStateProvince(),
        occurrence.getCountry(),
        occurrence.getLocality(), 
        occurrence.getCollectingMethod(),
        occurrence.getCollector(),
        occurrence.getAttributes(),
        occurrence.getVerbatimCollectingDate(), 
        occurrence.getVerbatimElevation(),
        occurrence.getYearCollected(),
        occurrence.getRelatedInformation()
        
    };
  }

  /**
   * Gets the OccurrenceSummary in array of String.
   * 
   * @return new String[] {id, species display name, public, validated, vetted,
   *         email, sharedUsersCSV, validationError}
   */
  public String[] getUnauthenticatedSummary() {
    return new String[] {
        occurrence.getId().toString(),
        getTaxonomic(),
        booleanToString(occurrence.isValidated()),
        getReviewedStatus(occurrence.getReviewed()),
        occurrence.getOwnerEmail(),
        occurrence.getSharedUsersCSV(),
        occurrence.getValidationError() == null
            || occurrence.getValidationError().equals("") ? constants.None()
            : occurrence.getValidationError() };
  }

  /**
   * Gets the OccurrenceSummary in array of String.
   * 
   * @return new String[] {id, public, validated, vetted, validationError}
   */
  public String[] getUserSummary() {
    return new String[] {
        occurrence.getId().toString(),
        getTaxonomic(),
        booleanToString(occurrence.isPublic_()),
        booleanToString(occurrence.isValidated()),
        getReviewedStatus(occurrence.getReviewed()),
        occurrence.getOwnerEmail(),
        occurrence.getSharedUsersCSV(),
        occurrence.getValidationError() == null
            || occurrence.getValidationError().equals("") ? constants.None()
            : occurrence.getValidationError() };
  }

  /**
   * Used in hash this OccurrenceSummary Object
   * 
   * @return occurrence id
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int result;
    try {
      result = occurrence.getId();
    } catch (Exception e) {
      result = occurrence.getId().hashCode();
    }
    return result;
  }

  /**
   * convert a boolean to a string repersentation: true -> "Yes" and false ->
   * "No".
   * 
   * @param b boolean
   * @return "Yes" if b is true, "No" if b is false
   */
  private String booleanToString(boolean b) {
    return b ? constants.Yes() : constants.No();
  }

  private String getReviewedStatus(Boolean reviewed) {
    return reviewed == null ? "waiting" : (reviewed ? "pos" : "neg");
  }

  private String getTaxonomic() {
    OccurrenceFieldItem item = getDisplayField(occurrence);
    return item == null ? constants.None() : item.toString();
  }

}
