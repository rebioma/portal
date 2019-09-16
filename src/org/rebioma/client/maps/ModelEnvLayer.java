package org.rebioma.client.maps;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.Image;
import org.gwtopenmaps.openlayers.client.layer.XYZ;
import org.gwtopenmaps.openlayers.client.layer.XYZOptions;

import com.google.gwt.core.client.GWT;

public class ModelEnvLayer extends AscTileLayer {

  protected ModelEnvLayer(String ascFileUrl) {
    this(ascFileUrl, 0.5);
  }
  
  public static ModelEnvLayer newInstance(String ascFileUrl){
	  return new ModelEnvLayer(ascFileUrl);
  }

  public ModelEnvLayer(String ascFilePath, double opacity) {
	  super();
	  this.imageOptions.setLayerOpacity(0.5);//RORO imageMapTypeOptions.setTileSize(Size.newInstance(256, 256));
    //RORO this.imageMapTypeOptions.setOpacity(opacity);
    baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f=" + ascFilePath;
    /*this.imageMapTypeOptions.setTileUrl(new TileUrlCallBack() {
		
		@Override
		public String getTileUrl(Point point,
				int zoomLevel) {
			String tileUrl = baseUrl;
		    tileUrl += "&x=" + new Double(Math.rint(point.getX())).intValue();
		    tileUrl += "&y=" +new Double(Math.rint(point.getY())).intValue();
		    tileUrl += "&z=" + zoomLevel;
		    return tileUrl;
		}
	});*/
  }

  @Override
  public TileLayerLegend getLegend() {
    // TODO Auto-generated method stub
    return null;
  }

}
