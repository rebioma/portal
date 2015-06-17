package org.rebioma.client.bean;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.DetailView;
import org.rebioma.client.bean.OccurrenceSummary.OccurrenceFieldItem;

// Generated Sep 17, 2008 10:15:17 AM by Hibernate Tools 3.2.2.GA

/**
 * Occurrence generated by hbm2java
 */
public class Occurrence implements java.io.Serializable {

	private String acceptedClass;

	private String acceptedFamily;
	private String acceptedGenus;
	private String acceptedKingdom;
	private String acceptedNomenclaturalCode;
	private String acceptedOrder;
	private String acceptedPhylum;
	private String acceptedSpecies;
	private String acceptedSpecificEpithet;
	private String acceptedSubfamily;
	private String acceptedSubgenus;
	private String acceptedSuborder;
	private String adjustedCoordinateUncertaintyInMeters;
	private String attributes;
	private String authorYearOfScientificName;
	private String basisOfRecord;
	private String catalogNumber;
	private String catalogNumberNumeric;
	private String class_;
	private String collectingMethod;
	private String collectionCode;
	private String collector;
	private String collectorNumber;
	private String continent;
	private String coordinateUncertaintyInMeters;
	private String country;
	private String county;
	private String dateIdentified;
	private String dateLastModified;
	private String dayCollected;
	private String dayOfYear;
	private String decimalLatitude;
	private String decimalLongitude;
	private String decLatInWgs84;
	private String decLongInWgs84;
	private String demelevation;
	private String disposition;
	private String earliestDateCollected;
	private Boolean emailVisible;
	private String etpTotal1950;
	private String etpTotal2000;
	private String etpTotalfuture;
	private String family;
	private String fieldNotes;
	private String fieldNumber;
	private String footprintSpatialFit;
	private String footprintWkt;
	private String genBankNumber;
	private String genus;
	private String geodeticDatum;
	private String geolStrech;
	private String georeferenceProtocol;
	private String georeferenceRemarks;
	private String georeferenceSources;
	private String georeferenceVerificationStatus;
	private String globalUniqueIdentifier;
	private String higherGeography;
	private String higherTaxon;
	private Integer id;
	private String identificationQualifer;
	private String identifiedBy;
	private String imageUrl;
	private String individualCount;
	private String informationWithheld;
	private String infraspecificEpithet;
	private String infraspecificRank;
	private String institutionCode;
	private String island;
	private String islandGroup;
	private String kingdom;
	private String lastUpdated;
	private String latestDateCollected;
	private String lifeStage;
	private String locality;
	private String maximumDepthInMeters;
	private String maximumElevationInMeters;
	private String maxPerc1950;
	private String maxPerc2000;
	private String maxPercfuture;
	private String maxtemp1950;
	private String maxTemp2000;
	private String maxTempfuture;
	private String minimumDepthInMeters;
	private String minimumElevationInMeters;
	private String minPerc1950;
	private String minPerc2000;
	private String minPercfuture;
	private String minTemp1950;
	private String minTemp2000;
	private String minTempfuture;
	private String monthCollected;
	private String nomenclaturalCode;
	private Boolean obfuscated;
	private String order_;
	private String otherCatalogNumbers;
	private String ownerEmail;
	private Integer owner;
	private String pfc1950;
	private String pfc1970;
	private String pfc1990;
	private String pfc2000;
	private String phylum;
	private String pointRadiusSpatialFit;
	private String preparations;
	private Boolean public_;
	private String realMar1950;
	private String realMar2000;
	private String realMarfuture;
	private String realMat1950;
	private String realMat2000;
	private String realMatfuture;
	private String relatedCatalogedItems;
	private String relatedInformation;
	private String remarks;
	private String scientificName;
	private String sex;
	private String sharedUsersCSV;
	private String specificEpithet;
	private String stateProvince;
	private Boolean tapirAccessible;
	private String timeCreated;
	private String typeStatus;
	private Boolean validated;
	private String validationError;
	private String validDistributionFlag;
	private String verbatimCollectingDate;
	private String verbatimCoordinates;
	private String verbatimCoordinateSystem;
	private String verbatimDepth;
	private String verbatimElevation;
	private String verbatimLatitude;
	private String verbatimLongitude;
	private String verbatimSpecies;
	private Boolean vettable;
	private Boolean vetted;
	private String vettingError;
	private String waterBody;
	private String wbpos1950;
	private String wbpos2000;
	private String wbposfuture;
	private String wbyear1950;
	private String wbyear2000;
	private String wbyearfuture;
	private String yearCollected;
	private String bibliographicCitation;
	private String preparator;
	private String verbatimLocality;
	private String habitat;
	private String vernacularName;
	private String associatedReferences;
	private String localityDescription;
	private Boolean noAssignation;
	private Boolean reviewed;

