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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.rebioma.client.bean.ShapeFileInfo;
import org.rebioma.client.maps.AscTileLayer.LayerInfo;
import org.rebioma.client.maps.ClearMapDrawingControl;
import org.rebioma.client.maps.CoordinatesControl;
import org.rebioma.client.maps.GeocoderControl;
import org.rebioma.client.maps.HideControl;
import org.rebioma.client.maps.KmlGenerator;
import org.rebioma.client.maps.MapControlsGroup;
import org.rebioma.client.maps.MapDrawingControl;
import org.rebioma.client.maps.MapDrawingControlListener;
import org.rebioma.client.maps.ModelEnvLayer;
import org.rebioma.client.maps.ModelingControl;
import org.rebioma.client.maps.OccurrenceMarkerManager;
import org.rebioma.client.maps.TileLayerLegend;
import org.rebioma.client.maps.TileLayerLegend.LegendCallback;
import org.rebioma.client.maps.TileLayerSelector;
import org.rebioma.client.maps.TileLayerSelector.TileLayerCallback;
import org.rebioma.client.services.MapGisService;
import org.rebioma.client.services.MapGisServiceAsync;

import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.controls.MapTypeControlOptions;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.maptypeid.MapTypeIdChangeMapEvent;
import com.google.gwt.maps.client.events.maptypeid.MapTypeIdChangeMapHandler;
import com.google.gwt.maps.client.events.zoom.ZoomChangeMapEvent;
import com.google.gwt.maps.client.events.zoom.ZoomChangeMapHandler;
import com.google.gwt.maps.client.layers.KmlLayer;
import com.google.gwt.maps.client.overlays.InfoWindow;
import com.google.gwt.maps.client.overlays.InfoWindowOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.Polygon;
import com.google.gwt.maps.client.overlays.PolygonOptions;
import com.google.gwt.maps.client.services.GeocoderRequestHandler;
import com.google.gwt.maps.client.services.GeocoderStatus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.theme.gray.client.tabs.GrayTabPanelBottomAppearance;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * A type of view that shows a Google map displaying pageable occurrence data
 * represented as markers.
 */
