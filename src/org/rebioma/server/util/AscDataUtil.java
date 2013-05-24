package org.rebioma.server.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.rebioma.client.bean.AscData;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.services.AscDataService.AscDataServiceException;
import org.rebioma.server.overlays.ASCFileReader;
import org.rebioma.server.overlays.ASCReaderProvider;
import org.rebioma.server.overlays.StoragePathManager;

public class AscDataUtil {

  public static final String ETP_TOTAL_00 = "etptotal_00.asc";
  public static final String WBYEAR_2X = "wbyear_2X.asc";
  public static final String WBYEAR_00 = "wbyear_00.asc";
  public static final String WBYEAR_50 = "wbyear_50.asc";
  public static final String WBPOS_50 = "wbpos_50.asc";
  public static final String WBPOS_2X = "wbpos_2x.asc";
  public static final String WBPOS_00 = "wbpos_00.asc";
  public static final String REALMAT_50 = "realmat_50.asc";
  public static final String REALMAT_2X = "realmat_2X.asc";
  public static final String REALMAT_00 = "realmat_00.asc";
  public static final String REALMAR_50 = "realmar_50.asc";
  public static final String REALMAR_2X = "realmar_2X.asc";
  public static final String REALMAR_00 = "realmar_00.asc";
  public static final String PFC2000 = "pfc2000.asc";
  public static final String PFC1990 = "pfc1990.asc";
  public static final String PFC1970 = "pfc1970.asc";
  public static final String PFC1950 = "pfc1950.asc";
  public static final String MIN_TEMP_50 = "mintemp_50.asc";
  public static final String MIN_TEMP_2X = "mintemp_2X.asc";
  public static final String MIN_TEMP_00 = "mintemp_00.asc";
  public static final String MIN_PREC_50 = "minprec_50.asc";
  public static final String MIN_PREC_2X = "minprec_2X.asc";
  public static final String MIN_PREC_00 = "minprec_00.asc";
  public static final String MAX_TEMP_50 = "maxtemp_50.asc";
  public static final String MAX_TEMP_2X = "maxtemp_2X.asc";
  public static final String MAX_TEMP_00 = "maxtemp_00.asc";
  public static final String MAX_PREC_50 = "maxprec_50.asc";
  public static final String MAX_PREC_2X = "maxprec_2X.asc";
  public static final String MAX_PREC_00 = "maxprec_00.asc";
  public static final String ETP_TOTAL_50 = "etptotal_50.asc";
  public static final String ETP_TOTAL_2X = "etptotal_2X.asc";

  public static final String GEOL_STRECH_00 = "geolstrech.asc";
  public static final String DEM_ELEVATION_00 = "topex.asc";

