package org.rebioma.client.maps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.Point;

public class ModelEnvLayer extends AscTileLayer {

  private final double opacity;

  public ModelEnvLayer(String ascFileUrl) {
    this(ascFileUrl, 0.5);
  }

  public ModelEnvLayer(String ascFilePath, double opacity) {
    this.opacity = opacity;
    baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f=" + ascFilePath;
  }

  @Override
  public TileLayerLegend getLegend() {
    // TODO Auto-generated method stub
    return null;
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
