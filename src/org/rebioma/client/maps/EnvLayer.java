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

import org.rebioma.client.bean.AscData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.Point;

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
        return new EnvLayer(data);
      }
    };
  }

  private static String dataSummary(AscData data) {
    return data.getEnvDataType() + " - " + data.getEnvDataSubtype() + " "
        + data.getYear();
  }

  private final AscData data;
  private final double opacity;

  private TileLayerLegend legend;

  public EnvLayer(AscData data) {
    this(data, .5);
  }

  public EnvLayer(AscData data, double opacity) {
    super();
    this.data = data;
    this.opacity = opacity;
    baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f=" + data.getFileName();
  }

  @Override
  public TileLayerLegend getLegend() {
    if (legend == null) {
      legend = new EnvLayerLegend(data);
    }
    return legend;
  }

  @Override
  public double getOpacity() {
    return opacity;
  }

  @Override
  public String getTileURL(Point tile, int zoomLevel) {
    String tileUrl = baseUrl;
    tileUrl += "&x=" + tile.getX();
    tileUrl += "&y=" + tile.getY();
    tileUrl += "&z=" + zoomLevel;
    return tileUrl;
  }

}
