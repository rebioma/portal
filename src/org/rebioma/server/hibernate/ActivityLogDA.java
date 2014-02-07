package org.rebioma.server.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.rebioma.client.bean.Activity;
import org.rebioma.server.services.OccurrenceDbImpl;
import org.rebioma.server.util.ManagedSession;

public class ActivityLogDA {
	
	
	private static Logger log = Logger.getLogger(ActivityLogDA.class);
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public List<Activity> getCommentActivity(int userId){
		
		String sql = "select uid, count(*), reviewed, usercomment, substr(datecommented || '', 0,15) || '00:00' as date " +
				"from occurrencecomments oc " +
				"left join record_review rv on (" +
				"	uid = userid " +
				"	and occurrenceid = oid " +
				"	and substr(datecommented || '', 0,15) = substr(reviewed_date || '', 0,15)" +
				") " +
				"where trim(trim(both E'\\n' from usercomment)) <> 'comment left when reviewed.' " +
				"and reviewed is null " +
				"and uid = " + userId + " " +
				"group by uid,usercomment, date, reviewed";
		log.info(sql);
		
		List<Activity> lists = new ArrayList<Activity>();
		try {
			Session sess = null;
			
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess= ManagedSession.createNewSessionAndTransaction();
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			int id = 0;
			while(rst.next()) {
				lists.add(new Activity(
						"c"+(++id),
						rst.getLong(2),
						null,
						format.parse(rst.getString(5)),
						rst.getString(4)
						));
			}
			ManagedSession.commitTransaction(sess);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}
	
	public List<Activity> getReviewActivity(int userId){
		
		String sql = "select userid, count(*), reviewed, substr(reviewed_date || '', 0,15) || '00:00' as date  , usercomment " +
				"from record_review rv " +
				"left join occurrencecomments cm on (userid = uid and occurrenceid = oid and substr(datecommented || '', 0,15) = substr(reviewed_date || '', 0,15)) " +
				"where userid = " + userId + " and reviewed is not null " +
				"group by userid, reviewed, date , usercomment";
		log.info(sql);
		
		List<Activity> lists = new ArrayList<Activity>();
		try {
			Session sess = null;
			
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess= ManagedSession.createNewSessionAndTransaction();
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			int id = 1;
			while(rst.next()) {
				lists.add(new Activity(
						"r"+(++id),
						rst.getLong(2),
						rst.getBoolean(3),
						format.parse(rst.getString(4)),
						rst.getString(5)
						));
			}
			ManagedSession.commitTransaction(sess);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}
	
	public boolean removeCommentActivity(int userId, Activity activity) {
		String sql = "delete from occurrencecomments where id in (select oc.id " +
				"from occurrencecomments oc " +
				"left join record_review rv on (" +
				"	uid = userid " +
				"	and occurrenceid = oid " +
				"	and substr(datecommented || '', 0,15) = substr(reviewed_date || '', 0,15)" +
				") " +
				"where trim(trim(both E'\\n' from usercomment)) <> 'comment left when reviewed.' " +
				"and reviewed is null " +
				"and uid = " + userId + " " +
				"and substr(datecommented || '', 0,15) || '00:00' = '" + format.format(activity.getDate()) + "' " +
				"and usercomment = '" + activity.getComment().replace("'", "''") + "')";
		log.info(sql);
		boolean rep = false;
		try {
			Session sess = null;
			
			Connection conn =null;
			Statement st=null;
			sess= ManagedSession.createNewSessionAndTransaction();
			conn=sess.connection();
			
			st = conn.createStatement();
			st.executeUpdate(sql);
			rep = true;
			log.warn("done");
			ManagedSession.commitTransaction(sess);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rep;
	}
	
	public boolean removeReviewActivity(int userId, Activity activity) {
		
		String sql = "select rv.id, cm.id, occurrenceid " +
				"from record_review rv " +
				"left join occurrencecomments cm on (userid = uid and occurrenceid = oid and substr(datecommented || '', 0,15) = substr(reviewed_date || '', 0,15)) " +
				"where userid = " + userId + " and reviewed = " + activity.getAction() + " " +
				"and substr(reviewed_date || '', 0,15) || '00:00' = '" + format.format(activity.getDate()) + "' ";
				sql += activity.getComment()==null?" and usercomment is null":"and usercomment = '" + activity.getComment().replace("'", "''") + "'";
		log.info(sql);
		boolean rep = false;
		List<Integer> ocId = new ArrayList<Integer>();
		try {
			Session sess = null;
			
			Connection conn =null;
			Statement st=null;
			ResultSet rst=null;
			sess= ManagedSession.createNewSessionAndTransaction();
			conn=sess.connection();
			
			st = conn.createStatement();
			rst = st.executeQuery(sql);
			
//			List<Integer> idComment = new ArrayList<Integer>();
			String idReview = "";
			String idComment = "";
			while(rst.next()) {
				idReview+=rst.getInt(1)+",";
				ocId.add(rst.getInt(3));
				if(rst.getString(2)!=null)
					idComment+=rst.getInt(2)+",";
			}
			idReview+="0";
			idComment+="0";
			resetReview(st, idReview);
			delectComment(st, idComment);
			rep = true;
//			log.warn(idReview + " - " + idComment + " - Updated: ");
			ManagedSession.commitTransaction(sess);
			int updated = 0;
//			String ids = "";
			for (Iterator iterator = ocId.iterator(); iterator.hasNext();) {
				Integer integer = (Integer) iterator.next();
//				ids += integer+ ",";
				if(new OccurrenceDbImpl().checkForReviewedChanged(integer))updated++;
			}
//			log.warn("- Updated: " + updated + " (" + ids + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rep;
	}
	
	private void resetReview(Statement st, String ids) throws SQLException {
		String sql = "update record_review set reviewed = null, reviewed_date = null where id in (" +ids +")";
		st.executeUpdate(sql);
	}
	
	private void delectComment(Statement st, String ids) throws SQLException {
		String sql = "delete from occurrencecomments where id in (" +ids +")";
		st.executeUpdate(sql);
	}

	public ActivityLogDA() {
		super();
	}
	
	public static void main(String[] args) {
		new OccurrenceDbImpl().checkForReviewedChanged(234381);
		
	}

}
