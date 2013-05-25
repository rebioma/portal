package org.rebioma.client.maps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.base.Point;
import com.google.gwt.maps.client.maptypes.TileUrlCallBack;

public class ModelEnvLayer extends AscTileLayer {

  protected ModelEnvLayer(String ascFileUrl) {
    this(ascFileUrl, 0.5);
  }
  
  public static ModelEnvLayer newInstance(String ascFileUrl){
	  return new ModelEnvLayer(ascFileUrl);
  }

  public ModelEnvLayer(String ascFilePath, double opacity) {
	  super();
    this.imageMapTypeOptions.setOpacity(opacity);
    baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f=" + ascFilePath;
    this.imageMapTypeOptions.setTileUrl(new TileUrlCallBack() {
		
		@Override
		public String getTileUrl(Point point,
				int zoomLevel) {
			String tileUrl = baseUrl;
		    tileUrl += "&x=" + point.getX();
		    tileUrl += "&y=" + point.getY();
		    tileUrl += "&z=" + zoomLevel;
		    return tileUrl;
		}
	});
  }

  @Override
  public TileLayerLegend getLegend() {
    // TODO Auto-generated method stub
    return null;
  }

}
