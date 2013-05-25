package org.rebioma.server.services;

import java.sql.Connection;
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
		
		String sql = "SELECT libelle,sum(\"private\") as nbprivate,sum(\"public\") as nbpublic,sum(reliable) as reliable, sum(awaiting) as awaiting,sum(questionnable) as questionnable,sum(invalidated) as invalidated,\n" +
						"0 as \"all\"\n" +
						"FROM \n" +
						"( \n" +
						"SELECT "+colonne+" as libelle , \n" +
						" count(*) as \"private\",0 as \"public\",0 as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.\"public\" = FALSE \n" +
						"GROUP BY " + colonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",count(*) as \"public\",0 as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.\"public\" = TRUE \n" +
						"GROUP BY  " + colonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\",count(*)  as reliable,0 as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed = true\n" +
						"GROUP BY  " + colonne +
						"UNION\n" +
						"SELECT "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,count(*) as awaiting,0 as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed IS NULL AND occurrence.validated=TRUE\n" +
						"GROUP BY  " + colonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,0 as awaiting,count(*)  as questionnable,0 as invalidated,0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.reviewed = FALSE\n" +
						"GROUP BY  " + colonne +
						"UNION\n" +
						"SELECT  "+colonne+"  as libelle, \n" +
						" 0 as \"private\",0 as \"public\", 0 as reliable,0 as awaiting,0 as questionnable,count(*) as invalidated, 0 as \"all\"\n" +
						"FROM occurrence  LEFT JOIN \"user\" u ON u.email=occurrence.email\n" +
						" WHERE 1=1 AND \n" +
						"occurrence.validated = FALSE\n" +
						"GROUP BY  " + colonne +
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
		// TODO Auto-generated method stub
		return null;
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

}
