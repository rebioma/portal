package org.rebioma.client.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Mikajy
 * 
 */
@XmlRootElement(name="taxonomy")
@XmlAccessorType (XmlAccessType.FIELD)
public class SpeciesTreeModel implements Serializable{

	public SpeciesTreeModel() {
		super();
	}
	
	public SpeciesTreeModel(Taxonomy ta, String level) {
		setAcceptedspecies(ta.getAcceptedSpecies());
//		setAuthorityName(ta.get);
		setClass_(ta.getClass_());
		setFamily(ta.getFamily());
		setGenus(ta.getGenus());
//		setInfos(infos);
		setKingdom(ta.getKingdom());
//		setLabel(taxonomyFieldConcerne);
		setLevel(level);
//		setOccurrence(occurrence);
		setOrder(ta.getOrder());
		setPhylum(ta.getPhylum());
		setReviewerName(ta.getReviewedBy());
		if(level.equals(SpeciesTreeModel.KINGDOM)){
			setSource(ta.getKingdomSource());
			setId(level + "_" + ta.getKingdom());
		}
		if(level.equals(SpeciesTreeModel.PHYLUM)){
			setSource(ta.getPhylumSource());
			setId(level + "_" + ta.getPhylum());
		}
		if(level.equals(SpeciesTreeModel.CLASS_)){
			setSource(ta.getClassSource());
			setId(level + "_" + ta.getClass_());
		}
		if(level.equals(SpeciesTreeModel.ORDER)){
			setSource(ta.getOrderSource());
			setId(level + "_" + ta.getOrder());
		}
		if(level.equals(SpeciesTreeModel.GENUS)){
			setSource(ta.getGenusSource());
			setId(level + "_" + ta.getGenus());
		}
		if(level.equals(SpeciesTreeModel.ACCEPTEDSPECIES)){
			setSource("");
			setId(level + "_" + ta.getAcceptedSpecies());
		}
//		setStatus(status);
		setSubclass(ta.getSubclass());
		setSuperfamily(ta.getSuperFamily());
//		setSynonymisedTaxa(ta.getS);
//		setVernecularName(ta.get);
	}

	public interface SpeciesTreeModelProperties extends
			PropertyAccess<SpeciesTreeModel> {

		@Path("id")
		ModelKeyProvider<SpeciesTreeModel> key();

		ValueProvider<SpeciesTreeModel, String> label();

		ValueProvider<SpeciesTreeModel, Integer> nbPrivateOccurence();

		ValueProvider<SpeciesTreeModel, Integer> nbPublicOccurence();

		ValueProvider<SpeciesTreeModel, String> level();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -611163360495508642L;
	public static final String LABEL = "Label";
	public static final String LEVEL = "Level";
	public static final String PRIVATE_OCCURENCE = "PrivateOcc";
	public static final String PUBLIC_OCCURENCE = "PublicOcc";
	public static final String AUTHORITY_NAME = "AuthorityName";
	public static final String STATUS = "Status";
	public static final String SYNONYMISED_TAXA = "SynonymisedTaxa";
	public static final String SOURCE = "Source";
	public static final String VERNECULAR_NAME = "VernecularName";
	public static final String REVIEWER_NAME = "ReviewerName";
	public static final String OCCURENCE = "Occurence";

	public static final String KINGDOM = "Kingdom";
	public static final String PHYLUM = "Phylum";
	public static final String CLASS_ = "Class";

	public static final String SUBCLASS = "Subclass";
	public static final String ORDER = "Order";
	public static final String SUPERFAMILY = "Superfamily";
	public static final String FAMILY = "Family";
	public static final String GENUS = "Genus";
	public static final String ACCEPTEDSPECIES = "Acceptedspecies";
	public static final String SPECIES = "Species";
	private String id;

	private String subclass;
	private String order;
	private String superfamily;
	private String family;
	private String genus;
	private String acceptedspecies;
	private String class_;
	private String kingdom;
	private String phylum;
	private String label;
	private String level;
	private int nbPrivateOccurence;
	private int nbPublicOccurence;
	private int nbTaxon;
	private String authorityName;
	private String status;
	private String synonymisedTaxa;
	private String source;
	private String vernecularName;
	private String reviewerName;
	private Occurrence occurrence;
	private List<SpeciesTreeModelInfoItem> infos;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<SpeciesTreeModelInfoItem> getInfos() {
		return infos;
	}

	public void setInfos(List<SpeciesTreeModelInfoItem> infos) {
		this.infos = infos;
	}
	
	public String getSubclass() {
		return subclass;
	}

	public void setSubclass(String subClass) {
		this.subclass = subClass;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSuperfamily() {
		return superfamily;
	}

	public void setSuperfamily(String superfamily) {
		this.superfamily = superfamily;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getAcceptedspecies() {
		return acceptedspecies;
	}

	public void setAcceptedspecies(String acceptedspecies) {
		this.acceptedspecies = acceptedspecies;
	}

	public String getClass_() {
		return class_;
	}

	public void setClass_(String classe) {
		this.class_ = classe;
	}

	public String getKingdom() {
		return kingdom;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public String getPhylum() {
		return phylum;
	}

	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getNbPrivateOccurence() {
		return nbPrivateOccurence;
	}

	public void setNbPrivateOccurence(int nbPrivateOccurence) {
		this.nbPrivateOccurence = nbPrivateOccurence;
	}

	public int getNbPublicOccurence() {
		return nbPublicOccurence;
	}

	public void setNbPublicOccurence(int nbPublicOccurence) {
		this.nbPublicOccurence = nbPublicOccurence;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSynonymisedTaxa() {
		return synonymisedTaxa;
	}

	public void setSynonymisedTaxa(String synonymisedTaxa) {
		this.synonymisedTaxa = synonymisedTaxa;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVernecularName() {
		return vernecularName;
	}

	public void setVernecularName(String vernecularName) {
		this.vernecularName = vernecularName;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

	public Occurrence getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Occurrence occurrence) {
		this.occurrence = occurrence;
	}

	public String getLevelQueryFilter() {
		// AcceptedKingdom, AcceptedPhylum, AcceptedClass, AcceptedOrder,
		// AcceptedFamily, AcceptedGenus, AcceptedSpecies
		String level = getLevel();
		if ("Kingdom".equalsIgnoreCase(level)) {
			return "AcceptedKingdom";
		} else if ("Phylum".equalsIgnoreCase(level)) {
			return "AcceptedPhylum";
		} else if ("taxon_class".equalsIgnoreCase(level)
				|| "clazz".equalsIgnoreCase(level)) {
			return "AcceptedClass";
		} else if ("Order".equalsIgnoreCase(level)) {
			return "AcceptedOrder";
		} else if ("Family".equalsIgnoreCase(level)) {
			return "AcceptedFamily";
		} else if ("Genus".equalsIgnoreCase(level)) {
			return "AcceptedGenus";
		} else if ("Species".equalsIgnoreCase(level)) {
			return "AcceptedSpecies";
		} else if ("class".equalsIgnoreCase(level)) {
			return "AcceptedClass";
		} else {
			return level;
		}
	}

	public int getNbTaxon() {
		return nbTaxon;
	}

	public void setNbTaxon(int nbTaxon) {
		this.nbTaxon = nbTaxon;
	}
}
