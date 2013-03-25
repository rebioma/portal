/**
 * 
 */
package org.rebioma.server.util;

import java.util.Set;

import org.rebioma.client.bean.User;

/**
 * Une classe qui regroupe les occurences à envoyer vers le propriétaire pour un cas donnée (cas 3 ou cas 5)
 * @author Mikajy
 *
 */
public class RevalidationMailNotification {
	private User destinataire;
	private Set<Integer> occurrenceIds;
	private String subject;
	private String body;
	public RevalidationMailNotification(){
		
	}
	public RevalidationMailNotification(User destinataire,
			Set<Integer> occurrenceIds, String subject, String body) {
		super();
		this.destinataire = destinataire;
		this.occurrenceIds = occurrenceIds;
		this.subject = subject;
		this.body = body;
	}
	
	public User getDestinataire() {
		return destinataire;
	}
	public void setDestinataire(User destinataire) {
		this.destinataire = destinataire;
	}
	public Set<Integer> getOccurrenceIds() {
		return occurrenceIds;
	}
	public void setOccurrenceIds(Set<Integer> occurrenceIds) {
		this.occurrenceIds = occurrenceIds;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
