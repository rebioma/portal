package org.rebioma.server.services;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.rebioma.client.EmailException;
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
		
		if(dataManager.getId().equals(user.getId())){
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
					String userFullName = userTmp.getFirstName() + " " + userTmp.getLastName();

					String tableR = "";
					
					for(OccurrenceComments oc: ocTmp){
						Occurrence occurrence = DBFactory.getOccurrenceDb().findById(oc.getOccurrenceId());
						CommentTable cTable = new CommentTable(
								oc.getOccurrenceId(),
								occurrence.getAcceptedSpecies(),
								oc.getUserComment(),
								dataManager.getFirstName() + " " + dataManager.getLastName(),
								format.format(oc.getDateCommented()),
								url,
								userTmp.getEmail(),
								userTmp.getPasswordHash()
							);
						cTable.setCommentedBy(user.getFirstName() + " " + user.getLastName());
						tableR+= cTable.toTable(rowNumber);
							rowNumber++;
					}
					String mail = CommentTable.getMailToTRB();
					mail = mail.replace("#{user}", userFullName);
					mail = mail.replace("#{table}", tableR);
//					PrintWriter p = new PrintWriter(new File("D:/mail_c_" + userTmp.getId() + ".html"));
//					p.write(mail);
//					p.close();
					EmailUtil.adminSendEmailTo2(userTmp.getEmail(), CommentTable.objectTRB, mail);
				}
			}catch(Exception e){
			  e.printStackTrace();
			  log.error("Sending occurrence comment error: " + e.getMessage());
			}
		} else {
		  try{
			int rowNumber = 0;
			String tableR 		= "";
			
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
			String userFullName = dataManager.getFirstName() + " " + dataManager.getLastName();
			String mail 		= CommentTable.getMailToDO();
			mail = mail.replace("#{user}", userFullName);
			mail = mail.replace("#{table}", tableR);
//			PrintWriter p = new PrintWriter(new File("D:/mail_c.html"));
//			p.write(mail);
//			p.close();
			EmailUtil.adminSendEmailTo2(dataManager.getEmail(), CommentTable.objectDataOwner, mail);
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
				String userFullName = userTmp.getFirstName() + " " + userTmp.getLastName();
				String mail = CommentTable.getMailOnReviwing();
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
				mail = mail.replace("#{user}", userFullName);
				mail = mail.replace("#{table}", tableR);
//				PrintWriter p = new PrintWriter(new File("D:/mail_c_" + userTmp.getId() + ".html"));
//				p.write(mail);
//				p.close();
//				log.info("sendin mail...");
				EmailUtil.adminSendEmailTo2(userTmp.getEmail(), CommentTable.objectTRB, mail);
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
				String mail = CommentTable.getCommentToSend();
				String userFullName = oc.getFirstName() + " " + oc.getLastName();
				String recapTable = "";
				HashMap<String, RecapTable> hRecap = OccurrenceCommentHbm.occurrenceTRBState(oc.getUId());
				Set<String> set = hRecap.keySet();
				Iterator it = set.iterator();
				int rowNum =0;
				while(it.hasNext()){
					RecapTable recap = hRecap.get(it.next());
					recapTable += "<tr" + (rowNum%2==1?CommentTable.trStyle:"") + ">" +
							"<td" + CommentTable.tdStyle + ">" + recap.getFirstName() + " " + recap.getLastName() + "</td>" +
							"<td" + CommentTable.tdStyle + ">" + recap.getReliable() + "</td>" +
							"<td" + CommentTable.tdStyle + ">" + recap.getQuestionable() + "</td>" +
							//"<td" + CommentTable.tdStyle + ">" + recap.getaReview() + "</td>" +
							"</tr>";
					rowNum++;
				}
				
				String validated[] = OccurrenceCommentHbm.occurrenceState(oc.getUId());
				
				String tableR = OccurrenceCommentHbm.creatCommentMail(oc, url, date1, date2);
				
				mail = mail.replace("#{user}", userFullName);
				mail = mail.replace("#{recapTable}", recapTable);
				mail = mail.replace("#{reliable}", validated[0]);
				mail = mail.replace("#{questionable}", validated[1]);
				mail = mail.replace("#{awaiting}", validated[2]);
				mail = mail.replace("#{invalidated}", validated[3]);
				if(tableR.length()>1){
					mail = mail.replace("<introEn/>", CommentTable.introDeatilTableEn);
					mail = mail.replace("<introFr/>", CommentTable.introDeatilTableFr);
					mail = mail.replace("<table/>", CommentTable.deatilTable);
					mail = mail.replace("#{tableD}", tableR);
				}

//				PrintWriter p = new PrintWriter(new File("D:/mail_" + i + ".html"));
//				p.write(mail);
//				p.close();
//				i++;
				EmailUtil.adminSendEmailTo2(oc.getEmail(), CommentTable.objectDataOwner, mail);
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
				
				String mail = CommentTable.getMailToTRB();
				String userFullName = u.getFirstName() + " " + u.getLastName();
				
				String tableR = OccurrenceCommentHbm.creatCommentMail(list, u, url, date1, date2);
				
				if(tableR.length()<=1)
					continue;
				mail = mail.replace("#{user}", userFullName);
				mail = mail.replace("#{table}", tableR);
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
				String mail = CommentTable.getMailToTRB();
				String userFullName = u.getFirstName() + " " + u.getLastName();
				String tableR = OccurrenceCommentHbm.creatCommentMail(list, u, url, date1, date2);
				
				if(tableR.length()<=1)
					continue;
				mail = mail.replace("#{user}", userFullName);
				mail = mail.replace("#{table}", tableR);
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
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date d = new Date();
//		d = format.parse("2013-01-01 01:00:00");
//		Date d2 = new Date();
////		d2 = format.parse("2012-12-1 01:00:00");
//		String url = "http://start.rebioma.net:8888/Portal.html?gwt.codesvr=start.rebioma.net:9997&";
//		System.out.println(url);
//		List<OccurrenceCommentModel> list = new ArrayList<OccurrenceCommentModel>();
//		list.add(new OccurrenceCommentModel(162, "",  "", "wilfried@rebioma.net", "", ""));
////		new MailingServiceImpl().sendComment(list,url,d,d2);
////		System.out.println(format.format(d));
//		MailingServiceImpl mail = new MailingServiceImpl();
//		HashMap<String, List<LastComment>> map = mail.getLastComments(d, d2);
//		mail.sendComment(map, list, url, d, d2);
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
	
	/**
	 * Notify the data owner by email when his data is downloaded 
	 * @param ownerMap list of data owner's email
	 * @param title 
	 * @param firstN
	 * @param lastN
	 * @param activity
	 * @param email
	 * @param institution
	 * @param dataUE
	 * @throws EmailException 
	 */
	public void sendDownloadMail(HashMap ownerMap, String title,
			String firstN, String lastN, String activity, String email,
			String institution, String dataUE) {
		Iterator it = ownerMap.keySet().iterator();
		while(it.hasNext()) {
			String oEmail = (String) ownerMap.get(it.next());
			User user = new UserDbImpl().findByEmail(oEmail);
			String mail = CommentTable.getDownloadMail();
			mail = mail.replace("#{user}", user.getFirstName());
			mail = mail.replace("#{title}", title);
			mail = mail.replace("#{firstN}", firstN);
			mail = mail.replace("#{lastN}", lastN);
			mail = mail.replace("#{activity}", activity);
			mail = mail.replace("#{email}", email);
			mail = mail.replace("#{institution}", institution);
			mail = mail.replace("#{dataue}", dataUE);
//			PrintWriter p = null;
//			try {
//				p = new PrintWriter(new File("/Users/razsoa/Documents/Travail/download.html"));
//			} catch (FileNotFoundException e1) {
//				e1.printStackTrace();
//			}
//			p.write(mail);
//			p.close();
			try {
				EmailUtil.adminSendEmailTo2("aimewilfried2@yahoo.fr", oEmail + " " + CommentTable.objectNotification, mail);
			} catch (EmailException e) {
				e.printStackTrace();
			}
		}
	}
	
}
