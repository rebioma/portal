package org.rebioma.client.gxt3.treegrid;

import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

public interface CheckBoxTreeGridListener<M> {
	
	/**
	 * 
	 * @param event
	 */
	void onTreeNodeStatisticIconClick(Event event, TreeNode<M> node);
	
	/**
	 * 
	 * @param event
	 */
	void onTreeNodeMoreInformationIconClick(Event event, TreeNode<M> node);
}
