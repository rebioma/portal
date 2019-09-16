/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.client.maps;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.layer.Image;
import org.gwtopenmaps.openlayers.client.layer.XYZ;
import org.rebioma.client.bean.AscData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.maptypes.TileUrlCallBack;

/**
 * Represents an environmental layer that backed by an {@link AscData} that can
 * be overlaid on a Google map.
 */
public class EnvLayer extends AscTileLayer {

  /**
   * Returns a layer info that allows this environmental layer to be lazy
   * loaded.
   * 
   * @param data
   *          the asc data
   * @return the loayer info
   */
  public static LayerInfo init(final AscData data) {
    return new LayerInfo() {
      @Override
      public String getName() {
        return dataSummary(data);
      }

      @Override
      protected AscTileLayer get() {
        return EnvLayer.newInstance(data);
      }
    };
  }

  private static String dataSummary(AscData data) {
    return data.getEnvDataType() + " - " + data.getEnvDataSubtype() + " "
        + data.getYear();
  }

  private AscData data;

  private TileLayerLegend legend;

  protected EnvLayer() {
    super();
  }
  
  public static EnvLayer newInstance(AscData data) {
		final EnvLayer envLayer = new EnvLayer();
		envLayer.data = data;
		// RORO envLayer.imageMapTypeOptions.setTileSize(Size.newInstance(256,
		// 256));
		envLayer.imageOptions.setLayerOpacity(0.5);// RORO
													// envLayer.imageMapTypeOptions.setOpacity(0.5);
		//envLayer.imageOptions.setDisplayOutsideMaxExtent(true);
		// envLayer.setOpacity(opacity);
		envLayer.baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f="
				+ data.getFileName();
		Image imageLayer = new Image("", envLayer.baseUrl, new Bounds(42.30124,-26.5823, 51.14843, -11.36225).transform(new Projection("EPSG:4326"), new Projection("EPSG:900913")), new Size(256,256),
				envLayer.imageOptions);
		imageLayer.setIsBaseLayer(false);
		envLayer.overlay=imageLayer;
		/*
		 * envLayer.imageMapTypeOptions.setTileUrl(new TileUrlCallBack() {
		 * 
		 * @Override public String getTileUrl(Point point, int zoomLevel) {
		 * String tileUrl = envLayer.baseUrl; tileUrl += "&x=" + new
		 * Double(Math.rint(point.getX())).intValue(); tileUrl += "&y=" + new
		 * Double(Math.rint(point.getY())).intValue(); tileUrl += "&z=" +
		 * zoomLevel; return tileUrl; } });
		 */
		return envLayer;
	}

  @Override
  public TileLayerLegend getLegend() {
    if (legend == null) {
      legend = new EnvLayerLegend(data);
    }
    return legend;
  }
}
