package org.rebioma.server.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.rebioma.client.bean.CommentTable;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.RecapTable;
import org.rebioma.server.util.EmailUtil;
import org.rebioma.server.util.HibernateUtil;

import BCrypt.BCrypt;

public class OccurrenceCommentHbm {
	
	private static Logger log = Logger.getLogger(OccurrenceCommentHbm.class);
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
	
	public static List<OccurrenceCommentModel> getCommentInfo(Date date1, Date date2){
		String date = "";
		
		if(date1!=null && date2!=null)
			date = "WHERE OccurrenceComments.dateCommented BETWEEN '" + format.format(date1) + "' AND '" + format.format(date2) + "' ";
		
		
		String sql = "SELECT " +
				"\"user\".id," +
				"\"user\".first_name," +
				"\"user\".last_name," +
				"\"user\".email," +
				"\"user\".password_hash," +
				"count(OccurrenceComments.id) AS FIELD_1 " +
				"FROM " +
				"\"user\" " +
				"INNER JOIN Occurrence ON (\"user\".id = Occurrence.Owner) " +
				"INNER JOIN OccurrenceComments ON (Occurrence.ID = OccurrenceComments.oid) " +
				date +
				"GROUP BY " +
				"\"user\".first_name," +
				"\"user\".last_name," +
				"\"user\".email," +
				"\"user\".id," +
				"\"user\".password_hash";
		log.info(sql);
		
		Session sess = null;
		
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		List<OccurrenceCommentModel> lists = new ArrayList<OccurrenceCommentModel>();
		try {
			sess=HibernateUtil.getSessionFactory().openSession();
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				OccurrenceCommentModel temp = new OccurrenceCommentModel(
						rst.getInt(1),
						rst.getString(2),
						rst.getString(3),
						rst.getString(4),
						rst.getString(6)+" Comments",
						rst.getString(5)
						);
				
				lists.add(temp);
				/*
				temp = hashList.get(rst.getString(1));
				log.info(rst.getString("reviewed")+
						rst.getString("comments"));
				if(temp != null){
					HashMap<String, RecapTable> recap = temp.getTrbReviewed();
					RecapTable recapTemp = recap.get(rst.getString("idTRB"));
					if(recapTemp != null){
						recapTemp.setComments(rst.getString("reviewed")+
								rst.getString("comments"));
						recap.put(rst.getString("idTRB"), recapTemp);
					}else{
						recap.put(rst.getString("idTRB"), new RecapTable(
								rst.getInt("idTRB"),
								rst.getString("first_nameTRB"),
								rst.getString("last_nameTRB"),
								rst.getString("emailTRB"),
								rst.getString("reviewed") +
								rst.getString("comments")
								));
					}
					temp.setTrbReviewed(recap);
				}else{
					temp = new OccurrenceCommentModel(
							rst.getInt(1),
							rst.getString(2),
							rst.getString(3),
							rst.getString(4)
							);
					HashMap<String, RecapTable> recap = new HashMap<String, RecapTable>();
					
					recap.put(rst.getString("idTRB"), new RecapTable(
							rst.getInt("idTRB"),
							rst.getString("first_nameTRB"),
							rst.getString("last_nameTRB"),
							rst.getString("emailTRB"),
							rst.getString("reviewed") +
							rst.getString("comments")
							));
					temp.setTrbReviewed(recap);
				}
				hashList.put(rst.getString(1), temp);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			return lists;
		}
	}
	
	public static String creatCommentMail(OccurrenceCommentModel oc, String url, Date date1, Date date2){
	
		/*String sql="SELECT oid, AcceptedSpecies, userComment, " +
			"CONCAT( CONCAT( first_name,  \" \" ) , last_name ) as trb, dateCommented " +
			"FROM OccurrenceComments, Occurrence, User, record_review " +
			"WHERE OccurrenceComments.oid = Occurrence.id " +
			"AND OccurrenceComments.uid = User.id " +
			"AND OccurrenceComments.uid = record_review.userId " +
			"AND OccurrenceComments.oid = record_review.occurrenceId " +
			"AND dateCommented >= reviewed_date " +
			"AND record_review.reviewed <> 1 " +
			"AND User.id <> '" + oc.getUId() +"' " +
			"AND Owner = '" + oc.getUId() +"' " +
			"AND dateCommented between '" + format.format(date1) + "' AND '" + format.format(date2) + "'";*/
		String date = "";
		
		if(date1!=null && date2!=null)
			date = "OccurrenceComments.dateCommented BETWEEN '" + format.format(date1) + "' AND '" + format.format(date2) + "' AND ";
		String sql = "SELECT " +
				"OccurrenceComments.oid," +
				"Occurrence.AcceptedSpecies," +
				"OccurrenceComments.userComment," +
				"first_name || ' ' || last_name AS trb," +
				"OccurrenceComments.dateCommented," +
				"record_review.reviewed " +
				"FROM " +
				"OccurrenceComments " +
				"LEFT OUTER JOIN record_review ON (OccurrenceComments.oid = record_review.OccurrenceId)" +
				"AND (OccurrenceComments.uid = record_review.userId)" +
				"INNER JOIN \"user\" ON (record_review.userId = \"user\".id)" +
				"INNER JOIN Occurrence ON (OccurrenceComments.oid = Occurrence.ID) " +
				"WHERE " +
				date +
				"(OccurrenceComments.dateCommented >= record_review.reviewed_date OR " +
				"record_review.reviewed_date IS NULL) AND" +
				"(record_review.reviewed = false OR " +
				"record_review.reviewed IS NULL OR " +
				"OccurrenceComments.dateCommented <> record_review.reviewed_date) AND " +
				"Owner = '" + oc.getUId() +"' AND " +
				"\"user\".id <> '" + oc.getUId() +"'";
		log.info(sql);
		//if(true)return "";
		Session sess = null;
	
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		String mail = "";
		try {
			sess=HibernateUtil.getSessionFactory().openSession(); 
			conn=sess.connection();
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			String pass = oc.getPasswordHash();
			int rowNumber = 0;
			while(rst.next()) {
				mail+= new CommentTable(
					rst.getInt(1),
					rst.getString(2),
					rst.getString(3),
					rst.getString(4),
					format.format(rst.getDate(5)),
					url,
					oc.getEmail(),
					pass
				).toTable(rowNumber);
				rowNumber++;
				//System.out.println("########"+oc.getEmail());
			}
			conn.commit();
			conn.close();
			sess.close();
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}finally {
			return mail;
		}
	}
	
	
	public static String[] occurrenceState(int userId){
		String sql = "SELECT reviewed, Validated, count( * ) " +
				"FROM Occurrence " +
				"WHERE Owner = " + userId +
				" GROUP BY reviewed, Validated";
		Session sess = null;
		log.info(sql);
		//System.out.println(sql);
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;
		String [] validated = {"0","0","0","0"};
		try {
			sess=HibernateUtil.getSessionFactory().openSession(); 
			conn=sess.connection();
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				//System.out.println(rst.getString(1) + "\t" + rst.getString(2) + "\t" + rst.getString(3));
				if(rst.getBoolean(1))validated[0] = rst.getString(3);
				else if(rst.getString(1)==null){
					if(rst.getBoolean(2))validated[2] = rst.getString(3);
					else validated[3] = rst.getString(3);
				}
				else if(!rst.getBoolean(1))validated[1] = rst.getString(3);
			}
			conn.commit();
			conn.close();
			sess.close();
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}finally {
			return validated;
		}
	}
	
