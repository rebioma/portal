package org.rebioma.server.util;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.context.ManagedSessionContext;

public abstract class ManagedSession {

	private static final Logger log = Logger.getLogger(HibernateUtil.class);

	public static org.hibernate.Session createNewSession() {
		org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().openSession();
		session.setFlushMode(FlushMode.MANUAL);
		ManagedSessionContext.bind(session);
		return (org.hibernate.Session) session;
	}

	public static void startNewTransaction(Session session) {
		session.beginTransaction();
	}

	public static org.hibernate.Session createNewSessionAndTransaction() {
		Session session = createNewSession();
		startNewTransaction(session);
		return session;
	}

	public static void commitTransaction(Session session) {
		ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
		session.flush();
		session.getTransaction().commit();
		session.close();
	}

	public static void rollbackTransaction(Session session) {
		ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
		session.flush();
		session.getTransaction().rollback();
		session.close();

	  }
}