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
package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceSummary;
import org.rebioma.client.bean.OccurrenceSummary.OccurrenceFieldItem;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.maps.OccurrenceMarker;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarkerList extends Composite {

  /**
   * A listener to listen on checked or unchecked or a checked box(es) in marker
   * list item.
   * 
   * @author tri
   * 
   */
  public interface CheckedSelectionListener {
    /**
     * Gets call when a check box(es) is checked or unchecked.
     * 
     * @param checksMap: the {@link Map} of marker item indexes to its checked
     *          value.
     */
    void onCheckedSelect(Map<Integer, Boolean> checksMap);
  }

  /**
   * A listener to listen on marker list action such as detail select, marker
   * select or species link select.
   * 
   * @author tri
   * 
   */
  public interface ItemSelectionListener {

    /**
     * Get called when an detail link is selected.
     * 
     * @param item the current {@link OccurrenceItem} is selected.
     */
    void onDetailSelect(OccurrenceItem item);

    /**
     * Gets called when an marker image is click.
     * 
     * @param item the current {@link OccurrenceItem} is selected.
     */
    void onMarkerSelect(OccurrenceItem item);

    /**
     * Gets called when an species link is click.
     * 
     * @param item the current {@link OccurrenceItem} is selected.
     */
    void onSpeciesSelect(OccurrenceItem item);
  }

  /**
   * An item represent a link to {@link Occurrence}
   * 
   * @author tri
   * 
   */
  public interface OccurrenceItem {
    /**
     * Gets the occurrence marker of for this item.
     * 
     * @return
     */
    OccurrenceMarker getOccurrenceMarker();

    void showDetail();
  }

  /**
   * A widget to play a marker represents in the map.
   * 
   * @author tri
   * 
   */
  private class MarkerItem extends Composite implements OccurrenceItem,
      ClickHandler {

    /**
     * The {@link OccurrenceMarker}
     */
    private final OccurrenceMarker occurrenceMarker;

    /**
     * The marker {@link Image} for the occurrence marker which is the same as
     * marker on the map.
     */
    private final Image markerImg;

    /**
     * The {@link HTML} link with species name.
     */
    private final HTML taxonimicValue;
    /**
     * The {@link HTML} link with string id of this occurrence item.
     */
    private final HTML idLink;

    /**
     * The {@link HTML} link for detail link.
     */
    private final HTML detailLink;

    /**
     * A {@link CheckBox} to show marker when checked and hide it when
     * unchecked.
     */
    private final CheckBox checkBox;

    private final HTML showLayersLink;

    /**
     * Initializes this MarkerItem with a {@link OccurrenceMarker}
     * 
     * @param occurrenceMarker
     */
    public MarkerItem(OccurrenceMarker occurrenceMarker) {
      this.occurrenceMarker = occurrenceMarker;
      Occurrence occurrence = occurrenceMarker.getOccurrence();
      OccurrenceFieldItem item = OccurrenceSummary.getDisplayField(occurrence);
      String displayText = item == null ? "----" : item.toString();
      taxonimicValue = new HTML(item == null ? "----" : item.toString());
      markerImg = new Image(OccurrenceMarker.getSpeciesMarkerUrlsMap().get(
          displayText));
      markerImg.addClickHandler(this);
      taxonimicValue.setStyleName("Taxonomic");
      idLink = new HTML(occurrence.getId() + "");
      idLink.addClickHandler(this);
      idLink.setStyleName("link");
      detailLink = new HTML(constants.Detail() + " &raquo;");
      detailLink.setStyleName("detaillink");
      //detailLink.addStyleName("detail-link");
      detailLink.addClickHandler(this);
      showLayersLink = new HTML(constants.ShowLayers());
      showLayersLink.setStyleName("showlayerslink");
      //showLayersLink.addStyleName("detail-link");
      showLayersLink.addClickHandler(this);
      checkBox = new CheckBox();
      checkBox.setValue(true);
      checkBox.addClickHandler(this);
      VerticalPanel vp = new VerticalPanel();
      HorizontalPanel hp = new HorizontalPanel();
      hp.add(idLink);
      hp.add(new HTML("&nbsp;-&nbsp;"));
      hp.add(showLayersLink);
      hp.add(new HTML("&nbsp;&nbsp;"));
      hp.add(detailLink);

      vp.add(hp);

      vp.add(taxonimicValue);
      HorizontalPanel mainHp = new HorizontalPanel();
      mainHp.add(checkBox);
      mainHp.add(markerImg);
      mainHp.add(vp);
      mainHp.setSpacing(5);
      initWidget(mainHp);
      mainHp.setStyleName(MARKER_ITEM_STYLE);
    }

    public OccurrenceMarker getOccurrenceMarker() {
      return occurrenceMarker;
    }

    /**
     * Checks whether the check box of this {@link MarkerItem} is checked.
     * 
     * @return if true if the {@link CheckBox} for this marker item is checked.
     */
    public boolean isChecked() {
      return checkBox.getValue();
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == detailLink) {
        fireOnDetailSelect(this);
      } else if (source == markerImg) {
        fireOnMarkerSelect(this);
      } else if (source == idLink) {
        fireOnSpeciesSelect(this);
      } else if (source == checkBox) {
        showOrHideMarker();
        fireOnCheckedSelect();
      } else if (source == showLayersLink) {
        LayerInfoPopup layerInfoPopup = LayerInfoPopup.getInstance();
        layerInfoPopup.loadLayersInfo(occurrenceMarker.getOccurrence());
      }

    }

    public void setChecked(boolean checked) {
      checkBox.setValue(checked);
      showOrHideMarker();
    }

    public void showDetail() {
      if (occurrenceListener != null) {
        occurrenceListener.onOccurrenceSelected(occurrenceMarker
            .getOccurrence());
      }

    }

    /**
     * Shows marker on the map if the check box is checked and hides it
     * otherwise.
     */
    private void showOrHideMarker() {
      occurrenceMarker.setVisible(checkBox.getValue());
    }

  }

  private static final String MARKER_ITEM_STYLE = "Marker-Item";
  private static final String DEFAULT_STYLE = "Marker-List";

  private final VerticalPanel itemsVp = new VerticalPanel();
  private final ListBox unmappableListBox = new ListBox();
  private final HTML unmappableMsg = new HTML();
  private final SimplePanel mainSp = new SimplePanel();

  private final List<ItemSelectionListener> selectionListeners = new ArrayList<ItemSelectionListener>();
  private final AppConstants constants = ApplicationView.getConstants();
  private OccurrenceListener occurrenceListener;
  private final List<CheckedSelectionListener> checkedSelectionListeners = new ArrayList<CheckedSelectionListener>();

  private final Map<String, Occurrence> unmappableOccurrenceMap = new HashMap<String, Occurrence>();

  public MarkerList() {
    mainSp.setWidget(itemsVp);
    initWidget(mainSp);
    mainSp.setStyleName(DEFAULT_STYLE);

    unmappableListBox.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent event) {
        String occurrenceKey = unmappableListBox.getItemText(unmappableListBox
            .getSelectedIndex());
        final Occurrence occurrence = unmappableOccurrenceMap
            .get(occurrenceKey);
        if (occurrence != null) {
          fireOnDetailSelect(new OccurrenceItem() {
            public OccurrenceMarker getOccurrenceMarker() {
              return null;
            }

            public void showDetail() {
              occurrenceListener.onOccurrenceSelected(occurrence);
            }

          });
        }
      }

    });
  }

  public void addCheckedSelectionLsitener(CheckedSelectionListener listener) {
    checkedSelectionListeners.add(listener);

  }

  public void addItem(OccurrenceMarker item) {
    itemsVp.add(new MarkerItem(item));
  }

  public void addItemSelectionListener(ItemSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void addUnmappableItems(List<Occurrence> occurrences) {
    unmappableListBox.clear();
    unmappableOccurrenceMap.clear();
    unmappableListBox.addItem(constants.Unmappble() + "...");
    for (Occurrence occurrence : occurrences) {
      OccurrenceFieldItem item = OccurrenceSummary.getDisplayField(occurrence);
      String itemText = occurrence.getId() + ": "
          + (item != null ? item.toString() : "----");
      unmappableListBox.addItem(itemText);
      unmappableOccurrenceMap.put(itemText, occurrence);
    }
    if (!occurrences.isEmpty()) {
      unmappableMsg.setHTML(constants.ThereAre() + " " + occurrences.size()
          + " " + constants.UnmappableOccurrences() + ":");
      VerticalPanel vp = new VerticalPanel();
      vp.add(unmappableMsg);
      vp.add(unmappableListBox);
      vp.setSpacing(2);
      itemsVp.insert(vp, 0);
    }
  }

  public void clear() {
    itemsVp.clear();
  }

  /**
   * Gets all {@link OccurrenceMarker} currently checked.
   * 
   * @return @{@link List} of {@link OccurrenceMarker} where marker item check
   *         box is checked.
   */
  public List<OccurrenceMarker> getCheckedMarkers() {
    int startIndex = (unmappableListBox.isAttached()) ? 1 : 0;
    List<OccurrenceMarker> occurrenceMarkers = new ArrayList<OccurrenceMarker>();
    for (int i = startIndex; i < itemsVp.getWidgetCount(); i++) {
      MarkerItem markerItem = (MarkerItem) itemsVp.getWidget(i);
      if (markerItem.isChecked()) {
        occurrenceMarkers.add(markerItem.getOccurrenceMarker());
      }
    }

    return occurrenceMarkers;
  }

  /**
   * Gets all {@link Occurrence} currently checked.
   * 
   * @return @{@link List} of {@link Occurrence} where marker item check box is
   *         checked.
   */
  public List<Occurrence> getCheckedOccurrence() {
    int startIndex = (unmappableListBox.isAttached()) ? 1 : 0;
    List<Occurrence> occurrences = new ArrayList<Occurrence>();
    for (int i = startIndex; i < itemsVp.getWidgetCount(); i++) {
      MarkerItem markerItem = (MarkerItem) itemsVp.getWidget(i);
      if (markerItem.isChecked()) {
        occurrences.add(markerItem.getOccurrenceMarker().getOccurrence());
      }
    }

    return occurrences;
  }

  public OccurrenceListener getOccurrenceListener() {
    return occurrenceListener;
  }

  /**
   * Checks all {@link MarkerItem} check box if checked is true and unchecked
   * all otherwise.
   * 
   * @param checked true to checked all, false to unchecked all
   */
  public void setCheckedAll(boolean checked) {
    int startIndex = (unmappableListBox.isAttached()) ? 1 : 0;
    for (int i = startIndex; i < itemsVp.getWidgetCount(); i++) {
      MarkerItem markerItem = (MarkerItem) itemsVp.getWidget(i);
      markerItem.setChecked(checked);
    }
    fireOnCheckedSelect();
  }

  /**
   * Sets all checks box value (checked or unchecked) base on a {@link Map}
   * indexes to its checked value.
   * 
   * @param checksMap: {@link Map} indexes to its checked value.
   */
  public void setCheckedFromMap(Map<Integer, Boolean> checksMap) {
    int startIndex = (unmappableListBox.isAttached()) ? 1 : 0;
    if (checksMap == null || checksMap.isEmpty()) {
      for (int index = startIndex; index < itemsVp.getWidgetCount(); index++) {
        MarkerItem item = (MarkerItem) itemsVp.getWidget(index);
        item.setChecked(true);
      }
    }
    for (Integer index : checksMap.keySet()) {
      try {
        MarkerItem item = (MarkerItem) itemsVp.getWidget(index + startIndex);
        item.setChecked(checksMap.get(index));
      } catch (IndexOutOfBoundsException e) {
        // do nothing
      }
    }
  }

  public void setOccurrenceListener(OccurrenceListener occurrenceListener) {
    this.occurrenceListener = occurrenceListener;
  }

  private void fireOnCheckedSelect() {
    int startIndex = (unmappableListBox.isAttached()) ? 1 : 0;
    Map<Integer, Boolean> checksMap = new HashMap<Integer, Boolean>();
    for (int i = startIndex; i < itemsVp.getWidgetCount(); i++) {
      MarkerItem item = (MarkerItem) itemsVp.getWidget(i);
      checksMap.put(i - startIndex, item.isChecked());
    }
    for (CheckedSelectionListener listener : checkedSelectionListeners) {
      listener.onCheckedSelect(checksMap);
    }
  }

  private void fireOnDetailSelect(OccurrenceItem item) {
    for (ItemSelectionListener listener : selectionListeners) {
      listener.onDetailSelect(item);
    }
  }

  private void fireOnMarkerSelect(OccurrenceItem item) {
    for (ItemSelectionListener listener : selectionListeners) {
      listener.onMarkerSelect(item);
    }
  }

  private void fireOnSpeciesSelect(OccurrenceItem item) {
    for (ItemSelectionListener listener : selectionListeners) {
      listener.onSpeciesSelect(item);
    }
  }

}