	private Boolean stability;
	
	private String accessRights;
	private String license;
	private String references;

	public Occurrence() {
	}

	public Occurrence(Integer owner, Boolean public_, Boolean vettable,
			Boolean validated, Boolean vetted, Boolean tapirAccessible) {
		this.public_ = public_;
		this.vettable = vettable;
		this.validated = validated;
		this.vetted = vetted;
		this.tapirAccessible = tapirAccessible;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Occurrence)) {
			return false;
		}
		if (this.id == null) {
			return false;
		}
		Occurrence compare = (Occurrence) obj;
		return compare.getId() != null && id.equals(compare.getId());
	}

	/**
	 * @return the stability
	 */
	public Boolean getStability() {
		return stability;
	}

	/**
	 * @param stability
	 *            the stability to set
	 */
	public void setStability(Boolean stability) {
		this.stability = stability;
	}

	public String getAcceptedClass() {
		return this.acceptedClass;
	}

	public String getAcceptedFamily() {
		return this.acceptedFamily;
	}

	public String getAcceptedGenus() {
		return this.acceptedGenus;
	}

	public String getAcceptedKingdom() {
		return this.acceptedKingdom;
	}

	public String getAcceptedNomenclaturalCode() {
		return this.acceptedNomenclaturalCode;
	}

	public String getAcceptedOrder() {
		return this.acceptedOrder;
	}

	public String getAcceptedPhylum() {
		return this.acceptedPhylum;
	}

	public String getAcceptedSpecies() {
		return this.acceptedSpecies;
	}

	public String getAcceptedSpecificEpithet() {
		return this.acceptedSpecificEpithet;
	}

	public String getAcceptedSubfamily() {
		return this.acceptedSubfamily;
	}

	public String getAcceptedSubgenus() {
		return this.acceptedSubgenus;
	}

	public String getAcceptedSuborder() {
		return this.acceptedSuborder;
	}

	public String getAdjustedCoordinateUncertaintyInMeters() {
		return this.adjustedCoordinateUncertaintyInMeters;
	}

	public String getAttributes() {
		return this.attributes;
	}

	public String getAuthorYearOfScientificName() {
		return this.authorYearOfScientificName;
	}

	public String getBasisOfRecord() {
		return this.basisOfRecord;
	}

	public String getCatalogNumber() {
		return this.catalogNumber;
	}

	public String getCatalogNumberNumeric() {
		return this.catalogNumberNumeric;
	}

	public String getClass_() {
		return this.class_;
	}

	public String getCollectingMethod() {
		return this.collectingMethod;
	}

	public String getCollectionCode() {
		return this.collectionCode;
	}

	public String getCollector() {
		return this.collector;
	}

	public String getCollectorNumber() {
		return this.collectorNumber;
	}

	public String getContinent() {
		return this.continent;
	}

	public String getCoordinateUncertaintyInMeters() {
		return this.coordinateUncertaintyInMeters;
	}

	public String getCountry() {
		return this.country;
	}

	public String getCounty() {
		return this.county;
	}

	public String getDateIdentified() {
		return this.dateIdentified;
	}

	public String getDateLastModified() {
		return this.dateLastModified;
	}

	public String getDayCollected() {
		return this.dayCollected;
	}

	public String getDayOfYear() {
		return this.dayOfYear;
	}

	public String getDecimalLatitude() {
		return this.decimalLatitude;
	}

	public String getDecimalLongitude() {
		return this.decimalLongitude;
	}

	public String getDecLatInWgs84() {
		return this.decLatInWgs84;
	}

	public String getDecLongInWgs84() {
		return this.decLongInWgs84;
	}

	public String getDemelevation() {
		return this.demelevation;
	}

	public String getDisposition() {
		return this.disposition;
	}

	public String getEarliestDateCollected() {
		return this.earliestDateCollected;
	}

	public String getEtpTotal1950() {
		return this.etpTotal1950;
	}

	public String getEtpTotal2000() {
		return this.etpTotal2000;
	}

	public String getEtpTotalfuture() {
		return this.etpTotalfuture;
	}

	public String getFamily() {
		return this.family;
	}

	public String getFieldNotes() {
		return this.fieldNotes;
	}

	public String getFieldNumber() {
		return this.fieldNumber;
	}

	public String getFootprintSpatialFit() {
		return this.footprintSpatialFit;
	}

	public String getFootprintWkt() {
		return this.footprintWkt;
	}

	public String getGenBankNumber() {
		return this.genBankNumber;
	}

	public String getGenus() {
		return this.genus;
	}

	public String getGeodeticDatum() {
		return this.geodeticDatum;
	}

	public String getGeolStrech() {
		return this.geolStrech;
	}

	public String getGeoreferenceProtocol() {
		return this.georeferenceProtocol;
	}

	public String getGeoreferenceRemarks() {
		return this.georeferenceRemarks;
	}

	public String getGeoreferenceSources() {
		return this.georeferenceSources;
	}

	public String getGeoreferenceVerificationStatus() {
		return this.georeferenceVerificationStatus;
	}

	public String getGlobalUniqueIdentifier() {
		return this.globalUniqueIdentifier;
	}

	public String getHigherGeography() {
		return this.higherGeography;
	}

	public String getHigherTaxon() {
		return this.higherTaxon;
	}

	public Integer getId() {
		return this.id;
	}

	public String getIdentificationQualifer() {
		return this.identificationQualifer;
	}

	public String getIdentifiedBy() {
		return this.identifiedBy;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public String getIndividualCount() {
		return this.individualCount;
	}

	public String getInformationWithheld() {
		return this.informationWithheld;
	}

	public String getInfraspecificEpithet() {
		return this.infraspecificEpithet;
	}

	public String getInfraspecificRank() {
		return this.infraspecificRank;
	}

	public String getInstitutionCode() {
		return this.institutionCode;
	}

	public String getIsland() {
		return this.island;
	}

	public String getIslandGroup() {
		return this.islandGroup;
	}

	public String getKingdom() {
		return this.kingdom;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public String getLatestDateCollected() {
		return this.latestDateCollected;
	}

	public String getLifeStage() {
		return this.lifeStage;
	}

	public String getLocality() {
		return this.locality;
	}

	public String getMaximumDepthInMeters() {
		return this.maximumDepthInMeters;
	}

	public String getMaximumElevationInMeters() {
		return this.maximumElevationInMeters;
	}

	public String getMaxPerc1950() {
		return this.maxPerc1950;
	}

	public String getMaxPerc2000() {
		return this.maxPerc2000;
	}

	public String getMaxPercfuture() {
		return this.maxPercfuture;
	}

	public String getMaxtemp1950() {
		return this.maxtemp1950;
	}

	public String getMaxTemp2000() {
		return this.maxTemp2000;
	}

	public String getMaxTempfuture() {
		return this.maxTempfuture;
	}

	public String getMinimumDepthInMeters() {
		return this.minimumDepthInMeters;
	}

	public String getMinimumElevationInMeters() {
		return this.minimumElevationInMeters;
	}

	public String getMinPerc1950() {
		return this.minPerc1950;
	}

	public String getMinPerc2000() {
		return this.minPerc2000;
	}

	public String getMinPercfuture() {
		return this.minPercfuture;
	}

	public String getMinTemp1950() {
		return this.minTemp1950;
	}

	public String getMinTemp2000() {
		return this.minTemp2000;
	}

	public String getMinTempfuture() {
		return this.minTempfuture;
	}

	public String getMonthCollected() {
		return this.monthCollected;
	}

	public String getNomenclaturalCode() {
		return this.nomenclaturalCode;
	}

	public String getOrder_() {
		return this.order_;
	}

	public String getOtherCatalogNumbers() {
		return this.otherCatalogNumbers;
	}

	public Integer getOwner() {
		return owner;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public String getPfc1950() {
		return this.pfc1950;
	}

	public String getPfc1970() {
		return this.pfc1970;
	}

	public String getPfc1990() {
		return this.pfc1990;
	}

	public String getPfc2000() {
		return this.pfc2000;
	}

	public String getPhylum() {
		return this.phylum;
	}

	public String getPointRadiusSpatialFit() {
		return this.pointRadiusSpatialFit;
	}

	public String getPreparations() {
		return this.preparations;
	}

	public String getRealMar1950() {
		return this.realMar1950;
	}

	public String getRealMar2000() {
		return this.realMar2000;
	}

	public String getRealMarfuture() {
		return this.realMarfuture;
	}

	public String getRealMat1950() {
		return this.realMat1950;
	}

	public String getRealMat2000() {
		return this.realMat2000;
	}

	public String getRealMatfuture() {
		return this.realMatfuture;
	}

	public String getRelatedCatalogedItems() {
		return this.relatedCatalogedItems;
	}

	public String getRelatedInformation() {
		return this.relatedInformation;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public String getScientificName() {
		return this.scientificName;
	}

	public String getSex() {
		return this.sex;
	}

	public String getSharedUsersCSV() {
		return sharedUsersCSV;
	}

	public String getSpecificEpithet() {
		return this.specificEpithet;
	}

	public String getStateProvince() {
		return this.stateProvince;
	}

	public String getTimeCreated() {
		return timeCreated;
	}

	public String getTypeStatus() {
		return this.typeStatus;
	}

	public String getValidationError() {
		return this.validationError;
	}

	public String getValidDistributionFlag() {
		return this.validDistributionFlag;
	}

	public String getVerbatimCollectingDate() {
		return this.verbatimCollectingDate;
	}

	public String getVerbatimCoordinates() {
		return this.verbatimCoordinates;
	}

	public String getVerbatimCoordinateSystem() {
		return this.verbatimCoordinateSystem;
	}

	public String getVerbatimDepth() {
		return this.verbatimDepth;
	}

	public String getVerbatimElevation() {
		return this.verbatimElevation;
	}

	public String getVerbatimLatitude() {
		return this.verbatimLatitude;
	}

	public String getVerbatimLongitude() {
		return this.verbatimLongitude;
	}

	public String getVerbatimSpecies() {
		return this.verbatimSpecies;
	}

	public String getVettingError() {
		return this.vettingError;
	}

	public String getWaterBody() {
		return this.waterBody;
	}

	public String getWbpos1950() {
		return this.wbpos1950;
	}

	public String getWbpos2000() {
		return this.wbpos2000;
	}

	public String getWbposfuture() {
		return this.wbposfuture;
	}

	public String getWbyear1950() {
		return this.wbyear1950;
	}

	public String getWbyear2000() {
		return this.wbyear2000;
	}

	public String getWbyearfuture() {
		return this.wbyearfuture;
	}

	public String getYearCollected() {
		return this.yearCollected;
	}

	public int hashCode() {
		if (id == null) {
			return 0;
		}
		return id.hashCode();
	}

	public Boolean isEmailVisible() {
		return emailVisible;
	}

	public Boolean isObfuscated() {
		return obfuscated;
	}

	public Boolean isPublic_() {
		return this.public_;
	}

	public Boolean isTapirAccessible() {
		return this.tapirAccessible;
	}

	public Boolean isValidated() {
		return this.validated;
	}

	public Boolean isVettable() {
		return this.vettable;
	}

	public Boolean isVetted() {
		return this.vetted;
	}

	public void setAcceptedClass(String acceptedClass) {
		this.acceptedClass = acceptedClass;
	}

	public void setAcceptedFamily(String acceptedFamily) {
		this.acceptedFamily = acceptedFamily;
	}

	public void setAcceptedGenus(String acceptedGenus) {
		this.acceptedGenus = acceptedGenus;
	}

	public void setAcceptedKingdom(String acceptedKingdom) {
		this.acceptedKingdom = acceptedKingdom;
	}

	public void setAcceptedNomenclaturalCode(String acceptedNomenclaturalCode) {
		this.acceptedNomenclaturalCode = acceptedNomenclaturalCode;
	}

	public void setAcceptedOrder(String acceptedOrder) {
		this.acceptedOrder = acceptedOrder;
	}

	public void setAcceptedPhylum(String acceptedPhylum) {
		this.acceptedPhylum = acceptedPhylum;
	}

	public void setAcceptedSpecies(String acceptedSpecies) {
		this.acceptedSpecies = acceptedSpecies;
	}

	public void setAcceptedSpecificEpithet(String acceptedSpecificEpithet) {
		this.acceptedSpecificEpithet = acceptedSpecificEpithet;
	}

	public void setAcceptedSubfamily(String acceptedSubfamily) {
		this.acceptedSubfamily = acceptedSubfamily;
	}

	public void setAcceptedSubgenus(String acceptedSubgenus) {
		this.acceptedSubgenus = acceptedSubgenus;
	}

	public void setAcceptedSuborder(String acceptedSuborder) {
		this.acceptedSuborder = acceptedSuborder;
	}

	public void setAdjustedCoordinateUncertaintyInMeters(
			String adjustedCoordinateUncertaintyInMeters) {
		this.adjustedCoordinateUncertaintyInMeters = adjustedCoordinateUncertaintyInMeters;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public void setAuthorYearOfScientificName(String authorYearOfScientificName) {
		this.authorYearOfScientificName = authorYearOfScientificName;
	}

	public void setBasisOfRecord(String basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	public void setCatalogNumberNumeric(String catalogNumberNumeric) {
		this.catalogNumberNumeric = catalogNumberNumeric;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public void setCollectingMethod(String collectingMethod) {
		this.collectingMethod = collectingMethod;
	}

	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}

	public void setCollector(String collector) {
		this.collector = collector;
	}

	public void setCollectorNumber(String collectorNumber) {
		this.collectorNumber = collectorNumber;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public void setCoordinateUncertaintyInMeters(
			String coordinateUncertaintyInMeters) {
		this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public void setDateIdentified(String dateIdentified) {
		this.dateIdentified = dateIdentified;
	}

	public void setDateLastModified(String dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public void setDayCollected(String dayCollected) {
		this.dayCollected = dayCollected;
	}

	public void setDayOfYear(String dayOfYear) {
		this.dayOfYear = dayOfYear;
	}

	public void setDecimalLatitude(String decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	public void setDecimalLongitude(String decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	public void setDecLatInWgs84(String decLatInWgs84) {
		this.decLatInWgs84 = decLatInWgs84;
	}

	public void setDecLongInWgs84(String decLongInWgs84) {
		this.decLongInWgs84 = decLongInWgs84;
	}

	public void setDemelevation(String demelevation) {
		this.demelevation = demelevation;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public void setEarliestDateCollected(String earliestDateCollected) {
		this.earliestDateCollected = earliestDateCollected;
	}

	public void setEmailVisible(Boolean emailVisible) {
		this.emailVisible = emailVisible;
	}

	public void setEtpTotal1950(String etpTotal1950) {
		this.etpTotal1950 = etpTotal1950;
	}

	public void setEtpTotal2000(String etpTotal2000) {
		this.etpTotal2000 = etpTotal2000;
	}

	public void setEtpTotalfuture(String etpTotalfuture) {
		this.etpTotalfuture = etpTotalfuture;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public void setFieldNotes(String fieldNotes) {
		this.fieldNotes = fieldNotes;
	}

	public void setFieldNumber(String fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public void setFootprintSpatialFit(String footprintSpatialFit) {
		this.footprintSpatialFit = footprintSpatialFit;
	}

	public void setFootprintWkt(String footprintWkt) {
		this.footprintWkt = footprintWkt;
	}

	public void setGenBankNumber(String genBankNumber) {
		this.genBankNumber = genBankNumber;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public void setGeodeticDatum(String geodeticDatum) {
		this.geodeticDatum = geodeticDatum;
	}

	public void setGeolStrech(String geolStrech) {
		this.geolStrech = geolStrech;
	}

	public void setGeoreferenceProtocol(String georeferenceProtocol) {
		this.georeferenceProtocol = georeferenceProtocol;
	}

	public void setGeoreferenceRemarks(String georeferenceRemarks) {
		this.georeferenceRemarks = georeferenceRemarks;
	}

	public void setGeoreferenceSources(String georeferenceSources) {
		this.georeferenceSources = georeferenceSources;
	}

	public void setGeoreferenceVerificationStatus(
			String georeferenceVerificationStatus) {
		this.georeferenceVerificationStatus = georeferenceVerificationStatus;
	}

	public void setGlobalUniqueIdentifier(String globalUniqueIdentifier) {
		this.globalUniqueIdentifier = globalUniqueIdentifier;
	}

	public void setHigherGeography(String higherGeography) {
		this.higherGeography = higherGeography;
	}

	public void setHigherTaxon(String higherTaxon) {
		this.higherTaxon = higherTaxon;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdentificationQualifer(String identificationQualifer) {
		this.identificationQualifer = identificationQualifer;
	}

	public void setIdentifiedBy(String identifiedBy) {
		this.identifiedBy = identifiedBy;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setIndividualCount(String individualCount) {
		this.individualCount = individualCount;
	}

	public void setInformationWithheld(String informationWithheld) {
		this.informationWithheld = informationWithheld;
	}

	public void setInfraspecificEpithet(String infraspecificEpithet) {
		this.infraspecificEpithet = infraspecificEpithet;
	}

	public void setInfraspecificRank(String infraspecificRank) {
		this.infraspecificRank = infraspecificRank;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public void setIsland(String island) {
		this.island = island;
	}

	public void setIslandGroup(String islandGroup) {
		this.islandGroup = islandGroup;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void setLatestDateCollected(String latestDateCollected) {
		this.latestDateCollected = latestDateCollected;
	}

	public void setLifeStage(String lifeStage) {
		this.lifeStage = lifeStage;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void setMaximumDepthInMeters(String maximumDepthInMeters) {
		this.maximumDepthInMeters = maximumDepthInMeters;
	}

	public void setMaximumElevationInMeters(String maximumElevationInMeters) {
		this.maximumElevationInMeters = maximumElevationInMeters;
	}

	public void setMaxPerc1950(String maxPerc1950) {
		this.maxPerc1950 = maxPerc1950;
	}

	public void setMaxPerc2000(String maxPerc2000) {
		this.maxPerc2000 = maxPerc2000;
	}

	public void setMaxPercfuture(String maxPercfuture) {
		this.maxPercfuture = maxPercfuture;
	}

	public void setMaxtemp1950(String maxtemp1950) {
		this.maxtemp1950 = maxtemp1950;
	}

	public void setMaxTemp2000(String maxTemp2000) {
		this.maxTemp2000 = maxTemp2000;
	}

	public void setMaxTempfuture(String maxTempfuture) {
		this.maxTempfuture = maxTempfuture;
	}

	public void setMinimumDepthInMeters(String minimumDepthInMeters) {
		this.minimumDepthInMeters = minimumDepthInMeters;
	}

	public void setMinimumElevationInMeters(String minimumElevationInMeters) {
		this.minimumElevationInMeters = minimumElevationInMeters;
	}

	public void setMinPerc1950(String minPerc1950) {
		this.minPerc1950 = minPerc1950;
	}

	public void setMinPerc2000(String minPerc2000) {
		this.minPerc2000 = minPerc2000;
	}

	public void setMinPercfuture(String minPercfuture) {
		this.minPercfuture = minPercfuture;
	}

	public void setMinTemp1950(String minTemp1950) {
		this.minTemp1950 = minTemp1950;
	}

	public void setMinTemp2000(String minTemp2000) {
		this.minTemp2000 = minTemp2000;
	}

	public void setMinTempfuture(String minTempfuture) {
		this.minTempfuture = minTempfuture;
	}

	public void setMonthCollected(String monthCollected) {
		this.monthCollected = monthCollected;
	}

	public void setNomenclaturalCode(String nomenclaturalCode) {
		this.nomenclaturalCode = nomenclaturalCode;
	}

	public void setObfuscated(Boolean obfuscated) {
		this.obfuscated = obfuscated;
	}

	public void setOrder_(String order) {
		this.order_ = order;
	}

	public void setOtherCatalogNumbers(String otherCatalogNumbers) {
		this.otherCatalogNumbers = otherCatalogNumbers;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public void setPfc1950(String pfc1950) {
		this.pfc1950 = pfc1950;
	}

	public void setPfc1970(String pfc1970) {
		this.pfc1970 = pfc1970;
	}

	public void setPfc1990(String pfc1990) {
		this.pfc1990 = pfc1990;
	}

	public void setPfc2000(String pfc2000) {
		this.pfc2000 = pfc2000;
	}

	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}

	public void setPointRadiusSpatialFit(String pointRadiusSpatialFit) {
		this.pointRadiusSpatialFit = pointRadiusSpatialFit;
	}

	public void setPreparations(String preparations) {
		this.preparations = preparations;
	}

	public void setPublic_(Boolean public_) {
		this.public_ = public_;
	}

	public void setRealMar1950(String realMar1950) {
		this.realMar1950 = realMar1950;
	}

	public void setRealMar2000(String realMar2000) {
		this.realMar2000 = realMar2000;
	}

	public void setRealMarfuture(String realMarfuture) {
		this.realMarfuture = realMarfuture;
	}

	public void setRealMat1950(String realMat1950) {
		this.realMat1950 = realMat1950;
	}

	public void setRealMat2000(String realMat2000) {
		this.realMat2000 = realMat2000;
	}

	public void setRealMatfuture(String realMatfuture) {
		this.realMatfuture = realMatfuture;
	}

	public void setRelatedCatalogedItems(String relatedCatalogedItems) {
		this.relatedCatalogedItems = relatedCatalogedItems;
	}

	public void setRelatedInformation(String relatedInformation) {
		this.relatedInformation = relatedInformation;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setReviewed(Boolean review) {
		this.reviewed = review;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setSharedUsersCSV(String sharedUsersCSV) {
		this.sharedUsersCSV = sharedUsersCSV;
	}

	public void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public void setTapirAccessible(Boolean tapirAccessible) {
		this.tapirAccessible = tapirAccessible;
	}

	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}

	public void setTypeStatus(String typeStatus) {
		this.typeStatus = typeStatus;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public void setValidationError(String validationError) {
		this.validationError = validationError;
	}

	public void setValidDistributionFlag(String validDistributionFlag) {
		this.validDistributionFlag = validDistributionFlag;
	}

	public void setVerbatimCollectingDate(String verbatimCollectingDate) {
		this.verbatimCollectingDate = verbatimCollectingDate;
	}

	public void setVerbatimCoordinates(String verbatimCoordinates) {
		this.verbatimCoordinates = verbatimCoordinates;
	}

	public void setVerbatimCoordinateSystem(String verbatimCoordinateSystem) {
		this.verbatimCoordinateSystem = verbatimCoordinateSystem;
	}

	public void setVerbatimDepth(String verbatimDepth) {
		this.verbatimDepth = verbatimDepth;
	}

	public void setVerbatimElevation(String verbatimElevation) {
		this.verbatimElevation = verbatimElevation;
	}

	public void setVerbatimLatitude(String verbatimLatitude) {
		this.verbatimLatitude = verbatimLatitude;
	}

	public void setVerbatimLongitude(String verbatimLongitude) {
		this.verbatimLongitude = verbatimLongitude;
	}

	public void setVerbatimSpecies(String verbatimSpecies) {
		this.verbatimSpecies = verbatimSpecies;
	}

	public void setVettable(Boolean vettable) {
		this.vettable = vettable;
	}

	public void setVetted(Boolean vetted) {
		this.vetted = vetted;
	}

	public void setVettingError(String vettingError) {
		this.vettingError = vettingError;
	}

	public void setWaterBody(String waterBody) {
		this.waterBody = waterBody;
	}

	public void setWbpos1950(String wbpos1950) {
		this.wbpos1950 = wbpos1950;
	}

	public void setWbpos2000(String wbpos2000) {
		this.wbpos2000 = wbpos2000;
	}

	public void setWbposfuture(String wbposfuture) {
		this.wbposfuture = wbposfuture;
	}

	public void setWbyear1950(String wbyear1950) {
		this.wbyear1950 = wbyear1950;
	}

	public void setWbyear2000(String wbyear2000) {
		this.wbyear2000 = wbyear2000;
	}

	public void setWbyearfuture(String wbyearfuture) {
		this.wbyearfuture = wbyearfuture;
	}

	public void setYearCollected(String yearCollected) {
		this.yearCollected = yearCollected;
	}

	public String getBibliographicCitation() {
		return bibliographicCitation;
	}

	public void setBibliographicCitation(String bibliographicCitation) {
		this.bibliographicCitation = bibliographicCitation;
	}

	public String getPreparator() {
		return preparator;
	}

	public void setPreparator(String preparator) {
		this.preparator = preparator;
	}

	public String getVerbatimLocality() {
		return verbatimLocality;
	}

	public void setVerbatimLocality(String verbatimLocality) {
		this.verbatimLocality = verbatimLocality;
	}

	public String getHabitat() {
		return habitat;
	}

	public void setHabitat(String habitat) {
		this.habitat = habitat;
	}

	public String getVernacularName() {
		return vernacularName;
	}

	public void setVernacularName(String vernacularName) {
		this.vernacularName = vernacularName;
	}

	public String getAssociatedReferences() {
		return associatedReferences;
	}

	public void setAssociatedReferences(String associatedReferences) {
		this.associatedReferences = associatedReferences;
	}

	public String getLocalityDescription() {
		return localityDescription;
	}

	public void setLocalityDescription(String localityDescription) {
		this.localityDescription = localityDescription;
	}
	
	public Boolean getNoAssignation() {
		return noAssignation;
	}

	public void setNoAssignation(Boolean noAssignation) {
		this.noAssignation = noAssignation;
	}

	public String getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	@Override
	public String toString() {
		return "ID=" + id + " Public=" + public_ + " Validated= " + validated
				+ " Vettable=" + vettable + " Vetted=" + vetted
				+ " TapirAccessible=" + tapirAccessible;
	}
	
	//{WD
	/**
	   * 
	   * Gets a {@link OccurrenceFieldItem} which contains taxonomic authorities
	   * species display and its value.
	   * 
	   * @param occurrence
	   * @return a {@link OccurrenceFieldItem} which contains taxonomic authorities
	   *         species display and its value.
	   */
	  private OccurrenceFieldItem displayField(Occurrence occurrence) {
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
	        DetailView.FieldConstants.GENUS_SPECIES, genusSpecies(occurrence)));
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
	  
	  private String genusSpecies(Occurrence occurrence) {
	    if (occurrence.getAcceptedGenus() == null
	        || occurrence.getAcceptedGenus() == null)
	      return null;
	    return occurrence.getAcceptedGenus().trim() + " "
	        + occurrence.getSpecificEpithet().trim();
	  }
	  
	  public String taxonomicField() {
		  OccurrenceFieldItem item = displayField(this);
		  return item == null ? ApplicationView.getConstants().None() : item.toString();
	  }
	  
	  public String emailField() {
		  	if(emailVisible) 
		  		return this.getOwnerEmail();
		  	else return ApplicationView.getConstants().EmailNotShow();
	  }
	  
	  //WD}
}
