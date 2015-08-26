/**
 * 
 */
package org.rebioma.client.bean.api;

import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author Mika
 *
 */
public class GsonXmlTransientExlusionStrategy implements ExclusionStrategy {

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
	 */
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		 return clazz.getAnnotation(XmlTransient.class) != null;
	}

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipField(com.google.gson.FieldAttributes)
	 */
	@Override
	public boolean shouldSkipField(FieldAttributes field) {
		return field.getAnnotation(XmlTransient.class) != null;
	}

}
