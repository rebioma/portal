package org.rebioma.server;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.context.ManagedSessionContext;
import org.hibernate.criterion.Projections;
import org.rebioma.client.bean.AscData;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.server.util.ManagedSession;

public class MySqlPing {
  private static boolean initialized = true;
  private static Logger log = Logger.getLogger(MySqlPing.class);
  private static final Timer t = new Timer();

  /**
   * Pings mysql server every 5 minutes (300000 ms).
   */
  public static void startPingTimer() {
	  
	if (initialized) {
      return;
    }
    initialized = true;
    t.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        try {
        	Session session = ManagedSession.createNewSessionAndTransaction();
          
        	session.createCriteria(AscData.class).setProjection(
        			Projections.count("id")).uniqueResult();
        	// ((Long)
        	// session.createQuery("select count(*) from AscData").iterate()
        	// .next()).intValue();
        	log.info("Pinging MySQL Server...");
        	ManagedSession.commitTransaction(session);
        } catch (Exception re) {
        	//HibernateUtil.rollbackTransaction();
        	log.error("Error while pinging server:" + re.getMessage(), re);
        }
      }
//  	 }, 10, 1800000);
    }, 1, 1000);
  }
  
  public static class TestThread extends Thread {
	  
      
      public TestThread(String name){
              super(name);
      }
      
      public void run(){
          while(true)
    	  try {
	        	Session session = ManagedSession.createNewSessionAndTransaction();
	          
	        	List<OccurrenceComments> oComments = session.createCriteria(Occurrence.class).setMaxResults(30).list();
	        	// ((Long)update
	        	// session.createQuery("select count(*) from AscData").iterate()
	        	// .next()).intValue();
	        	System.out.println("Pinging MySQL Server..." + super.getName() + "-----");
	        	ManagedSession.commitTransaction(session);
	        } catch (Exception re) {
	        	//HibernateUtil.rollbackTransaction();
	        	System.out.println("Error while pinging server:" + super.getName() + "-----" + re.getMessage());
	        	re.printStackTrace();
	        }
              
      }       
}
  
  public static void main(String[] args) {
	  TestThread t = new MySqlPing.TestThread("B");
      TestThread t2 = new MySqlPing.TestThread("C");
      t.start();
      t2.start();
  }
}
