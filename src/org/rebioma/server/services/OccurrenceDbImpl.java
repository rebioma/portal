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

import static org.hibernate.criterion.Example.create;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import java_cup.internal_error;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.OrderKey;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;
import org.rebioma.server.services.QueryFilter.InvalidFilter;
import org.rebioma.server.services.QueryFilter.Operator;
import org.rebioma.server.upload.Traitement;
import org.rebioma.server.util.HibernateUtil;
import org.rebioma.server.util.ManagedSession;
import org.rebioma.server.util.RecordReviewUtil;
import org.rebioma.server.util.StringUtil;

/**
 * Default implementation of {@link OccurrenceDb}. Uses Hibernate for database
 * operations.
 * 
 */
public class OccurrenceDbImpl implements OccurrenceDb {

  /**
   * A simple helper class that parses a string filter into it's component parts
   * (column, operator, value). Provides methods for testing the filter
   * operation.
   */
  protected static class OccurrenceFilter extends QueryFilter {

    public OccurrenceFilter(String filter) throws InvalidFilter {
      super(filter, Occurrence.class);
    }

    public OccurrenceFilter(String col, Operator op, Object value) throws InvalidFilter {
      super(col, op, value, Occurrence.class);
    }

    @Override
    public String getPropertyName(String property) {
      if (property.equalsIgnoreCase("validated")) {
        property = "validated";
      } else if (property.equalsIgnoreCase("quickSearch")) {
        property = "quickSearch";
      }

      // A private column doesn't exist, so we map it to public here:
      else if (property.equalsIgnoreCase("private") || property.equalsIgnoreCase("public")
          || property.equalsIgnoreCase("public_")) {
        property = "public_";
      } else if (property.equalsIgnoreCase("ownerName")) {
        property = "ownerEmail";
      } else if (property.equalsIgnoreCase("reviewed")) {
        property = "reviewed";
      } else if (property.equalsIgnoreCase("userReviewed")) {
        property = "userReviewed";
      } else if (property.equalsIgnoreCase("ownerEmail")) {
        property = "ownerEmail";
      } else if (property.equalsIgnoreCase("timeCreated")) {
        property = "timeCreated";
      } else if (property.equalsIgnoreCase("lastUpdated")) {
        property = "lastUpdated";
      } else if (property.equalsIgnoreCase("vettable")) {
        property = "vettable";
      } else if (property.equalsIgnoreCase("vetted")) {
        property = "vetted";
      } else if (property.equalsIgnoreCase("tapirAccessible")) {
        property = "tapirAccessible";
      } else if (property.equalsIgnoreCase("acceptedSpecies")) {
        property = "acceptedSpecies";
      } else if (property.equalsIgnoreCase("acceptedClass")) {
        property = "acceptedClass";
      } else if (property.equalsIgnoreCase("acceptedFamily")) {
        property = "acceptedFamily";
      } else if (property.equalsIgnoreCase("AcceptedGenus")) {
        property = "acceptedGenus";
      } else if (property.equalsIgnoreCase("AcceptedKingdom")) {
        property = "acceptedKingdom";
      } else if (property.equalsIgnoreCase("AcceptedNomenclaturalCode")) {
        property = "acceptedNomenclaturalCode";
      } else if (property.equalsIgnoreCase("AcceptedOrder")) {
        property = "acceptedOrder";
      } else if (property.equalsIgnoreCase("AcceptedPhylum")) {
        property = "acceptedPhylum";
      } else if (property.equalsIgnoreCase("AcceptedSpecificEpithet")) {
        property = "acceptedSpecificEpithet";
      } else if (property.equalsIgnoreCase("AcceptedSubfamily")) {
        property = "acceptedSubfamily";
      } else if (property.equalsIgnoreCase("AcceptedSubgenus")) {
        property = "acceptedSubgenus";
      } else if (property.equalsIgnoreCase("AcceptedSuborder")) {
        property = "acceptedSuborder";
      } else if (property.equalsIgnoreCase("attributes")) {
        property = "attributes";
      } else if (property.equalsIgnoreCase("authorYearOfScientificName")) {
        property = "authorYearOfScientificName";
      } else if (property.equalsIgnoreCase("basisOfRecord")) {
        property = "basisOfRecord";
      } else if (property.equalsIgnoreCase("catalogNumber")) {
        property = "catalogNumber";
      } else if (property.equalsIgnoreCase("catalogNumberNumeric")) {
        property = "catalogNumberNumeric";
      } else if (property.equalsIgnoreCase("class_") || property.equalsIgnoreCase("class")) {
        property = "class_";
      } else if (property.equalsIgnoreCase("collectingMethod")) {
        property = "collectingMethod";
      } else if (property.equalsIgnoreCase("collectionCode")) {
        property = "collectionCode";
      } else if (property.equalsIgnoreCase("collector")) {
        property = "collector";
      } else if (property.equalsIgnoreCase("collectorNumber")) {
        property = "collectorNumber";
      } else if (property.equalsIgnoreCase("continent")) {
        property = "continent";
      } else if (property.equalsIgnoreCase("coordinateUncertaintyInMeters")) {
        property = "coordinateUncertaintyInMeters";
      } else if (property.equalsIgnoreCase("country")) {
        property = "country";
      } else if (property.equalsIgnoreCase("dateIdentified")) {
        property = "dateIdentified";
      } else if (property.equalsIgnoreCase("dateLastModified")) {
        property = "dateLastModified";
      } else if (property.equalsIgnoreCase("dayCollected")) {
        property = "dayCollected";
      } else if (property.equalsIgnoreCase("dayOfYear")) {
        property = "dayOfYear";
      } else if (property.equalsIgnoreCase("decimalLatitude")) {
        property = "decimalLatitude";
      } else if (property.equalsIgnoreCase("decimalLongitude")) {
        property = "decimalLongitude";
      } else if (property.equalsIgnoreCase("disposition")) {
        property = "disposition";
      } else if (property.equalsIgnoreCase("earliestDateCollected")) {
        property = "earliestDateCollected";
      } else if (property.equalsIgnoreCase("family")) {
        property = "family";
      } else if (property.equalsIgnoreCase("fieldNotes")) {
        property = "fieldNotes";
      } else if (property.equalsIgnoreCase("fieldNumber")) {
        property = "fieldNumber";
      } else if (property.equalsIgnoreCase("footprintSpatialFit")) {
        property = "footprintSpatialFit";
      } else if (property.equalsIgnoreCase("footprintWkt")) {
        property = "footprintWkt";
      } else if (property.equalsIgnoreCase("genBankNumber")) {
        property = "genBankNumber";
      } else if (property.equalsIgnoreCase("genus")) {
        property = "genus";
      } else if (property.equalsIgnoreCase("geodeticDatum")) {
        property = "geodeticDatum";
      } else if (property.equalsIgnoreCase("georeferenceProtocol")) {
        property = "georeferenceProtocol";
      } else if (property.equalsIgnoreCase("georeferenceRemarks")) {
        property = "georeferenceRemarks";
      } else if (property.equalsIgnoreCase("georeferenceSources")) {
        property = "georeferenceSources";
      } else if (property.equalsIgnoreCase("georeferenceVerificationStatus")) {
        property = "georeferenceVerificationStatus";
      } else if (property.equalsIgnoreCase("globalUniqueIdentifier")) {
        property = "globalUniqueIdentifier";
      } else if (property.equalsIgnoreCase("higherGeography")) {
        property = "higherGeography";
      } else if (property.equalsIgnoreCase("higherTaxon")) {
        property = "higherTaxon";
      } else if (property.equalsIgnoreCase("identificationQualifer")) {
        property = "identificationQualifer";
      } else if (property.equalsIgnoreCase("identifiedBy")) {
        property = "identifiedBy";
      } else if (property.equalsIgnoreCase("imageUrl")) {
        property = "imageUrl";
      } else if (property.equalsIgnoreCase("individualCount")) {
        property = "individualCount";
      } else if (property.equalsIgnoreCase("informationWithheld")) {
        property = "informationWithheld";
      } else if (property.equalsIgnoreCase("infraspecificEpithet")) {
        property = "infraspecificEpithet";
      } else if (property.equalsIgnoreCase("infraspecificRank")) {
        property = "infraspecificRank";
      } else if (property.equalsIgnoreCase("institutionCode")) {
        property = "institutionCode";
      } else if (property.equalsIgnoreCase("islandGroup")) {
        property = "islandGroup";
      } else if (property.equalsIgnoreCase("island")) {
        property = "island";
      } else if (property.equalsIgnoreCase("kingdom")) {
        property = "kingdom";
      } else if (property.equalsIgnoreCase("latestDateCollected")) {
        property = "latestDateCollected";
      } else if (property.equalsIgnoreCase("lifeStage")) {
        property = "lifeStage";
      } else if (property.equalsIgnoreCase("maximumDepthInMeters")) {
        property = "maximumDepthInMeters";
      } else if (property.equalsIgnoreCase("minimumElevationInMeters")) {
        property = "minimumElevationInMeters";
      } else if (property.equalsIgnoreCase("minimumDepthInMeters")) {
        property = "minimumDepthInMeters";
      } else if (property.equalsIgnoreCase("maximumElevationInMeters")) {
        property = "maximumElevationInMeters";
      } else if (property.equalsIgnoreCase("monthCollected")) {
        property = "monthCollected";
      } else if (property.equalsIgnoreCase("nomenclaturalCode")) {
        property = "nomenclaturalCode";
      } else if (property.equalsIgnoreCase("order")) {
        property = "order_";
      } else if (property.equalsIgnoreCase("otherCatalogNumbers")) {
        property = "otherCatalogNumbers";
      } else if (property.equalsIgnoreCase("phylum")) {
        property = "phylum";
      } else if (property.equalsIgnoreCase("pointRadiusSpatialFit")) {
        property = "pointRadiusSpatialFit";
      } else if (property.equalsIgnoreCase("preparations")) {
        property = "preparations";
      } else if (property.equalsIgnoreCase("privatisland")) {
        property = "privatisland";
      } else if (property.equalsIgnoreCase("relatedCatalogedItems")) {
        property = "relatedCatalogedItems";
      } else if (property.equalsIgnoreCase("relatedInformation")) {
        property = "relatedInformation";
      } else if (property.equalsIgnoreCase("remarks")) {
        property = "remarks";
      } else if (property.equalsIgnoreCase("scientificName")) {
        property = "scientificName";
      } else if (property.equalsIgnoreCase("sex")) {
        property = "sex";
      } else if (property.equalsIgnoreCase("specificEpithet")) {
        property = "specificEpithet";
      } else if (property.equalsIgnoreCase("stateProvince")) {
        property = "stateProvince";
      } else if (property.equalsIgnoreCase("typeStatus")) {
        property = "typeStatus";
      } else if (property.equalsIgnoreCase("validDistributionFlag")) {
        property = "validDistributionFlag";
      } else if (property.equalsIgnoreCase("validationError")) {
        property = "validationError";
      } else if (property.equalsIgnoreCase("verbatimCollectingDate")) {
        property = "verbatimCollectingDate";
      } else if (property.equalsIgnoreCase("verbatimCoordinateSystem")) {
        property = "verbatimCoordinateSystem";
      } else if (property.equalsIgnoreCase("verbatimCoordinates")) {
        property = "verbatimCoordinates";
      } else if (property.equalsIgnoreCase("verbatimDepth")) {
        property = "verbatimDepth";
      } else if (property.equalsIgnoreCase("verbatimElevation")) {
        property = "verbatimElevation";
      } else if (property.equalsIgnoreCase("verbatimLatitude")) {
        property = "verbatimLatitude";
      } else if (property.equalsIgnoreCase("verbatimLongitude")) {
        property = "verbatimLongitude";
      } else if (property.equalsIgnoreCase("verbatimSpecies")) {
        property = "verbatimSpecies";
      } else if (property.equalsIgnoreCase("waterBody")) {
        property = "waterBody";
      } else if (property.equalsIgnoreCase("yearCollected")) {
        property = "yearCollected";
      } else if (property.equalsIgnoreCase("Locality")) {
        property = "locality";
      } else if (property.equalsIgnoreCase("County")) {
        property = "county";
      } else if (property.equalsIgnoreCase("id")) {
        property = "id";
      } else if (property.equalsIgnoreCase("emailVisible")) {
        property = "emailVisible";
      } else if (property.equalsIgnoreCase("sharedUsersCSV")) {
        property = "sharedUsersCSV";
      } else if (property.equalsIgnoreCase("sharedUsers")) {
        property = "sharedUsers";
      } else if (property.equalsIgnoreCase("unsharedUsers")) {
        property = "unsharedUsers";
      } else {
        return null;
      }
      return property;
    }
  }

