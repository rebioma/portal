package org.rebioma.server.util;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.rebioma.client.bean.Occurrence;

public class OccurrenceUtil {

  private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

  public static void main(String args[]) {
    Session session = sessionFactory.openSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      List<Occurrence> occurrences = session.createQuery("from Occurrence").list();
      for (Occurrence o : occurrences) {
        populateScientificName(o);
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // session.close();
    }
  }

  public static void populateScientificName(Occurrence o) {
    populateScientificName(o, true);
  }

  public static void populateScientificName(Occurrence o, boolean updated) {
    // if (!isEmpty(o.getScientificName())) {
    // return;
    // }
    String genus = o.getGenus();
    String cl = o.getClass_();
    String family = o.getFamily();
    String order = o.getOrder_();
    String phylum = o.getPhylum();
    String kingdom = o.getKingdom();
    String specificEpithet = o.getSpecificEpithet();
    String infraspecificEpithet = o.getInfraspecificEpithet();
    String scientificName = "unidentified";
    if (isEmpty(genus)) {
      if (isEmpty(family)) {
        if (isEmpty(order)) {
          if (isEmpty(cl)) {
            if (isEmpty(phylum)) {
              if (!isEmpty(kingdom)) {
                scientificName = kingdom;
              }
            } else
              scientificName = phylum;
          } else
            scientificName = cl;
        } else
          scientificName = order;
      } else
        scientificName = family;
    } else {
      if (isEmpty(specificEpithet)) {
        scientificName = genus;
      } else {
        if (isEmpty(infraspecificEpithet)) {
          scientificName = genus + " " + specificEpithet;
        } else {
          scientificName = genus + " " + specificEpithet + " " + infraspecificEpithet;
        }
      }
    }
    o.setScientificName(scientificName);
    if (!updated) {
      return;
    }
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      session.update(o);
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (Exception e) {
      HibernateUtil.rollbackTransaction();
      e.printStackTrace();
    }
  }

  private static boolean isEmpty(String s) {
    return s == null || s.trim().equals("");
  }
}
