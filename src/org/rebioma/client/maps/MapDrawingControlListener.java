package org.rebioma.client.maps;

import com.google.gwt.maps.client.overlays.Circle;
import com.google.gwt.maps.client.overlays.Polygon;

public interface MapDrawingControlListener {
	/**
	 * quand on a fini de dessiner un polygon sur le map
	 */
	public void polygonDrawingCompleteHandler(final Polygon polygon);
	
	/**
	 * @param circle
	 */
	public void circleDrawingCompleteHandler(final Circle circle);
	
	/**
	 * Quand on a supprimer le polygon tracé
	 */
	public void polygonDeletedHandler();
}
