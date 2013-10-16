/*
 * Copyright 2013 Rebioma
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
package org.rebioma.client.bean.gxt;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.ListView;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
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

  public interface PlaceProperties extends PropertyAccess<Occurrence> {
	 
	    @Path("id")
	    ModelKeyProvider<Occurrence> key();
	 
	    ValueProvider<Occurrence, Integer> id();
	 
	    ValueProvider<Occurrence, String> taxonomic();
	 
	    ValueProvider<Occurrence, Boolean> validated();
	 
	    ValueProvider<Occurrence, Boolean> reviewed();
	    
	    ValueProvider<Occurrence, String> email();
	    
	    ValueProvider<Occurrence, String> decimalLatitude();
	    
	    ValueProvider<Occurrence, String> decimalLongitude();
	    
	    ValueProvider<Occurrence, String> locality();
	    
	    ValueProvider<Occurrence, String> country();
	    
	    ValueProvider<Occurrence, String> stateProvince();
	    
	    ValueProvider<Occurrence, String> county();
	    
	    ValueProvider<Occurrence, String> validationError();
	    
	    ValueProvider<Occurrence, Boolean> public_();
	    
	    ValueProvider<Occurrence, String> basisOfRecord();
	    
	    ValueProvider<Occurrence, String> institutionCode();
	    
	    ValueProvider<Occurrence, String> collectingMethod();
	    
	    ValueProvider<Occurrence, String> collector();
	    
	    ValueProvider<Occurrence, String> attributes();
	    
	    ValueProvider<Occurrence, String> verbatimCollectingDate();
	    
	    ValueProvider<Occurrence, String> verbatimElevation();
	    
	    ValueProvider<Occurrence, String> yearCollected();
	    
	    ValueProvider<Occurrence, String> relatedInformation();
	    
	    ValueProvider<Occurrence, String> sharedUsersCSV();
	    
  }
  
  interface TaxonomicTemplate extends XTemplates {
	    @XTemplate("{v1}</br><b>{v2}</b>")
	    SafeHtml render(String v1, String v2);
  }
	
  interface ImageTemplate extends XTemplates {
	    @XTemplate("<img src='{src}'/>")
	    SafeHtml render(String src);
  }
	
  private static TaxonomicTemplate taxonomicTemplate = GWT.create(TaxonomicTemplate.class);
  
  private static ImageTemplate imageTemplate = GWT.create(ImageTemplate.class);
  
  public static final PlaceProperties properties = GWT.create(PlaceProperties.class);
  private static IdentityValueProvider<Occurrence> identity = new IdentityValueProvider<Occurrence>();
  public static CheckBoxSelectionModel<Occurrence> sm = new CheckBoxSelectionModel<Occurrence>(identity);
  
  public static final String GUEST_REQUIRED_HEADERS[] = { " ", "ReBioMa Id",
      constants.Taxonomy(), constants.Validated(), constants.Reviewed(),
      constants.Owner(), constants.Collaborators(), constants.ValidationError() };
  
  public static final List<ColumnConfig<Occurrence, ?>> getGuestColumnModel(){
	  ColumnConfig<Occurrence, Integer> cc1 = new ColumnConfig<Occurrence, Integer>(properties.id(), 80, "ReBioMa Id");
	  
	  ColumnConfig<Occurrence, String> cc2 = new ColumnConfig<Occurrence, String>(properties.taxonomic(), 220, constants.Taxonomy());
	  cc2.setCell(taxonomicCell);
	 
	  ColumnConfig<Occurrence, Boolean> cc3 = new ColumnConfig<Occurrence, Boolean>(properties.validated(), 80, constants.Validated());
	  cc3.setCell(imageCell);
	  cc3.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, Boolean> cc4 = new ColumnConfig<Occurrence, Boolean>(properties.reviewed(), 80, constants.Reviewed());
	  cc4.setCell(reviewedCell);
	  cc4.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  	
	  ColumnConfig<Occurrence, String> cc5 = new ColumnConfig<Occurrence, String>(properties.email(), 160, constants.Owner());
	  cc5.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc6 = new ColumnConfig<Occurrence, String>(properties.sharedUsersCSV(), 170, constants.Collaborators());
	  cc6.setCell(collaboratorsCell);
	  
	  ColumnConfig<Occurrence, String> cc7 = new ColumnConfig<Occurrence, String>(properties.validationError(), 170, constants.ValidationError());
	  cc7.setCell(cell);
	  
	  List<ColumnConfig<Occurrence, ?>> l = new ArrayList<ColumnConfig<Occurrence, ?>>();
	  l.add(sm.getColumn());
	  l.add(cc1);
	  l.add(cc2);
	  l.add(cc3);
	  l.add(cc4);
	  l.add(cc5);
	  l.add(cc6);
	  l.add(cc7);
	  return l;
  }
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
  public static final List<ColumnConfig<Occurrence, ?>> getUserColumnModel(){
	  ColumnConfig<Occurrence, Integer> cc1 = new ColumnConfig<Occurrence, Integer>(properties.id(), 80, "ReBioMa Id");
	  	
	  ColumnConfig<Occurrence, String> cc2 = new ColumnConfig<Occurrence, String>(properties.taxonomic(), 220, constants.Taxonomy());
	  cc2.setCell(taxonomicCell);
	  
	  ColumnConfig<Occurrence, Boolean> cc3 = new ColumnConfig<Occurrence, Boolean>(properties.public_(), 80, constants.Public());
	  cc3.setCell(imageCell);
	  cc3.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, Boolean> cc4 = new ColumnConfig<Occurrence, Boolean>(properties.validated(), 80, constants.Validated());
	  cc4.setCell(imageCell);
	  cc4.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  	 
	  ColumnConfig<Occurrence, Boolean> cc5 = new ColumnConfig<Occurrence, Boolean>(properties.reviewed(), 80, constants.Reviewed());
	  cc5.setCell(reviewedCell);
	  cc5.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, String> cc6 = new ColumnConfig<Occurrence, String>(properties.email(), 160, constants.Owner());
//	  cc6.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc7 = new ColumnConfig<Occurrence, String>(properties.sharedUsersCSV(), 170, constants.Collaborators());
	  cc7.setCell(collaboratorsCell);
	  
	  ColumnConfig<Occurrence, String> cc8 = new ColumnConfig<Occurrence, String>(properties.validationError(), 180, constants.ValidationError());
	  cc8.setCell(cell);
	  
	  List<ColumnConfig<Occurrence, ?>> l = new ArrayList<ColumnConfig<Occurrence, ?>>();
	  l.add(sm.getColumn());
	  l.add(cc1);
	  l.add(cc2);
	  l.add(cc3);
	  l.add(cc4);
	  l.add(cc5);
	  l.add(cc6);
	  l.add(cc7);
	  l.add(cc8);
	  return l;
  }
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
  public static final List<ColumnConfig<Occurrence, ?>> getReviewerColumnModel(){
	  ColumnConfig<Occurrence, Integer> cc1 = new ColumnConfig<Occurrence, Integer>(properties.id(), 80, "ReBioMa Id");
	  
	  ColumnConfig<Occurrence, String> cc2 = new ColumnConfig<Occurrence, String>(properties.taxonomic(), 220, constants.Taxonomy());
	  cc2.setCell(taxonomicCell);
	  
	  ColumnConfig<Occurrence, Boolean> cc3 = new ColumnConfig<Occurrence, Boolean>(properties.public_(), 60, constants.Public());
	  cc3.setCell(imageCell);
	  cc3.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, Boolean> cc4 = new ColumnConfig<Occurrence, Boolean>(properties.validated(), 60, constants.Validated());
	  cc4.setCell(imageCell);
	  cc4.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, Boolean> cc5 = new ColumnConfig<Occurrence, Boolean>(properties.reviewed(), 60, constants.Reviewed());
	  cc5.setCell(reviewedCell);
	  cc5.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, String> cc6 = new ColumnConfig<Occurrence, String>(properties.email(), 160, constants.Owner());
	  cc6.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc7 = new ColumnConfig<Occurrence, String>(properties.basisOfRecord(), 160, "Basis of record");
	  cc7.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc8 = new ColumnConfig<Occurrence, String>(properties.institutionCode(), 160, "Institution code");
	  cc8.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc9 = new ColumnConfig<Occurrence, String>(properties.stateProvince(), 100, "StateProvince");
	  cc9.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc10 = new ColumnConfig<Occurrence, String>(properties.county(), 100, "County");
	  cc10.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc11 = new ColumnConfig<Occurrence, String>(properties.locality(), 100, "Locality");
	  cc11.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc12 = new ColumnConfig<Occurrence, String>(properties.collectingMethod(), 100, "Collecting method");
	  cc12.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc13 = new ColumnConfig<Occurrence, String>(properties.collector(), 180, "Collector");
	  cc13.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc14 = new ColumnConfig<Occurrence, String>(properties.attributes(), 100, "Attributes");
	  cc14.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc15 = new ColumnConfig<Occurrence, String>(properties.verbatimCollectingDate(), 100, "Verbatim collecting Date");
	  cc15.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc16 = new ColumnConfig<Occurrence, String>(properties.verbatimElevation(), 100, "Verbatim elevation");
	  cc16.setCell(cell);
	  
	  ColumnConfig<Occurrence, String> cc17 = new ColumnConfig<Occurrence, String>(properties.yearCollected(), 80, constants.YearCollected().replace(":", ""));
	  cc17.setCell(cell);
	  cc17.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	  
	  ColumnConfig<Occurrence, String> cc18 = new ColumnConfig<Occurrence, String>(properties.relatedInformation(), 120, "Related information");
	  cc18.setCell(cell);
	  
	  List<ColumnConfig<Occurrence, ?>> l = new ArrayList<ColumnConfig<Occurrence, ?>>();
	  sm.getColumn().setWidth(20);
	  l.add(sm.getColumn());
	  l.add(cc1);
	  l.add(cc2);
	  l.add(cc3);
	  l.add(cc4);
	  l.add(cc5);
	  l.add(cc6);
	  l.add(cc7);
	  l.add(cc8);
	  l.add(cc9);
	  l.add(cc10);
	  l.add(cc11);
	  l.add(cc12);
	  l.add(cc13);
	  l.add(cc14);
	  l.add(cc15);
	  l.add(cc16);
	  l.add(cc17);
	  l.add(cc18);
	  return l;
  }
  public static final String MY_REVIEWED = "my reviewed";

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

//  /**
//   * Gets the OccurrenceSummary to display for map infowindow in array of
//   * String.
//   * 
//   * @return new String[] {id, species display info, validated, vetted,
//   *         ownerEmail, DecimalLatitude, DecimalLongitude, locality, country,
//   *         state, county, validationError}
//   */
//  public String[] getMapUnauthenticatedSummary() {
//    return new String[] {
//        occurrence.getId().toString(),
//        getTaxonomic(),
//        booleanToString(occurrence.isValidated()),
//        getReviewedStatus(occurrence.getReviewed()),
//        occurrence.getOwnerEmail(),
//        occurrence.getDecimalLatitude(),
//        occurrence.getDecimalLongitude(),
//        occurrence.getLocality(),
//        occurrence.getCountry(),
//        occurrence.getStateProvince(),
//        occurrence.getCounty(),
//        occurrence.getValidationError() == null
//            || occurrence.getValidationError().equals("") ? constants.None()
//            : occurrence.getValidationError() };
//  }

