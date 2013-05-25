package org.rebioma.client.bean;

import java.io.Serializable;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class SpeciesStatisticModel implements Serializable {

	public interface SpeciesStatisticModelProperties extends
			PropertyAccess<SpeciesStatisticModel> {

		@Path("label")
		ModelKeyProvider<SpeciesStatisticModel> key();

		ValueProvider<SpeciesStatisticModel, String> kindOfData();

		ValueProvider<SpeciesStatisticModel, Integer> nbRecords();

		//ValueProvider<SpeciesStatisticModel, String> observations();
	}

	private static final long serialVersionUID = 3998904104630596963L;

	private String kindOfData;

	private int nbRecords;

	private String observations;


	public String getKindOfData() {
		return kindOfData;
	}

	public void setKindOfData(String kindOfData) {
		this.kindOfData = kindOfData;
	}

	public int getNbRecords() {
		return nbRecords;
	}

	public void setNbRecords(int nbRecords) {
		this.nbRecords = nbRecords;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

}
