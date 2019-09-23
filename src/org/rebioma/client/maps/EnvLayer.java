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

import org.gwtopenmaps.openlayers.client.util.JSObject;
import org.rebioma.client.bean.AscData;

import com.google.gwt.core.client.GWT;

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
	 *            the asc data
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
		envLayer.imageOptions.setLayerOpacity(0.5);
		envLayer.baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f="
				+ data.getFileName();
		envLayer.imageOptions.setType("jpg");
		envLayer.imageOptions.setGetURL(getMyUrl(envLayer.baseUrl));
		return envLayer;
	}

	private static native JSObject getMyUrl(String baseurl) /*-{
		function get_my_url(bounds) {
			var res = this.map.getResolution();
			var x = Math.round((bounds.left - this.maxExtent.left)
					/ (res * this.tileSize.w));
			var y = Math.round((this.maxExtent.top - bounds.top)
					/ (res * this.tileSize.h));
			var z = this.map.getZoom();
			var limit = 100000000;
			var i = 0;
			var dir_x = x;
			var dir_y = y;
			for (i = z; i > 9; i--) {
				dir_x = (Math.floor(dir_x / 2.0));
				dir_y = (Math.floor(dir_y / 2.0));
			}
			var path = "9_" + dir_x + "_" + dir_y + "/jpg";
			if (y < 0 || y >= limit) {
				// return null;
			} else {
				limit = Math.pow(2, z);
				x = ((x % limit) + limit) % limit;
				y = ((y % limit) + limit) % limit;
				var url = baseurl + "&z=" + z + "&x=" + x + "&y=" + y;
				return url;
			}
		}
		return get_my_url;
	}-*/;

	@Override
	public TileLayerLegend getLegend() {
		if (legend == null) {
			legend = new EnvLayerLegend(data);
		}
		return legend;
	}
}
