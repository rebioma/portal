package org.rebioma.server.services;

import java.io.File;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rebioma.client.bean.CommentTable;
import org.rebioma.client.bean.LastComment;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.RecapTable;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.User;
import org.rebioma.server.daemon.AppStartUp;
import org.rebioma.server.daemon.StartUp;
import org.rebioma.server.hibernate.OccurrenceCommentHbm;
import org.rebioma.server.util.EmailUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
public class MailingServiceImpl extends RemoteServiceServlet implements org.rebioma.client.services.MailingService{
	
	private Logger log = Logger.getLogger(MailingServiceImpl.class);
	/**occurrence
	 * 
	 */
	private List<OccurrenceCommentModel> listOccurrenceComments;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
	
	@SuppressWarnings("deprecation")
	public List<OccurrenceCommentModel> getOccurrenceComments(Date date1, Date date2) {
		List<OccurrenceCommentModel> lists = new ArrayList<OccurrenceCommentModel>();
		if(date1==null && date2==null)return lists;
		lists = OccurrenceCommentHbm.getCommentInfo(date1, date2);
		return lists;
	}
	
	public HashMap<String, List<LastComment>> getLastComments(Date date1, Date date2) {
//		HashMap<String, List<LastComment>> lists = new HashMap<String, List<LastComment>>();
//		if(date1==null && date2==null)return lists;
		return OccurrenceCommentHbm.getLastComment(date1, date2);	
	}
	
