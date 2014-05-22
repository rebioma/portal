package org.rebioma.client.maps;

import org.rebioma.client.ApplicationView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.overlays.Polygon;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Mikajy
 *
 */
public class ClearMapDrawingControl extends HTML implements MapDrawingControlListener, ClickHandler{
	 private final MapDrawingControl mapDrawingcontrol;
	 
	 public ClearMapDrawingControl(MapDrawingControl control) {
		    super(ApplicationView.getConstants()
		            .DeleteDrawedPolygon());
		    mapDrawingcontrol = control;
		    mapDrawingcontrol.addListener(this);
		  //au debut, quand il n'y a pas encore de polygon tracé, ce control doit rester invisible
		    this.setVisible(false);
		    addClickHandler(this);
		    setStyleName("link");
	 }

	@Override
	public void polygonDrawingCompleteHandler(Polygon polygon) {
		this.setVisible(true);//afficher le controle
	}

	@Override
	public void onClick(ClickEvent event) {
		mapDrawingcontrol.clearPolygon();
	}

	@Override
	public void polygonDeletedHandler() {
		this.setVisible(false);//cacher le controle
	}
}
