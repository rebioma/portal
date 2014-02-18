package org.rebioma.client.gxt3.treegrid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.TreeStoreRemoveEvent;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.CheckProvider;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent.BeforeCheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent.HasBeforeCheckChangeHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.BeforeCollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.HasBeforeCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.BeforeExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.HasBeforeExpandItemHandlers;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.HasCheckChangeHandlers;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent.CheckChangedHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.HasCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.HasExpandItemHandlers;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckNodes;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridSelectionModel;

public class CheckboxTreeGrid<M> extends Grid<M> implements
		HasBeforeCollapseItemHandlers<M>, HasCollapseItemHandlers<M>,
		HasBeforeExpandItemHandlers<M>, HasExpandItemHandlers<M>,
		HasBeforeCheckChangeHandlers<M>, HasCheckChangeHandlers<M>,
		CheckProvider<M> {

	public static class TreeGridNode<M> extends TreeNode<M> {

		protected TreeGridNode(String id, M m) {
			super(id, m);
		}

		@Override
		public void clearElements() {
			super.clearElements();
			setContainerElement(null);
			setElContainer(null);
			element = null;
		}

		protected CheckState getCheckState() {
			return checked;
		}

		protected void setCheckState(CheckState state) {
			this.checked = state;
		}

	}
	private CheckNodes checkNodes = CheckNodes.BOTH;
	protected boolean checkable = true;
	private CheckCascade checkStyle = CheckCascade.TRI;
	private boolean cascade = true;

	protected boolean filtering;
	protected TreeLoader<M> loader;
	protected Map<String, TreeNode<M>> nodes = new FastMap<TreeNode<M>>();
	protected StoreHandlers<M> storeHandler = new StoreHandlers<M>() {

		@Override
		public void onAdd(StoreAddEvent<M> event) {
			CheckboxTreeGrid.this.onAdd(event);

		}

		@Override
		public void onClear(StoreClearEvent<M> event) {
			CheckboxTreeGrid.this.onClear(event);

		}

		@Override
		public void onDataChange(StoreDataChangeEvent<M> event) {
			CheckboxTreeGrid.this.onDataChange(event.getParent());

		}

		@Override
		public void onFilter(StoreFilterEvent<M> event) {
			CheckboxTreeGrid.this.onFilter(event);

		}

		@Override
		public void onRecordChange(StoreRecordChangeEvent<M> event) {
			CheckboxTreeGrid.this.onRecordChange(event);

		}

		@Override
		public void onRemove(StoreRemoveEvent<M> event) {
			CheckboxTreeGrid.this.onRemove((TreeStoreRemoveEvent<M>) event);

		}

		@Override
		public void onSort(StoreSortEvent<M> event) {
			CheckboxTreeGrid.this.onSort(event);

		}

		@Override
		public void onUpdate(StoreUpdateEvent<M> event) {
			CheckboxTreeGrid.this.onUpdate(event);

		}
	};

	protected HandlerRegistration storeHandlerRegistration;
	protected CheckboxTreeGridView<M> treeGridView;
	protected TreeStore<M> treeStore;
	private GridAppearance appearance;

	private boolean autoLoad, autoExpand;
	private boolean caching = true;
	private boolean expandOnFilter = true;
	private IconProvider<M> iconProvider;
	private TreeStyle style = new TreeStyle();
	private TreeAppearance treeAppearance;
	private final CheckboxTreeGridView<M> checkboxTreeGridView;
	private ColumnConfig<M, ?> treeColumn;
	
	private Set<CheckBoxTreeGridListener<M>> listeners;

	/**
	 * Creates a new tree grid.
	 * 
	 * @param store
	 *            the tree store
	 * @param cm
	 *            the column model
	 * @param treeColumn
	 *            the tree column
	 */
	public CheckboxTreeGrid(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn) {
		this(store, cm, treeColumn, GWT
				.<GridAppearance> create(GridAppearance.class));
	}

	/**
	 * Creates a new tree grid.
	 * 
	 * @param store
	 *            the tree store
	 * @param cm
	 *            the column model
	 * @param treeColumn
	 *            the tree column
	 * @param appearance
	 *            the grid appearance
	 */
	public CheckboxTreeGrid(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn, GridAppearance appearance) {
		this(store, cm, treeColumn, appearance, GWT
				.<TreeAppearance> create(SpeciesTreeAppearance.class));
	}

	/**
	 * Creates a new tree grid.
	 * 
	 * @param store
	 *            the tree store
	 * @param cm
	 *            the column model
	 * @param treeColumn
	 *            the tree column
	 * @param appearance
	 *            the grid appearance
	 * @param treeAppearance
	 *            the tree appearance
	 */
	public CheckboxTreeGrid(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn, GridAppearance appearance,
			TreeAppearance treeAppearance) {
		this.appearance = appearance;
		this.treeAppearance = treeAppearance;
		checkable = true;

		disabledStyle = null;

		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		this.appearance.render(builder);

		setElement(XDOM.create(builder.toSafeHtml()));
		getElement().makePositionable();

		// Do not remove, this is being used in Grid.css
		addStyleName("x-treegrid");

		getElement().setTabIndex(0);
		getElement().setAttribute("hideFocus", "true");

		this.cm = cm;
		setTreeColumn(treeColumn);
		this.treeStore = store;
		this.store = createListStore();

		setSelectionModel(new TreeGridSelectionModel<M>());

		disabledStyle = null;
		storeHandlerRegistration = treeStore.addStoreHandlers(storeHandler);
		checkboxTreeGridView = new CheckboxTreeGridView<M>(); 
		setView(checkboxTreeGridView);
		setAllowTextSelection(false);

		sinkCellEvents();
	}
	
	public void unCheckAll(){
		setCheckedSelection(null);
		List<M> models = treeStore.getAll();
		for(int i=0;i< models.size(); i++){
			M model = models.get(i);
			unCheck(model);
		}
	}
	/** 
	 * Recursivly uncheck the model and all children.
	 * @param model
	 */
	private void unCheck(M model){
		TreeNode<M> cn = findNode(model);
		TreeGridNode<M> gn = (TreeGridNode<M>)cn;
		gn.setCheckState(CheckState.UNCHECKED);
		if(treeStore.hasChildren(model)){
			List<M> children = treeStore.getAllChildren(model);
			for(M child: children){
				unCheck(child);
			}
		}
	}
	
	public void addCheckBoxTreeGridListener(CheckBoxTreeGridListener<M> listener){
		if(listener == null){
			return;
		}else if(this.listeners == null){
			this.listeners = new HashSet<CheckBoxTreeGridListener<M>>();
		}
		this.listeners.add(listener);
	}
	/**
	 * Collapses all nodes.
	 */
	public void collapseAll() {
		for (M child : treeStore.getRootItems()) {
			setExpanded(child, false, true);
		}
	}

	/**
	 * Expands all nodes.
	 */
	public void expandAll() {
		for (M child : treeStore.getRootItems()) {
			setExpanded(child, true, true);
		}
	}

	/**
	 * Returns the tree node for the given target.
	 * 
	 * @param target
	 *            the target element
	 * @return the tree node or null if no match
	 */
	public TreeNode<M> findNode(Element target) {
		Element row = (Element) getView().findRow(target);
		if (row != null) {
			XElement item = XElement.as(row).selectNode(
					treeAppearance.itemSelector());
			if (item != null) {
				String id = item.getId();
				TreeNode<M> node = nodes.get(id);
				return node;
			}
		}
		return null;
	}

	/**
	 * Returns the grid appearance.
	 * 
	 * @return the grid appearance
	 */
	public GridAppearance getAppearance() {
		return appearance;
	}

	/**
	 * Returns the model icon provider.
	 * 
	 * @return the icon provider
	 */
	public IconProvider<M> getIconProvider() {
		return iconProvider;
	}

	/**
	 * Returns the tree style.
	 * 
	 * @return the tree style
	 */
	public TreeStyle getStyle() {
		return style;
	}

	/**
	 * Returns the tree appearance.
	 * 
	 * @return the tree appearance
	 */
	public TreeAppearance getTreeAppearance() {
		return treeAppearance;
	}

	/**
	 * Returns the column that represents the tree nodes.
	 * 
	 * @return the tree column
	 */
	public ColumnConfig<M, ?> getTreeColumn() {
		return treeColumn;
	}

	/**
	 * Returns the tree loader.
	 * 
	 * @return the tree loader or null if not specified
	 */
	public TreeLoader<M> getTreeLoader() {
		return loader;
	}

	/**
	 * Returns the tree's tree store.
	 * 
	 * @return the tree store
	 */
	public TreeStore<M> getTreeStore() {
		return treeStore;
	}

	/**
	 * Returns the tree's view.
	 * 
	 * @return the view
	 */
	public CheckboxTreeGridView<M> getTreeView() {
		return treeGridView;
	}

	/**
	 * Returns true if auto expand is enabled.
	 * 
	 * @return the auto expand state
	 */
	public boolean isAutoExpand() {
		return autoExpand;
	}

	/**
	 * Returns true if auto load is enabled.
	 * 
	 * @return the auto load state
	 */
	public boolean isAutoLoad() {
		return autoLoad;
	}

	/**
	 * Returns true when a loader is queried for it's children each time a node
	 * is expanded. Only applies when using a loader with the tree store.
	 * 
	 * @return true if caching
	 */
	public boolean isCaching() {
		return caching;
	}

	/**
	 * Returns true if the model is expanded.
	 * 
	 * @param model
	 *            the model
	 * @return true if expanded
	 */
	public boolean isExpanded(M model) {
		TreeNode<M> node = findNode(model);
		return node != null && node.isExpanded();
	}

	/**
	 * Returns the if expand all and collapse all is enabled on filter changes.
	 * 
	 * @return the expand all collapse all state
	 */
	public boolean isExpandOnFilter() {
		return expandOnFilter;
	}

	/**
	 * Returns true if the model is a leaf node. The leaf state allows a tree
	 * item to specify if it has children before the children have been
	 * realized.
	 * 
	 * @param model
	 *            the model
	 * @return the leaf state
	 */
	public boolean isLeaf(M model) {
		return !hasChildren(model);
	}

	@Override
	public void reconfigure(ListStore<M> store, ColumnModel<M> cm) {
		throw new UnsupportedOperationException(
				"Please call the other reconfigure method");
	}

	public void reconfigure(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn) {
		if (isLoadMask()) {
			mask(DefaultMessages.getMessages().loadMask_msg());
		}
		this.store.clear();

		nodes.clear();

		this.store = createListStore();

		if (storeHandlerRegistration != null) {
			storeHandlerRegistration.removeHandler();
		}

		treeStore = store;
		if (treeStore != null) {
			storeHandlerRegistration = treeStore.addStoreHandlers(storeHandler);
		}

		treeGridView.initData(this.store, cm);

		this.cm = cm;
		setTreeColumn(treeColumn);
		// rebind the sm
		setSelectionModel(sm);
		if (isViewReady()) {
			view.refresh(true);
			doInitialLoad();
		}

		if (isLoadMask()) {
			unmask();
		}
	}

	/**
	 * Refreshes the data for the given model.
	 * 
	 * @param model
	 *            the model to be refreshed
	 */
	public void refresh(M model) {
		TreeNode<M> node = findNode(model);
		if (viewReady && node != null) {
			treeGridView.onIconStyleChange(node, calculateIconStyle(model));
			treeGridView.onJointChange(node, calculateJoint(model));
		}
	}

	/**
	 * If set to true, all non leaf nodes will be expanded automatically
	 * (defaults to false).
	 * 
	 * @param autoExpand
	 *            the auto expand state to set.
	 */
	public void setAutoExpand(boolean autoExpand) {
		this.autoExpand = autoExpand;
	}

	/**
	 * Sets whether all children should automatically be loaded recursively
	 * (defaults to false). Useful when the tree must be fully populated when
	 * initially rendered.
	 * 
	 * @param autoLoad
	 *            true to auto load
	 */
	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	/**
	 * Sets whether the children should be cached after first being retrieved
	 * from the store (defaults to true). When <code>false</code>, a load
	 * request will be made each time a node is expanded.
	 * 
	 * @param caching
	 *            the caching state
	 */
	public void setCaching(boolean caching) {
		this.caching = caching;
	}

	/**
	 * Sets the item's expand state.
	 * 
	 * @param model
	 *            the model
	 * @param expand
	 *            true to expand
	 */
	public void setExpanded(M model, boolean expand) {
		setExpanded(model, expand, false);
	}

	/**
	 * Sets the item's expand state.
	 * 
	 * @param model
	 *            the model
	 * @param expand
	 *            true to expand
	 * @param deep
	 *            true to expand all children recursively
	 */
	public void setExpanded(M model, boolean expand, boolean deep) {
		TreeNode<M> node = findNode(model);
		if (node != null) {
			if (expand) {
				// make parents visible
				List<M> list = new ArrayList<M>();
				M p = model;
				while ((p = treeStore.getParent(p)) != null) {
					if (!findNode(p).isExpanded()) {
						list.add(p);
					}
				}
				for (int i = list.size() - 1; i >= 0; i--) {
					M item = list.get(i);
					setExpanded(item, expand, false);
				}
			}

			if (expand) {
				if (!isLeaf(model)) {
					// if we are loading, ignore it
					if (node.isLoading()) {
						return;
					}
					// if we have a loader and node is not loaded make
					// load request and exit method
					if (!node.isExpanded() && loader != null
							&& (!node.isLoaded() || !caching) && !filtering) {
						treeStore.removeChildren(model);
						node.setExpand(true);
						node.setExpandDeep(deep);
						node.setLoading(true);
						treeGridView.onLoading(node);
						loader.loadChildren(model);
						return;
					}
					if (!node.isExpanded()
							&& fireCancellableEvent(new BeforeExpandItemEvent<M>(
									model))) {
						node.setExpanded(true);

						if (!node.isChildrenRendered()) {
							renderChildren(model, false);
							node.setChildrenRendered(true);
						}
						// expand
						treeGridView.expand(node);
						fireEvent(new ExpandItemEvent<M>(model));
					}

					if (deep) {
						setExpandChildren(model, true);
					}
				}
			} else {
				if (node.isExpanded()
						&& fireCancellableEvent(new BeforeCollapseItemEvent<M>(
								model))) {
					node.setExpanded(false);
					// collapse
					treeGridView.collapse(node);
					fireEvent(new CollapseItemEvent<M>(model));
				}
				if (deep) {
					setExpandChildren(model, false);
				}
			}
		}
		setChecked(model);
	}

	/**
	 * Sets whether the tree should expand all and collapse all when filters are
	 * applied (defaults to true).
	 * 
	 * @param expandOnFilter
	 *            true to expand and collapse on filter changes
	 */
	public void setExpandOnFilter(boolean expandOnFilter) {
		this.expandOnFilter = expandOnFilter;
	}

	/**
	 * Sets the tree's model icon provider which provides the icon style for
	 * each model.
	 * 
	 * @param iconProvider
	 *            the icon provider
	 */
	public void setIconProvider(IconProvider<M> iconProvider) {
		this.iconProvider = iconProvider;
	}

	/**
	 * Sets the item's leaf state. The leaf state allows control of the expand
	 * icon before the children have been realized.
	 * 
	 * @param model
	 *            the model
	 * @param leaf
	 *            the leaf state
	 */
	public void setLeaf(M model, boolean leaf) {
		TreeNode<M> t = findNode(model);
		if (t != null) {
			t.setLeaf(leaf);
		}
	}

	/**
	 * Sets the tree loader.
	 * 
	 * @param treeLoader
	 *            the tree loader
	 */
	public void setTreeLoader(TreeLoader<M> treeLoader) {
		this.loader = treeLoader;
		final CheckboxTreeGrid<M> treeGrid = this;
		loader.addLoadHandler(new LoadHandler<M, List<M>>(){
			@Override
			public void onLoad(
					LoadEvent<M, List<M>> event) {
					Mask.unmask(treeGrid.getElement());
			}
		});
	}

	@Override
	public void setView(GridView<M> view) {
		assert view instanceof CheckboxTreeGridView : "The view for a TreeGrid has to be an instance of CheckboxTreeGridView";
		super.setView(view);
		treeGridView = (CheckboxTreeGridView<M>) view;
	}

	/**
	 * Toggles the model's expand state.
	 * 
	 * @param model
	 *            the model
	 */
	public void toggle(M model) {
		TreeNode<M> node = findNode(model);
		if (node != null) {
			boolean expanded = node.isExpanded();
			setExpanded(model, !expanded);
			if(!expanded)onTriExpandCascade(model);
		}
	}

	protected ImageResource calculateIconStyle(M model) {
		ImageResource style = null;
		if (iconProvider != null) {
			ImageResource iconStyle = iconProvider.getIcon(model);
			if (iconStyle != null) {
				return iconStyle;
			}
		}
		TreeStyle ts = getStyle();
		if (!isLeaf(model)) {
			if (isExpanded(model)) {
				style = ts.getNodeOpenIcon() != null ? ts.getNodeOpenIcon()
						: treeAppearance.openNodeIcon();
			} else {
				style = ts.getNodeCloseIcon() != null ? ts.getNodeCloseIcon()
						: treeAppearance.closeNodeIcon();
			}
		} else {
			style = ts.getLeafIcon();
		}
		return style;
	}

	protected Joint calculateJoint(M model) {
		if (model == null) {
			return Joint.NONE;
		}
		TreeNode<M> node = findNode(model);
		Joint joint = Joint.NONE;
		if (node == null) {
			return joint;
		}
		if (!isLeaf(model)) {
			boolean children = true;

			if (node.isExpanded()) {
				joint = children ? Joint.EXPANDED : Joint.NONE;
			} else {
				joint = children ? Joint.COLLAPSED : Joint.NONE;
			}
		}
		return joint;
	}

	protected ListStore<M> createListStore() {
		return new ListStore<M>(treeStore.getKeyProvider()) {
			@Override
			public Record getRecord(M model) {
				return treeStore.getRecord(model);
			}

			@Override
			public boolean hasRecord(M model) {
				return treeStore.hasRecord(model);
			}
		};
	}

	protected int findLastOpenChildIndex(M model) {
		TreeNode<M> mark = findNode(model);
		M lc = model;
		while (mark != null && mark.isExpanded()) {
			M m = treeStore.getLastChild(mark.getModel());
			if (m != null) {
				lc = m;
				mark = findNode(lc);
			} else {
				break;
			}
		}
		return store.indexOf(lc);
	}

	protected TreeNode<M> findNode(M m) {
		if (m == null)
			return null;
		return nodes.get(generateModelId(m));
	}

	protected String generateModelId(M m) {
		return getId() + "_" + (treeStore.getKeyProvider().getKey(m));
	}

	protected boolean hasChildren(M model) {
		TreeNode<M> node = findNode(model);
		if (loader != null && node != null && !node.isLoaded()) {
			return loader.hasChildren(node.getModel());
		}
		if (node != null
				&& (!node.isLeaf() || treeStore.hasChildren(node.getModel()))) {
			return true;
		}
		return false;
	}

	protected void onAdd(StoreAddEvent<M> se) {
		if (viewReady) {
			M p = treeStore.getParent(se.getItems().get(0));
			if (p == null) {
				for (M child : se.getItems()) {
					register(child);
				}
				if (se.getIndex() > 0) {
					M prev = treeStore.getChild(se.getIndex() - 1);
					int index = findLastOpenChildIndex(prev);
					store.addAll(index + 1, se.getItems());
				} else {
					store.addAll(se.getIndex(), se.getItems());
				}
			} else {
				TreeNode<M> node = findNode(p);
				if (node != null) {
					for (M child : se.getItems()) {
						register(child);
					}
					if (!node.isExpanded()) {
						refresh(p);
						return;
					}
					int index = se.getIndex();
					if (index == 0) {
						int pindex = store.indexOf(p);
						store.addAll(pindex + 1, se.getItems());
					} else {
						index = store.indexOf(treeStore.getChildren(p).get(
								index - 1));
						TreeNode<M> mark = findNode(store.get(index));
						index = findLastOpenChildIndex(mark.getModel());
						store.addAll(index + 1, se.getItems());
					}
					refresh(p);
				}
			}
		}
	}

	@Override
	protected void onAfterRenderView() {
		super.onAfterRenderView();
		Mask.mask(this.getElement(), "Loading ...");
		doInitialLoad();
	}

	protected void onClear(StoreClearEvent<M> event) {
		onDataChange(null);
	}

	@Override
	protected void onClick(Event event) {
		EventTarget eventTarget = event.getEventTarget();
		if (Element.is(eventTarget)) {

			M m = store.get(getView().findRowIndex(Element.as(eventTarget)));
			if (m != null) {
				TreeNode<M> node = findNode(m);
				if (node != null) {
					Element jointEl = treeGridView.getJointElement(node);
					Element target = (com.google.gwt.user.client.Element) (Element.as(eventTarget)).cast();
					if(target.getClassName().contains(SpeciesTreeAppearance.STATISTIC_ICON_CLASS_NAME)){
						if(this.listeners != null){
							for(CheckBoxTreeGridListener<M> listener: this.listeners){
								if(listener != null){
									listener.onTreeNodeStatisticIconClick(event, node);
								}
							}
						}
						event.stopPropagation();
					} else if(target.getClassName().contains(SpeciesTreeAppearance.MORE_INFORMATION_ICON_CLASS_NAME)){
						if(this.listeners != null){
							for(CheckBoxTreeGridListener<M> listener: this.listeners){
								if(listener != null){
									listener.onTreeNodeMoreInformationIconClick(event, node);
								}
							}
						}
						event.stopPropagation();
					} else if (jointEl != null
							&& DOM.isOrHasChild(
									(com.google.gwt.user.client.Element) jointEl
											.cast(),
									(com.google.gwt.user.client.Element) (Element
											.as(eventTarget)).cast())) {
						toggle(m);
					} else {
						// si on a cliqu� sur un checkbox, alors on change
						// l'etat du checkbox.
						Element checkEl = treeGridView.getCheckElement(node);
						XEvent e = event.<XEvent> cast();
						if (checkable && checkEl != null && e.within(checkEl)) {
							TreeGridNode<M> gn = (TreeGridNode<M>) node;
							onCheckClick(event, gn);
						}else{
							super.onClick(event);
						}
					}
				}
			}
		} else {
			super.onClick(event);
		}
	}
	
	public void onStatisticIconClick(Event event, TreeNode<M> node){
		//à implementer comme une methode abstraite
	}

	protected void onDataChange(M parent) {
		if (!viewReady) {
			return;
		}

		if (parent == null) {
			store.clear();
			nodes.clear();
			renderChildren(null, autoLoad);
		} else {
			TreeNode<M> n = findNode(parent);
			if (n != null) {
				n.setLoaded(true);
				n.setLoading(false);
				renderChildren(parent, autoLoad);

				if (n.isExpand() && !isLeaf(parent)) {
					n.setExpand(false);
					boolean deep = n.isExpandDeep();
					n.setExpandDeep(false);
					boolean c = caching;
					caching = true;
					setExpanded(parent, true, deep);
					caching = c;
				} else {
					refresh(parent);
				}
			}
		}
	}

	@Override
	protected void onDoubleClick(Event e) {
		super.onDoubleClick(e);
		if (Element.is(e.getEventTarget())) {
			int i = getView().findRowIndex(Element.as(e.getEventTarget()));
			M m = store.get(i);
			if (m != null) {
				toggle(m);
			}
		}
	}

	protected void onFilter(StoreFilterEvent<M> se) {
		onDataChange(null);
		if (expandOnFilter && treeStore.isFiltered()) {
			expandAll();
		}
	}

	protected void onRecordChange(StoreRecordChangeEvent<M> event) {
		store.update(event.getRecord().getModel());

	}

	protected void onRemove(TreeStoreRemoveEvent<M> event) {
		if (viewReady) {
			unregister(event.getItem());
			store.remove(event.getItem());
			for (M child : event.getChildren()) {
				unregister(child);
				store.remove(child);
			}
			TreeNode<M> p = findNode(event.getParent());
			if (p != null && p.isExpanded()
					&& treeStore.getChildCount(p.getModel()) == 0) {
				setExpanded(p.getModel(), false);
			} else if (p != null && treeStore.getChildCount(p.getModel()) == 0) {
				refresh(event.getParent());
			}
		}
	}

	protected void onSort(StoreSortEvent<M> event) {
		onDataChange(null);

	}

	protected void onUpdate(StoreUpdateEvent<M> se) {
		for (M m : se.getItems()) {
			if (store.indexOf(m) != -1) {
				store.update(m);
			}
		}
	}

	protected String register(M m) {
		String id = generateModelId(m);
		if (!nodes.containsKey(id)) {
			nodes.put(id, new TreeGridNode<M>(id, m));
		}
		return id;
	}

	protected void renderChildren(M parent, boolean auto) {
		List<M> children = parent == null ? treeStore.getRootItems()
				: treeStore.getChildren(parent);

		for (M child : children) {
			register(child);
		}

		if (parent == null && children.size() > 0) {
			store.addAll(children);
		}

		for (M child : children) {
			if (autoExpand) {
				final M c = child;
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						setExpanded(c, true);

					}
				});
			} else if (loader != null) {
				if (autoLoad) {
					if (store.isFiltered() || (!auto)) {
						renderChildren(child, auto);
					} else {
						loader.loadChildren(child);
					}
				}
			}
		}
	}

	protected void setTreeColumn(ColumnConfig<M, ?> treeColumn) {
		assert (treeColumn != null && cm.indexOf(treeColumn) != -1) : "treeColumn not found in ColumnModel";
		this.treeColumn = treeColumn;
	}

	protected void unregister(M m) {
		TreeNode<M> node = findNode(m);
		if (node != null) {
			node.clearElements();

			nodes.remove(generateModelId(m));
		}
	}

	private void doInitialLoad() {
		List<M> rootItems = treeStore.getRootItems(); 
		if (rootItems.size() == 0 && loader != null) {
			loader.load();
		} else {
			renderChildren(null, false);
			if (autoExpand) {
				expandAll();
			}
		}

	}

	private void setExpandChildren(M m, boolean expand) {
		for (M child : treeStore.getChildren(m)) {
			setExpanded(child, expand, true);
		}
	}

	@Override
	public HandlerRegistration addCheckChangedHandler(
			CheckChangedHandler<M> handler) {
		return addHandler(handler, CheckChangedEvent.getType());
	}

	@Override
	public HandlerRegistration addCheckChangeHandler(
			CheckChangeHandler<M> handler) {
		return addHandler(handler, CheckChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addBeforeCheckChangeHandler(
			BeforeCheckChangeHandler<M> handler) {
		return addHandler(handler, BeforeCheckChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addBeforeCollapseHandler(
			BeforeCollapseItemHandler<M> handler) {
		return addHandler(handler, BeforeCollapseItemEvent.getType());
	}

	@Override
	public HandlerRegistration addBeforeExpandHandler(
			BeforeExpandItemHandler<M> handler) {
		return addHandler(handler, BeforeExpandItemEvent.getType());
	}

	@Override
	public HandlerRegistration addCollapseHandler(CollapseItemHandler<M> handler) {
		return addHandler(handler, CollapseItemEvent.getType());
	}

	@Override
	public HandlerRegistration addExpandHandler(ExpandItemHandler<M> handler) {
		return addHandler(handler, ExpandItemEvent.getType());
	}

	protected void onCheckClick(Event event, TreeGridNode<M> node) {
		event.stopPropagation();
		event.preventDefault();
		setChecked(
				node.getModel(),
				(node.getCheckState() == CheckState.CHECKED || node
						.getCheckState() == CheckState.PARTIAL) ? CheckState.UNCHECKED
						: CheckState.CHECKED);
	}

	protected boolean isCheckable(TreeNode<M> node) {
		boolean leaf = isLeaf(node.getModel());
		boolean check = checkable;
		switch (checkNodes) {
		case LEAF:
			if (!leaf) {
				check = false;
			}
			break;
		case PARENT:
			if (leaf) {
				check = false;
			}
		}
		return check;
	}

	public boolean isCheckable() {
		return checkable;
	}

	@Override
	public List<M> getCheckedSelection() {
		List<M> checked = new ArrayList<M>();
		for (TreeNode<M> n : nodes.values()) {
			TreeGridNode<M> gn = (TreeGridNode<M>) n;
			//{WD si le parent est coché on ne recupere plus les children cochés
			M parent = treeStore.getParent(n.getModel());
			if(parent != null && ((TreeGridNode<M>)findNode(parent)).getCheckState() == CheckState.CHECKED)
				continue;
			//}
			if (gn.getCheckState() == CheckState.CHECKED) {
				checked.add(n.getModel());
			}
		}
		return checked;
	}

	@Override
	public boolean isChecked(M model) {
		TreeNode<M> node = findNode(model);
		if (node != null && isCheckable(node)) {
			TreeGridNode<M> gridNode = (TreeGridNode<M>) node;
			return gridNode.getCheckState() == CheckState.CHECKED;
		}
		return false;
	}

	@Override
	public void setCheckedSelection(List<M> selection) {
		for (M m : store.getAll()) {
			setChecked(
					m,
					selection != null && selection.contains(m) ? CheckState.CHECKED
							: CheckState.UNCHECKED);
		}
	}

	/**
	 * The check cascade style value which determines if check box changes
	 * cascade to parent and children.
	 * 
	 * @return the check cascade style
	 */
	public CheckCascade getCheckStyle() {
		return checkStyle;
	}

	protected void onCheckCascade(M model, CheckState checked) {
		switch (getCheckStyle()) {
		case PARENTS:
			if (checked == CheckState.CHECKED) {
				M p = treeStore.getParent(model);
				while (p != null) {
					setChecked(p, CheckState.CHECKED);
					p = treeStore.getParent(p);
				}
			} else {
				for (M child : treeStore.getChildren(model)) {
					setChecked(child, CheckState.UNCHECKED);
				}
			}
			break;
		case CHILDREN:
			for (M child : treeStore.getChildren(model)) {
				setChecked(child, checked);
			}
			break;
		case TRI:
			onTriCheckCascade(model, checked);
			break;
		}
	}

	protected void onTriCheckCascade(M model, CheckState checked) {
		if (checked == CheckState.CHECKED) {

			List<M> children = treeStore.getAllChildren(model);
			cascade = false;
			for (M child : children) {
				TreeNode<M> n = findNode(child);
				if (n != null) {
					setChecked(child, checked);
				}

			}

			M parent = treeStore.getParent(model);
			while (parent != null) {
				boolean allChildrenChecked = true;
				for (M child : treeStore.getAllChildren(parent)) {
					TreeNode<M> n = findNode(child);
					if (n != null) {
						if (!isChecked(child)) {
							allChildrenChecked = false;
						}
					}
				}

				if (!allChildrenChecked) {
					setChecked(parent, CheckState.PARTIAL);
				} else {
					setChecked(parent, CheckState.CHECKED);
				}

				parent = treeStore.getParent(parent);

			}
			cascade = true;
		} else if (checked == CheckState.UNCHECKED) {
			List<M> children = treeStore.getAllChildren(model);
			cascade = false;
			for (M child : children) {
				setChecked(child, checked);
			}

			M parent = treeStore.getParent(model);
			while (parent != null) {
				boolean anyChildChecked = false;
				for (M child : treeStore.getAllChildren(parent)) {
					if (isChecked(child)) {
						anyChildChecked = true;
					}
				}

				if (anyChildChecked) {
					setChecked(parent, CheckState.PARTIAL);
				} else {
					setChecked(parent, CheckState.UNCHECKED);
				}

				parent = treeStore.getParent(parent);
			}

			cascade = true;
		}
	}

	/**
	 * Sets the check state of the item. The checked state will only be set for
	 * nodes that have been rendered, {@link #setAutoLoad(boolean)} can be used
	 * to render all children.
	 * 
	 * @param item
	 *            the item
	 * @param checked
	 *            the check state
	 */
	public void setChecked(M item, CheckState checked) {
		if (!checkable)
			return;
		TreeNode<M> node = findNode(item);
		if (node != null) {
			TreeGridNode<M> gridNode = (TreeGridNode<M>) node;
			if (gridNode.getCheckState() == checked) {
				return;
			}

			boolean leaf = isLeaf(item);
			if ((!leaf && checkNodes == CheckNodes.LEAF)
					|| (leaf && checkNodes == CheckNodes.PARENT)) {
				return;
			}

			if (fireCancellableEvent(new BeforeCheckChangeEvent<M>(
					node.getModel(), gridNode.getCheckState()))) {

				gridNode.setCheckState(checked);

				treeGridView.onCheckChange(node, checkable, checked);

				fireEvent(new CheckChangeEvent<M>(item,
						gridNode.getCheckState()));
				fireEvent(new CheckChangedEvent<M>(getCheckedSelection()));

				if (cascade) {
					onCheckCascade(item, checked);
				}
			}
		}
	}
	
	protected void onTriExpandCascade(M model) {
		if(model == null)return;
		TreeNode<M> node = findNode(model);
		TreeGridNode<M> gridN = (TreeGridNode<M>) node;
		if(gridN.getCheckState()==CheckState.CHECKED) {
//			List<M> children = treeStore.getAllChildren(model);
//			for (M child: children) {
//				TreeNode<M> n = findNode(child);
//				TreeGridNode<M> gridNode = (TreeGridNode<M>) n;
//				gridNode.setCheckState(CheckState.CHECKED);
//	
//				treeGridView.onCheckChange(n, checkable, CheckState.CHECKED);
//			
//			}
			return;
		} else {
			List<M> children = treeStore.getAllChildren(model);
			
			if(children ==null || children.size() == 0)return;
			boolean allChildrenUnChecked = true;
			for (M child : children) {
				TreeNode<M> n = findNode(child);
				if (n != null) {
					onTriExpandCascade(child);
					if (isChecked(child)) {
						allChildrenUnChecked = false;
					}
				}
			}
			TreeNode<M> n = findNode(model);
			if (!allChildrenUnChecked) {
				TreeGridNode<M> gridNode = (TreeGridNode<M>) n;
				gridNode.setCheckState(CheckState.PARTIAL);
	
				treeGridView.onCheckChange(n, checkable, CheckState.PARTIAL);
			} 
		}
	}
	
	public void setChecked(M item) {
		if (!checkable)
			return;
		TreeNode<M> node = findNode(item);
		if (node != null) {
			TreeGridNode<M> gridNode = (TreeGridNode<M>) node;
			CheckState checked = gridNode.getCheckState();
			if (gridNode.getCheckState() != CheckState.CHECKED) {
				return;
			}

			boolean leaf = isLeaf(item);
			if ((!leaf && checkNodes == CheckNodes.LEAF)
					|| (leaf && checkNodes == CheckNodes.PARENT)) {
				return;
			}

			if (fireCancellableEvent(new BeforeCheckChangeEvent<M>(
					node.getModel(), gridNode.getCheckState()))) {

				gridNode.setCheckState(checked);

				treeGridView.onCheckChange(node, checkable, checked);

				fireEvent(new CheckChangeEvent<M>(item,
						gridNode.getCheckState()));
				fireEvent(new CheckChangedEvent<M>(getCheckedSelection()));

				if (cascade) {
					onCheckCascade(item, checked);
				}
			}
		}
	}
}