	/**
	 * 
	 * @param comments
	 * @param id
	 */
	public void notifyComment(Set<OccurrenceComments> comments, User user, Integer owner) {
//		int i=0;
		String url = new StartUp().load().getProperty("url","http://data.rebioma.net");
		User dataManager = OccurrenceCommentHbm.getUserById(owner+"");
		
		if(dataManager.getId() != user.getId()){
			HashMap<String, List<OccurrenceComments>> trbMap = new HashMap<String, List<OccurrenceComments>>();
			List<RecordReview> trb = null;
			for(OccurrenceComments ocC : comments){
				if(trb==null) trb = new RecordReviewDbImpl().getRecordReviewsByOcc(ocC.getOccurrenceId());
				for(RecordReview rr: trb){
					List<OccurrenceComments> ocSet = trbMap.get(rr.getUserId()+"");
					if(ocSet==null){
						ocSet = new ArrayList<OccurrenceComments>();
					}
					ocSet.add(ocC);
					trbMap.put(rr.getUserId()+"", ocSet);
				}
			}
			try {
				Iterator it = trbMap.keySet().iterator();
				while(it.hasNext()){
					String id = (String) it.next();
					List<OccurrenceComments> ocTmp = trbMap.get(id);
					User userTmp = OccurrenceCommentHbm.getUserById(id);
					int rowNumber = 0;
					String mail = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" +
							"<p" + CommentTable.pStyle + ">Dear " + userTmp.getFirstName() + " " + dataManager.getLastName() + ",</p>" +
							"<p" + CommentTable.pFrStyle + ">Bonjour " + userTmp.getFirstName() + " " + dataManager.getLastName() + ",</p>";
							
					String tableIntro = "<p" + CommentTable.pStyle + ">There are new comments on the data recorded within the REBIOMA data portal that you had reviewed. These occurrences commented are listed in the table below.</p>" +
							"<p" + CommentTable.pFrStyle + ">Il y a des nouvelles commentaires sur les donn&eacute;es enregistr&eacute;es dans le portail de donn&eacute;es de REBIOMA que vous aviez revis&eacute;. Ils sont list&eacute;s dans le tableau ci-dessous.</p>" +
							"<p" + CommentTable.pStyle + ">" +
								"<table>" +
									"<tr>" +
										"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
									"</tr><tr>" +
									"</tr>" +
								"</table>" +
								"<table>" +
								"<tr>" +
									"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
								"</tr><tr>" +
								"</tr>" +
							"</table>" +
							"</p>";
					
					String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
							" Please contact us with any questions or comments.</p>" +
							"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de donn&eacute;es de REBIOMA et &agrave; la conservation de la biodiversit&eacute; de Madagascar." +
							" N'h&eacute;sitez &agrave; nous contacter si vous avez des questions ou des remarques.</p>";
					
					String signature = "<p" + CommentTable.pStyle + ">Sincerely,<br/>" +
							"Rebioma Portal Team<br/>" +
							"Tel: +261 20 22 597 89<br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+261 033 15 880 40<br/>" +
							"Site:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;www.rebioma.net<br/>" +
							"E-mail: rebiomawebportal@gmail.com</p>";
					
					String tableH = "<br /><table" + CommentTable.tableStyle + ">" +
							"<tbody>" +
							"	<tr>" +
							"		<th style='" + CommentTable.thStyle + "width:90px'>" +
							"			&nbsp;Occurrence Id" +
							"		</th>" +
							"		<th style='" + CommentTable.thStyle + "width:110px'>Species</th>" +
							"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
							"		<th style='" + CommentTable.thStyle + "width:120px'>DataOwner</th>" +
							"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
							"	</tr>";
					
					String tableF = "</tbody></table>";
					String tableR = "";
					for(OccurrenceComments oc: ocTmp){
						Occurrence occurrence = DBFactory.getOccurrenceDb().findById(oc.getOccurrenceId());
						tableR+= new CommentTable(
								oc.getOccurrenceId(),
								occurrence.getAcceptedSpecies(),
								oc.getUserComment(),
								dataManager.getFirstName() + " " + dataManager.getLastName(),
								format.format(oc.getDateCommented()),
								url,
								userTmp.getEmail(),
								userTmp.getPasswordHash()
							).toTable(rowNumber);
							rowNumber++;
					}
					mail += tableIntro + tableH + tableR + tableF + thx + signature + "</body></html>";
//					PrintWriter p = new PrintWriter(new File("D:/mail_c_" + userTmp.getId() + ".html"));
//					p.write(mail);
//					p.close();
					EmailUtil.adminSendEmailTo2(userTmp.getEmail(), CommentTable.objectNotification, mail);
				}
			}catch(Exception e){
			  e.printStackTrace();
			  log.error("Sending occurrence comment error: " + e.getMessage());
			}
		} else {
		  try{
			int rowNumber = 0;
			String mail = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" +
					"<p" + CommentTable.pStyle + ">Dear " + dataManager.getFirstName() + " " + dataManager.getLastName() + ",</p>" +
					"<p" + CommentTable.pFrStyle + ">Bonjour " + dataManager.getFirstName() + " " + dataManager.getLastName() + ",</p>";
					
				
			String tableIntro = "<p" + CommentTable.pStyle + ">An expert from the REBIOMA Taxonomic Review Board TRB have reviewed  some of your data recorded within the REBIOMA data portal and have made comments about your occurrence data that are listed in the table below.</p>" +
					"<p" + CommentTable.pFrStyle + ">Un expert membres du REBIOMA Taxonomic Review Board TRB ont valid&eacute; vos donn&eacute;es enregistr&eacute;es sur le portail de donn&eacute;es de REBIOMA et ont d&eacute;pos&eacute; des commentaires sur vos donn&eacute;es. Ils sont list&eacute;s dans le tableau ci-dessous.</p>" +
					"<p" + CommentTable.pStyle + ">" +
						"<table>" +
							"<tr>" +
								"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
							"</tr><tr>" +
								"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>We encourage you to reply to the comments of experts in the data portal to resolve any questions.</i></td>" +
							"</tr>" +
						"</table>" +
						"<table>" +
						"<tr>" +
							"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
						"</tr><tr>" +
							"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i> Nous vous encourageons de r&eacute;pondre aux commentaires des experts pour la fiabilit&eacute;s de vos donn&eacute;es.</i></td>" +
						"</tr>" +
					"</table>" +
					"</p>";
			
			String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
					" Please contact us with any questions or comments.</p>" +
					"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de donn&eacute;es de REBIOMA et &agrave; la conservation de la biodiversit&eacute; de Madagascar." +
					" N'h&eacute;sitez &agrave; nous contacter si vous avez des questions ou des remarques.</p>";
			
			String signature = "<p" + CommentTable.pStyle + ">Sincerely,<br/>" +
					"Rebioma Portal Team<br/>" +
					"Tel: +261 20 22 597 89<br/>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+261 033 15 880 40<br/>" +
					"Site:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;www.rebioma.net<br/>" +
					"E-mail: rebiomawebportal@gmail.com</p>";
			
			String tableH = "<br /><table" + CommentTable.tableStyle + ">" +
					"<tbody>" +
					"	<tr>" +
					"		<th style='" + CommentTable.thStyle + "width:90px'>" +
					"			&nbsp;Occurrence Id" +
					"		</th>" +
					"		<th style='" + CommentTable.thStyle + "width:110px'>Species</th>" +
					"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
					"		<th style='" + CommentTable.thStyle + "width:120px'>Reviwers</th>" +
					"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
					"	</tr>";
			
			String tableF = "</tbody></table>";
			String tableR = "";
			for(OccurrenceComments oc : comments){
				Occurrence occurrence = DBFactory.getOccurrenceDb().findById(oc.getOccurrenceId());
				tableR+= new CommentTable(
					oc.getOccurrenceId(),
					occurrence.getAcceptedSpecies(),
					oc.getUserComment(),
					user.getFirstName() + " " + user.getLastName(),
					format.format(oc.getDateCommented()),
					url,
					dataManager.getEmail(),
					dataManager.getPasswordHash()
				).toTable(rowNumber);
				rowNumber++;
			}
			mail += tableIntro + tableH + tableR + tableF + thx + signature + "</body></html>";
//			PrintWriter p = new PrintWriter(new File("D:/mail_c.html"));
//			p.write(mail);
//			p.close();
			EmailUtil.adminSendEmailTo2(dataManager.getEmail(), CommentTable.objectNotification, mail);
		  }catch(Exception e){
			  e.printStackTrace();
			  log.error("Sending occurrence comment error: " + e.getMessage());
		  }
		}
	}
	
