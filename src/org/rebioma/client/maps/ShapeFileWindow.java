package org.rebioma.client.maps;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.bean.ShapeFileInfo;
import org.rebioma.client.services.MapGisService;
import org.rebioma.client.services.MapGisServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

public class ShapeFileWindow extends Window implements SelectHandler {

	class KeyProvider implements ModelKeyProvider<ShapeFileInfo> {
		@Override
		public String getKey(ShapeFileInfo item) {
			String key = item.getGid() + item.getLibelle();
			return key;
		}
	}
	
	private Tree<ShapeFileInfo, String> tree;
	
	private List<ShapeSelectionHandler> handlers = new ArrayList<ShapeSelectionHandler>();
	
	public void addTreeSelectHandler(ShapeSelectionHandler handler){
		this.handlers.add(handler);
	}

	public ShapeFileWindow() {
		super();
		this.init();
	}

	private void init() {
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		VerticalLayoutContainer p = new VerticalLayoutContainer();
	    p.setBorders(true);
	    p.getElement().getStyle().setBackgroundColor("white");
	    p.getElement().getStyle().setHeight(280, Unit.PX);
	    p.getElement().getStyle().setOverflow(Overflow.AUTO);
	    panel.add(p);
	    
		this.setHeadingText("Liste des fichiers shapes");
		//panel.setPixelSize(315, 400);
		panel.addStyleName("margin-10");
		panel.setHeight(300);
		
		final MapGisServiceAsync mapGisService = GWT.create(MapGisService.class);
		RpcProxy<ShapeFileInfo, List<ShapeFileInfo>> proxy = new RpcProxy<ShapeFileInfo, List<ShapeFileInfo>>() {
			@Override
			public void load(ShapeFileInfo loadConfig,
					AsyncCallback<List<ShapeFileInfo>> callback) {
				mapGisService.getShapeFileItems(loadConfig, callback);
			}
		};
		
		TreeLoader<ShapeFileInfo> loader = new TreeLoader<ShapeFileInfo>(proxy) {
			@Override
			public boolean hasChildren(ShapeFileInfo parent) {
				return parent.getGid() == 0;
			}
		};

		TreeStore<ShapeFileInfo> store = new TreeStore<ShapeFileInfo>(
				new KeyProvider());
		loader.addLoadHandler(new ChildTreeStoreBinding<ShapeFileInfo>(store));
	    tree = new Tree<ShapeFileInfo, String>(store, new ValueProvider<ShapeFileInfo, String>() {
		    	@Override
				public String getValue(ShapeFileInfo object) {
					return object.getLibelle();
				}
	
				@Override
				public void setValue(ShapeFileInfo object, String value) {
				}
	
				@Override
				public String getPath() {
					return "libelle";
				}
	      });
	     tree.setLoader(loader);
	      tree.setWidth(300);
	      //tree.getStyle().setLeafIcon(ExampleImages.INSTANCE.music());
	      tree.setCheckable(true);
	      tree.setCheckStyle(CheckCascade.TRI);
	      tree.setAutoLoad(true);
	      /*tree.addCheckChangedHandler(new CheckChangedHandler<ShapeFileInfo>() {
	          @Override
	          public void onCheckChanged(CheckChangedEvent<ShapeFileInfo> event) {
	            
	          }
	      });*/
	      TextButton textBtn = new TextButton("Show Layer");
	      textBtn.addSelectHandler(this);
	      this.addButton(textBtn);
	      p.add(tree);
	      this.add(panel);
	      this.setButtonAlign(BoxLayoutPack.END);
	}

	@Override
	public void onSelect(SelectEvent event) {
		List<ShapeFileInfo> checkedSelection = tree.getCheckedSelection();
		for(ShapeSelectionHandler handler: handlers){
			handler.onShapeSelect(checkedSelection);
		}
		
	}
}
