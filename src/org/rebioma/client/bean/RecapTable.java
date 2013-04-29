package org.rebioma.client.bean;

public class RecapTable implements java.io.Serializable {

	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String questionable = "-";
	private String reliable = "-";
	private String aReview = "-";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	
	public String getQuestionable() {
		return questionable;
	}
	
	public void setQuestionable(String questionable) {
		this.questionable = questionable;
	}
	
	public String getReliable() {
		return reliable;
	}
	
	public void setReliable(String reliable) {
		this.reliable = reliable;
	}
	
	public String getaReview() {
		return aReview;
	}
	
	public void setaReview(String aReview) {
		this.aReview = aReview;
	}
	
	public void setComments(String comments){
		if(comments.toLowerCase().startsWith("t")){
			reliable = comments.substring(1);
		}else if(comments.startsWith("null")){
			aReview = comments.substring(4);
		}else{ 
			questionable = comments.substring(1);
		}
	}
	
	public RecapTable(int id, String firstName, String lastName, String email,
			String comments) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		setComments(comments);
	}
	
	public RecapTable() {
		super();
	}
	
	public static void main(String[] args) {
	}
}
