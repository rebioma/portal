package org.rebioma.client.services;

import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.ShapeFileInfo;

import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Mika
 *
 */
@RemoteServiceRelativePath("mapGisService")
public interface MapGisService  extends RemoteService {
	/**
	 * Recuperer les id des occurrences qui se trouvent, geographiquement, à l'interieurs de la forme "overlayType" avec les coordonnée geomCoordonnees
	 * @param overlayType
	 * @param geomCoordonnees
	 * @return
	 */
//	List<Integer> findOccurrenceIdByGeom(/*OverlayType overlayType, */List<LatLng> geomCoordonnees);
	
	List<Integer> findOccurrenceIdByGeom(String kml);
	
	
	List<ShapeFileInfo> getShapeFileItems(ShapeFileInfo shapeFile);
	
	/**
	 * 
	 * @param tableGidsMap - couple du nom de la table et les gid de la table
	 * @return liste d'identifiant d'occurrence
	 */
	List<Integer> findOccurrenceIdsByShapeFiles(Map<String, List<Integer>> tableGidsMap);
	
	void launchBatch(String pathShape,String pathShp2pgsql);
}
