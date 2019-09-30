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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.OpenLayers;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.EventType;
import org.gwtopenmaps.openlayers.client.event.FeatureHighlightedListener;
import org.gwtopenmaps.openlayers.client.event.MapClickListener;
import org.gwtopenmaps.openlayers.client.event.MapLayerChangedListener;
import org.gwtopenmaps.openlayers.client.event.MapZoomListener;
import org.gwtopenmaps.openlayers.client.event.MarkerBrowserEventListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.KML;
import org.gwtopenmaps.openlayers.client.handler.PathHandler;
import org.gwtopenmaps.openlayers.client.handler.PolygonHandler;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Markers;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.gwtopenmaps.openlayers.client.protocol.HTTPProtocol;
import org.gwtopenmaps.openlayers.client.protocol.HTTPProtocolOptions;
import org.gwtopenmaps.openlayers.client.protocol.Protocol;
import org.gwtopenmaps.openlayers.client.strategy.BBoxStrategy;
import org.gwtopenmaps.openlayers.client.strategy.FixedStrategy;
import org.gwtopenmaps.openlayers.client.strategy.RefreshStrategy;
import org.gwtopenmaps.openlayers.client.strategy.Strategy;
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
import org.rebioma.client.maps.GeocoderControl;
import org.rebioma.client.maps.HideControl;
import org.rebioma.client.maps.MapControlsGroup;
import org.rebioma.client.maps.ModelEnvLayer;
import org.rebioma.client.maps.ModelingControl;
import org.rebioma.client.maps.OccurrenceMarkerManager;
import org.rebioma.client.maps.OccurrenceMarkerManager.OptionsManager;
import org.rebioma.client.maps.TileLayerLegend;
import org.rebioma.client.maps.TileLayerLegend.LegendCallback;
import org.rebioma.client.maps.TileLayerSelector;
import org.rebioma.client.maps.TileLayerSelector.TileLayerCallback;
import org.rebioma.client.services.MapGisService;
import org.rebioma.client.services.MapGisServiceAsync;
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
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.ToggleButton;
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
		TileLayerCallback, ItemSelectionListener, SelectionHandler<Widget>,
		OccurrencePageSizeChangeHandler,/*RORO GeocoderRequestHandler,*/AsyncCallback<String> {

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
			Window.open(
					GWT.getHostPageBaseURL() + "ModelOutput/"
							+ ascModel.getModelLocation() + ".zip", "_blank",
					"");

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
			Window.open(
					GWT.getHostPageBaseURL() + "ModelOutput/"
							+ ascModel.getModelLocation() + "/" + climateName
							+ ".zip", "_blank", "");
		}

		@Override
		public String toString() {
			return ascModel.getModelLocation() + "/" + climateName + ".zip";
		}

	}

private static class MapGeocoderResult extends Composite {
		private final VerticalPanel vp = new VerticalPanel();