//  /**
//   * Gets the OccurrenceSummary to display for map infowindow in array of
//   * String.
//   * 
//   *@reutrn String[] {id, species display info, isPublic, validated, vetted,
//   *         ownerEmail, DecimalLatitude, DecimalLongitude, locality, country,
//   *         state, county, validationError}
//   */
//  public String[] getMapUserSummary() {
//    return new String[] {
//        occurrence.getId().toString(),
//        getTaxonomic(),
//        booleanToString(occurrence.isPublic_()),
//        booleanToString(occurrence.isValidated()),
//        getReviewedStatus(occurrence.getReviewed()),
//        occurrence.getOwnerEmail(),
//        occurrence.getDecimalLatitude(),
//        occurrence.getDecimalLongitude(),
//        occurrence.getLocality(),
//        occurrence.getCountry(),
//        occurrence.getStateProvince(),
//        occurrence.getCounty(),
//        occurrence.getValidationError() == null
//            || occurrence.getValidationError().equals("") ? constants.None()
//            : occurrence.getValidationError() };
//  }

  /**
   * Gets Occurrence of this OccurrenceSummary.
   * 
   * @return Occurrence
   */
  public Occurrence getOccurrence() {
    return occurrence;
  }

//  public String[] getReviewerSummary() {
//    return new String[] {
//        occurrence.getId().toString(),
//        getTaxonomic(),
//        booleanToString(occurrence.isPublic_()),
//        booleanToString(occurrence.isValidated()),
//       /* MY_REVIEWED,*/
//        getReviewedStatus(occurrence.getReviewed()),
//        occurrence.getOwnerEmail(),/*
//        occurrence.getSharedUsersCSV(),
//        occurrence.getValidationError() == null
//            || occurrence.getValidationError().equals("") ? constants.None()
//            : occurrence.getValidationError()*/
//        occurrence.getBasisOfRecord(), 
//        occurrence.getInstitutionCode(),
//        /*occurrence.getGenus(), 
//        occurrence.getSpecificEpithet(),
//        occurrence.getInfraspecificEpithet(), */
//        occurrence.getStateProvince(),
//        occurrence.getCountry(),
//        occurrence.getLocality(), 
//        occurrence.getCollectingMethod(),
//        occurrence.getCollector(),
//        occurrence.getAttributes(),
//        occurrence.getVerbatimCollectingDate(), 
//        occurrence.getVerbatimElevation(),
//        occurrence.getYearCollected(),
//        occurrence.getRelatedInformation()
//        
//    };
//  }

  /**
   * Gets the OccurrenceSummary in array of String.
   * 
   * @return new String[] {id, species display name, public, validated, vetted,
   *         email, sharedUsersCSV, validationError}
   */
