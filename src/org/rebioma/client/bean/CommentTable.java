package org.rebioma.client.bean;

import java.util.List;

//import java.util.Date;
//import com.extjs.gxt.ui.client.data.BaseTreeModel;
//import com.google.gwt.core.client.GWT;

@SuppressWarnings("serial")
public class CommentTable implements java.io.Serializable{
	
	private int oid;
	private String acceptedSpecies;
	private String userComment;
	private String commentedBy;
	private String trb;
	private String dateCommented;
	private String url;
	private String email;
	private String passwdHash;
	private List<String> lOid;
	
	public final static String objectNotification = "Notification from the rebioma dataportal";
	public final static String objectTRB = "An urgent message from the rebioma dataportal";
	public final static String objectDataOwner = objectTRB;
	
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
	
	public String getCommentedBy() {
		return commentedBy;
	}

	public void setCommentedBy(String commentedBy) {
		this.commentedBy = commentedBy;
	}

	public static String getMailToTRB() {
		return mailToTRBEn + hr + mailToTRBFr;
	}

	public static String getMailToDO() {
		return mailToDOEn + hr + mailToDOFr;
	}

	public static String getMailOnReviwing() {
		return mailOnReviwingEn + hr + mailOnReviwingEn;
	}
	
	public static String getCommentToSend() {
		return commentToSendEn + hr + commentToSendFr;
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
	
	public CommentTable(List<String> lOid, String userComment,
			String trb, String dateCommented, String url, String email, String passwdHash) {
		super();
		this.lOid = lOid;
		this.userComment = userComment;
		this.trb = trb;
		this.dateCommented = dateCommented;
		this.url = url;
		this.email = email;
		this.passwdHash = passwdHash;
	}
	
	public String toTable(int rowNumber){
		if(url.contains("gwt.codesvr")){
			url+="&";//dev mode
		}else url+="?";//production mode
		String rebiomaId = "";
		if(this.lOid == null){
			return "<tr" + (rowNumber%2==1?trStyle:"") + ">" +
					"<td " + tdStyle + "><a href='" + this.url + "signinc="+this.passwdHash+"&emailc="+this.email+"&id=" + this.oid + "'>" +
						this.oid +
					"</a></td>" + 
					"<td  " + tdStyle + ">" + this.acceptedSpecies + "</td>" +
					"<td  " + tdStyle + ">" + this.userComment.replace("\n", "<br/>") + "</td>" +
					(this.commentedBy==null?"":"<td  " + tdStyle + ">" + this.commentedBy + "</td>") + 
					"<td  " + tdStyle + ">" + this.trb + "</td>" + 
					"<td  " + tdStyle + ">" + this.dateCommented + "</td>" + 
				"</tr>";
		}else{
			for(String ocid:this.lOid){
				rebiomaId+="<a href='" + this.url + "signinc="+this.passwdHash+"&emailc="+this.email+"&id=" + ocid.split("=")[0] + "'>" +
					ocid.split("=")[0] + 
					"</a> - (" + ocid.split("=")[1] + ") - ";
			}
			return "<tr" + (rowNumber%2==1?trStyle:"") + ">" +
					"<td " + tdStyle + ">" + rebiomaId +
					"</td>" + 
					"<td  " + tdStyle + ">" + this.userComment.replace("\n", "<br/>") + "</td>" +
					(this.commentedBy==null?"":"<td  " + tdStyle + ">" + this.commentedBy + "</td>") + 
					"<td  " + tdStyle + ">" + this.trb + "</td>" + 
					"<td  " + tdStyle + ">" + this.dateCommented + "</td>" + 
				"</tr>";
		}
		
	}
	
	private static String head = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head><body>";
	
	private static String foot = "</body></html>";
	
	private static String hr = "<p>--------------</p>";
	
	public final static String tdStyle = " style='color:#222;font-size:0.8em;border: 1px solid #CCCCCC;padding: 4px;text-align: left;'";
	public final static String trStyle = " style='background: none repeat scroll 0 0 #EEEEEE;'";
	public final static String tableStyle = " style='border-collapse: collapse;width: 100%;margin: 0;padding: 0;border-collapse: collapse;'";
	public final static String thStyle = "font-size:0.8em;border: 1px solid #CCCCCC;padding: 6px;text-align: left;background: none repeat scroll 0 0 #333333;color: white;font-weight: bold;";
	public final static String pStyle = " style='margin:10px 0;color:#222;font-size:9pt;'";
	public final static String tdNoteStyle = " style='color:#476000;font-size:9pt;'";
	public final static String spanStyle = " style='color:#222;font-size:9pt;'";
	public final static String spanFrStyle = " style='color:#0000ff;font-size:9pt;'";
	public final static String pFrStyle = " style='margin:10px 0;color:#0000ff;font-size:9pt;'";
	
	private static String signatureEn = 
"<p " + pStyle + ">" +
"	Sincerely,<br/>" +
"	Rebioma Portal Team<br/>" +
"	Tel: +261 20 22 597 89<br/>" +
"	+261 33 15 880 40<br/>" +
"	Site: www.rebioma.net<br/>" +
"	E-mail: rebiomawebportal@gmail.com" +
"</p>";
	
	private static String signatureFr = 
head + 
"<p " + pFrStyle + ">" +
"	Sincèrement,<br/>" +
"	Equipe Rebioma<br/>" +
"	Tel: +261 20 22 597 89<br/>" +
"	+261 33 15 880 40<br/>" +
"	Site: www.rebioma.net<br/>" +
"	E-mail: rebiomawebportal@gmail.com" +
"</p>";
	
	private static String mailToTRBEn = 
head +
"<p" + pStyle + ">" +
"Dear #{user}," +
"</p>" +
"<p" + pStyle + ">" +
"There are new comments on the data recorded within the REBIOMA data portal assigned to you for review. " +
"These occurrences commented are listed in the table below." +
"</p>" +
"<p" + pStyle + "><table><tr>" +
"<td" + tdNoteStyle + ">" +
"		<i>Note: </i></td>" +
"	<td" + tdNoteStyle + ">" +
"		<i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
"</tr></table>" +
"</p><br />" +
"<table" + tableStyle + "><tbody>" +
"	<tr><th style='" + thStyle + "width:90px'>" +
"		&nbsp;Occurrence Id</th><th style='" + thStyle + "width:110px'>" +
"		Species</th><th style='" + thStyle + "'>" +
"		Comment</th><th style='" + thStyle + "width:100px'>" +
"		Commented by</th><th style='" + thStyle + "width:100px'>" +
"		Data owner</th><th style='" + thStyle + "width:100px'>" +
"		Date</th>" +
"	</tr>" +
"	#{table}" +
"</tbody></table>" +
"<p" + pStyle + ">" +
"	Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity. Please contact us with any questions or comments." +
"</p>" +
signatureEn;
	
	private static String mailToTRBFr = 
"<p" + pFrStyle + ">" +
"Bonjour #{user}," +
"</p>" +
"<p" + pFrStyle + ">" +
"Il y a des nouveaux commentaires sur les données enregistrées dans le portail de données de REBIOMA qui vous sont assignées pour révision. " +
"Ils sont listés dans le tableau ci-dessous." +
"</p>" +
"<p" + pFrStyle + "><table><tr>" +
"<td" + tdNoteStyle + ">" +
"		<i>NB: </i></td>" +
"	<td" + tdNoteStyle + ">" +
"		<i>Si vous voulez accéder à un commentaire sur le portail de données, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
"</tr></table>" +
"</p><br />" +
"<table" + tableStyle + "><tbody>" +
"	<tr><th style='" + thStyle + "width:90px'>" +
"		&nbsp;Occurrence Id</th><th style='" + thStyle + "width:110px'>" +
"		Espèce</th><th style='" + thStyle + "'>" +
"		Commentaire</th><th style='" + thStyle + "width:100px'>" +
"		Commenté par</th><th style='" + thStyle + "width:100px'>" +
"		Propriétaire</th><th style='" + thStyle + "width:100px'>" +
"		Date</th>" +
"	</tr>" +
"	#{table}" +
"</tbody></table>" +
"<p" + pFrStyle + ">" +
"	Nous vous remercions de votre précieuse contribution au portail de données de REBIOMA et à la conservation de la biodiversité de Madagascar. " +
"	N'hésitez à nous contacter si vous avez des questions ou des remarques." +
"</p>" +
signatureFr + 
foot;
	
	private static String mailToDOEn = 
head +
"<p" + pStyle + ">" +
"Dear #{user}," +
"</p>" +
"<p" + pStyle + ">" +
"There are new comments on your data recorded within the REBIOMA data portal. " +
"These occurrences commented are listed in the table below." +
"</p>" +
"<p" + pStyle + "><table><tr>" +
"<td" + tdNoteStyle + ">" +
"		<i>Note: </i></td>" +
"	<td" + tdNoteStyle + ">" +
"		<i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
"</tr></table>" +
"</p><br />" +
"<table" + tableStyle + "><tbody>" +
"	<tr><th style='" + thStyle + "width:90px'>" +
"		&nbsp;Occurrence Id</th><th style='" + thStyle + "width:110px'>" +
"		Species</th><th style='" + thStyle + "'>" +
"		Comment</th><th style='" + thStyle + "width:100px'>" +
"		Commented by</th><th style='" + thStyle + "width:100px'>" +
"		Date</th>" +
"	</tr>" +
"	#{table}" +
"</tbody></table>" +
"<p" + pStyle + ">" +
"	Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity. " +
"	Please contact us with any questions or comments.</p>" +
signatureEn;
	
	private static String mailToDOFr = 
"<p" + pFrStyle + ">" +
"Bonjour #{user}," +
"</p>" +
"<p" + pFrStyle + ">" +
"Il y a des nouveaux commentaires sur vos données enregistrées dans le portail de données de REBIOMA. " +
"Ils sont listés dans le tableau ci-dessous." +
"</p>" +
"<p" + pFrStyle + "><table><tr>" +
"<td" + tdNoteStyle + ">" +
"		<i>NB: </i></td>" +
"	<td" + tdNoteStyle + ">" +
"		<i>Si vous voulez accéder à un commentaire sur le portail de données, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
"</tr></table>" +
"</p><br />" +
"<table" + tableStyle + "><tbody>" +
"	<tr><th style='" + thStyle + "width:90px'>" +
"		&nbsp;Occurrence Id</th><th style='" + thStyle + "width:110px'>" +
"		Espèce</th><th style='" + thStyle + "'>" +
"		Commentaire</th><th style='" + thStyle + "width:100px'>" +
"		Commenté par</th><th style='" + thStyle + "width:100px'>" +
"		Date</th>" +
"	</tr>" +
"	#{table}" +
"</tbody></table>" +
"<p" + pFrStyle + ">" +
"	Nous vous remercions de votre précieuse contribution au portail de données de REBIOMA et à la conservation de la biodiversité de Madagascar. " +
"	N'hésitez à nous contacter si vous avez des questions ou des remarques.</p>" +
signatureFr + 
foot;
	
	private static String mailOnReviwingEn = 
head +
"<p" + pStyle + ">" +
"Dear #{user}," +
"</p>" +
"<p" + pStyle + ">" +
"An expert from the REBIOMA Taxonomic Review Board (TRB) has reviewed some of your data recorded within the REBIOMA data portal and has made comments about your occurrence data that are listed in the table below." +
"</p>" +
"<p" + pStyle + "><table><tr>" +
"<td" + tdNoteStyle + ">" +
"		<i>Note: </i></td>" +
"	<td" + tdNoteStyle + ">" +
"		<i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
"</tr></table>" +
"</p><br />" +
"<table" + tableStyle + "><tbody>" +
"	<tr><th style='" + thStyle + "width:190px'>" +
"		&nbsp;Occurrence Id</th><th style='" + thStyle + "width:110px'>" +
"		Species</th><th style='" + thStyle + "'>" +
"		Comment</th><th style='" + thStyle + "width:100px'>" +
"		TRB</th><th style='" + thStyle + "width:100px'>" +
"		Date</th>" +
"	</tr>" +
"	#{table}" +
"</tbody></table>" +
"<p" + pStyle + ">" +
"	Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity. " +
"	Please contact us with any questions or comments.</p>" +
signatureEn;
	
	private static String mailOnReviwingFr = 
"<p" + pFrStyle + ">" +
"Bonjour #{user}," +
"</p>" +
"<p" + pFrStyle + ">" +
"Un expert membres du REBIOMA Taxonomic Review Board TRB a validé vos données enregistrées sur le portail de données de REBIOMA et a déposé des commentaires sur vos données. " +
"Ils sont listés dans le tableau ci-dessous." +
"</p>" +
"<p" + pFrStyle + "><table><tr>" +
"<td" + tdNoteStyle + ">" +
"		<i>NB: </i></td>" +
"	<td" + tdNoteStyle + ">" +
"		<i>Si vous voulez accéder à un commentaire sur le portail de données, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
"</tr></table>" +
"</p><br />" +
"<table" + tableStyle + "><tbody>" +
"	<tr><th style='" + thStyle + "width:190px'>" +
"		&nbsp;Occurrence Id</th><th style='" + thStyle + "width:110px'>" +
"		Espèce</th><th style='" + thStyle + "'>" +
"		Commentaire</th><th style='" + thStyle + "width:100px'>" +
"		TRB</th><th style='" + thStyle + "width:100px'>" +
"		Date</th>" +
"	</tr>" +
"	#{table}" +
"</tbody></table>" +
"<p" + pFrStyle + ">" +
"	Nous vous remercions de votre précieuse contribution au portail de données de REBIOMA et à la conservation de la biodiversité de Madagascar. " +
"	N'hésitez à nous contacter si vous avez des questions ou des remarques.</p>" +
signatureFr + 
foot;	
	
	private static String commentToSendEn = 
head +
"<p" + pStyle + ">" +
"Dear #{user}," +
"</p>" +
"<p" + pStyle + ">" +
"	Experts from the REBIOMA Taxonomic Review Board TRB have reviewed some of your data recorded within the REBIOMA data portal." +
" 	These are the results of the review process:" +
"</p>" +
"<p" + pStyle + ">" +
"<table" + tableStyle + "><tr>" +
"	<th style='" + thStyle + "'>TRB</th>" +
"	<th style='" + thStyle + "'>Reliable</th>" +
"	<th style='" + thStyle + "'>Questionable</th>" +
"</tr>" +
"#{recapTable}" +
"</table>" +
"</p>" +
"<div" + pStyle + ">In total, you have:</div>" +
"<p" + pStyle + "><ul>" +
	"<li><span" + spanStyle + ">#{reliable} validated occurrences as reliable,</span></li>" +
	"<li><span" + spanStyle + ">#{questionable} validated occurrences as questionable,</span></li>" +
	"<li><span" + spanStyle + ">#{awaiting} occurrences awaiting for review,</span></li>" +
	"<li><span" + spanStyle + ">#{invalidated} invalidated.</span></li>" +
"</ul>" +
"</p>" +
"<introEn/>" +
"<p" + pStyle + ">" +
"	Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
"	Please contact us with any questions or comments." +
"</p>" +
signatureEn
;
	
	private static String commentToSendFr = 
"<p" + pFrStyle + ">" +
"Bonjour #{user}," +
"</p>" +
"<p" + pFrStyle + ">" +
"	Des experts membres du REBIOMA Taxonomic Review Board TRB ont validé vos données enregistrées sur le portail de données de REBIOMA. " +
"	Les résultats des validations sont les suivants:" +
"</p>" +
"<p" + pFrStyle + ">" +
"<table" + tableStyle + "><tr>" +
"	<th style='" + thStyle + "'>TRB</th>" +
"	<th style='" + thStyle + "'>Fiable</th>" +
"	<th style='" + thStyle + "'>Questionnable</th>" +
"</tr>" +
"#{recapTable}" +
"</table>" +
"</p>" +
"<div" + pFrStyle + ">En total, vous avez:</div>" +
"<p" + pStyle + "><ul>" +
	"<li><span" + spanFrStyle + ">#{reliable} occurrences validées comme fiables,</span></li>" +
	"<li><span" + spanFrStyle + ">#{questionable} occurrences validées comme questionnables,</span></li>" +
	"<li><span" + spanFrStyle + ">#{awaiting} occurrences en enttente de révision,</span></li>" +
	"<li><span" + spanFrStyle + ">#{invalidated} invalide pendant la validation automatique lors du téléchargement (upload) des données.</span></li>" +
"</ul>" +
"</p>" +
"<introFr/>" +
"<p" + pFrStyle + ">" +
"	Nous vous remercions de votre précieuse contribution au portail de données de REBIOMA et à la conservation de la biodiversité de Madagascar. " +
"	N'hésitez à nous contacter si vous avez des questions ou des remarques." +
"</p>" +
signatureFr +
"<table/>" +
foot;	
	
	public static String introDeatilTableEn = 
"<p" + CommentTable.pStyle + ">Also, the reviewers have made comments about your occurrence data that are listed in the table below.</p>" +
"<p" + CommentTable.pStyle + ">" +
"<table>" +
"<tr>" +
"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
"</tr><tr>" +
"<td " + CommentTable.tdNoteStyle + " colspan='2'><i>We encourage you to reply to the comments of experts in the data portal to resolve any questions.</i></td>" +
"</tr>" +
"</table>" +
"</p>";	
	
	public static String introDeatilTableFr = 
"<p" + CommentTable.pFrStyle + ">Aussi, les validateurs ont d&eacute;pos&eacute; des commentaires sur vos donn&eacute;es. Ils sont list&eacute;s dans le tableau ci-dessous.</p>" +
"<p" + CommentTable.pStyle + ">" +
"<table>" +
"<tr>" +
"<td" + CommentTable.tdNoteStyle + "><i>Nb:</i></td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
"</tr><tr>" +
"<td" + CommentTable.tdNoteStyle + " colspan='2'><i>Nous vous encourageons de r&eacute;pondre aux commentaires des experts pour la fiabilit&eacute;s de vos donn&eacute;es.</i></td>" +
"</tr>" +
"</table>" +
"</p>";
	
	public static String deatilTable = 
"<br /><table" + CommentTable.tableStyle + ">" +
"<tbody>" +
"	<tr>" +
"		<th style='" + CommentTable.thStyle + "width:90px'>" +
"			&nbsp;Occurrence Id" +
"		</th>" +
"		<th style='" + CommentTable.thStyle + "width:110px'>Species</th>" +
"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
"		<th style='" + CommentTable.thStyle + "width:120px'>Reviwers</th>" +
"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
"	</tr>" +
"	#{tableD}" +
"</tbody></table>";
}