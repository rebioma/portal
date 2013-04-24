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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class GeocoderControl extends CustomControl {

  public static class ArrowMarker extends Marker {

    private static Icon icon;

    private static MarkerOptions getOptions(LatLng point, String address) {
      icon = Icon.newInstance();
      icon.setShadowURL("http://www.google.com/mapfiles/arrowshadow.png");
      icon.setImageURL("http://www.google.com/mapfiles/arrow.png");
      icon.setIconSize(Size.newInstance(39, 34));
      icon.setShadowSize(Size.newInstance(39, 34));
      icon.setIconAnchor(Point.newInstance(9, 34));
      icon.setInfoWindowAnchor(Point.newInstance(9, 2));
      MarkerOptions options = MarkerOptions.newInstance(icon);
      options.setTitle(address);
      return options;
    }

    public ArrowMarker(LatLng point, String address) {
      super(point, getOptions(point, address));

    }

  }

  public static ArrowMarker createMarker(LatLng point, String address) {
    return new ArrowMarker(point, address);
  }

  private Map<String, LatLng> cache;
  private final Geocoder geocoder = new Geocoder();
  private final TextBox addressBox = new TextBox();

  private final LatLngCallback callback;
  private Button submit;

  private final Panel panel = new FlowPanel();

  public GeocoderControl(final LatLngCallback callback) {
    super(new ControlPosition(ControlAnchor.TOP_RIGHT, 7, 30));
    this.callback = callback;
  }

  public String getAddress() {
    return addressBox.getText();
  }

  public LatLng getCachedPoint(String address) {
    if (cache == null) {
      cache = new HashMap<String, LatLng>();
    }
    return cache.get(address);
  }

  public Widget getControlWidget() {
    return panel;
  }

  @Override
  public boolean isSelectable() {
    return true;
  }

  public void lookupAddress(final String address) {
    addressBox.setText(address);
    LatLng point = getCachedPoint(addressBox.getText());
    if (point != null) {
      callback.onSuccess(point);

    } else {
      geocoder.getLatLng(address, callback);
    }
  }

  public void lookupAddress(final String address, final LatLngCallback callback) {
    addressBox.setText(address);
    LatLng point = getCachedPoint(addressBox.getText());
    if (point != null) {
      callback.onSuccess(point);

    } else {
      geocoder.getLatLng(address, callback);
    }
  }

  public void setAddress(String address) {
    addressBox.setText(address);
  }

  @Override
  protected Widget initialize(final MapWidget map) {
    final FormPanel form = new FormPanel();
    form.setAction("#");
    Panel formElements = new FlowPanel();
    addressBox.setVisibleLength(26);
    addressBox.selectAll();
    formElements.add(addressBox);
    submit = new Button("Find place");
    submit.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        String address = getAddress();
        if (address == null || address.trim().length() == 0) {
          Window.alert("Please enter an address.");
          return;
        }
        form.submit();
      }
    });
    formElements.add(submit);
    formElements.add(new HTML("&nbsp;"));
    form.add(formElements);
    form.addSubmitHandler(new SubmitHandler() {

      public void onSubmit(SubmitEvent event) {
        lookupAddress(addressBox.getText(), callback);
        event.cancel();
      }
    });
    panel.add(form);
    return panel;
  }
}
