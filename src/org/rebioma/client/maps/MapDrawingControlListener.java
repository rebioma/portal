package org.rebioma.client.maps;

import com.google.gwt.maps.client.overlays.Polygon;

public interface MapDrawingControlListener {
	/**
	 * quand on a fini de dessiner un polygon sur le map
	 */
	public void polygonDrawingCompleteHandler(final Polygon polygon);
	
	/**
	 * Quand on a supprimer le polygon tracé
	 */
	public void polygonDeletedHandler();
}
