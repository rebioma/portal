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

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.user.client.ui.Widget;

public class GeocoderInfoWindowContent extends InfoWindowContent {

  public interface GeocoderResult {

  }

  private static InfoWindowTab[] getInfoTabs(Widget[] widgets) {
    InfoWindowTab[] tabs = new InfoWindowTab[widgets.length];
    for (int i = 0; i < widgets.length; i++) {
      tabs[i] = new InfoWindowTab("foo", widgets[i]);
    }
    return tabs;
  }

  public GeocoderInfoWindowContent(Widget[] widgets, int selectedTab) {
    super(getInfoTabs(widgets), selectedTab);

  }

}
