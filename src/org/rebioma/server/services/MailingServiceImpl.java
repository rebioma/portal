package org.rebioma.server.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.rebioma.client.EmailException;
import org.rebioma.client.bean.CommentTable;
import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.RecapTable;
import org.rebioma.server.daemon.AppStartUp;
import org.rebioma.server.hibernate.OccurrenceCommentHbm;
import org.rebioma.server.util.EmailUtil;

import BCrypt.BCrypt;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
		
	public void sendComment(List<OccurrenceCommentModel> list, String url, Date date1, Date date2) {
		for(OccurrenceCommentModel oc : list){
			try{
				String mail = "<p" + CommentTable.pStyle + ">Dear " + oc.getFirstName() + " " + oc.getLastName() + "," +
						"<div style='color:#0000ff'>Bonjour " + oc.getFirstName() + " " + oc.getLastName() + ",</div></p>" +
						"<p" + CommentTable.pStyle + ">Experts from the REBIOMA Taxonomic Review Board TRB have reviewed  some of your data recorded within the REBIOMA data portal." +
						" These are the results of the review process:</p>" +
						"<p" + CommentTable.pFrStyle + ">Des experts membres du REBIOMA Taxonomic Review Board TRB ont valid&eacute; vos donn&eacute;es enregistr&eacute;es sur le portail de donn&eacute;es de REBIOMA." +
						" Les résultats des validations sont les suivants:</p>";
					
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
							"<li><span" + CommentTable.spanStyle + ">" + validated[3] + " invalidated (invalide pendant la validation automatique lors du téléchargement(upload) des données).</span></li>" +
						"</ul>" +
						"</p>";

				String tableIntro = "<p" + CommentTable.pStyle + ">Also, the reviewers have made comments about your occurrence data that are listed in the table below.</p>" +
						"<p" + CommentTable.pFrStyle + ">Aussi, les validateurs ont déposé des commentaires sur vos données. Ils sont listés dans le tableau ci-dessous.</p>" +
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
								"<td" + CommentTable.tdNoteStyle + ">&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i>Si vous voulez acceder à un commentaire sur le portail données, cliquez sur la colonne Occurrence Id correspondante.</i></td>" +
							"</tr><tr>" +
								"<td>&nbsp;</td><td" + CommentTable.tdNoteStyle + "><i> Nous vous encourageons de répondre aux commentaires des experts pour la fiabilités de vos données.</i></td>" +
							"</tr>" +
						"</table>" +
						"</p>";
				
				String thx = "<p" + CommentTable.pStyle + ">Thank you for your generous and important contributions to the REBIOMA data portal and the conservation of Madagascar's biodiversity." +
						" Please contact us with any questions or comments.</p>" +
						"<p" + CommentTable.pFrStyle + ">Nous vous remercions de votre pr&eacute;cieuse contribution  au portail de données de REBIOMA et à la conservation de la biodiversit&eacute; de Madagascar." +
						" N'hésitez à nous contacter si vous avez des questions ou des remarques.</p>";
				
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
					mail += trbTable + thx + signature +tableH + tableR + tableF;

				//System.out.println(mail);
				EmailUtil.adminSendEmailTo2(oc.getEmail(), "Notification about your data on the rebioma dataportal", mail);
			}catch(EmailException e){
				e.printStackTrace();
				log.error("Sending occurrence comment error: " + e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
		Date d = new Date();
		d = format.parse("2012-10-1 01:00:00");
		Date d2 = new Date();
		d2 = format.parse("2012-12-1 01:00:00");
		String url = "http://start.rebioma.net:8888/Portal.html?gwt.codesvr=start.rebioma.net:9997&";
		System.out.println(url);
		List<OccurrenceCommentModel> list = new ArrayList<OccurrenceCommentModel>();
		//list.add(new OccurrenceCommentModel(186,"","","wilfried@rebioma.net"));
		new MailingServiceImpl().sendComment(list,url,d,d2);
		System.out.println(format.format(d));
	}

	@Override
	public String[] getMailingStat() {
		Properties p = new AppStartUp().load();
		String [] string = {
				p.getProperty("start", "false"),
				p.getProperty("frequency", "2"),
				p.getProperty("date", "")
		};
		return string;
	}

	@Override
	public boolean setMailing(String stat, String frequency, String date, String url) {
		Properties p = new Properties();
		p.setProperty("start", stat);
		p.getProperty("frequency", frequency);
		p.getProperty("date", date);
		p.getProperty("url", url);
		return new AppStartUp().save(p);
	}

}
