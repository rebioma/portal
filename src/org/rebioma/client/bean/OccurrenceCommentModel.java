package org.rebioma.client.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class OccurrenceCommentModel extends BaseTreeModel {
	
	public static final String UID = "uId";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";
	public static final String COMMENT_DETAIL = "userComment";
	private String passwordHash = "";
	//private HashMap<String, RecapTable> trbReviewed;
	
	/*public HashMap<String, RecapTable> getTrbReviewed() {
		return trbReviewed;
	}

	public void setTrbReviewed(HashMap<String, RecapTable> trbReviewed) {
		this.trbReviewed = trbReviewed;
		Set<String> set = this.trbReviewed.keySet();
		Iterator it = set.iterator();
		String temp = "";
		while(it.hasNext()){
			RecapTable recap = this.trbReviewed.get(it.next());
			temp += recap.getReliable()+"(R) " + recap.getQuestionable()+"(?) " +
					recap.getLastName()+"/ ";
		}	
		setCommentdetail(temp);
	}
	*/
	
	public OccurrenceCommentModel(){
		super();
	}

	public OccurrenceCommentModel(int uId, String firstName, String lastName, String email, String commentDetail, String passwd){
		set(this.UID,uId);
		set(this.FIRST_NAME,firstName);
		set(this.LAST_NAME,lastName);
		set(this.EMAIL,email);
		set(this.COMMENT_DETAIL, commentDetail);
		setPasswordHash(passwd);
	}

	public int getUId(){
		return (Integer)get(this.UID);
	}
	
	public String getFirstName(){
		return (String)get(this.FIRST_NAME);
	}
	
	public String getLastName(){
		return (String)get(this.LAST_NAME);
	}
	
	public String getEmail(){
		return (String)get(this.EMAIL);
	}
	
	public String getCommentdetail(){
		return (String)get(this.COMMENT_DETAIL);
	}
	
	public void setUId(int uId){
		set(this.UID,uId);
	}
	
	public void setFirstName(String firstName){
		set(this.FIRST_NAME,firstName);
	}
	
	public void setLastName(String lastName){
		set(this.LAST_NAME,lastName);
	}
	
	public void setEmail(String email){
		set(this.EMAIL,email);
	}
	
	public void setCommentdetail(String commentdetail){
		set(this.COMMENT_DETAIL,commentdetail);
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
}
