package org.rebioma.client.bean;

import java.io.Serializable;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class SpeciesTreeModelInfoItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1390731659682210853L;

	public interface SpeciesTreeModelInfosProperties extends
			PropertyAccess<SpeciesTreeModelInfoItem> {

		@Path("label")
		ModelKeyProvider<SpeciesTreeModelInfoItem> key();

		ValueProvider<SpeciesTreeModelInfoItem, String> label();

		ValueProvider<SpeciesTreeModelInfoItem, String> value();
	}
	
	public SpeciesTreeModelInfoItem() {
		super();
	}

	public SpeciesTreeModelInfoItem(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}

	private String label;
	
	private String value;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
