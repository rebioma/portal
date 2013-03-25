/*
 * Copyright 2008 University of California at Berkeley.
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
package org.rebioma.server.overlays;

/**
 * Convert between polar coordinates (lng, lat) describing a geographic location
 * in [-180,180]x[-90,90] and 2D Cartesian coordinates describing a point on a
 * square map [0,1]x[0,1] The point (-180, +89.xxx) corresponds to (0,0) The
 * point (+180, -89.xxx) corresponds to (1,1)
 */
public interface GeoProjection {
  public static class GMapsMercatorProjection implements GeoProjection {
    private GMapsMercatorProjection() {
    }

    public double toLat(double y) {
      double lat = Math.atan(Math.exp((1 - 2 * y) * Math.PI)) * 2 / Math.PI
          * 180.0 - 90.0;
      return lat;
    }

    public double toLng(double x) {
      double lng = x * 360.0 - 180.0;
      return lng;
    }

    public double toX(double lng) {
      double x = lng / 360.0 + 0.5;
      return x;
    }

    public double toY(double lat) {
      lat = lat / 180.0 * Math.PI;
      double y = Math.log(Math.tan(Math.PI / 4 + lat / 2));
      // clamp y to [-pi, pi] and translate to [0,1]
      // See Google Maps section at
      // http://en.wikipedia.org/wiki/Mercator_projection
      if (y > Math.PI)
        y = Math.PI;
      if (y < -Math.PI)
        y = -Math.PI;
      y = 0.5 - y / (2 * Math.PI);
      return y;
    }

  }

  public static final GeoProjection GMAPS_MERCATOR_PROJECTION = new GMapsMercatorProjection();

  double toLat(double y);

  double toLng(double x);

  double toX(double lng);

  double toY(double lat);
}
