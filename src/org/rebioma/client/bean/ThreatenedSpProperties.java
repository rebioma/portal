package org.rebioma.client.bean;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ThreatenedSpProperties extends PropertyAccess<ThreatenedSpeciesModel> {
	
	@Path("id")
	ModelKeyProvider<ThreatenedSpeciesModel> id();

	ValueProvider<ThreatenedSpeciesModel, String> acceptedSpecies();

	ValueProvider<ThreatenedSpeciesModel, String> statuIucn();

	ValueProvider<ThreatenedSpeciesModel, String> kingdom();

	ValueProvider<ThreatenedSpeciesModel, String> phylum();

	ValueProvider<ThreatenedSpeciesModel, String> class_();

	ValueProvider<ThreatenedSpeciesModel, String> order();

	ValueProvider<ThreatenedSpeciesModel, String> family();

	ValueProvider<ThreatenedSpeciesModel, String> genus();

	ValueProvider<ThreatenedSpeciesModel, String> specificEpithet();

}
