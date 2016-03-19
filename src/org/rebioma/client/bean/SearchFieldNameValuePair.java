/**
 * 
 */
package org.rebioma.client.bean;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Mikajy
 *
 */
public class SearchFieldNameValuePair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fieldName;
	
	private Set<String> fieldValues;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Set<String> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Set<String> fieldValues) {
		this.fieldValues = fieldValues;
	}


	
}
