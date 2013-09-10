package org.rebioma.server.daemon;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.server.services.MailingServiceImpl;
 
public class StartUp implements ServletContextListener {
	
	Timer t = new Timer();
	
	boolean initialized = false;
	
	Logger log = Logger.getLogger(MailingServiceImpl.class);
	
	MailingServiceImpl mail = null;
	
	String fileName = "mailing.properties";
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d HH");
	
	boolean start = false;
	
	long frequency[] = {7*1000*60*60*24, 14*1000*60*60*24, 30*1000*60*60*24}; 
	
	public Properties load(){
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
			p.store(new FileOutputStream(fileName), "");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void checkProperties() {
		Properties p = new Properties();
		
		p = load();

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
		}, 2000, 30000);
       
    }
 
    public void contextInitialized(ServletContextEvent arg0) {
        // Invoke the daemon/background process code
        mail = new MailingServiceImpl();
        invokeIndefinitePrintTask();
    }
    
    public static void main(String[] args) {
		
	}
}