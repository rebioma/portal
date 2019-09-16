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
import org.gwtopenmaps.openlayers.client.layer.ImageOptions;
import org.gwtopenmaps.openlayers.client.layer.XYZ;
import org.gwtopenmaps.openlayers.client.layer.XYZOptions;

/**
 * An abstract class that represents ASC tile layers.
 */
public abstract class AscTileLayer {
	
	//protected final ImageMapTypeOptions imageMapTypeOptions;
	protected final ImageOptions imageOptions;

  /**
   * Allows {@link AscTileLayer} objects to be lazy loaded.
   */
  public abstract static class LayerInfo {
    private AscTileLayer instance;

    public AscTileLayer getInstance() {
      if (instance == null) {
        instance = get();
      }
      return instance;
    }

    public abstract String getName();

    protected abstract AscTileLayer get();
  }
  
  private int mapIndex;

  protected String baseUrl;

 //RORO protected ImageMapType overlay;
  protected Image overlay;
  public AscTileLayer() {
    super();
   //RORO  imageMapTypeOptions = ImageMapTypeOptions.newInstance();
    imageOptions = new ImageOptions();
    imageOptions.setIsBaseLayer(false);
   /* Bounds b=new Bounds(42.30124, -26.5823, 51.14843, -11.36225);
	b.transform(new Projection("EPSG:4326"), new Projection("EPSG:900913"));
    imageOptions.setMaxExtent(b);
    imageOptions.setMaxResolution(6);
    imageOptions.setNumZoomLevels(10);*/
    imageOptions.setDisplayInLayerSwitcher(false);
  }

 /* public ImageMapType asOverlay() {
    if (overlay == null) {
      overlay = ImageMapType.newInstance(imageMapTypeOptions);
    }
    return overlay;
  }*/
  public Image asOverlay() {
	    if (overlay == null) {
	    	Bounds b=new Bounds(42.30124, -26.5823, 51.14843, -11.36225);
	    	b.transform(new Projection("EPSG:4326"), new Projection("EPSG:900913"));
	    	overlay = new Image("", baseUrl, b, new Size(256,256), imageOptions);
	    		  }
	    return overlay;
	  }
  public abstract TileLayerLegend getLegend();
  
  public int getMapIndex(){
	  return mapIndex;
  }
  /**
   * On memorise l'index de l'imageMapType quand il est ajouté au map
   * @param idx
   */
  public void setMapIndex(int idx){
	  this.mapIndex = idx;
  }
  
 /* public ImageMapTypeOptions getImageMapTypeOptions(){
	  return imageMapTypeOptions;
  }*/
  public ImageOptions getImageMapTypeOptions(){
	  return imageOptions;
  }
}
