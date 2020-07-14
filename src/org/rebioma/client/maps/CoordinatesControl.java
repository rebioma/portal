package org.rebioma.client.maps;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.events.mousemove.MouseMoveMapEvent;
import com.google.gwt.maps.client.events.mousemove.MouseMoveMapHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
/**
 * Afficher le coordonnées de la souris sur le map
 * @author Mikajy
 *
 */
public class CoordinatesControl extends HorizontalPanel implements MouseMoveMapHandler {
	
	private HTML latlongLabel = new HTML();
	private Element element;
	
	NumberFormat format1 = NumberFormat.getFormat( "00.00000000");
	NumberFormat format2 = NumberFormat.getFormat("000.00000000");
	
	public CoordinatesControl(MapWidget map, ControlPosition position) {
		super();
//		map.setControls(position, this);
		element = addLatLong();
		this.setLabel(map.getCenter());
		map.getControls(ControlPosition.RIGHT_BOTTOM).push(element);
//		map.getControls(ControlPosition.BOTTOM_RIGHT).insertAt(0, this.getElement());
//		Window.alert(map.getControls(ControlPosition.RIGHT_BOTTOM).pop().getInnerText());
		map.addMouseMoveHandler(this);
		latlongLabel.setStyleName("coordinate_label");
//		longitudeLabel.setStyleName("coordinate_label");
		this.add(latlongLabel);
//		this.add(longitudeLabel);
	}
	
	public static native com.google.gwt.user.client.Element addLatLong() /*-{
	    var controlDiv = document.createElement('div');
	    controlDiv.index = 0;
		return controlDiv;
	}-*/;

	public CoordinatesControl(MapWidget map) {
		this(map, ControlPosition.RIGHT_BOTTOM);
	}

	@Override
	public void onEvent(MouseMoveMapEvent evt) {
		LatLng coord = evt.getMouseEvent().getLatLng();
		this.setLabel(coord);
	}
	
	private void setLabel(LatLng coord){
		element.setInnerHTML("<div class='coordinate_label'>" + format1.format(coord.getLatitude()) + ", " + format2.format(coord.getLongitude()) + "</div>");
	}
}
