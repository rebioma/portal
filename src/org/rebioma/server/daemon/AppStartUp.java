package org.rebioma.server.daemon;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.rebioma.client.bean.LastComment;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.server.services.MailingServiceImpl;
 
public class AppStartUp implements ServletContextListener {
	
	private Timer t = new Timer();
	
	private boolean initialized = false;
	
	private Logger log = Logger.getLogger(MailingServiceImpl.class);
	
	private MailingServiceImpl mail = null;
	
	private static String fileName = "mailing.properties";
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private boolean start = false;
	
	private long frequency[] = {(long)7*1000*60*60*24, (long)14*1000*60*60*24, (long)30*1000*60*60*24}; 
	
	static public Properties load(){
		Properties p = new Properties();
		File f = new File(fileName);
		try {
			p.load(new FileInputStream(f));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p;
	}
	
	public boolean save(Properties p){
		try {
//			System.out.println("##############" +
//					p.getProperty("start", "false") + " " + 
//					p.getProperty("url", "false") + " " + 
//					p.getProperty("frequency", "2") + " " +
//					p.getProperty("date", "1900-01-01 01:00:00")
//			);
//			FileOutputStream file = new FileOutputStream(new File(fileName));
			p.store(new FileOutputStream(new File(fileName)), "mailing");
//			file.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void checkProperties() {
		Properties p = load();

		Date date1 = null,date2=new Date();
		
		if(p.getProperty("start", "false").contains("true")){
			String frq = p.getProperty("frequency","2");
			long diff = frequency[Integer.valueOf(frq)];
			String url = p.getProperty("url","http://data.rebioma.net");
			
			try {
				date1 = format.parse(p.getProperty("date",format.format(date2)));
			} catch (ParseException e) {
				date1 = new Date();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			date2 = new Date();
			p.setProperty("date", format.format(date2));
			if((date2.getTime()-date1.getTime())>=diff){
				List<OccurrenceCommentModel> occs = mail.getOccurrenceComments(date1, date2);
				mail.sendComment(occs, url, date1, date2);
				HashMap<String, List<LastComment>> map = mail.getLastComments(date1, date2);
				mail.sendComment(map, url, date1, date2);
				date2.setHours(date1.getHours());
				date2.setMinutes(date1.getMinutes());
				date2.setSeconds(date1.getSeconds());
				p.setProperty("date", format.format(date2));
				save(p);
			}
		}
		
	}
	  
	
    public void contextDestroyed(ServletContextEvent arg0) {
        // cleanup operations here 
    	mail = null;
        System.out.println("Cleanup activity: mailing service set to null");
    }
    
    public void invokeIndefinitePrintTask(){
		
    	if(initialized){
    		return;
    	}
    	initialized = true;
        t.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				checkProperties();
				log.info("mailing check");
			}
		}, 2000, 1000000);
       
    }
 
    public void contextInitialized(ServletContextEvent arg0) {
        // Invoke the daemon/background process code
        mail = new MailingServiceImpl();
        invokeIndefinitePrintTask();
    }
    
    public static void main(String[] args) {
		
	}
}