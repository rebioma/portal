package org.rebioma.client.maps;

import org.rebioma.client.maps.TileLayerSelector.TileLayerCallback;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.services.GeocoderRequestHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Quand on ajoute plusieurs control au meme position, seul le dernier control ajouté est capable de memoriser les handler(click, select, ...)
 * On a crée cette classe pour regrouper les controls qui doivent se trouver au meme position
 * @author Mikajy
 *
 */
public class MapControlsGroup extends VerticalPanel {

	private GeocoderControl geocoder;
	
	private TileLayerSelector tileLayerSelector;
	
	public MapControlsGroup(TileLayerCallback callback, GeocoderRequestHandler requestHandler){
		super();
		this.geocoder = new GeocoderControl(requestHandler);
		this.tileLayerSelector = new EnvLayerSelector(callback);
		this.add(geocoder);
		this.add(tileLayerSelector);
		this.setCellHorizontalAlignment(getTable(), ALIGN_LEFT);
		this.addStyleName("table-right");
		this.setVerticalAlignment(ALIGN_MIDDLE);
		this.setHorizontalAlignment(ALIGN_LEFT);
	}
	
	public GeocoderControl getGeocoder(){
		return this.geocoder;
	}
	
	public TileLayerSelector getLayerSelector(){
		return this.tileLayerSelector;
	}
	
	public void setMap(MapWidget map, ControlPosition position){
		getLayerSelector().setMap(map);
		map.setControls(position, this);
	}

}
