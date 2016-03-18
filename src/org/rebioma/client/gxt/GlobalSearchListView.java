/**
 * 
 */
package org.rebioma.client.gxt;

import org.rebioma.client.gxt.forms.AdvancedComboBoxExample.Forum;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;

/**
 * @author Mikajy
 * 
 */
public class GlobalSearchListView {
	

	public interface DataRenderer extends XTemplates {
		@XTemplate("<p>Field: {data.es_field}</p><p>Value: {data.es_value}</p>")
		public SafeHtml render(Forum data);
	}
	
	DataRenderer renderer = GWT.create(DataRenderer.class);

	public Widget asWidget() {
		
//		final ExampleServiceAsync service = GWT.create(ExampleService.class);
		return null;
	}
}