	public void notifyComment(HashMap<Integer, List<String>> mailData, User user, String comment) {
//		int i=0;
		String url = new StartUp().load().getProperty("url","http://data.rebioma.net");
		
		Iterator it = mailData.keySet().iterator();
		while(it.hasNext()){
			int id = (Integer) it.next();
			User userTmp = OccurrenceCommentHbm.getUserById(id+"");
			List<String> list = mailData.get(id);
			try {
				int rowNumber = 0;
				String mail = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" +
						"<p" + CommentTable.pStyle + ">Dear " + userTmp.getFirstName() + " " + userTmp.getLastName() + ",</p>" +
						"<p" + CommentTable.pFrStyle + ">Bonjour " + userTmp.getFirstName() + " " + userTmp.getLastName() + ",</p>";
							
				String tableIntro = "<p" + CommentTable.pStyle + ">An expert from the REBIOMA Taxonomic Review Board TRB have reviewed  some of your data recorded within the REBIOMA data portal and have made comments about your occurrence data that are listed in the table below.</p>" +
						"<p" + CommentTable.pFrStyle + ">Un expert membres du REBIOMA Taxonomic Review Board TRB ont valid&eacute; vos donn&eacute;es enregistr&eacute;es sur le portail de donn&eacute;es de REBIOMA et ont d&eacute;pos&eacute; des commentaires sur vos donn&eacute;es. Ils sont list&eacute;s dans le tableau ci-dessous.</p>" +
						"<p" + CommentTable.pStyle + ">" +
							"<table>" +
								"<tr>" +
									"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
								"</tr><tr>" +
									"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>We encourage you to reply to the comments of experts in the data portal to resolve any questions.</i></td>" +
								"</tr>" +
							"</table>" +
							"<table>" +
							"<tr>" +
								"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
							"</tr><tr>" +
								"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i> Nous vous encourageons de r&eacute;pondre aux commentaires des experts pour la fiabilit&eacute;s de vos donn&eacute;es.</i></td>" +
							"</tr>" +
						"</table>" +
						"</p>";
					
					String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
							" Please contact us with any questions or comments.</p>" +
							"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de donn&eacute;es de REBIOMA et &agrave; la conservation de la biodiversit&eacute; de Madagascar." +
							" N'h&eacute;sitez &agrave; nous contacter si vous avez des questions ou des remarques.</p>";
					
					String signature = "<p" + CommentTable.pStyle + ">Sincerely,<br/>" +
							"Rebioma Portal Team<br/>" +
							"Tel: +261 20 22 597 89<br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+261 033 15 880 40<br/>" +
							"Site:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;www.rebioma.net<br/>" +
							"E-mail: rebiomawebportal@gmail.com</p>";
					
					String tableH = "<br /><table" + CommentTable.tableStyle + ">" +
							"<tbody>" +
							"	<tr>" +
							"		<th style='" + CommentTable.thStyle + "width:200px'>" +
							"			&nbsp;Occurrence Id (Species)</th>" +
							"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
							"		<th style='" + CommentTable.thStyle + "width:120px'>TRB</th>" +
							"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
							"	</tr>";
					
					String tableF = "</tbody></table>";
					String tableR = "";
					tableR+= new CommentTable(
						list,
						comment,
						userTmp.getFirstName() + " " + userTmp.getLastName(),
						format.format(new Date()),
						url,
						userTmp.getEmail(),
						userTmp.getPasswordHash()
					).toTable(rowNumber);
					rowNumber++;
					mail += tableIntro + tableH + tableR + tableF + thx + signature + "</body></html>";
//					PrintWriter p = new PrintWriter(new File("D:/mail_c_" + userTmp.getId() + ".html"));
//					p.write(mail);
//					p.close();
					log.info("sendin mail...");
					EmailUtil.adminSendEmailTo2(userTmp.getEmail(), CommentTable.objectNotification, mail);
			}catch(Exception e){
			  e.printStackTrace();
			  log.error("Sending occurrence comment error: " + e.getMessage());
			}
		}
	}
	
