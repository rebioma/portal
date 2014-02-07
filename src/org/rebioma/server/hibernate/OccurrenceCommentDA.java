package org.rebioma.server.hibernate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.bean.CommentTable;
import org.rebioma.client.bean.LastComment;
import org.rebioma.client.bean.LastComment;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.RecapTable;
import org.rebioma.client.bean.User;
import org.rebioma.server.util.ManagedSession;

public class OccurrenceCommentDA {
	
	private static Logger log = Logger.getLogger(OccurrenceCommentDA.class);
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static List<OccurrenceCommentModel> getCommentInfo(Date date1, Date date2){
		String date = "";
		
		if(date1!=null && date2!=null)
			date = "OccurrenceComments.dateCommented BETWEEN '" + format.format(date1) + "' AND '" + format.format(date2) + "' AND ";
		
		
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
				"WHERE " +
				date +
				"usercomment <> '\n comment left when reviewed.' " +
				"GROUP BY " +
				"\"user\".first_name," +
				"\"user\".last_name," +
				"\"user\".email," +
				"\"user\".id," +
				"\"user\".password_hash";
		log.info(sql);
		
		List<OccurrenceCommentModel> lists = new ArrayList<OccurrenceCommentModel>();
		try {
			Session sess = null;
			
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess= ManagedSession.createNewSessionAndTransaction();
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				OccurrenceCommentModel temp = new OccurrenceCommentModel(
						rst.getInt(1),
						rst.getString(2),
						rst.getString(3),
						rst.getString(4),
						rst.getString(6) + (rst.getInt(6)<=1?" comment":" comments"),
						rst.getString(5)
						);
				
				lists.add(temp);
			}
			ManagedSession.commitTransaction(sess);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}
	
	public static List<OccurrenceCommentModel> getCommentInfo(HashMap<String, List<LastComment>> map, Date date1, Date date2){
		
		Iterator it = map.keySet().iterator();
		List<OccurrenceCommentModel> lists = new ArrayList<OccurrenceCommentModel>();

		while(it.hasNext()){
			String uid = (String) it.next();
			List<LastComment> list = map.get(uid);
			User u = OccurrenceCommentDA.getUserById(uid);
			OccurrenceCommentModel temp = new OccurrenceCommentModel(
					u.getId(),
					u.getFirstName(),
					u.getLastName(),
					u.getEmail(),
					list.size() + (list.size()<=1?" comment":" comments"),
					u.getPasswordHash()
					);
			lists.add(temp);
		}
		return lists;
	}
	
	
	
