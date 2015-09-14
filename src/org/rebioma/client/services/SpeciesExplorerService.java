package org.rebioma.client.services;

import java.util.HashMap;
import java.util.List;

import org.rebioma.client.bean.SpeciesStatisticModel;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.SpeciesTreeModelInfoItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * 
 * @author mikajy
 *
 */
@RemoteServiceRelativePath("speciesExplorerService")
public interface SpeciesExplorerService extends RemoteService {
	
	public static final HashMap<String, String> LEVELS=new HashMap<String, String>(){
        {
            put("KINGDOM", SpeciesTreeModel.KINGDOM);
            put("PHYLUM", SpeciesTreeModel.PHYLUM);
            put("CLASS", SpeciesTreeModel.CLASS_);
            put("GENUS", SpeciesTreeModel.GENUS);
            put("ORDER", SpeciesTreeModel.ORDER);
            put("FAMILY", SpeciesTreeModel.FAMILY);
            put("ACCEPTEDSPECIES", "Species");
        }
	};
	/**
	 * récuperer les enfant d'un node
	 * @param parent
	 * @return
	 */
	public List<SpeciesTreeModel> getChildren(SpeciesTreeModel parent);
	
	/**
	 * 
	 * @param model
	 * @return
	 */
	public List<SpeciesStatisticModel> getStatistics(SpeciesTreeModel model);
	
	/**
	 * 
	 */
	public void loadCsv();
	
	/**
	 * 
	 * @param source
	 * @return
	 */
	List<SpeciesTreeModelInfoItem> getInfomations(SpeciesTreeModel source);
	
}