//  public String[] getUnauthenticatedSummary() {
//    return new String[] {
//        occurrence.getId().toString(),
//        getTaxonomic(),
//        booleanToString(occurrence.isValidated()),
//        getReviewedStatus(occurrence.getReviewed()),
//        occurrence.getOwnerEmail(),
//        occurrence.getSharedUsersCSV(),
//        occurrence.getValidationError() == null
//            || occurrence.getValidationError().equals("") ? constants.None()
//            : occurrence.getValidationError() };
//  }

  /**
   * Gets the OccurrenceSummary in array of String.
   * 
   * @return new String[] {id, public, validated, vetted, validationError}
   */
//  public String[] getUserSummary() {
//    return new String[] {
//        occurrence.getId().toString(),
//        getTaxonomic(),
//        booleanToString(occurrence.isPublic_()),
//        booleanToString(occurrence.isValidated()),
//        getReviewedStatus(occurrence.getReviewed()),
//        occurrence.getOwnerEmail(),
//        occurrence.getSharedUsersCSV(),
//        occurrence.getValidationError() == null
//            || occurrence.getValidationError().equals("") ? constants.None()
//            : occurrence.getValidationError() };
//  }

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
  private static String booleanToString(boolean b) {
    return b ? constants.Yes() : constants.No();
  }

  private static String getReviewedStatus(Boolean reviewed) {
    return reviewed == null ? "waiting" : (reviewed ? "pos" : "neg");
  }

  private static AbstractCell taxonomicCell = new AbstractCell<String>() {
		 
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
    	  if (value == null || value.equals("")) {
    		  value = "----";
    	  } else if (value.equalsIgnoreCase("none")) {
    		  value = "----";
    	  }
    	  String s[] = (value.trim() + " - ").split(" - ");
    	  sb.append(taxonomicTemplate.render(s[0], s[1]));
      }
  };
  
  private static AbstractCell reviewedCell = new AbstractCell<Boolean>() {
		 
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Boolean val, SafeHtmlBuilder sb) {
    	  String src = ListView.NULL_URL;
    	  if(val == null)
    		  src = ListView.WAITING_IMG_URL;
    	  else if((boolean)val)
    		  src = ListView.THUMB_UP_URL;
    	  else 
    		  src = ListView.THUMB_DOWN_URL;
    	  sb.append(imageTemplate.render(src));
      }
  };
  
  private static AbstractCell imageCell = new AbstractCell<Boolean>() {
		 
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Boolean val, SafeHtmlBuilder sb) {
    	  String src = ListView.NULL_URL;
    	  boolean value = (boolean)val;
    	  if (value) {
            src = ListView.CHECK_IMG_URL;
          } else {
            src = ListView.X_IMG_URL;
          }
    	  sb.append(imageTemplate.render(src));
      }
  };
  
  private static AbstractCell collaboratorsCell = new AbstractCell<String>() {
		 
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, String val, SafeHtmlBuilder sb) {
    	  if (val == null || val.trim().equals("") || val.equalsIgnoreCase("none")) {
              val = "----";
    	  }
    	  sb.append(SafeHtmlUtils.fromString(val.replaceAll(",", ", ")));
      }
  };
  
  private static AbstractCell cell = new AbstractCell<String>() {
		 
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, String val, SafeHtmlBuilder sb) {
    	  if (val == null || val.trim().equals("") || val.equalsIgnoreCase("none")) {
              val = "----";
    	  }
    	  sb.append(SafeHtmlUtils.fromString(val));
      }
  };
  
}
