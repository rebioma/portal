package org.rebioma.client.maps;

import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.google.gwt.core.client.GWT;

public class ModelEnvLayer extends AscTileLayer {

	protected ModelEnvLayer(String ascFileUrl) {
		this(ascFileUrl, 0.5);
	}

	public static ModelEnvLayer newInstance(String ascFileUrl) {
		return new ModelEnvLayer(ascFileUrl);
	}

	public ModelEnvLayer(String ascFilePath, double opacity) {
		super();
		this.imageOptions.setLayerOpacity(0.5);
		baseUrl = GWT.getModuleBaseURL() + "ascOverlay?f=" + ascFilePath;
		imageOptions.setType("jpg");
		imageOptions.setGetURL(getMyUrl(baseUrl));
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
		// TODO Auto-generated method stub
		return null;
	}

}