public class MapView extends ComponentView implements CheckedSelectionListener,
DataRequestListener, PageClickListener, PageListener<Occurrence>,
TileLayerCallback, ItemSelectionListener, SelectionHandler<Widget>, OccurrencePageSizeChangeHandler, 
GeocoderRequestHandler,	MapDrawingControlListener, AsyncCallback<String> {


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
		@Override
		public String toString() {
			return ascModel.getModelLocation() + ".zip";
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
		@Override
		public String toString() {
			return ascModel.getModelLocation() + "/" + climateName + ".zip";
		}

	}

	private static class MapGeocoderResult extends Composite {
		private final VerticalPanel vp = new VerticalPanel();

		public MapGeocoderResult(LatLng point, String address) {
			vp.add(new Label(address));
			vp.setStyleName("address");
			vp.add(new Label(point.getToUrlValue(7)));
			vp.setStyleName("latlong");
			initWidget(vp);
		}
	}


	private final MapGisServiceAsync mapGisService = GWT
			.create(MapGisService.class);

	private CsvDownloadWidget modelDownloadWidget = new CsvDownloadWidget("modelDownload");

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
					envLayer = ModelEnvLayer.newInstance("ModelOutput/"
							+ ascModel.getModelLocation() + "/" + itemLabel.getText() + "/"
							+ ascModel.getModelLocation() + ".asc");
				}
				Label modelDate = new Label(""/*DateTimeFormat.getFormat("d/M/yyyy").format(new Date())*/);
				modelDate.setStyleName("flou");
				panel.add(itemLabel);
				panel.add(modelDate);
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
				modelDownloadWidget.show(downloadCommand); 
				//    	  downloadCommand.execute();
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
					map.getOverlayMapTypes().push(envLayer.asOverlay());
					envLayer.setMapIndex(map.getOverlayMapTypes().getLength() - 1);
				} else {
					map.getOverlayMapTypes().removeAt(envLayer.getMapIndex());
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
				 map.getOverlayMapTypes().removeAt(envLayer.getMapIndex());
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
					addHistoryItem(false);
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
				next.setVisible(start + 10 < count);
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
					closeInfoWindows();
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

	private final Set<InfoWindow> infoWindows = new HashSet<InfoWindow>();

	/**
	 * Far enough out to show the entire country of Madagascar on a 800x600 Google
	 * map.
	 */
	private static final int DEFAULT_ZOOM = 5;

	private Set<KmlLayer> kmlLayers = new HashSet<KmlLayer>();

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

	private final GeocoderControl geocoder;

	/**
	 * The list box on the map that allows users to overlay environmental layers
	 * on the Google map.
	 */
	private final TileLayerSelector envLayerSelector;

	private final MapControlsGroup controlsGroup;

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
	private final ClickMapHandler mapClickHandler = new ClickMapHandler() {
		@Override
		public void onEvent(ClickMapEvent event) {
			if (envLayerInfo != null) {
				LatLng point = event.getMouseEvent().getLatLng();
				TileLayerLegend legend = envLayerInfo.getInstance().getLegend();
				legend.lookupValue(point, envLegendCallback);
			}
		}
	};

	/**
	 * This is the handler for map zoom events. After a zoom, it issues a history
	 * event.
	 */
	private final ZoomChangeMapHandler mapZoomHandler = new ZoomChangeMapHandler() {
		@Override
		public void onEvent(ZoomChangeMapEvent event) {
			handleHistoryEvent();

		}
	};

	/**
	 * This is the handler for map type changes. After a change, it issues a
	 * history event.
	 */
	private final MapTypeIdChangeMapHandler mapTypeHandler = new MapTypeIdChangeMapHandler() {

		@Override
		public void onEvent(MapTypeIdChangeMapEvent event) {
			handleHistoryEvent();
		}
	};

	/**
	 * True if the map view is reconstructing it's view from parameters in a
	 * history token. This is needed so that we can disable new history events
	 * while reconstructing the view when a history event is fired.
	 */
	private boolean isHistoryChanging = false;

	private final List<OccurrenceMarkerManager> occurrenceMarkers = new ArrayList<OccurrenceMarkerManager>();

	private final List<Marker> geocoderMarkers = new ArrayList<Marker>();

	private final ActionTool actionTool;

	// private final HorizontalPanel idPanel = new HorizontalPanel();

	private OccurrenceListener occurrenceListener;

	private final HistoryState historyState = new MapState();
	private final Map<String, MapTypeId> mapTypesMap = new HashMap<String, MapTypeId>();
	//  private final VerticalPanel mainVp = new VerticalPanel();
	private final VerticalLayoutContainer mainVp = new VerticalLayoutContainer();

	/**
	 * For some reason when make this static it cause a weird infinite
	 * initializing, so make it non-static to solve this problem.
	 */
	private final MapTypeId DEFAULT_MAP_TYPE = MapTypeId.TERRAIN;
	private static final String DEFAULT_STYLE = "OccurrenceMapView";

	private boolean isInitializing = true;
	private OccurrenceSummaryContent summaryContent = null;
	//  private final HorizontalSplitPanel hsp;
	private final BorderLayoutContainer con;
	private final ModelSearch modelSearch;
	private final TabPanel leftTab;
	private final ContentPanel leftTabPanel;
	private final ContentPanel mapPanel;
	private boolean switched = false;
	private int currentSelectedTab = 0;
	private Widget currentTab = null;
	private ToolBar toolHp;
	TabPanel modelTab;
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
		this.controlsGroup = new MapControlsGroup(this, this);
		envLayerSelector = controlsGroup.getLayerSelector();
		geocoder = controlsGroup.getGeocoder();
		initMap();
		modelSearch = new ModelSearch();

		leftTab = new TabPanel();
		leftTab.addStyleName("text");
		TabItemConfig markerTb = new TabItemConfig(constants.MarkerResult());
		TabItemConfig modelTb = new TabItemConfig(constants.ModelSearch());
		ScrollPanel mrspanel = new ScrollPanel(markerList);
		ScrollPanel mdspanel = new ScrollPanel(modelSearch);
		
		modelTab = new TabPanel(GWT.<TabPanelAppearance> create(GrayTabPanelBottomAppearance.class));
		modelTab.addStyleName("d-text");
		modelTab.setBodyBorder(false);
		modelTab.setBorders(false);
		modelTab.getElement().getStyle().setBackgroundColor("white");
		TabItemConfig dateModel = new TabItemConfig("__");
		modelTab.add(mdspanel, dateModel); 
		modelTab.setHeight(modelTab.getOffsetHeight()-10);
		//    mrspanel.add(markerList);
		//    mdspanel.add(modelSearch);
		
		currentTab = mrspanel;
		leftTab.add(mrspanel, markerTb);
		leftTab.add(modelTab, modelTb); // Add Model Tab
		setDateModel("String dmggfd");
		//init model affichage
		String modelSearchTerm = historyState.getHistoryParameters(
				UrlParam.M_SEARCH).toString();
		modelSearch.search(modelSearchTerm);
		
		//leftTab.addSelectionHandler(this);
		leftTab.addSelectionHandler(this);
		//    hsp = new HorizontalSplitPanel();
		con = new BorderLayoutContainer();

		BorderLayoutData westData = new BorderLayoutData(360);
		westData.setMinSize(200);
		westData.setCollapsible(false);
		westData.setSplit(false);
		westData.setCollapseMini(false);
		westData.setMargins(new Margins(5, 5, 5, 0));

		MarginData centerData = new MarginData();
		centerData.setMargins(new Margins(5, 0, 5, 0));
		//    hsp.setLeftWidget(leftTab);
		//    hsp.setRightWidget(map);
		leftTab.setHeight(Window.getClientHeight()-183);

		leftTabPanel = new ContentPanel();
		leftTabPanel.setHeaderVisible(false);
		//    leftTabPanel.setHeadingHtml();
		leftTabPanel.setBorders(false);
		leftTabPanel.setBodyBorder(false);
		leftTabPanel.add(leftTab);
		mapPanel = new ContentPanel();
		mapPanel.setHeaderVisible(false);
		mapPanel.setHeight(Window.getClientHeight()-183);
		mapPanel.add(map);

		con.setWestWidget(leftTabPanel, westData);
		con.setCenterWidget(mapPanel, centerData);

		//    HorizontalPanel toolHp = new HorizontalPanel();
		toolHp = new ToolBar();
		//{WD force toolbar layout view
		pager.setToolBar(toolHp);
		//}
		//    actionTool.setWidth("200px");
		toolHp.add(actionTool);
		toolHp.add(new FillToolItem());
		//    pager.setWidth("300px");
		HorizontalPanel pWHp = new HorizontalPanel();
		pWHp.add(pager);
		pWHp.setCellHorizontalAlignment(pager,
				HasHorizontalAlignment.ALIGN_RIGHT);
		//    pWHp.setWidth("470px");
		toolHp.add(pWHp);

		//    toolHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		//    toolHp.setCellHorizontalAlignment(actionTool,
		//        HasHorizontalAlignment.ALIGN_LEFT);
		//    toolHp
		//        .setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);
		//    toolHp.setWidth("100%");
		//    toolHp.setStyleName(DEFAULT_STYLE + "-ToolBar");
		//    mainVp.add(toolHp);
		// vp.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);
		//    mainVp.setCellHeight(toolHp, "20px");


		// idPanel.setHeight("20px");
		// vp.add(idPanel);
		// vp.setCellHeight(idPanel, "20px");

		//    mainVp.add(hsp);
		//    mainVp.add(con);
		//    mainVp.setCellVerticalAlignment(hsp, HasVerticalAlignment.ALIGN_TOP);
		//    mainVp.setCellVerticalAlignment(con, HasVerticalAlignment.ALIGN_TOP);
		//    mainVp.setStyleName(DEFAULT_STYLE);

		mainVp.setBorders(false);
		mainVp.add(toolHp);
		mainVp.add(con, new VerticalLayoutData(1, 1));

		initWidget(mainVp);
		// mainVp.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		// mainVp.setSize("100%", "100%");
		//    hsp.setSplitPosition("30%");

		markerList.addItemSelectionListener(this);
		markerList.setCheckedAll(true);

		String historyToken = History.getToken();
		//    leftTab.setTabIndex(1);
		//    leftTab.setActiveWidget(modelSearch);
		//    leftTab.setTabScroll(true);
		//    leftTab.setResizeTabs(true);
		if (!historyToken.equals("")) {
			handleOnValueChange(historyToken);
		}
		//    History.fireCurrentHistoryState();
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			public void execute() {
				resize(Window.getClientWidth(), Window.getClientHeight());
				isInitializing = false;
				getUpdateDate();
				forceLayout();
			}

		});
	}

	private void setDateModel(String dm) {
		Widget w = modelTab.getActiveWidget();
		if(w != null) {
			TabItemConfig config = modelTab.getConfig(w);
			if(dm==null) config.setText("");
			else {
				config.setText("last update : " + dm);
			}
			modelTab.update(w, config);
		}
	}
		
	@Override
	public void onSuccess(String line) {
		try {
			Date date = DateTimeFormat.getFormat("d/M/yyyy").parse(line);
			line = DateTimeFormat.getFormat("d MMM. yyyy").format(date);
			
		} catch (Exception e){}
		setDateModel(line);
	}
	
	@Override
	public void onFailure(Throwable arg0) {
		
	}
	
	private void getUpdateDate() {
		mapGisService.getMUpdate(GWT.getHostPageBaseURL(), this);

//		try {
//			new RequestBuilder(RequestBuilder.GET, "ModelOutput/update.txt").sendRequest("", new RequestCallback() {
//				  @Override
//				  public void onResponseReceived(Request req, Response resp) {
//				    line = resp.getText();
//				    // do stuff with the text
//				  }
//
//				  @Override
//				  public void onError(Request res, Throwable throwable) {
//				    // handle errors
//				  }
//				});
//		} catch (RequestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
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
		forceLayout();
	}

	public void onSelection(SelectionEvent<Widget> event) {
		Object source = event.getSource();
		if (source == leftTab) {
			if (!event.getSelectedItem().equals(currentTab)) {
				currentTab = (Widget) event.getSelectedItem();
				addHistoryItem(false);
				if (currentTab.equals(modelSearch.getParent())) {
					modelSearch.setPage(1);
					String modelSearchTerm = historyState.getHistoryParameters(
							UrlParam.M_SEARCH).toString();
					modelSearch.search(modelSearchTerm);
				} else if (currentTab.equals(markerList.getParent())) {
					//Exception ?
					try{
						modelSearch.clearOverLay();
					}catch(Exception e){e.printStackTrace();};
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
		if (map.getMapTypeId() == MapTypeId.SATELLITE) {
			map.setVisible(visible);
		} else {
			map.setVisible(true);
		}
	}

	public void switchBack() {
		if (switched) {
			map.setMapTypeId(MapTypeId.SATELLITE);
		}
		switched = false;
	}

	public boolean temperalySwitchMapType() {
		switched = true;
		//    boolean swit = map.getCurrentMapType() == MapType.getEarthMap();
		boolean swit = map.getMapTypeId() == MapTypeId.SATELLITE;
		if (swit) {
			map.setMapTypeId(DEFAULT_MAP_TYPE);
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

			//      MapTypeId mapType = getMapType(historyState
			//          .getHistoryParameters(UrlParam.MAP_TYPE)
			//          + "");
			//      map.setMapTypeId(DEFAULT_MAP_TYPE);
			int zoomLv = (Integer) historyState.getHistoryParameters(UrlParam.ZOOM);
			map.setZoom(zoomLv == HistoryState.UNDEFINED ? DEFAULT_ZOOM
					: zoomLv);
			map
			.setCenter((LatLng) historyState
					.getHistoryParameters(UrlParam.CENTER));
			// geocoder.setAddress();
			//      geocoder.lookupAddress(historyState
			//          .getHistoryParameters(UrlParam.ADDRESS)
			//          + "");

			//      int layerIndex = (Integer) historyState
			//          .getHistoryParameters(UrlParam.LAYER);
			//      if (layerIndex == HistoryState.UNDEFINED) {
			//        envLayerSelector.clearSelection();
			//        envLayerInfo = null;
			//      } else {
			//        envLayerInfo = envLayerSelector.selectLayer(layerIndex);
			//        if (envLayerInfo != null) {
			//          LatLng point = (LatLng) historyState
			//              .getHistoryParameters(UrlParam.LOOKUP_POINT);
			//          String value = historyState
			//              .getHistoryParameters(UrlParam.LOOKUP_VALUE)
			//              + "";
			//          TileLayerLegend legend = envLayerInfo.getInstance().getLegend();
			//          legend.setDisplay(point, value);
			//        }
			//      }
			Integer leftTabIndex = (Integer) historyState
					.getHistoryParameters(UrlParam.LEFT_TAB);
			if (leftTabIndex == null || leftTabIndex < 0) {
				leftTabIndex = 0;
			}
			String modelSearchTerm = historyState.getHistoryParameters(
					UrlParam.M_SEARCH).toString();
			//	    Integer modelSearchPage = (Integer) historyState
			//	    		.getHistoryParameters(UrlParam.M_PAGE);
			//	    modelSearch.setPage(modelSearchPage);
			historyState.parseCheckedUrl();
			if(leftTabIndex!=0) {
				currentTab = leftTab.getWidget((int)leftTabIndex);
				leftTab.setActiveWidget(currentTab);//selectTab(leftTabIndex);
				modelSearch.setPage(1);
				modelSearch.search(modelSearchTerm!=null?modelSearchTerm:"");
			}
			isHistoryChanging = false;
		}

		// parent.switchView(MAP, true);
	}

	protected boolean isMyView(String value) {
		historyState.setHistoryToken(value);
		return historyState.getHistoryParameters(UrlParam.VIEW).toString()
				.equalsIgnoreCase(MAP);
	}

	protected void mapGeocoderResult(JsArray<com.google.gwt.maps.client.services.GeocoderResult> results) {
		String address = geocoder.getAddress();
		StringBuilder sb = new StringBuilder();
		LatLng point = null;
		for(int i=0;i< results.length();i++){
			com.google.gwt.maps.client.services.GeocoderResult geoResult = results.get(i);
			point = geoResult.getGeometry().getLocation();
			MapGeocoderResult result = new MapGeocoderResult(point, address);
			sb.append(result);
			InfoWindowOptions contentOptions = InfoWindowOptions.newInstance();
			contentOptions.setContent(result);
			contentOptions.setPosition(point);
			final InfoWindow content = InfoWindow.newInstance(contentOptions);
			if (geocoderMarkers != null) {
				for(Marker marker: geocoderMarkers){
					marker.setMap((MapWidget)null);
				}
			}
			Marker geocoderMarker = GeocoderControl.createMarker(point, address);
			geocoderMarker.setMap(map);
			geocoderMarker.addClickHandler(new ClickMapHandler() {
				@Override
				public void onEvent(ClickMapEvent event) {
					closeInfoWindows();
					content.open(map);
					infoWindows.add(content);
				}
			});
			geocoderMarkers.add(geocoderMarker);
		}
		if(results.length() == 1 && point != null){
			map.setCenter(point);
		}
	}

	protected void resetToDefaultState() {
		historyState.clearChecksState();
	}

	protected void resize(int width, int height) {
		int h = height;
		height = height - mainVp.getAbsoluteTop();
		if (height <= 0) {
			height = 1;
		}
		int w = width - 20;
		mapPanel.setHeight(h - 183);
		leftTab.setHeight(h - 183);
		//    hsp.setSplitPosition("30%");
		mainVp.setPixelSize(w, height - 10);
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			public void execute() {
				forceLayout();
				map.triggerResize();
				//          map.setCenter(map.getCenter());
			}

		});
		//    initMap();
		//    map.checkResizeAndCenter();
	}

	private void clearOccurrenceMarkers() {
		for (OccurrenceMarkerManager markerManager : occurrenceMarkers) {
			markerManager.getMarker().setMap((MapWidget)null);
		}
		occurrenceMarkers.clear();
		markerList.clear();
	}

	private MapTypeId getMapType(String type) {
		MapTypeId mapType = MapTypeId.fromValue(type);
		if (mapType == null || !mapTypesMap.containsValue(mapType)) {
			mapType = DEFAULT_MAP_TYPE;
		}
		return mapType;
	}

	private ModelingControl getModelControl() {
		final ModelingControl modelControl = new ModelingControl();
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			public void execute() {
				//        Widget modelControlWidget = modelControl.getControlWidget();
				//        ControlPosition hideControlPosition = new ControlPosition(
				//            ControlAnchor.TOP_RIGHT, modelControl.getXOffset()
				//                + modelControlWidget.getOffsetWidth() + 10, modelControl
				//                .getYOffset());
				HideControl hideControl = new HideControl();
				map.setControls(ControlPosition.TOP_RIGHT, hideControl);
				hideControl.addControlWidgetToHide(modelControl);
				hideControl.addControlWidgetToHide(geocoder);
				hideControl.addControlWidgetToHide(envLayerSelector);
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
			query += map.getZoom();
			break;
		case CENTER:
			query += map.getCenter().getToUrlValue(7);
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
			int index = leftTab.getWidgetIndex(leftTab.getActiveWidget());//leftTab.getTabBar().getSelectedTab();
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
			query += map.getMapTypeId().toString();
			break;
		case LOOKUP_POINT:
			if (lookupPoint != null) {
				query += lookupPoint.getToUrlValue(7) + "&"
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
	
	private LatLng[] marineLatLng() {
		LatLng[] a = new LatLng[60];
		
		a[0] = LatLng.newInstance(-24.821639,47.101136);
		a[1] = LatLng.newInstance(-25.065697,46.518860);
		a[2] = LatLng.newInstance(-25.234758,46.051941);
		a[3] = LatLng.newInstance(-25.497827,45.634461);
		a[4] = LatLng.newInstance(-25.547397,45.208740);
		a[5] = LatLng.newInstance(-25.284438,44.714355);
		a[6] = LatLng.newInstance(-25.110472,44.266663);
		a[7] = LatLng.newInstance(-24.961160,44.093628);
		a[8] = LatLng.newInstance(-24.612064,43.972779);
		a[9] = LatLng.newInstance(-24.352101,43.720093);
		a[10] = LatLng.newInstance(-23.503552,43.807983);
		a[11] = LatLng.newInstance(-23.322080,43.769531);
		a[12] = LatLng.newInstance(-22.907803,43.500366);
		a[13] = LatLng.newInstance(-22.593726,43.341065);
		a[14] = LatLng.newInstance(-22.100909,43.352051);
		a[15] = LatLng.newInstance(-21.672743,43.500366);
		a[16] = LatLng.newInstance(-21.361013,43.549805);
		a[17] = LatLng.newInstance(-21.243303,43.862915);
		a[18] = LatLng.newInstance(-20.843412,43.983764);
		a[19] = LatLng.newInstance(-20.014645,44.555053);
		a[20] = LatLng.newInstance(-19.777043,44.467163);
		a[21] = LatLng.newInstance(-19.445874,44.538574);
		a[22] = LatLng.newInstance(-19.067310,44.302368);
		a[23] = LatLng.newInstance(-18.823117,44.318847);
		a[24] = LatLng.newInstance(-18.396230,44.088134);
		a[25] = LatLng.newInstance(-18.004856,44.071656);
		a[26] = LatLng.newInstance(-17.748687,44.085388);
		a[27] = LatLng.newInstance(-17.488221,43.975525);
		a[28] = LatLng.newInstance(-16.636192,44.497376);
		a[29] = LatLng.newInstance(-16.301688,44.501495);
		a[30] = LatLng.newInstance(-16.212038,45.271911);
		a[31] = LatLng.newInstance(-16.035255,46.485901);
		a[32] = LatLng.newInstance(-15.482799,47.210999);
		a[33] = LatLng.newInstance(-15.149020,47.614746);
		a[34] = LatLng.newInstance(-14.743011,48.034973);
		a[35] = LatLng.newInstance(-14.277692,48.084412);
		a[36] = LatLng.newInstance(-13.928736,48.180542);
		a[37] = LatLng.newInstance(-13.800741,48.386536);
		a[38] = LatLng.newInstance(-13.459080,48.820496);
		a[39] = LatLng.newInstance(-13.068777,48.935852);
		a[40] = LatLng.newInstance(-12.771625,48.988037);
		a[41] = LatLng.newInstance(-12.517028,49.029236);
		a[42] = LatLng.newInstance(-12.366831,49.202270);
		a[43] = LatLng.newInstance(-12.736801,49.537353);
		a[44] = LatLng.newInstance(-12.957580,49.794159);
		a[45] = LatLng.newInstance(-13.285392,49.891662);
		a[46] = LatLng.newInstance(-13.982046,50.092163);
		a[47] = LatLng.newInstance(-14.626109,50.136109);
		a[48] = LatLng.newInstance(-15.331870,50.405273);
		a[49] = LatLng.newInstance(-15.874168,50.141602);
		a[50] = LatLng.newInstance(-15.371599,49.883423);
		a[51] = LatLng.newInstance(-15.416615,49.627991);
		a[52] = LatLng.newInstance(-15.660065,49.570312);
		a[53] = LatLng.newInstance(-16.507199,49.740601);
		a[54] = LatLng.newInstance(-17.363746,49.369812);
		a[55] = LatLng.newInstance(-17.719910,49.438477);
		a[56] = LatLng.newInstance(-18.140632,49.309387);
		a[57] = LatLng.newInstance(-19.804983,48.729859);
		a[58] = LatLng.newInstance(-21.490058,48.202515);
		a[59] = LatLng.newInstance(-24.819146,47.098389);

		return a;
	}
	
	private LatLng[] terrestrialLatLng() {
		LatLng[] a = new LatLng[47];
		a[0] = LatLng.newInstance(-17.977411,49.553833);
		a[1] = LatLng.newInstance(-25.107900,47.189026);
		a[2] = LatLng.newInstance(-25.562265,45.703125);
		a[3] = LatLng.newInstance(-25.641526,45.175781);
		a[4] = LatLng.newInstance(-25.373810,44.692383);
		a[5] = LatLng.newInstance(-25.344027,44.307861);
		a[6] = LatLng.newInstance(-25.045792,43.989258);
		a[7] = LatLng.newInstance(-24.676970,43.879395);
		a[8] = LatLng.newInstance(-24.357105,43.648682);
		a[9] = LatLng.newInstance(-23.563987,43.593750);
		a[10] = LatLng.newInstance(-23.019076,43.374023);
		a[11] = LatLng.newInstance(-22.030911,43.154297);
		a[12] = LatLng.newInstance(-21.657428,43.330078);
		a[13] = LatLng.newInstance(-21.268900,43.494873);
		a[14] = LatLng.newInstance(-21.217701,43.769531);
		a[15] = LatLng.newInstance(-20.756114,43.901367);
		a[16] = LatLng.newInstance(-19.951405,44.450683);
		a[17] = LatLng.newInstance(-19.766704,44.346313);
		a[18] = LatLng.newInstance(-19.435514,44.428711);
		a[19] = LatLng.newInstance(-19.051734,44.176025);
		a[20] = LatLng.newInstance(-18.791918,44.208984);
		a[21] = LatLng.newInstance(-18.375379,43.978271);
		a[22] = LatLng.newInstance(-17.748687,43.978271);
		a[23] = LatLng.newInstance(-17.476432,43.879395);
		a[24] = LatLng.newInstance(-16.627639,44.411545);
		a[25] = LatLng.newInstance(-16.152028,44.375152);
		a[26] = LatLng.newInstance(-16.183024,44.846191);
		a[27] = LatLng.newInstance(-15.903226,45.225219);
		a[28] = LatLng.newInstance(-15.421910,46.538086);
		a[29] = LatLng.newInstance(-14.689881,47.416992);
		a[30] = LatLng.newInstance(-13.581921,47.776795);
		a[31] = LatLng.newInstance(-13.154376,48.208008);
		a[32] = LatLng.newInstance(-12.358783,48.672180);
		a[33] = LatLng.newInstance(-11.872726,49.262695);
		a[34] = LatLng.newInstance(-13.052723,50.020752);
		a[35] = LatLng.newInstance(-15.268288,50.520630);
		a[36] = LatLng.newInstance(-15.725770,50.419006);
		a[37] = LatLng.newInstance(-16.045814,50.202027);
		a[38] = LatLng.newInstance(-15.876809,49.976806);
		a[39] = LatLng.newInstance(-15.665222,49.910889);
		a[40] = LatLng.newInstance(-15.543668,49.806519);
		a[41] = LatLng.newInstance(-15.591293,49.652710);
		a[42] = LatLng.newInstance(-15.910895,49.768067);
		a[43] = LatLng.newInstance(-16.086531,49.748841);
		a[44] = LatLng.newInstance(-16.230498,49.905396);
		a[45] = LatLng.newInstance(-16.736167,50.064697);
		a[46] = LatLng.newInstance(-17.164223,49.839477);
		return a;
	}
	
	private void drawPolygon(LatLng a[]) {
		
	    JsArray<LatLng> paths = ArrayHelper.toJsArray(a);

	    PolygonOptions pOpts = PolygonOptions.newInstance();
	    pOpts.setFillColor("#0000FF");
	    pOpts.setStrokeColor("FFFFFF");
	    pOpts.setFillOpacity(0.0);
	    pOpts.setStrokeOpacity(0.9d);
	    pOpts.setStrokeWeight(1);
	    pOpts.setPaths(paths);
	    pOpts.setMap(map);
	    @SuppressWarnings("unused")
	    Polygon polygon = Polygon.newInstance(pOpts);
	    
//	    mapPanel.add(map);
	}

	private void initMap() {
		MapOptions mapOptions = MapOptions.newInstance();
		mapOptions.setCenter(HistoryState.DEFAULT_CENTER);
		mapOptions.setZoom(DEFAULT_ZOOM);
		mapOptions.setDraggableCursor("crosshair");
		mapOptions.setDraggingCursor("move");
		mapOptions.setMapTypeId(DEFAULT_MAP_TYPE);
		mapOptions.setScaleControl(true);
		MapTypeControlOptions mapTypeControlOptions = MapTypeControlOptions.newInstance();
		mapTypeControlOptions.setMapTypeIds(MapTypeId.values());
		mapTypesMap.put(MapTypeId.TERRAIN.toString(), MapTypeId.TERRAIN);
		mapTypesMap.put(MapTypeId.ROADMAP.toString(), MapTypeId.ROADMAP);
		mapTypesMap.put(MapTypeId.SATELLITE.toString(), MapTypeId.SATELLITE);
		mapOptions.setMapTypeControlOptions(mapTypeControlOptions);
		mapOptions.setMapTypeControl(true);
		map = new MapWidget(mapOptions);
		map.setWidth("100%");
		map.setHeight("100%");
		final MapView mapView = this;
		
//		drawPolygon(marineLatLng());
//		drawPolygon(terrestrialLatLng());
		
		// map.addControl(getModelControl());
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			@Override
			public void execute() {
				HideControl hideControl = new HideControl();
				map.setControls(ControlPosition.TOP_RIGHT, hideControl);
				controlsGroup.setMap(map, ControlPosition.RIGHT_TOP);
				//			 map.setControls(ControlPosition.TOP_RIGHT, geocoder);
				/*ScaleControl scaleControl = new ScaleControl();
	        LargeMapControl largeMapControl = new LargeMapControl();
	        MenuMapTypeControl mapTypeControl = new MenuMapTypeControl();

	        map.addControl(scaleControl);
	        map.addControl(largeMapControl);
	        map.addControl(mapTypeControl);

	        ControlPosition hideControlPosition = new ControlPosition(
	            ControlAnchor.TOP_RIGHT, 100, 10);*/
				//	        envLayerSelector.setMap(map, ControlPosition.TOP_RIGHT);
				hideControl.addControlWidgetToHide(geocoder);
				hideControl.addControlWidgetToHide(envLayerSelector);

				CoordinatesControl coordinatesControl = new CoordinatesControl(map);

				MapDrawingControl mapDrawingControl = new MapDrawingControl(map,
						ControlPosition.TOP_CENTER);
				mapDrawingControl.addListener(mapView);
				ClearMapDrawingControl clearMapDrawingControl = new ClearMapDrawingControl(mapDrawingControl);
				map.setControls(ControlPosition.TOP_LEFT, clearMapDrawingControl);
			}
		});
		map.addClickHandler(mapClickHandler);
		map.addZoomChangeHandler(mapZoomHandler);
		map.addMapTypeIdChangeHandler(mapTypeHandler);
		//    map.checkResizeAndCenter();
	}

	private void closeInfoWindows(){
		for(InfoWindow w: infoWindows){
			if(w != null){
				w.close();
			}
		}
		infoWindows.clear();
	}

	private void mapOccurrenceMarkers(List<Occurrence> occurrences) {
		OccurrenceMarkerManager.resetIcons();
		List<Occurrence> unmappableOccs = new ArrayList<Occurrence>();
		for (Occurrence occurrence : occurrences) {
			if (!OccurrenceMarkerManager.isMappable(occurrence)) {
				unmappableOccs.add(occurrence);
				continue;
			}
			final OccurrenceMarkerManager markerManager = OccurrenceMarkerManager.newInstance(occurrence);
			occurrenceMarkers.add(markerManager);
			markerList.addItem(markerManager);
			Marker marker = markerManager.getMarker();
			marker.setMap(map);
			marker.addClickHandler(new ClickMapHandler() {
				@Override
				public void onEvent(ClickMapEvent event) {
					showWindowInfo(markerManager);
				}
			});
		}
		markerList.addUnmappableItems(unmappableOccs);
	}

	private void setOccurrenceListener(OccurrenceListener occurrenceListener) {
		this.occurrenceListener = occurrenceListener;
		markerList.setOccurrenceListener(occurrenceListener);
	}

	private void showWindowInfo(OccurrenceMarkerManager occurrenceMarkerManager) {
		if (summaryContent == null) {
			summaryContent = new OccurrenceSummaryContent();
		}
		closeInfoWindows();
		summaryContent.loadOccurrenceInfo(occurrenceMarkerManager.getOccurrence());
		//parent.getAbsoluteLeft();
		InfoWindowOptions infoWindowOptions = InfoWindowOptions.newInstance();
		infoWindowOptions.setPosition(occurrenceMarkerManager.getMarker().getPosition());
		try{
			infoWindowOptions.setContent(summaryContent);
		}catch(JavaScriptException e){
			//on reessaie
			//TODO determiner pourquoi pour un nombre paire(2ième, 4ième, ...) d'execution, il y a une JavaScriptException
			infoWindowOptions.setContent(summaryContent);
		}

		InfoWindow infoWindow = InfoWindow.newInstance(infoWindowOptions);
		infoWindow.open(map);
		infoWindows.add(infoWindow);
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

	@Override
	public void onCallback(
			JsArray<com.google.gwt.maps.client.services.GeocoderResult> results,
			GeocoderStatus status) {
		if(GeocoderStatus.OK.equals(status)){
			mapGeocoderResult(results);
			handleHistoryEvent();	
		}else if(GeocoderStatus.ZERO_RESULTS.equals(status)){
			Window.confirm("Address not found. Add to the Madagascar Gazeteer?");
		}else{ //failure
			for(Marker marker: geocoderMarkers){
				marker.setMap((MapWidget)null);
			}
		}
	}

	public void forceLayout(){
		toolHp.forceLayout();
	}

	private void reloadPageWithOccurrenceIds(List<Integer> occurrenceIds){
		// on prepare le query
		pager.getQuery().setOccurrenceIdsFilter(
				new HashSet<Integer>());
		pager.getQuery().getOccurrenceIdsFilter()
		.addAll(occurrenceIds);
		// on recharge les vue DetailView et ListView
		//requestData(1);

		//prendre en compte les eventuelle changement de critère dans les comboboxes de la barre horizontal en haut
		OccurrenceView occView = ApplicationView.getApplication().getOccurrenceView();
		occView.getSearchForm().search();
	}

	/**
	 * appeler quand l'user a fini de dessiner un polygone sur le map (à l'aide
	 * du control MapDrawingControl)
	 */
	@Override
	public void polygonDrawingCompleteHandler(Polygon polygon) {
		//		final List<LatLng> coords = new ArrayList<LatLng>();
		if (polygon != null) {
			/*polygon.getPath().forEach(new MVCArrayCallback<LatLng>() {
				@Override
				public void forEach(LatLng arg0, int arg1) {
					coords.add(arg0);
				}
			});*/
			String kml = KmlGenerator.polygon2Kml(polygon);
			GWT.log("Polygon completed paths=" + kml);
			/*
			 * Window.alert(" Representation en kml du polygon\n" + kml);
			 * pager.getQuery().setOccurrenceIdsFilter(new HashSet<Integer>());
			 * pager.getQuery().getOccurrenceIdsFilter().add(115408);//teste
			 * requestData(1);
			 */
			Mask.mask((XElement)map.getElement(), "Loading");
			mapGisService.findOccurrenceIdByGeom(kml,
					new AsyncCallback<List<Integer>>() {
				@Override
				public void onSuccess(List<Integer> result) {
					reloadPageWithOccurrenceIds(result);
					Mask.unmask((XElement)map.getElement());
				}

				@Override
				public void onFailure(Throwable caught) {
					Mask.unmask((XElement)map.getElement());
					Window.alert("Failure =>" + caught.getMessage());
				}
			});
		}
	}

	@Override
	public void polygonDeletedHandler() {
		pager.getQuery().setOccurrenceIdsFilter(new HashSet<Integer>());
		OccurrenceView occView = ApplicationView.getApplication().getOccurrenceView();
		occView.getSearchForm().search();
	}

	private Map<String, List<Integer>> getTableGidsMap(List<ShapeFileInfo> shapeFileInfos){
		Map<String, List<Integer>> tableGidsMap = new HashMap<String, List<Integer>>();
		for(ShapeFileInfo info: shapeFileInfos){
			if(!tableGidsMap.containsKey(info.getTableName())){
				tableGidsMap.put(info.getTableName(), new ArrayList<Integer>());
			}
			if(info.getGid() > 0){
				tableGidsMap.get(info.getTableName()).add(info.getGid());
			}

		}
		return tableGidsMap;
	}

	public void loadKmlLayer(List<ShapeFileInfo> shapeFileInfos, boolean search){
		//chargement des occurrences
		/* ------------------------ chargement des layers ------------------------------- */
		//en mode local on doit copier le fichier kml généré par notre servlet dans http://41.74.23.114/kmlfiles/ pour voir le layer sur le map
		//		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		//			@Override
		//			public void execute() {
		//				KmlLayer layer = KmlLayer.newInstance("http://41.74.23.114/kmlfiles/lim_region_aout0681891013141617.kmz");
		//				layer.setMap(map);
		//			}
		//		});
		Map<String, List<Integer>> tableGidsMap = this.getTableGidsMap(shapeFileInfos);
		//en mode production on peut utiliser directement le servlet puisque l'url est public
		final Set<String> urls = KmlUtil.getKmlFileUrl(tableGidsMap);
		//suppression des layers existants
		for(KmlLayer layer: kmlLayers){
			if(layer != null){
				layer.setMap(null);
			}
		}
		//Chargement des layers kml

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				for(String url: urls){
					KmlLayer layer = KmlLayer.newInstance(
							url
//							"http://192.168.123.252/kmlfiles/lim_region_aout0681891013141617.kmz"
							);
					layer.setMap(map);
					kmlLayers.add(layer);
				}
			}
		});
		
		if(!search)return;

		Mask.mask((XElement)map.getElement(), "Loading");
		mapGisService.findOccurrenceIdsByShapeFiles(tableGidsMap, new AsyncCallback<List<Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				Mask.unmask((XElement)map.getElement());
				Window.alert("Failure =>" + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Integer> result) {
				reloadPageWithOccurrenceIds(result);
				Mask.unmask((XElement)map.getElement());
			}
		});

	}

}