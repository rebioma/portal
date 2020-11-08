package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.rebioma.client.bean.GraphicModel;
import org.rebioma.client.bean.Iucn;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.bean.ThreatenedSpeciesModel;
import org.rebioma.client.services.TaxonomyService;
import org.rebioma.client.services.UserService.UserServiceException;
import org.rebioma.server.util.HibernateUtil;
import org.rebioma.server.util.ManagedSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;


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
	@Override
	public PagingLoadResult<ThreatenedSpeciesModel> threatenedSpecies(
			PagingLoadConfig config, Iucn iucnS) {

		String iucnStatus = iucnS == null ? "" :iucnS.getName();
		List<ThreatenedSpeciesModel> list = new ArrayList<ThreatenedSpeciesModel>();
		String sql = "Select Distinct id, acceptedspecies, kingdom, phylum, Taxonomy.class, Taxonomy.order,family, genus, specificepithet from Taxonomy where iucn='"+iucnStatus.replaceAll("'", "").replace("\"", "")+"'";
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

				ThreatenedSpeciesModel obj = new ThreatenedSpeciesModel();
				obj.setId(rst.getString("id"));
				obj.setAcceptedSpecies(rst.getString("acceptedspecies"));
				obj.setKingdom(rst.getString("kingdom"));
				obj.setPhylum(rst.getString("phylum"));
				obj.setClass_(rst.getString("class"));
				obj.setOrder(rst.getString("order"));
				obj.setFamily(rst.getString("family"));
				obj.setGenus(rst.getString("genus"));
				obj.setSpecificEpithet(rst.getString("specificepithet"));
				list.add(obj);
				//					System.out.println(list);
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

		if (config.getSortInfo().size() > 0) {
			SortInfo sort = config.getSortInfo().get(0);
			if (sort.getSortField() != null) {
				final String sortField = sort.getSortField();
				if (sortField != null) {
					Collections.sort(list,  sort.getSortDir().comparator(new Comparator<ThreatenedSpeciesModel>() {
						public int compare(ThreatenedSpeciesModel p1, ThreatenedSpeciesModel p2) {
							if (sortField.equals("Kingdom")) {
								return p1.getKingdom().compareTo(p2.getKingdom());
							} else if (sortField.startsWith("Scientific")) {
								return p1.getAcceptedSpecies().compareTo(p2.getAcceptedSpecies());
							}else if (sortField.startsWith("Specific")) {
								return p1.getSpecificEpithet().compareTo(p2.getSpecificEpithet());
							} else if (sortField.equals("Class")) {
								return p1.getClass_().compareTo(p2.getClass_());
							} else if (sortField.equals("Family")) {
								return p1.getFamily().compareTo(p2.getFamily());
							} else if (sortField.equals("Order")) {
								return p1.getOrder().compareTo(p2.getOrder());
							}else if (sortField.equals("Genus")) {
								return p1.getGenus().compareTo(p2.getGenus());
							}
							return 0;
						}
					}));
				}
			}
		}
		ArrayList<ThreatenedSpeciesModel> sublist = new ArrayList<ThreatenedSpeciesModel>();
		int start = config.getOffset();
		int limit = list.size();
		if (config.getLimit() > 0) {
			limit = Math.min(start + config.getLimit(), limit);
		}
		for (int i = config.getOffset(); i < limit; i++) {
			sublist.add(list.get(i));
		}
		return new PagingLoadResultBean<ThreatenedSpeciesModel>(sublist, list.size(), config.getOffset());
	}

	@Override
	public PagingLoadResult<Iucn> getIucnStatus(PagingLoadConfig config) {
		// TODO Auto-generated method stub
		List<Taxonomy> list = getiucn_status();
		List<Iucn> listI = new ArrayList<Iucn>();

		String[] iucnSList = "EX EW RE CR EN VU CD NT LC LR/CD LR/NT LR/LC DD NA NE".split(" ");

		List<Iucn> iucns = new ArrayList<Iucn>();
		for (int i = 0; i < iucnSList.length; i++) {
			iucns.add(new Iucn(iucnSList[i], i));
		}

		HashMap<String, Integer> hashIucn = new HashMap<String, Integer>();
		for (int i = 0; i < iucnSList.length; i++) {
			hashIucn.put(iucns.get(i).getName(), i);
		}
		for (int i = 0; i < list.size(); i++) {
			listI.add(new Iucn(list.get(i).getIucn(), hashIucn.get(list.get(i).getIucn().toUpperCase())));
		}

		Collections.sort(listI, new Comparator<Iucn>() {
			public int compare(Iucn a, Iucn b) {
				return a.getId() - b.getId();
			}
		});

		return new PagingLoadResultBean<Iucn>(listI, listI.size(), config.getOffset());
	}

}
