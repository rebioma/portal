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

public class GMapsUtil {
  public static double east(int x, int y, int z) {
    double tileSize = Math.pow(2, -z);
    double x_ = (x + 1) * tileSize;
    return GeoProjection.GMAPS_MERCATOR_PROJECTION.toLng(x_);
  }

  public static double north(int x, int y, int z) {
    double tileSize = Math.pow(2, -z);
    double y_ = y * tileSize;
    return GeoProjection.GMAPS_MERCATOR_PROJECTION.toLat(y_);
  }

  public static double south(int x, int y, int z) {
    double tileSize = Math.pow(2, -z);
    double y_ = (y + 1) * tileSize;
    return GeoProjection.GMAPS_MERCATOR_PROJECTION.toLat(y_);
  }

  public static double west(int x, int y, int z) {
    double tileSize = Math.pow(2, -z);
    double x_ = x * tileSize;
    return GeoProjection.GMAPS_MERCATOR_PROJECTION.toLng(x_);
  }
}
