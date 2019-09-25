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

import org.gwtopenmaps.openlayers.client.Icon;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.layer.Markers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

public class GeocoderControl extends FlowPanel {

	private static Markers getOptions(LonLat point, String address) {
		Icon icon = new Icon("http://www.google.com/mapfiles/arrow.png",new Size(24,24));
//		icon.setSize(Size.newInstance(39, 34));
//		icon.setAnchor(Point.newInstance(9, 34));
//		icon.setOrigin(Point.newInstance(9, 2));
		Icon shadow = new Icon("http://www.google.com/mapfiles/arrowshadow.png",new Size(24,24));
//		shadow.setSize(Size.newInstance(39, 34));
		Markers options = new Markers(address);
		Marker marker=new Marker(point,icon);
		options.setZIndex(0);//RORO.setClickable(true);
		options.addMarker(marker);
		options.addMarker(new Marker(point,shadow));
		return options;
	}

	public static Markers createMarker(LonLat point, String address) {
		Markers options = getOptions(point, address);
		Marker marker = new Marker(point,null);
		options.addMarker(marker);
		return options;
	}

	// private Map<String, LatLng> cache;
	/*RORO private final Geocoder geocoder;
	private final GeocoderRequest geocoderRequest;
	private final GeocoderRequestHandler geocoderRequestHandler;
	private final TextBox addressBox = new TextBox();
*/
	private Button submit;

/*	RORO public GeocoderControl(GeocoderRequestHandler requestHandler) {
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
	}*/

	/* RORO public String getAddress() {
		return addressBox.getText();
	}*/

	// public LatLng getCachedPoint(String address) {
	// if (cache == null) {
	// cache = new HashMap<String, LatLng>();
	// }
	// return cache.get(address);
	// }

	public void lookupAddress(final String address) {
		//RORO lookupAddress(address, this.geocoderRequestHandler);
	}

	/*RORO public void lookupAddress(final String address,
			GeocoderRequestHandler requestHandler) {
		geocoderRequest.setAddress(address);
		geocoder.geocode(geocoderRequest, requestHandler);
	}*/

	public void setAddress(String address) {
		//RORO addressBox.setText(address);
	}
}
