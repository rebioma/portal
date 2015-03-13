package org.rebioma.client.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rebioma.client.bean.ShapeFileInfo;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.MapGisService;
import org.rebioma.client.services.MapGisServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreFilter;
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
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
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

	private AppConstants constants;

	public void addTreeSelectHandler(ShapeSelectionHandler handler){
		this.handlers.add(handler);
	}

	public ShapeFileWindow(AppConstants constants) {
		super();
		this.constants = constants;
		this.init();
	}

	private SimpleComboBox<String> combo = new SimpleComboBox<String>(new StringLabelProvider<String>());

	private String selection = "";

	private TextButton searchBtn;

	private TextButton textBtn;

	private TextButton go;

	private StoreFilter<ShapeFileInfo> filters;

	private void init() {
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);

		ToolBar toolBar = new ToolBar();

		combo.setAllowTextSelection(false);
		combo.setEditable(false);
		combo.setEnabled(true);
		combo.setTriggerAction(TriggerAction.ALL);
		toolBar.add(new FieldLabel(combo, " Area admin"));

		go = new TextButton("Search");
		go.addStyleName("text");

		toolBar.add(go);

		final VerticalLayoutContainer p = new VerticalLayoutContainer();
		p.setBorders(false);
		p.getElement().getStyle().setBackgroundColor("white");
		p.getElement().getStyle().setHeight(280, Unit.PX);
		p.getElement().getStyle().setOverflow(Overflow.AUTO);
		panel.add(p);

		//		toolBar.setLayoutData(new VerticalLayoutData(1, -1));
		//	    p.add(toolBar);

		this.getHeader().addStyleName("text");
		this.setHeadingText(constants.shapefileList());
		//panel.setPixelSize(315, 400);
		//		panel.addStyleName("margin-10");
		panel.setHeight(300);

		final MapGisServiceAsync mapGisService = GWT.create(MapGisService.class);

		mapGisService.listAreaAdmin(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable arg0) {

			}

			@Override
			public void onSuccess(List<String> arg0) {
				combo.add("ALL");
				for (String list : arg0) {
					combo.add(list);
				}
			}
		});

		RpcProxy<ShapeFileInfo, List<ShapeFileInfo>> proxy = new RpcProxy<ShapeFileInfo, List<ShapeFileInfo>>() {
			@Override
			public void load(ShapeFileInfo loadConfig,
					AsyncCallback<List<ShapeFileInfo>> callback) {
				mapGisService.getShapeFileItems(loadConfig, callback);
			}
		};

		final TreeLoader<ShapeFileInfo> loader = new TreeLoader<ShapeFileInfo>(proxy) {
			@Override
			public boolean hasChildren(ShapeFileInfo parent) {
				return parent.getGid() == 0;
			}
		};

		loader.setReuseLoadConfig(true);

		final TreeStore<ShapeFileInfo> store = new TreeStore<ShapeFileInfo>(
				new KeyProvider());
		loader.addLoadHandler(new ChildTreeStoreBinding<ShapeFileInfo>(store));

		StoreFilterField<ShapeFileInfo> filter = new StoreFilterField<ShapeFileInfo>() {
			@Override
			protected boolean doSelect(Store<ShapeFileInfo> store, ShapeFileInfo parent, ShapeFileInfo item, String filter) {
				if (item instanceof ShapeFileInfo) {
					return false;
				}

				String name = item.getLibelle();
				name = name.toLowerCase();
				if (name.startsWith(filter.toLowerCase())) {
					return true;
				}
				return false;
			}
		};
		filter.bind(store);

		filters = new StoreFilter<ShapeFileInfo>() {
			@Override
			public boolean select(Store<ShapeFileInfo> store, ShapeFileInfo parent, ShapeFileInfo item) {
				String name = item.getGroup();
				try {
					name = name.toLowerCase().trim();
					if (name.equals(selection.toLowerCase())) {
						return true;
					}
					return false;
				}catch (Exception e) {}
				return false;
			}
		};

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
		tree.getStore().clear();
		tree.addStyleName("text");
		tree.setLoader(loader);
		tree.setWidth(380);
		//		tree.getElement().getStyle().setProperty("text-overflow", "ellipsis");
		//		tree.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		//tree.getStyle().setLeafIcon(ExampleImages.INSTANCE.music());
		tree.setCheckable(true);
		tree.setCheckStyle(CheckCascade.TRI);
		tree.setAutoLoad(true);
		tree.sync(true);
		/*tree.addCheckChangedHandler(new CheckChangedHandler<ShapeFileInfo>() {
	          @Override
	          public void onCheckChanged(CheckChangedEvent<ShapeFileInfo> event) {

	          }
	      });*/

		combo.addSelectionHandler(new SelectionHandler<String>() {

			@Override
			public void onSelection(SelectionEvent<String> arg0) {
				selection = arg0.getSelectedItem();
			}
		});
		go.addSelectHandler(this);

		searchBtn = new TextButton("Show & search");
		searchBtn.addStyleName("text");
		searchBtn.addSelectHandler(this);
		this.addButton(searchBtn);

		textBtn = new TextButton("Show Layer");
		textBtn.addStyleName("text");
		textBtn.addSelectHandler(this);
		this.addButton(textBtn);

		p.add(toolBar, new VerticalLayoutData(1, -1));
		p.add(tree, new VerticalLayoutData(1, 1));
		this.add(panel);
		this.setButtonAlign(BoxLayoutPack.END);
	}

	@Override
	public void onSelect(SelectEvent event) {
		List<ShapeFileInfo> checkedSelection = tree.getCheckedSelection();

		if(event.getSource() == go) {
			if(selection.equals("ALL")) {
				tree.getStore().removeFilter(filters);
			} else {
				tree.getStore().addFilter(filters);
				tree.getStore().setEnableFilters(true);
			}
			return;
		}
			for(ShapeSelectionHandler handler: handlers){
				handler.onShapeSelect(checkedSelection, event.getSource() == searchBtn);
			}

	}

}
