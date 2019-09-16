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

import org.gwtopenmaps.openlayers.client.Icon;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.layer.Markers;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceSummary;
import org.rebioma.client.bean.OccurrenceSummary.OccurrenceFieldItem;

public class OccurrenceMarkerManager{
	private static Marker marker;
	private Markers markers;
	public static class OptionsManager {
		private static Map<String, String> speciesMarkerUrls = new HashMap<String, String>();
		private static int index = 0;

		public OptionsManager() {
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

		public Markers getOptions(Occurrence occurrence) {
			Markers options =new Markers("ReBioMa ID: " + occurrence.getId());
			return options;
		}

		public Icon getIcon(Occurrence occurrence) {
			Markers options = new Markers("");
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
			//RORO MarkerImage iconImage = MarkerImage.newInstance(url); 

			Icon icon = new Icon(url, new Size(20, 34));
			//return icon;
//			iconImage.setSize(Size.newInstance(20, 34));
//			iconImage.setAnchor(Point.newInstance(9, 34));
//			iconImage.setOrigin(Point.newInstance(9, 2));
			//RORO options.setIcon(iconImage);
			//MarkerImage shadow = MarkerImage.newInstance("http://www.google.com/mapfiles/shadow50.png");
			//shadow.setSize(Size.newInstance(37, 34));
			//options.setShadow(shadow);
			return icon;

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

	public LonLat getPoint(Occurrence occurrence) {
		if(occurrence.getDecimalLatitude() != null && occurrence.getDecimalLongitude() != null){
			double latitude = Double.parseDouble(occurrence.getDecimalLatitude());
			double longitude = Double.parseDouble(occurrence.getDecimalLongitude());
			return new LonLat(longitude,latitude);
		}else{
			return null;
		}
		
	}

	private final Occurrence occurrence;

	protected OccurrenceMarkerManager(Occurrence occurrence) {
		this.occurrence = occurrence;
		Markers options = optionsManager.getOptions(occurrence);
		LonLat point = getPoint(occurrence);
		if(point == null){
			point = new LonLat(0, 0);
		}
		//options.setPosition(point);
		marker = new Marker(point,optionsManager.getIcon(occurrence));
		options.addMarker(marker);
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
	
	public Markers getMarkers(){

		return markers;
	}
	public Marker getMarker(){

		return marker;
	}

}
