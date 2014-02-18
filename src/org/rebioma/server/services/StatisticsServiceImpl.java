package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.server.util.HibernateUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

public class StatisticsServiceImpl extends RemoteServiceServlet implements StatisticsService {

	@Override
	public List<StatisticModel> getStatisticsByType(int statisticsType) {
		List<StatisticModel> ret = new ArrayList<StatisticModel>();
		String colonne="";
		String groupColonne="";
		switch (statisticsType) {
		case 1:
			colonne=" u.first_name || ' ' || upper(u.last_name) || ' - ' || u.institution || ' ' || u.email ";
			groupColonne=colonne;
			break;
		case 2:
			colonne=" institutioncode ";
			groupColonne=colonne;
			break;
		case 3:
			colonne=" collectioncode ";
			groupColonne=colonne;
			break;
		case 4:
			colonne=" cast(yearcollected as int)/10 * 10 || ' ~ ' || " +
					"case " +
					"	when cast(max(yearcollected)as int)/10 = cast(extract(year from current_date)as int)/10 " +
					"	then date_part('year', current_date) || '' " +
					"	else cast(yearcollected as int)/10 * 10 + 9 || '' " +
					"end  ";
			groupColonne=" cast(yearcollected as int)/10 ";
			break;
		default:
			break;
		}
		
		String sql = "SELECT libelle,sum(\"private\") as nbprivate,sum(\"public\") as nbpublic,sum(reliable) as reliable, sum(awaiting) as awaiting,sum(questionnable) as questionnable,sum(invalidated) as invalidated,\n" +
						"0 as \"all\"\n" +
						"FROM \n" +
						"( \n" +
						"SELECT "+colonne+" as libelle , \n" +
						" count(*) as \"private\",0 as \"public\",0 as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.\"public\" = FALSE \n" +
						"GROUP BY " + groupColonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",count(*) as \"public\",0 as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.\"public\" = TRUE \n" +
						"GROUP BY  " + groupColonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\",count(*)  as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed = true\n" +
						"GROUP BY  " + groupColonne +
						"UNION\n" +
						"SELECT "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,count(*) as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed IS NULL AND occurrence.validated=TRUE\n" +
						"GROUP BY  " + groupColonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,0 as awaiting,count(*)  as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed = FALSE\n" +
						"GROUP BY  " + groupColonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,0 as awaiting,0 as questionnable,count(*) as invalidated, 0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.validated = FALSE\n" +
						"GROUP BY  " + groupColonne +
						")as tbl\n" +
						"GROUP BY libelle ORDER BY libelle";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		try {
			sess=HibernateUtil.getSessionFactory().openSession(); 
			conn=sess.connection();		
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				if(rst.getString("libelle")!=null && !rst.getString("libelle").trim().isEmpty()){
					StatisticModel obj = new StatisticModel();				
					obj.setNbInvalidated(rst.getInt("invalidated"));
					obj.setNbAwaiting(rst.getInt("awaiting"));
					obj.setNbPrivateData(rst.getInt("nbprivate"));
					obj.setNbPublicData(rst.getInt("nbpublic"));
					obj.setNbQuestionable(rst.getInt("questionnable"));
					obj.setNbReliable(rst.getInt("reliable"));				
					obj.setStatisticType(statisticsType);
					obj.setTitle(rst.getString("libelle"));
					ret.add(obj);
				}				
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
								
		return ret;
	}

	@Override
	public List<StatisticModel> getStatisticDetails(
			StatisticModel statisticModel) {
		
		return getStatisticDetails(statisticModel.getStatisticType(), statisticModel.getTitle());
	}

	@Override
	public PagingLoadResult<StatisticModel> getStatisticsByType(
			int statisticsType, PagingLoadConfig config) {
		List<StatisticModel> statisticModels = getStatisticsByType(statisticsType);
		int start = config.getOffset();
		int limit = statisticModels.size();
		ArrayList<StatisticModel> subListToShow = new ArrayList<StatisticModel>(); 
		if (config.getLimit() > 0) {  
			limit = Math.min(start + config.getLimit(), limit);  
		}  
		for (int i = config.getOffset(); i < limit; i++) {        
			subListToShow.add(statisticModels.get(i));
		}         
		return new PagingLoadResultBean<StatisticModel>  
			(subListToShow, statisticModels.size(),config.getOffset());  
	}

	/*@Override
	public List<StatisticModel> getStatisticsByType(int statisticsType) {
		List<StatisticModel> list = new ArrayList<StatisticModel>();
		
		list.add(new StatisticModel(1,1, "Rakoto frah", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(2,1, "Rakoto frahq", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(3,1, "Rakoto frahw", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(4,1, "Rakoto frahr", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(5,1, "Rakoto fraht", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(6,1, "Rakoto frahf", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(7,1, "Rakoto frahh", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(8,1, "Rakoto frahj", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(9,1, "Bema kelyd", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(10,1, "Bema kelys", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(11,1, "Bema kelya", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(12,1, "Bema kelyz", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(13,1, "Bema kelyp", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(14,1, "Bema kelym", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(15,1, "Bema kelyg", 2, 3, 4, 5, 6, 7));
		list.add(new StatisticModel(16,1, "Bema kelyh", 2, 3, 4, 5, 6, 7));
	
	return list;
	}*/

	@Override
	public PagingLoadResult<StatisticModel> getStatisticDetails(
			StatisticModel statisticModel, PagingLoadConfig config) {
		List<StatisticModel> statisticModels = getStatisticDetails(statisticModel);
		int start = config.getOffset();
		int limit = statisticModels.size();
		ArrayList<StatisticModel> subListToShow = new ArrayList<StatisticModel>(); 
		if (config.getLimit() > 0) {  
			limit = Math.min(start + config.getLimit(), limit);  
		}  
		for (int i = config.getOffset(); i < limit; i++) {        
			subListToShow.add(statisticModels.get(i));
		}         
		return new PagingLoadResultBean<StatisticModel>  
			(subListToShow, statisticModels.size(),config.getOffset()); 
	}

	@Override
	public List<StatisticModel> getStatisticDetails(int statisticsType,
			String libelle) {
		
		List<StatisticModel> ret = new ArrayList<StatisticModel>();
		String colonne="";
		switch (statisticsType) {
		case 1:
			colonne=" u.first_name || ' ' || upper(u.last_name) || ' - ' || u.institution || ' ' || u.email ";
			break;
		case 2:
			colonne=" institutioncode ";
			break;
		case 3:
			colonne=" collectioncode ";
			break;
		case 4:
			colonne=" yearcollected ";
			break;
		default:
			break;
		}
		
		String sql = "SELECT UPPER(acceptedclass) as acceptedclass,sum(\"private\") as nbprivate,sum(\"public\") as nbpublic,sum(reliable) as reliable, sum(awaiting) as awaiting,sum(questionnable) as questionnable,sum(invalidated) as invalidated,\n" +
						"0 as \"all\"\n" +
						"FROM \n" +
						"( \n" +
						"SELECT  acceptedclass, "+colonne+" as libelle , \n" +
						" count(*) as \"private\",0 as \"public\",0 as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.\"public\" = FALSE \n" +
						"GROUP BY " + colonne + " ,acceptedclass " +
						"UNION\n" +
						"SELECT   acceptedclass , "+colonne+"  as libelle, \n" +
						" 0 as \"private\",count(*) as \"public\",0 as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.\"public\" = TRUE \n" +
						"GROUP BY  " + colonne +" ,acceptedclass " +
						"UNION\n" +
						"SELECT   acceptedclass , "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\",count(*)  as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed = true\n" +
						"GROUP BY  " + colonne +" ,acceptedclass " +
						"UNION\n" +
						"SELECT  acceptedclass , "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,count(*) as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed IS NULL AND occurrence.validated=TRUE\n" +
						"GROUP BY  " + colonne +" ,acceptedclass " +
						"UNION\n" +
						"SELECT   acceptedclass , "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,0 as awaiting,count(*)  as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed = FALSE\n" +
						"GROUP BY  " + colonne +" ,acceptedclass " +
						"UNION\n" +
						"SELECT acceptedclass , "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,0 as awaiting,0 as questionnable,count(*) as invalidated, 0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.validated = FALSE\n" +
						"GROUP BY  " + colonne +" ,acceptedclass " +
						")as tbl\n" +
						" WHERE libelle= ? " +
						"GROUP BY  upper(acceptedclass) ORDER BY  upper(acceptedclass)";
		System.out.println(sql);
		Session sess = null;		
		Connection conn =null;
		PreparedStatement st=null;
		ResultSet rst=null;
		try {
			sess=HibernateUtil.getSessionFactory().openSession(); 
			conn=sess.connection();		
			st = conn.prepareStatement(sql);
			st.setString(1, libelle);
			rst = st.executeQuery();
			while(rst.next()) {
				//if(rst.getString("libelle")!=null && !rst.getString("libelle").trim().isEmpty()){
					StatisticModel obj = new StatisticModel();				
					obj.setNbInvalidated(rst.getInt("invalidated"));
					obj.setNbAwaiting(rst.getInt("awaiting"));
					obj.setNbPrivateData(rst.getInt("nbprivate"));
					obj.setNbPublicData(rst.getInt("nbpublic"));
					obj.setNbQuestionable(rst.getInt("questionnable"));
					obj.setNbReliable(rst.getInt("reliable"));				
					obj.setStatisticType(statisticsType);
					obj.setTitle(rst.getString("acceptedclass"));
					ret.add(obj);
				//}				
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
								
		return ret;
	}

}
