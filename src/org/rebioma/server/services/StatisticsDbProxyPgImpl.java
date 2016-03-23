/**
 * 
 */
package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.rebioma.client.bean.ListStatisticAPIModel;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.services.StatisticType;
import org.rebioma.server.util.HibernateUtil;

/**
 * @author Mikajy
 *
 */
public class StatisticsDbProxyPgImpl implements StatisticsDbProxy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1924594213719015046L;

	@Override
	public ListStatisticAPIModel getStatisticsByTypeEnum(StatisticType type) {
		if(type == null){
			throw new IllegalArgumentException("Cannot find Statistic enum type [null]");
		}
		final long startMillis = System.currentTimeMillis();
		ListStatisticAPIModel listStatisticAPIModel = new ListStatisticAPIModel();
		String colonne="";
		String groupColonne="";
		switch (type) {
		case TYPE_DATA_MANAGER:
			colonne=" u.first_name || ' ' || upper(u.last_name) || ' - ' || u.institution || ' ' || u.email ";
			groupColonne=colonne;
			break;
		case TYPE_DATA_PROVIDER_INSTITUTION:
			colonne=" institutioncode ";
			groupColonne=colonne;
			break;
		case TYPE_COLLECTION_CODE:
			colonne=" collectioncode ";
			groupColonne=colonne;
			break;
		case TYPE_YEAR_COLLECTED:
			colonne=" cast(year as int)/10 * 10 || ' ~ ' || " +
					"case " +
					"	when cast(max(year)as int)/10 = cast(extract(year from current_date)as int)/10 " +
					"	then date_part('year', current_date) || '' " +
					"	else cast(year as int)/10 * 10 + 9 || '' " +
					"end  ";
			groupColonne=" cast(year as int)/10 ";
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
		List<StatisticModel> ret = new ArrayList<StatisticModel>();
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
					obj.setStatisticType(type.asInt());
					obj.setTitle(rst.getString("libelle"));
					ret.add(obj);
				}				
			}
			listStatisticAPIModel.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			listStatisticAPIModel.setSuccess(false);
			listStatisticAPIModel.setMessage(e.getMessage());
			
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
		final long endMillis = System.currentTimeMillis();
		listStatisticAPIModel.setStatistics(ret);
		listStatisticAPIModel.setTookInMillis(endMillis - startMillis);
		return listStatisticAPIModel;
	}

	@Override
	public List<StatisticModel> getStatisticsByType(int statisticsType) {
		StatisticType statisticType = StatisticType.asEnum(statisticsType);
		ListStatisticAPIModel listStatisticAPIModel = getStatisticsByTypeEnum(statisticType);
		return listStatisticAPIModel.getStatistics();
	}

}
