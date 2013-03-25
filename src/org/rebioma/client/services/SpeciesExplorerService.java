package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.SpeciesTreeModel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * 
 * @author mikajy
 *
 */
@RemoteServiceRelativePath("speciesExplorerService")
public interface SpeciesExplorerService extends RemoteService {
	/**
	 * récuperer les enfant d'un node
	 * @param parent
	 * @return
	 */
	public List<SpeciesTreeModel> getChildren(SpeciesTreeModel parent);
	
	
	public void loadCsv();
}
