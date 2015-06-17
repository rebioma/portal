package org.rebioma.server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class RevalidationFileUtil {
 private static Properties revalidationProperties = null;
	 
	 private static final Logger logger = Logger.getLogger(EmailUtil.class);
	 private static final String SEPARATOR;
	 private static final String FILE_EXTENSION = ".log";
	 
	 static {
		 	SEPARATOR = System.getProperty("file.separator");
		    ResourceBundle rb = ResourceBundle.getBundle("revalidation");

		    if (rb != null) {
		    	revalidationProperties = new Properties();
		      Enumeration<String> keys = rb.getKeys();

		      while (keys.hasMoreElements()) {
		        String key = keys.nextElement();
		        revalidationProperties.put(key, rb.getObject(key));
		      }
		    } else {
		      logger.error("unable to open properties file " + "SendMail.properties"
		          + " for send mail. Make sure it existed.", null);
		    }
		  }
	 public static String  generateFileName(){
		 String fileName = "";
		 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_Hms");
		 try {
			String directoryName = revalidationProperties.getProperty("storage_file_log");
			if(StringUtils.isBlank(directoryName)){
				directoryName = System.getProperty("user.dir");
			}
			String fileNames = revalidationProperties.getProperty("file_name");
			if(StringUtils.isBlank(fileNames)){
				fileNames = "revalidation";
			}
			File dirFile = new File(directoryName);
			if(!dirFile.exists()){
				 logger.info("Le repertoire de log [" + dirFile.getAbsolutePath() + "] n'existe pas encore...");
				 logger.info("...création du repertoire [" + dirFile.getAbsolutePath() + "]");
				 if(!dirFile.mkdirs()){
					 logger.error("Erreur de création du repertoire [" + dirFile.getAbsolutePath() + "]");
					 throw new FileNotFoundException("Le repertoire [" + dirFile.getAbsolutePath() + "] n'existe pas");
				 }
			}
			validateDirectory(dirFile);
			 StringBuilder sb = new StringBuilder();
			 sb.append(directoryName).append(SEPARATOR)
			 .append(fileNames).append(simpleDateFormat.format(new Date())).append(FILE_EXTENSION);
			 fileName = sb.toString();
			 logger.info("filename = " + fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return fileName;
	 }
	 
	 public static int getBatch() {
		 int ret=1000;
		 
		 if(revalidationProperties.containsKey("batch")) {
			 String temp = revalidationProperties.getProperty("batch");
			 try {
				 ret=Integer.parseInt(temp);
			 }catch(Exception ex) {
				 ex.printStackTrace();
			 }
		 }		 
		 return ret;
	 }
	 
	 
	 /**
	    * V�rification de la validit� d'un "r�pertoire"
	    */
	    private static void validateDirectory (File pADirectory) throws Exception {
	        if (pADirectory == null) {
	            throw new IllegalArgumentException("Directory null");
	        }
	        if (!pADirectory.exists()) {
	            throw new FileNotFoundException("Directory not existing");
	        }
	        if (!pADirectory.isDirectory()) {
	            throw new IllegalArgumentException("It is not a directory");
	        }
	        if (!pADirectory.canRead()) {
	            throw new IllegalArgumentException("Can't read in the Directory");
	        }
	        
	        if(!pADirectory.canWrite()){
	        	 throw new IllegalArgumentException("Can't write in the Directory");
	        }
	    }
}
