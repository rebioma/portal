package org.rebioma.server.services;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.AscModelResult;
import org.rebioma.client.bean.AscModel;
import org.rebioma.server.util.HibernateUtil;

public class AscModelDbImpl implements AscModelDb {
  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(AscModelDbImpl.class);
  private static final String MODEL_OUTPUT = "/ModelOutput";

  public static void initDatabase() {
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      session.createQuery("delete from AscModel").executeUpdate();
      File file = new File("war" + MODEL_OUTPUT);
      for (File f : file.listFiles()) {
        String speciesName;
        if (f.isDirectory()) {
          speciesName = f.getName();
        } else {
          continue;
        }
        if (!speciesName.equals(".svn")) {
          AscModel ascModel = new AscModel(speciesName.replaceAll("_", " "),
              speciesName, speciesName + ".html");
          session.save(ascModel);
        }
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }

    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public static void main(String args[]) {
    initDatabase();
  }

  public AscModelResult findAscModel(String acceptedSpecies, int start,
      int limit) {
    log.debug("finding AscModels of " + acceptedSpecies);
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Criteria criteria = session.createCriteria(AscModel.class);
      criteria.add(Restrictions.ilike("acceptedSpecies", acceptedSpecies,
          MatchMode.ANYWHERE));
      criteria.setFirstResult(0);
      criteria.setProjection(Projections.count("id"));
      Integer count = (Integer) criteria.uniqueResult();
      criteria.setProjection(null);
      criteria.addOrder(Order.asc("acceptedSpecies"));
      criteria.setFirstResult(start);
      criteria.setMaxResults(limit);
      List<AscModel> results = criteria.list();
      /*Tax: Reset the firstResult to 0 for the projection*/
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      log.info(results.size()+"  count:"+count+"  acceptsps:"+acceptedSpecies+"  start:"+start+"  limit:"+limit);
      int i_count;
      if(count == null)i_count = 0;
      else i_count = count.intValue();
      AscModelResult ascres = new AscModelResult(results, i_count);
      return ascres;
    } catch (RuntimeException re) {
      log.error("find failed", re);
      re.printStackTrace();
      HibernateUtil.rollbackTransaction();
      throw re;
    }
  }
}
