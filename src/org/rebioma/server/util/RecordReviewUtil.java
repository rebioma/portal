package org.rebioma.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.rebioma.client.bean.RecordReview;

public class RecordReviewUtil {

  public static final double DEFAULT_PERCENT_CUT = 50.0; // 50%
  private static final Logger logger = Logger.getLogger(RecordReviewUtil.class);
  public static Properties devModeProp = new Properties();
  private static Boolean isDevMode = null;
  
  static {
	    ResourceBundle rb = ResourceBundle.getBundle("dev");

	    if (rb != null) {
	      Enumeration<String> keys = rb.getKeys();

	      while (keys.hasMoreElements()) {
	        String key = keys.nextElement();
	        devModeProp.put(key, rb.getObject(key));
	      }
	    } else {
	    	logger.warn(
	              "error while load dev.properties the system is operate in production mode", null);
	    }
  }
//  static {
//    try {
//      InputStream inputStream = ClassLoader
//          .getSystemResourceAsStream("dev.properties");
//      if (inputStream != null) {
//        devModeProp.load(inputStream);
//      } else {
//        logger
//            .warn("can't find dev.properties file. Development mode is disable");
//      }
//      logger.info("successfully load dev.properties");
//    } catch (IOException e) {
//      logger
//          .warn(
//              "error while load dev.properties the system is operate in production mode",
//              e);
//    }
//  }

  public static String getSentEmail(String email) {
    if (isDevMode()) {
      return devModeProp.getProperty("testUserEmail", email);
    }
    return email;
  }

  public static boolean isDevMode() {
    if (isDevMode == null) {
//    	System.out.println(devModeProp.getProperty("devmode"));
      isDevMode = Boolean.parseBoolean(devModeProp.getProperty("devmode",
          "false").trim());
      logger.info("the system is operating in "
          + (isDevMode ? "development" : "production") + " mode.");
    }
    return isDevMode;
  }

  public static Boolean isRecordReviewed(List<RecordReview> recordReviews,
      double percentageToBePositive) {
    int totalPos = 0;
    int totalNeg = 0;
    for (RecordReview recordReview : recordReviews) {
      Boolean reviewed = recordReview.getReviewed();
      if (reviewed != null) {
        if (reviewed) {
          totalPos++;
        } else {
          totalNeg++;
        }
      }
    }
    int totalReviewed = totalPos + totalNeg;
    if (totalReviewed == 0) {
      return null;
    }
    double posPercentage = (totalPos * 1.0) / totalReviewed * 100;
    return posPercentage >= percentageToBePositive;
  }

  // private static void initProps() {
  // try {
  // devModeProp.load(ClassLoader.getSystemResourceAsStream("dev.properties"));
  // logger.info("successfully load dev.properties");
  // } catch (IOException e) {
  // logger.error("error while load dev.properties", e);
  // }
  // }
  
  public static void main(String[] args) {
  }

}
