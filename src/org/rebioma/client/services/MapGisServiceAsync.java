package org.rebioma.client.services;

import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.ShapeFileInfo;

import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MapGisServiceAsync {

	void findOccurrenceIdByGeom(String kml,
			AsyncCallback<List<Integer>> callback);

	void getShapeFileItems(ShapeFileInfo shapeFile, 
			AsyncCallback<List<ShapeFileInfo>> callback);

	void findOccurrenceIdsByShapeFiles(Map<String, List<Integer>> tableGidsMap,
			AsyncCallback<List<Integer>> callback);

	void launchBatch(String pathShape, String pathShp2pgsql,
			AsyncCallback<Void> callback);

	void listAreaAdmin(AsyncCallback<List<String>> callback);

	void getMUpdate(String url, AsyncCallback<String> callback);

	void findOccurrenceIdByCircle(double lat, double lon, double radius,
			AsyncCallback<List<Integer>> callback);

}
