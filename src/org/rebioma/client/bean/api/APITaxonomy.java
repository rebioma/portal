/**
 * 
 */
package org.rebioma.client.bean.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.rebioma.client.bean.Taxonomy;

/**
 * @author Mika
 *
 */
@XmlRootElement(name="taxonomy")
@XmlAccessorType(XmlAccessType.FIELD)
public class APITaxonomy extends Taxonomy {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7294382775193543009L;
	
	private int nombrePrivateOccurrences;
	private int nombrePublicOccurrences;
	
	public int getNombrePrivateOccurrences() {
		return nombrePrivateOccurrences;
	}
	public void setNombrePrivateOccurrences(int nombrePrivateOccurrences) {
		this.nombrePrivateOccurrences = nombrePrivateOccurrences;
	}
	public int getNombrePublicOccurrences() {
		return nombrePublicOccurrences;
	}
	public void setNombrePublicOccurrences(int nombrePublicOccurrences) {
		this.nombrePublicOccurrences = nombrePublicOccurrences;
	}
	
}
