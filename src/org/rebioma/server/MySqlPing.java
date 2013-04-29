package org.rebioma.server;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.rebioma.client.bean.AscData;
import org.rebioma.server.util.HibernateUtil;

public class MySqlPing {
  private static boolean initialized = false;
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
          Session session = HibernateUtil.getCurrentSession();
          boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
          session.createCriteria(AscData.class).setProjection(
              Projections.count("id")).uniqueResult();
          // ((Long)
          // session.createQuery("select count(*) from AscData").iterate()
          // .next()).intValue();
          log.info("Pinging MySQL Server...");
          if (isFirstTransaction) {
            HibernateUtil.commitCurrentTransaction();
          }
        } catch (RuntimeException re) {
          HibernateUtil.rollbackTransaction();
          log.error("Error while pinging server:" + re.getMessage(), re);
        }
      }
//    }, 10, 1800000);
    }, 10, 300000);
  }
}
