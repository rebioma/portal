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
package org.rebioma.client.i18n;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A list box of supported locales. Selecting a locale reloads the client for
 * the selected locale.
 * 
 * @author eighty
 * 
 */
public class LocaleListBox extends Composite {

  /**
   * Gets the URL of the page, without a hash of query string.
   * 
   * @return the location of the page
   */
  private static native String getHostPageLocation() /*-{
    var s = $doc.location.href;

    // Pull off any hash.
    var i = s.indexOf('#');
    if (i != -1)
    s = s.substring(0, i);

    // Pull off any query string.
    i = s.indexOf('?');
    if (i != -1)
    s = s.substring(0, i);

    // Ensure a final slash if non-empty.
    return s;
  }-*/;

  ListBox localeBox = new ListBox();

  public static final String ENGLISH = "English";

  /**
   * Constructor that builds the list box of available locales.
   */
  public LocaleListBox() {
    localeBox = new ListBox();
    initWidget(localeBox);
    String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
    if (currentLocale.equals("default")) {
      currentLocale = "en";
    }
    String[] localeNames = LocaleInfo.getAvailableLocaleNames();
    for (String localeName : localeNames) {
      if (!localeName.equals("default")) {
        String nativeName = LocaleInfo.getLocaleNativeDisplayName(localeName);
        localeBox.addItem(nativeName, localeName);
        if (localeName.equals(currentLocale)) {
          localeBox.setSelectedIndex(localeBox.getItemCount() - 1);
        }
      }
    }
    localeBox.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        String localeName = localeBox.getValue(localeBox.getSelectedIndex());

        Window.open(getHostPageLocation() + "?locale=" + localeName
            + Window.Location.getHash(), "_self", "");
      }
    });
  }

  public String getCurrentLocale() {
    return localeBox.getItemText(localeBox.getSelectedIndex());
  }
}