		public MapGeocoderResult(LonLat point, String address) {
			vp.add(new Label(address));
			vp.setStyleName("address");
			vp.add(new Label(point.lon()+","+point.lat()));//RORO
			vp.setStyleName("latlong");
			initWidget(vp);
		}
	}

	private final MapGisServiceAsync mapGisService = GWT
			.create(MapGisService.class);

	private CsvDownloadWidget modelDownloadWidget = new CsvDownloadWidget(
			"modelDownload");

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
							+ ascModel.getModelLocation() + "/"
							+ itemLabel.getText() + "/"
							+ ascModel.getModelLocation() + ".asc");
				}
				Label modelDate = new Label(""/*
											 * DateTimeFormat.getFormat("d/M/yyyy"
											 * ).format(new Date())
											 */);
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
			Window.open(
					GWT.getHostPageBaseURL() + "ModelOutput/"
							+ ascModel.getModelLocation() + "/"
							+ itemLabel.getText() + "/"
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
				// downloadCommand.execute();
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
					map.getMap().addLayer(envLayer.asOverlay());
				} else {
					map.getMap().removeLayer(envLayer.asOverlay());
				}
			}

		}

		public void open() {
			TreeItem child = getChild(0);
			if (itemLabel == null
					|| child.getText().equals(constants.Loading())) {
				DataSwitch.get().getModelClimateEras(
						ascModel.getModelLocation(), this);
			}
		}

		/**
		 * @param clickCommand
		 *            the clickCommand to set
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
			//RORO	map.getMap().removeLayer(envLayer.asOverlay());
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
					resultInfo.setText((start + 1) + " - " + end + " "
							+ constants.Of() + " " + count);
					resultTree.clear();
					for (AscModel ascModel : result.getResults()) {
						ModelItem item = new ModelItem(
								ascModel.getAcceptedSpecies(), ascModel, false,
								new DownloadAllClimatesCommand(ascModel));
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
			if (currentSearchTerm == null
					|| !searchTerm.equals(currentSearchTerm)) {
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
		private final HTML detailLink = new HTML(constants.Detail()
				+ " &raquo;");
		private final HTML showLayersLink = new HTML(constants.ShowLayers());

		public OccurrenceSummaryContent() {
			detailLink.setStyleName("detaillink");
			showLayersLink.setStyleName("showlayerslink");
			VerticalPanel vp = new VerticalPanel();
			vp.add(occurrenceInfo);
			//vp.add(showLayersLink);
			//vp.add(detailLink);
			initWidget(vp);
			detailLink.addClickHandler(this);
			showLayersLink.addClickHandler(this);
		}

		/**
		 * Builds the info window display for an occurrence.
		 * 
		 * @param occurrence
		 *            the occurrence to display in the info window
		 */
		public void loadOccurrenceInfo(Occurrence occurrence) {
			boolean isSignedIn = ApplicationView.getAuthenticatedUser() != null;
			this.occurrence = occurrence;
			OccurrenceSummary occSumm = new OccurrenceSummary(occurrence);
			String infoContent[] = isSignedIn ? occSumm.getMapUserSummary()
					: occSumm.getMapUnauthenticatedSummary();
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
					//RORO closeInfoWindows();
					occurrenceListener.onOccurrenceSelected(occurrence);
				}
			} else if (source == showLayersLink) {
				if (occurrence != null) {
					LayerInfoPopup layerInfoPopup = LayerInfoPopup
							.getInstance();
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

	private LonLat lookupPoint;

	//RORO private final Set<InfoWindow> infoWindows = new HashSet<InfoWindow>();

	/**
	 * Far enough out to show the entire country of Madagascar on a 800x600
	 * Google map.
	 */
	private static final int DEFAULT_ZOOM = 5;

	private Set<Vector> kmlLayers = new HashSet<Vector>();

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
	 * Initializes the map view and returns it's view info so that it can be
	 * lazy loaded.
	 * 
	 * @param composite
	 *            the parent view
	 * @param query
	 *            {@link OccurrenceQuery} using for this view
	 * @param pageListener
	 *            {@link PageListener} to listener for page changing
	 * @param isDefaultView
	 *            true if this constructed view is default view.
	 * @param occurrenceListener
	 *            {@link OccurrenceListener} to notify {@link Occurrence}
	 *            selection.
	 */
	public static ViewInfo init(final View composite,
			final OccurrenceQuery query,
			final PageListener<Occurrence> pageListener,
			final boolean isDefaultView,
			final OccurrenceListener occurrenceListener) {
		return new ViewInfo() {

			protected View constructView() {
				return new MapView(composite, query, pageListener,
						isDefaultView, occurrenceListener);
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
	MapOptions defaultMapOptions = new MapOptions();
	MapWidget map = new MapWidget("100%", "100%", defaultMapOptions);
	private Popup popup;
	HorizontalPanel hpButtons;
	public final static VerticalPanel vCOntrolMap = new VerticalPanel();

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
	 * This is the asynchronous callback for the environmental layer on the
	 * Google map. It notifies the map view when a map click returns a layer
	 * value at the point clicked.
	 * 
	 * Note that this callback issues a history event.
	 */
	private final LegendCallback envLegendCallback = new LegendCallback() {
		public void onLookup(LonLat point, String value) {
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
	private final MapClickListener mapClickHandler = new MapClickListener() {

		@Override
		public void onClick(MapClickEvent mapClickEvent) {
			if (envLayerInfo != null) {
				LonLat point = mapClickEvent.getLonLat();
				point.transform(map.getMap().getProjection(),
						DEFAULT_PROJECTION.getProjectionCode());
				TileLayerLegend legend = envLayerInfo.getInstance().getLegend();
				legend.lookupValue(point, envLegendCallback);
			}
		}
	};

	/**
	 * This is the handler for map zoom events. After a zoom, it issues a
	 * history event.
	 */
	private final MapZoomListener mapZoomHandler = new MapZoomListener() {
		@Override
		public void onMapZoom(MapZoomEvent eventObject) {
			handleHistoryEvent();

		}
	};

	/**
	 * This is the handler for map type changes. After a change, it issues a
	 * history event.
	 */
	private final MapLayerChangedListener mapTypeHandler = new MapLayerChangedListener() {

		@Override
		public void onLayerChanged(MapLayerChangedEvent eventObject) {
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

	private final List<Markers> geocoderMarkers = new ArrayList<Markers>();// RORO

	private final ActionTool actionTool;

	// private final HorizontalPanel idPanel = new HorizontalPanel();

	private OccurrenceListener occurrenceListener;

	private final HistoryState historyState = new MapState();
	private final Map<String, Layer> mapTypesMap = new HashMap<String, Layer>();
	// private final VerticalPanel mainVp = new VerticalPanel();
	private final VerticalLayoutContainer mainVp = new VerticalLayoutContainer();

	/**
	 * For some reason when make this static it cause a weird infinite
	 * initializing, so make it non-static to solve this problem.
	 */
	private OSM osm = OSM.Mapnik("OpenStreetMap");
	private final Layer DEFAULT_MAP_TYPE = osm;// .TERRAIN;
	private static final String DEFAULT_STYLE = "OccurrenceMapView";

	private boolean isInitializing = true;
	private OccurrenceSummaryContent summaryContent = null;
	// private final HorizontalSplitPanel hsp;
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
	 * Creates a new map view. The map view is intended to be part of a
	 * composite view which displays a page of occurrences on a map or in a
	 * list.
	 * 
	 * @param parent
	 *            the parent composite view
	 * @param pageListener
	 *            {@link PageListener} to listener for page changing
	 * @param isDefaultView
	 *            true if this view is default view.
	 * @param occurrenceListener
	 *            {@link OccurrenceListener} to notify {@link Occurrence}
	 *            selection.
	 */
	// ////////////////////////////////////////////////////////////////
	private final ToggleButton drawPolygonButton = new ToggleButton(constants.Draw());
	private final ToggleButton navigateButton = new ToggleButton(constants.Navigate());
	// the DrawFeature and DeleteFeature controls
	private DrawFeature drawLineFeatureControl = null;
	private SelectFeature deleteFeatureControl = null;
	private final VerticalPanel APPan = new VerticalPanel();
	private final VerticalPanel deforestationPan = new VerticalPanel();
	private VerticalLayoutContainer layerPan = new VerticalLayoutContainer();
	private HorizontalPanel hplayerPan = new HorizontalPanel();


	private MapView(View parent, OccurrenceQuery query,
			PageListener<Occurrence> pageListener, boolean isDefaultView,
			OccurrenceListener oListener) {
		super(parent, isDefaultView);
		setOccurrenceListener(oListener);
		int pageSize = query.getLimit();
		if (pageSize < 0) {
			pageSize = OccurrencePagerWidget.DEFAULT_PAGE_SIZE;
		}
		pager = new OccurrencePagerWidget(pageSize, query, true); // Set up
																	// number of
																	// records
																	// per page
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
		this.controlsGroup = new MapControlsGroup(this);//RORO
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

		modelTab = new TabPanel(
				GWT.<TabPanelAppearance> create(GrayTabPanelBottomAppearance.class));
		modelTab.addStyleName("d-text");
		modelTab.setBodyBorder(false);
		modelTab.setBorders(false);
		modelTab.getElement().getStyle().setBackgroundColor("white");
		TabItemConfig dateModel = new TabItemConfig("__");
		modelTab.add(mdspanel, dateModel);
		modelTab.setHeight(modelTab.getOffsetHeight() - 10);
		// mrspanel.add(markerList);
		// mdspanel.add(modelSearch);
		HorizontalPanel hpAP = new HorizontalPanel();
		HorizontalPanel hpd = new HorizontalPanel();
		HorizontalPanel APhp=new HorizontalPanel();
		HorizontalPanel hpFC=new HorizontalPanel();
		Label lbap = new Label(constants.AP());
		Label lbd = new Label(constants.DeforestationMap());
		APhp.add(lbap);
		APhp.add(new HTML("<a>Source: SAPM 2017</a>"));
		APhp.setSpacing(10);
		hpFC.add(lbd);
		hpFC.add(new HTML("<a target='_blank' href='https://bioscenemada.cirad.fr/maps/'>Source</a>"));
		hpFC.setSpacing(10);
		hpAP.add(APPan);
		hpd.add(deforestationPan);
		APPan.setSpacing(5);
		deforestationPan.setSpacing(5);
		layerPan.add(APhp, new VerticalLayoutData(1, 20, new Margins(
				0, 0, 0, 0)));
		layerPan.add(hpAP, new VerticalLayoutData(1, 20, new Margins(
				0, 0, 0, 0)));
		layerPan.add(hpFC, new VerticalLayoutData(1, 20, new Margins(
				0, 0, 0, 0)));
		layerPan.add(hpd, new VerticalLayoutData(1, 300, new Margins(
				0, 0, 0, 0)));
		hplayerPan.add(layerPan);
		currentTab = mrspanel;
		leftTab.add(mrspanel, markerTb);
		leftTab.add(modelTab, modelTb);
		leftTab.add(hplayerPan, constants.Layers());// Add Model Tab
		setDateModel("String dmggfd");
		// init model affichage
		String modelSearchTerm = historyState.getHistoryParameters(
				UrlParam.M_SEARCH).toString();
		modelSearch.search(modelSearchTerm);

		// leftTab.addSelectionHandler(this);
		leftTab.addSelectionHandler(this);
		// hsp = new HorizontalSplitPanel();
		con = new BorderLayoutContainer();

		BorderLayoutData westData = new BorderLayoutData(400);
		westData.setMinSize(200);
		westData.setCollapsible(true);
		westData.setSplit(true);
		westData.setCollapseMini(true);
		westData.setMargins(new Margins(5, 5, 5, 0));

		MarginData centerData = new MarginData();
		centerData.setMargins(new Margins(5, 0, 5, 0));
		// hsp.setLeftWidget(leftTab);
		// hsp.setRightWidget(map);
		leftTab.setHeight(Window.getClientHeight() - 183);
		// leftTab.setHeight(450);
		leftTabPanel = new ContentPanel();
		//leftTabPanel.setHeaderVisible(false);
		// leftTabPanel.setHeadingHtml();
		leftTabPanel.setBorders(false);
		leftTabPanel.setBodyBorder(false);
		leftTabPanel.add(leftTab);
		mapPanel = new ContentPanel();
		mapPanel.setHeaderVisible(false);
		mapPanel.setHeight(Window.getClientHeight() - 183);
		// mapPanel.setHeight(450);
		mapPanel.add(map);
		BorderLayoutData eastData = new BorderLayoutData(500);
		eastData.setMinSize(200);
		eastData.setCollapsible(true);
		eastData.setSplit(true);
		eastData.setCollapseMini(true);
		eastData.setMargins(new Margins(5, 5, 5, 5));
		// //////////////////RORO END CODE
		mapPanel.add(map);
		HorizontalPanel panel = new HorizontalPanel();
		// panel.setHeight("450px");
		hpButtons = new HorizontalPanel();
		vCOntrolMap.add(hpButtons);
		panel.add(vCOntrolMap);
		ContentPanel pan=new ContentPanel();
		pan.add(panel);
		hpButtons.add(drawPolygonButton);
		hpButtons.add(navigateButton);
		con.setWestWidget(leftTabPanel, westData);
		con.setCenterWidget(mapPanel, centerData);
		con.setEastWidget(pan, eastData);
		// HorizontalPanel toolHp = new HorizontalPanel();
		toolHp = new ToolBar();
		// {WD force toolbar layout view
		pager.setToolBar(toolHp);
		// }
		// actionTool.setWidth("200px");
		toolHp.add(actionTool);
		toolHp.add(new FillToolItem());
		// pager.setWidth("300px");
		HorizontalPanel pWHp = new HorizontalPanel();
		pWHp.add(pager);
		pWHp.setCellHorizontalAlignment(pager,
				HasHorizontalAlignment.ALIGN_RIGHT);
		// pWHp.setWidth("470px");
		toolHp.add(pWHp);

		// toolHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		// toolHp.setCellHorizontalAlignment(actionTool,
		// HasHorizontalAlignment.ALIGN_LEFT);
		// toolHp
		// .setCellHorizontalAlignment(pager,
		// HasHorizontalAlignment.ALIGN_RIGHT);
		// toolHp.setWidth("100%");
		// toolHp.setStyleName(DEFAULT_STYLE + "-ToolBar");
		// mainVp.add(toolHp);
		// vp.setCellHorizontalAlignment(pager,
		// HasHorizontalAlignment.ALIGN_RIGHT);
		// mainVp.setCellHeight(toolHp, "20px");

		// idPanel.setHeight("20px");
		// vp.add(idPanel);
		// vp.setCellHeight(idPanel, "20px");

		// mainVp.add(hsp);
		// mainVp.add(con);
		// mainVp.setCellVerticalAlignment(hsp, HasVerticalAlignment.ALIGN_TOP);
		// mainVp.setCellVerticalAlignment(con, HasVerticalAlignment.ALIGN_TOP);
		// mainVp.setStyleName(DEFAULT_STYLE);

		mainVp.setBorders(false);
		mainVp.add(toolHp);
		mainVp.add(con, new VerticalLayoutData(1, 1));

		initWidget(mainVp);
		// mainVp.setPixelSize(Window.getClientWidth(),
		// Window.getClientHeight());
		// mainVp.setSize("100%", "100%");
		// hsp.setSplitPosition("30%");

		markerList.addItemSelectionListener(this);
		markerList.setCheckedAll(true);

		String historyToken = History.getToken();
		// leftTab.setTabIndex(1);
		// leftTab.setActiveWidget(modelSearch);
		// leftTab.setTabScroll(true);
		// leftTab.setResizeTabs(true);
		if (!historyToken.equals("")) {
			handleOnValueChange(historyToken);
		}
		// History.fireCurrentHistoryState();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
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
		if (w != null) {
			TabItemConfig config = modelTab.getConfig(w);
			if (dm == null)
				config.setText("");
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

		} catch (Exception e) {
		}
		setDateModel(line);
	}

	@Override
	public void onFailure(Throwable arg0) {

	}

	private void getUpdateDate() {
		mapGisService.getMUpdate(GWT.getHostPageBaseURL(), this);

		// try {
		// new RequestBuilder(RequestBuilder.GET,
		// "ModelOutput/update.txt").sendRequest("", new RequestCallback() {
		// @Override
		// public void onResponseReceived(Request req, Response resp) {
		// line = resp.getText();
		// // do stuff with the text
		// }
		//
		// @Override
		// public void onError(Request res, Throwable throwable) {
		// // handle errors
		// }
		// });
		// } catch (RequestException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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
	 * added to the history stack. Basically it encodes the map view state into
	 * a history token.
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
					// Exception ?
					try {
						modelSearch.clearOverLay();
					} catch (Exception e) {
						e.printStackTrace();
					}
					;
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
		if (map.getMap().getBaseLayer() == osm) {
			map.setVisible(visible);
		} else {
			map.setVisible(true);
		}
	}

	public void switchBack() {
		if (switched) {
			map.getMap().addLayer(osm);
			osm.setIsBaseLayer(true);
		}
		switched = false;
	}

	public boolean temperalySwitchMapType() {
		switched = true;
		boolean swit = map.getMap().getBaseLayer() == osm;
		if (swit) {
			map.getMap().addLayer(osm);
			osm.setIsBaseLayer(true);
		}
		return switched = swit;
	}

	/**
	 * This method is called when history changes and it's responsible for
	 * reconstructing the map view from parameters encoded in the history token
	 * via <code>historyToken()</code>.
	 * 
	 * To avoid new history events during the reconstruction process, it sets
	 * the <code>isHistoryChanging</code> property to true and restores it to
	 * false.
	 */

	protected void handleOnValueChange(String historyToken) {

		GWT.log(this.getClass() + " -- onHistoryChanged(): " + historyToken,
				null);
		if (historyToken.length() > 0) {
			isHistoryChanging = true;
			historyState.setHistoryToken(historyToken);

			// MapTypeId mapType = getMapType(historyState
			// .getHistoryParameters(UrlParam.MAP_TYPE)
			// + "");
			// map.setMapTypeId(DEFAULT_MAP_TYPE);
			int zoomLv = (Integer) historyState
					.getHistoryParameters(UrlParam.ZOOM);
			/*
			 * RORO map.getMap().setMinMaxZoomLevel(zoomLv ==
			 * HistoryState.UNDEFINED ? DEFAULT_ZOOM : zoomLv,zoomLv ==
			 * HistoryState.UNDEFINED ? DEFAULT_ZOOM : zoomLv); map.getMap()
			 * .setCenter((LonLat) historyState
			 * .getHistoryParameters(UrlParam.CENTER));
			 */
			// geocoder.setAddress();
			// geocoder.lookupAddress(historyState
			// .getHistoryParameters(UrlParam.ADDRESS)
			// + "");

			// int layerIndex = (Integer) historyState
			// .getHistoryParameters(UrlParam.LAYER);
			// if (layerIndex == HistoryState.UNDEFINED) {
			// envLayerSelector.clearSelection();
			// envLayerInfo = null;
			// } else {
			// envLayerInfo = envLayerSelector.selectLayer(layerIndex);
			// if (envLayerInfo != null) {
			// LatLng point = (LatLng) historyState
			// .getHistoryParameters(UrlParam.LOOKUP_POINT);
			// String value = historyState
			// .getHistoryParameters(UrlParam.LOOKUP_VALUE)
			// + "";
			// TileLayerLegend legend = envLayerInfo.getInstance().getLegend();
			// legend.setDisplay(point, value);
			// }
			// }
			Integer leftTabIndex = (Integer) historyState
					.getHistoryParameters(UrlParam.LEFT_TAB);
			if (leftTabIndex == null || leftTabIndex < 0) {
				leftTabIndex = 0;
			}
			String modelSearchTerm = historyState.getHistoryParameters(
					UrlParam.M_SEARCH).toString();
			// Integer modelSearchPage = (Integer) historyState
			// .getHistoryParameters(UrlParam.M_PAGE);
			// modelSearch.setPage(modelSearchPage);
			historyState.parseCheckedUrl();
			if (leftTabIndex != 0) {
				currentTab = leftTab.getWidget((int) leftTabIndex);
				leftTab.setActiveWidget(currentTab);// selectTab(leftTabIndex);
				modelSearch.setPage(1);
				modelSearch.search(modelSearchTerm != null ? modelSearchTerm
						: "");
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

	/* RORO protected void mapGeocoderResult(
			JsArray<com.google.gwt.maps.client.services.GeocoderResult> results) {
		String address = geocoder.getAddress();
		StringBuilder sb = new StringBuilder();
		LatLng point = null;
		for (int i = 0; i < results.length(); i++) {
			com.google.gwt.maps.client.services.GeocoderResult geoResult = results
					.get(i);
			point = geoResult.getGeometry().getLocation();
			MapGeocoderResult result = new MapGeocoderResult(point, address);
			sb.append(result);
			InfoWindowOptions contentOptions = InfoWindowOptions.newInstance();
			contentOptions.setContent(result);
			contentOptions.setPosition(point);
			final InfoWindow content = InfoWindow.newInstance(contentOptions);
			if (geocoderMarkers != null) {
				for (Markers marker : geocoderMarkers) {
					map.getMap().removeLayer(marker);
					// RORO marker.setMap((MapWidget)null);
				}
			}
			/*
			 * RORO Marker geocoderMarker = GeocoderControl.createMarker(point,
			 * address); //RORO geocoderMarker.setMap(map);
			 * geocoderMarker.addClickHandler(new ClickMapHandler() {
			 * 
			 * @Override public void onEvent(ClickMapEvent event) {
			 * closeInfoWindows(); //RORO content.open(map);
			 * infoWindows.add(content); } });
			 */
			// RORO geocoderMarkers.add(geocoderMarker);
		/*RORO }
		if (results.length() == 1 && point != null) {
			// RORO map.setCenter(point);
		}
	}*/

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
		// hsp.setSplitPosition("30%");
		mainVp.setPixelSize(w, height - 10);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				forceLayout();
				// RORO map.triggerResize();
				// map.setCenter(map.getCenter());
			}

		});
		// initMap();
		// map.checkResizeAndCenter();
	}

	private void clearOccurrenceMarkers() {
		for (OccurrenceMarkerManager markerManager : occurrenceMarkers) {
			// RORO markerManager.getMarker().setMap((MapWidget)null);
	 map.getMap().removeLayer(markerManager.getMarkers());
		//	map.getMap().removeOverlayLayers();
		}
		occurrenceMarkers.clear();
		markerList.clear();
	}

	/*
	 * private MapTypeId getMapType(String type) { MapTypeId mapType =
	 * MapTypeId.fromValue(type); if (mapType == null ||
	 * !mapTypesMap.containsValue(mapType)) { mapType = DEFAULT_MAP_TYPE; }
	 * return Layer; }
	 */

	private ModelingControl getModelControl() {
		final ModelingControl modelControl = new ModelingControl();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				// Widget modelControlWidget = modelControl.getControlWidget();
				// ControlPosition hideControlPosition = new ControlPosition(
				// ControlAnchor.TOP_RIGHT, modelControl.getXOffset()
				// + modelControlWidget.getOffsetWidth() + 10, modelControl
				// .getYOffset());
				HideControl hideControl = new HideControl();
				vCOntrolMap.add(hideControl);
				// RORO map.setControls(ControlPosition.TOP_RIGHT, hideControl);
				hideControl.addControlWidgetToHide(modelControl);
				hideControl.addControlWidgetToHide(geocoder);
				hideControl.addControlWidgetToHide(envLayerSelector);
			}
		});
		return modelControl;
	}

	/**
	 * A helper method used for encoding map view parameters into a history
	 * token.
	 * 
	 * @param urlParam
	 *            the URL parameter to encode
	 */
	private String getUrlToken(UrlParam urlParam) {
		String query = urlParam.lower() + "=";
		switch (urlParam) {
		case ZOOM:
			query += map.getMap().getZoom();
			break;
		case CENTER:
			query += map.getMap().getCenter().lon() + "";
			map.getMap().getCenter().lat();
			break;
		case ADDRESS:
			/*RORO String address = geocoder.getAddress();
			if ((address != null) && (address.length() > 0)) {
				query += geocoder.getAddress();
			} else {
				query = "";
			}*/
			break;
		case LEFT_TAB:
			int index = leftTab.getWidgetIndex(leftTab.getActiveWidget());// leftTab.getTabBar().getSelectedTab();
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
			query += map.getMap().getBaseLayer();
			break;
		case LOOKUP_POINT:
			if (lookupPoint != null) {
				lookupPoint.transform(map.getMap().getProjection(),
						DEFAULT_PROJECTION.getProjectionCode()); // transform
																	// lonlat to
																	// more
																	// readable
																	// format
				query += lookupPoint.lon() + " ; " + lookupPoint.lat() + "&"// .getToUrlValue(7)
																			// +
																			// "&"
						+ UrlParam.LOOKUP_VALUE.lower() + "=" + lookupValue;
			} else {
				query = "";
			}
			break;
		case LAYER:
			if (envLayerInfo != null) {
				query += envLayerSelector.getSelectionIndex(envLayerInfo
						.getName());
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
	 * A simple helper method that only adds history events to the composite
	 * view parent if the map view isn't currently restoring the map view from a
	 * history token.
	 */
	private void handleHistoryEvent() {
		if (!isHistoryChanging && !isInitializing && !switched) {
			addHistoryItem(false);
		}

	}

	private LonLat[] marineLatLng() {
		LonLat[] a = new LonLat[60];
		a[0] = new LonLat(47.101136,-24.821639);
		a[1] = new LonLat(46.518860,-25.065697);
		a[2] = new LonLat(46.051941,-25.234758);
		a[3] = new LonLat(45.634461,-25.497827);
		a[4] = new LonLat(45.208740,-25.547397);
		a[5] = new LonLat(44.714355,-25.284438);
		a[6] = new LonLat(44.266663,-25.110472);
		a[7] = new LonLat(44.093628,-24.961160);
		a[8] = new LonLat(43.972779,-24.612064);
		a[9] = new LonLat(43.720093,-24.352101);
		a[10] = new LonLat(43.807983,-23.503552);
		a[11] = new LonLat(43.769531,-23.322080);
		a[12] = new LonLat(43.500366,-22.907803);
		a[13] = new LonLat(43.341065,-22.593726);
		a[14] = new LonLat(43.352051,-22.100909);
		a[15] = new LonLat(43.500366,-21.672743);
		a[16] = new LonLat(43.549805,-21.361013);
		a[17] = new LonLat(43.862915,-21.243303);
		a[18] = new LonLat(43.983764,-20.843412);
		a[19] = new LonLat(44.555053,-20.014645);
		a[20] = new LonLat(44.467163,-19.777043);
		a[21] = new LonLat(44.538574,-19.445874);
		a[22] = new LonLat(44.302368,-19.067310);
		a[23] = new LonLat(44.318847,-18.823117);
		a[24] = new LonLat(44.088134,-18.396230);
		a[25] = new LonLat(44.071656,-18.004856);
		a[26] = new LonLat(44.085388,-17.748687);
		a[27] = new LonLat(43.975525,-17.488221);
		a[28] = new LonLat(44.497376,-16.636192);
		a[29] = new LonLat(44.501495,-16.301688);
		a[30] = new LonLat(45.271911,-16.212038);
		a[31] = new LonLat(46.485901,-16.035255);
		a[32] = new LonLat(47.210999,-15.482799);
		a[33] = new LonLat(47.614746,-15.149020);
		a[34] = new LonLat(48.034973,-14.743011);
		a[35] = new LonLat(48.084412,-14.277692);
		a[36] = new LonLat(48.180542,-13.928736);
		a[37] = new LonLat(48.386536,-13.800741);
		a[38] = new LonLat(48.820496,-13.459080);
		a[39] = new LonLat(48.935852,-13.068777);
		a[40] = new LonLat(48.988037,-12.771625);
		a[41] = new LonLat(49.029236,-12.517028);
		a[42] = new LonLat(49.202270,-12.366831);
		a[43] = new LonLat(49.537353,-12.736801);
		a[44] = new LonLat(49.794159,-12.957580);
		a[45] = new LonLat(49.891662,-13.285392);
		a[46] = new LonLat(50.092163,-13.982046);
		a[47] = new LonLat(50.136109,-14.626109);
		a[48] = new LonLat(50.405273,-15.331870);
		a[49] = new LonLat(50.141602,-15.874168);
		a[50] = new LonLat(49.883423,-15.371599);
		a[51] = new LonLat(49.627991,-15.416615);
		a[52] = new LonLat(49.570312,-15.660065);
		a[53] = new LonLat(49.740601,-16.507199);
		a[54] = new LonLat(49.369812,-17.363746);
		a[55] = new LonLat(49.438477,-17.719910);
		a[56] = new LonLat(49.309387,-18.140632);
		a[57] = new LonLat(48.729859,-19.804983);
		a[58] = new LonLat(48.202515,-21.490058);
		a[59] = new LonLat(47.098389,-24.819146);

		return a;
	}

	private LonLat[] terrestrialLatLng() {
		LonLat[] a = new LonLat[47];
		a[0] = new LonLat(49.553833,-17.977411);
		a[1] = new LonLat(47.189026,-25.107900);
		a[2] = new LonLat(45.703125,-25.562265);
		a[3] = new LonLat(45.175781,-25.641526);
		a[4] = new LonLat(44.692383,-25.373810);
		a[5] = new LonLat(44.307861,-25.344027);
		a[6] = new LonLat(43.989258,-25.045792);
		a[7] = new LonLat(43.879395,-24.676970);
		a[8] = new LonLat(43.648682,-24.357105);
		a[9] = new LonLat(43.593750,-23.563987);
		a[10] = new LonLat(43.374023,-23.019076);
		a[11] = new LonLat(43.154297,-22.030911);
		a[12] = new LonLat(43.330078,-21.657428);
		a[13] = new LonLat(43.494873,-21.268900);
		a[14] = new LonLat(43.769531,-21.217701);
		a[15] = new LonLat(43.901367,-20.756114);
		a[16] = new LonLat(44.450683,-19.951405);
		a[17] = new LonLat(44.346313,-19.766704);
		a[18] = new LonLat(44.428711,-19.435514);
		a[19] = new LonLat(44.176025,-19.051734);
		a[20] = new LonLat(44.208984,-18.791918);
		a[21] = new LonLat(43.978271,-18.375379);
		a[22] = new LonLat(43.978271,-17.748687);
		a[23] = new LonLat(43.879395,-17.476432);
		a[24] = new LonLat(44.411545,-16.627639);
		a[25] = new LonLat(44.375152,-16.152028);
		a[26] = new LonLat(44.846191,-16.183024);
		a[27] = new LonLat(45.225219,-15.903226);
		a[28] = new LonLat(46.538086,-15.421910);
		a[29] = new LonLat(47.416992,-14.689881);
		a[30] = new LonLat(47.776795,-13.581921);
		a[31] = new LonLat(48.208008,-13.154376);
		a[32] = new LonLat(48.672180,-12.358783);
		a[33] = new LonLat(49.262695,-11.872726);
		a[34] = new LonLat(50.020752,-13.052723);
		a[35] = new LonLat(50.520630,-15.268288);
		a[36] = new LonLat(50.419006,-15.725770);
		a[37] = new LonLat(50.202027,-16.045814);
		a[38] = new LonLat(49.976806,-15.876809);
		a[39] = new LonLat(49.910889,-15.665222);
		a[40] = new LonLat(49.806519,-15.543668);
		a[41] = new LonLat(49.652710,-15.591293);
		a[42] = new LonLat(49.768067,-15.910895);
		a[43] = new LonLat(49.748841,-16.086531);
		a[44] = new LonLat(49.905396,-16.230498);
		a[45] = new LonLat(50.064697,-16.736167);
		a[46] = new LonLat(49.839477,-17.164223);
		return a;
	}

	private static final Projection DEFAULT_PROJECTION = new Projection(
			"EPSG:4326");

	private void initMap() {
		OpenLayers.setProxyHost("olproxy?targetURL=");
		defaultMapOptions = new MapOptions();
		defaultMapOptions.setProjection("EPSG:4326");
		defaultMapOptions.setDisplayProjection(new Projection("EPSG:4326"));
		defaultMapOptions.setNumZoomLevels(16);
		OSM osm_1 = OSM.Mapnik("OpenStreetMap");
		osm_1.setIsBaseLayer(true);
		map.getMap().addLayer(osm_1);
		GoogleV3Options gHybridOptions = new GoogleV3Options();
		gHybridOptions.setNumZoomLevels(20);
		gHybridOptions.setType(GoogleV3MapType.G_HYBRID_MAP);
		GoogleV3 gHybrid = new GoogleV3("Google Hybrid", gHybridOptions);

		GoogleV3Options gSatelliteOptions = new GoogleV3Options();
		gSatelliteOptions.setNumZoomLevels(20);
		gSatelliteOptions.setType(GoogleV3MapType.G_SATELLITE_MAP);
		GoogleV3 gSatellite = new GoogleV3("Google Satellite",
				gSatelliteOptions);
		map.getMap().addLayers(new Layer[] { gHybrid, gSatellite });
		// / Checkbox pour les autres couches
		final CheckBox checkBoxAP = new CheckBox(constants.AP());
		final CheckBox checkBoxfor1953 = new CheckBox(constants.DeforestationMap()+" 1953");
		final CheckBox checkBoxfor1973 = new CheckBox(constants.DeforestationMap()+" 1973");
		final CheckBox checkBoxfor1990 = new CheckBox(constants.DeforestationMap()+" 1990");
		final CheckBox checkBoxfor2000 = new CheckBox(constants.DeforestationMap()+" 2000");
		final CheckBox checkBoxfor2005 = new CheckBox(constants.DeforestationMap()+" 2005");
		final CheckBox checkBoxfor2010 = new CheckBox(constants.DeforestationMap()+" 2010");
		final CheckBox checkBoxfor2014 = new CheckBox(constants.DeforestationMap()+" 2014");
		APPan.add(checkBoxAP);
		deforestationPan.add(checkBoxfor1953);
		deforestationPan.add(checkBoxfor1973);
		deforestationPan.add(checkBoxfor1990);
		deforestationPan.add(checkBoxfor2000);
		deforestationPan.add(checkBoxfor2005);
		deforestationPan.add(checkBoxfor2010);
		deforestationPan.add(checkBoxfor2014);

		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("portal:protected_areas");
		//wmsParams.setStyles("AP_bycat_UICN_style");
		wmsParams.setParameter("transparent", "true");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setUntiled();
		wmsLayerParams.setProjection("EPSG:3857");
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrl = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayer = new WMS(constants.AP(), wmsUrl, wmsParams,
				wmsLayerParams);
		checkBoxAP.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxAP.getValue()) {
					map.getMap().addLayer(wmsLayer);
				} else {
					map.getMap().removeLayer(wmsLayer);
				}
			}
		});
		WMSParams wmsParamstif = new WMSParams();
		wmsParamstif.setFormat("image/png");
		wmsParamstif.setLayers("portal:for1953");
		wmsParamstif.setParameter("transparent", "true");
		WMSOptions wmsLayerParamstif = new WMSOptions();
		wmsLayerParamstif.setUntiled();
		wmsLayerParamstif.setProjection("EPSG:3857");
		wmsLayerParamstif.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif = new WMS(constants.DeforestationMap()+" 1953", wmsUrltif,
				wmsParamstif, wmsLayerParamstif);
		checkBoxfor1953.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor1953.getValue()) {
					map.getMap().addLayer(wmsLayertif);
				} else {
					map.getMap().removeLayer(wmsLayertif);
				}
			}
		});
		WMSParams wmsParams1973 = new WMSParams();
		wmsParams1973.setFormat("image/png");
		wmsParams1973.setLayers("portal:for1973");
		wmsParams1973.setParameter("transparent", "true");
		WMSOptions wmsLayerParams1973 = new WMSOptions();
		wmsLayerParams1973.setUntiled();
		wmsLayerParams1973.setProjection("EPSG:3857");
		wmsLayerParams1973.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif1973 = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif1973 = new WMS(constants.DeforestationMap()+" 1973",
				wmsUrltif1973, wmsParams1973, wmsLayerParams1973);
		checkBoxfor1973.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor1973.getValue()) {
					map.getMap().addLayer(wmsLayertif1973);
				} else {
					map.getMap().removeLayer(wmsLayertif1973);
				}
			}
		});

		WMSParams wmsParams1990 = new WMSParams();
		wmsParams1990.setFormat("image/png");
		wmsParams1990.setLayers("portal:for1990");
		wmsParams1990.setStyles("");
		wmsParams1990.setParameter("transparent", "true");
		WMSOptions wmsLayerParams1990 = new WMSOptions();
		wmsLayerParams1990.setUntiled();
		wmsLayerParams1990.setProjection("EPSG:3857");
		wmsLayerParams1990.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif1990 = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif1990 = new WMS(constants.DeforestationMap()+" 1990",
				wmsUrltif1990, wmsParams1990, wmsLayerParams1990);
		checkBoxfor1990.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor1990.getValue()) {
					map.getMap().addLayer(wmsLayertif1990);
				} else {
					map.getMap().removeLayer(wmsLayertif1990);
				}
			}
		});

		WMSParams wmsParams2000 = new WMSParams();
		wmsParams2000.setFormat("image/png");
		wmsParams2000.setLayers("portal:for2000");
		wmsParams2000.setStyles("");
		wmsParams2000.setParameter("transparent", "true");
		WMSOptions wmsLayerParams2000 = new WMSOptions();
		wmsLayerParams2000.setUntiled();
		wmsLayerParams2000.setProjection("EPSG:3857");
		wmsLayerParams2000.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif2000 = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif2000 = new WMS(constants.DeforestationMap()+" 2000",
				wmsUrltif2000, wmsParams2000, wmsLayerParams2000);
		checkBoxfor2000.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor2000.getValue()) {
					map.getMap().addLayer(wmsLayertif2000);
				} else {
					map.getMap().removeLayer(wmsLayertif2000);
				}
			}
		});

		WMSParams wmsParams2005 = new WMSParams();
		wmsParams2005.setFormat("image/png");
		wmsParams2005.setLayers("portal:for2005");
		wmsParams2005.setParameter("transparent", "true");
		WMSOptions wmsLayerParams2005 = new WMSOptions();
		wmsLayerParams2005.setUntiled();
		wmsLayerParams2005.setProjection("EPSG:3857");
		wmsLayerParams2005.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif2005 = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif2005 = new WMS(constants.DeforestationMap()+" 2005",
				wmsUrltif2005, wmsParams2005, wmsLayerParams2005);
		checkBoxfor2005.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor2005.getValue()) {
					map.getMap().addLayer(wmsLayertif2005);
				} else {
					map.getMap().removeLayer(wmsLayertif2005);
				}
			}
		});

		WMSParams wmsParams2010 = new WMSParams();
		wmsParams2010.setFormat("image/png");
		wmsParams2010.setLayers("portal:for2010");
		wmsParams2010.setStyles("");
		wmsParams2010.setParameter("transparent", "true");
		WMSOptions wmsLayerParams2010 = new WMSOptions();
		wmsLayerParams2010.setUntiled();
		wmsLayerParams2010.setProjection("EPSG:3857");
		wmsLayerParams2010.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif2010 = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif2010 = new WMS(constants.DeforestationMap()+" 2010",
				wmsUrltif2010, wmsParams2010, wmsLayerParams2010);
		checkBoxfor2010.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor2010.getValue()) {
					map.getMap().addLayer(wmsLayertif2010);
				} else {
					map.getMap().removeLayer(wmsLayertif2010);
				}
			}
		});

		WMSParams wmsParams2014 = new WMSParams();
		wmsParams2014.setFormat("image/png");
		wmsParams2014.setLayers("portal:for2014");
		wmsParams2014.setStyles("");
		wmsParams2014.setParameter("transparent", "true");
		WMSOptions wmsLayerParams2014 = new WMSOptions();
		wmsLayerParams2014.setUntiled();
		wmsLayerParams2014.setProjection("EPSG:900913");
		wmsLayerParams2014.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrltif2014 = "http://localhost:8086/geoserver/portal/wms";
		final WMS wmsLayertif2014 = new WMS(constants.DeforestationMap()+" 2014",
				wmsUrltif2014, wmsParams2014, wmsLayerParams2014);
		checkBoxfor2014.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBoxfor2014.getValue()) {
					map.getMap().addLayer(wmsLayertif2014);
				} else {
					map.getMap().removeLayer(wmsLayertif2014);
				}
			}
		});

		// Adds the custom mouse position to the map
		MousePositionOutput mpOut = new MousePositionOutput() {
			@Override
			public String format(LonLat lonLat,
					org.gwtopenmaps.openlayers.client.Map map) {
				String out = "";
				out += "<b>longitude </b> ";
				out += lonLat.lon();
				out += "<b>latitude</b> ";
				out += lonLat.lat();
				return out;
			}
		};

		MousePositionOptions mpOptions = new MousePositionOptions();
		mpOptions.setFormatOutput(mpOut);
		map.getMap().addControl(new MousePosition(mpOptions));
		map.getMap().addControl(new LayerSwitcher()); // + sign in the
														// upperright corner to
														// display the layer
														// switcher
		map.getMap().addControl(new OverviewMap()); // + sign in the lowerright
													// to display the
													// overviewmap
		map.getMap().addControl(new ScaleLine()); // Display the scaleline
		/*LonLat lonlat=new LonLat(5232016.0665556,-1920825.0401101);
	      lonlat.transform(new Projection("EPSG:32738").getProjectionCode(), new Projection("EPSG:32738").getProjectionCode());
	      */
		/// System.out.println(""+ lonlat.lon()+","+lonlat.lat());
		LonLat lonLat=new LonLat(47,-19);
		lonLat.transform(new Projection("EPSG:4326").getProjectionCode(), map.getMap().getProjection());
		map.getMap().setCenter(lonLat, DEFAULT_ZOOM);
		// map.getMap().setCenter(new LonLat(47.3318, -18.8296), 6);
		map.getElement().getFirstChildElement().getStyle().setZIndex(0);

		final MapView mapView = this;

		// drawPolygon(marineLatLng());
		// drawPolygon(terrestrialLatLng());

		// map.addControl(getModelControl());
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				HideControl hideControl = new HideControl();
				vCOntrolMap.add(hideControl);
				vCOntrolMap.add(controlsGroup);
				controlsGroup.setMap(map);

				// map.setControls(ControlPosition.TOP_RIGHT, geocoder);
				/*
				 * ScaleControl scaleControl = new ScaleControl();
				 * LargeMapControl largeMapControl = new LargeMapControl();
				 * MenuMapTypeControl mapTypeControl = new MenuMapTypeControl();
				 * 
				 * map.addControl(scaleControl);
				 * map.addControl(largeMapControl);
				 * map.addControl(mapTypeControl);
				 * 
				 * ControlPosition hideControlPosition = new ControlPosition(
				 * ControlAnchor.TOP_RIGHT, 100, 10);
				 */
				// envLayerSelector.setMap(map, ControlPosition.TOP_RIGHT);
				hideControl.addControlWidgetToHide(geocoder);
				hideControl.addControlWidgetToHide(envLayerSelector);
				VectorOptions vectorOptions = new VectorOptions();
				final Style style = new Style();
				style.setStrokeWidth(5);
				vectorOptions.setStyle(style);
				final RefreshStrategy refreshStrategy = new RefreshStrategy(); // to
				// refresh
				// the
				// map
				// after
				// the
				// user
				// has
				// drawn
				// a
				// polygon
				refreshStrategy.setForce(true);
				vectorOptions.setStrategies(new Strategy[] {
						new BBoxStrategy(), refreshStrategy });
				// if your wms is in a different projection use
				// vectorOptions.setProjection(LAMBERT72);
				final Vector wfsLayer = new Vector("WFS", vectorOptions);
				// Create the delete control
				deleteFeatureControl = new SelectFeature(wfsLayer);
				final SelectFeature addFeatureControl = new SelectFeature(
						wfsLayer);
				map.getMap().addControl(deleteFeatureControl);
				map.getMap().addControl(addFeatureControl);
				deleteFeatureControl
						.addFeatureHighlightedListener(new FeatureHighlightedListener() {
							public void onFeatureHighlighted(
									VectorFeature vectorFeature) {
								vectorFeature.destroy();
							}
						});
				// Create the drawline control
				final DrawFeatureOptions drawFeatureOptions = new DrawFeatureOptions(); // create
																						// DrawFeatureOptions
																						// to
																						// listen
																						// on
				drawFeatureOptions
						.onFeatureAdded(new DrawFeature.FeatureAddedListener() // listen
																				// for
																				// the
																				// adding
																				// of
																				// features.
						{
							public void onFeatureAdded(
									VectorFeature vectorFeature) {
								vectorFeature.getGeometry().transform(
										new Projection(map.getMap()
												.getProjection()),
										DEFAULT_PROJECTION);
								GWT.log("Polygon completed paths="
										+ vectorFeature.getGeometry()
												.toString());
								Mask.mask((XElement) map.getElement(),
										"Loading");
								mapGisService.findOccurrenceIdByGeom(
										vectorFeature.getGeometry().toString(),
										new AsyncCallback<List<Integer>>() {
											@Override
											public void onSuccess(
													List<Integer> result) {
												reloadPageWithOccurrenceIds(result);
												Mask.unmask((XElement) map
														.getElement());
											}

											@Override
											public void onFailure(
													Throwable caught) {
												Mask.unmask((XElement) map
														.getElement());
												Window.alert("Failure =>"
														+ caught.getMessage());
											}
										});
							}
						});
				drawLineFeatureControl = new DrawFeature(wfsLayer,
						new PathHandler(), drawFeatureOptions);
				final DrawFeature drawFeature = new DrawFeature(wfsLayer,
						new PolygonHandler(), drawFeatureOptions);
				map.getMap().addControl(drawFeature);

				map.getMap().addControl(drawLineFeatureControl);
				drawPolygonButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (drawPolygonButton.isDown()) {
							navigateButton.setValue(false);
							deleteFeatureControl.deactivate();
							drawFeature.activate();
							// drawLineFeatureControl.activate();
							// addFeatureControl.activate();
						} else {
							drawLineFeatureControl.deactivate();
						}
					}
				});
				drawPolygonButton.setStyleName("tgbtstyle");
				navigateButton.setStyleName("tgbtstyle");
				navigateButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (navigateButton.isDown()) {
							drawPolygonButton.setValue(false);
							drawFeature.deactivate();
							deleteFeatureControl.activate();
						} else {
							deleteFeatureControl.deactivate();
						}
					}
				});

			}
		});
			// map.checkResizeAndCenter();
		map.getMap().addMapClickListener(mapClickHandler);
		map.getMap().addMapZoomListener(mapZoomHandler);
		map.getMap().addMapLayerChangedListener(mapTypeHandler);
	}

