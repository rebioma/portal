package org.rebioma.client.gxt3.treegrid;

import java.util.List;

import org.rebioma.client.gxt3.treegrid.CheckboxTreeGrid.TreeGridNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnData;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.tree.TreeView.TreeViewRenderMode;

public class CheckboxTreeGridView<M> extends GridView<M> {
	protected CheckboxTreeGrid<M> tree;
	protected TreeStore<M> treeStore;
	private boolean checkable;
	

	 interface TreeResourceBundle extends ClientBundle {
		ImageResource loading();
	 }
	protected TreeResourceBundle resourceBundle = GWT.create(TreeResourceBundle.class);

	public CheckboxTreeGridView() {
		scrollOffset -= 2;
		checkable = true;
	}

	public void collapse(TreeNode<M> node) {
		M p = node.getModel();
		M lc = treeStore.getLastChild(p);

		int start = ds.indexOf(p);
		int end = tree.findLastOpenChildIndex(lc);

		for (int i = end; i > start; i--) {
			ds.remove(i);
		}
		tree.refresh(p);
	}

	public void expand(TreeNode<M> node) {
		M p = node.getModel();
		List<M> children = treeStore.getChildren(p);
		if (children.size() > 0) {
			int idx = ds.indexOf(p);

			ds.addAll(idx + 1, children);

			for (M child : children) {
				TreeNode<M> cn = findNode(child);
				TreeGridNode<M> gn = (TreeGridNode<M>)cn;
				//on force le TreeGrid à remettre les checked node à checked après l'expand
				if(gn.getCheckState() == CheckState.CHECKED){
					//pour simuler un changement d'etat, on decoche avant de le coché.
					gn.setCheckState(CheckState.UNCHECKED);
					tree.setChecked(child, CheckState.CHECKED);
				}
				if (cn != null && cn.isExpanded()) {
					expand(cn);
				}
			}
		}
		tree.refresh(p);
	}

	public XElement getElementContainer(TreeNode<M> node) {
		if (node.getElementContainer() == null) {
			node.setElContainer(node.getElement() != null ? tree
					.getTreeAppearance().getContainerElement(
							node.getElement().<XElement> cast()) : null);
		}
		return node.getElementContainer().cast();
	}

	public Element getIconElement(TreeNode<M> node) {
		if (node.getIconElement() == null) {
			Element row = getRowElement(node);
			if (row != null) {
				XElement r = row.cast();
				XElement icon = tree.getTreeAppearance().findIconElement(r);
				node.setIconElement(icon);
			}
		}
		return node.getIconElement();
	}

	public Element getJointElement(TreeNode<M> node) {
		if (node.getJointElement() == null) {
			Element row = getRowElement(node);
			if (row != null) {
				XElement r = row.cast();
				XElement joint = tree.getTreeAppearance().findJointElement(r);
				node.setJointElement(joint);
			}
		}
		return node.getJointElement();
	}

	public SafeHtml getTemplate(M m, String id, SafeHtml text,
			ImageResource icon, boolean checkable, Joint joint, int level) {
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		tree.getTreeAppearance().renderNode(sb, id, text, tree.getStyle(),
				icon, checkable, CheckState.UNCHECKED, joint, level - 1,
				TreeViewRenderMode.ALL);
		return sb.toSafeHtml();
	}

	public Element getCheckElement(TreeNode<M> node) {
		if (node.getCheckElement() == null) {
			node.setCheckElement(getElementContainer(node) != null ? tree
					.getTreeAppearance().getCheckElement(
							getElementContainer(node)) : null);
		}
		return node.getCheckElement();
	}

	public Element getTextElement(TreeNode<M> node) {
		if (node == null) {
			return null;
		}
		if (node.getTextElement() == null) {
			Element row = getRowElement(node);
			if (row != null) {
				XElement t = row.<XElement> cast().selectNode(
						tree.getTreeAppearance().textSelector());
				node.setTextElement(t);
			}
		}
		return node.getTextElement();
	}

	@Override
	public boolean isSelectableTarget(Element target) {
		boolean b = super.isSelectableTarget(target);
		if (!b) {
			return false;
		}

		TreeNode<M> node = tree.findNode(target);
		if (node != null) {
			Element j = getJointElement(node);
			if (j != null
					&& DOM.isOrHasChild((com.google.gwt.user.client.Element) j,
							(com.google.gwt.user.client.Element) target)) {
				return false;
			}
		}
		return true;
	}

	public void onDropChange(Element e, boolean drop) {
		tree.getTreeAppearance().onDropOver(XElement.as(e), drop);
	}

	public void onIconStyleChange(TreeNode<M> node, ImageResource icon) {
		Element iconEl = getIconElement(node);
		if (iconEl != null) {
			Element e;
			if (icon != null) {
				e = (Element) IconHelper.getElement(icon);
			} else {
				e = DOM.createSpan();
			}
			node.setIconElement((Element) iconEl.getParentElement()
					.insertBefore(e, iconEl));
			iconEl.removeFromParent();
		}
	}

