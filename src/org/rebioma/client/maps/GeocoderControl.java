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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.Point;
import com.google.gwt.maps.client.base.Size;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerImage;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.services.Geocoder;
import com.google.gwt.maps.client.services.GeocoderRequest;
import com.google.gwt.maps.client.services.GeocoderRequestHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

public class GeocoderControl extends FlowPanel {

	private static MarkerOptions getOptions(LatLng point, String address) {
		MarkerImage icon = MarkerImage
				.newInstance("http://www.google.com/mapfiles/arrow.png");
//		icon.setSize(Size.newInstance(39, 34));
//		icon.setAnchor(Point.newInstance(9, 34));
//		icon.setOrigin(Point.newInstance(9, 2));
		MarkerImage shadow = MarkerImage
				.newInstance("http://www.google.com/mapfiles/arrowshadow.png");
//		shadow.setSize(Size.newInstance(39, 34));
		MarkerOptions options = MarkerOptions.newInstance();
		options.setClickable(true);
		options.setDraggable(false);
		options.setIcon(icon);
		options.setShadow(shadow);
		options.setTitle(address);
		return options;
	}

	public static Marker createMarker(LatLng point, String address) {
		MarkerOptions options = getOptions(point, address);
		Marker marker = Marker.newInstance(options);
		marker.setPosition(point);
		return marker;
	}

	// private Map<String, LatLng> cache;
	private final Geocoder geocoder;
	private final GeocoderRequest geocoderRequest;
	private final GeocoderRequestHandler geocoderRequestHandler;
	private final TextBox addressBox = new TextBox();

	private Button submit;

	public GeocoderControl(GeocoderRequestHandler requestHandler) {
		super();
		this.geocoderRequestHandler = requestHandler;
		this.geocoderRequest = GeocoderRequest.newInstance();
		this.geocoder = Geocoder.newInstance();
		final FormPanel form = new FormPanel();
		form.setAction("#");
		Panel formElements = new FlowPanel();
		addressBox.setVisibleLength(26);
		addressBox.selectAll();
		formElements.add(addressBox);
		submit = new Button("Find place");
		formElements.add(submit);
		
		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String address = getAddress();
				if (address == null || address.trim().length() == 0) {
					Window.alert("Please enter an address.");
					return;
				}
				form.submit();
			}
		});
//		formElements.add(new HTML("&nbsp;"));
		form.add(formElements);
		form.addSubmitHandler(new SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				lookupAddress(addressBox.getText());
				event.cancel();
			}
		});
		add(form);
	}

	public String getAddress() {
		return addressBox.getText();
	}

	// public LatLng getCachedPoint(String address) {
	// if (cache == null) {
	// cache = new HashMap<String, LatLng>();
	// }
	// return cache.get(address);
	// }

	public void lookupAddress(final String address) {
		lookupAddress(address, this.geocoderRequestHandler);
	}

	public void lookupAddress(final String address,
			GeocoderRequestHandler requestHandler) {
		geocoderRequest.setAddress(address);
		geocoder.geocode(geocoderRequest, requestHandler);
	}

	public void setAddress(String address) {
		addressBox.setText(address);
	}
}
