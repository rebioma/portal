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

import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.DetailView.FieldConstants;
import org.rebioma.client.MarkerList.CheckedSelectionListener;
import org.rebioma.client.MarkerList.ItemSelectionListener;
import org.rebioma.client.MarkerList.OccurrenceItem;
import org.rebioma.client.OccurrenceQuery.DataRequestListener;
import org.rebioma.client.PagerWidget.PageClickListener;
import org.rebioma.client.bean.AscModel;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceSummary;
import org.rebioma.client.maps.AscTileLayer.LayerInfo;
import org.rebioma.client.maps.EnvLayerSelector;
import org.rebioma.client.maps.GeocoderControl;
import org.rebioma.client.maps.GeocoderControl.ArrowMarker;
import org.rebioma.client.maps.HideControl;
import org.rebioma.client.maps.ModelEnvLayer;
import org.rebioma.client.maps.ModelingControl;
import org.rebioma.client.maps.OccurrenceMarker;
import org.rebioma.client.maps.TileLayerLegend;
import org.rebioma.client.maps.TileLayerLegend.LegendCallback;
import org.rebioma.client.maps.TileLayerSelector;
import org.rebioma.client.maps.TileLayerSelector.TileLayerCallback;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.control.MenuMapTypeControl;
import com.google.gwt.maps.client.control.ScaleControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapTypeChangedHandler;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerRemoveHandler;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A type of view that shows a Google map displaying pageable occurrence data
 * represented as markers.
 */