	/**
	 * sent trb's commnents to the dataowner 
	 * @param list
	 * @param url
	 * @param date1
	 * @param date2
	 */
	public void sendComment(List<OccurrenceCommentModel> list, String url, Date date1, Date date2) {
		int i=0;
		for(OccurrenceCommentModel oc : list){
			try{
				String mail = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" +
						"<p" + CommentTable.pStyle + ">Dear " + oc.getFirstName() + " " + oc.getLastName() + ",</p>" +
						"<p" + CommentTable.pFrStyle + ">Bonjour " + oc.getFirstName() + " " + oc.getLastName() + ",</p>" +
						"<p" + CommentTable.pStyle + ">Experts from the REBIOMA Taxonomic Review Board TRB have reviewed  some of your data recorded within the REBIOMA data portal." +
						" These are the results of the review process:</p>" +
						"<p" + CommentTable.pFrStyle + ">Des experts membres du REBIOMA Taxonomic Review Board TRB ont valid&eacute; vos donn&eacute;es enregistr&eacute;es sur le portail de donn&eacute;es de REBIOMA." +
						" Les r&eacute;sultats des validations sont les suivants:</p>";
					
				String trbTable = "<p" + CommentTable.pStyle + "><table" + CommentTable.tableStyle + "><tr>" +
						"<th style='" + CommentTable.thStyle + "'>TRB</th>" +
						"<th style='" + CommentTable.thStyle + "'>Reliable</th>" +
						"<th style='" + CommentTable.thStyle + "'>Questionable</th>" +
						//"<th style='" + CommentTable.thStyle + "'>Awaiting review</th>" +
						"</tr>";
						HashMap<String, RecapTable> hRecap = OccurrenceCommentHbm.occurrenceTRBState(oc.getUId());
						Set<String> set = hRecap.keySet();
						Iterator it = set.iterator();
						int rowNum =0;
						while(it.hasNext()){
							RecapTable recap = hRecap.get(it.next());
							trbTable += "<tr" + (rowNum%2==1?CommentTable.trStyle:"") + ">" +
									"<td" + CommentTable.tdStyle + ">" + recap.getFirstName() + " " + recap.getLastName() + "</td>" +
									"<td" + CommentTable.tdStyle + ">" + recap.getReliable() + "</td>" +
									"<td" + CommentTable.tdStyle + ">" + recap.getQuestionable() + "</td>" +
									//"<td" + CommentTable.tdStyle + ">" + recap.getaReview() + "</td>" +
									"</tr>";
							rowNum++;
						}
				trbTable += "</table></p>";
				
				String validated[] = OccurrenceCommentHbm.occurrenceState(oc.getUId());
				
				trbTable += "<div" + CommentTable.pStyle + ">In total, you have:</div><div style='color:#0000ff'>En total, vous avez:</div>" +
						"<p" + CommentTable.pStyle + "><ul>" +
							"<li><span" + CommentTable.spanStyle + ">" + validated[0] + " validated occurrences as reliable (occurrences valid&eacute;es comme fiables),</span></li>" +
							"<li><span" + CommentTable.spanStyle + ">" + validated[1] + " validated occurrences as questionable (occurrences valid&eacute;es comme questionnables),</span></li>" +
							"<li><span" + CommentTable.spanStyle + ">" + validated[2] + " occurrences awaiting for review (occurrences en enttente de r&eacute;vision),</span></li>" +
							"<li><span" + CommentTable.spanStyle + ">" + validated[3] + " invalidated (invalide pendant la validation automatique lors du t&eacute;l&eacute;chargement(upload) des donn&eacute;es).</span></li>" +
						"</ul>" +
						"</p>";

				String tableIntro = "<p" + CommentTable.pStyle + ">Also, the reviewers have made comments about your occurrence data that are listed in the table below.</p>" +
						"<p" + CommentTable.pFrStyle + ">Aussi, les validateurs ont d&eacute;pos&eacute; des commentaires sur vos donn&eacute;es. Ils sont list&eacute;s dans le tableau ci-dessous.</p>" +
						"<p" + CommentTable.pStyle + ">" +
							"<table>" +
								"<tr>" +
									"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
								"</tr><tr>" +
									"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>We encourage you to reply to the comments of experts in the data portal to resolve any questions.</i></td>" +
								"</tr>" +
							"</table>" +
							"<table>" +
							"<tr>" +
								"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
							"</tr><tr>" +
								"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i> Nous vous encourageons de r&eacute;pondre aux commentaires des experts pour la fiabilit&eacute;s de vos donn&eacute;es.</i></td>" +
							"</tr>" +
						"</table>" +
						"</p>";
				
				String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
						" Please contact us with any questions or comments.</p>" +
						"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de donn&eacute;es de REBIOMA et &agrave; la conservation de la biodiversit&eacute; de Madagascar." +
						" N'h&eacute;sitez &agrave; nous contacter si vous avez des questions ou des remarques.</p>";
				
				String signature = "<p" + CommentTable.pStyle + ">Sincerely,<br/>" +
						"Rebioma Portal Team<br/>" +
						"Tel: +261 20 22 597 89<br/>" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+261 033 15 880 40<br/>" +
						"Site:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;www.rebioma.net<br/>" +
						"E-mail: rebiomawebportal@gmail.com</p>";
				
				String tableH = "<br /><table" + CommentTable.tableStyle + ">" +
						"<tbody>" +
						"	<tr>" +
						"		<th style='" + CommentTable.thStyle + "width:90px'>" +
						"			&nbsp;Occurrence Id" +
						"		</th>" +
						"		<th style='" + CommentTable.thStyle + "width:110px'>Species</th>" +
						"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
						"		<th style='" + CommentTable.thStyle + "width:120px'>Reviwers</th>" +
						"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
						"	</tr>";
				
				String tableF = "</tbody></table>";
				
				String tableR = OccurrenceCommentHbm.creatCommentMail(oc, url, date1, date2);
				
				if(tableR.length()<=1)
					mail += trbTable + thx + signature;
				else
					mail += trbTable + thx + signature +tableH + tableR + tableF + "</body></html>";

//				PrintWriter p = new PrintWriter(new File("D:/mail_" + i + ".html"));
//				p.write(mail);
//				p.close();
//				i++;
				EmailUtil.adminSendEmailTo2(oc.getEmail(), CommentTable.objetDataOwner, mail);
			}catch(Exception e){
				e.printStackTrace();
				log.error("Sending occurrence comment error: " + e.getMessage());
			}
		}
	}
	