/*RORO	private void closeInfoWindows() {
		for (InfoWindow w : infoWindows) {
			if (w != null) {
				w.close();
			}
		}
		infoWindows.clear();
	}*/

	private void mapOccurrenceMarkers(List<Occurrence> occurrences) {
		OccurrenceMarkerManager.resetIcons();
		List<Occurrence> unmappableOccs = new ArrayList<Occurrence>();
		for (Occurrence occurrence : occurrences) {
			if (!OccurrenceMarkerManager.isMappable(occurrence)) {
				unmappableOccs.add(occurrence);
				continue;
			}
			final OccurrenceMarkerManager markerManager = OccurrenceMarkerManager
					.newInstance(occurrence);
			occurrenceMarkers.add(markerManager);
			markerList.addItem(markerManager);
			OptionsManager op = new OptionsManager();
			Markers markers = op.getOptions(markerManager.getOccurrence());
			LonLat lonlat = markerManager.getPoint(markerManager
					.getOccurrence());
			lonlat.transform("EPSG:4326", map.getMap().getProjection());
			map.getMap().addLayer(markers);
			final Marker marker = new Marker(lonlat, op.getIcon(markerManager
					.getOccurrence()));
			markers.addMarker(marker);
			map.getMap().addLayer(markers);

			marker.addBrowserEventListener(EventType.MAP_CLICK,
					new MarkerBrowserEventListener() {

						public void onBrowserEvent(
								MarkerBrowserEventListener.MarkerBrowserEvent markerBrowserEvent) {
							showWindowInfo(markerManager);
							popup = new FramedCloud(
									"id1",
									marker.getLonLat(),
									null,
									""
											+ summaryContent,
									null, true);
							popup.setPanMapIfOutOfView(true);
							popup.setAutoSize(true);
							map.getMap().addPopup(popup);
						}
					});// marker.setMap(map);
			/*
			 * marker.addClickHandler(new ClickMapHandler() {
			 * 
			 * @Override public void onEvent(ClickMapEvent event) {
			 * showWindowInfo(markerManager); } });
			 */
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
		//RORO closeInfoWindows();
		summaryContent.loadOccurrenceInfo(occurrenceMarkerManager
				.getOccurrence());
		OptionsManager op = new OptionsManager();
		Markers markers = op
				.getOptions(occurrenceMarkerManager.getOccurrence());
		LonLat lonlat = occurrenceMarkerManager
				.getPoint(occurrenceMarkerManager.getOccurrence());
		lonlat.transform("EPSG:4326", map.getMap().getProjection());
		map.getMap().addLayer(markers);
		final Marker marker = new Marker(lonlat,
				op.getIcon(occurrenceMarkerManager.getOccurrence()));
		markers.addMarker(marker);
		map.getMap().addLayer(markers);

		try {
			marker.addBrowserEventListener(EventType.MAP_CLICK,
					new MarkerBrowserEventListener() {

						public void onBrowserEvent(
								MarkerBrowserEventListener.MarkerBrowserEvent markerBrowserEvent) {
							popup = new FramedCloud("id1", marker.getLonLat(),
									null, "" + summaryContent, null, true);
							popup.setPanMapIfOutOfView(true);
							popup.setAutoSize(true);
							map.getMap().addPopup(popup);
						}

					});
			popup = new FramedCloud("id1", marker.getLonLat(), null, ""
					+ summaryContent, null, true);
			popup.setPanMapIfOutOfView(true); 
			popup.setAutoSize(true);
			map.getMap().addPopup(popup);

		} catch (JavaScriptException e) {

		}
	}

	@Override
	public void onPageSizeChange(int newPageSize) {
		OccurrenceView occView = ApplicationView.getApplication()
				.getOccurrenceView();
		occView.setPageSize(newPageSize);
		// on recharge les donn?es
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

	/*RORO @Override
	public void onCallback(
			JsArray<com.google.gwt.maps.client.services.GeocoderResult> results,
			GeocoderStatus status) {
		if (GeocoderStatus.OK.equals(status)) {
			mapGeocoderResult(results);
			handleHistoryEvent();
		} else if (GeocoderStatus.ZERO_RESULTS.equals(status)) {
			Window.confirm("Address not found. Add to the Madagascar Gazeteer?");
		} else { // failure
			for (Markers marker : geocoderMarkers) {
				// RORO marker.setMap((MapWidget)null);
				map.getMap().removeLayer(marker);
			}
		}
	}*/

	public void forceLayout() {
		toolHp.forceLayout();
	}

	private void reloadPageWithOccurrenceIds(List<Integer> occurrenceIds) {
		// on prepare le query
		pager.getQuery().setOccurrenceIdsFilter(new HashSet<Integer>());
		pager.getQuery().getOccurrenceIdsFilter().addAll(occurrenceIds);
		// on recharge les vue DetailView et ListView
		// requestData(1);

		// prendre en compte les eventuelle changement de critère dans les
		// comboboxes de la barre horizontal en haut
		OccurrenceView occView = ApplicationView.getApplication()
				.getOccurrenceView();
		occView.getSearchForm().search();
	}
	private Map<String, List<Integer>> getTableGidsMap(
			List<ShapeFileInfo> shapeFileInfos) {
		Map<String, List<Integer>> tableGidsMap = new HashMap<String, List<Integer>>();
		for (ShapeFileInfo info : shapeFileInfos) {
			if (!tableGidsMap.containsKey(info.getTableName())) {
				tableGidsMap.put(info.getTableName(), new ArrayList<Integer>());
			}
			if (info.getGid() > 0) {
				tableGidsMap.get(info.getTableName()).add(info.getGid());
			}

		}
		return tableGidsMap;
	}

	public void loadKmlLayer(List<ShapeFileInfo> shapeFileInfos, boolean search) {
		;
		Map<String, List<Integer>> tableGidsMap = this
				.getTableGidsMap(shapeFileInfos);
		// en mode production on peut utiliser directement le servlet puisque
		// l'url est public
		final Set<String> urls = KmlUtil.getKmlFileUrl(tableGidsMap);
		// suppression des layers existants
		for (Vector layer : kmlLayers) {
			if (layer != null) {
				//map.getMap().removeLayer(layer);
			}
		}
		// Chargement des layers kml

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				for (String url : urls) {
					VectorOptions kmlOptions = new VectorOptions();
					kmlOptions
							.setStrategies(new Strategy[] { new FixedStrategy() });
					HTTPProtocolOptions protocolOptions = new HTTPProtocolOptions();
					protocolOptions.setUrl(url);
					KML kml = new KML();
					kml.setExtractStyles(true);
					kml.setExtractAttributes(true);
					kml.setMaxDepth(2);
					protocolOptions.setFormat(kml);
					Protocol protocol = new HTTPProtocol(protocolOptions);
					kmlOptions.setProtocol(protocol);
					Vector kmlLayer = new Vector("KML", kmlOptions);

					map.getMap().addLayer(kmlLayer);
					map.getElement().getFirstChildElement().getStyle()
							.setZIndex(0);
					System.out.println(url);
				}
			}
		});

		if (!search)
			return;

		Mask.mask((XElement) map.getElement(), "Loading");
		mapGisService.findOccurrenceIdsByShapeFiles(tableGidsMap,
				new AsyncCallback<List<Integer>>() {
					@Override
					public void onFailure(Throwable caught) {
						Mask.unmask((XElement) map.getElement());
						Window.alert("Failure =>" + caught.getMessage());
					}

					@Override
					public void onSuccess(List<Integer> result) {
						reloadPageWithOccurrenceIds(result);
						Mask.unmask((XElement) map.getElement());
					}
				});

	}
}