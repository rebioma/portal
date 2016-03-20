/**
 * 
 */
package org.rebioma.client.gxt.forms;

import java.util.List;

import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.SearchFieldNameValuePair;
import org.rebioma.client.services.OccurrenceSearchServiceAsync;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.ListStoreBinding;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent.LoadExceptionHandler;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * @author Mikajy
 * 
 */
public class GlobalSearchListViewWindow extends com.sencha.gxt.widget.core.client.Window{
	private OccurrenceSearchServiceAsync occurrenceSearchService = org.rebioma.client.services.OccurrenceSearchService.Proxy
			.get();

	private SelectionChangedHandler<SearchFieldNameValuePair> selectionChangedHandler = null;
//	interface Renderer extends XTemplates {
//		@XTemplate("<div><h3>{data.fieldName}</h3><p>{data.fieldValue}</p><hr/></div>")
//		public SafeHtml render(SearchFieldNameValuePair data);
//
//		// @XTemplate(source = "ListViewExample.html")
//		// public SafeHtml renderItem(SearchFieldNameValuePair nvp, Style
//		// style);
//	}

//	Renderer renderer = GWT.create(Renderer.class);
	
	private int lastSearchHash = Integer.MIN_VALUE;
	
	protected class LoadConfig {
		String sessionid;
		OccurrenceQuery query;
		public LoadConfig(String sid, OccurrenceQuery occQuery){
			sessionid = sid;
			query = occQuery;
		}
	}
	

	public GlobalSearchListViewWindow(SelectionChangedHandler<SearchFieldNameValuePair> selectionChangedHandler) {
		super();
		// TODO Auto-generated constructor stub
		this.selectionChangedHandler = selectionChangedHandler;
	}
	
	public GlobalSearchListViewWindow() {
		super();
		// TODO Auto-generated constructor stub
	}


	public GlobalSearchListViewWindow(WindowAppearance appearance) {
		super(appearance);
		// TODO Auto-generated constructor stub
	}
	
	private int getSearchHash(String sessionId, OccurrenceQuery occurrenceQuery){
		int hash = 0;
		if(sessionId != null || occurrenceQuery != null){
			if(sessionId == null){
				hash = occurrenceQuery.toString().hashCode();
			}else if(occurrenceQuery == null){
				hash = sessionId.hashCode();
			}else{
				hash = (sessionId + "" + occurrenceQuery.toString()).hashCode();
			}
		}
		return hash;
	}
	
	public void showAndLoad(String sessionId, OccurrenceQuery occurrenceQuery){
		this.show();
		int hash = getSearchHash(sessionId, occurrenceQuery);
		if(lastSearchHash != hash){
			lastSearchHash = hash;
			final LoadConfig loadConfig = new LoadConfig(sessionId, occurrenceQuery);
			RpcProxy<LoadConfig, List<SearchFieldNameValuePair>> proxy = new RpcProxy<LoadConfig, List<SearchFieldNameValuePair>>() {
				@Override
				public void load(LoadConfig lc,
						AsyncCallback<List<SearchFieldNameValuePair>> callback) {
					try{
						String sessionId = loadConfig.sessionid;
						OccurrenceQuery occQuery = loadConfig.query;
						occurrenceSearchService.getSearchFieldNameValuePair(
								sessionId,occQuery, callback);
					}catch(Exception e){
						//Que faire ici ?
						throw new RuntimeException(e);
					}
					
				}
			};
			Loader<LoadConfig, List<SearchFieldNameValuePair>> loader = new Loader<LoadConfig, List<SearchFieldNameValuePair>>(
					proxy);
			ModelKeyProvider<SearchFieldNameValuePair> kp = new ModelKeyProvider<SearchFieldNameValuePair>() {
				@Override
				public String getKey(SearchFieldNameValuePair item) {
					return item.getFieldName();
				}
			};
			final XElement elt = this.getElement();
			final com.sencha.gxt.widget.core.client.Window that = this;
			ListStore<SearchFieldNameValuePair> store = new ListStore<SearchFieldNameValuePair>(
					kp);
			loader.addLoadHandler(new ListStoreBinding<LoadConfig, SearchFieldNameValuePair, List<SearchFieldNameValuePair>>(
					store));
			loader.addLoadHandler(new LoadHandler<GlobalSearchListViewWindow.LoadConfig, List<SearchFieldNameValuePair>>(){

				@Override
				public void onLoad(
						LoadEvent<LoadConfig, List<SearchFieldNameValuePair>> event) {
					Mask.unmask(elt);
				}
				
			});
			loader.addBeforeLoadHandler(new BeforeLoadHandler<GlobalSearchListViewWindow.LoadConfig>() {
				@Override
				public void onBeforeLoad(BeforeLoadEvent<LoadConfig> event) {
					Mask.mask(elt, "Loading...");
				}
			});
			loader.addLoadExceptionHandler(new LoadExceptionHandler<GlobalSearchListViewWindow.LoadConfig>() {
				@Override
				public void onLoadException(LoadExceptionEvent<LoadConfig> event) {
					Mask.unmask(elt);
					Window.alert("Le serveur est momentanement indisponible. Merci de réesayer plus tard.");
					that.setVisible(false);
				}
			});
			loader.load();
			
			ListView<SearchFieldNameValuePair, SearchFieldNameValuePair> listView = new ListView<SearchFieldNameValuePair, SearchFieldNameValuePair>(store, new IdentityValueProvider<SearchFieldNameValuePair>());
			listView.setCell(new SimpleSafeHtmlCell<SearchFieldNameValuePair>(new AbstractSafeHtmlRenderer<SearchFieldNameValuePair>() {
				@Override
				public SafeHtml render(SearchFieldNameValuePair nvp) {
					StringBuilder sb = new StringBuilder();
					sb.append("<div class=\"global-result-item\">").append("<h3>").append(nvp.getFieldName()).append("</h3>")
						.append("<ul style=\"margin-left: 10px;\">");
					for(String v: nvp.getFieldValues()){
						sb.append("<li>")
							.append(v)
						.append("</li>");	
					}
					sb.append("</ul>")
					.append("</div>");
					return SafeHtmlUtils.fromTrustedString(sb.toString());
					
//					return SafeHtmlUtils.fromTrustedString("<div style=\"border-bottom:1px black dashed;\"><h3>" + nvp.getFieldName() + "</h3><div>" + nvp.getFieldValue() + "</div></div>");//renderer.render(nvp);
					
				}
			}));
			if(selectionChangedHandler != null) listView.getSelectionModel().addSelectionChangedHandler(selectionChangedHandler);
			SimpleContainer simpleContainer = new SimpleContainer();
		    final VerticalLayoutContainer rowLayoutContainer = new VerticalLayoutContainer();
		    simpleContainer.add(rowLayoutContainer);
		    simpleContainer.add(listView);
		    this.add(simpleContainer);
		}
	}

}