	/**
	 * send dataowner's comments to the trb
	 * @param list
	 * @param url
	 * @param date1
	 * @param date2
	 */
	public void sendComment(HashMap<String, List<LastComment>> map, String url, Date date1, Date date2) {
		Iterator it = map.keySet().iterator();
		int i=0;
		while(it.hasNext()){
			String uid = (String) it.next();
			List<LastComment> list = map.get(uid);
			User u = OccurrenceCommentHbm.getUserById(uid);
			try{
				String head = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" +
					"<p" + CommentTable.pStyle + ">Dear " + u.getFirstName() + " " + u.getLastName() + ",</p>" +
					"<p" + CommentTable.pFrStyle + ">Bonjour " + u.getFirstName() + " " + u.getLastName() + ",</p>" +
					"<p" + CommentTable.pStyle + ">There are new comments on the data recorded within the REBIOMA data portal that you had reviewed." +
					" These occurrences commented are listed in the table below.</p>" +
					"<p" + CommentTable.pFrStyle + ">Il y a des nouvelles commentaires sur les donn&eacute;es enregistr&eacute;es dans le portail de donn&eacute;es de REBIOMA que vous aviez revis&eacute;." +
					" Les occurrences comment&eacute;s sont list&eacute;s dans le tableau ci-dessous.</p>" +
					"<p" + CommentTable.pStyle + ">" +
						"<table>" +
							"<tr>" +
								"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
							"</tr><tr>" +
								"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>We encourage you to reply to the comments of experts in the data portal to resolve any questions.</i></td>" +
							"</tr>" +
						"</table>" +
						"<table>" +
						"<tr>" +
							"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
						"</tr><tr>" +
							"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i> Nous vous encourageons de r&eacute;pondre aux commentaires des experts pour la fiabilit&eacute;s de vos donn&eacute;es.</i></td>" +
						"</tr>" +
					"</table>" +
					"</p>";
			
				String tableH = "<br /><table" + CommentTable.tableStyle + ">" +
						"<tbody>" +
						"	<tr>" +
						"		<th style='" + CommentTable.thStyle + "width:90px'>" +
						"			&nbsp;Occurrence Id" +
						"		</th>" +
						"		<th style='" + CommentTable.thStyle + "width:110px'>Species</th>" +
						"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
						"		<th style='" + CommentTable.thStyle + "width:120px'>Data manager</th>" +
						"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
						"	</tr>";
				
				String tableR = OccurrenceCommentHbm.creatCommentMail(list, u, url, date1, date2);
				
				String tableF = "</tbody></table>";
				
				String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
						" Please contact us with any questions or comments.</p>" +
						"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de donn&eacute;es de REBIOMA et &agrave; la conservation de la biodiversit&eacute; de Madagascar." +
						" N'h&eacute;sitez &agrave; nous contacter si vous avez des questions ou des remarques.</p>";
				
				String signature = "<p" + CommentTable.pStyle + ">Sincerely,<br/>" +
						"Rebioma Portal Team<br/>" +
						"Tel: +261 20 22 597 89<br/>" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+261 033 15 880 40<br/>" +
						"Site:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;www.rebioma.net<br/>" +
						"E-mail: rebiomawebportal@gmail.com</p>";
				String mail = "";
				if(tableR.length()<=1)
					continue;
				else
					mail = head + tableH + tableR + tableF + thx +  signature + "</body></html>";
//				PrintWriter p = new PrintWriter(new File("d:/mail"+i+".html"));
//				p.write(mail);i++;
//				p.close();
				EmailUtil.adminSendEmailTo2(u.getEmail(), CommentTable.objectNotification, mail);
			}catch(Exception e){
				e.printStackTrace();
				log.error("Sending occurrence comment error: " + e.getMessage());
			}
		}
	}
	
