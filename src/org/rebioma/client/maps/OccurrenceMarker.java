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

import com.google.gwt.maps.client.event.MarkerInfoWindowOpenHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;

public class OccurrenceMarker extends Marker {

  private static class OptionsManager {
    private static Map<String, String> speciesMarkerUrls = new HashMap<String, String>();
    private static int index = 0;
    private static Icon baseIcon;

    OptionsManager() {
      baseIcon = Icon.newInstance();
      baseIcon.setShadowURL("http://www.google.com/mapfiles/shadow50.png");
      baseIcon.setIconSize(Size.newInstance(20, 34));
      baseIcon.setShadowSize(Size.newInstance(37, 34));
      baseIcon.setIconAnchor(Point.newInstance(9, 34));
      baseIcon.setInfoWindowAnchor(Point.newInstance(9, 2));
    }

    public String getIconUrl(Occurrence occurrence) {
      return speciesMarkerUrls.get(occurrence.getAcceptedSpecies());
    }

    public void resetIcons() {
      speciesMarkerUrls.clear();
      index = 0;
    }

    MarkerOptions getOptions(Occurrence occurrence) {
      MarkerOptions options = MarkerOptions.newInstance(getIcon(occurrence));
      options.setTitle("ReBioMa ID: " + occurrence.getId());
      return options;
    }

    private Icon getIcon(Occurrence occurrence) {
      OccurrenceFieldItem fieldItem = OccurrenceSummary
              .getDisplayField(occurrence);
      String species = fieldItem == null ? "----" : fieldItem.toString();
      String url = speciesMarkerUrls.get(species);
      if (url == null) {
        final char letter = (char) ('A' + index++);
        url = "http://www.google.com/mapfiles/marker" + letter + ".png";
        speciesMarkerUrls.put(species, url);
      }
      Icon icon = Icon.newInstance(baseIcon);
      icon.setImageURL(url);
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

  private static LatLng getPoint(Occurrence occurrence) {
    double latitude = Double.parseDouble(occurrence.getDecimalLatitude());
    double longitude = Double.parseDouble(occurrence.getDecimalLongitude());
    return LatLng.newInstance(latitude, longitude);
  }

  private final Occurrence occurrence;

  public OccurrenceMarker(Occurrence occurrence) {
    super(getPoint(occurrence), optionsManager.getOptions(occurrence));
    this.occurrence = occurrence;
    super.addMarkerInfoWindowOpenHandler(new MarkerInfoWindowOpenHandler() {
      public void onInfoWindowOpen(MarkerInfoWindowOpenEvent event) {
        // TODO Auto-generated method stub

      }

    });
  }

  public String getIconUrl() {
    return optionsManager.getIconUrl(occurrence);
  }

  public Occurrence getOccurrence() {
    return occurrence;
  }

}
