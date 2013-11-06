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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The base class for widgets that support lazy loading and respond to history
 * events. Subclasses implement the <code>historyToken</code> method that
 * returns the full state of the view encoded in a single token.
 */
public abstract class View extends Composite implements
    ValueChangeHandler<String>, ResizeHandler {
  /**
   * View implementations are typically initialized by a method that returns a
   * ViewInfo. The ViewInfo provides information about the view and also
   * supports lazy loading it.
   */
  public static abstract class ViewInfo {

    private View instance;

    public View getView() {
      if (instance == null) {
        instance = constructView();
        instance.setViewInfo(this);
        History.addValueChangeHandler(instance);
        Window.addResizeHandler(instance);
        if (instance instanceof ComponentView) {
          ComponentView childView = (ComponentView) instance;
          childView.parent.addStateChangeListener(childView);
          childView.onStateChanged(ApplicationView.getCurrentState());
          childView.addStyleName(ComponentView.STYLE_NAME);
        }
      }
      return instance;
    }

    /**
     * Checks whether the view is already constructed.
     * 
     * @return true if this view is already constructed.
     */
    public boolean isViewConstrcuted() {
      return instance != null;
    }

    /**
     * Lazy loads the view.
     */
    protected abstract View constructView();

    protected abstract String getHisTokenName();

    protected abstract String getName();

  }

  /**
   * Defines the application state.
   * 
   */
  public enum ViewState {
    RESEARCHER("Researcher"), UNAUTHENTICATED("Guest"), ADMIN("Admin"), REVIEWER(
            "Reviewer"), SUPERADMIN("SuperAdmin");

    public static ViewState toViewState(String role) {
      ViewState viewState = ViewState.UNAUTHENTICATED;
      if (role.equalsIgnoreCase("admin")) {
        viewState = ADMIN;
      } else if (role.equalsIgnoreCase("reviewer")) {
        viewState = REVIEWER;
      } else if (role.equalsIgnoreCase("researcher")) {
    	  viewState = RESEARCHER;
      } else if (role.equalsIgnoreCase("superadmin")) {
    	  viewState = SUPERADMIN;
      }
      return viewState;
    }

    String name;

    ViewState(String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }

  public interface ViewStateChangeListener {
    public void onStateChanged(ViewState state);
  }

  /**
   * A simple helper class that exposes map URL parameters encoded in a history
   * token.
   */
  protected static abstract class HistoryState {
    protected String historyToken;

    protected static final int UNDEFINED = -1;
    /**
     * An advance search index for {@link AdvanceSearchView}.
     */
    protected static final String ASEARCH_INDEX = "index";
    /**
     * An advance search type (i.e BasisOfRecord} for {@link AdvanceSearchView}.
     */
    protected static final String ASEARCH_TYPE = "aType";
    /**
     * An advance search operator for {@link AdvanceSearchView}.
     */
    protected static final String AOPERATOR = "operator";
    /**
     * An advance search value for {@link AdvanceSearchView}.
     */
    protected static final String ASEARCH_VALUE = "value";

    protected static final LatLng DEFAULT_CENTER = LatLng.newInstance(-19, 47);
    /**
     * A maps from a {@link CheckBox} index in a view to its checked values
     * (checked/unchecked).
     */
    private Map<Integer, Boolean> checksMap = new HashMap<Integer, Boolean>();

    public void clearChecksState() {
      checksMap.clear();
    }

    /**
     * Gets the history token represent checked/unchecked states of a
     * {@link View}.
     * 
     * @param checked true if checked.
     * @return history token represent checked/unchecked states of a
     *         {@link View}.
     */
    public String getCheckedValues(boolean checked) {
      StringBuilder sb = new StringBuilder();
      for (Integer index : checksMap.keySet()) {
        boolean isChecked = checksMap.get(index);
        if (isChecked == checked) {
          sb.append(index + ",");
        }
      }
      if (sb.length() != 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
    }

    /**
     * Gets A {@link Map} of a {@link View} checks state.
     * 
     * @return
     */
    public Map<Integer, Boolean> getChecksMap() {
      return checksMap;
    }

    public abstract Object getHistoryParameters(UrlParam param);

    /**
     * Parses the Url for checks state history tokens and set it in
     * {@link #checksMap}.
     */
    public void parseCheckedUrl() {
      checksMap.clear();
      for (Integer index : intSetValues(UrlParam.CHECKED)) {
        checksMap.put(index, true);
      }
      for (Integer index : intSetValues(UrlParam.UNCHECKED)) {
        checksMap.put(index, false);
      }
    }

    /**
     * Sets the checks state of a view with a check state {@link Map}.
     * 
     * @param checksMap {@link Map} of checks state
     */
    public void setChecksMap(Map<Integer, Boolean> checksMap) {
      this.checksMap = checksMap;
    }

    /**
     * Calls this method before call any other methods to parse history state
     * token.
     * 
     * @param historyToken the history token URL.
     */
    public void setHistoryToken(String historyToken) {
      this.historyToken = historyToken;
    }

    /**
     * Gets a {@link Map} of a search fields values in the following mapping:
     * 
     * <br> {@link #ASEARCH_INDEX} to an integer of a advance search field index
     * </br>
     * 
     * <br> {@link #ASEARCH_INDEX} + {@link #ASEARCH_TYPE} to a string of a advance
     * search type (i.e BasisOfRecord} </br>
     * 
     * <br>{@link #ASEARCH_TYPE} + {@link #AOPERATOR} to a string of a advance
     * search operator: =, !=, like, notLike, >=, <= </br>
     * 
     * <br> {@link #ASEARCH_TYPE} + {@link #AOPERATOR} + {@link #ASEARCH_VALUE} to a
     * string of a advance search value </br>
     * 
     * @param asearchToken a token represent a search field in the following
     *          format: 0,BasisOfRecord|like, speci|unlike,pre |...
     * @return a {@link Map} of advance search field values
     */
    protected Map<String, List<String>> getAsearchFieldValues(
        String asearchToken) {
      Map<String, List<String>> asearchValues = new HashMap<String, List<String>>();
      String fieldValues[] = asearchToken.split("[\\|]");
      int valuesLen = fieldValues.length;
      if (valuesLen >= 2) {
        String searchType = fieldValues[1];
        String asearchIndex = fieldValues[0];
        addValue(asearchValues, ASEARCH_INDEX, asearchIndex);
        addValue(asearchValues, asearchIndex, searchType);
        for (int i = 2; i < valuesLen; i++) {
          String operatorValue[] = fieldValues[i].split(",");
          if (operatorValue.length < 2) {
            continue;
          }
          String operator = operatorValue[0];
          addValue(asearchValues, searchType + AOPERATOR, operator);
          String searchValues = "";
          for (int valuesIndex = 1; valuesIndex < operatorValue.length; valuesIndex++) {
            searchValues += operatorValue[valuesIndex];
          }
          addValue(asearchValues, searchType + operator + ASEARCH_VALUE,
              searchValues);
        }

      }
      return asearchValues;
    }

    /**
     * A helper method that returns a URL parameter value as an integer.
     * 
     * @param urlParam
     */
    protected int integerValue(UrlParam param) {
      try {
        return Integer.parseInt(stringValue(param));
      } catch (Exception e) {
        return UNDEFINED;
      }
    }

    /**
     * Gets a {@link Set} of Integer from a {@link UrlParam}.
     * 
     * @param urlParam {@link UrlParam} contains a set of integer.
     * @return a {@link Set} of Integer from a {@link UrlParam}.
     */
    protected Set<Integer> intSetValues(UrlParam urlParam) {
      Set<Integer> listValues = new HashSet<Integer>();

      String value = stringValue(urlParam);
      String values[] = value.split(",");
      for (String val : values) {
        try {
          listValues.add(Integer.parseInt(val));
        } catch (NumberFormatException ne) {

        }
      }

      return listValues;
    }

    /**
     * A helper method that returns a {@link LatLng} constructed from a
     * coordinate parameter that's encoded in a history token as:
     * 
     * <pre>
     * &quot;latitude,longitude&quot;
     * </pre>
     * 
     * If the coordinate information cannot be parsed, the DEFAULT_CENTER is
     * returned.
     * 
     * @param urlParam the {@link UrlParam} that contains coordinate information
     */
    protected LatLng latLngValue(UrlParam urlParam) {
      try {
        String[] point = stringValue(urlParam).split(",");
        double lat = Double.parseDouble(point[0]);
        double lng = Double.parseDouble(point[1]);
        return LatLng.newInstance(lat, lng);
      } catch (Exception e) {
        return DEFAULT_CENTER;
      }
    }

    /**
     * Gets all values of the given {@link UrlParam}.
     * 
     * @param urlParam
     * @return a {@link List} of Integer from a {@link UrlParam
     */
    protected List<String> listValue(UrlParam urlParam) {
      return ApplicationView.getHistoryTokenParamValues(historyToken, urlParam
          .lower());
    }

    /**
     * A helper method that returns a URL parameter as a string.
     * {@link UrlParam}.
     * 
     * @param urlParam the {@link UrlParam} in the history token
     */
    protected String stringValue(UrlParam urlParam) {
      String value = "";
      try {
        value = ApplicationView.getHistoryTokenParamValues(historyToken,
            urlParam.lower()).get(0);
      } catch (Exception e) {
      }
      return value;
    }

    private void addValue(Map<String, List<String>> map, String key,
        String value) {
      if (!map.containsKey(key)) {
        map.put(key, new ArrayList<String>());
      }
      if (!map.get(key).contains(value)) {
        map.get(key).add(value);
      }
    }
  }

  /**
   * Represents the available URL parameter names that describe the map view
   * state.
   */
  protected enum UrlParam {
    /**
     * A location address name for geocoder in {@link MapView}.
     */
    ADDRESS,
    /**
     * A token key for advance search in {@link AdvanceSearchView}.
     */
    ASEARCH,
    /**
     * Current zoom level in {@link MapView}.
     */
    ZOOM,
    /**
     * Current Center latlng location (i.e -19,47) in {@link MapView}.
     */
    CENTER,
    /**
     * Current environmental layer latlng lookup point (i,e 20,40) in
     * {@link MapView}.
     */
    LOOKUP_POINT,
    /**
     * Current environmental layer level in {@link MapView}.
     */
    LAYER,
    /**
     * Current selected {@link MapType} in {@link MapView}.
     */
    MAP_TYPE,
    /**
     * Url param history token for {@link ResultFilter} in
     * {@link OccurrenceView).
     */
    RF,
    /**
     * Current selected environmental layer lookup value (i.e 0.0 percent) in
     * {@link MapView}.
     */
    LOOKUP_VALUE,
    /**
     * Current records page in {@link MapView}.
     */
    PAGE,
    /**
     * Current selected search type in {@link OccurrenceView}.
     */
    TYPE,
    /**
     * Current lookup query in {@link OccurrenceView}.
     */
    QUERY,
    /**
     * Current selected tab (i.e occurrences) in {@link OccurrenceView}.
     */
    TAB,
    /**
     * Current checked {@link CheckBox} indexes(i.e 1,2,3,4) in {@link MapView}
     * or {@link ListView}.
     */
    CHECKED,
    /**
     * Current enchecked {@link CheckBox} indexes(i.e 0,5,6) in {@link MapView}
     * or {@link ListView}.
     */
    UNCHECKED,
    /**
     * Current selected view (i.e map).
     */
    VIEW,
    /**
     * Current checked/unchecked of checked All {@link CheckBox} (i.e
     * true/false).
     */
    CHECKEDALL,
    /**
     * Current selected private {@link RadioButton} in {@link UploadView}.
     */
    PRIVATE,
    /**
     * Current selected public {@link RadioButton} in {@link UploadView}.
     */
    PUBLIC,
    /**
     * Current selected modeling {@link CheckBox} in {@link UploadView}.
     */
    MODELING,
    /**
     * Current selected showEmail {@link CheckBox} in {@link UploadView}.
     */
    SHOW_EMAIL,
    /**
     * Current search checked {@link CheckBox} indexes(i.e 1,2,3,4) in
     * {@link CollaboratorVIew}
     */
    SEARCH_CHECKED,
    /**
     * Current search enchecked {@link CheckBox} indexes(i.e 0,5,6) in
     * {@link CollaboratorView}
     */
    SEARCH_UNCHECKED,
    /**
     * A token key for collaborator search in {@link CollaboratorView}.
     */
    COLLABORATOR_SEARCH_FIELDS,
    /**
     * A token key for collaborator search in {@link CollaboratorView} that
     * denotes what widgets have been loaded.
     */
    COLLABORATOR_WIDGETS_LOADED, ERROR_TYPE, ID,
    /**
     * Shared type box history token use in {@link OccurrenceView}
     */
    ST, LEFT_TAB, M_SEARCH, M_PAGE, SP;

    public String lower() {
      return name().toLowerCase();
    }
  }

  private static class GeocoderResult extends Composite {
    private final VerticalPanel vp = new VerticalPanel();

    public GeocoderResult(LatLng point, String address) {
      vp.add(new Label(address));
      vp.add(new Label(point.getToUrlValue(7)));
      initWidget(vp);
    }
  }

  protected static final AppConstants constants = GWT
      .create(AppConstants.class);
  /**
   * True if history button is clicked (i.e refresh, back, and forward browser
   * buttons).
   */
  protected boolean historyButtonClicked = false;

  protected List<ViewStateChangeListener> stateChangeListeners = null;
  private ViewInfo viewInfo;

  /**
   * Adds a new item to the {@link History} stack that represents the full state
   * of the view and optionally issues a new history event.
   * 
   * @param issueEvent true if a history event should be fired
   */
  public abstract void addHistoryItem(boolean issueEvent);

  public ViewInfo getViewInfo() {
    return viewInfo;
  }

  /**
   * Returns a single history token that represents the full state of the view.
   */
  public abstract String historyToken();

  /**
   * Called before the view is displayed.
   */
  public void onShow() {
  }

  public void onValueChange(ValueChangeEvent<String> event) {
    historyButtonClicked = true;
    String historyToken = event.getValue();
    if (!isMyView(historyToken)) {
      return;
    }
    handleOnValueChange(historyToken);
  }

  public void setViewInfo(ViewInfo viewInfo) {
    this.viewInfo = viewInfo;
  }

  /**
   * Adds a {@link ViewStateChangeListener} to listener on state change event.
   * 
   * Only parent view should use this.
   * 
   * @param listener
   */
  protected void addStateChangeListener(ViewStateChangeListener listener) {
    if (stateChangeListeners == null) {
      stateChangeListeners = new ArrayList<ViewStateChangeListener>();
    }
    stateChangeListeners.add(listener);
  }

  /**
   * Calls this method to fire all {@link #stateChangeListeners} when
   * {@link ViewState} is changes.
   * 
   * Only the parent view should call this method
   * 
   * @param viewState
   */
  protected void fireOnStateChange(ViewState viewState) {
    for (ViewStateChangeListener listener : stateChangeListeners) {
      listener.onStateChanged(viewState);
    }
  }

  protected abstract void handleOnValueChange(String historyToken);

  protected abstract boolean isMyView(String value);

  protected abstract void resetToDefaultState();

  /**
   * Override this method if this view have more than one children views.
   * 
   * @param view name to swtich to.
   * @param isLoadRecord TODO
   */
  protected void switchView(String view, boolean isLoadRecord) {

  }

}
