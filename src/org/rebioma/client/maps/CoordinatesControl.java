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

//	    var firstChild = document.createElement('button');
//	    firstChild.style.backgroundColor = '#fff';
//	    firstChild.style.border = 'none';
//	    firstChild.style.outline = 'none';
//	    firstChild.style.width = '28px';
//	    firstChild.style.height = '28px';
//	    firstChild.style.borderRadius = '2px';
//	    firstChild.style.boxShadow = '0 1px 4px rgba(0,0,0,0.3)';
//	    firstChild.style.cursor = 'pointer';
//	    firstChild.style.marginRight = '10px';
//	    firstChild.style.padding = '0';
//	    firstChild.title = 'Your Location';
//	    controlDiv.appendChild(firstChild);
//
//	    var secondChild = document.createElement('div');
//	    secondChild.style.margin = '5px';
//	    secondChild.style.width = '18px';
//	    secondChild.style.height = '18px';
//	    secondChild.style.backgroundImage = 'url(https://maps.gstatic.com/tactile/mylocation/mylocation-sprite-2x.png)';
//	    secondChild.style.backgroundSize = '180px 18px';
//	    secondChild.style.backgroundPosition = '0 0';
//	    secondChild.style.backgroundRepeat = 'no-repeat';
//	    firstChild.appendChild(secondChild);

	    controlDiv.index = 0;
		return controlDiv;
//	    map.controls[google.maps.ControlPosition.BOTTOM_RIGHT].push(controlDiv);
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
