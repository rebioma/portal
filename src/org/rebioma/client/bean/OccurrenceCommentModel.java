package org.rebioma.client.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("serial")
public class OccurrenceCommentModel implements java.io.Serializable {
	
	private int uId;
	private String firstName;
	private String lastName;
	private String email;
	private String commentDetail;
	private String passwordHash = "";
	
	public OccurrenceCommentModel(){
		super();
	}

	public OccurrenceCommentModel(int uId, String firstName, String lastName,
			String email, String commentDetail, String passwordHash) {
		super();
		this.uId = uId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.commentDetail = commentDetail;
		this.passwordHash = passwordHash;
	}

	public int getUId() {
		return uId;
	}

	public void setUId(int uId) {
		this.uId = uId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCommentDetail() {
		return commentDetail;
	}

	public void setCommentDetail(String commentDetail) {
		this.commentDetail = commentDetail;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
}
