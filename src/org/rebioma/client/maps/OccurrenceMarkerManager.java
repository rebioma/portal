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

import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceSummary;
import org.rebioma.client.bean.OccurrenceSummary.OccurrenceFieldItem;

import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.Point;
import com.google.gwt.maps.client.base.Size;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerImage;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class OccurrenceMarkerManager{
	private Marker marker;
	private static class OptionsManager {
		private static Map<String, String> speciesMarkerUrls = new HashMap<String, String>();
		private static int index = 0;

		OptionsManager() {
			/*baseIcon = Icon.newInstance();
			baseIcon
					.setShadowURL("http://www.google.com/mapfiles/shadow50.png");
			baseIcon.setIconSize(Size.newInstance(20, 34));
			baseIcon.setShadowSize(Size.newInstance(37, 34));
			baseIcon.setIconAnchor(Point.newInstance(9, 34));
			baseIcon.setInfoWindowAnchor(Point.newInstance(9, 2));*/
		}

		public String getIconUrl(Occurrence occurrence) {
			return speciesMarkerUrls.get(occurrence.getAcceptedSpecies());
		}

		public void resetIcons() {
			speciesMarkerUrls.clear();
			index = 0;
		}

		MarkerOptions getOptions(Occurrence occurrence) {
			MarkerOptions options = getIcon(occurrence);
			options.setTitle("ReBioMa ID: " + occurrence.getId());
			return options;
		}

		private MarkerOptions getIcon(Occurrence occurrence) {
			MarkerOptions options = MarkerOptions.newInstance();
			OccurrenceFieldItem fieldItem = OccurrenceSummary
					.getDisplayField(occurrence);
			String species = fieldItem == null ? "----" : fieldItem.toString();
			String url = speciesMarkerUrls.get(species);
			if (url == null) {
				final char letter = (char) ('A' + index++);
				// url = "http://www.google.com/mapfiles/marker" + letter +
				// ".png";
				// documentation =>
				// https://developers.google.com/chart/image/docs/gallery/dynamic_icons#plain_pin
				String iconColor;
				String letterColor = "000000";
				/*
				 * - reliable => Occurrence.reviewed = 1 AND validated = 1 -
				 * Invalidated => Occurrence.validated = 0 - Questionable =>
				 * Occurrence.reviewed = 0 - awaiting review =>
				 * Occurrence.reviewed = NULL AND Occurrence.validated = 1
				 */
				if (Boolean.TRUE.equals(occurrence.isValidated())
						&& Boolean.TRUE.equals(occurrence.getReviewed())) {// reliable
					iconColor = "cefbc5";// vert
				} else if (Boolean.FALSE.equals(occurrence.getReviewed())) {// Questionnable
					iconColor = "bdc3fb";// blue
				} else if (Boolean.FALSE.equals(occurrence.isValidated())) {// invalide
					iconColor = "fa93ab";// rouge
				} else {
					// En attente de validation
					iconColor = "fbecc5";
					;// "D9D919";//jaune
				}
				url = "http://chart.googleapis.com/chart?chst=d_map_pin_letter&chld="
						+ letter + "|" + iconColor + "|" + letterColor;
//				url = "http://www.google.com/mapfiles/marker" + letter + ".png";
				speciesMarkerUrls.put(species, url);
			}
			MarkerImage iconImage = MarkerImage.newInstance(url); 
//			iconImage.setSize(Size.newInstance(20, 34));
//			iconImage.setAnchor(Point.newInstance(9, 34));
//			iconImage.setOrigin(Point.newInstance(9, 2));
			options.setIcon(iconImage);
			//MarkerImage shadow = MarkerImage.newInstance("http://www.google.com/mapfiles/shadow50.png");
			//shadow.setSize(Size.newInstance(37, 34));
			//options.setShadow(shadow);
			return options;
		}
	}

	private static OptionsManager optionsManager = new OptionsManager();

	public static Map<String, String> getSpeciesMarkerUrlsMap() {
		return OptionsManager.speciesMarkerUrls;
	}

	public static boolean isMappable(Occurrence occurrence) {
		try {
			Double.parseDouble(occurrence.getDecimalLatitude());
			Double.parseDouble(occurrence.getDecimalLongitude());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void resetIcons() {
		optionsManager.resetIcons();
	}

	public static LatLng getPoint(Occurrence occurrence) {
		if(occurrence.getDecimalLatitude() != null && occurrence.getDecimalLongitude() != null){
			double latitude = Double.parseDouble(occurrence.getDecimalLatitude());
			double longitude = Double.parseDouble(occurrence.getDecimalLongitude());
			return LatLng.newInstance(latitude, longitude);
		}else{
			return null;
		}
		
	}

	private final Occurrence occurrence;

	protected OccurrenceMarkerManager(Occurrence occurrence) {
		this.occurrence = occurrence;
		MarkerOptions options = optionsManager.getOptions(occurrence);
		LatLng point = getPoint(occurrence);
		if(point == null){
			point = LatLng.newInstance(0, 0);
		}
		options.setPosition(point);
		marker = Marker.newInstance(options);
//		super.addMarkerInfoWindowOpenHandler(new MarkerInfoWindowOpenHandler() {
//			public void onInfoWindowOpen(MarkerInfoWindowOpenEvent event) {
//				// TODO Auto-generated method stub
//
//			}
//
//		});
	}
	
	public static OccurrenceMarkerManager newInstance(Occurrence occurrence){
		return new OccurrenceMarkerManager(occurrence);
	}

	public String getIconUrl() {
		return optionsManager.getIconUrl(occurrence);
	}

	public Occurrence getOccurrence() {
		return occurrence;
	}
	
	public Marker getMarker(){
		return marker;
	}

}
