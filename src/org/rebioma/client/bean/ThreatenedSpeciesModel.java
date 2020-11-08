package org.rebioma.client.bean;

// Generated 26 ao�t 2012 20:34:47 by Hibernate Tools 3.3.0.GA


@SuppressWarnings("serial")
public class ThreatenedSpeciesModel implements java.io.Serializable {

	private String id;
	private String acceptedSpecies;
	private String kingdom;
	private String phylum;
	private String class_;
	private String order;
	private String family;
	private String genus;
	private String specificEpithet;
	
	
	public ThreatenedSpeciesModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ThreatenedSpeciesModel(String statuIucn, String acceptedSpecies,
			String kingdom, String phylum, String class_, String order,
			String family, String genus, String specificEpithet) {
		super();
		this.id = statuIucn;
		this.acceptedSpecies = acceptedSpecies;
		this.kingdom = kingdom;
		this.phylum = phylum;
		this.class_ = class_;
		this.order = order;
		this.family = family;
		this.genus = genus;
		this.specificEpithet = specificEpithet;
	}


	public String getAcceptedSpecies() {
		return acceptedSpecies;
	}


	public void setAcceptedSpecies(String acceptedSpecies) {
		this.acceptedSpecies = acceptedSpecies;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	public String getClass_() {
		return class_;
	}
	public void setClass_(String class_) {
		this.class_ = class_;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
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
	public String getSpecificEpithet() {
		return specificEpithet;
	}
	public void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet;
	}

	
}