	/**
	 * send dataowner's comments to the trb 
	 * @param list
	 * @param url
	 * @param date1
	 * @param date2
	 */
	public void sendComment(HashMap<String, List<LastComment>> map, List<OccurrenceCommentModel> lists, String url, Date date1, Date date2) {
		Iterator it = map.keySet().iterator();
		int i=0;
		while(it.hasNext()){
			String uid = (String) it.next();
			boolean doIt = false;
			for(OccurrenceCommentModel ocCM: lists){
				if(uid.equals(ocCM.getUId() + "")){
					doIt = true;
					break;
				}
			}
			if(!doIt)continue;
			List<LastComment> list = map.get(uid);
			User u = OccurrenceCommentHbm.getUserById(uid);
			try{
				String head = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" +
					"<p" + CommentTable.pStyle + ">Dear " + u.getFirstName() + " " + u.getLastName() + ",</p>" +
					"<p" + CommentTable.pFrStyle + ">Bonjour " + u.getFirstName() + " " + u.getLastName() + ",</p>" +
					"<p" + CommentTable.pStyle + ">There are new comments on the data recorded within the REBIOMA data portal that you had reviewed." +
					" These occurrences commented are listed in the table below.</p>" +
					"<p" + CommentTable.pFrStyle + ">Il y a des nouvelles commentaires sur les donn&eacute;es enregistr&eacute;es dans le portail de donn&eacute;es de REBIOMA que vous aviez revis&eacute;." +
					" Les occurrences comment&eacute;s sont list&eacute;s dans le tableau ci-dessous.</p>" +
					"<p" + CommentTable.pStyle + ">" +
						"<table>" +
							"<tr>" +
								"<td" + CommentTable.tdNoteStyle + "><i>Note: </i></td><td" + CommentTable.tdNoteStyle + "><i>if you want to see a comment on the data portal, click on the corresponding column Occurrence Id.</i></td>" +
							"</tr><tr>" +
								"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>We encourage you to reply to the comments of experts in the data portal to resolve any questions.</i></td>" +
							"</tr>" +
						"</table>" +
						"<table>" +
						"<tr>" +
							"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder &agrave; un commentaire sur le portail donn&eacute;es, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
						"</tr><tr>" +
							"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i> Nous vous encourageons de r&eacute;pondre aux commentaires des experts pour la fiabilit&eacute;s de vos donn&eacute;es.</i></td>" +
						"</tr>" +
					"</table>" +
					"</p>";
			
				String tableH = "<br /><table" + CommentTable.tableStyle + ">" +
						"<tbody>" +
						"	<tr>" +
						"		<th style='" + CommentTable.thStyle + "width:90px'>" +
						"			&nbsp;Occurrence Id" +
						"		</th>" +
						"		<th style='" + CommentTable.thStyle + "width:110px'>Species</th>" +
						"		<th style='" + CommentTable.thStyle + "'>Comments</th>" +
						"		<th style='" + CommentTable.thStyle + "width:120px'>Data manager</th>" +
						"		<th style='" + CommentTable.thStyle + "width:100px'>Date</th>" +
						"	</tr>";
				
				String tableR = OccurrenceCommentHbm.creatCommentMail(list, u, url, date1, date2);
				
				String tableF = "</tbody></table>";
				
				String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
						" Please contact us with any questions or comments.</p>" +
						"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de donn&eacute;es de REBIOMA et &agrave; la conservation de la biodiversit&eacute; de Madagascar." +
						" N'h&eacute;sitez &agrave; nous contacter si vous avez des questions ou des remarques.</p>";
				
				String signature = "<p" + CommentTable.pStyle + ">Sincerely,<br/>" +
						"Rebioma Portal Team<br/>" +
						"Tel: +261 20 22 597 89<br/>" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+261 033 15 880 40<br/>" +
						"Site:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;www.rebioma.net<br/>" +
						"E-mail: rebiomawebportal@gmail.com</p>";
				String mail = "";
				if(tableR.length()<=1)
					continue;
				else
					mail = head + tableH + tableR + tableF + thx +  signature + "</body></html>";
//				PrintWriter p = new PrintWriter(new File("d:/mail"+i+".html"));
//				p.write(mail);i++;
//				p.close();
				EmailUtil.adminSendEmailTo2(u.getEmail(), CommentTable.objectNotification, mail);
			}catch(Exception e){
				e.printStackTrace();
				log.error("Sending occurrence comment error: " + e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();
		d = format.parse("2013-01-01 01:00:00");
		Date d2 = new Date();
//		d2 = format.parse("2012-12-1 01:00:00");
		String url = "http://start.rebioma.net:8888/Portal.html?gwt.codesvr=start.rebioma.net:9997&";
		System.out.println(url);
		List<OccurrenceCommentModel> list = new ArrayList<OccurrenceCommentModel>();
		list.add(new OccurrenceCommentModel(162, "",  "", "wilfried@rebioma.net", "", ""));
//		new MailingServiceImpl().sendComment(list,url,d,d2);
//		System.out.println(format.format(d));
		MailingServiceImpl mail = new MailingServiceImpl();
		HashMap<String, List<LastComment>> map = mail.getLastComments(d, d2);
		mail.sendComment(map, list, url, d, d2);
	}

	@Override
	public String[] getMailingStat() {
		Properties p = new AppStartUp().load();
		String [] string = {
				p.getProperty("start", "false"),
				p.getProperty("frequency", "2"),
				p.getProperty("date", "1900-01-01 01:00:00")
		};
		return string;
	}

	@Override
	public boolean setMailing(String stat, String frequency, String date, String url) {
		Properties p = new Properties();
		p.setProperty("start", stat);
		p.setProperty("frequency", frequency);
		p.setProperty("date", date);
		p.setProperty("url", url);
		return new AppStartUp().save(p);
	}
	
	private List<OccurrenceCommentModel> occurrenceComments;
	@Override
	public PagingLoadResult<OccurrenceCommentModel> getOccurrenceComments(PagingLoadConfig config, String mailTo, Date date1, Date date2) {
		
		occurrenceComments = getOccurrenceComments(mailTo, date1, date2);

		if (config.getSortInfo().size() > 0) {
			SortInfo sort = config.getSortInfo().get(0);
			if (sort.getSortField() != null) {
				final String sortField = sort.getSortField();
				if (sortField != null) {
					Collections.sort(occurrenceComments, sort.getSortDir().comparator(new Comparator<OccurrenceCommentModel>() {
						public int compare(OccurrenceCommentModel p1, OccurrenceCommentModel p2) {
							if (sortField.equals("email")) {
								return p1.getEmail().compareTo(p2.getEmail());
							} else if (sortField.startsWith("First")) {
								return p1.getFirstName().compareTo(p2.getFirstName());
							} else if (sortField.startsWith("Last")) {
								return p1.getLastName().compareTo(p2.getLastName());
							} else if (sortField.startsWith("Comment")) {
								return p1.getCommentDetail().compareTo(p2.getCommentDetail());
							}
							return 0;
						}
					}));
				}
			}
		}
		ArrayList<OccurrenceCommentModel> sublist = new ArrayList<OccurrenceCommentModel>();
		int start = config.getOffset();
		int limit = occurrenceComments.size();
		if (config.getLimit() > 0) {
			limit = Math.min(start + config.getLimit(), limit);
		}
		for (int i = config.getOffset(); i < limit; i++) {
			sublist.add(occurrenceComments.get(i));
		}
		return new PagingLoadResultBean<OccurrenceCommentModel>(sublist, occurrenceComments.size(), config.getOffset());
	}
	
	public List<OccurrenceCommentModel> getOccurrenceComments(String mailTo, Date date1, Date date2) {
		List<OccurrenceCommentModel> lists = new ArrayList<OccurrenceCommentModel>();
		if(date1==null && date2==null)return lists;
		if(mailTo.equals("TRB")){
			HashMap<String, List<LastComment>> map = getLastComments(date1, date2);
			lists = OccurrenceCommentHbm.getCommentInfo(map, date1, date2);
		}else 
			lists = OccurrenceCommentHbm.getCommentInfo(date1, date2);
		return lists;	
	}

	@Override
	public boolean sendSelected(String mailTo, Date date1, Date date2,
			List<OccurrenceCommentModel> list) {
		String url = AppStartUp.load().getProperty("url","http://data.rebioma.net");
		if(mailTo.equals("TRB")){
			HashMap<String, List<LastComment>> map = getLastComments(date1, date2);
			sendComment(map, list, url, date1, date2);
		}else 
			sendComment(list, url, date1, date2);
		return true;
	}
	
}
