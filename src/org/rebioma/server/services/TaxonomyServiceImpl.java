package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.rebioma.client.bean.GraphicModel;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.services.TaxonomyService;
import org.rebioma.client.services.UserService.UserServiceException;
import org.rebioma.server.util.HibernateUtil;
import org.rebioma.server.util.ManagedSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


@SuppressWarnings("serial")
public class TaxonomyServiceImpl extends RemoteServiceServlet implements
		TaxonomyService {
	PreparedStatement pstm;
	
	@Override
	
	public void maj(String category,String scientific_name){

				try {
					String sql = "UPDATE Taxonomy set iucn=? where acceptedspecies like ? or verbatimspecies like ?";
					Session session = ManagedSession.createNewSessionAndTransaction();
					pstm=session.connection().prepareStatement(sql);
					pstm.setString(1, category.replace("\"", ""));
					pstm.setString(2, scientific_name.replaceAll("'", "").replace("\"", ""));
					pstm.setString(3, scientific_name.replaceAll("'", "").replace("\"", ""));
					pstm.executeUpdate();
					ManagedSession.commitTransaction(session);

				} catch (Exception e) {
					e.printStackTrace();
				}
	}
}
