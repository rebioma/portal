package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.Session;
import org.rebioma.client.services.HomestatService;
import org.rebioma.server.util.ManagedSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class HomestatServiceImpl extends RemoteServiceServlet implements
		HomestatService {

	@Override
	public int getCountOccurrence() {
		int count = 0;
		try {
			String sql = "Select count(id)from Occurrence";
			Connection conn = null;
			Statement st = null;
			ResultSet rst = null;
			Session sess = ManagedSession.createNewSessionAndTransaction();
			conn = sess.connection();
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while (rst.next()) {
				count = rst.getInt(1);
			}
			ManagedSession.commitTransaction(sess);
			System.err.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	@Override
	public int getCountUser() {
		int count = 0;
		try {
			String sql = "Select count(id)from public.User";
			Connection conn = null;
			Statement st = null;
			ResultSet rst = null;
			Session sess = ManagedSession.createNewSessionAndTransaction();
			conn = sess.connection();
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while (rst.next()) {
				count = rst.getInt(1);
			}
			ManagedSession.commitTransaction(sess);
			System.err.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	@Override
	public int getCountSpecies() {
		int count = 0;
		try {
			String sql = "Select count(Distinct acceptedspecies) from Taxonomy";
			Connection conn = null;
			Statement st = null;
			ResultSet rst = null;
			Session sess = ManagedSession.createNewSessionAndTransaction();
			conn = sess.connection();
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while (rst.next()) {
				count = rst.getInt(1);
			}
			ManagedSession.commitTransaction(sess);
			System.err.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

}