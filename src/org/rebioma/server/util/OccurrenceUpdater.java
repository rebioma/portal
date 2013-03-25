package org.rebioma.server.util;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.bean.Occurrence;

public class OccurrenceUpdater {

  public enum JavaType {
    BOOLEAN, DOUBLE, INT, LONG, STRING
  }

  public interface UpdateListener {
    public void onChanged(String text);

    public void onSaved(Occurrence o);
  }

  public static final String CURATORIAL_EXTENSION_FIELDS[] = {
      "CatalogNumberNumeric", "IdentifiedBy", "DateIdentified",
      "CollectorNumber", "FieldNumber", "VerbatimCollectingDate",
      "VerbatimElevation", "VerbatimDepth", "TypeStatus",
      "OtherCatalogNumbers", "RelatedCatalogedItems", "Disposition",
      "IndividualCount" };

  public static final String DARWIN_CORE_FIELDS[] = { "GlobalUniqueIdentifier",
      "DateLastModified", "BasisOfRecord", "InstitutionCode", "CollectionCode",
      "CatalogNumber", "InformationWithheld", "Remarks", "ScientificName",
      "HigherTaxon", "Kingdom",
      "Phylum",
      "Class",
      "Order",
      "Family",
      "Genus",
      "SpecificEpithet",
      "InfraspecificRank",
      "InfraspecificEpithet",
      "AuthorYearOfScientificName",
      "NomenclaturalCode",
      // Identification Elements (Darwin Core)
      "IdentificationQualifer",
      // Locality Elements (Darwin Core)
      "HigherGeography", "Continent", "WaterBody", "IslandGroup", "Island",
      "Country", "StateProvince", "County", "Locality",
      "MinimumElevationInMeters", "MaximumElevationInMeters",
      "MinimumDepthInMeters",
      "MaximumDepthInMeters",
      // Collecting Event Elements
      "CollectingMethod", "ValidDistributionFlag", "EarliestDateCollected",
      "LatestDateCollected", "DayOfYear", "MonthCollected", "DayCollected",
      "Collector",
      // Biological Elements
      "Sex", "LifeStage", "Attributes",
      // References elements
      "ImageUrl", "RelatedInformation" };

  public static final String ENV_VARIABLES[] = { "Demelevation",
      "EtpTotal2000", "EtpTotalfuture", "EtpTotal1950", "GeolStrech",
      "MaxPerc2000", "MaxPercfuture", "MaxPerc1950", "MaxTemp2000",
      "MaxTempfuture", "Maxtemp1950", "MinPerc2000", "MinPercfuture",
      "MinPerc1950", "MinTemp2000", "MinTempfuture", "MinTemp1950", "Pfc1950",
      "Pfc1970", "Pfc1990", "Pfc2000", "RealMar2000", "RealMarfuture",
      "RealMar1950", "RealMat2000", "RealMatfuture", "RealMat1950",
      "Wbpos2000", "Wbposfuture", "Wbpos1950", "Wbyear2000", "Wbyearfuture",
      "Wbyear1950" };

  public static final String GEOSPATIAL_EXTENDTIONS[] = { "DecimalLatitude",
      "DecimalLongitude", "GeodeticDatum", "CoordinateUncertaintyInMeters",
      "PointRadiusSpatialFit", "VerbatimCoordinates", "VerbatimLatitude",
      "VerbatimLongitude", "VerbatimCoordinateSystem", "GeoreferenceProtocol",
      "GeoreferenceSources", "GeoreferenceVerificationStatus",
      "GeoreferenceRemarks", "FootprintWkt", "FootprintSpatialFit",
      "AcceptedNomenclaturalCode", "DecLatInWgs84", "DecLongInWgs84",
      "AdjustedCoordinateUncertaintyInMeters" };

  public static final String REQUIRED_FIELDS[] = { "Id", "Owner", "OwnerEmail",
      "Public", "Vettable", "Validated", "Vetted", "TapirAccessible",
      "VettingError", "ValidationError", "BasisOfRecord", "YearCollected",
      "Genus", "SpecificEpithet", "DecimalLatitude", "DecimalLongitude",
      "GeodeticDatum", "CoordinateUncertaintyInMeters", "NomenclaturalCode" };
  public static final String TAXONOMIC_AUTHORITY[] = { "NomenclaturalCode",
      "AcceptedKingdom", "AcceptedPhylum", "AcceptedClass", "AcceptedOrder",
      "AcceptedSuborder", "AcceptedFamily", "AcceptedSubfamily",
      "AcceptedGenus", "AcceptedSubgenus", "AcceptedSpecificEpithet",
      "AcceptedSpecies", "VerbatimSpecies" };
  private static List<UpdateListener> updateListeners;

  public static void addUpdateListener(UpdateListener listener) {
    if (updateListeners == null) {
      updateListeners = new ArrayList<UpdateListener>();
    }
    updateListeners.add(listener);
  }

  public static void fireOnSaved(Occurrence o) {
    for (UpdateListener listener : updateListeners) {
      listener.onSaved(o);
    }
  }

  public static void main(String args[]) {

  }

  public static void removeUpdateListener(UpdateListener listener) {
    if (updateListeners != null) {
      updateListeners.remove(listener);
    }
  }

}
