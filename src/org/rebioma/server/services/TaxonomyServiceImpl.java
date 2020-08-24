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
	Statement st;
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
	@Override
	public List<Taxonomy> threatenedSpecies(String status) {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select Distinct acceptedspecies, kingdom, phylum, Taxonomy.class, Taxonomy.order,family, genus, specificepithet from Taxonomy where iucn='"+status.replaceAll("'", "").replace("\"", "")+"'";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		int count = 0;		
		try {
			sess=HibernateUtil.getSessionFactory().openSession();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
			conn=sess.connection();		
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				
				Taxonomy obj = new Taxonomy();
					obj.setAcceptedSpecies(rst.getString("acceptedspecies"));
					obj.setKingdom(rst.getString("kingdom"));
					obj.setPhylum(rst.getString("phylum"));
					obj.setClass_(rst.getString("class"));
					obj.setOrder(rst.getString("order"));
					obj.setFamily(rst.getString("family"));
					obj.setGenus(rst.getString("genus"));
					obj.setSpecificEpithet(rst.getString("specificepithet"));
					list.add(obj);
					System.out.println(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {		
			if(rst!=null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(st!=null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			if(sess!=null)
				sess.close();
		}	
	return list;
	}

	@Override
	public List<Taxonomy> getiucn_status() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select iucn from Taxonomy where iucn is not null group by iucn";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		try {
			sess=HibernateUtil.getSessionFactory().openSession();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
			conn=sess.connection();		
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				
				Taxonomy obj = new Taxonomy();
					obj.setIucn(rst.getString("iucn"));
					list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {		
			if(rst!=null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(st!=null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			if(sess!=null)
				sess.close();
		}	
	return list;
	}

	@Override
	public List<Taxonomy> getKingdom() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select kingdom from Taxonomy group by kingdom";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		try {
			sess=HibernateUtil.getSessionFactory().openSession();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
			conn=sess.connection();		
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				
				Taxonomy obj = new Taxonomy();
					obj.setKingdom(rst.getString("kingdom"));
					list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {		
			if(rst!=null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(st!=null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			if(sess!=null)
				sess.close();
		}	
	return list;
	}

	@Override
	public List<Taxonomy> getKingdomT() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select kingdom from Taxonomy where isterrestrial=1 group by kingdom";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		try {
			sess=HibernateUtil.getSessionFactory().openSession();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
			conn=sess.connection();		
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				
				Taxonomy obj = new Taxonomy();
					obj.setKingdom(rst.getString("kingdom"));
					list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {		
			if(rst!=null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(st!=null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			if(sess!=null)
				sess.close();
		}	
	return list;
	}

	@Override
	public List<Taxonomy> getKingdomM() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select kingdom from Taxonomy where ismarine=1 group by kingdom";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		try {
			sess=HibernateUtil.getSessionFactory().openSession();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
			conn=sess.connection();		
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				
				Taxonomy obj = new Taxonomy();
					obj.setKingdom(rst.getString("kingdom"));
					list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {		
			if(rst!=null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(st!=null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			if(sess!=null)
				sess.close();
		}	
	return list;
	}
	public void majstatut_iucn_occ(){
		try {
			String sql = "UPDATE Occurrence set iucn_status=Taxonomy.iucn from Taxonomy where Taxonomy.acceptedspecies=concat(Occurrence.genus,' ',Occurrence.specificepithet)";
			Session session = ManagedSession.createNewSessionAndTransaction();
			pstm=session.connection().prepareStatement(sql);
			pstm.executeUpdate();
			ManagedSession.commitTransaction(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
}
}