  private static Map<String, AscData> ascsData;
  static {
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      List<AscData> ascDatas = session.createQuery("from AscData").list();
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      ascsData = new HashMap<String, AscData>();
      for (AscData ascData : ascDatas) {
        ascsData.put(ascData.getFileName(), ascData);
      }
    } catch (RuntimeException re) {
      //HibernateUtil.rollbackTransaction();
      re.printStackTrace();
    }
  }

  public static Map<String, AscData> convertDescriptionsMap(List<AscData> ascsData) {
    Map<String, AscData> ascDataMap = new HashMap<String, AscData>();
    for (AscData ascData : ascsData) {
      ascDataMap.put(ascData.getDescription().toLowerCase() + " - " + ascData.getYear().toLowerCase(), ascData);
    }
    return ascDataMap;
  }

  public static String getValue(AscData ascData, Double lat, Double lng) {
    if (lat == null || lng == null) {
      return null;
    }
    boolean outsideBounds = ascData.getSouthBoundary() > lat
        || ascData.getNorthBoundary() < lat || ascData.getWestBoundary() > lng
        || ascData.getEastBoundary() < lng;
    if (outsideBounds) {
      return null;
    }
    String ascPath;
    try {
      ascPath = StoragePathManager.getStoragePath(ascData.getFileName(), "");
      ASCFileReader asc = ASCReaderProvider.getReader(ascPath);
      return asc.getValue(lat, lng) + "";
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

  public static void main(String args[]) {
    // List<Occurrence> occurrences = generateTestOccurrences(100000);
    // try {
    // for (Occurrence occurrence : occurrences) {
    //
    // AscDataUtil.setLayerValuesToOccurrence(ascsFileToData, occurrence);
    //
    // }
    // } catch (AscDataServiceException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    String command = "load";
    if (args.length > 0) {
      command = args[0];
    }
    List<Occurrence> occurrences = null;
    //Session session = HibernateUtil.getCurrentSession();
    //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      occurrences = session.createQuery("from Occurrence").list();
      if (command.equalsIgnoreCase("load")) {
        for (Occurrence occurrence : occurrences) {
          resetLayerValues(occurrence);
          setLayerValuesToOccurrence(occurrence);
          session.update(occurrence);
        }
      } else if (command.equalsIgnoreCase("unload")) {
        for (Occurrence occurrence : occurrences) {
          resetLayerValues(occurrence);
          session.update(occurrence);
        }
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      e.printStackTrace();
      return;
    }
  }

  public static void printAscDataInfo(String fileName) {
    try {
      fileName = StoragePathManager.getStoragePath(fileName, "");
      ASCFileReader asc = ASCReaderProvider.getReader(fileName);
      System.out.println("max: " + asc.maxDataValue());
      System.out.println("min: " + asc.minDataValue());

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void resetLayerValues(Occurrence occurrence) {
    occurrence.setPfc1950(null);
    occurrence.setPfc1970(null);
    occurrence.setPfc1990(null);
    occurrence.setPfc2000(null);
    occurrence.setEtpTotal1950(null);
    occurrence.setEtpTotal2000(null);
    occurrence.setEtpTotalfuture(null);
    occurrence.setMinPerc1950(null);
    occurrence.setMinPerc2000(null);
    occurrence.setMinPercfuture(null);
    occurrence.setMaxPerc1950(null);
    occurrence.setMaxPerc2000(null);
    occurrence.setMaxPercfuture(null);
    occurrence.setMinTemp1950(null);
    occurrence.setMinTemp2000(null);
    occurrence.setMinTempfuture(null);
    occurrence.setMaxtemp1950(null);
    occurrence.setMaxTemp2000(null);
    occurrence.setMaxTempfuture(null);
    occurrence.setRealMar1950(null);
    occurrence.setRealMar2000(null);
    occurrence.setRealMarfuture(null);
    occurrence.setRealMat1950(null);
    occurrence.setRealMat2000(null);
    occurrence.setRealMatfuture(null);
    occurrence.setWbpos1950(null);
    occurrence.setWbpos2000(null);
    occurrence.setWbposfuture(null);
    occurrence.setWbyear1950(null);
    occurrence.setWbyear2000(null);
    occurrence.setWbyearfuture(null);
    occurrence.setDemelevation(null);
    occurrence.setGeolStrech(null);
  }

  /**
   * Sets all appropriated layer values to {@link occurrence}.
   * 
   * @param ascsData
   * @param occurrence
   * @throws AscDataServiceException
   */
  public static void setLayerValuesToOccurrence(Occurrence occurrence) {
    String year = occurrence.getYearCollected();
    Integer intYear = null;
    try {
      intYear = Integer.parseInt(year);
    } catch (Exception e) {
      return;
    }
    Double lat = null;
    Double lng = null;
    try {
      lat = Double.parseDouble(occurrence.getDecimalLatitude());
      lng = Double.parseDouble(occurrence.getDecimalLongitude());
    } catch (Exception e) {
      return;
    }
    if (intYear < 1969) {
      occurrence.setPfc1950(getValue(ascsData.get(PFC1950), lat, lng));
      occurrence
          .setEtpTotal1950(getValue(ascsData.get(ETP_TOTAL_50), lat, lng));
      occurrence.setMinPerc1950(getValue(ascsData.get(MIN_PREC_50), lat, lng));
      occurrence.setMaxPerc1950(getValue(ascsData.get(MAX_PREC_50), lat, lng));
      occurrence.setMinTemp1950(getValue(ascsData.get(MIN_TEMP_50), lat, lng));
      occurrence.setMaxtemp1950(getValue(ascsData.get(MAX_TEMP_50), lat, lng));
      occurrence.setRealMar1950(getValue(ascsData.get(REALMAR_50), lat, lng));
      occurrence.setRealMat1950(getValue(ascsData.get(REALMAT_50), lat, lng));
      occurrence.setWbpos1950(getValue(ascsData.get(WBPOS_50), lat, lng));
      occurrence.setWbyear1950(getValue(ascsData.get(WBYEAR_50), lat, lng));
      
      occurrence.setGeolStrech(getValue(ascsData.get(GEOL_STRECH_00), lat, lng));
      occurrence
          .setDemelevation(getValue(ascsData.get(DEM_ELEVATION_00), lat, lng));

    } else if (intYear < 1975) {
      occurrence.setPfc1970(getValue(ascsData.get(PFC1970), lat, lng));
      occurrence
          .setEtpTotal1950(getValue(ascsData.get(ETP_TOTAL_50), lat, lng));
      occurrence.setMinPerc1950(getValue(ascsData.get(MIN_PREC_50), lat, lng));
      occurrence.setMaxPerc1950(getValue(ascsData.get(MAX_PREC_50), lat, lng));
      occurrence.setMinTemp1950(getValue(ascsData.get(MIN_TEMP_50), lat, lng));
      occurrence.setMaxtemp1950(getValue(ascsData.get(MAX_TEMP_50), lat, lng));
      occurrence.setRealMar1950(getValue(ascsData.get(REALMAR_50), lat, lng));
      occurrence.setRealMat1950(getValue(ascsData.get(REALMAT_50), lat, lng));
      occurrence.setWbpos1950(getValue(ascsData.get(WBPOS_50), lat, lng));
      occurrence.setWbyear1950(getValue(ascsData.get(WBYEAR_50), lat, lng));
      
      occurrence.setGeolStrech(getValue(ascsData.get(GEOL_STRECH_00), lat, lng));
      occurrence
          .setDemelevation(getValue(ascsData.get(DEM_ELEVATION_00), lat, lng));

    } else if (intYear < 1990) {
      occurrence.setPfc1970(getValue(ascsData.get(PFC1970), lat, lng));
      occurrence
          .setEtpTotal2000(getValue(ascsData.get(ETP_TOTAL_00), lat, lng));
      occurrence.setMinPerc2000(getValue(ascsData.get(MIN_PREC_00), lat, lng));
      occurrence.setMaxPerc2000(getValue(ascsData.get(MAX_PREC_00), lat, lng));
      occurrence.setMinTemp2000(getValue(ascsData.get(MIN_TEMP_00), lat, lng));
      occurrence.setMaxTemp2000(getValue(ascsData.get(MAX_TEMP_00), lat, lng));
      occurrence.setRealMar2000(getValue(ascsData.get(REALMAR_00), lat, lng));
      occurrence.setRealMat2000(getValue(ascsData.get(REALMAT_00), lat, lng));
      occurrence.setWbpos2000(getValue(ascsData.get(WBPOS_00), lat, lng));
      occurrence.setWbyear2000(getValue(ascsData.get(WBYEAR_00), lat, lng));
      
      occurrence.setGeolStrech(getValue(ascsData.get(GEOL_STRECH_00), lat, lng));
      occurrence
          .setDemelevation(getValue(ascsData.get(DEM_ELEVATION_00), lat, lng));

    } else if (intYear < 2000) {
      occurrence.setPfc1990(getValue(ascsData.get(PFC1990), lat, lng));
      occurrence
          .setEtpTotal2000(getValue(ascsData.get(ETP_TOTAL_00), lat, lng));
      occurrence.setMinPerc2000(getValue(ascsData.get(MIN_PREC_00), lat, lng));
      occurrence.setMaxPerc2000(getValue(ascsData.get(MAX_PREC_00), lat, lng));
      occurrence.setMinTemp2000(getValue(ascsData.get(MIN_TEMP_00), lat, lng));
      occurrence.setMaxTemp2000(getValue(ascsData.get(MAX_TEMP_00), lat, lng));
      occurrence.setRealMar2000(getValue(ascsData.get(REALMAR_00), lat, lng));
      occurrence.setRealMat2000(getValue(ascsData.get(REALMAT_00), lat, lng));
      occurrence.setWbpos2000(getValue(ascsData.get(WBPOS_00), lat, lng));
      occurrence.setWbyear2000(getValue(ascsData.get(WBYEAR_00), lat, lng));
      
      occurrence.setGeolStrech(getValue(ascsData.get(GEOL_STRECH_00), lat, lng));
      occurrence
          .setDemelevation(getValue(ascsData.get(DEM_ELEVATION_00), lat, lng));

    } else {
      occurrence.setPfc2000(getValue(ascsData.get(PFC2000), lat, lng));
      occurrence
          .setEtpTotal2000(getValue(ascsData.get(ETP_TOTAL_00), lat, lng));
      occurrence.setMinPerc2000(getValue(ascsData.get(MIN_PREC_00), lat, lng));
      occurrence.setMaxPerc2000(getValue(ascsData.get(MAX_PREC_00), lat, lng));
      occurrence.setMinTemp2000(getValue(ascsData.get(MIN_TEMP_00), lat, lng));
      occurrence.setMaxTemp2000(getValue(ascsData.get(MAX_TEMP_00), lat, lng));
      occurrence.setRealMar2000(getValue(ascsData.get(REALMAR_00), lat, lng));
      occurrence.setRealMat2000(getValue(ascsData.get(REALMAT_00), lat, lng));
      occurrence.setWbpos2000(getValue(ascsData.get(WBPOS_00), lat, lng));
      occurrence.setWbyear2000(getValue(ascsData.get(WBYEAR_00), lat, lng));

      occurrence.setEtpTotalfuture(getValue(ascsData.get(ETP_TOTAL_2X), lat,
          lng));
      occurrence
          .setMinPercfuture(getValue(ascsData.get(MIN_PREC_2X), lat, lng));
      occurrence
          .setMaxPercfuture(getValue(ascsData.get(MAX_PREC_2X), lat, lng));
      occurrence
          .setMinTempfuture(getValue(ascsData.get(MIN_TEMP_2X), lat, lng));
      occurrence
          .setMaxTempfuture(getValue(ascsData.get(MAX_TEMP_2X), lat, lng));
      occurrence.setRealMarfuture(getValue(ascsData.get(REALMAR_2X), lat, lng));
      occurrence.setRealMatfuture(getValue(ascsData.get(REALMAT_2X), lat, lng));
      occurrence.setWbposfuture(getValue(ascsData.get(WBPOS_2X), lat, lng));
      occurrence.setWbyearfuture(getValue(ascsData.get(WBYEAR_2X), lat, lng));

      occurrence.setGeolStrech(getValue(ascsData.get(GEOL_STRECH_00), lat, lng));
      occurrence
          .setDemelevation(getValue(ascsData.get(DEM_ELEVATION_00), lat, lng));

    }
    // Double value = getValue(ascData, lat, lng);

  }

  private static List<Occurrence> generateTestOccurrences(int num) {
    List<Occurrence> occurrences = new ArrayList<Occurrence>();
    double maxLat = -11.9083308251577;
    double maxLng = 50.5083453552794;
    double minLat = -25.64166487475;
    double minLng = 43.183344973250;
    double lat = minLat;
    double lng = minLng;
    int maxYear = 2009;
    int minYear = 1950;
    int year = minYear;
    for (int i = 0; i < num; i++) {
      lat = incrementDouble(lat, minLat, maxLat);
      lng = incrementDouble(lng, minLng, maxLng);
      year = incrementInt(year, minYear, maxYear);
      Occurrence occurrence = new Occurrence();
      occurrence.setDecimalLatitude(lat + "");
      occurrence.setDecimalLongitude(lng + "");
      occurrence.setYearCollected(year + "");
      occurrences.add(occurrence);
    }
    return occurrences;
  }

  private static double incrementDouble(double value, double minValue,
      double maxValue) {
    value += 0.0000000001;
    if (value > maxValue) {
      value = minValue;
    }
    return value;
  }

  private static int incrementInt(int value, int minValue, int maxValue) {
    value += 1;
    if (value > maxValue) {
      value = minValue;
    }
    return value;
  }
}
