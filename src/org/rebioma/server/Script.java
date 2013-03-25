package org.rebioma.server;

import java.lang.reflect.Method;

import org.rebioma.client.bean.Occurrence;

public class Script {

  static String fields[] = new String[] { "id", "owner", "public_", "vettable",
      "validated", "vetted", "tapirAccessible", "ownerEmail", "vettingError",
      "validationError", "basisOfRecord", "yearCollected", "genus",
      "specificEpithet", "decimalLatitude", "decimalLongitude",
      "geodeticDatum", "coordinateUncertaintyInMeters", "dateLastModified",
      "institutionCode", "collectionCode", "catalogNumber", "scientificName",
      "globalUniqueIdentifier", "informationWithheld", "remarks",
      "higherTaxon", "kingdom", "phylum", "class_", "order", "family",
      "infraspecificRank", "infraspecificEpithet",
      "authorYearOfScientificName", "nomenclaturalCode",
      "identificationQualifer", "higherGeography", "continent", "waterBody",
      "islandGroup", "island", "country", "stateProvince", "county",
      "locality", "minimumElevationInMeters", "maximumElevationInMeters",
      "minimumDepthInMeters", "maximumDepthInMeters", "collectingMethod",
      "validDistributionFlag", "earliestDateCollected", "latestDateCollected",
      "dayOfYear", "monthCollected", "dayCollected", "collector", "sex",
      "lifeStage", "attributes", "imageUrl", "relatedInformation",
      "catalogNumberNumeric", "identifiedBy", "dateIdentified",
      "collectorNumber", "fieldNumber", "fieldNotes", "verbatimCollectingDate",
      "verbatimElevation", "verbatimDepth", "preparations", "typeStatus",
      "genBankNumber", "otherCatalogNumbers", "relatedCatalogedItems",
      "disposition", "individualCount", "pointRadiusSpatialFit",
      "verbatimCoordinates", "verbatimLatitude", "verbatimLongitude",
      "verbatimCoordinateSystem", "georeferenceProtocol",
      "georeferenceSources", "georeferenceVerificationStatus",
      "georeferenceRemarks", "footprintWkt", "footprintSpatialFit",
      "verbatimSpecies", "acceptedSpecies", "acceptedNomenclaturalCode",
      "acceptedKingdom", "acceptedPhylum", "acceptedClass", "acceptedOrder",
      "acceptedSuborder", "acceptedFamily", "acceptedSubfamily",
      "acceptedGenus", "acceptedSubgenus", "acceptedSpecificEpithet",
      "decLatInWgs84", "decLongInWgs84",
      "adjustedCoordinateUncertaintyInMeters", "demelevation", "etpTotal2000",
      "etpTotalfuture", "etpTotal1950", "geolStrech", "maxPerc2000",
      "maxPercfuture", "maxPerc1950", "maxTemp2000", "maxTempfuture",
      "maxtemp1950", "minPerc2000", "minPercfuture", "minPerc1950",
      "minTemp2000", "minTempfuture", "minTemp1950", "pfc1950", "pfc1970",
      "pfc1990", "pfc2000", "realMar2000", "realMarfuture", "realMar1950",
      "realMat2000", "realMatfuture", "realMat1950", "wbpos2000",
      "wbposfuture", "wbpos1950", "wbyear2000", "wbyearfuture", "wbyear1950" };

  public static void main(String args[]) {
    // Arrays.sort(fields);
    // for (String field : fields) {
    // String capField = (field.charAt(0) + "").toUpperCase()
    // + field.substring(1, field.length());
    // if (field.contains("_")) {
    // capField = capField.substring(0, field.length() - 1);
    // }
    // System.out
    // .println("    if (fieldName.equals(\""
    // + capField
    // + "\")) { \n"
    // + "\tif (fieldValue.equals(\"----\")) {\n"
    // + "\tmyOccurrence.set"
    // + capField
    // + "(\"\");\n"
    // + "\t} else {\n"
    // + "\tmyOccurrence.set"
    // + capField
    // + "(fieldValue);\n"
    // + "\t}\n"
    // + "\tcurrentChanges.remove(currentChanges.firstKey());\n"
    // + "\tfieldName = currentChanges.firstEntry().getKey();\n"
    // + "\tfieldValue = currentChanges.firstEntry().getValue().contents;\n"
    // + "\t}");
    // }
    //    
    wah();
  }

  public static void wah() {
    Class<Occurrence> c = Occurrence.class;
    Method fields[] = c.getMethods();
    for (Method field : fields) {
      System.out.println(field.getName());
    }
    try {
      System.out.println(c.getMethod("getBasisOfRecord", c));
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
