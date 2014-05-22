package org.rebioma.client.maps;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.events.mousemove.MouseMoveMapEvent;
import com.google.gwt.maps.client.events.mousemove.MouseMoveMapHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
/**
 * Afficher le coordonnées de la souris sur le map
 * @author Mikajy
 *
 */
public class CoordinatesControl extends HorizontalPanel implements MouseMoveMapHandler {
	
	private HTML latitudeLabel = new HTML();
	private HTML longitudeLabel = new HTML();
	
	public CoordinatesControl(MapWidget map, ControlPosition position) {
		super();
		map.setControls(position, this);
		map.addMouseMoveHandler(this);
		latitudeLabel.setStyleName("coordinate_label");
		longitudeLabel.setStyleName("coordinate_label");
		this.add(latitudeLabel);
		this.add(longitudeLabel);
	}

	public CoordinatesControl(MapWidget map) {
		this(map, ControlPosition.RIGHT_BOTTOM);
	}

	@Override
	public void onEvent(MouseMoveMapEvent evt) {
		LatLng coord = evt.getMouseEvent().getLatLng();
		this.setLabel(coord);
	}
	
	private void setLabel(LatLng coord){
		latitudeLabel.setHTML(Double.toString(coord.getLatitude()));
		longitudeLabel.setHTML(Double.toString(coord.getLongitude()));
	}
}
