package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.rebioma.client.bean.GraphicModel;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.services.GraphicService;
import org.rebioma.server.util.HibernateUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GraphicServiceImpl extends RemoteServiceServlet implements
GraphicService {
	@Override
	public List<Taxonomy> getCountSpeciesGpByIUCN_Status() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
			String sql = "Select iucn, count(Distinct acceptedspecies)as count from Taxonomy where iucn is not null group by iucn";
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
						obj.setCount(rst.getInt("count"));
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
	public List<Taxonomy> getCountSpeciesTerrestreGpByIUCN_Status() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select count(Distinct acceptedspecies)as count,iucn from Taxonomy where iucn is not null and isterrestrial=1 group by iucn";
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
					obj.setCount(rst.getInt("count"));
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
	public List<Taxonomy> getCountSpeciesTerrestreGpByKingdomGpByIUCN_Status(String kingdom) {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select count(Distinct acceptedspecies) as count,iucn from Taxonomy where iucn is not null and isterrestrial=1 and kingdom='"+kingdom.replaceAll("'", "").replace("\"", "")+"' group by iucn";
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
					obj.setCount(rst.getInt("count"));
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
	public List<Taxonomy> getCountSpeciesMarinGpByIUCN_Status() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select count(Distinct acceptedspecies)as count,iucn from Taxonomy where iucn is not null and ismarine=1 group by iucn";
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
					obj.setCount(rst.getInt("count"));
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
	public List<GraphicModel> getOccurrenceByRegion() {
		List<GraphicModel> list = new ArrayList<GraphicModel>();
		String sql = "Select count(o.id)as count,r.nom_region as nom_region" +
				" from Occurrence o, regions r " +
				"where st_within(o.geom,r.geom) " +
				"group by r.nom_region";
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
				
				GraphicModel obj = new GraphicModel();
					obj.setNom_region(rst.getString("nom_region"));
					obj.setCount(rst.getInt("count"));
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
	public List<Occurrence> getOccPerYear() {
		List<Occurrence> list = new ArrayList<Occurrence>();
		String sql = "select year, count(id),count(reviewed) as reviewed" +
				" from Occurrence " +
				"where reviewed and year is not null or" +
				" year is not null and validationerror not like '%Invalid YearCollected%' group by year order by year";
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
				
				Occurrence obj = new Occurrence();
					obj.setYear(rst.getString("year"));
					obj.setCount(rst.getInt("count"));
					obj.setCountreviewed(rst.getInt("reviewed"));
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
	public List<Taxonomy> getCountTSpeciesGpByKingdom() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "select count(distinct acceptedspecies),kingdom from taxonomy where isterrestrial=1 group by kingdom";
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
					obj.setCount(rst.getInt("count"));	
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
	public List<Taxonomy> getCountMSpeciesGpByKingdom() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "select count(distinct acceptedspecies),kingdom from taxonomy where ismarine=1 group by kingdom";
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
					obj.setCount(rst.getInt("count"));
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
	public List<Taxonomy> getCountMSpeciesGpByKingdomAndIUCN_cat(String kingdom) {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select count(Distinct acceptedspecies) as count,iucn from Taxonomy where iucn is not null and ismarine=1 and kingdom='"+kingdom.replaceAll("'", "").replace("\"", "")+"' group by iucn";
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
					obj.setCount(rst.getInt("count"));
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
	public List<Taxonomy> getCountSpeciesGpByKingdom() {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "select count(distinct acceptedspecies),kingdom from taxonomy group by kingdom";
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
					obj.setCount(rst.getInt("count"));
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
	public List<Taxonomy> getCountSpeciesGpByKingdomGpByIUCN_Status(
			String kingdom) {
		List<Taxonomy> list = new ArrayList<Taxonomy>();
		String sql = "Select count(Distinct acceptedspecies) as count,iucn from Taxonomy where iucn is not null and kingdom='"+kingdom.replaceAll("'", "").replace("\"", "")+"' group by iucn";
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
					obj.setCount(rst.getInt("count"));
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
	public List<Occurrence> getOccPerYearBetween2date(String year1,String year2) {
		List<Occurrence> list = new ArrayList<Occurrence>();
		String sql = "select year, count(id),count(reviewed) as reviewed" +
				" from Occurrence " +
				"where reviewed and year is not null and year between '"+year1+"' and '"+year2+"' or" +
				" year is not null and validationerror not like '%Invalid YearCollected%' and year between '"+year1+"' and '"+year2+"' group by year order by year";
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
				Occurrence obj = new Occurrence();
					obj.setYear(rst.getString("year"));
					obj.setCount(rst.getInt("count"));
					obj.setCountreviewed(rst.getInt("reviewed"));
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
}
