package org.rebioma.client.bean;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.core.client.GWT;

@SuppressWarnings("serial")
public class CommentTable implements java.io.Serializable{
	
	private int oid;
	private String acceptedSpecies;
	private String userComment;
	private String trb;
	private String dateCommented;
	private String url;
	private String email;
	private String passwdHash;
	
	public static final String tdStyle = " style='color:#222;font-size:0.8em;border: 1px solid #CCCCCC;padding: 4px;text-align: left;'";
	public static final String trStyle = " style='background: none repeat scroll 0 0 #EEEEEE;'";
	public final static String tableStyle = " style='border-collapse: collapse;width: 100%;margin: 0;padding: 0;border-collapse: collapse;'";
	public final static String thStyle = "font-size:0.8em;border: 1px solid #CCCCCC;padding: 6px;text-align: left;background: none repeat scroll 0 0 #333333;color: white;font-weight: bold;";
	public final static String pStyle = " style='margin:10px 0;color:#222;font-size:9pt;'";
	public final static String tdNoteStyle = " style='color:#476000;font-size:9pt;'";
	public final static String spanStyle = " style='color:#222;font-size:9pt;'";
	public static String pFrStyle = " style='margin:10px 0;color:#0000ff;font-size:9pt;'";
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPasswdHash() {
		return passwdHash;
	}

	public void setPasswdHash(String passwdHash) {
		this.passwdHash = passwdHash;
	}

	public CommentTable(){
		super();
	}

	public int getOid() {
		return oid;
	}

	public void setOid(int oid) {
		this.oid = oid;
	}

	public String getAcceptedSpecies() {
		return acceptedSpecies;
	}

	public void setAcceptedSpecies(String acceptedSpecies) {
		this.acceptedSpecies = acceptedSpecies;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public String getTrb() {
		return trb;
	}

	public void setTrb(String trb) {
		this.trb = trb;
	}

	public String getDateCommented() {
		return dateCommented;
	}

	public void setDateCommented(String dateCommented) {
		this.dateCommented = dateCommented;
	}

	public CommentTable(int oid, String acceptedSpecies, String userComment,
			String trb, String dateCommented, String url, String email, String passwdHash) {
		super();
		this.oid = oid;
		this.acceptedSpecies = acceptedSpecies;
		this.userComment = userComment;
		this.trb = trb;
		this.dateCommented = dateCommented;
		this.url = url;
		this.email = email;
		this.passwdHash = passwdHash;
	}
	
	public String toTable(int rowNumber){
		
		return "<tr" + (rowNumber%2==1?trStyle:"") + ">" +
					"<td " + tdStyle + ">" +
						"<a href='" + this.url + "signinc="+this.passwdHash+"&emailc="+this.email+"&id=" + this.oid + "'>" +
						this.oid + 
						"</a>" +
					"</td>" + 
					"<td  " + tdStyle + ">" + this.acceptedSpecies + "</td>" +
					"<td  " + tdStyle + ">" + this.userComment + "</td>" +
					"<td  " + tdStyle + ">" + this.trb + "</td>" + 
					"<td  " + tdStyle + ">" + this.dateCommented + "</td>" + 
				"</tr>";
	}

}