  private static QueryFilter dummyFilter = null;

  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(OccurrenceDbImpl.class);

  private static final String VALID_BASIS_OF_RECORDS[] = new String[] { "FossilSpecimen",
      "HumanObservation", "LivingSpecimen", "MachineObservation", "MovingImage",
      "PreservedSpecimen", "SoundRecording", "StillImage", "OtherSpecimen" };

  public static String getOccurrencePropertyName(String property) {
    if (dummyFilter == null) {
      try {
        dummyFilter = new OccurrenceFilter("id = 0");
      } catch (InvalidFilter e) {
        e.printStackTrace();
        return null;
      }
    }
    return dummyFilter.getPropertyName(property);

  }

  public static void main(String args[]) {
	  Scanner scan = null;
	  int t = 0;
	  int f = 0;
	  int j = 1;
	  String ids ="\n";
	  try {
		scan = new Scanner(new File("D:\\occurrenceidoc.log"));
		while(scan.hasNext()){
			int id = Integer.valueOf(scan.nextLine());
			System.out.println(id);
			if(new OccurrenceDbImpl().checkForReviewedChanged(id)){
				t++;
				ids+=id+",";
			}
			else f++;
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  System.out.println(t + " --"+j+"-- " + f + ids);
	//System.out.println(new OccurrenceDbImpl().checkForReviewedChanged(115267));  
	  
    //OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
    /*OccurrenceQuery query = new OccurrenceQuery(0, OccurrenceQuery.UNLIMITED);
    try {
      for (Integer id : occurrenceDb.findOccurrenceIdsByQuery(query, (User) null)) {
        occurrenceDb.checkForReviewedChanged(id);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
    //Set<AttributeValue> attributeValues = new HashSet<OccurrenceDb.AttributeValue>();
    //occurrenceDb.findByAttributeValues(attributeValues);
  }

  UserDb userDb = DBFactory.getUserDb();

  RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();

  TaxonomicReviewerDb taxonomicReviewerDb = DBFactory.getTaxonomicReviewerDb();
  List<TaxonomicReviewer> taxonomiKRB =taxonomicReviewerDb.findAll();
  public OccurrenceDbImpl() {
  }

  /**
   * @see org.rebioma.server.services.OccurrenceDb#assignReviewer(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public int assignReviewer(String userEmail, String taxoFieldName, String taxoFieldValue, boolean isMarine, boolean isTerrestrial)
      throws OccurrenceServiceException {
    // Session session = HibernateUtil.getCurrentSession();
    // boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    int assignOccsCount = 0;
    try {
      User user = userDb.findByEmail(userEmail);
      AttributeValue attributeValue = new AttributeValue(taxoFieldName, taxoFieldValue);
      if (user != null && attributeValue.isValid()) {
        // check to see whether this assignment is already existed
        if (!taxonomicReviewerDb.isAssignmentExisted(user.getId(), attributeValue.getAttribute(),
            attributeValue.getValue())) {
          TaxonomicReviewer taxoReviewer = new TaxonomicReviewer(user.getId(),
              attributeValue.getAttribute(), attributeValue.getValue(), isMarine, isTerrestrial);
          // persist the assignment to the database for future occurrence
          // references.
          taxoReviewer = taxonomicReviewerDb.save(taxoReviewer);
          // find and assign all existence occurrences that match the assigning
          // taxonomic field and value pair
          // List<Occurrence> occurrences =
          // findByAttributeValue(attributeValue);
          List<Integer> ids = findValidatedOccIdsByAttributeValue(attributeValue, isMarine, isTerrestrial);
          System.out.println("assigning reviewer to " + ids.size() + " matches occurrences");
          int idsCount = ids.size();
          Set<Integer> percentages = new HashSet<Integer>();
          for (int i = 0; i < idsCount; i++) {
            int id = ids.get(i);
            RecordReview recordReview = new RecordReview();
            recordReview.setOccurrenceId(id);
            recordReview.setUserId(user.getId());
            recordReview = recordReviewDb.save(recordReview);
            if (recordReview != null) {
              resetOccurrenceReviewedStatus(id);
              // occurrence.setReviewed(null);
              // // attachClean(occurrence);
              // attachDirty(occurrence);
              assignOccsCount++;
              // System.out.println(assignOccsCount);
            }
            int percentage = (int) ((i / (idsCount * 1.0)) * 100);
            if (percentage % 5 == 0 && percentages.add(percentage)) {
              System.out.print(percentage + "%...");
            }
          }
          System.out.println("\ndone assigned " + ids.size() + " occurrences to " + userEmail
              + " and " + assignOccsCount + " was assigned");
        } else {
        	List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByUser(user.getId());
        	Map<Integer, Integer> recordIds = new HashMap<Integer, Integer>();
        	for(RecordReview recordReview : recordReviews){
        		recordIds.put(recordReview.getOccurrenceId(), recordReview.getOccurrenceId());
        	}
//        	TaxonomicReviewer taxoReviewer = new TaxonomicReviewer(user.getId(),
//                    attributeValue.getAttribute(), attributeValue.getValue(), isMarine, isTerrestrial);
//        	taxoReviewer = taxonomicReviewerDb.save(taxoReviewer);
        	List<Integer> ids = findValidatedOccIdsByAttributeValue(attributeValue, isMarine, isTerrestrial);
        	List<Integer> newIds = new ArrayList<Integer>();
        	for(Integer i: ids){
        		if(recordIds.get(i)==null)newIds.add(i);
        	}
        	System.out.println("assigning reviewer to " + newIds.size() + " matches occurrences");
        	int idsCount = newIds.size();
        	
        	Set<Integer> percentages = new HashSet<Integer>();
        	for (int i = 0; i < idsCount; i++) {
        		int id = newIds.get(i);
        		RecordReview recordReview = new RecordReview();
        		recordReview.setOccurrenceId(id);
        		recordReview.setUserId(user.getId());
        		recordReview = recordReviewDb.save(recordReview);
        		if (recordReview != null) {
        			resetOccurrenceReviewedStatus(id);
        			assignOccsCount++;
        		}
        		int percentage = (int) ((i / (idsCount * 1.0)) * 100);
        		if (percentage % 5 == 0 && percentages.add(percentage)) {
        			System.out.print(percentage + "%...");
        		}
        	}
        	System.out.println("\ndone assigned " + newIds.size() + " occurrences to " + userEmail
        			+ " and " + assignOccsCount + " was assigned");
        }
      }
      // if (isFirstTransaction) {
      // HibernateUtil.commitCurrentTransaction();
      // }
      return assignOccsCount;
    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      e.printStackTrace();
      throw new OccurrenceServiceException("unable to assign " + taxoFieldName + " with value "
          + taxoFieldValue + " for user" + userEmail);
    }
  }

  public void attachClean(Session session, Occurrence instance) {
    log.debug("attaching clean Occurrence instance");
    try {
//      ManagedSession.createNewSessionAndTransaction().lock(instance, LockMode.NONE);
      session.lock(instance, LockMode.NONE);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(Occurrence instance) {
	  try {
		  //Session session = HibernateUtil.getCurrentSession();
		  //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
		  Session session = ManagedSession.createNewSessionAndTransaction();
		  attachDirty(instance, session);
		  //if (isFirstTransaction) {
		  //  HibernateUtil.commitCurrentTransaction();
		  //}
		  ManagedSession.commitTransaction(session);
	  } catch (RuntimeException re) {
		  log.error("attach failed", re);
		  //HibernateUtil.rollbackTransaction();
		  throw re;
	  }
  }

  	public void attachDirty(Occurrence instance, Session session) {
  		log.debug("attaching dirty Occurrence instance");
  		instance.setLastUpdated((new Timestamp(System.currentTimeMillis())).toString());
  		boolean newOccurrence = instance.getId() == null;
  		session.saveOrUpdate(instance);
  		if (newOccurrence) {
  			assignReviewRecords(instance);
  		}
  		log.debug("attach successful");
  	}

   /**
   * If an updated {@link Occurrence} contains vetted = true, make sure it is
   * validated otherwise set it back to false.
   */
  public void attachDirty(Set<Occurrence> instances, Traitement traitement, List<RecordReview> rcdrv,
		  boolean clearReview, boolean isSA) {
    log.debug("attaching dirty Occurrence instances");
    Occurrence ref = null;
    Date date = new Date();
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      int cent = instances.size();
      int i = 0;
      for (Occurrence instance : instances) {
    	  traitement.setTraitement("Attach dirty Occurrences ", cent*1024, ++i*1024);
    	  if(traitement.getCancel()){
    		  log.debug("attaching dirty Occurrence instances");
    		  ManagedSession.rollbackTransaction(session);//HibernateUtil.rollbackTransaction();
    		  return;
    	  }
    	  boolean newOccurrence = instance.getId() == null;
    	  ref = instance;
    	  // {WD
    	  if(!isSA)
    		  instance.setLastUpdated((new Timestamp(System.currentTimeMillis())).toString());
    	  if(isSA && clearReview) {
    		  instance.setReviewed(null);
    	  }
    	  //}
    	  try{
    		  instance.setDecimalLatitude(instance.getDecimalLatitude().replace(',','.'));
    		  instance.setDecimalLongitude(instance.getDecimalLongitude().replace(',','.'));
    	  }catch(Exception e){}
    		  // if (instance.isVetted()) {
    	  // if (!instance.isValidated()) {
    	  // instance.setVetted(false);
    	  // }
    	  // }
    	  session.saveOrUpdate(instance);
    	  if (newOccurrence) {
    		  // on vérifie si l'occ est valide et est trbData
    		  if((instance.isValidated()==null || instance.isValidated())&&assignAndReviewRecords(instance, date, session)) {
    			  instance.setReviewed(true);
    			  session.saveOrUpdate(instance);
    		  }
    		  assignReviewRecords(instance, session);
    	  } else {
        			
    		  if(isSA) {
        		if(clearReview) {
        			List<RecordReview> recordReviews = recordReviewDb.findByProperty(instance.getId(), rcdrv);        
        			for (RecordReview recordReview : recordReviews) {
        				recordReview.setReviewed(null);
        				recordReview.setReviewedDate(null);
        				session.update(recordReview);
        			}
        		}
    		  } else {
    			  //List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(instance.getId());
    			  if(instance.getNoAssignation()!=null && instance.getNoAssignation()) {
    				  continue;
    			  }
    			  List<RecordReview> recordReviews = recordReviewDb.findByProperty(instance.getId(), rcdrv);        
    			  for (RecordReview recordReview : recordReviews) {
    				  recordReview.setReviewed(null);
    				  recordReview.setReviewedDate(null);
    				  session.update(recordReview);
    			  }
    		  }
    	  }
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.info("attach failed (" + ref + ") ", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }
  
  public void attachDirty(Set<Occurrence> instances) {
	    log.debug("attaching dirty Occurrence instances");
	    Occurrence ref = null;
	    try {
	      //Session session = HibernateUtil.getCurrentSession();
	      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
	      Session session = ManagedSession.createNewSessionAndTransaction();
	      for (Occurrence instance : instances) {
	        boolean newOccurrence = instance.getId() == null;
	        ref = instance;
	        instance.setLastUpdated((new Timestamp(System.currentTimeMillis())).toString());
	        // if (instance.isVetted()) {
	        // if (!instance.isValidated()) {
	        // instance.setVetted(false);
	        // }
	        // }
	        session.saveOrUpdate(instance);
	        if (newOccurrence) {
	          assignReviewRecords(instance);
	        } else {
	          List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(instance.getId());
	          for (RecordReview recordReview : recordReviews) {
	            recordReview.setReviewed(null);
	            recordReview.setReviewedDate(null);
	            session.update(recordReview);
	          }
	        }
	      }
	      //if (isFirstTransaction) {
	      //  HibernateUtil.commitCurrentTransaction();
	      //}
	      ManagedSession.commitTransaction(session);
	      log.debug("attach successful");
	    } catch (RuntimeException re) {
	      log.info("attach failed (" + ref + ") ", re);
	      //HibernateUtil.rollbackTransaction();
	      throw re;
	    }
	  }
  public void attachDirty(Set<Occurrence> instances, boolean resetReview) {
	    log.debug("attaching dirty Occurrence instances");
	    Occurrence ref = null;
	    try {
	      //Session session = HibernateUtil.getCurrentSession();
	      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
	      Session session = ManagedSession.createNewSessionAndTransaction();
	      for (Occurrence instance : instances) {
	        boolean newOccurrence = instance.getId() == null;
	        ref = instance;
	        if(resetReview)
//	        	instance.setLastUpdated((new Timestamp(System.currentTimeMillis())).toString());
	        	instance.setReviewed(null);
	        // if (instance.isVetted()) {
	        // if (!instance.isValidated()) {
	        // instance.setVetted(false);
	        // }
	        // }
	        session.saveOrUpdate(instance);
	        if (newOccurrence) {
	          assignReviewRecords(instance);
	        } else if(resetReview) {
	          List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(instance.getId());
	          for (RecordReview recordReview : recordReviews) {
	            recordReview.setReviewed(null);
	            recordReview.setReviewedDate(null);
	            session.update(recordReview);
	          }
	        }
	      }
	      //if (isFirstTransaction) {
	      //  HibernateUtil.commitCurrentTransaction();
	      //}
	      ManagedSession.commitTransaction(session);
	      log.debug("attach successful");
	    } catch (RuntimeException re) {
	      log.info("attach failed (" + ref + ") ", re);
	      //HibernateUtil.rollbackTransaction();
	      throw re;
	    }
	  }
  public boolean checkForReviewedChanged(int occurrenceId) {
    try {
//      Session session = HibernateUtil.getCurrentSession();
//      boolean isFirstSession = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      Occurrence occ = findById(occurrenceId);
      Boolean oldReviewed = occ.getReviewed();
      Boolean newReviewed = RecordReviewUtil.isRecordReviewed(
          recordReviewDb.getRecordReviewsByOcc(occurrenceId), RecordReviewUtil.DEFAULT_PERCENT_CUT);
      boolean isChanged = false;
      if (oldReviewed == null) {
        if (newReviewed != null) {
          isChanged = true;
          if(!Boolean.TRUE.equals(occ.getStability())){
        	  occ.setStability(true);
          }
        }
      } else {
        if (newReviewed == null) {
          isChanged = true;
        } else {
          isChanged = !oldReviewed.equals(newReviewed);
        }
      }
      if (isChanged) {
        occ.setReviewed(newReviewed);
        attachClean(session, occ);
        attachDirty(occ,session);
        // TODO: send user emails
        // User user = DBFactory.getUserDb().findById(occ.getOwner());
        // String subject = "";
        // EmailUtil.adminSendEmailTo(user.getEmail(), "", "");
      }

//      if (isFirstSession) {
//        HibernateUtil.commitCurrentTransaction();
//      }
      ManagedSession.commitTransaction(session);
      return isChanged;
    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      log.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public void delete(Occurrence persistentInstance) {
    log.debug("deleting Occurrence instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
	  Session session = ManagedSession.createNewSessionAndTransaction();
	  session.delete(persistentInstance);
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
	  ManagedSession.commitTransaction(session);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public void delete(Set<Occurrence> persistentInstances) {
    log.debug("deleting Occurrence instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      for (Occurrence persistentInstance : persistentInstances) {
        session.delete(persistentInstance);
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  /**
   * Deletes all {@link Occurrence} found by this query and belong to the given
   * user, found by user id. First delete records with original query. If there
   * is no record to delete with the first delete then replace original query
   * where acceptedSpecies "=" to acceptedSpecies "like" and call delete again.
   * 
   * @see org.rebioma.server.services.OccurrenceDb#detele(OccurrenceQuery, User)
   */
  public int detele(OccurrenceQuery query, User user) {
    int deletedRecord = 0;
    if (user != null) {
      Set<OccurrenceFilter> searchFilters = QueryFilter.getFilters(query.getBaseFilters(),
          OccurrenceFilter.class);
      searchFilters
          .addAll(QueryFilter.getFilters(query.getSearchFilters(), OccurrenceFilter.class));
      deletedRecord = deleteByQuery(user, searchFilters, query.getResultFilter(), 1);
      if (deletedRecord == 0) {
        // for (OccurrenceFilter f : searchFilters) {
        // if (f.column.equals(f.getPropertyName("acceptedspecies"))) {
        // f.operator = "like";
        // }
        // }
        deletedRecord = deleteByQuery(user, searchFilters, query.getResultFilter(), 2);
      }
    }
    return deletedRecord;
  }

  public List<Occurrence> findByAttributeValue(AttributeValue attributeValue) {
    Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
    attributeValues.add(attributeValue);
    return findByAttributeValues(attributeValues);
  }

  public List<Occurrence> findByAttributeValues(Set<AttributeValue> attributeValues) {
    log.debug("finding Occurrence instance by AttributeValues");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      Criteria criteria = session.createCriteria(Occurrence.class);
      Criterion orCriterion = null;
      for (AttributeValue attributeValue : attributeValues) {
        if (orCriterion == null) {
          orCriterion = attributeValue.toCriterion();
        } else {
          orCriterion = Restrictions.or(orCriterion, attributeValue.toCriterion());
        }
      }
      criteria.add(orCriterion);
      List<Occurrence> results = criteria.list();
      log.debug("find by attributeValue successful, result size: " + results.size());
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public List<Occurrence> findByExample(Occurrence instance) {
    log.debug("finding Occurrence instance by example");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      List<Occurrence> results = session.createCriteria("org.rebioma.client.bean.Occurrence")
          .add(create(instance)).list();
      log.debug("find by example successful, result size: " + results.size());
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public List<Occurrence> findByExample(Set<Occurrence> instances) {
    List<Occurrence> result = new ArrayList<Occurrence>();
    for (Occurrence instance : instances) {
      result.addAll(findByExample(instance));
    }
    return result;
  }

  public Occurrence findById(java.lang.Integer id) {
    log.debug("getting Occurrence instance with id: " + id);
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      Occurrence instance = (Occurrence) session.get(
          "org.rebioma.client.bean.Occurrence", id);
      if (instance == null) {
        log.debug("get successful, no instance found");
      } else {
        log.debug("get successful, instance found");
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return instance;
    } catch (RuntimeException re) {
      log.error("get failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public List<Occurrence> findById(Set<Integer> ids) {
    List<Occurrence> results = new ArrayList<Occurrence>();
    for (Integer id : ids) {
      results.add(findById(id));
    }
    return results;
  }

  public List<Occurrence> findByOccurrenceQuery(OccurrenceQuery query, Integer userId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Finds {@link Occurrence} objects by using a {@link Criteria} with a
   * {@link Restrictions} for each filter and {@link OccurenceType}. If no
   * occurrences are found for the original query, the same query is executed
   * with 'eq' filters changed to 'like'.
   * 
   * @throws Exception
   */
  public List<Occurrence> findByOccurrenceQuery(OccurrenceQuery query, User user) throws Exception {
    log.debug("finding Occurrence instances by query.");
    Set<OccurrenceFilter> filters = QueryFilter.getFilters(query.getBaseFilters(),
        OccurrenceFilter.class);
    filters.addAll(QueryFilter.getFilters(query.getSearchFilters(), OccurrenceFilter.class));
    Set<OccurrenceFilter> disjunctionFilters = new HashSet<OccurrenceDbImpl.OccurrenceFilter>();
    disjunctionFilters.addAll(QueryFilter.getFilters(query.getDisjunctionSearchFilters(), OccurrenceFilter.class));
    for(OccurrenceFilter filtre: disjunctionFilters){
    	filtre.setDisjunction(true);
    }
    filters.addAll(disjunctionFilters);
    // Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(
    // Occurrence.class);
    List<Occurrence> results;
    results = find(query, filters, user, 1);

    if (results == null || results.size() == 0) {
      // for (OccurrenceFilter f : filters) {
      // if (f.column.equals(f.getPropertyName("acceptedspecies"))) {
      // f.operator = "like";
      // }
      // }
      results = find(query, filters, user, 2);
    }
    return results;
  }

  public List<Integer> findOccurrenceIdsByQuery(OccurrenceQuery query, User user) throws Exception {
    Set<OccurrenceFilter> filters = QueryFilter.getFilters(query.getBaseFilters(),
        OccurrenceFilter.class);
    filters.addAll(QueryFilter.getFilters(query.getSearchFilters(), OccurrenceFilter.class));
    Set<OccurrenceFilter> disjunctionFilters = new HashSet<OccurrenceDbImpl.OccurrenceFilter>();
    disjunctionFilters.addAll(QueryFilter.getFilters(query.getDisjunctionSearchFilters(), OccurrenceFilter.class));
    for(OccurrenceFilter filtre: disjunctionFilters){
    	filtre.setDisjunction(true);
    }
    filters.addAll(disjunctionFilters);
    // Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(
    // Occurrence.class);
    List<Integer> results;
    results = findIds(query, filters, user, 1);

    if (results == null || results.size() == 0) {
      // for (OccurrenceFilter f : filters) {
      // if (f.column.equals(f.getPropertyName("acceptedspecies"))) {
      // f.operator = "like";
      // }
      // }
      results = findIds(query, filters, user, 2);
    }
    return results;
  }

  @Override
  public List<OccurrenceReview> getOccurrenceReviewsOf(int occurrenceId) {
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirst = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(occurrenceId);
      List<OccurrenceReview> occurrenceReviews = new ArrayList<OccurrenceReview>();
      if (recordReviews != null) {
        for (RecordReview recordReview : recordReviews) {
          User reviewer = userDb.findById(recordReview.getUserId());
          occurrenceReviews.add(new OccurrenceReview(reviewer.getFirstName() + " "
              + reviewer.getLastName(), reviewer.getEmail(), occurrenceId, recordReview
              .getReviewed(), recordReview.getReviewedDate()));
        }
      }
      //if (isFirst) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return occurrenceReviews;

    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  public Occurrence merge(Occurrence detachedInstance) {
    log.debug("merging Occurrence instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      Occurrence result = (Occurrence) session.merge(detachedInstance);
      log.debug("merge successful");
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return result;
    } catch (RuntimeException re) {
      log.error("merge failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public void persist(Occurrence transientInstance) {
    log.debug("persisting Occurrence instance");
    try {
      ManagedSession.createNewSession().persist(transientInstance);	
      //HibernateUtil.getCurrentSession().persist(transientInstance);
      log.debug("persist successful");
    } catch (RuntimeException re) {
      log.error("persist failed", re);
      throw re;
    }
  }

  /**
   * Deletes Occurrence objects from the set 'instances' which for some reason
   * are invalid for uploading
   * 
   * @see org.rebioma.server.services.OccurrenceDb#removeBadId(java.util.Set,
   *      User)
   */
  public String removeBadId(Set<Occurrence> instances, User loggedinUser) {
    StringBuilder improperIds = new StringBuilder("[");
    StringBuilder idNotinDb = new StringBuilder("[");
    StringBuilder multipleId = new StringBuilder("[");
    StringBuilder notYourRecords = new StringBuilder("[");
    List<Occurrence> toRemove = new ArrayList<Occurrence>();
    if (loggedinUser == null) {
      instances.clear();
      return "{\"userLoggedIn\":false}";
    }
    
    boolean sAdmin = new RoleDbImpl().isSAdmin(loggedinUser.getId());
    
    Integer id;
    Map<Integer, Occurrence> testDuplicate = new HashMap<Integer, Occurrence>();
    for (Occurrence o : instances) {

      // remove the occurrence that has owner id does not match with owner id or
      // owner email does not match with user email.

      if (o.getId() == null) {
        String createdTime = (new Timestamp(System.currentTimeMillis())).toString();
        o.setTimeCreated(createdTime);
      } else if (o.getId() != null) {
        try {
          id = o.getId();
        } catch (Exception e) {
          if (toRemove.add(o)) {
            log.error("attach failed (" + o + ") Id formatted improperly");
            improperIds.append("\"" + o.getId() + "\"" + ",");
          }
          continue;
        }
        Occurrence existenceOccurrence = findById(id);
        String ownerEmail = (o.getOwnerEmail()).trim();
        String currentUserEmail = loggedinUser.getEmail().trim();
        String existenceOwnerEmail = existenceOccurrence == null ? null : existenceOccurrence
            .getOwnerEmail().trim();
        if (existenceOccurrence == null) {
          if (toRemove.add(o)) {
            log.error("attach failed (" + o + ") Id not found in Database");
            idNotinDb.append("\"" + o.getId() + "\"" + ",");
          }
        } else if ((!existenceOwnerEmail.equalsIgnoreCase(ownerEmail)
            || !existenceOwnerEmail.equalsIgnoreCase(currentUserEmail)) && !sAdmin) {
          if (toRemove.add(o)) {
            log.error("attach failed (" + o + ") record is not belong to " + currentUserEmail);
            notYourRecords.append("\"" + o.getId() + "\"" + ",");
          }
        } else {
          if(sAdmin)
        	o.setReviewed(existenceOccurrence.getReviewed());	
          o.setTimeCreated(existenceOccurrence.getTimeCreated());
          if (testDuplicate.containsKey(id)) {
            if (multipleId.indexOf(o.getId() + "") == -1) {
              multipleId.append("\"" + o.getId() + "\"" + ",");
            }
            if (toRemove.add(o)) {
              log.error("attach failed (" + o
                  + ") Uploading multiple occurrences with identical Id");
            }
            if (toRemove.add(testDuplicate.get(id))) {
              log.error("attach failed (" + testDuplicate.get(id)
                  + ") Uploading multiple occurrences with identical Id");
            }
          }
          testDuplicate.put(id, o);
        }
      }
    }

    for (Occurrence o : toRemove) {
      if (!instances.remove(o)) {
        log.error("weve got an error with instances.remove");
      }
    }
    log.debug("occurrencedbimpl says " + toRemove.size());

    String message = "{";
    message += "\"improperIds\":";
    if (improperIds.length() != 1) {
      improperIds.setCharAt(improperIds.lastIndexOf(","), ']');
      message += improperIds.toString();
    } else {
      message += "[]";
    }
    message += ",";
    message += "\"idNotInDb\":";
    if (idNotinDb.length() != 1) {
      idNotinDb.setCharAt(idNotinDb.lastIndexOf(","), ']');
      message += idNotinDb.toString();
    } else {
      message += "[]";
    }
    message += ",";
    message += "\"multipleId\":";
    if (multipleId.length() != 1) {
      multipleId.setCharAt(multipleId.lastIndexOf(","), ']');
      message += multipleId.toString();
    } else {
      message += "[]";
    }
    message += ",";
    message += "\"notYourRecords\":";
    if (notYourRecords.length() != 1) {
      notYourRecords.setCharAt(notYourRecords.lastIndexOf(","), ']');
      message += notYourRecords.toString();
    } else {
      message += "[]";
    }
    message += "}";

    return message;
  }

  public void resetReviews() {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();
      session.createQuery("update Occurrence o set o.reviewed=null").executeUpdate();
      ManagedSession.commitTransaction(session);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void updateStability(Occurrence o, Boolean stability, Session sess) {
	    try {
	      //Query query = sess.createQuery("update Occurrence o set o.stability=:stability where o.id=:ID");
	      Query query = sess.createQuery("update Occurrence o set o.stability="+stability+" where o.id="+o.getId());
	      //query.setParameter("stability", stability);
	      //query.setParameter("ID", o.getId());
	      query.executeUpdate();	      	 
	    } catch (Exception e) {
	      log.error(e.getMessage(), e);
	    }
	  }
  /**
   * First update records with original query if there is no update, replace the
   * original query acceptedSpecies = to acceptedspecies like query. If user is
   * trying to vet all records restrict the update query to only updated
   * validated records.
   * 
   * @seeorg.rebioma.server.services.OccurrenceDb#update(org.rebioma.client. 
   *                                                                         OccurrenceQuery
   *                                                                         )
   */

  public int update(OccurrenceQuery query, User user) {
    if (user == null) {
      return 0;
    }
    Set<OccurrenceFilter> filters = QueryFilter.getFilters(query.getBaseFilters(),
        OccurrenceFilter.class);
    filters.addAll(QueryFilter.getFilters(query.getSearchFilters(), OccurrenceFilter.class));
    Set<OccurrenceFilter> updates = QueryFilter.getFilters(query.getUpdates(),
        OccurrenceFilter.class);
    int updatedRecordCount = updateByQuery(user, filters, updates, query.getResultFilter(), 1);
    if (updatedRecordCount == 0) {
      // for (OccurrenceFilter f : filters) {
      // if (f.column.equals(f.getPropertyName("acceptedspecies"))) {
      // f.operator = "like";
      // }
      // }
    } else {
      return updatedRecordCount;
    }
    return updateByQuery(user, filters, updates, query.getResultFilter(), 2);

  }

  @Override
  public String validateOccurrences(Set<Occurrence> instances, User loggedinUser,
      Map<User, Set<Occurrence>> userOccurrencesMap) {
    StringBuilder improperIds = new StringBuilder("[");
    StringBuilder idNotinDb = new StringBuilder("[");
    StringBuilder multipleId = new StringBuilder("[");
    StringBuilder notYourRecords = new StringBuilder("[");
    List<Occurrence> toRemove = new ArrayList<Occurrence>();
    if (loggedinUser == null) {
      instances.clear();
      return "{\"userLoggedIn\":false}";
    }
    Integer id;
    Map<Integer, Occurrence> testDuplicate = new HashMap<Integer, Occurrence>();
    Session session = ManagedSession.createNewSessionAndTransaction();

    UserDb userDb = DBFactory.getUserDb();
    RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();
    TaxonomicReviewerDb taxonomicReviewerDb = DBFactory.getTaxonomicReviewerDb();
    String message = null;
    try {
      for (Occurrence o : instances) {

        // remove the occurrence that has owner id does not match with owner id
        // or
        // owner email does not match with user email.

        if (o.getId() == null) {
          String createdTime = (new Timestamp(System.currentTimeMillis())).toString();
          o.setTimeCreated(createdTime);
        } else if (o.getId() != null) {
          try {
            id = o.getId();
          } catch (Exception e) {
            if (toRemove.add(o)) {
              log.error("attach failed (" + o + ") Id formatted improperly");
              improperIds.append("\"" + o.getId() + "\"" + ",");
            }
            continue;
          }
          Occurrence existenceOccurrence = findById(id);
          String ownerEmail = o.getOwnerEmail();
          String ownerName = o.getOwnerEmail();
          if (existenceOccurrence == null) {
            if (toRemove.add(o)) {
              log.error("attach failed (" + o + ") Id not found in Database");
              idNotinDb.append("\"" + o.getId() + "\"" + ",");
            }
          } else if (!existenceOccurrence.getOwnerEmail().equalsIgnoreCase(ownerEmail)
              || !existenceOccurrence.getOwnerEmail().equalsIgnoreCase(ownerName)) {
            if (toRemove.add(o)) {
              log.error("attach failed (" + o + ") record is not belong to "
                  + loggedinUser.getEmail());
              notYourRecords.append("\"" + o.getId() + "\"" + ",");
            }
          } else {
            o.setTimeCreated(existenceOccurrence.getTimeCreated());
            if (testDuplicate.containsKey(id)) {
              if (multipleId.indexOf(o.getId() + "") == -1) {
                multipleId.append("\"" + o.getId() + "\"" + ",");
              }
              if (toRemove.add(o)) {
                log.error("attach failed (" + o
                    + ") Uploading multiple occurrences with identical Id");
              }
              if (toRemove.add(testDuplicate.get(id))) {
                log.error("attach failed (" + testDuplicate.get(id)
                    + ") Uploading multiple occurrences with identical Id");
              }
            } else {
              // mark record that are change which i need to notified user
              List<RecordReview> recordReviews = recordReviewDb.getRecordReviewsByOcc(id);
              for (RecordReview recordReview : recordReviews) {
                int userId = recordReview.getUserId();
                List<TaxonomicReviewer> reviewers = taxonomicReviewerDb
                    .getTaxonomicReviewers(recordReview.getUserId());
                for (TaxonomicReviewer reviewer : reviewers) {
                  String taxoField = reviewer.getTaxonomicField();
                  String taxoValue = reviewer.getTaxonomicValue();
                  if (isTaxonomicMatch(new AttributeValue(taxoField, taxoValue), o)) {
                    User user = userDb.findById(userId);
                    Set<Occurrence> notifyingOccs = userOccurrencesMap.get(user);
                    if (notifyingOccs == null) {
                      notifyingOccs = new HashSet<Occurrence>();
                      userOccurrencesMap.put(user, notifyingOccs);
                    }
                    notifyingOccs.add(o);
                  }
                }
              }
            }
            testDuplicate.put(id, o);
          }
        }
      }

      for (Occurrence o : toRemove) {
        if (!instances.remove(o)) {
          log.error("weve got an error with instances.remove");
        }
      }
      log.debug("occurrencedbimpl says " + toRemove.size());

      message = "{";
      message += "\"improperIds\":";
      if (improperIds.length() != 1) {
        improperIds.setCharAt(improperIds.lastIndexOf(","), ']');
        message += improperIds.toString();
      } else {
        message += "[]";
      }
      message += ",";
      message += "\"idNotInDb\":";
      if (idNotinDb.length() != 1) {
        idNotinDb.setCharAt(idNotinDb.lastIndexOf(","), ']');
        message += idNotinDb.toString();
      } else {
        message += "[]";
      }
      message += ",";
      message += "\"multipleId\":";
      if (multipleId.length() != 1) {
        multipleId.setCharAt(multipleId.lastIndexOf(","), ']');
        message += multipleId.toString();
      } else {
        message += "[]";
      }
      message += ",";
      message += "\"notYourRecords\":";
      if (notYourRecords.length() != 1) {
        notYourRecords.setCharAt(notYourRecords.lastIndexOf(","), ']');
        message += notYourRecords.toString();
      } else {
        message += "[]";
      }
      message += "}";
      ManagedSession.commitTransaction(session);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return message;
  }

  /**
   * Adds a {@link Set} of search {@link OccurrenceFilter} to the
   * {@link Criteria}
   * 
   * @param criteria {@link Criteria} to be added with filters
   * @param user an id of a user in db.
   * @param searchFilters a filters that use for find Occurrences.
   * @param resultFilter the {@link ResultFilter} to determine what public
   *          filter is added.
   * @param tryCount TODO
   * @return {@link Set} of string filters was added to criteria.
   */
  private Set<String> addCreterionByFilters(Criteria criteria, User user,
      Set<OccurrenceFilter> searchFilters, ResultFilter resultFilter, int tryCount) {
    Set<String> queryFilters = new HashSet<String>();
    // if (userId != null) {

    // }
    // Adds restrictions for each query filter:
    boolean isMyOccurrence = false;
    String ownerField = getOccurrencePropertyName("ownerEmail");
    List<Criterion> disjunctionCriterions = new ArrayList<Criterion>(); 
    for (OccurrenceFilter filter : searchFilters) {
      if ((filter.getOperator() != Operator.IS_EMPTY && filter.getOperator() != Operator.IS_NOT_EMPTY)
          && filter.value instanceof String && ((String) filter.value).equals("")) {
        continue;
      }
      if (filter.column.equalsIgnoreCase(ownerField)) {
        isMyOccurrence = true;
      }
      String queryFilter = null;
      Criterion criterion = null;
      // if filter column is "quickSearch" then this is a simple search
      if (filter.column.equalsIgnoreCase(filter.getPropertyName("quickSearch"))) {
        Criterion acceptedSpeciesCri = null;
        Criterion verbatimSpeciesCri = null;
        Criterion scientificNameCri = null;
        String acceptedSpecies = filter.getPropertyName("acceptedSpecies");
        String verbatimSpecies = filter.getPropertyName("verbatimSpecies");
        String scientificName = filter.getPropertyName("scientificName");
        if (tryCount == 1) {
          acceptedSpeciesCri = Restrictions.ilike(acceptedSpecies, filter.getValue().toString(),
              MatchMode.START);
          verbatimSpeciesCri = Restrictions.ilike(verbatimSpecies, filter.getValue().toString(),
              MatchMode.START);
          scientificNameCri = Restrictions.ilike(scientificName, filter.getValue().toString(),
              MatchMode.START);
          queryFilter = acceptedSpecies + " like '" + filter.getValue() + "%' or "
              + verbatimSpecies + " like '" + filter.getValue() + "%' or " + scientificName
              + " like '" + filter.getValue() + "%'";
        } else if (tryCount == 2) {
          acceptedSpeciesCri = Restrictions.ilike(acceptedSpecies, filter.getValue().toString(),
              MatchMode.ANYWHERE);
          verbatimSpeciesCri = Restrictions.ilike(verbatimSpecies, filter.getValue().toString(),
              MatchMode.ANYWHERE);
          scientificNameCri = Restrictions.ilike(scientificName, filter.getValue().toString(),
              MatchMode.ANYWHERE);
          queryFilter = acceptedSpecies + " like '%" + filter.getValue() + "%' or "
              + verbatimSpecies + " like '%" + filter.getValue() + "%' or " + scientificName
              + " like '%" + filter.getValue() + "%'";
        }
        criterion = Restrictions.or(scientificNameCri,
            Restrictions.or(acceptedSpeciesCri, verbatimSpeciesCri));
      } else {
        Operator op = filter.getOperator();
        String value;
        switch (op) {
        case CONTAIN:
          value = (String) filter.getValue();
          criterion = Restrictions.ilike(filter.column, value, MatchMode.ANYWHERE);
          break;
        case EQUAL:
          criterion = null;
          if (filter.column.equals(filter.getPropertyName("sex"))) {
            value = (String) filter.getValue();
            if (value.equalsIgnoreCase("unknown")) {
              Object[] filters = getComplexCriterion(new String[] { "male", "female",
                  "hermaphroditic" }, filter.column, "!=");
              criterion = (Criterion) filters[0];
              queryFilter = filters[1].toString();
              // criterion = Restrictions.or(Restrictions., rhs)
            } else {
              criterion = Restrictions.eq(filter.column, value);
            }
          } else if (filter.column.equals(filter.getPropertyName("BasisOfRecord"))) {
            value = (String) filter.getValue();
            if (value.equalsIgnoreCase("Non-standardSpecimen")) {
              Object[] filters = getComplexCriterion(VALID_BASIS_OF_RECORDS, filter.column, "!=");
              criterion = (Criterion) filters[0];
              queryFilter = filters[1].toString();
            } else {
              criterion = Restrictions.eq(filter.column, value);
            }
          } else {
        	// {wd} on utilise "upper()" si type column = String 
          	if(StringUtil.isType(Occurrence.class, filter.column, String.class))
          		criterion = Restrictions.sqlRestriction("upper({alias}." + StringUtil.columnName(Occurrence.class, filter.column) + ") = upper(?)", filter.getValue(), Hibernate.STRING);
          	else
          		criterion = Restrictions.eq(filter.column, filter.getValue());
          }
          break;
        case NOT_CONTAIN:
          value = (String) filter.getValue();
          criterion = Restrictions
              .not(Restrictions.ilike(filter.column, value, MatchMode.ANYWHERE));
          break;
        case NOT_EQUAL:
          criterion = null;
          if (filter.column.equals(filter.getPropertyName("sex"))) {
            value = (String) filter.getValue();
            if (value.equalsIgnoreCase("unknown")) {
              Object[] filters = getComplexCriterion(new String[] { "male", "female",
                  "hermaphroditic" }, filter.column, "=");
              criterion = (Criterion) filters[0];
              queryFilter = filters[1].toString();
              // criterion = Restrictions.or(Restrictions., rhs)
            } else {
              criterion = Restrictions.ne(filter.column, value);
            }
          } else if (filter.column.equals(filter.getPropertyName("BasisOfRecord"))) {
            value = (String) filter.getValue();
            if (value.equalsIgnoreCase("Non-standardSpecimen")) {
              Object[] filters = getComplexCriterion(VALID_BASIS_OF_RECORDS, filter.column, "=");
              criterion = (Criterion) filters[0];
              queryFilter = filters[1].toString();
            } else {
              criterion = Restrictions.ne(filter.column, value);
            }
          } else {
            criterion = Restrictions.ne(filter.column, filter.getValue());
          }
          break;
        case START_WITH:
          value = (String) filter.getValue();
          criterion = Restrictions.ilike(filter.column, value, MatchMode.START);
          break;
        case NOT_START_WITH:
          value = (String) filter.getValue();
          criterion = Restrictions.not(Restrictions.ilike(filter.column, value, MatchMode.START));
          break;
        case LESS:
          criterion = Restrictions.lt(filter.column, filter.getValue());
          break;
        case GREATER:
          criterion = Restrictions.gt(filter.column, filter.getValue());
          break;
        case LESS_EQUAL:
          criterion = Restrictions.le(filter.column, filter.getValue());
          break;
        case GREATER_EQUAL:
          criterion = Restrictions.ge(filter.column, filter.getValue());
          break;
        case IN:
          if (filter.getValue() instanceof Collection<?>) {
            criterion = Restrictions.in(filter.column, (Collection<?>) filter.getValue());
            //{WD
          } else if (StringUtil.isType(Occurrence.class, filter.column, Integer.class)){
        	  Object values[] = filter.getIntegerValues();
              criterion = Restrictions.in(filter.column, values);
              //}
          } else {
            String values[] = filter.getCollectionValues();
            criterion = Restrictions.in(filter.column, values);
          }
          break;
        case NOT_IN:
          //{WD
          if (StringUtil.isType(Occurrence.class, filter.column, Integer.class)){
        	  Object arryValues[] = filter.getIntegerValues();
        	  criterion = Restrictions.not(Restrictions.in(filter.column, arryValues));
          //}
          } else {
        	  String arryValues[] = filter.getCollectionValues();
        	  criterion = Restrictions.not(Restrictions.in(filter.column, arryValues));
          }
          break;
        case IS_EMPTY:
          if (StringUtil.isString(Occurrence.class, filter.column)) {
            // System.out.println(filter.column + "null and empty");
            criterion = Restrictions.or(Restrictions.isNull(filter.column),
                Restrictions.eq(filter.column, ""));
          } else {
            // System.out.println(filter.column + "null");
            criterion = Restrictions.isNull(filter.column);
          }
          // System.out.println(criterion);
          break;
        case IS_NOT_EMPTY:
          criterion = Restrictions.and(Restrictions.isNotNull(filter.column),
              Restrictions.ne(filter.column, ""));
          System.out.println(criterion);
          break;
        }
      }

      if (criterion != null) {
        if (queryFilter == null) {
          queryFilter = filter.toString();
        }
        queryFilters.add(queryFilter);
        if(filter.isDisjunction()){
        	disjunctionCriterions.add(criterion);
        }else{
        	criteria.add(criterion);
        }
        
      }
    }
    Set<String> filtersString = new HashSet<String>();
    Criterion publicCriterion = getPublicCriterion(user, resultFilter, isMyOccurrence,
        filtersString);
    if (publicCriterion != null) {
      criteria.add(publicCriterion);
      queryFilters.addAll(filtersString);
    }
    Criterion disjunctionCriterion = null;
    for(Criterion c: disjunctionCriterions){
    	if(disjunctionCriterion == null){
    		disjunctionCriterion = Restrictions.disjunction().add(c);
    	}else{
    		disjunctionCriterion = Restrictions.or(disjunctionCriterion, c);
    	}
    }
    if(disjunctionCriterion != null){
    	criteria.add(disjunctionCriterion);
    }
    return queryFilters;
  }

  /**
   * Vérifie si l'occurrence appartient à un trb et qu'elle sera révisé et ne seta pas assigné à d'autre trb
   * @param occ
   * @param date
   * @return 
   */
  private boolean assignAndReviewRecords(Occurrence occ, Date date, Session session) {
	  boolean noAssignation = false;
	  if(occ.getNoAssignation()!=null && occ.getNoAssignation()) {
			for (TaxonomicReviewer taxonomicReviewer: taxonomiKRB) {
				if(noAssignation = occ.getOwner().equals(taxonomicReviewer.getUserId())) {
					session.saveOrUpdate(new RecordReview(taxonomicReviewer.getUserId(), occ.getId(),
							true, date));
				}
			}
	  }
	  return noAssignation;
  }
  
  private void assignReviewRecords(Occurrence occ, Session session) {
	if (occ.getNoAssignation()!=null && occ.getNoAssignation())
	  return;
    AttributeValue attributeValue = new AttributeValue();
    for (TaxonomicReviewer taxonomicReviewer : taxonomiKRB) {
      attributeValue.setAttribute(taxonomicReviewer.getTaxonomicField());
      attributeValue.setValue(taxonomicReviewer.getTaxonomicValue());
      if (isTaxonomicMatch(attributeValue, occ) && checkAcceptedspeciesLocation(taxonomicReviewer, occ.getAcceptedSpecies())) {
        RecordReview recordReview = new RecordReview(taxonomicReviewer.getUserId(), occ.getId(),
            null);
        if(session==null)
        	recordReviewDb.save(recordReview);
        else recordReviewDb.save(recordReview, session);
      }
    }
  }
  
  private void assignReviewRecords(Occurrence occ) {
	  assignReviewRecords(occ, null);
  }
  
  //{WD}
  /**
   * 
   */
  private boolean checkAcceptedspeciesLocation(TaxonomicReviewer trb, String acceptedSpecies){
	Session session = ManagedSession.createNewSessionAndTransaction();
	int isM = trb.getIsMarine()?1:0;
	int isT = trb.getIsTerrestrial()?1:0;
	Query query = session.createQuery("select id from Taxonomy t where t.acceptedSpecies "
    		+ "='" + acceptedSpecies + "' and (t.isMarine = " + isM + " or t.isTerrestrial = " + isT + ")");
    List<Integer> ids = query.list();
    ManagedSession.commitTransaction(session);
	return (ids!=null || ids.size()>0);
	  
  }
  /**
   * Create {@link RecordReview} for each of the occurrence that not yet assign
   * to user with user id = {@link TaxonomicReviewer#getUserId()}.
   * 
   * @param taxonomicReviewer
   */
  private Set<Occurrence> assignReviewToOccurrence(TaxonomicReviewer taxonomicReviewer,
      List<Occurrence> occurrences) {
    Set<Occurrence> assignedOccs = new HashSet<Occurrence>();
    if (occurrences != null) {
      for (Occurrence occurrence : occurrences) {
        int occurrenceId = occurrence.getId();
        // only create record review for occurrence that have not yet assign to
        // this user
        RecordReview recordReview = new RecordReview(taxonomicReviewer.getUserId(), occurrenceId,
            null);
        recordReview = recordReviewDb.save(recordReview);
        if (recordReview != null) {
          assignedOccs.add(occurrence);
        }

      }
    }
    return assignedOccs;
  }

  private String createDeleteQuery(Criteria criteria, Set<OccurrenceFilter> searchFilters,
      User user, ResultFilter resultFilter, int tryCount) {
    return "delete Occurrence"
        + createWhereClause(criteria, user, searchFilters, resultFilter, tryCount);
  }

  /**
   * create a concat sql query in the following format: sharedUsersCSV =
   * concat(sharedUsersCSV, CASE WHEN sharedUsersCSV like ' userId ' then ''
   * else ' userId ,' END, CASE WHEN ..., '') <br>
   * sharedUsersCSV format: space userid space, space userid space, ..., .There
   * is and comma in the end of the id list to rule out end of the list corner
   * case when a user id is removed from the list. A space before and after
   * userId to ensure searching user id in sharedUsersCSV return the correct
   * result (i.e 18 and 8 are there same in like query without extra spaces).
   * 
   * @param csvUserIdsList user list In CSV;
   * @param userEmail current logged user id
   * @return sharedUsersCSV = concat(sharedUsersCSV, CASE WHEN sharedUsersCSV
   *         like ' userId ' then '' else ' userId ,' END, CASE WHEN ..., '')
   */
  private String createUpdatedSharedUsersCSVSQL(String csvUserIdsList, String userEmail) {
    StringBuilder sb = new StringBuilder();
    String usersList[] = csvUserIdsList.split(",");
    String beginQuery = "sharedUsersCSV = concat(sharedUsersCSV,";
    String endQuery = " ''), ";
    for (String email : usersList) {
      email = email.trim();
      if (email.equals("")) {
        continue;
      }
      sb.append(" CASE WHEN sharedUsersCSV like '%" + email + "%' then '' else '" + email
          + ",' END, ");
    }
    if (sb.length() > 0) {
      sb.insert(0, beginQuery);
      sb.append(endQuery);
    }
    return sb.toString();
  }

  private String createUpdatedUnsharedUsersSQL(String csvUserList) {
    StringBuilder sb = new StringBuilder();
    String usersList[] = csvUserList.split(",");
    String replaceQuery = "sharedUsersCSV = replace(sharedUsersCSV,";
    for (String userEmail : usersList) {
      userEmail = userEmail.trim();
      if (userEmail.equals("")) {
        continue;
      }
      sb.append(replaceQuery);
      sb.append("'" + userEmail + ",',''), ");
    }
    return sb.toString();
  }

  /**
   * Create Hibernate update query base on search filters, update filters, and
   * user id. If user is trying to vet all records restrict the update query to
   * only updated validated records.
   * 
   * @param criteria {@link Criteria} to be added {@link Criterion} filters.
   * @param searchFilters filters use to find to be updated occurrences
   * @param updateFilters filters contain updated fields.
   * @param user id of this current user;
   * @param resultFilter the {@link ResultFilter} to determine what public
   *          filter is added.
   * @param tryCount TODO
   * @return Hibernate update query.
   */
  private String createUpdateQuery(Criteria criteria, Set<OccurrenceFilter> searchFilters,
      Set<OccurrenceFilter> updateFilters, User user, ResultFilter resultFilter, int tryCount) {
    StringBuilder sb = new StringBuilder("update Occurrence set  ");
    boolean addVettedRetriction = false;
    for (OccurrenceFilter filter : updateFilters) {
      String value = filter.value.toString();
      if (filter.value instanceof String) {
        value = "'" + value + "'";
      }
      if (filter.column.equals(filter.getPropertyName("vetted")) && filter.value.equals(true)) {
        addVettedRetriction = true;
      }

      if (filter.column.equals(filter.getPropertyName("sharedUsers"))) {
        sb.append(createUpdatedSharedUsersCSVSQL(filter.value.toString(), user.getEmail()));
      } else if (filter.column.equals(filter.getPropertyName("unsharedUsers"))) {
        sb.append(createUpdatedUnsharedUsersSQL(filter.value.toString()));
      } else {
        sb.append(filter.column + "=" + value + ", ");
      }
    }
    sb.delete(sb.length() - 2, sb.length());
    // removed public = false in searchFilters if this is My Occurrences only
    if (addVettedRetriction) {
      try {
        searchFilters.add(new OccurrenceFilter("validated = true"));
      } catch (InvalidFilter e) {
        // shouldn't happen
        e.printStackTrace();
      }
    }
    sb.append(createWhereClause(criteria, user, searchFilters, resultFilter, tryCount));
    return sb.toString();
  }

  /**
   * Creates mysql where clause base on searchFilters and userId (i.e where
   * owner=1 and public_=true ...).
   * 
   * @param criteria {@link Criteria} contains the query table object.
   * @param user id of this current user;
   * @param searchFilters filters use to find to be updated occurrences
   * @param resultFilter the {@link ResultFilter} to determine what public
   *          filter is added.
   * @param tryCount TODO
   * @return MySQL where clause.
   */
  private String createWhereClause(Criteria criteria, User user,
      Set<OccurrenceFilter> searchFilters, ResultFilter resultFilter, int tryCount) {
    // Set<Filter> privateFilters = isMyOccurrenceOnly(userId, searchFilters);
    Set<String> queryFilters = this.addCreterionByFilters(criteria, user, searchFilters,
        resultFilter, tryCount);
    StringBuilder sb = new StringBuilder(" where (ownerEmail='" + user.getEmail() + "')");
    for (String filter : queryFilters) {
      sb.append(" and (" + filter + ")");
    }

    return sb.toString();
  }

  /**
   * Deletes all record found by searchFilters.
   * 
   * @param user user that own deleted records.
   * @param searchFilters {@link OccurrenceFilter} use to search for deleted
   *          record
   * @param resultFilter the {@link ResultFilter} to determine what public
   *          filter is added.
   * @param tryCount TODO
   * @return number of records got deleted.
   */
  private int deleteByQuery(User user, Set<OccurrenceFilter> searchFilters,
      ResultFilter resultFilter, int tryCount) {

    log.debug("deleting Occurrences instances by query.");
    Session session = ManagedSession.createNewSessionAndTransaction();
    int deletedRecord = 0;
    try {
      String deleteQuery = createDeleteQuery(session.createCriteria(Occurrence.class),
          searchFilters, user, resultFilter, tryCount);
      log.info(deleteQuery);
      deletedRecord = session.createQuery(deleteQuery).executeUpdate();
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      log.error("delete by query failed", re);
      ManagedSession.rollbackTransaction(session);
      //HibernateUtil.rollbackTransaction();
      throw re;
    } finally {
      // session.close();
    }
    return deletedRecord;
  }

  private List<Occurrence> find(OccurrenceQuery query, Set<OccurrenceFilter> filters, User user,
      int tryCount) throws Exception {
    log.debug("finding Occurrence instances by query.");
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();
      List<Occurrence> results = null;
      Criteria criteria = session.createCriteria(Occurrence.class);
      OccurrenceFilter userReviewFilter = null;
      OccurrenceFilter myreviewPublicFilter = null;
      ResultFilter resultFilter = query.getResultFilter();
      for (OccurrenceFilter filter : filters) {
        if (filter.column.equals(filter.getPropertyName("userReviewed"))) {
          userReviewFilter = filter;
          if (resultFilter != null) {
            if (resultFilter == ResultFilter.PUBLIC) {
              myreviewPublicFilter = new OccurrenceFilter("public = true");
            } else if (resultFilter == ResultFilter.PRIVATE) {
              myreviewPublicFilter = new OccurrenceFilter("public = false");
            }
            resultFilter = null;
          }
        }
      }
      if (myreviewPublicFilter != null) {
        filters.add(myreviewPublicFilter);
      }
      filters.remove(userReviewFilter);
      OccurrenceFilter idsFilter = null;
      if (userReviewFilter != null) {
        Boolean reviewed = null;
        if (userReviewFilter.operator == Operator.EQUAL) {
          reviewed = (Boolean) userReviewFilter.getValue();
        }
        List<Integer> occIds = recordReviewDb.getRecordReviewOccIds(user.getId(), reviewed);
        System.out.println(occIds.size());
        if (occIds.isEmpty()) {
          occIds.add(0);
        }
        idsFilter = new OccurrenceFilter("id", Operator.IN, occIds);
        filters.add(idsFilter);
      }
      log.info("find filters: "
          + addCreterionByFilters(criteria, user, filters, resultFilter, tryCount));
      if (userReviewFilter != null) {
        filters.remove(idsFilter);
        filters.add(userReviewFilter);
      }
      if (myreviewPublicFilter != null) {
        filters.remove(myreviewPublicFilter);
      }
      List<OrderKey> orderingMap = query.getOrderingMap();
      log.info("order map = " + orderingMap);
      if (query.isCountTotalResults()) {
          criteria.setFirstResult(0);
          criteria.setProjection(Projections.count("id"));
          Integer count = (Integer) criteria.uniqueResult();
          if (count != null) {
            query.setCount(count);
          }
      } else {
          query.setCount(-1);
      }
      // Sets the start, limit, and order by accepted species:
      criteria.setFirstResult(query.getStart());
      if (query.getLimit() != OccurrenceQuery.UNLIMITED) {
        criteria.setMaxResults(query.getLimit());
      }
      criteria.setProjection(null);
      /*for (OrderKey orderKey : orderingMap) {
        String property = orderKey.getAttributeName();
        String occAttribute = getOccurrencePropertyName(property);
        if (orderKey.isAsc()) {
          log.info("order by property " + occAttribute + " in ascending order");
          criteria.addOrder(Order.asc(occAttribute));
        } else {
          log.info("order by property " + occAttribute + " in descending order");
          criteria.addOrder(Order.desc(occAttribute));
        }
      }*/
      criteria.addOrder(Order.asc("id"));
      results = criteria.list();
      
      // filters.addAll(removedFilters);
      log.debug("find by example successful, result size: " + results.size());
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      re.printStackTrace();
      throw re;
    } catch (Exception e) {
      log.error("unexpected error: ", e);
      e.printStackTrace();
      throw e;
    }

  }

  private List<Integer> findIds(OccurrenceQuery query, Set<OccurrenceFilter> filters, User user,
      int tryCount) throws Exception {
    log.debug("finding Occurrence instances by query.");
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();
      List<Integer> results = null;
      Criteria criteria = session.createCriteria(Occurrence.class).setProjection(Projections.id());
      OccurrenceFilter userReviewFilter = null;
      for (OccurrenceFilter filter : filters) {
        if (filter.column.equals(filter.getPropertyName("userReviewed"))) {
          userReviewFilter = filter;
        }
      }
      filters.remove(userReviewFilter);
      List<OrderKey> orderingMap = query.getOrderingMap();
      for (OrderKey orderKey : orderingMap) {
        String property = orderKey.getAttributeName();
        if (orderKey.isAsc()) {
          //criteria.addOrder(Order.asc(getOccurrencePropertyName(property)));
        } else {
          //criteria.addOrder(Order.desc(getOccurrencePropertyName(property)));
        }
      }
      // Sets the start, limit, and order by accepted species:
      criteria.setFirstResult(query.getStart());
      if (query.getLimit() != OccurrenceQuery.UNLIMITED) {
        criteria.setMaxResults(query.getLimit());
      }
      OccurrenceFilter idsFilter = null;
      if (userReviewFilter != null) {
        Boolean reviewed = null;
        if (userReviewFilter.operator == Operator.EQUAL) {
          reviewed = (Boolean) userReviewFilter.getValue();
        }
        List<Integer> occIds = recordReviewDb.getRecordReviewOccIds(user.getId(), reviewed);
        System.out.println(occIds.size());
        if (occIds.isEmpty()) {
          occIds.add(0);
        }
        idsFilter = new OccurrenceFilter("id", Operator.IN, occIds);
        filters.add(idsFilter);
      }
      log.info("find filters: "
          + addCreterionByFilters(criteria, user, filters, query.getResultFilter(), tryCount));
      if (userReviewFilter != null) {
        filters.remove(idsFilter);
        filters.add(userReviewFilter);
      }
      results = criteria.list();
      if (query.isCountTotalResults()) {
        criteria.setFirstResult(0);
        criteria.setProjection(Projections.count("id"));
        Integer count = (Integer) criteria.uniqueResult();
        if (count != null) {
          query.setCount(count);
        }
      } else {
        query.setCount(-1);
      }
      // filters.addAll(removedFilters);
      log.debug("find by example successful, result size: " + results.size());
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      re.printStackTrace();
      throw re;
    } catch (Exception e) {
      log.error("unexpected error: ", e);
      e.printStackTrace();
      throw e;
    }
  }

  private List<Integer> findOccIdsByAttributeValue(AttributeValue attributeValue) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    Query query = session.createQuery("select id from Occurrence o where o."
        + attributeValue.getAttribute() + "='" + attributeValue.getValue() + "'");
    List<Integer> ids = query.list();
    ManagedSession.commitTransaction(session);
    return ids;
  }

  private List<Integer> findValidatedOccIdsByAttributeValue(AttributeValue attributeValue) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    Query query = session.createQuery("select id from Occurrence o where o."
        + attributeValue.getAttribute() + "='" + attributeValue.getValue()
        + "' and o.validated=true");
    List<Integer> ids = query.list();
    ManagedSession.commitTransaction(session);
    return ids;
  }
  //{WD}
  private List<Integer> findValidatedOccIdsByAttributeValue(AttributeValue attributeValue, boolean isMarrine, boolean isTerrestrial) {
	    Session session = ManagedSession.createNewSessionAndTransaction();
	    if(isMarrine&&isTerrestrial){
	    	return findValidatedOccIdsByAttributeValue(attributeValue);
	    }
	    List<Integer> ids = new ArrayList<Integer>();
	    int isM = isMarrine?1:0;
	    int isT = isTerrestrial?1:0;
	    Connection con = session.connection();
	    Statement st;
		try {
			st = con.createStatement();
			ResultSet rst = st.executeQuery("select o.id from Occurrence o left join taxonomy t on (upper(o.acceptedspecies) = upper(t.acceptedspecies)) where upper(o."
		        + attributeValue.getAttribute() + ")=upper('" + attributeValue.getValue()
		        + "') and o.validated=true and (t.ismarine = " + isM + " or t.isterrestrial = " + isT + ") and o.trbdata is not true");
			
		    while(rst.next()) {
				ids.add(new Integer(rst.getInt(1)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    ManagedSession.commitTransaction(session);
	    return ids;
  }

  private Object[] getComplexCriterion(String values[], String fieldName, String operator) {
    Criterion criterion = null;
    String queryFilter = null;
    String filterOperator = operator.equals("!=") ? " and " : " or ";
    for (String value : values) {
      if (criterion == null) {
        if (operator.equals("!=")) {
          criterion = Restrictions.ne(fieldName, value);
        } else if (operator.equals("=")) {
          criterion = Restrictions.eq(fieldName, value);
        }
        queryFilter = fieldName + operator + "'" + value + "'";
      } else {
        queryFilter += filterOperator + fieldName + operator + "'" + value + "'";
        if (operator.equals("!=")) {
          criterion = Restrictions.and(criterion, Restrictions.ne(fieldName, value));
        } else if (operator.equals("=")) {
          criterion = Restrictions.or(criterion, Restrictions.eq(fieldName, value));
        }
      }
    }
    return new Object[] { criterion, queryFilter };
  }

  /**
   * Gets A criterion that constrain the query to the given {@link ResultFilter}
   * .
   * 
   * @param user logged in user id
   * @param resultFilter the {@link ResultFilter} to determine what public
   *          filter is added.
   * 
   *          sharedUsersCSV format: space userid space, space userid space,
   *          ..., .There is and comma in the end of the id list to rule out end
   *          of the list corner case when a user id is removed from the list. A
   *          space before and after userId to ensure searching user id in
   *          sharedUsersCSV return the correct result (i.e 18 and 8 are there
   *          same in like query without extra spaces).
   * @param isMyOccurrence TODO
   * @param queryFilters TODO
   * 
   * @return {@link Criterion} for Hibernate {@link Criteria}
   */
  private Criterion getPublicCriterion(User user, ResultFilter resultFilter,
      boolean isMyOccurrence, Set<String> queryFilters) {
    // removeOwnerFilter(filters);
    if (resultFilter == null) {
      return null;
    }
    boolean sAdmin = false;
    if(user != null)
    	sAdmin = new RoleDbImpl().isSAdmin(user.getId());
    Criterion criterion = null;
    String publicCol = getOccurrencePropertyName("public");
    String ownerCol = getOccurrencePropertyName("ownerEmail");
    switch (resultFilter) {
    case PUBLIC:
      criterion = Restrictions.eq(publicCol, true);
      queryFilters.add(criterion.toString());
      break;
    case PRIVATE:
    	if(sAdmin){
    		criterion = Restrictions.eq(publicCol, false);
    		queryFilters.add(publicCol + "=false ");
    	} else {
      criterion = Restrictions.and(Restrictions.eq(publicCol, false),
          Restrictions.eq(ownerCol, user.getEmail()));
      queryFilters.add(publicCol + "=false and " + ownerCol + "='" + user.getEmail() + "'");
    	}
      break;
    case BOTH:
    	if(sAdmin){
    		Criterion publicCri = Restrictions.eq(publicCol, true);
    		Criterion privateCri = Restrictions.eq(publicCol, false);
    		criterion = Restrictions.or(publicCri, privateCri);
            queryFilters.add(publicCri.toString() + " or " + privateCri.toString());
    	} else {
	      Criterion publicCri = Restrictions.eq(publicCol, true);
	      Criterion privateCri = Restrictions.and(Restrictions.eq(publicCol, false),
	          Restrictions.eq(ownerCol, user == null ? null : user.getEmail()));
	      String publicQ = publicCri.toString();
	      String privateQ = publicCol + "=false and " + ownerCol + "='" + user.getEmail() + "'";
	      if (!isMyOccurrence) {
	        privateCri = Restrictions.or(privateCri,
	            Restrictions.ilike("sharedUsersCSV", user.getEmail(), MatchMode.ANYWHERE));
	        criterion = Restrictions.or(publicCri, privateCri);
	        queryFilters.add(publicQ + " or " + privateQ + " or " + "sharedUsersCSV like '%"
	            + user.getEmail() + "%'");
	      } else {
	        criterion = Restrictions.or(publicCri, privateCri);
	        queryFilters.add(publicQ + " or " + privateQ);
	      }
    	}
      break;
    }

    // if (!isMyOccurrence && userId != null) {
    // criteria.add(Restrictions.ilike("sharedUsersCSV", " " + userId + " "));
    // queryFilters.add("sharedUsersCSV like '% " + userId + " ");
    //
    // }
    return criterion;
  }

  private boolean isTaxonomicChanged(AttributeValue attributeValue, Occurrence occA, Occurrence occB) {
    String propertyName = StringUtil.capFirstLetter(attributeValue.getAttribute());
    Class<Occurrence> oClass = Occurrence.class;
    try {
      Method getMethod = oClass.getMethod("get" + propertyName);
      Object valueA = getMethod.invoke(occA);
      Object valueB = getMethod.invoke(occB);
      if (valueA != null) {
        if (valueB != null) {
          return !valueA.toString().equalsIgnoreCase(valueB.toString());
        } else {
          return true;
        }
      } else if (valueB != null) {
        return true;
      } else {
        return false;
      }

    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
    } catch (IllegalArgumentException e) {
    } catch (IllegalAccessException e) {

    } catch (InvocationTargetException e) {

    }
    return false;
  }

  private boolean isTaxonomicMatch(AttributeValue attributeValue, Occurrence occurrence) {
    String propertyName = StringUtil.capFirstLetter(attributeValue.getAttribute());
    Class<Occurrence> oClass = Occurrence.class;
    try {
      Method getMethod = oClass.getMethod("get" + propertyName);
      Object obj = getMethod.invoke(occurrence);
      if (obj != null) {
        return obj.toString().equals(attributeValue.getValue());
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
    } catch (IllegalArgumentException e) {
    } catch (IllegalAccessException e) {

    } catch (InvocationTargetException e) {

    }
    return false;
  }

  private void resetOccurrenceReviewedStatus(int id) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    Query query = session.createQuery("update Occurrence set reviewed=NULL where id=" + id);
    query.executeUpdate();
    ManagedSession.commitTransaction(session);
  }

  /**
   * Updates Occurrences base on query and user id
   * 
   * @param user owner id for this update
   * @param searchFilters a filters that use for find Occurrences.
   * @param updateFilters a filters that use for updating {@link Occurrence}.
   * @param resultFilter the {@link ResultFilter} to determine what public
   *          filter is added.
   * @param tryCount TODO
   * @return number of records get updated;
   */
  private int updateByQuery(User user, Set<OccurrenceFilter> searchFilters,
      Set<OccurrenceFilter> updateFilters, ResultFilter resultFilter, int tryCount) {

    log.debug("updating Occurrences instances by query.");
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      String updateQuery = createUpdateQuery(session.createCriteria(Occurrence.class),
          searchFilters, updateFilters, user, resultFilter, tryCount);
      log.info(updateQuery);
      int updatedRecords = session.createQuery(updateQuery).executeUpdate();

      ManagedSession.commitTransaction(session);
      return updatedRecords;
    } catch (RuntimeException re) {
      log.error("update by query failed", re);
      throw re;
    } finally {
      // session.close();
    }
  }
  
  @Override
  /**
   * recreate all rReviewrecords attached to the occurrence
   */
  public void resetRecordReview(Occurrence occurrence, boolean isStable) {
  	 log.debug("attaching dirty Occurrence instance");
  	    try {
  	    	//Session session = HibernateUtil.getCurrentSession();
		  Session session = ManagedSession.createNewSession();
		  //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
  	      occurrence.setLastUpdated((new Timestamp(System.currentTimeMillis())).toString());
  	      occurrence.setReviewed(null);
  	      occurrence.setStability(isStable);
  	      session.saveOrUpdate(occurrence);
//  	      ManagedSession.commitTransaction(session);
	   
  	      assignReviewRecords(occurrence);
  	      log.debug("attach successful");
  	      ManagedSession.commitTransaction(session);
  	      
  	    } catch (RuntimeException re) {
  	      log.error("attach failed", re);
  	      //HibernateUtil.rollbackTransaction();
  	      throw re;
  	    }
  	
  }
  
  public void resetRecordReview(Occurrence occurrence, boolean isStable, Session sess) {
	  	 log.debug("attaching dirty Occurrence instance");
	  	   
  	      occurrence.setLastUpdated((new Timestamp(System.currentTimeMillis())).toString());
  	      occurrence.setReviewed(null);
  	      occurrence.setStability(isStable);
  	      sess.saveOrUpdate(occurrence);		   
  	      assignReviewRecords(occurrence);
  	      
  	      log.debug("attach successful");	  	   
	  }  
}
