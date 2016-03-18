/**
 * 
 */
package org.rebioma.client.gxt.forms;

import java.util.List;

import org.rebioma.client.bean.SearchFieldNameValuePair;
import org.rebioma.client.services.OccurrenceSearchServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.ListStoreBinding;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * @author Mikajy
 * 
 */
public class GlobalSearchListViewWidget {
	private SimpleContainer contentPanel = null;
	private OccurrenceSearchServiceAsync occurrenceSearchService = org.rebioma.client.services.OccurrenceSearchService.Proxy
			.get();

	interface Renderer extends XTemplates {
		@XTemplate("<div style=\"font-weight: bold;\">{data.fieldName}</div><div>{data.fieldValue}</div>")
		public SafeHtml render(SearchFieldNameValuePair data);

		// @XTemplate(source = "ListViewExample.html")
		// public SafeHtml renderItem(SearchFieldNameValuePair nvp, Style
		// style);
	}

	Renderer renderer = GWT.create(Renderer.class);

	public Widget asWidget() {
		if (contentPanel == null) {
			RpcProxy<String, List<SearchFieldNameValuePair>> proxy = new RpcProxy<String, List<SearchFieldNameValuePair>>() {
				@Override
				public void load(String loadConfig,
						AsyncCallback<List<SearchFieldNameValuePair>> callback) {
					occurrenceSearchService.getSearchFieldNameValuePair(
							loadConfig, callback);
				}
			};
			Loader<String, List<SearchFieldNameValuePair>> loader = new Loader<String, List<SearchFieldNameValuePair>>(
					proxy);
			ModelKeyProvider<SearchFieldNameValuePair> kp = new ModelKeyProvider<SearchFieldNameValuePair>() {
				@Override
				public String getKey(SearchFieldNameValuePair item) {
					return item.getFieldName();
				}
			};
			ListStore<SearchFieldNameValuePair> store = new ListStore<SearchFieldNameValuePair>(
					kp);
			loader.addLoadHandler(new ListStoreBinding<String, SearchFieldNameValuePair, List<SearchFieldNameValuePair>>(
					store));
			loader.load();
			
			ListView<SearchFieldNameValuePair, SearchFieldNameValuePair> listView = new ListView<SearchFieldNameValuePair, SearchFieldNameValuePair>(store, new IdentityValueProvider<SearchFieldNameValuePair>());
			listView.setCell(new SimpleSafeHtmlCell<SearchFieldNameValuePair>(new AbstractSafeHtmlRenderer<SearchFieldNameValuePair>() {
				@Override
				public SafeHtml render(SearchFieldNameValuePair nvp) {
					return renderer.render(nvp);
				}
			}));
			listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<SearchFieldNameValuePair>() {

				@Override
				public void onSelectionChanged(
						SelectionChangedEvent<SearchFieldNameValuePair> event) {
					Window.alert(event.getSource().getSelectedItem().getFieldName());
				}
			});
			contentPanel = new SimpleContainer();
		    final VerticalLayoutContainer rowLayoutContainer = new VerticalLayoutContainer();
		    contentPanel.add(rowLayoutContainer);
		    contentPanel.add(listView);
		}
		return contentPanel;
	}
}