	public static User getUserById(String id){
		Session session = ManagedSession.createNewSessionAndTransaction();
	    try {
	      User user = (User) session.createCriteria(User.class).add(
	          Restrictions.eq("id", Integer.valueOf(id))).uniqueResult();
	      ManagedSession.commitTransaction(session);
	      return user;
	    } catch (HibernateException e) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("Error: " + e.getMessage(), e);
	      return null;
	    } 
	}
	
	public static String creatCommentMail(List<LastComment> lComment, User trb, String url, Date date1, Date date2){
		
		String date = "";
		if(date1!=null && date2!=null)
			date = "occurrencecomments.dateCommented BETWEEN '" + format.format(date1) + "' AND '" + format.format(date2) + "' AND ";
		String mail = "";
		int rowNumber = 0;
		for(LastComment c: lComment){
			try {
				String sql = "SELECT " +
						"occurrencecomments.oid, " +
						"acceptedSpecies, " +
						"occurrencecomments.usercomment, " +
						"u1.first_name, " +
						"u1.last_name, " +
						"occurrencecomments.datecommented, " +
						"u2.first_name, " +
						"u2.last_name " +
						"FROM " +
						"occurrence, " +
						"occurrencecomments, " +
						"\"user\" as u1, " +
						"\"user\" as u2 " +
						"WHERE " +
						date +
						"occurrence.id = occurrencecomments.oid AND " +
						"occurrence.owner = u1.id AND " +
						"datecommented >= '" + c.getDate() + "' AND " +
						"occurrencecomments.uid = u2.id AND " +
						"usercomment <> '\n comment left when reviewed.' AND " +
						"'" + trb.getId() + "' <> uid and occurrencecomments.oid = " + c.getOid();
				log.info(sql);
				Session sess = null;
				Connection conn =null;
				Statement st=null;
				ResultSet rst=null;
				sess = ManagedSession.createNewSessionAndTransaction(); 
				conn=sess.connection();
				st = conn.createStatement();
				rst = st.executeQuery(sql);
				while(rst.next()) {
					CommentTable cTable = new CommentTable(
						rst.getInt(1),
						rst.getString(2),
						rst.getString(3),
						rst.getString(4) + " " + rst.getString(5),
						format.format(rst.getDate(6)),
						url,
						trb.getEmail(),
						trb.getPasswordHash()
					);
					cTable.setCommentedBy(rst.getString(7) + " " + rst.getString(8));
					mail+= cTable.toTable(rowNumber);
					rowNumber++;
				}
//				conn.commit();
//				conn.close();
//				sess.close();
				ManagedSession.commitTransaction(sess);
			} catch (Exception e) {
				log.info(e.getMessage());
				e.printStackTrace();
			}
		}
		return mail;
	}
	
	public static HashMap<String, List<LastComment>> getLastComment(Date date1, Date date2){
		String date = "";
		
		if(date1!=null && date2!=null)
			date = "AND OccurrenceComments.dateCommented BETWEEN '" + format.format(date1) + "' AND '" + format.format(date2) + "' ";
		
		
		String sql = "select uid, oid, max(datecommented) " +
				"from occurrencecomments " +
				"where uid in (select distinct userid from taxonomic_reviewer) " +
				"and usercomment <> '\n comment left when reviewed.' " +
				date +
				"group by uid,oid";
		log.info(sql);
		
		HashMap<String, List<LastComment>> lists = new HashMap<String, List<LastComment>>();
		try {
			Session sess = null;
			
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess= ManagedSession.createNewSessionAndTransaction();
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			while(rst.next()) {
				String uid = rst.getString(1);
				int oid = rst.getInt(2);
				Date dateC = rst.getDate(3);
				List<LastComment> listD = lists.get(uid);
				if(listD==null){
					listD = new ArrayList<LastComment>();
				}
				listD.add(new LastComment(oid, dateC));
				
				lists.put(uid,listD);
			}
			ManagedSession.commitTransaction(sess);
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
				"usercomment <> '\n comment left when reviewed.' and " +
				"(OccurrenceComments.dateCommented >= record_review.reviewed_date OR " +
				"record_review.reviewed_date IS NULL) AND" +
				"(record_review.reviewed = false OR " +
				"record_review.reviewed IS NULL OR " +
				"OccurrenceComments.dateCommented <> record_review.reviewed_date) AND " +
				"Owner = '" + oc.getUId() +"' AND " +
				"\"user\".id <> '" + oc.getUId() +"'";
		log.info(sql);
		//if(true)return "";
		
		String mail = "";
		try {
			Session sess = null;
	
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess = ManagedSession.createNewSessionAndTransaction(); 
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
//			conn.commit();
//			conn.close();
//			sess.close();
			ManagedSession.commitTransaction(sess);
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
		log.info(sql);
		//System.out.println(sql);
		String [] validated = {"0","0","0","0"};
		try {
			Session sess = null;
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess = ManagedSession.createNewSessionAndTransaction(); 
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
//			conn.commit();
//			conn.close();
//			sess.close();
			ManagedSession.commitTransaction(sess);
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
		log.info(sql);
		
		HashMap<String, RecapTable> hRecap = new HashMap<String, RecapTable>(); 
		try {
			Session sess = null;
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess = ManagedSession.createNewSessionAndTransaction(); 
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
//			conn.commit();
//			conn.close();
//			sess.close();
			ManagedSession.commitTransaction(sess);
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}finally {
			return hRecap;
		}
	}
	public static void main(String[] args) {
		OccurrenceCommentDA hbm = new OccurrenceCommentDA();
		HashMap<String, List<LastComment>> res = hbm.getLastComment(null, null);
//		hbm.creatCommentMail(res, null, null, null);
//		HashMap<String, RecapTable> res = hbm.occurrenceTRBState(186);
//		Iterator it = res.keySet().iterator();
//		while(it.hasNext()){
//			RecapTable r = res.get(it.next());
//			System.out.println(r.getFirstName() + " " + r.getLastName() + " " + r.getQuestionable() + " " + r.getReliable());
//		}
			
	}
}
