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

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class TileLayerLegend extends CustomControl {

  public interface LegendCallback {
    public void onLookup(LatLng point, String value);
  }

  protected DialogBox details;

  public TileLayerLegend() {
    super(new ControlPosition(ControlAnchor.TOP_RIGHT, 7, 90));
  }

  @Override
  public boolean isSelectable() {
    return true;
  }

  public abstract void lookupValue(LatLng point, LegendCallback callback);

  public abstract void setDisplay(LatLng point, String value);

  public void showDetails() {
    if (details == null) {
      details = getDetails();
    }
    details.center();
    details.show();
  }

  protected abstract DialogBox getDetails();

  protected abstract Widget getLegendWidget();

  @Override
  protected Widget initialize(MapWidget map) {
    return getLegendWidget();
  }
}