	public void onJointChange(TreeNode<M> node, Joint joint) {
		Element jointEl = getJointElement(node);
		if (jointEl != null) {
			XElement elem = node.getElement().cast();
			node.setJointElement(tree.getTreeAppearance().onJointChange(elem,
					jointEl.<XElement> cast(), joint, tree.getStyle()));
		}
	}

	public void onLoading(TreeNode<M> node) {
		onIconStyleChange(node, resourceBundle.loading());
	}

	@Override
	public void refresh(boolean headerToo) {
		if (grid != null && grid.isViewReady()) {
			for (TreeNode<M> node : tree.nodes.values()) {
				node.clearElements();

			}
		}
		super.refresh(headerToo);
	}

	@Override
	protected void doSort(int colIndex, SortDir sortDir) {
		ColumnConfig<M, ?> column = cm.getColumn(colIndex);
		if (!isRemoteSort()) {
			treeStore.clearSortInfo();

			// These casts can fail, but in dev mode the exception will be
			// caught by
			// the
			// try/catch, unless there are no items in the Store
			@SuppressWarnings({ "unchecked", "rawtypes" })
			ValueProvider<? super M, Comparable> vp = (ValueProvider) column
					.getValueProvider();
			@SuppressWarnings("unchecked")
			StoreSortInfo<M> s = new StoreSortInfo<M>(vp, sortDir);

			if (sortDir == null && storeSortInfo != null
					&& storeSortInfo.getValueProvider() == vp) {
				s.setDirection(storeSortInfo.getDirection() == SortDir.ASC ? SortDir.DESC
						: SortDir.ASC);
			} else if (sortDir == null) {
				s.setDirection(SortDir.ASC);
			}

			if (GWT.isProdMode()) {
				treeStore.addSortInfo(s);
			} else {
				try {
					// addSortInfo will apply its sort when called, which might
					// trigger an
					// exception if the column passed in's data isn't Comparable
					treeStore.addSortInfo(s);
				} catch (ClassCastException ex) {
					GWT.log("Column can't be sorted "
							+ column.getValueProvider().getPath()
							+ " is not Comparable. ", ex);
					throw ex;
				}
			}

		} else {
			// not supported
		}

	}

	protected TreeNode<M> findNode(M m) {
		return tree.findNode(m);
	}

	@Override
	protected List<ColumnData> getColumnData() {
		List<ColumnData> data = super.getColumnData();

		for (int i = 0; i < data.size(); i++) {
			if (cm.indexOf(tree.getTreeColumn()) == i) {
				ColumnData cd = data.get(i);
				cd.setClassNames(cd.getClassNames() + " x-treegrid-column");
			}
		}

		return data;
	}

	protected int getIndenting(TreeNode<M> node) {
		return 18;
	}

	@Override
	protected <N> SafeHtml getRenderedValue(int rowIndex, int colIndex, M m,
			ListStore<M>.Record record) {
		ColumnConfig<M, N> cc = cm.getColumn(colIndex);
		SafeHtml s = super.getRenderedValue(rowIndex, colIndex, m, record);
		TreeNode<M> node = findNode(m);
		if (node != null && cc == tree.getTreeColumn()) {
			return getTemplate(m, node.getId(), s, tree.calculateIconStyle(m),
					checkable, tree.calculateJoint(m), treeStore.getDepth(m));
		}
		return s;
	}

	protected Element getRowElement(TreeNode<M> node) {
		return (Element) getRow(ds.indexOf(node.getModel()));
	}

	@Override
	protected StoreSortInfo<M> getSortState() {
		if (treeStore.getSortInfo().size() > 0) {
			return treeStore.getSortInfo().get(0);
		}
		return null;
	}

	@Override
	protected void init(Grid<M> grid) {
		tree = (CheckboxTreeGrid<M>) grid;
		super.init(grid);
	}

	@Override
	protected void initData(ListStore<M> ds, ColumnModel<M> cm) {
		super.initData(ds, cm);
		treeStore = tree.getTreeStore();
	}

	@Override
	protected void onRemove(M m, int index, boolean isUpdate) {
		super.onRemove(m, index, isUpdate);
		TreeNode<M> node = findNode(m);
		if (node != null) {
			node.clearElements();
		}
	}
	
	public void onCheckChange(TreeNode<M> node, boolean checkable, CheckState state) {
	    Element checkEl = (Element) getCheckElement(node);
	    if (checkEl != null) {
	      node.setCheckElement(tree.getTreeAppearance().onCheckChange(node.getElement().<XElement> cast(),
	          checkEl.<XElement> cast(), checkable, state));
	    }
	  }
}