	public static HashMap<String, RecapTable> occurrenceTRBState(int userId){
		String sql = "SELECT " +
				"record_review.reviewed," +
				"count(*) AS comments," +
				"record_review.userId," +
				"\"user\".first_name," +
				"\"user\".last_name," +
				"\"user\".email " +
				"FROM " +
				"record_review " +
				"LEFT OUTER JOIN Occurrence ON (record_review.occurrenceId = Occurrence.ID) " +
				"INNER JOIN \"user\" ON (record_review.userId = \"user\".id) " +
				"WHERE " +
				"Occurrence.Owner = " + userId +
				" AND Validated = true " +
				"GROUP BY " +
				"record_review.reviewed," +
				"record_review.userId," +
				"\"user\".first_name," +
				"\"user\".last_name," +
				"\"user\".email";
		Session sess = null;
		log.info(sql);
		Connection conn =null;
		Statement st=null;
		ResultSet rst=null;

		HashMap<String, RecapTable> hRecap = new HashMap<String, RecapTable>(); 
		try {
			sess=HibernateUtil.getSessionFactory().openSession(); 
			conn=sess.connection();
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				RecapTable recap = hRecap.get(rst.getString(3));
				if(recap != null){
					recap.setComments(rst.getString(1)+
							rst.getString(2));
				}else{
					recap = new RecapTable(
							rst.getInt(3),
							rst.getString(4),
							rst.getString(5),
							rst.getString(6),
							rst.getString(1) +
							rst.getString(2)
							);
				}
				hRecap.put(rst.getString(3), recap);
			}
			conn.commit();
			conn.close();
			sess.close();
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}finally {
			return hRecap;
		}
	}
	public static void main(String[] args) {
		OccurrenceCommentHbm hbm = new OccurrenceCommentHbm();
		HashMap<String, RecapTable> res = hbm.occurrenceTRBState(186);
		Iterator it = res.keySet().iterator();
		while(it.hasNext()){
			RecapTable r = res.get(it.next());
			System.out.println(r.getFirstName() + " " + r.getLastName() + " " + r.getQuestionable() + " " + r.getReliable());
		}
			
	}
}
