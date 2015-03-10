package org.rebioma.server.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.rebioma.client.EmailException;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.RevalidationException;
import org.rebioma.client.bean.RevalidationResult;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.RevalidationService;
import org.rebioma.server.util.EmailUtil;
import org.rebioma.server.util.ManagedSession;
import org.rebioma.server.util.RevalidationFileUtil;
import org.rebioma.server.util.RevalidationMailNotification;
import org.taxonomy.Classification;
import org.taxonomy.ClassificationOracle;
import org.taxonomy.CsvClassificationOracle;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RevalidationServiceImpl extends RemoteServiceServlet implements
		RevalidationService, Revalidation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4384588011807855692L;
	
	private SessionIdService sessionService = DBFactory.getSessionIdService();
	OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
	RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();
	ValidationService validationService = DBFactory.getValidationService();
	OccurrenceCommentsService commentsService = DBFactory.getOccurrentCommentService();
	 UserDb userDb = DBFactory.getUserDb();
	ClassificationOracle taxonomicAuthority;
	protected FileWriter logFileWriter = null;
	
	protected static final String MAIL_SUBJECT_CASE3 = "Revalidation case 3";
	protected static final String MAIL_SUBJECT_CASE2 = "Revalidation case 2";
	protected static final String MAIL_SUBJECT_CASE4 = "Revalidation case 4";
	protected static final String MAIL_BODY_CASE3 = "Lastly, we have have updated the REBIOMA Taxonomy Authority (TA). Some of your data recorded within the REBIOMA data portal are involved in taxonomy classification updates. Some species were erased or changed in the REBIOMA taxonomy authority.";
	protected static final String MAIL_BODY_CASE5 = "Lastly, we have have updated the REBIOMA Taxonomy Authority (TA). Some of your data recorded within the REBIOMA data portal are involved in taxonomy classification updates. Some species were split into several new derived species.";
	protected static final String MAIL_SUBJECT_CASE5 = "Revalidation case 5";
	protected static final String OCCURRENCE_COMMENTS = "Case 5 detected during revalidation";
	protected static final int ADMIN_ID = 220; //User related to the REBIOMA Data portal mail system (rebiomawebportal@gmail.com)
	protected static final Logger log = Logger
			.getLogger(RevalidationServiceImpl.class);

	public RevalidationServiceImpl() {
		try {
			File taxonomy = new File(ValidationServiceImpl.class.getResource(
					"taxonomy.csv").getFile());
			taxonomicAuthority = new CsvClassificationOracle(taxonomy);
		} catch (IOException e) {
			log.info("Unable to create taxonomic authority.");
		}
	}

	@Override
	public List<Occurrence> fetchAllOccurrences() throws Exception {
		Occurrence occ = new Occurrence();
		occ.setValidated(false);
		return occurrenceDb.findByExample(occ);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Occurrence> fetchInvalidOccurrences() throws Exception {
		log.debug("finding all Occurrences invalids");
		List<Occurrence> results = new ArrayList<Occurrence>();
		try {
			Session session = ManagedSession.createNewSessionAndTransaction();

			// Query query = session.createSQLQuery(
			// "select * from occurrence where id not in(select occurrenceid from record_review)")
			// .addEntity(Occurrence.class);
			/*
			 * Query query = session.createSQLQuery(
			 * "select * from Occurrence where validated=0")
			 * .addEntity(Occurrence.class);
			 */
			Query query = session
					.createQuery("from Occurrence o where o.validated=:invalidated ");// AND o.id IN(1767489,1768452,1773931,1782761,1769247,1802900,1802131,1790105,1779527,1774533,1837145,1837401)");
			query.setParameter("invalidated", Boolean.FALSE);
			results = query.list();
			log.debug("all Occurrences invalids successful, result size: "
					+ results.size());
			ManagedSession.commitTransaction(session);
			log.info("Occurrences invalids : " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find all occurrences invalids failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Occurrence> fetchValidOccurrences() throws Exception {
		log.info("finding valids Occurrences ");
		List<Occurrence> results = new ArrayList<Occurrence>();
		try {
			Session session = ManagedSession.createNewSessionAndTransaction();

			/*
			 * Query query = session.createSQLQuery(
			 * "select * from Occurrence where stability is null or stability =0"
			 * ) .addEntity(Occurrence.class);
			 */
			Query query = session
					.createQuery("from Occurrence o where o.validated=:validated and (o.stability is null or o.stability=:unstable)  ");//and o.id IN (1767489,1768452,1773931,1782761,1769247,1802900,1802131,1790105,1779527,1774533,1837145,1837401)");
			query.setParameter("validated", Boolean.TRUE);
			query.setParameter("unstable", Boolean.FALSE);
			
			results = query.list();
			log.debug("all Occurrences invalids successful, result size: "
					+ results.size());
			ManagedSession.commitTransaction(session);
			log.info("valids Occurrences  : " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find valids occurrences  failed", re);
			throw re;
		}
	}

	protected void modifyMap(String key, Map<String, List<Occurrence>> map,
			Occurrence occurrence) {
		if (map.containsKey(key)) {
			map.get(key).add(occurrence);
		} else {
			ArrayList<Occurrence> listeocc = new ArrayList<Occurrence>();
			listeocc.add(occurrence);
			map.put(key, listeocc);
		}
	}

	/**
	 * Updates the {@link Occurrence} validation errors by appending the msg to
	 * it. Also sets the {@link Occurrence} validated flag to false.
	 * 
	 * @param occurrence
	 *            the occurrence to update
	 * @param msg
	 *            the error message
	 */
	protected static void updateValidationErrors(Occurrence occurrence,
			String msg) {
		String errors = occurrence.getValidationError();
		if (errors == null) {
			errors = "";
		}
		errors += msg + "- ";// Error parsing from download
		occurrence.setValidationError(errors);
		occurrence.setValidated(false);
	}

	/**
	 * Validates an {@link Occurrence} taxonomy by using a
	 * {@link ClassificationOracle} to update the following fields:
	 * 
	 * <pre>
	 * NomenclaturalCode 
	 * AcceptedKingdom
	 * AcceptedPhylum
	 * AcceptedClass
	 * AcceptedOrder
	 * AcceptedSuborder
	 * AcceptedFamily
	 * AcceptedSubfamily
	 * AcceptedGenus
	 * AcceptedSubgenus
	 * AcceptedSpecificEpithet
	 * AcceptedSpecies
	 * VerbatimSpecies
	 * </pre>
	 * 
	 * Genus, SpecificEpithet, and NomenclaturalCode properties are required to
	 * classify an {@link Occurrence}.
	 * 
	 * @see http://code.google.com/p/rebioma/wiki/Validation
	 * 
	 * @param occurrence
	 *            the occurrence to validate
	 */
	protected void validateTaxonomy(Occurrence occurrence) {
		try {
			String genus = occurrence.getGenus();
			String se = occurrence.getSpecificEpithet();
			String nc = occurrence.getNomenclaturalCode();
			String ir = occurrence.getInfraspecificRank();
			String ie = occurrence.getInfraspecificEpithet();
			Classification c = taxonomicAuthority.classify(genus, se, ir, ie, nc); /*
																				 * ie
																				 * before
																				 * nc
																				 */

			Session session = ManagedSession.createNewSessionAndTransaction();
			if (c != null) {
				String ok = c.getValidation();
				if (ok.equals("OK")) {
					occurrence.setAcceptedKingdom(c.getKingdom());
					occurrence.setAcceptedPhylum(c.getPhylum());
					occurrence.setAcceptedClass(c.getClazz());/* Fix Issue 296 */
					occurrence.setAcceptedOrder(c.getOrder());
					occurrence.setAcceptedSuborder(c.getSuborder());
					occurrence.setAcceptedFamily(c.getFamily());
					occurrence.setAcceptedSubfamily(c.getSubfamily());
					occurrence.setAcceptedGenus(c.getGenus());
					occurrence.setAcceptedSubgenus(c.getSubgenus());
					occurrence.setAcceptedSpecificEpithet(c
							.getSpecificEpithet());
					occurrence.setAcceptedSpecies(c.getAcceptedSpecies());
					occurrence.setVerbatimSpecies(c.getVerbatimSpecies()); /*
																			 * Fix
																			 * Issue
																			 * 329
																			 */
					occurrence.setStability(true);

				} else if (ok.equals("KO")) {
					String notes;
					notes = c.getNotes();
					updateValidationErrors(occurrence, notes);
				} else {
					updateValidationErrors(occurrence,
							"Taxonomic classification not validated yet");
				}

			} else {
				updateValidationErrors(occurrence,
						"Taxonomic classification unknown");
			}

			session.saveOrUpdate(occurrence);
			ManagedSession.commitTransaction(session);
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}

	}
	
	protected void validateTaxonomy(Session session, Occurrence occurrence) {
		try {
			
			
			String genus = occurrence.getGenus();
			String se = occurrence.getSpecificEpithet();
			String nc = occurrence.getNomenclaturalCode();
			String ir = occurrence.getInfraspecificRank();
			String ie = occurrence.getInfraspecificEpithet();
			Classification c = taxonomicAuthority.classify(genus, se, ir, ie, nc); /*
																				 * ie
																				 * before
																				 * nc
																				 */

			
			if (c != null) {
				String ok = c.getValidation();
				if (ok.equals("OK")) {
					occurrence.setAcceptedKingdom(c.getKingdom());
					occurrence.setAcceptedPhylum(c.getPhylum());
					occurrence.setAcceptedClass(c.getClazz());/* Fix Issue 296 */
					occurrence.setAcceptedOrder(c.getOrder());
					occurrence.setAcceptedSuborder(c.getSuborder());
					occurrence.setAcceptedFamily(c.getFamily());
					occurrence.setAcceptedSubfamily(c.getSubfamily());
					occurrence.setAcceptedGenus(c.getGenus());
					occurrence.setAcceptedSubgenus(c.getSubgenus());
					occurrence.setAcceptedSpecificEpithet(c
							.getSpecificEpithet());
					occurrence.setAcceptedSpecies(c.getAcceptedSpecies());
					occurrence.setVerbatimSpecies(c.getVerbatimSpecies()); /*
																			 * Fix
																			 * Issue
																			 * 329
																			 */
					occurrence.setStability(true);

				} else if (ok.equals("KO")) {
					String notes;
					notes = c.getNotes();
					updateValidationErrors(occurrence, notes);
				} else {
					updateValidationErrors(occurrence,
							"Taxonomic classification not validated yet");
				}

			} else {
				updateValidationErrors(occurrence,
						"Taxonomic classification unknown");
			}

			session.saveOrUpdate(occurrence);
			
		} catch (RuntimeException re) {
			log.error("attach failed", re);			
			throw re;
		}

	}

	@SuppressWarnings("finally")
	
	private RevalidationResult revalidateAll(String sessionId) throws Exception {
		long dateStart = System.currentTimeMillis();
		RevalidationResult result=new RevalidationResult();
		try{
			File logFile = new File(RevalidationFileUtil.generateFileName());
			if(!logFile.exists()){
				if(logFile.createNewFile()){
					log.info("file ["+logFile.getAbsolutePath()+"] created OK");
				}
			}
			logFileWriter = new FileWriter(logFile, false);
			log.info("avy nicr�er fichier");
			if(!logFile.exists()){
				log.info("tsis");
			}else{
				log.info("misy "+logFile.getAbsolutePath());
			}
			if(sessionId != null){
				//l'utilisateur qui a lancé la revalidation
				User user = sessionService.getUserBySessionId(sessionId);
				if(user != null){
					logFileWriter.write("Revalidation ordered by " +user.toString());
				}
			}
			//La date du lacement du processus
			logFileWriter.write("Revalidation begins at " +new Date().toString());
			Map<String, List<Occurrence>> map = new HashMap<String, List<Occurrence>>();

			List<Occurrence> listInvalid = fetchInvalidOccurrences();
			for (Occurrence occurrence : listInvalid) {
				String genus = occurrence.getGenus();
				String se = occurrence.getSpecificEpithet();
				String nc = occurrence.getNomenclaturalCode();
				String ir = occurrence.getInfraspecificRank();
				String ie = occurrence.getInfraspecificEpithet();
				Classification c = taxonomicAuthority.classify(genus, se, ir, ie, nc); /*
																					 * ie
																					 * before
																					 * nc
																					 */
				if (c != null) {
					String ok = c.getValidation();
					if (ok.equals("OK")) {
						//System.out.println("----------------> Cas 2 itoo "+occurrence.getGenus()+" Verbatime  "+c.getVerbatimSpecies());
						// case 2
						modifyMap("2", map, occurrence);
					}
				} else {
					// case 1
					modifyMap("1", map, occurrence);
				}
			}

			List<Occurrence> listValid = fetchValidOccurrences();
			for (Occurrence occurrence : listValid) {
				String genus = occurrence.getGenus();
				String se = occurrence.getSpecificEpithet();
				String nc = occurrence.getNomenclaturalCode();
				String ir = occurrence.getInfraspecificRank();
				String ie = occurrence.getInfraspecificEpithet();
				Classification c = taxonomicAuthority.classify(genus, se, ir, ie, nc); /*
																					 * ie
																					 * before
																					 * nc
																					 */
				if (c != null) {
					String ok = c.getValidation();
					if (ok.equals("OK")) {
						if (c.getStability().equals("0")) {
							// case 5
							modifyMap("5", map, occurrence);
						} else {
							// case 4
							modifyMap("4", map, occurrence);
						}
					}
					if (ok.equals("KO")) {
						// case 3
						modifyMap("3", map, occurrence);
					}
				} else {
					// case 3
					modifyMap("3", map, occurrence);
				}
			}
			/*
			for (Map.Entry<String, List<Occurrence>> entry : map.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue().size());
				for (Occurrence o : entry.getValue()) {
					System.out.println("    ****** " + o.getId());
				}
			}
			*/
			
			List<Occurrence> occurrencies = map.get("2");
			System.out.println("----------------> Cas 2 ---------------------->"+occurrencies.size());
			revalidateAction(result, map);
			long dateEnd = System.currentTimeMillis();
			long duration = (dateEnd - dateStart)/1000;
			logFileWriter.write("\n Duration of operation : "+duration+"seconds");
		}finally{			
			if(logFileWriter != null){
				//logFileWriter.flush();
				logFileWriter.close();
			}
			return result;
		}
	}
	
	public List<Integer> getIndiceSubList(int size,int max){		
		 List<Integer> ret = new ArrayList<Integer>();
		 if(size<=max) {
			 ret.add(0);
			 max=size;
		 }else {
			 int taille=size;
			 int mod= taille/max;
			 int i=0;
			 for(i=0;i<mod;i++) {
				 ret.add(i*max);				 
			 }
			 ret.add(i*max);
		 }
		 return ret;
	}
	
	private Set<Occurrence> getSubList(Set<Occurrence> sets, int i,int j){
		Set<Occurrence> ret = new HashSet<Occurrence>();
		Occurrence[] tabs = new Occurrence[sets.size()];
		sets.toArray(tabs);
		
		for(int idx=i;idx<j;idx++) {
			ret.add(tabs[idx]);
		}
		
		return ret;
	}
	
	private String getLogTemplate(String title, Occurrence o){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		if(o != null){
			sb.append(title).append(": ");
			sb.append(o.getAcceptedSpecies());
			sb.append(" ");
			sb.append(o.getAcceptedSpecificEpithet())
			.append(", id: ").append(o.getId())
			.append(", owner: ").append(o.getOwner()).append(",").append(o.getOwnerEmail());
		}
		return sb.toString();
	}
	
	private RevalidationResult treatCase2(RevalidationResult result, Set<Occurrence> occurrences) throws RevalidationException{
			
		/**
		 * OCCURENCE MATCHES NEW NAME: => Same as upload: do the
		 * validation attach occurrences to RecordReview
		 */

		if (!occurrences.isEmpty()) {
			occurrenceDb.refresh();
			log.info(" Occurrences 2 : "
					+ occurrences.size() + " ............");
			try {
				logFileWriter.write("\nNumber of Occurrences case 2 :"+occurrences.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
			int max= RevalidationFileUtil.getBatch() ; int size=occurrences.size();
			List<Integer> ret =getIndiceSubList(size, max);
			int indice=1;
			int nbtotal=0;
			 for(Integer j:ret) {
				
				 int idx=max*indice;
				 if(size<idx)
					 idx=size;
					 
				 
				// occurrences.subList(j, idx );
				System.out.println("j:"+j+" idx:"+idx);
				Set<Occurrence> subOccurrences=getSubList(occurrences, j, idx);
				 
				validationService.validate(subOccurrences);
				Session session=null;
				Transaction tx=null;
				try {
					session = ManagedSession.createNewSessionAndTransaction();
					//tx=session.beginTransaction();				    
					
					for (Occurrence o : subOccurrences) {						
						occurrenceDb.resetRecordReview(o, true,session);
						String uneLigne = getLogTemplate(MAIL_SUBJECT_CASE2, o);
						logFileWriter.write(uneLigne);
					}	
					ManagedSession.commitTransaction(session);
					//tx.commit();
					nbtotal+=subOccurrences.size();
		  	    }catch (IOException e) {
					e.printStackTrace();
				} 
				catch (RuntimeException re) {	
					RevalidationException exc = new RevalidationException();
					result.setErrorMessage(re.getMessage());
					result.getResultMap().put(2, nbtotal);
					exc.setResult(result);
		  	      	if(session!=null)ManagedSession.rollbackTransaction(session);
					//if(tx!=null)tx.rollback();
		  	      throw exc;
		  	    } 
				finally {					 
				     //if(session!=null) session.close();
				 }
		  	    
				indice++;
			 }
			result.getResultMap().put(2, nbtotal);
		}

		log.info(" fin 2 : ............");
		
		return result;
	}
	
	private RevalidationResult treatCase3(RevalidationResult result, Set<Occurrence> occurrences) throws RevalidationException {
		
		
		if (!occurrences.isEmpty()) {
			occurrenceDb.refresh();
			int max= RevalidationFileUtil.getBatch(); int size=occurrences.size();
			try {
				logFileWriter.write("\nNumber of Occurrences case 3 :"+occurrences.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Integer> ret =getIndiceSubList(size, max);
			int indice=1;int nbtotal=0;
	  	    Map<Integer, Set<Integer>> ownerOccurrencesForCase3 = new HashMap<Integer, Set<Integer>>();
	  	    Map<Integer, RevalidationMailNotification> mailNotificationUserMap = new HashMap<Integer, RevalidationMailNotification>();
			for(Integer j:ret) {
				 
				 int idx=max*indice;
				 if(size<idx)
					 idx=size;
				//System.out.println(j+" "+idx);
				Set<Occurrence> subOccurrences=getSubList(occurrences, j, idx);
				
				Session session=null;
				try {
					session = ManagedSession.createNewSessionAndTransaction();
					
					validationService.validate(subOccurrences);
					for (Occurrence o : subOccurrences) {
						String uneLigne = getLogTemplate(MAIL_SUBJECT_CASE3, o);
						logFileWriter.write(uneLigne);
						List<RecordReview> recordReviews = recordReviewDb
								.getRecordReviewsByOcc(session,o.getId());
						for (RecordReview recordReview : recordReviews) {
							recordReviewDb.delete(session,recordReview);
						}
						
						/*
						 * Tantely:J'ai ajout� ces deux lignes codes pour
						 * r�initialiser l'occurrence et puis le revalider
						 */
						occurrenceDb.resetRecordReview(o, false,session);
						validateTaxonomy(session, o);
						
						int ownerId = o.getOwner();
			         
						
						Set<Integer> occurrencesCase3 = ownerOccurrencesForCase3
				            .get(ownerId);
						if (occurrencesCase3 == null) {
						   occurrencesCase3 = new HashSet<Integer>();
						   ownerOccurrencesForCase3.put(ownerId, occurrencesCase3);
						}
						occurrencesCase3.add(o.getId());			
					}
					
					
					if (!ownerOccurrencesForCase3.isEmpty()) {
					      for (Integer userId : ownerOccurrencesForCase3.keySet()) {
						    if(mailNotificationUserMap.containsKey(userId) && mailNotificationUserMap.get(userId) != null){
						    	Set<Integer> occurrenceIds = mailNotificationUserMap.get(userId).getOccurrenceIds();
						    	if(occurrenceIds == null){
						    		occurrenceIds = new HashSet<Integer>();
						    	}
						    	occurrenceIds.addAll(ownerOccurrencesForCase3.get(userId));
						    }else{
						    	User owner = userDb.findById(session,userId);
						    	RevalidationMailNotification mailNotifier = new RevalidationMailNotification(owner, ownerOccurrencesForCase3.get(userId), MAIL_SUBJECT_CASE3, MAIL_BODY_CASE3);
						    	mailNotificationUserMap.put(userId, mailNotifier);
						    }
					      }
					}
					
					ManagedSession.commitTransaction(session);	
					nbtotal+=subOccurrences.size();
		  	    }catch (IOException e) {
					e.printStackTrace();
				} 
				catch (RuntimeException re) {	
					RevalidationException exc = new RevalidationException();
					result.setErrorMessage(re.getMessage());
					result.getResultMap().put(3, nbtotal);
					exc.setResult(result);
		  	      	ManagedSession.rollbackTransaction(session);
		  	      throw exc;
		  	    } 
				finally {					 
				     if(session!=null) session.close();
				 }
		  	    
				indice++;
			 }
			//on envoie les mail
			try{
			    log.info("sending email notification of revalidation case 3");
				notifyOwners(mailNotificationUserMap);
			}catch(IOException e){
				e.printStackTrace();
			}
			result.getResultMap().put(3, nbtotal);
		}
		return  result;
	}
	
	private void notifyOwners(Map<Integer, RevalidationMailNotification> mailNotificationUserMap) throws IOException{
		for(Map.Entry<Integer, RevalidationMailNotification> entry: mailNotificationUserMap.entrySet()){
			RevalidationMailNotification mailNotification = entry.getValue();
			User destinataire = mailNotification.getDestinataire();
			Set<Integer> occurenceIds = mailNotification.getOccurrenceIds();
			String sujet = mailNotification.getSubject();
			String body = mailNotification.getBody();
		      log.info("sending email with subject: " + sujet + " to "+ destinataire.getEmail());
			try {
				logFileWriter.write("\n ----------------------------------------------------------");
				logFileWriter.write("\n D�but d'envoie de mail pour: " + destinataire.getFirstName() );
				logFileWriter.write("\n Adresse :" + destinataire.getEmail());
				logFileWriter.write("\n Sujet: "+sujet);
				logFileWriter.write("\n Nombre d'occurences: "+ occurenceIds.size());
				EmailUtil.notifyUserForRevalidation(destinataire,  occurenceIds, sujet, body);
				logFileWriter.write("\n Mail envoy� avec succ�s � " + destinataire.getEmail());
				logFileWriter.write("\n ----------------------------------------------------------");
			} catch (EmailException e) {
				log.error(
	              "unable to send email reviewed changes notification email to user "
	                  + destinataire.getEmail(), e);
	        	e.printStackTrace();
				logFileWriter.write("\n Oups !!!! erreur d'envoie de mail pour :" + destinataire.getEmail());
				logFileWriter.write("\n ----------------------------------------------------------");
	        }
		}
	}
	
	private RevalidationResult treatCase4(RevalidationResult result, Set<Occurrence> occurrences) throws RevalidationException{
		
		/**
		 * OCCURENCE MATCHES NEW NAME: => Same as upload: do the
		 * validation attach occurrences to RecordReview
		 */
		if (!occurrences.isEmpty()) {
			log.info(" Occurrences 4 : "
					+ occurrences.size() + " ............");
			int max= RevalidationFileUtil.getBatch(); int size=occurrences.size();
			try {
				logFileWriter.write("\nNumber of Occurrences case 4 :"+occurrences.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Integer> ret =getIndiceSubList(size, max);
			int indice=1;int nbtotal=0;
			 for(Integer j:ret) {
				 
				 int idx=max*indice;
				 if(size<idx)
					 idx=size;
					 
				 
				// occurrences.subList(j, idx );
				System.out.println(j+" "+idx);
				Set<Occurrence> subOccurrences=getSubList(occurrences, j, idx);
				 
				
				Session session=null;
				try {
					session = ManagedSession.createNewSessionAndTransaction();
				    
				    for (Occurrence o : subOccurrences) {
						validateTaxonomy(session,o);
						String uneLigne = getLogTemplate(MAIL_SUBJECT_CASE4, o);
						logFileWriter.write(uneLigne);
						// o.setStability(true);

					}
					
				    ManagedSession.commitTransaction(session);
				    nbtotal+=subOccurrences.size();
		  	    }catch (IOException e) {
					e.printStackTrace();
				} 
				catch (RuntimeException re) {	
					RevalidationException exc = new RevalidationException();
					result.setErrorMessage(re.getMessage());
					result.getResultMap().put(4, nbtotal);
					exc.setResult(result);
					ManagedSession.commitTransaction(session);
		  	      throw exc;
		  	    } 
				finally {					 
				     if(session!=null) session.close();
				 }
		  	    
				indice++;
			 }
			 result.getResultMap().put(4, nbtotal);
		}

		log.info(" fin 4 : ............");
		
		return result;
	}
	
	private RevalidationResult treatCase5(RevalidationResult result, Set<Occurrence> occurrences) throws RevalidationException{
		
		/**
		 * OCCURENCE MATCHES NEW NAME: => Same as upload: do the
		 * validation attach occurrences to RecordReview
		 */
		if (!occurrences.isEmpty()) {
			log.info(" Occurrences 5 : "
					+ occurrences.size() + " ............");
			int max= RevalidationFileUtil.getBatch(); int size=occurrences.size();
			
			writeLog("\nNumber of Occurrences case 5 :"+occurrences.size());			
			Map<Integer, RevalidationMailNotification> mailNotificationUserMap = new HashMap<Integer, RevalidationMailNotification>();
			List<Integer> ret =getIndiceSubList(size, max);
			int indice=1;int nbtotal=0;
			 for(Integer j:ret) {
				 
				 int idx=max*indice;
				 if(size<idx)
					 idx=size;
					 
				 
				// occurrences.subList(j, idx );
				System.out.println(j+" "+idx);
				Set<Occurrence> subOccurrences=getSubList(occurrences, j, idx);
				 
				Session session=null;
				try {
					session = ManagedSession.createNewSessionAndTransaction();
				    
				    
				    Map<Integer, Set<Integer>> ownerOccurrencesForCase5 = new HashMap<Integer, Set<Integer>>();
					Set<OccurrenceComments> allComments = new HashSet<OccurrenceComments>();
					Date date = new Date();
					for (Occurrence o : subOccurrences) {
						String uneLigne = getLogTemplate(MAIL_SUBJECT_CASE5, o);
						logFileWriter.write(uneLigne);
						//pour avoir la note, on classify l'occurence
						String genus = o.getGenus();
						String se = o.getSpecificEpithet();
						String nc = o.getNomenclaturalCode();
						String ir = o.getInfraspecificRank();
						String ie = o.getInfraspecificEpithet();
						Classification c = taxonomicAuthority.classify(genus, se, ir, ie, nc);
						String oComment = c.getNotes();
						OccurrenceComments comment = new OccurrenceComments(o.getId(),ADMIN_ID, oComment);
						comment.setDateCommented(date);
						allComments.add(comment);
						List<RecordReview> recordReviews = recordReviewDb
								.getRecordReviewsByOcc(session,o.getId());
						for (RecordReview recordReview : recordReviews) {
							recordReviewDb.delete(session,recordReview);
						}

						occurrenceDb.resetRecordReview(o, false,session);
						occurrenceDb.updateStability(o, Boolean.TRUE,session);
						
						int ownerId = o.getOwner();
			         
						
						Set<Integer> occurrencesCase5 = ownerOccurrencesForCase5
				            .get(ownerId);
						if (occurrencesCase5 == null) {
						   occurrencesCase5 = new HashSet<Integer>();
						   ownerOccurrencesForCase5.put(ownerId, occurrencesCase5);
						}
						occurrencesCase5.add(o.getId());
						
					}
					commentsService.attachDirty(session,allComments);
					if (!ownerOccurrencesForCase5.isEmpty()) {
					      for (Integer userId : ownerOccurrencesForCase5.keySet()) {
						    if(mailNotificationUserMap.containsKey(userId) && mailNotificationUserMap.get(userId) != null){
						    	Set<Integer> occurrenceIds = mailNotificationUserMap.get(userId).getOccurrenceIds();
						    	if(occurrenceIds == null){
						    		occurrenceIds = new HashSet<Integer>();
						    	}
						    	occurrenceIds.addAll(ownerOccurrencesForCase5.get(userId));
						    }else{
						    	User owner = userDb.findById(session,userId);
						    	RevalidationMailNotification mailNotifier = new RevalidationMailNotification(owner, ownerOccurrencesForCase5.get(userId), MAIL_SUBJECT_CASE5, MAIL_BODY_CASE5);
						    	mailNotificationUserMap.put(userId, mailNotifier);
						    }
					      }
					}
					ManagedSession.commitTransaction(session);
					nbtotal+=subOccurrences.size();
		  	    }catch (IOException e) {
					e.printStackTrace();
				} 
				catch (RuntimeException re) {		
					RevalidationException exc = new RevalidationException();
					result.setErrorMessage(re.getMessage());
					result.getResultMap().put(5, nbtotal);
					exc.setResult(result);
		  	      	if(session!=null)ManagedSession.rollbackTransaction(session);
		  	      throw exc;
		  	    } 
				finally {					 
				     //if(session!=null) session.close();
				 }
		  	    
				indice++;
			 }
			//on envoie les mail
			try{
			    log.info("sending email notification of revalidation case 5");
				notifyOwners(mailNotificationUserMap);
			}catch(IOException e){
				e.printStackTrace();
			}
			 result.getResultMap().put(5, nbtotal);
		}

		log.info(" fin 5 : ............");
		
		return result;
	}

	private void writeLog(String str) {
		try {
			logFileWriter.write(str);
		} catch (IOException e) {			
			e.printStackTrace();
		}	
	}
	protected void revalidateAction(RevalidationResult result, Map<String, List<Occurrence>> mapOccurences) throws RevalidationException {
		
			List<String> keys = new ArrayList<String>();
			keys.add("1");
			keys.add("2");
			keys.add("3");
			keys.add("4");
			keys.add("5");
			
//			for (String key : mapOccurences.keySet()) {
//				System.out.println(" ------------Key -------------------> "+key);
//				keys.add(key);				
//			}
			
			List<Occurrence> occurrencies;
			Set<Occurrence> occurrences;
			for (String key : keys) {
				 occurrencies = mapOccurences.get(key);
				 occurrences= new HashSet<Occurrence>(occurrencies);
				 
				System.out.println(" ------------Key -------------------> "+key+" ------------ size =>"+occurrencies.size());
				//int keyInt = Integer.parseInt(key);
				
				if(key.equals("2")){
					System.out.println(" Occurrences 2 : "+ occurrences.size() + " ............");
					log.info(" Occurrences 2 : "+ occurrences.size() + " ............");
					treatCase2(result,occurrences);
				}else if(key.equals("3")){
					/**
					 * For 1:MANY-or 1:0 synonomy: => 1.delete all recordreviews
					 * for each occurence 2.send mail to owner to request more
					 * information 3.set the status of occurence as invalid <=>
					 * it is the same as doing the validation , we are sure that
					 * theses occurrences will be invalid
					 */
					log.info(" Occurrences 3 : " + occurrences.size()
							+ " ............");
					
					treatCase3(result,occurrences);
					log.info(" fin 3 : ............");
				}else if(key.equals("4")){
					/**
					 * For 1:1 or Many:1 Synonomy: => Change the taxonomy in the
					 * occurence table , no other change
					 */
					log.info(" Occurrences 4 : " + occurrences.size()
							+ " ............");
					/*
					for (Occurrence o : occurrences) {
						validateTaxonomy(o);
						// o.setStability(true);

					}
					*/
					treatCase4(result,occurrences);
					log.info(" fin 4 : ............");
					
				}else if(key.equals("5")){
					log.info(" Occurrences 5 : " + occurrences.size()
							+ " ............");
					treatCase5(result,occurrences);					
					log.info(" fin 5 : ............");
				}
				
				
				/*switch (keyInt) {
				case 2:
					//System.out.println(" Occurrences 2 : "+ occurrences.size() + " ............");
					log.info(" Occurrences 2 : "+ occurrences.size() + " ............");
					treatCase2(result,occurrences);
					break;
				case 3:
					
					
					*//**
					 * For 1:MANY-or 1:0 synonomy: => 1.delete all recordreviews
					 * for each occurence 2.send mail to owner to request more
					 * information 3.set the status of occurence as invalid <=>
					 * it is the same as doing the validation , we are sure that
					 * theses occurrences will be invalid
					 *//*
					log.info(" Occurrences 3 : " + occurrences.size()
							+ " ............");
					
					treatCase3(result,occurrences);
					log.info(" fin 3 : ............");
					
					
					
					
					break;

				case 4:
					*//**
					 * For 1:1 or Many:1 Synonomy: => Change the taxonomy in the
					 * occurence table , no other change
					 *//*
					log.info(" Occurrences 4 : " + occurrences.size()
							+ " ............");
					
					for (Occurrence o : occurrences) {
						validateTaxonomy(o);
						// o.setStability(true);

					}
					
					treatCase4(result,occurrences);
					log.info(" fin 4 : ............");
					break;

				case 5:
					log.info(" Occurrences 5 : " + occurrences.size()
							+ " ............");
					treatCase5(result,occurrences);					
					log.info(" fin 5 : ............");
					break;

				default:
					break;
				}*/
			}
			log.info("FIN");
			
	}

	@Override
	public RevalidationResult revalidate(String sessionId) throws Exception {
		RevalidationResult result = new RevalidationResult();
		
		try{
			result=revalidateAll(sessionId);
		}catch(RevalidationException ex) {
			result=ex.getResult();
		}		
		/*
		for (Map.Entry<Integer, Integer> entry : result.getResultMap().entrySet())
		{
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		System.out.println(result.getErrorMessage());
		*/
		return result;
	}

	@Override
	public void cancelRevalidation(String sessionId) throws Exception {
		throw new RuntimeException("Revalidation canceled");
	}

}