public class MapView extends ComponentView implements CheckedSelectionListener,
    DataRequestListener, PageClickListener, PageListener<Occurrence>,
    TileLayerCallback, ItemSelectionListener, SelectionHandler<Integer>, OccurrencePageSizeChangeHandler {

  /**
   * Manage history states of map View.
   */
  public class MapState extends HistoryState {

    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case LAYER:
      case LEFT_TAB:
      case ZOOM:
      case M_PAGE:
        return integerValue(param);
      case LOOKUP_POINT:
      case CENTER:
        return latLngValue(param);
      case LOOKUP_VALUE:
      case ADDRESS:
      case VIEW:
        if (isDefaultView && historyToken.trim().equals("")) {
          return MAP;
        }
      case M_SEARCH:
      case MAP_TYPE:
        return stringValue(param);
      }
      return "";
    }

  }

  private class DownloadAllClimatesCommand implements Command {
    private final AscModel ascModel;

    public DownloadAllClimatesCommand(AscModel ascModel) {
      this.ascModel = ascModel;
    }

    @Override
    public void execute() {
      Window.open(GWT.getHostPageBaseURL() + "ModelOutput/"
          + ascModel.getModelLocation() + ".zip", "_blank", "");

    }
  }

  private class DownloadClimateCommand implements Command {
    private final String climateName;
    private final AscModel ascModel;

    public DownloadClimateCommand(String climateName, AscModel ascModel) {
      this.climateName = climateName;
      this.ascModel = ascModel;
    }

    public void execute() {
      Window.open(GWT.getHostPageBaseURL() + "ModelOutput/"
          + ascModel.getModelLocation() + "/" + climateName + ".zip", "_blank",
          "");
    }
  }

  private static class GeocoderResult extends Composite {
    private final VerticalPanel vp = new VerticalPanel();

    public GeocoderResult(LatLng point, String address) {
      vp.add(new Label(address));
      vp.setStyleName("address");
      vp.add(new Label(point.toUrlValue(7)));
      vp.setStyleName("latlong");
      initWidget(vp);
    }
  }

  private class ModelItem extends TreeItem implements ClickHandler,
      AsyncCallback<List<String>>, Command, ValueChangeHandler<Boolean> {
    private Command clickCommand;
    private Command downloadCommand;
    private Label itemLabel;
    private Label downloadLink;
    private CheckBox checkbox;;
    private final AscModel ascModel;
    private ModelEnvLayer envLayer;

    ModelItem(String text, AscModel ascModel, boolean addedCheckBox,
        Command downloadCommand) {
      super();
      this.ascModel = ascModel;
      if (downloadCommand == null) {
        // itemLabel = new Label(text);
        setHTML(text);
      } else {
        HorizontalPanel panel = new HorizontalPanel();
        this.downloadCommand = downloadCommand;
        itemLabel = new Label(text);
        downloadLink = new Label(constants.Download());
        if (addedCheckBox) {
          checkbox = new CheckBox();
          panel.add(checkbox);
          checkbox.addValueChangeHandler(this);
          checkbox.addStyleName("modeling_check_box");
          envLayer = new ModelEnvLayer("ModelOutput/"
              + ascModel.getModelLocation() + "/" + itemLabel.getText() + "/"
              + ascModel.getModelLocation() + ".asc");
        }
        panel.add(itemLabel);
        panel.add(downloadLink);
        panel.setSpacing(0);
        setWidget(panel);
        downloadLink.addStyleName("link");
        downloadLink.addStyleName("download_link");
        downloadLink.addClickHandler(this);
        itemLabel.addClickHandler(this);
        // itemLabel.addStyleName("link");
      }

    }

    public void clearOverLay() {
      for (int i = 0; i < getChildCount(); i++) {
        TreeItem treeItem = getChild(i);
        if (treeItem instanceof ModelItem) {

          ((ModelItem) treeItem).clearThisOverlay();
        }
      }

    }

    public void execute() {
      Window.open(GWT.getHostPageBaseURL() + "ModelOutput/"
          + ascModel.getModelLocation() + "/" + itemLabel.getText() + "/"
          + ascModel.getIndexFile(), "_blank", null);

    }

    /**
     * @return the clickCommand
     */
    public Command getClickCommand() {
      return clickCommand;
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == itemLabel) {
        if (clickCommand != null) {
          clickCommand.execute();
        }
      } else if (source == downloadLink) {
        if (downloadCommand != null) {
          downloadCommand.execute();
        }
      }
    }

    public void onFailure(Throwable caught) {
      Window.alert(caught.getMessage());
      GWT.log(caught.getMessage(), caught);

    }

    public void onSuccess(List<String> result) {
      removeItems();
      for (String climate : result) {
        ModelItem item = new ModelItem(climate, ascModel, true,
            new DownloadClimateCommand(climate, ascModel));
        item.setClickCommand(item);
        addItem(item);
      }
    }

    public void onValueChange(ValueChangeEvent<Boolean> event) {
      Object source = event.getSource();
      if (source == checkbox) {
        if (event.getValue()) {
          map.addOverlay(envLayer.asOverlay());
        } else {
          map.removeOverlay(envLayer.asOverlay());
        }
      }

    }

    public void open() {
      TreeItem child = getChild(0);
      if (itemLabel == null || child.getText().equals(constants.Loading())) {
        DataSwitch.get().getModelClimateEras(ascModel.getModelLocation(), this);
      }
    }

    /**
     * @param clickCommand the clickCommand to set
     */
    public void setClickCommand(Command clickCommand) {
      this.clickCommand = clickCommand;
      if (clickCommand == null) {
        itemLabel.removeStyleName("link");
      } else {
        itemLabel.addStyleName("link");
      }
    }

    private void clearThisOverlay() {
      if (envLayer != null) {
        map.removeOverlay(envLayer.asOverlay());
      }

    }

  }

  private class ModelSearch extends Composite implements KeyUpHandler,
      OpenHandler<TreeItem>, AsyncCallback<AscModelResult>, ClickHandler {
    private final TextBox searchBox;
    private final Tree resultTree;
    private final HTML next;
    private final Label resultInfo;
    private final HTML previous;
    private int start = 0;
    private int limit = 10;
    private String currentSearchTerm;
    private AscModelResult currentResult;

    public ModelSearch() {
      Label searchLabel = new Label(constants.ModelSearch());
      VerticalPanel mainPanel = new VerticalPanel();
      HorizontalPanel searchPanel = new HorizontalPanel();
      HorizontalPanel pagePanel = new HorizontalPanel();
      searchBox = new TextBox();
      resultTree = new Tree();
      next = new HTML("&nbsp;&#x203A;");
      previous = new HTML("&#x2039;&nbsp;");
      resultInfo = new Label();
      next.setVisible(false);
      previous.setVisible(false);
      pagePanel.setSpacing(5);
      pagePanel.add(previous);
      pagePanel.add(resultInfo);
      pagePanel.add(next);
      searchPanel.add(searchLabel);
      searchPanel.add(searchBox);
      mainPanel.add(searchPanel);
      mainPanel.add(pagePanel);
      mainPanel.add(resultTree);
      searchPanel.setSpacing(5);
      initWidget(mainPanel);
      setStyleName("model_search");
      searchLabel.addStyleName("label");
      searchBox.addStyleName("search");
      next.addStyleName("link");
      previous.addStyleName("link");
      searchBox.addKeyUpHandler(this);
      resultTree.addOpenHandler(this);
      next.addClickHandler(this);
      previous.addClickHandler(this);
    }

    public void clearOverLay() {
      for (int i = 0; i < resultTree.getItemCount(); i++) {
        ModelItem modelItem = (ModelItem) resultTree.getItem(i);
        modelItem.clearOverLay();
      }

    }

    public int getPage() {
      int page = (start / limit) + 1;
      return page;
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == next) {
        start += 10;
        search(currentSearchTerm);
      } else if (source == previous) {
        start -= 10;
        search(currentSearchTerm);
      }

    }

    public void onFailure(Throwable caught) {
      Window.alert(caught.getMessage());
      GWT.log(caught.getMessage(), caught);
    }

    public void onKeyUp(KeyUpEvent event) {
      Object source = event.getSource();
      if (source == searchBox) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          String searchText = searchBox.getText().trim();
          start = 0;
          limit = 10;
          search(searchText);
        }
      }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
      TreeItem item = event.getTarget();
      String firstItem = item.getChild(0).getText();
      GWT.log("firstItem: " + firstItem);
      if (firstItem.equalsIgnoreCase(constants.Loading())) {
        ModelItem modelItem = (ModelItem) item;
        modelItem.open();
      }
    }

    public void onSuccess(AscModelResult result) {
      currentResult = result;
      if (currentResult != null) {
        int count = result.getCount();
        if (count > 0) {
          int end = start + limit;
          if (end > count) {
            end = count;
          }
          resultInfo.setText((start + 1) + " - " + end + " " + constants.Of()
              + " " + count);
          resultTree.clear();
          for (AscModel ascModel : result.getResults()) {
            ModelItem item = new ModelItem(ascModel.getAcceptedSpecies(),
                ascModel, false, new DownloadAllClimatesCommand(ascModel));
            item.addItem(constants.Loading());
            resultTree.addItem(item);
          }
        } else {
          resultInfo.setText(constants.NoSearchResults());
        }
        next.setVisible((start + count) >= (start + limit));
        previous.setVisible(start >= 10);
      } else {

      }
    }

    public void setPage(int page) {
      if (page < 0) {
        start = 0;
      } else {
        start = (page - 1) * limit;
      }
    }

    protected void search(String searchTerm) {
      if (currentSearchTerm == null || !searchTerm.equals(currentSearchTerm)) {
        currentSearchTerm = searchTerm;
        searchBox.setText(searchTerm);
      }
      DataSwitch.get().findModelLocation(searchTerm, start, limit, this);
    }
  }

  /**
   * The widget displayed in a marker info window.
   */
  private class OccurrenceSummaryContent extends Composite implements
      ClickHandler {
    private final HTML occurrenceInfo = new HTML();
    private Occurrence occurrence = null;
    private final HTML detailLink = new HTML(constants.Detail() + " &raquo;");
    private final HTML showLayersLink = new HTML(constants.ShowLayers());

    public OccurrenceSummaryContent() {
      detailLink.setStyleName("detaillink");
      showLayersLink.setStyleName("showlayerslink");
      VerticalPanel vp = new VerticalPanel();
      vp.add(occurrenceInfo);
      vp.add(showLayersLink);
      vp.add(detailLink);
      initWidget(vp);

      detailLink.addClickHandler(this);
      showLayersLink.addClickHandler(this);
    }

    /**
     * Builds the info window display for an occurrence.
     * 
     * @param occurrence the occurrence to display in the info window
     */
    public void loadOccurrenceInfo(Occurrence occurrence) {
      boolean isSignedIn = ApplicationView.getAuthenticatedUser() != null;
      this.occurrence = occurrence;
      OccurrenceSummary occSumm = new OccurrenceSummary(occurrence);
      String infoContent[] = isSignedIn ? occSumm.getMapUserSummary() : occSumm
          .getMapUnauthenticatedSummary();
      StringBuilder sb = new StringBuilder();
      sb.append("<div id=\"content\">");
      String headers[] = isSignedIn ? USER_MAP_FIELDS : GUEST_MAP_FIELDS;
      for (int i = 1; i < headers.length; i++) {
        String header = headers[i];
        String fieldData;
        if (header.equals(constants.Owner())
            && !OccurrenceSummary.isEmailVisisble(occurrence)) {
          fieldData = constants.EmailNotShow();
        } else {
          fieldData = infoContent[i - 1];
        }
        if (fieldData == null) {
          fieldData = "";
        }
        if (fieldData.contains(",")) {
          fieldData = "<br>" + fieldData.replace(",", "<br>");
        }
        sb.append("<b>" + header + "</b>: " + fieldData + "<br>");
      }
      sb.delete(sb.length() - 4, sb.length());
      sb.append("</div>");
      occurrenceInfo.setHTML(sb.toString());
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == detailLink) {
        if (occurrence != null) {
          parent.switchView(DETAIL, false);
          map.getInfoWindow().close();
          occurrenceListener.onOccurrenceSelected(occurrence);
        }
      } else if (source == showLayersLink) {
        if (occurrence != null) {
          LayerInfoPopup layerInfoPopup = LayerInfoPopup.getInstance();
          layerInfoPopup.loadLayersInfo(occurrence);
        }
      }

    }
  }

  /**
   * The widget that supports paging through occurrence search results.
   */
  private final OccurrencePagerWidget pager;

  private final MarkerList markerList = new MarkerList();

  private String lookupValue;

  private LatLng lookupPoint;

  /**
   * Far enough out to show the entire country of Madagascar on a 800x600 Google
   * map.
   */
  private static final int DEFAULT_ZOOM = 5;

  /**
   * Display field for map info window.
   */
  public static final String USER_MAP_FIELDS[] = { " ", "ReBioMa Id",
      constants.Taxonomy(), constants.Public(), constants.Validated(),
      constants.Certified(), constants.Owner(),
      FieldConstants.DECIMAL_LATITUDE, FieldConstants.DECIMAL_LONGITUDE,
      FieldConstants.LOCALITY, FieldConstants.COUNTRY,
      FieldConstants.STATE_PROVINCE, FieldConstants.COUNTY,
      constants.ValidationError() };
  /**
   * Display field for map info window.
   */
  public static final String GUEST_MAP_FIELDS[] = { " ", "ReBioMa Id",
      constants.Taxonomy(), constants.Validated(), constants.Certified(),
      constants.Owner(), FieldConstants.DECIMAL_LATITUDE,
      FieldConstants.DECIMAL_LONGITUDE, FieldConstants.LOCALITY,
      FieldConstants.COUNTRY, FieldConstants.STATE_PROVINCE,
      FieldConstants.COUNTY, constants.ValidationError() };

  /**
   * Initializes the map view and returns it's view info so that it can be lazy
   * loaded.
   * 
   * @param composite the parent view
   * @param query {@link OccurrenceQuery} using for this view
   * @param pageListener {@link PageListener} to listener for page changing
   * @param isDefaultView true if this constructed view is default view.
   * @param occurrenceListener {@link OccurrenceListener} to notify
   *          {@link Occurrence} selection.
   */
  public static ViewInfo init(final View composite,
      final OccurrenceQuery query, final PageListener<Occurrence> pageListener,
      final boolean isDefaultView, final OccurrenceListener occurrenceListener) {
    return new ViewInfo() {

      protected View constructView() {
        return new MapView(composite, query, pageListener, isDefaultView,
            occurrenceListener);
      }

      protected String getHisTokenName() {
        return MAP;
      }

      protected String getName() {
        return MAP;
      }
    };
  }

  /**
   * The Google map widget that initially displays the center of Madagascar.
   */
  private MapWidget map;

  /**
   * The text box on the map that allows users to search for addresses in
   * Madagascar. If a match is found, the map is re-centered on the address
   * point. Otherwise an alert window is displayed.
   * 
   * Note that this control has an asynchronous callback that issues a history
   * event.
   */
  private final GeocoderControl geocoder = new GeocoderControl(
      new LatLngCallback() {
        public void onFailure() {
          if (geocoderMarker != null) {
            map.removeOverlay(geocoderMarker);
          }
          String address = geocoder.getAddress();
          if ((address != null) && (address.trim().length() > 0)) {
            Window
                .confirm("Address not found. Add to the Madagascar Gazeteer?");
          }
        }

        public void onSuccess(LatLng point) {
          mapGeocoderResult(point);
          handleHistoryEvent();
        }
      });

  /**
   * The list box on the map that allows users to overlay environmental layers
   * on the Google map.
   */
  private final TileLayerSelector envLayerSelector;

  /**
   * The environmental layer on the Google map that is currently visible.
   */
  private LayerInfo envLayerInfo;

  /**
   * This is the asynchronous callback for the environmental layer on the Google
   * map. It notifies the map view when a map click returns a layer value at the
   * point clicked.
   * 
   * Note that this callback issues a history event.
   */
  private final LegendCallback envLegendCallback = new LegendCallback() {
    public void onLookup(LatLng point, String value) {
      lookupPoint = point;
      lookupValue = value;
      handleHistoryEvent();
    }
  };

  /**
   * This is the handler for map clicks. If a user clicks the map when a layer
   * is visible, the point is used to asynchronously lookup the layer value at
   * the clicked point via the layer legend.
   */
  private final MapClickHandler mapClickHandler = new MapClickHandler() {
    public void onClick(MapClickEvent event) {
      if (envLayerInfo != null && event.getOverlay() == null) {
        LatLng point = event.getLatLng();
        TileLayerLegend legend = envLayerInfo.getInstance().getLegend();
        legend.lookupValue(point, envLegendCallback);
      }
    }
  };

  /**
   * This is the handler for map zoom events. After a zoom, it issues a history
   * event.
   */
  private final MapZoomEndHandler mapZoomHandler = new MapZoomEndHandler() {
    public void onZoomEnd(MapZoomEndEvent event) {
      handleHistoryEvent();
    }
  };

  /**
   * This is the handler for map type changes. After a change, it issues a
   * history event.
   */
  private final MapTypeChangedHandler mapTypeHandler = new MapTypeChangedHandler() {
    public void onTypeChanged(MapTypeChangedEvent event) {
      handleHistoryEvent();
    }
  };

  /**
   * True if the map view is reconstructing it's view from parameters in a
   * history token. This is needed so that we can disable new history events
   * while reconstructing the view when a history event is fired.
   */
  private boolean isHistoryChanging = false;

  private final List<OccurrenceMarker> occurrenceMarkers = new ArrayList<OccurrenceMarker>();

  private final ActionTool actionTool;

  // private final HorizontalPanel idPanel = new HorizontalPanel();

  private ArrowMarker geocoderMarker;

  private OccurrenceListener occurrenceListener;

  private final HistoryState historyState = new MapState();
  private final Map<String, MapType> mapTypesMap = new HashMap<String, MapType>();
  private final VerticalPanel mainVp = new VerticalPanel();
  /**
   * For some reason when make this static it cause a weird infinite
   * initializing, so make it non-static to solve this problem.
   */
  private final MapType DEFAULT_MAP_TYPE = MapType.getPhysicalMap();
  private static final String DEFAULT_STYLE = "OccurrenceMapView";

  private boolean isInitializing = true;
  private OccurrenceSummaryContent summaryContent = null;
  private final HorizontalSplitPanel hsp;
  private final ModelSearch modelSearch;
  private final TabPanel leftTab;
  private boolean switched = false;
  private int currentSelectedTab = 0;

  /**
   * Creates a new map view. The map view is intended to be part of a composite
   * view which displays a page of occurrences on a map or in a list.
   * 
   * @param parent the parent composite view
   * @param pageListener {@link PageListener} to listener for page changing
   * @param isDefaultView true if this view is default view.
   * @param occurrenceListener {@link OccurrenceListener} to notify
   *          {@link Occurrence} selection.
   */
  private MapView(View parent, OccurrenceQuery query,
      PageListener<Occurrence> pageListener, boolean isDefaultView,
      OccurrenceListener oListener) {
    super(parent, isDefaultView);
    setOccurrenceListener(oListener);
    int pageSize = query.getLimit();
	if(pageSize < 0){
		pageSize = OccurrencePagerWidget.DEFAULT_PAGE_SIZE;
	}
    pager = new OccurrencePagerWidget(pageSize, query, true); // Set up number of records per page
    markerList.addCheckedSelectionLsitener(this);
    query.addDataRequestListener(this);
    if (pageListener != null) {
      pager.addPageListener(pageListener);
    }
    pager.addPageSizeChangeListener(this);
    pager.addPageClickListener(this);

    pager.addPageListener(this);

    actionTool = new ActionTool(false) {

      protected void setCheckedAll(boolean checked) {
        markerList.setCheckedAll(checked);
      }

    };

    envLayerSelector = new EnvLayerSelector(this);
    initMap();
    initMapTypesMap();
    modelSearch = new ModelSearch();
    leftTab = new TabPanel();
    leftTab.add(markerList, constants.MarkerResult());
    leftTab.add(modelSearch, constants.ModelSearch()); // Add Model Tab
    leftTab.addSelectionHandler(this);
    hsp = new HorizontalSplitPanel();
    hsp.setLeftWidget(leftTab);
    hsp.setRightWidget(map);

    HorizontalPanel toolHp = new HorizontalPanel();
    toolHp.add(actionTool);
    toolHp.add(pager);
    toolHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    toolHp.setCellHorizontalAlignment(actionTool,
        HasHorizontalAlignment.ALIGN_LEFT);
    toolHp
        .setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);
    toolHp.setWidth("100%");
    toolHp.setStyleName(DEFAULT_STYLE + "-ToolBar");
    mainVp.add(toolHp);
    // vp.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);
    mainVp.setCellHeight(toolHp, "20px");

    // idPanel.setHeight("20px");
    // vp.add(idPanel);
    // vp.setCellHeight(idPanel, "20px");

    mainVp.add(hsp);
    mainVp.setCellVerticalAlignment(hsp, HasVerticalAlignment.ALIGN_TOP);
    mainVp.setStyleName(DEFAULT_STYLE);
    initWidget(mainVp);
    // mainVp.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
    // mainVp.setSize("100%", "100%");
    hsp.setSplitPosition("30%");

    markerList.addItemSelectionListener(this);
    markerList.setCheckedAll(true);

    String historyToken = History.getToken();
    leftTab.selectTab(currentSelectedTab);
    if (!historyToken.equals("")) {
      handleOnValueChange(historyToken);
    }
    // History.fireCurrentHistoryState();
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        resize(Window.getClientWidth(), Window.getClientHeight());
        isInitializing = false;
      }

    });
  }

  public MapWidget getMapWidget() {
    return map;
  }

  public OccurrenceListener getOccurrenceListener() {
    return occurrenceListener;
  }

  public PagerWidget<Occurrence> getPagerWidget() {

    return pager;
  }

  public HistoryState getState(String historyToken) {
    historyState.setHistoryToken(historyToken);
    return historyState;
  }

  /**
   * The composite parent view calls this method when a new history item is
   * added to the history stack. Basically it encodes the map view state into a
   * history token.
   */

  public String historyToken() {
    StringBuilder tokens = new StringBuilder();
    // tokens.append(getUrlToken(UrlParam.ZOOM));
    // tokens.append(getUrlToken(UrlParam.CENTER));
    // tokens.append(getUrlToken(UrlParam.ADDRESS));
    // tokens.append(getUrlToken(UrlParam.LOOKUP_POINT));
    // tokens.append(getUrlToken(UrlParam.LAYER));
    for (UrlParam param : UrlParam.values()) {
      tokens.append(getUrlToken(param));
    }
    tokens.deleteCharAt(tokens.length() - 1);
    return tokens.toString();
  }

  /**
   * Gets Called when a check box(es) is clicked.
   */
  public void onCheckedSelect(Map<Integer, Boolean> checksMap) {
    historyState.setChecksMap(checksMap);
    if (!checksMap.isEmpty()) {
      handleHistoryEvent();
    }

  }

  /**
   * Gets called when an detail link on a {@link MarkerItem} is clicked.
   */
  public void onDetailSelect(OccurrenceItem item) {
    parent.switchView(DETAIL, true);
    item.showDetail();

  }

  /**
   * Gets called when the environmental overlay is cleared.
   */
  public void onLayerCleared(LayerInfo layerInfo) {
    envLayerInfo = null;

  }

  /**
   * Gets called when the environmental overlay is selected.
   */
  public void onLayerSelected(LayerInfo layerInfo) {
    envLayerInfo = layerInfo;
    handleHistoryEvent();
  }

  /**
   * Gets called when an marker image on a {@link MarkerItem} is clicked.
   */
  public void onMarkerSelect(OccurrenceItem item) {
    showWindowInfo(item.getOccurrenceMarker());

  }

  /**
   * Gets called when a paging button is clicked.
   */
  public void onPageClicked() {
    parent.resetToDefaultState();
  }

  /**
   * Get called when data from pager is come backed from {@link DataSwitch}.
   */
  public void onPageLoaded(List<Occurrence> data, int pageNumber) {
    if (isMyView(History.getToken())) {
      clearOccurrenceMarkers();
      mapOccurrenceMarkers(data);
      handleHistoryEvent();
      markerList.setCheckedFromMap(historyState.getChecksMap());
    }

  }

  public void onSelection(SelectionEvent<Integer> event) {
    Object source = event.getSource();
    if (source == leftTab) {
      if (event.getSelectedItem() != currentSelectedTab) {
        this.currentSelectedTab = event.getSelectedItem();
        addHistoryItem(false);
        if (currentSelectedTab == 1) {
          modelSearch.setPage(1);
          modelSearch.search("");
        } else if (currentSelectedTab == 0) {
          modelSearch.clearOverLay();
        }
      }
    }

  }

  /**
   * Gets called when an species id link on a {@link MarkerItem} is clicked.
   */
  public void onSpeciesSelect(OccurrenceItem item) {
    showWindowInfo(item.getOccurrenceMarker());

  }

  /**
   * Get called when a {@link ViewState}, which control by root parent is
   * changed.
   * 
   * @see org.rebioma.client.ComponentView#onStateChanged(org.rebioma.client.View.ViewState)
   */

  public void onStateChanged(ViewState state) {
    if (!isMyView(parent.historyToken())) {
      return;
    }
    // pager.goToPage(pager.getCurrentPageNumber());
  }

  /**
   * Gets called when a query request data from this MapView.
   */
  public void requestData(int pageNum) {
    if (isMyView(History.getToken())) {
      pager.init(pageNum);
    }

  }

  public void setVisible(boolean visible) {
    if (map.getCurrentMapType() == MapType.getEarthMap()) {
      map.setVisible(visible);
    } else {
      map.setVisible(true);
    }
  }

  public void switchBack() {
    if (switched) {
      map.setCurrentMapType(MapType.getEarthMap());
    }
    switched = false;
  }

  public boolean temperalySwitchMapType() {
    switched = true;
    boolean swit = map.getCurrentMapType() == MapType.getEarthMap();
    if (swit) {
      map.setCurrentMapType(DEFAULT_MAP_TYPE);
    }
    return switched = swit;
  }

  /**
   * This method is called when history changes and it's responsible for
   * reconstructing the map view from parameters encoded in the history token
   * via <code>historyToken()</code>.
   * 
   * To avoid new history events during the reconstruction process, it sets the
   * <code>isHistoryChanging</code> property to true and restores it to false.
   */

  protected void handleOnValueChange(String historyToken) {
    GWT.log(this.getClass() + " -- onHistoryChanged(): " + historyToken, null);
    if (historyToken.length() > 0) {
      isHistoryChanging = true;
      historyState.setHistoryToken(historyToken);
      int zoomLv = (Integer) historyState.getHistoryParameters(UrlParam.ZOOM);
      map
          .setZoomLevel(zoomLv == HistoryState.UNDEFINED ? DEFAULT_ZOOM
              : zoomLv);
      map
          .setCenter((LatLng) historyState
              .getHistoryParameters(UrlParam.CENTER));
      // geocoder.setAddress();
      geocoder.lookupAddress(historyState
          .getHistoryParameters(UrlParam.ADDRESS)
          + "");

      int layerIndex = (Integer) historyState
          .getHistoryParameters(UrlParam.LAYER);
      if (layerIndex == HistoryState.UNDEFINED) {
        envLayerSelector.clearSelection();
        envLayerInfo = null;
      } else {
        envLayerInfo = envLayerSelector.selectLayer(layerIndex);
        if (envLayerInfo != null) {
          LatLng point = (LatLng) historyState
              .getHistoryParameters(UrlParam.LOOKUP_POINT);
          String value = historyState
              .getHistoryParameters(UrlParam.LOOKUP_VALUE)
              + "";
          TileLayerLegend legend = envLayerInfo.getInstance().getLegend();
          legend.setDisplay(point, value);
        }
      }
      MapType mapType = getMapType(historyState
          .getHistoryParameters(UrlParam.MAP_TYPE)
          + "");
      map.setCurrentMapType(mapType);
      Integer leftTabIndex = (Integer) historyState
          .getHistoryParameters(UrlParam.LEFT_TAB);
      if (leftTabIndex == null || leftTabIndex < 0) {
        leftTabIndex = 0;
      }
      leftTab.selectTab(leftTabIndex);
      String modelSearchTerm = historyState.getHistoryParameters(
          UrlParam.M_SEARCH).toString();
      Integer modelSearchPage = (Integer) historyState
          .getHistoryParameters(UrlParam.M_PAGE);
      modelSearch.setPage(modelSearchPage);
      modelSearch.search(modelSearchTerm);
      historyState.parseCheckedUrl();
      isHistoryChanging = false;
    }

    // parent.switchView(MAP, true);
  }

  protected boolean isMyView(String value) {
    historyState.setHistoryToken(value);
    return historyState.getHistoryParameters(UrlParam.VIEW).toString()
        .equalsIgnoreCase(MAP);
  }

  protected void mapGeocoderResult(LatLng point) {
    String address = geocoder.getAddress();
    StringBuilder sb = new StringBuilder();
    GeocoderResult result = new GeocoderResult(point, address);
    sb.append(result);
    final InfoWindowContent content = new InfoWindowContent(result);
    map.setCenter(point);
    if (geocoderMarker != null) {
      map.removeOverlay(geocoderMarker);
    }
    geocoderMarker = GeocoderControl.createMarker(point, address);
    map.addOverlay(geocoderMarker);
    geocoderMarker.addMarkerClickHandler(new MarkerClickHandler() {
      public void onClick(MarkerClickEvent event) {
        map.getInfoWindow().open(event.getSender(), content);
      }
    });
    final InfoWindow info = map.getInfoWindow();
    info.open(point, content);
    geocoderMarker.addMarkerRemoveHandler(new MarkerRemoveHandler() {
      public void onRemove(MarkerRemoveEvent event) {
        info.close();
      }
    });
  }

  protected void resetToDefaultState() {
    historyState.clearChecksState();
  }

  protected void resize(int width, int height) {
    height = height - mainVp.getAbsoluteTop();
    if (height <= 0) {
      height = 1;
    }
    int w = width - 20;
    hsp.setSplitPosition("30%");
    mainVp.setPixelSize(w, height);
    map.checkResizeAndCenter();
  }

  private void clearOccurrenceMarkers() {
    for (Overlay marker : occurrenceMarkers) {
      map.removeOverlay(marker);
    }
    occurrenceMarkers.clear();
    markerList.clear();
  }

  private MapType getMapType(String type) {
    MapType mapType = mapTypesMap.get(type);
    if (mapType == null) {
      mapType = DEFAULT_MAP_TYPE;
    }
    return mapType;
  }

  private ModelingControl getModelControl() {
    final ModelingControl modelControl = new ModelingControl();
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        Widget modelControlWidget = modelControl.getControlWidget();
        ControlPosition hideControlPosition = new ControlPosition(
            ControlAnchor.TOP_RIGHT, modelControl.getXOffset()
                + modelControlWidget.getOffsetWidth() + 10, modelControl
                .getYOffset());
        HideControl hideControl = new HideControl(hideControlPosition);
        map.addControl(hideControl);
        hideControl.addControlWidgetToHide(modelControlWidget);
        hideControl.addControlWidgetToHide(geocoder.getControlWidget());
        hideControl.addControlWidgetToHide(envLayerSelector.getControlWidget());
      }
    });
    return modelControl;
  }

  /**
   * A helper method used for encoding map view parameters into a history token.
   * 
   * @param urlParam the URL parameter to encode
   */
  private String getUrlToken(UrlParam urlParam) {
    String query = urlParam.lower() + "=";
    switch (urlParam) {
    case ZOOM:
      query += map.getZoomLevel();
      break;
    case CENTER:
      query += map.getCenter().toUrlValue(7);
      break;
    case ADDRESS:
      String address = geocoder.getAddress();
      if ((address != null) && (address.length() > 0)) {
        query += geocoder.getAddress();
      } else {
        query = "";
      }
      break;
    case LEFT_TAB:
      int index = leftTab.getTabBar().getSelectedTab();
      if (index < 0) {
        index = 0;
      }
      query += index;
      break;
    case M_SEARCH:
      query += modelSearch.searchBox.getText().trim();
      break;
    case M_PAGE:
      query += modelSearch.getPage();
      break;
    case MAP_TYPE:
      query += map.getCurrentMapType().getName(true);
      break;
    case LOOKUP_POINT:
      if (lookupPoint != null) {
        query += lookupPoint.toUrlValue(7) + "&"
            + UrlParam.LOOKUP_VALUE.lower() + "=" + lookupValue;
      } else {
        query = "";
      }
      break;
    case LAYER:
      if (envLayerInfo != null) {
        query += envLayerSelector.getSelectionIndex(envLayerInfo.getName());
      } else {
        query = "";
      }
      break;
    case CHECKED:
      String checkedValues = historyState.getCheckedValues(true);
      if (checkedValues.equals("")) {
        query = "";
      } else {
        query += checkedValues;
      }
      break;
    case UNCHECKED:
      String uncheckedValues = historyState.getCheckedValues(false);
      if (uncheckedValues.equals("")) {
        query = "";
      } else {
        query += uncheckedValues;
      }
      break;
    default:
      query = "";
    }
    return query.length() == 0 ? query : query + "&";
  }

  /**
   * A simple helper method that only adds history events to the composite view
   * parent if the map view isn't currently restoring the map view from a
   * history token.
   */
  private void handleHistoryEvent() {
    if (!isHistoryChanging && !isInitializing && !switched) {
      addHistoryItem(false);
    }

  }

  private void initMap() {
    map = new MapWidget(HistoryState.DEFAULT_CENTER, DEFAULT_ZOOM, "crosshair",
        "move");
    map.setWidth("100%");
    map.setHeight("100%");
    map.setCurrentMapType(DEFAULT_MAP_TYPE);
    // map.addControl(getModelControl());
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        map.addControl(geocoder);
        map.addControl(envLayerSelector);
        ScaleControl scaleControl = new ScaleControl();
        LargeMapControl largeMapControl = new LargeMapControl();
        MenuMapTypeControl mapTypeControl = new MenuMapTypeControl();

        map.addControl(scaleControl);
        map.addControl(largeMapControl);
        map.addControl(mapTypeControl);

        ControlPosition hideControlPosition = new ControlPosition(
            ControlAnchor.TOP_RIGHT, 100, 10);
        HideControl hideControl = new HideControl(hideControlPosition);
        map.addControl(hideControl);
        hideControl.addControlWidgetToHide(geocoder.getControlWidget());
        hideControl.addControlWidgetToHide(envLayerSelector.getControlWidget());

      }

    });
    map.addMapType(DEFAULT_MAP_TYPE);
    map.addMapType(MapType.getEarthMap());
    map.addMapClickHandler(mapClickHandler);
    map.addMapZoomEndHandler(mapZoomHandler);
    map.addMapTypeChangedHandler(mapTypeHandler);
    map.checkResizeAndCenter();
    MapType.getEarthMap();
  }

  private void initMapTypesMap() {
    for (MapType mapType : map.getMapTypes()) {
      mapTypesMap.put(mapType.getName(true), mapType);
    }
    // mapTypesMap.put(key, value)
  }

  private void mapOccurrenceMarkers(List<Occurrence> occurrences) {
    OccurrenceMarker.resetIcons();
    OccurrenceMarker marker;
    List<Occurrence> unmappableOccs = new ArrayList<Occurrence>();
    for (Occurrence occurrence : occurrences) {
      if (!OccurrenceMarker.isMappable(occurrence)) {
        unmappableOccs.add(occurrence);
        continue;
      }
      occurrenceMarkers.add(marker = new OccurrenceMarker(occurrence));
      markerList.addItem(marker);
      map.addOverlay(marker);
      marker.addMarkerClickHandler(new MarkerClickHandler() {
        public void onClick(MarkerClickEvent event) {
          OccurrenceMarker marker = (OccurrenceMarker) event.getSender();
          showWindowInfo(marker);
        }
      });
    }
    markerList.addUnmappableItems(unmappableOccs);
  }

  private void setOccurrenceListener(OccurrenceListener occurrenceListener) {
    this.occurrenceListener = occurrenceListener;
    markerList.setOccurrenceListener(occurrenceListener);
  }

  private void showWindowInfo(OccurrenceMarker occurrenceMarker) {
	  if (summaryContent == null) {
      summaryContent = new OccurrenceSummaryContent();
    }
    summaryContent.loadOccurrenceInfo(occurrenceMarker.getOccurrence());
    map.getInfoWindow().open(occurrenceMarker,new InfoWindowContent(summaryContent));

  }

	@Override
	public void onPageSizeChange(int newPageSize) {
	  OccurrenceView occView = ApplicationView.getApplication().getOccurrenceView();
	  occView.setPageSize(newPageSize);
      //on recharge les donn�es
      requestData(1);
	}

	@Override
	public DataPager<Occurrence> getDataPagerWidget() {
		return pager.pager;
	}

	@Override
	public OccurrencePagerWidget getOccurrencePagerWidget() {
		return pager;
	}
}