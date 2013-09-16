package org.rebioma.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.form.client.api.table.StaticTable;
import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.OccurrenceQuery.DataRequestListener;
import org.rebioma.client.PagerWidget.PageClickListener;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.OccurrenceSummary;
import org.rebioma.client.bean.User;
import org.rebioma.client.maps.OccurrenceMarkerManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.MapTypeControlOptions;
import com.google.gwt.maps.client.events.drag.DragMapEvent;
import com.google.gwt.maps.client.events.drag.DragMapHandler;
import com.google.gwt.maps.client.events.dragend.DragEndMapEvent;
import com.google.gwt.maps.client.events.dragend.DragEndMapHandler;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerImage;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays the occurrence detail of a current selected occurrence as a Tree.
 * All Occurrence fields are editable except id, owner, and owner email.
 * 
 */
public class DetailView extends ComponentView implements OpenHandler<TreeItem>,
		SelectionHandler<TreeItem>, PageListener<Occurrence>,
		PageClickListener, DataRequestListener, OccurrencePageSizeChangeHandler {

	/**
	 * A listener that get fire when there is a change in one of the occurrence
	 * fields.
	 * 
	 */
	public interface FieldChangeListener {
		void onChanged(FieldEditor fieldEditor);
	}

	/**
	 * Contains all the field constants.
	 * 
	 */
	public static class FieldConstants {

		public static final String ACCEPTED_CLASS = "Accepted Class";
		public static final String ACCEPTED_FAMILY = "Accepted Family";
		public static final String ACCEPTED_GENUS = "Accepted Genus";
		public static final String ACCEPTED_KINGDOM = "Accepted Kingdom";
		public static final String ACCEPTED_NOMENCLATURAL_CODE = "Accepted Nomenclatural Code";
		public static final String ACCEPTED_ORDER = "Accepted Order";
		public static final String ACCEPTED_PHYLUM = "Accepted Phylum";
		public static final String ACCEPTED_SPECIES = "Accepted Species";
		public static final String ACCEPTED_SPECIFIC_EPITHET = "Accepted Specific Epithet";
		public static final String ACCEPTED_SUBFAMILY = "Accepted Subfamily";
		public static final String ACCEPTED_SUBGENUS = "Accepted Subgenus";
		public static final String ACCEPTED_SUBORDER = "Accepted Suborder";
		public static final String ADJUSTED_COORDINATE_UNCERTAINTY_IN_METERS = "Adjusted Coord. Uncertainty";
		public static final String ATTRIBUTES = "Attributes";
		public static final String AUTHOR_YEAR_OF_SCIENTIFIC_NAME = "Author Year Of Scientific Name";
		public static final String BASIS_OF_RECORD = "Basis Of Record";
		public static final String CATALOG_NUMBER = "Catalog Number";
		public static final String CATALOG_NUMBER_NUMERIC = "Catalog Number Numeric";

		public static final String CLASS_ = "Class";
		public static final String COLLECTING_METHOD = "Collecting Method";
		public static final String COLLECTION_CODE = "Collection Code";
		public static final String COLLECTOR = "Collector";
		public static final String COLLECTOR_NUMBER = "Collector Number";
		public static final String CONTINENT = "Continent";
		public static final String COORDINATE_UNCERTAINTY_IN_METERS = "Coordinate Uncertainty In Meters";
		public static final String COUNTRY = "Country";
		public static final String COUNTY = "County";
		public static final String DATE_IDENTIFIED = "Date Identified";

		public static final String DATE_LAST_MODIFIED = "Date Last Modified";
		public static final String DAY_COLLECTED = "Day Collected";
		public static final String DAY_OF_YEAR = "Day Of Year";
		public static final String DEC_LAT_IN_WGS84 = "Latitutde In WGS84";
		public static final String DEC_LONG_IN_WGS84 = "Longitude In WGS84";
		public static final String DECIMAL_LATITUDE = "Decimal Latitude";
		public static final String DECIMAL_LONGITUDE = "Decimal Longitude";
		public static final String DEMELEVATION = "Digital Elevation Model";
		public static final String DISPOSITION = "Disposition";
		public static final String EARLIEST_DATE_COLLECTED = "Earliest Date Collected";
		public static final String ETP_TOTAL1950 = "EPT Total (1950)";
		public static final String ETP_TOTAL2000 = "EPT Total (2000)";
		public static final String ETP_TOTALFUTURE = "EPT Total (future)";
		public static final String FAMILY = "Family";
		public static final String FIELD_NOTES = "Field Notes";
		public static final String FIELD_NUMBER = "Field Number";
		public static final String FOOTPRINT_SPATIAL_FIT = "Footprint Spatial Fit";
		public static final String FOOTPRINT_WKT = "Footprint Well Known Text";
		public static final String GEN_BANK_NUMBER = "Genetic Bank Number";
		public static final String GENUS = "Genus";
		public static final String GENUS_SPECIES = "Genus species";
		public static final String GEODETIC_DATUM = "Geodetic Datum";
		public static final String GEOL_STRECH = "Geology Strech";
		public static final String GEOREFERENCE_PROTOCOL = "Georeference Protocol";
		public static final String GEOREFERENCE_REMARKS = "Georeference Remarks";
		public static final String GEOREFERENCE_SOURCES = "Georeference Sources";
		public static final String GEOREFERENCE_VERIFICATION_STATUS = "Georeference Verification Status";
		public static final String GLOBAL_UNIQUE_IDENTIFIER = "Global Unique Identifier";

		public static final String HIGHER_GEOGRAPHY = "Higher Geography";
		public static final String HIGHER_TAXON = "Higher Taxon";
		public static final String ID = "Rebioma Id";
		public static final String IDENTIFICATION_QUALIFER = "Identification Qualifer";
		public static final String IDENTIFIED_BY = "Identified By";
		public static final String IMAGE_URL = "Image URL";
		public static final String INDIVIDUAL_COUNT = "Individual Count";
		public static final String INFORMATION_WITHHELD = "Information Withheld";
		public static final String INFRASPECIFIC_EPITHET = "Infraspecific Epithet";
		public static final String INFRASPECIFIC_RANK = "Infraspecific Rank";
		public static final String INSTITUTION_CODE = "Institution Code";
		public static final String ISLAND = "Island";
		public static final String ISLAND_GROUP = "Island Group";
		public static final String KINGDOM = "Kingdom";
		public static final String LAST_UPDATED = "Last Updated";
		public static final String LATEST_DATE_COLLECTED = "Latest Date Collected";
		public static final String LIFE_STAGE = "Life Stage";
		public static final String LOCALITY = "Locality";
		public static final String MAX_PREC1950 = "Max Precip. (1950)";
		public static final String MAX_PREC2000 = "Max Precip. (2000)";
		public static final String MAX_PRECFUTURE = "Max Precip. (Future)";
		public static final String MAX_TEMP2000 = "Max Temp. (2000)";
		public static final String MAX_TEMPFUTURE = "Max Temp. (Future)";
		public static final String MAXIMUM_DEPTH_IN_METERS = "Maximum Depth In Meters";
		public static final String MAXIMUM_ELEVATION_IN_METERS = "Maximum Elevation In Meters";
		public static final String MAXTEMP1950 = "Max Temp. (1950)";
		public static final String MIN_PREC1950 = "Min Precip. (1950)";
		public static final String MIN_PREC2000 = "Min Precip. (2000)";
		public static final String MIN_PRECFUTURE = "Min Precip. (Future)";
		public static final String MIN_TEMP1950 = "Min Temp. (1950)";
		public static final String MIN_TEMP2000 = "Min Temp. (2000)";
		public static final String MIN_TEMPFUTURE = "Min Temp. (Future)";
		public static final String MINIMUM_DEPTH_IN_METERS = "Minimum Depth In Meters";
		public static final String MINIMUM_ELEVATION_IN_METERS = "Minimum Elevation In Meters";
		public static final String MONTH_COLLECTED = "Month Collected";
		public static final String NOMENCLATURAL_CODE = "Nomenclatural Code";
		public static final Set<String> NON_EDITABLE_FIELDS = new HashSet<String>();
		public static final String ORDER = "Order";
		public static final String OTHER_CATALOG_NUMBERS = "Other Catalog Numbers";
		public static final String OWNER = "Owner";
		public static final String OWNER_EMAIL = "Owner Email";
		// public static final String OWNER_NAME = "Owner Name";
		public static final String PFC1950 = "PFC (1950)";
		public static final String PFC1970 = "PFC (1970)";
		public static final String PFC1990 = "PFC (1990)";
		public static final String PFC2000 = "PFC (2000)";
		public static final String PHYLUM = "Phylum";
		public static final String POINT_RADIUS_SPATIAL_FIT = "Point Radius Spatial Fit";
		public static final String PREPARATIONS = "Preparations";
		public static final String PUBLIC = "Public";
		public static final String REAL_MAR1950 = "MAP (1950)";
		public static final String REAL_MAR2000 = "MAP (2000)";

		public static final String REAL_MARFUTURE = "MAP (Future)";
		public static final String REAL_MAT1950 = "MAT (1950)";
		public static final String REAL_MAT2000 = "MAT (2000)";

		public static final String REAL_MATFUTURE = "MAT (Future)";
		public static final String RELATED_CATALOGED_ITEMS = "Related Cataloged Items";
		public static final String RELATED_INFORMATION = "Related Information";
		public static final String REMARKS = "Remarks";
		public static final String SCIENTIFIC_NAME = "Scientific Name";
		public static final String SEX = "Sex";
		public static final String SPECIFIC_EPITHET = "Specific Epithet";
		public static final String STATE_PROVINCE = "State Province";
		public static final String TAPIR_ACCESSIBLE = "Tapir Accessible";
		public static final String TIME_CREATED = "Time Created";
		public static final Map<String, String> TIPS_MAP = new HashMap<String, String>();
		public static final String TYPE_STATUS = "Type Status";
		public static final String VALID_DISTRIBUTION_FLAG = "Valid Distribution Flag";
		public static final String VALIDATED = "Validated";
		public static final String VALIDATION_ERROR = "Validation Error";
		public static final String VERBATIM_COLLECTING_DATE = "Verbatim Collecting Date";
		public static final String VERBATIM_COORDINATE_SYSTEM = "Verbatim Coordinate System";
		public static final String VERBATIM_COORDINATES = "Verbatim Coordinates";
		public static final String VERBATIM_DEPTH = "Verbatim Depth";
		public static final String VERBATIM_ELEVATION = "Verbatim Elevation";
		public static final String VERBATIM_LATITUDE = "Verbatim Latitude";
		public static final String VERBATIM_LONGITUDE = "Verbatim Longitude";
		public static final String VERBATIM_SPECIES = "Verbatim Species";
		public static final String VETTABLE = "Vettable";
		public static final String VETTED = "Vetted";
		public static final String REVIEWED = "Reviewed";
		public static final String VETTING_ERROR = "Vetting Error";
		public static final String WATER_BODY = "Water Body";
		public static final String WBPOS1950 = "# of Pos. WB (1950)";
		public static final String WBPOS2000 = "# of Pos. WB (2000)";
		public static final String WBPOSFUTURE = "# of Pos. WB (Future)";

		public static final String WBYEAR1950 = "Annual water balance - 1950";
		public static final String WBYEAR2000 = "Annual water balance - 2000";

		public static final String WBYEARFUTURE = "Annual water balance - Future";
		public static final String YEAR_COLLECTED = "Year Collected";
		static {
			NON_EDITABLE_FIELDS.add(ID);
			NON_EDITABLE_FIELDS.add(OWNER);
			NON_EDITABLE_FIELDS.add(OWNER_EMAIL);
			NON_EDITABLE_FIELDS.add(VALIDATION_ERROR);
			NON_EDITABLE_FIELDS.add(VALIDATED);
			NON_EDITABLE_FIELDS.add(REVIEWED);
			NON_EDITABLE_FIELDS.add(VETTING_ERROR);
			NON_EDITABLE_FIELDS.add(LAST_UPDATED);
			NON_EDITABLE_FIELDS.add(TIME_CREATED);
			NON_EDITABLE_FIELDS.add(ACCEPTED_CLASS);
			NON_EDITABLE_FIELDS.add(ACCEPTED_FAMILY);
			NON_EDITABLE_FIELDS.add(ACCEPTED_GENUS);
			NON_EDITABLE_FIELDS.add(ACCEPTED_KINGDOM);
			NON_EDITABLE_FIELDS.add(ACCEPTED_ORDER);
			NON_EDITABLE_FIELDS.add(ACCEPTED_PHYLUM);
			NON_EDITABLE_FIELDS.add(ACCEPTED_SPECIES);
			NON_EDITABLE_FIELDS.add(ACCEPTED_SPECIFIC_EPITHET);
			NON_EDITABLE_FIELDS.add(ACCEPTED_SUBFAMILY);
			NON_EDITABLE_FIELDS.add(ACCEPTED_SUBGENUS);
			NON_EDITABLE_FIELDS.add(ACCEPTED_SUBORDER);
			NON_EDITABLE_FIELDS.add(VERBATIM_SPECIES); /* Added by Jenjy */
		}

		static {

			TIPS_MAP.put(ADJUSTED_COORDINATE_UNCERTAINTY_IN_METERS,
					"Adjusted Coordinate Uncertainty In Meters");
			TIPS_MAP.put(
					ETP_TOTAL2000,
					"Annual total evapotranspiration calculated by summing 12 monthly evapotranspiration rates - 2000");
			TIPS_MAP.put(
					ETP_TOTALFUTURE,
					"Annual total evapotranspiration calculated by summing 12 monthly evapotranspiration rates - Future");
			TIPS_MAP.put(
					ETP_TOTAL1950,
					"Annual total evapotranspiration calculated by summing 12 monthly evapotranspiration rates - 1950");
			TIPS_MAP.put(MAX_PREC2000,
					"Mean precipitation of the wettest month - 2000");
			TIPS_MAP.put(MAX_PRECFUTURE,
					"Mean precipitation of the wettest month - Future");
			TIPS_MAP.put(MAX_PREC1950,
					"Mean precipitation of the wettest month - 1950");
			TIPS_MAP.put(MAX_TEMP2000,
					"Mean temperature of the hottest month - 2000");
			TIPS_MAP.put(MAX_TEMPFUTURE,
					"Mean temperature of the hottest month - Future");
			TIPS_MAP.put(MAXTEMP1950,
					"Mean temperature of the hottest month - 1950");
			TIPS_MAP.put(MIN_PREC2000,
					"Mean precipitation of the driest month - 2000");
			TIPS_MAP.put(MIN_PRECFUTURE,
					"Mean precipitation of the driest month - Future");
			TIPS_MAP.put(MIN_PREC1950,
					"Mean precipitation of the driest month - 1950");
			TIPS_MAP.put(MIN_TEMP2000,
					"Mean temperature of the coldest month - 2000");
			TIPS_MAP.put(MIN_TEMPFUTURE,
					"Mean temperature of the coldest month - Future");
			TIPS_MAP.put(MIN_TEMP1950,
					"Mean temperature of the coldest month - 1950");
			TIPS_MAP.put(PFC1950, "Precent Forest Cover - 1950");
			TIPS_MAP.put(PFC1970, "Precent Forest Cover - 1970");
			TIPS_MAP.put(PFC1990, "Precent Forest Cover - 1990");
			TIPS_MAP.put(PFC2000, "Precent Forest Cover - 2000");
			TIPS_MAP.put(REAL_MAR2000,
					"Mean annual precipitation (sum of mean monthly rainfall) - 2000");
			TIPS_MAP.put(REAL_MARFUTURE,
					"Mean annual precipitation (sum of mean monthly rainfall) - Future");
			TIPS_MAP.put(REAL_MAR1950,
					"Mean annual precipitation (sum of mean monthly rainfall) - 1950");
			TIPS_MAP.put(REAL_MAT2000,
					"Mean annual temperature (mean of monthly temperatures) - 2000");
			TIPS_MAP.put(REAL_MATFUTURE,
					"Mean annual temperature (mean of monthly temperatures) - Future");
			TIPS_MAP.put(REAL_MAT1950,
					"Mean annual temperature (mean of monthly temperatures) - 1950");
			TIPS_MAP.put(WBPOS2000,
					"The number of months with a positive water balance - 2000");
			TIPS_MAP.put(WBPOSFUTURE,
					"The number of months with a positive water balance - Future");
			TIPS_MAP.put(WBPOS1950,
					"The number of months with a positive water balance - 1950");

			TIPS_MAP.put(WBYEAR1950, "Annual water balance - 1950");
			TIPS_MAP.put(WBYEAR2000, "Annual water balance - 2000");
			TIPS_MAP.put(WBYEARFUTURE, "Annual water balance - Future");

			TIPS_MAP.put(GEOL_STRECH, "Geology Stretch - 2000");
			TIPS_MAP.put(DEMELEVATION, "Elevation - 2000");
		}

		public static String getFullFieldDescription(String fieldName) {
			return TIPS_MAP.get(fieldName);
		}
	}

	/**
	 * A editable widget where user input is validated based on Occurrence
	 * fields.
	 * 
	 * An updater content mallform input should not be saved.
	 * 
	 * @author tri
	 * 
	 */
	abstract class FieldEditor extends Composite implements KeyUpHandler,
			ClickHandler {

		/**
		 * A default CSS style for this widget
		 */
		public static final String DEFAULT_STYLE = "Field-Editor";

		/**
		 * A error CSS style for error input
		 */
		public static final String ERROR_STYLE = "error";

		/**
		 * A change CSS style for a field that the text have been changed
		 */
		public static final String FIELD_CHANGED_STYLE = "changed";

		/**
		 * A non-select CSS style for a field that not currently selected.
		 */
		public final static String NON_SELECTED_STYLE = "non-selected";

		/**
		 * A CSS style for a field that is not ediable.
		 */
		public final static String NOT_EDITABLE_STYLE = "not-editable";

		/**
		 * A select CSS style for a field that is currently selected.
		 */
		public final static String SELECTED_STYLE = "selected";

		/**
		 * A CSS style for a field that is not being hovered by a mouse.
		 */
		private static final String MOUSE_NOT_OVER = "mouse-not-over";

		/**
		 * A CSS style for a field that being hovered by a mouse.
		 */
		private static final String MOUSE_OVER = "mouse-over";

		/**
		 * A CSS style for display field, value field that are not current
		 * editing.
		 */
		private static final String NORMAL_STYLE = "mouse-not-over";

		private static final String VALUE_STYLE = "value";

		/**
		 * A input {@link TextBox}where user can edit this field value.
		 */
		protected TextBox input = null;

		/**
		 * List of {@link FieldChangeListener}. These listeners get fired when a
		 * change is made.
		 */
		private final List<FieldChangeListener> changeListeners = new ArrayList<FieldChangeListener>();

		/**
		 * The {@link HorizontalPanel} widget contain the field name and field
		 * value.
		 */
		private final HorizontalPanel container = new HorizontalPanel();

		/**
		 * A {@link Label} for display the value of this field.
		 */
		private final Label displayField;

		/**
		 * True if the value of this field is editable.
		 */
		private boolean editable = false;

		/**
		 * True if the input cause an error.
		 */
		private boolean error = false;

		/**
		 * A {@link Label} for display field name.
		 */
		private final Label fieldNameLb = new Label();

		/**
		 * Store originalText and default it to "----"
		 */
		private String originalText = "----";

		/**
		 * True if it is currently selected.
		 */
		private boolean selected = false;

		/**
		 * Initializes this widget with the given field name and the original
		 * field value.
		 * 
		 * @param fieldName
		 *            the name of this field
		 * @param fieldValue
		 *            the value of this field
		 */
		public FieldEditor(String fieldName, String fieldValue) {

			displayField = new Label();
			displayField.setStyleName(NORMAL_STYLE);
			originalText = fieldValue;
			displayField.setText(fieldValue);
			fieldNameLb.setText(fieldName);
			fieldNameLb.setWidth("300px");
			fieldNameLb.setStyleName(NON_SELECTED_STYLE);
			String toolTip = FieldConstants.TIPS_MAP.get(fieldName);
			if (toolTip != null) {
				fieldNameLb.setTitle(toolTip);
			}
			initWidget(container);

			container.add(this.fieldNameLb);
			container.add(displayField);
			container.setStyleName(DEFAULT_STYLE);

			displayField.addClickHandler(this);
			displayField.addMouseOverHandler(new MouseOverHandler() {

				public void onMouseOver(MouseOverEvent event) {
					if (isEditable()) {
						displayField.setStylePrimaryName(MOUSE_OVER);
					}
				}
			});
			displayField.addMouseOutHandler(new MouseOutHandler() {

				public void onMouseOut(MouseOutEvent event) {
					if (isEditable()) {
						displayField.setStylePrimaryName(NORMAL_STYLE);
					}
				}
			});
			input = new TextBox();
			input.setStyleName("editor");
			input.addKeyUpHandler(this);
			input.addBlurHandler(new BlurHandler() {
				/**
				 * When a user is clicked out site of the editing text box,
				 * enforces enter key action.
				 * 
				 * @param sender
				 *            text box widget
				 */
				public void onBlur(BlurEvent event) {
					setNewValue();

				}
			});
		}

		/**
		 * Adds a change listener.
		 * 
		 * @param changeListener
		 *            {@link FieldChangeListener};
		 */
		public void addChangeListener(FieldChangeListener changeListener) {
			changeListeners.add(changeListener);
		}

		/**
		 * Returns true of the the field names is the same.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else if (obj instanceof FieldEditor) {
				FieldEditor editor = (FieldEditor) obj;
				return editor.fieldNameLb.getText().trim()
						.equals(fieldNameLb.getText().trim());
			}
			return false;
		}

		/**
		 * Calls {@link #onOccurrenceUpdate()} when a save action is enforce.
		 * This method is call by {@link DetailView} when "Save Changes" button
		 * is clicked.
		 */
		public void fireOnSaved() {
			if (isChanged()) {
				try {
					onOccurrenceUpdated();
					setError(false, null);
				} catch (Exception e) {
					setError(true, e.getMessage());
					GWT.log(e.getMessage(), e);
				}
			}

		}

		/**
		 * Gets the field name of this Editor
		 * 
		 * @return field name String
		 */
		public String getName() {
			return fieldNameLb.getText();
		}

		/**
		 * Gets original text of this field
		 * 
		 * @return
		 */
		public String getOriginalText() {
			return originalText;
		}

		/**
		 * Gets value of the display value field.
		 * 
		 * @return String value in the display value field.
		 */
		public String getValue() {
			return displayField.getText();
		}

		/**
		 * A text from the text box is consider to be changed if it is differed
		 * from the original text and the input text is valid.
		 * 
		 * @return true if it is changed, false otherwise.
		 */
		public boolean isChanged() {
			String text = input.getText().trim();
			return isDifferent(text) && !error;
		}

		/**
		 * Gets whether this field is editable.
		 * 
		 * @return true if the value field is editable.
		 */
		public boolean isEditable() {
			return editable;
		}

		/**
		 * Gets whether input text is valid.
		 * 
		 * @return true if the input text is valid.
		 */
		public boolean isError() {
			return error;
		}

		public boolean isOriginalText(String text) {
			return originalText.equals(text);
		}

		public void onClick(ClickEvent event) {
			Object source = event.getSource();
			if (source == displayField) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						setEditorField();
					}
				});
			} else if (source == fieldNameLb) {

			}
		}

		public void onKeyUp(KeyUpEvent event) {
			if (isEnterKey(event.getNativeKeyCode())) {
				setNewValue();
			} else if (isEscapeKey(event.getNativeKeyCode())) {
				restorePreviousValue();
			}
		}

		/**
		 * Sets CSS style to {@link #SELECTED_STYLE} if selected is true, and
		 * sets it to {@link #NON_SELECTED_STYLE} otherwise.
		 * 
		 * @param selected
		 *            true if is selected.
		 */
		public void onSelected(boolean selected) {
			this.selected = selected;
			if (selected) {
				fieldNameLb.setStyleName(SELECTED_STYLE);
			} else {
				fieldNameLb.setStyleName(NON_SELECTED_STYLE);
			}
		}

		/**
		 * Restores displays field to original value.
		 */
		public void restoreOriginal() {
			displayField.setText(originalText);
			resetStyles();
			restorePreviousValue();
			input.setText("");
		}

		/**
		 * Restores display value field to previous value.
		 */
		public void restorePreviousValue() {
			if (container.remove(input)) {
				container.add(displayField);
				onSelected(selected);
			}
		}

		/**
		 * Sets this value field to be editable if editable = true.
		 * 
		 * @param editable
		 *            true if this value field is editable.
		 */
		public void setEditable(boolean editable) {
			this.editable = editable;
			resetStyles();
		}

		/**
		 * This value field is editable, changes the display value field to the
		 * input text box then sets it text to display value text and selects
		 * all the text in input text box.
		 */
		public void setEditorField() {
			if (editable) {
				fieldNameLb.setStyleName(NON_SELECTED_STYLE);
				container.remove(displayField);
				container.add(input);
				input.setFocus(true);
				input.setText(displayField.getText());
				input.selectAll();
			}
		}

		/**
		 * Sets error = true if detects an input error.
		 * 
		 * @param error
		 * @param errorMsg
		 *            TODO
		 */
		public void setError(boolean error, String errorMsg) {
			this.error = error;
			if (error) {
				displayField.addStyleName(ERROR_STYLE);
				displayField.setTitle(errorMsg);
			} else {
				displayField.setStyleName(NORMAL_STYLE);
			}

		}

		/**
		 * Sets the newly input value to display value field and display it.
		 */
		public void setNewValue() {
			String text = input.getText();
			setError(false, null);
			if (text.equals("")) {
				input.setText("----");
				setNewValue();
			} else if (isOriginalText(text)) {
				// do nothing
			} else if (isDifferent(text)) {
				displayField.setStyleName(NORMAL_STYLE);
				displayField.addStyleName(FIELD_CHANGED_STYLE);
				displayField.setText(text);
				fireOnChanged();
			} else {
			}
			restorePreviousValue();
		}

		/**
		 * Sets original text for this field value.
		 * 
		 * @param originalText
		 */
		public void setOriginalText(String originalText) {
			this.originalText = originalText;
		}

		/**
		 * Sets a given value to the input text.
		 * 
		 * @param newValue
		 *            a new value
		 */
		public void setValue(String newValue) {
			input.setText(newValue);
			displayField.setStyleName(NORMAL_STYLE);
			displayField.addStyleName(FIELD_CHANGED_STYLE);
			displayField.setText(newValue);
			// fireOnChanged();
		}

		/**
		 * It gets called when "save changes" button is clicked.
		 */
		protected abstract void onOccurrenceUpdated();

		/**
		 * Fires all {@link FieldChangeListener} when the value is changed.
		 */
		private void fireOnChanged() {
			for (FieldChangeListener listener : changeListeners) {
				listener.onChanged(this);
			}
		}

		/**
		 * Checks whether the given text is differed then the original text.
		 * 
		 * @param text
		 *            the new value
		 * @return true if the given text is differed then the original text.
		 */
		private boolean isDifferent(String text) {
			return !text.equals("") && !originalText.equals(text);
		}

		/**
		 * Checks whether the given key is enter key.
		 * 
		 * @param keyCode
		 *            ASCII key code
		 * @return true if the given key is enter key.
		 */
		private boolean isEnterKey(int keyCode) {
			return keyCode == KeyCodes.KEY_ENTER;
		}

		/**
		 * Checks whether the given key is escape key.
		 * 
		 * @param keyCode
		 *            ASCII key code
		 * @return true if the given key is escape key.
		 */
		private boolean isEscapeKey(int keyCode) {
			return keyCode == KeyCodes.KEY_ESCAPE;
		}

		private void resetStyles() {
			if (editable) {
				displayField.setStyleName(NORMAL_STYLE);
			} else {
				displayField.setStyleName(NOT_EDITABLE_STYLE);
			}
		}
	}

	private class DetailComment extends Composite {
		private final FlexTable commentTable = new FlexTable();

		public DetailComment() {
			initWidget(commentTable);
			setStyleName("Display-Comment");
			commentTable.setBorderWidth(0);
			commentTable.setCellPadding(0);
			commentTable.setCellSpacing(0);
		}

		public void addComment(final OccurrenceComments comment) {
			StringBuilder sb = new StringBuilder();
			VerticalPanel commentPanel = new VerticalPanel();
			HTML commentBy = new HTML();
			commentBy.setStyleName("display");
			DateTimeFormat dateFormat = DateTimeFormat.getFormat("MMM d, yyyy");
			sb.append("Commented by <a href='mailto:'" + comment.getUserEmail()
					+ ">" + comment.getUserEmail() + "</a>" + ", "
					+ dateFormat.format(comment.getDateCommented()));
			sb.append("<p>");
			sb.append(comment.getUserComment().replaceAll("\n", "<br>"));
			commentBy.setHTML(sb.toString());
			int row = commentTable.getRowCount();

			commentPanel.add(commentBy);
			commentTable.setWidget(row, 0, commentPanel);
			commentPanel.setStyleName("user-comment");
			User user = ApplicationView.getAuthenticatedUser();
			if (user != null && user.getId().equals(comment.getUserId())) {
				HTML deleteLink = new HTML(constants.Delete());
				deleteLink.setStyleName("link");
				commentPanel.add(deleteLink);
				deleteLink.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (Window.confirm("Delete this comment?")) {
							deleteComment(comment);
						}
					}

				});
			}
			// commentTable.getRowFormatter().setStyleName(row, "user-comment");
		}
	}

	/**
	 * A widget used to add and save occurrence detail comment to the server.
	 * 
	 * @author tri
	 * 
	 */
	private class DetailCommentEditor extends Composite {
		private final TextArea commentText = new TextArea();
		private boolean commentUnsaved;
		private final Button discard = new Button(constants.Discard());
		private final Button submit = new Button(constants.Submit());

		public DetailCommentEditor() {
			VerticalPanel mainVp = new VerticalPanel();
			mainVp.setStyleName(COMMENT_STYLE);
			initWidget(mainVp);
			HorizontalPanel hp0 = new HorizontalPanel();
			final CheckBox ck = new CheckBox();
			Label ckLabel = new Label("Send a copy of the comment by email.");
			hp0.setSpacing(5);
			hp0.add(ck);
			hp0.add(ckLabel);
			HorizontalPanel hp = new HorizontalPanel();
			Label commentTitle = new Label(ADD_COMMENT_MESSAGE);
			commentTitle.setStyleName(COMMENT_TITLE_STYLE);
			hp.setSpacing(5);
			hp.add(submit);
			hp.add(discard);
			mainVp.setSpacing(5);
			mainVp.add(commentTitle);
			mainVp.add(commentText);
			setEditorStyle(false);
			mainVp.setCellVerticalAlignment(commentTitle,
					HasVerticalAlignment.ALIGN_TOP);
			// mainVp.add(commentTitle);
			mainVp.add(hp0);
			mainVp.add(hp);
			submit.setEnabled(false);
			discard.setEnabled(false);
			// commentText.setStyleName(COMMENT_EDITOR_STYLE);
			// commentText.setWidth("40em");
			// commentText.setHeight("10em");
			commentText.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					if (commentText.getText().trim().equals("")) {
						commentUnsaved = false;
					} else {
						commentUnsaved = true;
					}
					submit.setEnabled(commentUnsaved);
					discard.setEnabled(commentUnsaved);
					ck.setEnabled(commentUnsaved);
					ck.setChecked(false);
				}
			});

			submit.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (!commentUnsaved) {
						return;
					}
					String sessionId = Cookies
							.getCookie(ApplicationView.SESSION_ID_NAME);
					Integer owner = currentOccurrence.getOwner();
					OccurrenceComments comment = new OccurrenceComments();
					comment.setOccurrenceId(currentOccurrence.getId());
					comment.setUserComment(commentText.getText());
					comment.setDateCommented(new Date());
					Set<OccurrenceComments> comments = new HashSet<OccurrenceComments>();
					comments.add(comment);
					DataSwitch.get().updateComments(sessionId, owner, comments, ck.getValue(),
							new AsyncCallback<Integer>() {
								public void onFailure(Throwable caught) {
									GWT.log(caught.getLocalizedMessage(),
											caught);
									Window.alert(caught.getLocalizedMessage());
								}

								public void onSuccess(Integer result) {
									if (result != null && result > 0) {
										setEditorStyle(false);
										loadUserComment();
									} else {
										Window.alert(constants
												.UnexpectedError());
									}

								}

							});

				}

			});
			discard.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (!Window.confirm("Discard your comment?")) {
						return;
					}
					setEditorStyle(false);
				}

			});
			commentText.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (!commentUnsaved) {
						setEditorStyle(true);
					}

				}

			});

			commentText.addBlurHandler(new BlurHandler() {
				public void onBlur(BlurEvent event) {
					if (commentText.getText().equals("")) {
						setEditorStyle(false);
					}

				}
			});
		}

		public boolean isCommentUnsaved() {
			return commentUnsaved;
		}

		public void saveData() {
			submit.click();
		}

		public void setEditorStyle(boolean isEditing) {
			if (!isEditing) {
				commentText.setText(DEFAULT_COMMENT_TEXT);
				commentText.setStyleName(COMMENT_EDITOR_STYLE + "-Viewing");
				submit.setEnabled(false);
				discard.setEnabled(false);
				commentUnsaved = false;
			} else {
				commentText.setText("");
				commentText.setStyleName(COMMENT_EDITOR_STYLE + "-Editing");
			}
		}

		public void setFocus(boolean focused) {
			commentText.setFocus(focused);
		}
	}

	/**
	 * A tree items contains all its fields and value.
	 * 
	 */
	private abstract class DetailItem extends TreeItem implements
			FieldChangeListener {

		private boolean editable;

		/**
		 * True it is already loaded. it is used for lazy loading.
		 */
		private boolean loaded;

		/**
		 * Initializes the OccDetailItem with a given tree node name.
		 * 
		 * A loading item is initially added for the expand button to appear. It
		 * is deleted from the tree when this tree is loaded.
		 * 
		 * @param rootName
		 *            the name of this tree node.
		 */
		DetailItem(String rootName) {
			super(rootName);
			addItem("loading");
		}

		/**
		 * Gets the {@link FieldEditor} that have the same name with the given
		 * OccurrenceFieldEditor
		 * 
		 * @param editor
		 *            {@link FieldEditor}
		 * @return {@link FieldEditor}
		 */
		public FieldEditor getEditor(String fieldName) {
			for (int i = 0; i < getChildCount(); i++) {
				FieldEditor editor = (FieldEditor) getChild(i).getWidget();
				if (editor != null && fieldName.equals(editor.getName())) {
					return editor;
				}
			}
			return null;
		}

		public boolean isLoaded() {
			return loaded;
		}

		/**
		 * Enables save buttons when there is a change detected. Updates marker
		 * position if the {@link FieldConstants#DECIMAL_LATITUDE} and
		 * {@link FieldConstants#DECIMAL_LONGITUDE} is changed.
		 */
		public void onChanged(FieldEditor fieldEditor) {
			if (fieldEditor.isChanged()) {
				setUpdateButtonsEnable(true);
				isDataUnsaved = true;
			}
			String fieldName = fieldEditor.getName();
			try {
				if (fieldName.equals(FieldConstants.DECIMAL_LATITUDE)) {
					newLat = LOWER_BOUND - 1;
					newLat = Double.parseDouble(fieldEditor.getValue());
					if (newLat < LOWER_BOUND || newLat > UPPER_BOUND) {
						fieldEditor.setError(true, constants.OutOfBound());
					}
				} else if (fieldName.equals(FieldConstants.DECIMAL_LONGITUDE)) {
					newLng = LOWER_BOUND - 1;
					newLng = Double.parseDouble(fieldEditor.getValue());
					if (newLng < LOWER_BOUND || newLng > UPPER_BOUND) {
						fieldEditor.setError(true, constants.OutOfBound());
					}
				}
				updateMarkerPosition();
			} catch (Exception e) {
				fieldEditor.setError(true, constants.InvalidDecimalValue());
				smallMap.setCenter();
			}

		}

		/**
		 * Restores all items of this tree to their original text.
		 */
		public void restoreOriginalValues() {
			if (loaded) {
				for (int i = 0; i < super.getChildCount(); i++) {
					FieldEditor editor = (FieldEditor) super.getChild(i)
							.getWidget();
					editor.restoreOriginal();
				}
				loaded = false;
			}

		}

		/**
		 * Sets all {@link FieldEditor} belong to this tree to editable or not
		 * ediable.
		 * 
		 * Note: all fields in {@link FieldConstants#NON_EDITABLE_FIELDS} will
		 * not be set to be editable.
		 * 
		 * @param editable
		 *            true if it is editable.
		 */
		public void setFieldsEditable(boolean editable) {
			this.editable = editable;
			if (loaded) {
				for (int i = 0; i < getChildCount(); i++) {
					FieldEditor editor = (FieldEditor) getChild(i).getWidget();
					if (!FieldConstants.NON_EDITABLE_FIELDS.contains(editor
							.getName())) {
						editor.setEditable(editable);
					}
				}
			}
		}

		/**
		 * Sets whether this tree is loaded.
		 * 
		 * @param loaded
		 *            true if this tree haven't yet initialized or it need to be
		 *            reset with new values.
		 */
		public void setLoaded(boolean loaded) {
			this.loaded = loaded;

		}

		/**
		 * Adds the given {@link FieldEditor} if it is not yet in the tree.
		 * Resets this field to the given editor value if it is already in the
		 * tree. An editor is considered to be in the child items if there is an
		 * item with same field name existed in its child items.
		 * 
		 * @param editor
		 *            {@link FieldEditor}
		 */
		void addFieldEditorItem(FieldEditor editor) {
			String editorName = editor.getName();
			FieldEditor oEditor = getEditor(editorName);
			boolean isEditable;
			if (FieldConstants.NON_EDITABLE_FIELDS.contains(editorName)) {
				isEditable = false;
			} else {
				isEditable = editable;
			}
			if (oEditor == null) {
				addItem(editor);
				editor.addChangeListener(this);
				editor.setEditable(isEditable);
			} else {
				oEditor.setOriginalText(editor.getOriginalText());
				oEditor.restoreOriginal();
				oEditor.setEditable(isEditable);
			}
		}

		/**
		 * Fires all {@link FieldEditor#fireOnSaved()} that belong to this tree
		 * item.
		 */
		void fireOnSaved() {
			if (loaded) {
				for (int i = 0; i < super.getChildCount(); i++) {
					FieldEditor editor = (FieldEditor) super.getChild(i)
							.getWidget();
					editor.fireOnSaved();
				}
			}
		}

		/**
		 * Loads all items from the {@link DetailView#currentOccurrence} that
		 * belong to this tree.
		 */
		protected abstract void loadTable();
	}

	private class DetailPrintView extends VerticalPanel implements
			SelectionHandler<Integer> {
		private boolean loaded = false;

		public DetailPrintView() {
			// setStyleName("Print-View");
		}

		public void addTable(DetailItem item) {
			if (!item.isLoaded()) {
				item.loadTable();
				item.setLoaded(true);
			}
			HTML tableName = new HTML("<b>" + item.getText() + "</b>");
			FlexTable table = new FlexTable();
			table.setBorderWidth(1);
			for (int i = 0; i < item.getChildCount(); i++) {
				FieldEditor editor = (FieldEditor) item.getChild(i).getWidget();
				table.setText(i, 0, editor.getName());
				table.setText(i, 1, editor.getValue());
			}
			add(tableName);
			add(table);
		}

		public boolean isLoaded() {
			return loaded;
		}

		public void onSelection(SelectionEvent<Integer> event) {
			if (event.getSelectedItem() == 1 && !isLoaded()) {
				for (int i = 0; i < detailTree.getItemCount(); i++) {
					DetailItem item = (DetailItem) detailTree.getItem(i);
					addTable(item);
					loaded = true;
				}
			}
		}

	}

	private static class DetailViewInfo extends ViewInfo implements
			OccurrenceListener {
		private DetailView detailView;
		private final PageListener<Occurrence> pageListener;
		private final View parent;
		private final OccurrenceQuery query;

		DetailViewInfo(View parent, PageListener<Occurrence> pageListener,
				OccurrenceQuery query) {
			this.parent = parent;
			this.pageListener = pageListener;
			this.query = query;
		}

		public void onOccurrenceSelected(Occurrence occurrence) {
			getView();
			detailView.onOccurrenceSelected(occurrence, true);

		}

		protected View constructView() {
			detailView = new DetailView(parent, pageListener, query);
			return detailView;
		}

		protected String getHisTokenName() {
			return DETAIL;
		}

		protected String getName() {
			return DETAIL;
		}

	}

	private class OccurrenceLink extends HTML implements ClickHandler {
		private final Occurrence occurrence;

		public OccurrenceLink(Occurrence o) {
			super();
			occurrence = o;
			setHTML(o.getId() + "");
			setStyleName("link");
			addStyleName("occurrenceLink");
			addClickHandler(this);
		}

		public void onClick(ClickEvent event) {
			if (currentSelectedLink == this) {
				return;
			}
			select(true);
		}

		public void select(boolean selected) {
			if (selected) {
				if (currentSelectedLink != null) {
					currentSelectedLink.removeStyleName(SELECTED_STYLE);
				}
				currentSelectedLink = this;
				addStyleName(SELECTED_STYLE);
				onOccurrenceSelected(occurrence, true);
			} else {
				removeStyleName(SELECTED_STYLE);
				if (currentSelectedLink == this) {
					currentSelectedLink = null;
				}
			}
		}
	}

	private class PrintView extends Composite implements ClickHandler {
		private final List<Widget> previousWidgets = new ArrayList<Widget>();
		private final VerticalPanel printPanel = new VerticalPanel();

		public PrintView() {
			initWidget(printPanel);
			setStyleName("Print-View");
			HTML printLink = new HTML("Print");
			Button back = new Button("Back to Detail");
			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.setSpacing(5);
			buttonPanel.add(printLink);
			buttonPanel.add(back);
			printPanel.add(buttonPanel);
			printLink.setStyleName("link");
			printLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Window.print();
				}
			});
			back.addClickHandler(this);
		}

		public void addTable(DetailItem item) {
			if (!item.isLoaded()) {
				item.loadTable();
				item.setLoaded(true);
			}
			HTML tableName = new HTML("<b>" + item.getText() + "</b>");
			FlexTable table = new FlexTable();
			table.setBorderWidth(1);
			for (int i = 0; i < item.getChildCount(); i++) {
				FieldEditor editor = (FieldEditor) item.getChild(i).getWidget();
				table.setText(i, 0, editor.getName());
				table.setText(i, 1, editor.getValue());
			}
			printPanel.add(tableName);
			printPanel.add(table);
		}

		public void clearTable() {
			Widget buttonPanel = printPanel.getWidget(0);
			printPanel.clear();
			printPanel.add(buttonPanel);
		}

		// public void hide() {
		// RootPanel root = RootPanel.get();
		// root.remove(this);
		// for (Widget w : previousWidgets) {
		// root.add(w);
		// }
		// }

		public void onClick(ClickEvent event) {
			mainSp.setWidget(mainPanel);
			addHistoryItem(false);

		}

		// public void show() {
		// RootPanel root = RootPanel.get();
		// for (int i = 0; i < root.getWidgetCount(); i++) {
		// previousWidgets.add(root.getWidget(i));
		// }
		// for (int i = 0; i < root.getWidgetCount(); i++) {
		// root.remove(i);
		// }
		//
		// RootPanel.get().add(this);
		// }

	}

	private class ReviewersReviewed extends Composite {
		StaticTable reviewerTable;
		Label status;

		public ReviewersReviewed() {
			VerticalPanel mainPanel = new VerticalPanel();
			status = new Label();
			reviewerTable = new StaticTable(constants.Reviewers());
			reviewerTable.setHeaders(REVIEWER_TABLE_HEAER,
					REVIEWER_TABLE_HEADER_CSS);
			mainPanel.add(status);
			mainPanel.add(reviewerTable);
			// reviewerTable.setText(0, 1, constants)
			initWidget(mainPanel);
		}

		public void addReviewer(OccurrenceReview reviewer) {
			Widget data[] = new Widget[4];
			data[0] = new Label(reviewer.getName());
			data[1] = new Label(reviewer.getEmail());
			Boolean reviewed = reviewer.getReviewed();
			String image = reviewed == null ? WAITING_IMG_URL
					: (reviewed ? THUMB_UP_URL : THUMB_DOWN_URL);
			data[2] = new HTML("<img src='" + image + "'/>");
			data[3] = new Label(reviewer.getReviewedDate() + "");
			data[2].addStyleName("center");
			reviewerTable.addDataRows(data);
		}

		public void clear() {
			reviewerTable.clear();
		}

		public void setError(String errorMsg) {
			status.setText(errorMsg);
			status.addStyleName("red");
			status.removeStyleName("blue");
		}

		public void setLoaded(boolean isLoaded) {
			if (isLoaded) {
				status.setText("");
			} else {
				status.setText(constants.Loading());
				status.addStyleName("blue");
				status.removeStyleName("red");
			}
		}

	}

	/**
	 * A small google map with a fixed size. Displays at most one marker at a
	 * time.
	 * 
	 * 
	 */
	private class SmallMap extends Composite {

		private final MapWidget map;
		private Marker marker = null;

		/**
		 * Initializes smalll map and center it at Madagascar.
		 */
		public SmallMap() {
			MapOptions options = MapOptions.newInstance();
			options.setCenter(CENTER);
			options.setZoom(6);
			options.setMapTypeId(MapTypeId.TERRAIN);
			options.setScaleControl(true);
			// options.setDraggableCursor("crosshair");
			// options.setDraggingCursor("move");
			MapTypeControlOptions mapTypeControlOptions = MapTypeControlOptions
					.newInstance();
			mapTypeControlOptions.setMapTypeIds(MapTypeId.values());
			options.setMapTypeControlOptions(mapTypeControlOptions);
			options.setMapTypeControl(true);
			map = new MapWidget(options);
//			 MapTypeRegistry registry = MapTypeRegistry.newInstance();
//			 registry.set(MapTypeId.HYBRID.toString(), MapTypeId.HYBRID);
//			 registry.set(MapTypeId.ROADMAP.toString(), MapTypeId.ROADMAP);
//			 registry.set(MapTypeId.TERRAIN.toString(), MapTypeId.TERRAIN);
//			 registry.set(MapTypeId.SATELLITE.toString(),
//			 MapTypeId.SATELLITE);
//			 map.setMapTypesRegistry(registry);
//			 map.setWidth("100%");
//			 map.setHeight("100%");
			// initMap();
			map.setStyleName(SMALL_MAP_STYLE);
			initWidget(map);
			Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			      public void execute() {
			    	  /*
			    	   * Forcer le rechargement de la carte.
			    	   * Sans cette partie de code, il faut redimentionner manuellement le navigateur pour avoir un map complet
			    	   */
			    	  map.triggerResize();
						map.setZoom(map.getZoom());
				    	  map.triggerResize();
						setCenter();
			      }
			});
		}

		/**
		 * Sets center of this map to Madagascar.
		 */
		public void setCenter() {
			// map.setOverlayMapTypes(null);
			if (map.getOverlayMapTypes() != null) {
				map.getOverlayMapTypes().clear();
			}
			setMarker(CENTER);
		}

		/**
		 * Adds marker at a given latitude and longitude and center it there.
		 * 
		 * @param latitude
		 *            double latitude of the marker
		 * @param longitude
		 *            double longitude of the marker
		 */
		public void setMarker(LatLng latLng) {
			if (marker == null) {
				MarkerOptions markerOptions = MarkerOptions.newInstance();
				MarkerImage icon = MarkerImage.newInstance(DEFAULT_MARKER_ICON);
				markerOptions.setIcon(icon);
				markerOptions.setDraggable(true);
				markerOptions.setPosition(latLng);
				marker = Marker.newInstance(markerOptions);
				marker.setDraggable(false);
				marker.addDragHandler(new DragMapHandler() {
					@Override
					public void onEvent(DragMapEvent event) {
						FieldEditor latEditor = requiredItems
								.getEditor(FieldConstants.DECIMAL_LATITUDE);
						FieldEditor lngEditor = requiredItems
								.getEditor(FieldConstants.DECIMAL_LONGITUDE);
						LatLng latlng = marker.getPosition();
						NumberFormat numberFormat = NumberFormat
								.getFormat("#.0000000#");
						latEditor.setValue(numberFormat.format(latlng.getLatitude()));
						lngEditor.setValue(numberFormat.format(latlng
								.getLongitude()));
					}
				});
				marker.addDragEndHandler(new DragEndMapHandler() {
					@Override
					public void onEvent(DragEndMapEvent event) {
						FieldEditor latEditor = requiredItems
								.getEditor(FieldConstants.DECIMAL_LATITUDE);
						FieldEditor lngEditor = requiredItems
								.getEditor(FieldConstants.DECIMAL_LONGITUDE);
						latEditor.setNewValue();
						lngEditor.setNewValue();

					}
				});
				// MarkerImage shadow =
				// MarkerImage.newInstance(DEFAULT_MARKER_SHADOW_URL);
				marker.setIcon(icon);
			}
			if(currentOccurrence != null){
				marker.setMap((MapWidget)null);
				OccurrenceMarkerManager markerManager = OccurrenceMarkerManager.newInstance(currentOccurrence);
				marker = markerManager.getMarker();
			}
			// marker.setShadow(shadow);
			// marker.getIcon_MarkerImage().setUrl(DEFAULT_MARKER_ICON);
			// marker.getShadow_MarkerImage().setUrl(DEFAULT_MARKER_SHADOW_URL);
			// map.setOverlayMapTypes(null);
			marker.setPosition(latLng);
			marker.setMap(map);
			map.setZoom(10);
			map.setCenter(latLng);
		}

		public void setMarkerDragEnabled(boolean enabled) {
			if (marker != null) {
				marker.setDraggable(enabled);
			}
		}

		/**
		 * Initializes this map with small map control and set a fixed with to
		 * it.
		 */
		private void initMap() {
			// map.addControl(new SmallMapControl());
			// map.addControl(new MapTypeControl(true));
			// map.setMapTypeId(MapTypeId.TERRAIN);
			// map.setScrollWheelZoomEnabled(true);
			// map.setPixelSize(MAP_WIDTH, MAP_HEIGHT);
		}
	}

	/**
	 * Controls tree expands all and tree collapse all actions.
	 * 
	 * @author tri
	 * 
	 */
	private class TreeExpansion extends Composite {
		private final HTML collapseLink = new HTML(constants.CollapseAll());
		private final HTML expandLink = new HTML(constants.ExpandAll());

		public TreeExpansion() {
			HorizontalPanel panel = new HorizontalPanel();
			panel.setStyleName(EXPAND_LINKS_STYLE);
			panel.add(expandLink);
			expandLink.setStyleName("link");
			panel.add(new HTML("&nbsp;&nbsp;"));
			panel.add(collapseLink);
			collapseLink.setStyleName("link");
			panel.add(new HTML("&nbsp;&nbsp;"));
			initWidget(panel);
			expandLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					expandsAll();
				}

			});
			collapseLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					collapseAll();
				}

			});
		}
	}

	/**
	 * Update buttons consists of "save changes" button and cancel button and is
	 * used to save or cancel occurrence changes.
	 * 
	 * @author tri
	 * 
	 */
	private class UpdateButtons extends Composite {
		private final Button cancel = new Button(constants.Cancel());
		private final Button save = new Button(constants.SaveChanges());

		public UpdateButtons() {
			HorizontalPanel panel = new HorizontalPanel();
			panel.add(save);
			panel.add(new HTML("&nbsp;&nbsp"));
			panel.add(cancel);
			panel.add(new HTML("&nbsp;&nbsp"));
			panel.setStyleName(UPDATE_BUTTONS_STYLE);
			initWidget(panel);
			save.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					saveChanges(true);

				}

			});
			cancel.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					restoreOriginal();
					setUpdateButtonsEnable(false);
					isDataUnsaved = false;
				}

			});
		}
	}

  private static final String WAITING_IMG_URL = "images/waiting.png";

  private static final String THUMB_UP_URL = "images/thumb_up.png";

  private static final String THUMB_DOWN_URL = "images/question_mark.png";
  private static final String REVIEWER_TABLE_HEAER[] = new String[] {
      constants.Name(), constants.Email(), constants.Reviewed(),
      constants.ReviewDate() };
  private static final String REVIEWER_TABLE_HEADER_CSS[] = new String[] {
      "name", "email", "reviewed", "reviewed_date" };

	/**
	 * Default style name for this widget.
	 */
	public static final String DEFAULT_STYLE = "OccurrenceView-Detail";

	public static final String DETAIL_STYLE = "Detail";

	public static final String FOOTER_STYLE = "Footer";

	/**
	 * OccurrenceDetailView id history token param name.
	 */
	public static final String ID = "id";
	public static final String TOOL_STYLE = "Tool";

	private static final String ADD_COMMENT_MESSAGE = "Add a Comment";
	/**
	 * Madagascar latitude and longitude {@link LatLng}.
	 */
	private static final LatLng CENTER = LatLng.newInstance(-19, 47);

	private static final String COMMENT_EDITOR_STYLE = "Editor";

	private static final String COMMENT_STYLE = "Comment";

	private static final String COMMENT_TITLE_STYLE = "Title";
	/**
	 * Dawin Core field.
	 */
	private static final String CORE_FIELDS = constants.DarwinCore();
	/**
	 * Curatorial Extension field
	 */
	private static final String CURA_FIELDS = constants.CuratorialExtension();
	private static final String DEFAULT_COMMENT_TEXT = "Enter your comments";

	private static final String DEFAULT_MARKER_ICON = "http://maps.google.com/mapfiles/marker.png";

	private static final String DEFAULT_MARKER_SHADOW_URL = "http://www.google.com/mapfiles/shadow50.png";

	/**
	 * Environmental Variables field.
	 */
	private static final String ENV_FIELDS = constants.EnvironmentalVariables();

	private static final String EXPAND_LINKS_STYLE = "Expand-Links";

	/**
	 * Geospatial Extension field.
	 */
	private static final String GEO_FIELDS = constants.GeospatialExtension();

	private static final int LOWER_BOUND = -90;

	private static ViewInfo occurrenceViewInfo = null;
	/**
	 * Required Field
	 */
	private static final String REQUIRED_FIELDS = constants.RequiredFields();

	private static final String SMALL_MAP_STYLE = "Small-Map";
	private static final int SMALL_MAP_WIDTH = 300;
	private static final int SMALL_MAP_HEIGHT = 300;

	/**
	 * Taxonomy Authority field.
	 */
	private static final String TAXO_FIELDS = constants.TaxonomyAuthority();

	private static final String UPDATE_BUTTONS_STYLE = "Update-Buttons";

	private static final int UPPER_BOUND = 90;

	private static final String VIEW_TOKEN_VALUE = DETAIL;

	private static final String SELECTED_STYLE = "Selected";

	public static ViewInfo init(View parent,
			PageListener<Occurrence> pageListener, OccurrenceQuery query) {
		if (occurrenceViewInfo == null) {
			occurrenceViewInfo = new DetailViewInfo(parent, pageListener, query);
		}
		return occurrenceViewInfo;
	}

	private final HTML addCommentLink = new HTML("Add a comment below:");

	// private VerticalPanel commentMapVp = new VerticalPanel();
	private final DetailCommentEditor commentEditor = new DetailCommentEditor();

	private final VerticalPanel commentMapVp = new VerticalPanel();

	/**
	 * Darwin core tree
	 */
	private final DetailItem coreItems;

	/**
	 * Curatorial Extension tree
	 */
	private final DetailItem curaItems;

	/**
	 * A current selected {@link Occurrence}
	 */
	private Occurrence currentOccurrence = null;

	/**
	 * A current selected {@link FieldEditor}
	 */
	private FieldEditor currentSelectedEditor = null;

	private final DetailComment detailComment = new DetailComment();

	/**
	 * The main {@link Tree} to display all {@link Occurrence} fields.
	 */
	private final Tree detailTree = new Tree();
	/**
	 * Environmental Variables tree
	 */
	private final DetailItem envItems;
	/**
	 * Geospatial Extension tree
	 */
	private final DetailItem geoItems;

	/**
	 * A back link to go to the previous view.
	 */
	private final HTML instruction = new HTML();

	private boolean isDataUnsaved = false;

	/**
	 * A main {@link VerticalPanel} for this tree.
	 */
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final ScrollPanel mainSp = new ScrollPanel();

	/**
	 * A main map widget {@link MapWidget}.
	 */
	private double newLat = LOWER_BOUND - 1;

	private double newLng = LOWER_BOUND - 1;

	/**
	 * Required fields tree
	 */
	private final DetailItem requiredItems;

	private boolean signedIn = false;
	/**
	 * A google map display the current selected point
	 */
	private final SmallMap smallMap;
	/**
	 * Taxonomy Authority tree
	 */
	private final DetailItem taxoItems;
	/**
	 * A {@link HorizontalPanel} widget to display links and buttons on the top
	 * if this view.
	 */
	private final HorizontalPanel toolPanel = new HorizontalPanel();
	/**
	 * Bottom {@link UpdateButtons} (i.e Save Changes and cancel buttons)
	 */
	private final UpdateButtons updateButtonsBottom;

	/**
	 * Top {@link UpdateButtons} (i.e Save Changes and cancel buttons)
	 */
	private final UpdateButtons updateButtonsTop;

	private final OccurrencePagerWidget pagerWidget;

	// private final PrintView printView = new PrintView();

	private final HorizontalPanel occLinks = new HorizontalPanel();

	private final HistoryState historyState = new HistoryState() {
		public Object getHistoryParameters(UrlParam param) {
			switch (param) {
			case VIEW:
			case ID:
				return stringValue(param);
			}
			return "";
		}

	};

	private OccurrenceLink currentSelectedLink;
	private final ReviewersReviewed reviewersReviewed;

	/**
	 * Initializes OccurrenceDetailView
	 * 
	 * @param pageListener
	 *            TODO
	 * @param changeListener
	 *            TODO
	 */
	public DetailView(View parent, PageListener<Occurrence> pageListener,
			OccurrenceQuery query) {
		super(parent, false);
		smallMap = new SmallMap();
		int pageSize = query.getLimit();
		if (pageSize < 0) {
			pageSize = OccurrencePagerWidget.DEFAULT_PAGE_SIZE;
		}
		pagerWidget = new OccurrencePagerWidget(pageSize, query, false);
		pagerWidget.addPageClickListener(this);
		pagerWidget.addPageListener(pageListener);
		pagerWidget.addPageListener(this);
		pagerWidget.addPageSizeChangeListener(this);
		query.addDataRequestListener(this);
		addCommentLink.setStyleName("link");
		addCommentLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				commentEditor.setFocus(true);
				commentEditor.setEditorStyle(true);
			}

		});
		commentMapVp.setStyleName("Left");
		commentMapVp.add(smallMap);
		commentMapVp.add(addCommentLink);
		commentMapVp.add(detailComment);
		commentMapVp.add(commentEditor);
		updateButtonsTop = new UpdateButtons();
		updateButtonsBottom = new UpdateButtons();
		reviewersReviewed = new ReviewersReviewed();
		HorizontalPanel hp = new HorizontalPanel();
		VerticalPanel vp = new VerticalPanel();
		// vp.add(updateButtonsTop);
		vp.add(new TreeExpansion());
		vp.add(detailTree);
		vp.add(new TreeExpansion());
		vp.add(updateButtonsBottom);
		hp.add(commentMapVp);
		hp.add(vp);
		hp.add(reviewersReviewed);
		hp.setCellVerticalAlignment(reviewersReviewed,
				HasVerticalAlignment.ALIGN_TOP);
		hp.setCellHorizontalAlignment(reviewersReviewed,
				HasHorizontalAlignment.ALIGN_LEFT);
		SimplePanel toolContainer = new SimplePanel();
		toolContainer.setWidget(toolPanel);
		toolContainer.setStyleName(TOOL_STYLE);
		mainPanel.add(toolContainer);
		mainPanel.add(hp);
		mainPanel.setStyleName(DEFAULT_STYLE);
		toolPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		toolPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE); // CHANGED
		initTool();
		detailTree.setStyleName(DETAIL_STYLE);
		mainSp.setWidget(mainPanel);
		initWidget(mainSp);
		setUpdateButtonsEnable(false);
		requiredItems = new DetailItem(REQUIRED_FIELDS) {

			protected void loadTable() {
				loadRequiredItems();
			}
		};
		envItems = new DetailItem(ENV_FIELDS) {

			protected void loadTable() {
				if (OccurrenceSummary.isAscLayersLoaded(currentOccurrence)) {
					loadEnvItems();
					return;
				}
				DataSwitch.get().loadAscData(currentOccurrence,
						new AsyncCallback<Occurrence>() {
							public void onFailure(Throwable caught) {
								Window.confirm(caught.getMessage());
								GWT.log(caught.getMessage(), caught);

							}

							public void onSuccess(Occurrence result) {
								loadEnvItems();
							}

						});

			}

		};
		curaItems = new DetailItem(CURA_FIELDS) {

			protected void loadTable() {
				loadCuraItems();

			}

		};

		geoItems = new DetailItem(GEO_FIELDS) {

			protected void loadTable() {
				loadGeoItems();

			}

		};

		coreItems = new DetailItem(CORE_FIELDS) {

			protected void loadTable() {
				loadCoreItems();

			}

		};

		taxoItems = new DetailItem(TAXO_FIELDS) {

			protected void loadTable() {
				loadTaxoItems();

			}

		};

		detailTree.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				FieldEditor editor = (FieldEditor) detailTree.getSelectedItem()
						.getWidget();

				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						&& editor != null) {
					editor.setEditorField();
				}

			}
		});
		// detailTree.addTreeListener(this);

		detailTree.addItem(requiredItems);
		detailTree.addItem(coreItems);
		detailTree.addItem(envItems);
		detailTree.addItem(curaItems);
		detailTree.addItem(taxoItems);
		detailTree.addItem(geoItems);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				String historyToken = History.getToken();
				if (!historyToken.equals("") && isMyView(historyToken)) {
					handleOnValueChange(historyToken);
				}

			}

		});
		detailTree.addOpenHandler(this);
		detailTree.addSelectionHandler(this);
		detailTree.addCloseHandler(new CloseHandler<TreeItem>() {
			/**
			 * Resize the detail panel when the an tree item is close.
			 * 
			 * @param event
			 */
			public void onClose(CloseEvent<TreeItem> event) {
				resize(Window.getClientWidth(), Window.getClientHeight());
			}

		});
	}

	public boolean containsUnsavedData() {
		return isDataUnsaved || commentEditor.isCommentUnsaved();
	}

	public String historyToken() {
		StringBuilder sb = new StringBuilder();
		String id = "";
		boolean isPrintView = false;
		if (currentOccurrence == null) {
			String historyToken = History.getToken();
			id = getIdFromHistoryToken(historyToken);
			isPrintView = Boolean.parseBoolean(this
					.getIsPrintViewFromHistoryToken(historyToken));
		} else {
			id = currentOccurrence.getId() + "";
			// isPrintView = mainSp.getWidget() == this.printView;
		}
		sb.append(ID + "=" + id);
		sb.append("&p=" + isPrintView);
		return sb.toString();
	}

	/**
	 * Resets currentOccurrence to the given occurrence then load Occurrence
	 * field values. Also disable editable fields if current selected occurrence
	 * is not belong to the current user.
	 * 
	 */
	public void onOccurrenceSelected(Occurrence occurrence,
			boolean isAddHistoryItem) {
		if (currentOccurrence == null || currentOccurrence != occurrence) {
			mainSp.setPixelSize(Window.getClientWidth(),
					Window.getClientHeight() - mainSp.getAbsoluteTop());
			requiredItems.setLoaded(false);
			coreItems.setLoaded(false);
			envItems.setLoaded(false);
			curaItems.setLoaded(false);
			taxoItems.setLoaded(false);
			geoItems.setLoaded(false);
			isDataUnsaved = false;
			setUpdateButtonsEnable(false);
			currentOccurrence = occurrence;
			collapseAll();
			User currentUser = ApplicationView.getAuthenticatedUser();
			String userEmail = occurrence.getOwnerEmail();
			boolean isEditable = (currentUser != null)
					&& (currentUser.getEmail().equals(userEmail)) && signedIn;

			setFieldsEditable(isEditable);
			setCommentEnable(signedIn);
			try {
				newLat = Double.parseDouble(currentOccurrence
						.getDecimalLatitude());
				newLng = Double.parseDouble(currentOccurrence
						.getDecimalLongitude());
				updateMarkerPosition();
			} catch (Exception e) {
				smallMap.setCenter();
			}
			requiredItems.setState(true);
			loadUserComment();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					resize(Window.getClientWidth(), Window.getClientHeight());
				}
			});

		}
		// maybeSwitchView(History.getToken());
		if (isAddHistoryItem) {
			addHistoryItem(false);
		}

	}

	/**
	 * Loads the selected OccDetailItem if it is not yet loaded when the state
	 * of this tree is changed.
	 */
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem item = event.getTarget();
		String itemText = item.getText();
		DetailItem detailItem = null;
		if (itemText.equals(REQUIRED_FIELDS)) {
			detailItem = requiredItems;
		} else if (itemText.equals(CORE_FIELDS)) {
			detailItem = coreItems;
		} else if (itemText.equals(CURA_FIELDS)) {
			detailItem = curaItems;
		} else if (itemText.equals(ENV_FIELDS)) {
			detailItem = envItems;
		} else if (itemText.equals(GEO_FIELDS)) {
			detailItem = geoItems;
		} else if (itemText.equals(TAXO_FIELDS)) {
			detailItem = taxoItems;
		}
		if (detailItem != null && !detailItem.isLoaded()) {
			detailItem.loadTable();
			detailItem.setLoaded(true);
		}
		// Resizes this the panel containing this tree when an item is expand or
		// collapse to ensure consistent behavior on all browsers.
		resize(Window.getClientWidth(), Window.getClientHeight());
	}

	public void onPageClicked() {
		parent.resetToDefaultState();
	}

	public void onPageLoaded(List<Occurrence> data, int pageNumber) {
		occLinks.clear();
		historyState.setHistoryToken(History.getToken());
		for (Occurrence o : data) {
			OccurrenceLink occLink = new OccurrenceLink(o);
			String id = historyState.getHistoryParameters(UrlParam.ID)
					.toString();
			// if (o == currentOccurrence || o.getId().toString().equals(id)) {
			occLink.select(o == currentOccurrence
					|| o.getId().toString().equals(id));
			// } else {
			// occLink.select(false);
			// }
			occLinks.add(occLink);
		}
		addHistoryItem(false);

	}

	/**
	 * 
	 * When an item is selected do the following:
	 * 
	 * If it not the root tree nodes, selected the field name only by calling
	 * {@link FieldEditor#onSelected(boolean)}.
	 * 
	 * Clears the previous selected item.
	 * 
	 */
	public void onSelection(SelectionEvent<TreeItem> event) {
		FieldEditor selectedWidget = (FieldEditor) event.getSelectedItem()
				.getWidget();
		if (currentSelectedEditor != null) {
			currentSelectedEditor.onSelected(false);
		}
		if (selectedWidget != null) {
			detailTree.setStyleName("nonSelected");
			currentSelectedEditor = selectedWidget;
			currentSelectedEditor.onSelected(true);
		} else {
			detailTree.setStyleName(DETAIL_STYLE);
		}

	}

	/**
	 * This method is called when the view is active and on screen.
	 */

	public void onShow() {
		// resize();
	}

	public void onStateChanged(ViewState state) {
		switch (state) {
		case RESEARCHER:
			signedIn = true;
			break;
		case ADMIN:
			signedIn = true;
			break;
		case REVIEWER:
			signedIn = true;
			break;
		case UNAUTHENTICATED:
			signedIn = false;
			break;
		}
		// Reloads occurrences states when application state is changed.
		if (currentOccurrence != null && isMyView(parent.historyToken())) {
			Occurrence temp = currentOccurrence;
			currentOccurrence = null;
			onOccurrenceSelected(temp, true);
		}

	}

	public void requestData(int pageNum) {
		if (isMyView(History.getToken())) {
			pagerWidget.init(pageNum);
		}
	}

	public void saveData() {
		if (isDataUnsaved) {
			saveChanges(false);
		}
		if (commentEditor.isCommentUnsaved()) {
			commentEditor.saveData();
		}
	}

	protected void handleOnValueChange(String historyToken) {
		onHistoryChanged(historyToken);
	}

	protected boolean isMyView(String value) {
		historyButtonClicked = false;
		historyState.setHistoryToken(value);
		String views = historyState.getHistoryParameters(UrlParam.VIEW) + "";
		return views.equalsIgnoreCase(DETAIL);
	}

	protected void resetToDefaultState() {
		// TODO Auto-generated method stub

	}

	protected void resize(int width, int height) {
		height = height - mainSp.getAbsoluteTop();
		if (height <= 0) {
			height = 1;
		}
		final int h = height - 10;
	    final int w = width - 22;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				mainSp.setPixelSize(w, h);

			}
		});

	}

	/**
	 * Collapse all tree nodes.
	 */
	private void collapseAll() {
		if (currentOccurrence != null) {
			requiredItems.setState(false);
			coreItems.setState(false);
			envItems.setState(false);
			curaItems.setState(false);
			taxoItems.setState(false);
			geoItems.setState(false);
		}
	}

	/**
	 * Deletes a comment.
	 * 
	 * @param comment
	 *            {@link OccurrenceComments} to be deleted.
	 */
	private void deleteComment(OccurrenceComments comment) {
		String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
		Set<OccurrenceComments> comments = new HashSet<OccurrenceComments>();
		comments.add(comment);
		DataSwitch.get().deleteComments(sessionId, comments,
				new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage(), caught);
						Window.alert(caught.getMessage());

					}

					public void onSuccess(Integer result) {
						if (result != null && result > 0) {
							loadUserComment();
						}
					}

				});
	}

	/**
	 * Expands all tree nodes.
	 */
	private void expandsAll() {
		if (currentOccurrence != null) {
			requiredItems.setState(true);
			coreItems.setState(true);
			envItems.setState(true);
			curaItems.setState(true);
			taxoItems.setState(true);
			geoItems.setState(true);
		}
	}

	private String getIdFromHistoryToken(String historyToken) {
		try {
			return ApplicationView.getHistoryTokenParamValues(historyToken,
					"id").get(0);
		} catch (Exception e) {
			return null;
		}
	}

	private String getIsPrintViewFromHistoryToken(String historyToken) {
		try {
			return ApplicationView
					.getHistoryTokenParamValues(historyToken, "p").get(0);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Initializes tool widget with widgets with back link expand link, collapse
	 * link, save button, and cancel button.
	 */
	private void initTool() {
		toolPanel.add(new HTML("&nbsp;&nbsp;"));
		toolPanel.add(instruction);
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.add(occLinks);
		scrollPanel.setWidth(Window.getClientWidth() - 700 + "px");
		toolPanel.add(scrollPanel);

		occLinks.setStyleName("OccurrenceLinks");
		instruction.setWidth("250px");
		HTML csvLink = new HTML("CSV");
		// HTML printLink = new HTML("Print");
		// printLink.setStyleName("link");
		csvLink.setStyleName("downloadlink");
		csvLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				OccurrencePagerWidget.getCsvDownloader().show(
						"id = " + currentOccurrence.getId(), null);
			}
		});
		// printLink.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// printView.clearTable();
		// for (int i = 0; i < detailTree.getItemCount(); i++) {
		// DetailItem item = (DetailItem) detailTree.getItem(i);
		// printView.addTable(item);
		// }
		// // printView.setPixelSize(Window.getClientWidth(), Window
		// // .getClientHeight());
		// // printView.show();
		// mainSp.setWidget(printView);
		// addHistoryItem(false);
		// Window.print();
		// }
		// });
		HorizontalPanel csvPanel = new HorizontalPanel();
		// csvPanel.add(printLink);
		// csvPanel.add(new HTML("&nbsp;&nbsp;"));
		csvPanel.add(pagerWidget);
		csvPanel.add(csvLink);
		csvPanel.add(new HTML("&nbsp;&nbsp;"));
		toolPanel.add(csvPanel);
		toolPanel.setCellHorizontalAlignment(csvPanel,
				HorizontalPanel.ALIGN_RIGHT);
		toolPanel.setWidth("100%");
		toolPanel.setSpacing(5);
	}

	/**
	 * Determines whether the {@link #newLat} and the {@link #newLong} is within
	 * {@link #LOWER_BOUND} and {@link #UPPER_BOUND};
	 * 
	 * @return
	 */
	private boolean isInBound() {
		return (newLat >= LOWER_BOUND && newLat <= UPPER_BOUND)
				&& (newLng >= LOWER_BOUND && newLng <= UPPER_BOUND);
	}

	/**
	 * Loads all value belong to {@link #CORE_FIELDS}
	 */
	private void loadCoreItems() {
		boolean firstLoad = coreItems.getChildCount() == 1;
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GLOBAL_UNIQUE_IDENTIFIER,
				toNoNullString(currentOccurrence.getGlobalUniqueIdentifier())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGlobalUniqueIdentifier(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DATE_LAST_MODIFIED,
				toNoNullString(currentOccurrence.getDateLastModified())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDateLastModified(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.INSTITUTION_CODE,
				toNoNullString(currentOccurrence.getInstitutionCode())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setInstitutionCode(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.COLLECTION_CODE,
				toNoNullString(currentOccurrence.getCollectionCode())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCollectionCode(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.CATALOG_NUMBER, toNoNullString(currentOccurrence
						.getCatalogNumber())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCatalogNumber(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.INFORMATION_WITHHELD,
				toNoNullString(currentOccurrence.getInformationWithheld())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setInformationWithheld(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.RELATED_INFORMATION,
				toNoNullString(currentOccurrence.getRelatedInformation())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRelatedInformation(value);
			}
		});

		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.REMARKS,
				toNoNullString(currentOccurrence.getRemarks())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRemarks(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.SCIENTIFIC_NAME,
				toNoNullString(currentOccurrence.getScientificName())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setScientificName(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.HIGHER_TAXON, toNoNullString(currentOccurrence
						.getHigherTaxon())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setHigherTaxon(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.KINGDOM,
				toNoNullString(currentOccurrence.getKingdom())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setKingdom(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.PHYLUM,
				toNoNullString(currentOccurrence.getPhylum())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPhylum(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.CLASS_,
				toNoNullString(currentOccurrence.getClass_())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setClass_(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.ORDER,
				toNoNullString(currentOccurrence.getOrder_())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setOrder_(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.FAMILY,
				toNoNullString(currentOccurrence.getFamily())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setFamily(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.INFRASPECIFIC_RANK,
				toNoNullString(currentOccurrence.getInfraspecificRank())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setInfraspecificRank(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.INFRASPECIFIC_EPITHET,
				toNoNullString(currentOccurrence.getInfraspecificEpithet())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setInfraspecificEpithet(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.AUTHOR_YEAR_OF_SCIENTIFIC_NAME,
				toNoNullString(currentOccurrence
						.getAuthorYearOfScientificName())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAuthorYearOfScientificName(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.IDENTIFICATION_QUALIFER,
				toNoNullString(currentOccurrence.getIdentificationQualifer())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setIdentificationQualifer(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.HIGHER_GEOGRAPHY,
				toNoNullString(currentOccurrence.getHigherGeography())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setHigherGeography(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.CONTINENT,
				toNoNullString(currentOccurrence.getContinent())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setContinent(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.WATER_BODY,
				toNoNullString(currentOccurrence.getWaterBody())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWaterBody(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ISLAND_GROUP, toNoNullString(currentOccurrence
						.getIslandGroup())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setIslandGroup(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.ISLAND,
				toNoNullString(currentOccurrence.getIsland())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setIsland(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.COUNTRY,
				toNoNullString(currentOccurrence.getCountry())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCountry(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.STATE_PROVINCE, toNoNullString(currentOccurrence
						.getStateProvince())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setStateProvince(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.COUNTY,
				toNoNullString(currentOccurrence.getCounty())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCounty(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.LOCALITY,
				toNoNullString(currentOccurrence.getLocality())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setLocality(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ADJUSTED_COORDINATE_UNCERTAINTY_IN_METERS,
				toNoNullString(currentOccurrence
						.getAdjustedCoordinateUncertaintyInMeters())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence
						.setAdjustedCoordinateUncertaintyInMeters(value);
			}
		});
		coreItems
				.addFieldEditorItem(new FieldEditor(
						FieldConstants.MINIMUM_ELEVATION_IN_METERS,
						toNoNullString(currentOccurrence
								.getMinimumElevationInMeters())) {

					public void onOccurrenceUpdated() {
						String value = input.getText();
						currentOccurrence.setMinimumElevationInMeters(value);
					}
				});
		coreItems
				.addFieldEditorItem(new FieldEditor(
						FieldConstants.MAXIMUM_ELEVATION_IN_METERS,
						toNoNullString(currentOccurrence
								.getMaximumElevationInMeters())) {

					public void onOccurrenceUpdated() {
						String value = input.getText();
						currentOccurrence.setMaximumElevationInMeters(value);
					}
				});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MINIMUM_DEPTH_IN_METERS,
				toNoNullString(currentOccurrence.getMinimumDepthInMeters())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinimumDepthInMeters(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MAXIMUM_DEPTH_IN_METERS,
				toNoNullString(currentOccurrence.getMaximumDepthInMeters())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaximumDepthInMeters(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.COLLECTING_METHOD,
				toNoNullString(currentOccurrence.getCollectingMethod())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCollectingMethod(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VALID_DISTRIBUTION_FLAG,
				toNoNullString(currentOccurrence.getValidDistributionFlag())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setValidDistributionFlag(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.EARLIEST_DATE_COLLECTED,
				toNoNullString(currentOccurrence.getEarliestDateCollected())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setEarliestDateCollected(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.LATEST_DATE_COLLECTED,
				toNoNullString(currentOccurrence.getLatestDateCollected())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setLatestDateCollected(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DAY_OF_YEAR, toNoNullString(currentOccurrence
						.getDayOfYear())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDayOfYear(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MONTH_COLLECTED,
				toNoNullString(currentOccurrence.getMonthCollected())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMonthCollected(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DAY_COLLECTED, toNoNullString(currentOccurrence
						.getDayCollected())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDayCollected(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.COLLECTOR,
				toNoNullString(currentOccurrence.getCollector())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCollector(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.SEX,
				toNoNullString(currentOccurrence.getSex())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setSex(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.LIFE_STAGE,
				toNoNullString(currentOccurrence.getLifeStage())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setLifeStage(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.ATTRIBUTES,
				toNoNullString(currentOccurrence.getAttributes())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAttributes(value);
			}
		});
		coreItems.addFieldEditorItem(new FieldEditor(FieldConstants.IMAGE_URL,
				toNoNullString(currentOccurrence.getImageUrl())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setImageUrl(value);
			}
		});
		if (firstLoad) {
			coreItems.removeItem(coreItems.getChild(0));
		}
	}

	private void loadCuraItems() {
		boolean firstLoad = curaItems.getChildCount() == 1;
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.CATALOG_NUMBER_NUMERIC,
				toNoNullString(currentOccurrence.getCatalogNumberNumeric())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCatalogNumberNumeric(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.IDENTIFIED_BY, toNoNullString(currentOccurrence
						.getIdentifiedBy())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setIdentifiedBy(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DATE_IDENTIFIED,
				toNoNullString(currentOccurrence.getDateIdentified())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDateIdentified(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.COLLECTOR_NUMBER,
				toNoNullString(currentOccurrence.getCollectorNumber())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCollectorNumber(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.FIELD_NUMBER, toNoNullString(currentOccurrence
						.getFieldNumber())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setFieldNumber(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_COLLECTING_DATE,
				toNoNullString(currentOccurrence.getVerbatimCollectingDate())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimCollectingDate(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_ELEVATION,
				toNoNullString(currentOccurrence.getVerbatimElevation())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimElevation(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_DEPTH, toNoNullString(currentOccurrence
						.getVerbatimDepth())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimDepth(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.TYPE_STATUS, toNoNullString(currentOccurrence
						.getTypeStatus())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setTypeStatus(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.OTHER_CATALOG_NUMBERS,
				toNoNullString(currentOccurrence.getOtherCatalogNumbers())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setOtherCatalogNumbers(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.RELATED_CATALOGED_ITEMS,
				toNoNullString(currentOccurrence.getRelatedCatalogedItems())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRelatedCatalogedItems(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DISPOSITION, toNoNullString(currentOccurrence
						.getDisposition())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDisposition(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.FIELD_NOTES, toNoNullString(currentOccurrence
						.getFieldNotes())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setFieldNotes(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.PREPARATIONS, toNoNullString(currentOccurrence
						.getPreparations())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPreparations(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GEN_BANK_NUMBER,
				toNoNullString(currentOccurrence.getGenBankNumber())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGenBankNumber(value);
			}
		});
		curaItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.INDIVIDUAL_COUNT,
				toNoNullString(currentOccurrence.getIndividualCount())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setIndividualCount(value);
			}
		});
		if (firstLoad) {
			curaItems.removeItem(curaItems.getChild(0));
		}
	}

	private void loadEnvItems() {
		boolean firstLoad = envItems.getChildCount() == 1;
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DEMELEVATION, toNoNullString(currentOccurrence
						.getDemelevation())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDemelevation(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ETP_TOTAL2000, toNoNullString(currentOccurrence
						.getEtpTotal2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setEtpTotal2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ETP_TOTALFUTURE,
				toNoNullString(currentOccurrence.getEtpTotalfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setEtpTotalfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ETP_TOTAL1950, toNoNullString(currentOccurrence
						.getEtpTotal1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setEtpTotal1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.GEOL_STRECH,
				toNoNullString(currentOccurrence.getGeolStrech())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGeolStrech(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MAX_PREC2000, toNoNullString(currentOccurrence
						.getMaxPerc2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaxPerc2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MAX_PRECFUTURE, toNoNullString(currentOccurrence
						.getMaxPercfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaxPercfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MAX_PREC1950, toNoNullString(currentOccurrence
						.getMaxPerc1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaxPerc1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MAX_TEMP2000, toNoNullString(currentOccurrence
						.getMaxTemp2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaxTemp2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MAX_TEMPFUTURE, toNoNullString(currentOccurrence
						.getMaxTempfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaxTempfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.MAXTEMP1950,
				toNoNullString(currentOccurrence.getMaxtemp1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMaxtemp1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MIN_PREC2000, toNoNullString(currentOccurrence
						.getMinPerc2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinPerc2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MIN_PRECFUTURE, toNoNullString(currentOccurrence
						.getMinPercfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinPercfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MIN_PREC1950, toNoNullString(currentOccurrence
						.getMinPerc1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinPerc1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MIN_TEMP2000, toNoNullString(currentOccurrence
						.getMinTemp2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinTemp2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MIN_TEMPFUTURE, toNoNullString(currentOccurrence
						.getMinTempfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinTempfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.MIN_TEMP1950, toNoNullString(currentOccurrence
						.getMinTemp1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setMinTemp1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.PFC1950,
				toNoNullString(currentOccurrence.getPfc1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPfc1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.PFC1970,
				toNoNullString(currentOccurrence.getPfc1970())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPfc1970(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.PFC1990,
				toNoNullString(currentOccurrence.getPfc1990())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPfc1990(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.PFC2000,
				toNoNullString(currentOccurrence.getPfc2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPfc2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REAL_MAR2000, toNoNullString(currentOccurrence
						.getRealMar2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRealMar2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REAL_MARFUTURE, toNoNullString(currentOccurrence
						.getRealMarfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRealMarfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REAL_MAR1950, toNoNullString(currentOccurrence
						.getRealMar1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRealMar1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REAL_MAT2000, toNoNullString(currentOccurrence
						.getRealMat2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRealMat2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REAL_MATFUTURE, toNoNullString(currentOccurrence
						.getRealMatfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRealMatfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REAL_MAT1950, toNoNullString(currentOccurrence
						.getRealMat1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setRealMat1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.WBPOS2000,
				toNoNullString(currentOccurrence.getWbpos2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWbpos2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.WBPOSFUTURE,
				toNoNullString(currentOccurrence.getWbposfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWbposfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.WBPOS1950,
				toNoNullString(currentOccurrence.getWbpos1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWbpos1950(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.WBYEAR2000,
				toNoNullString(currentOccurrence.getWbyear2000())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWbyear2000(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.WBYEARFUTURE, toNoNullString(currentOccurrence
						.getWbyearfuture())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWbyearfuture(value);
			}
		});
		envItems.addFieldEditorItem(new FieldEditor(FieldConstants.WBYEAR1950,
				toNoNullString(currentOccurrence.getWbyear1950())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setWbyear1950(value);
			}
		});
		if (firstLoad) {
			envItems.removeItem(envItems.getChild(0));
		}
	}

	private void loadGeoItems() {
		boolean firstLoad = geoItems.getChildCount() == 1;
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.POINT_RADIUS_SPATIAL_FIT,
				toNoNullString(currentOccurrence.getPointRadiusSpatialFit())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPointRadiusSpatialFit(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_COORDINATES,
				toNoNullString(currentOccurrence.getVerbatimCoordinates())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimCoordinates(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_LATITUDE,
				toNoNullString(currentOccurrence.getVerbatimLatitude())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimLatitude(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_LONGITUDE,
				toNoNullString(currentOccurrence.getVerbatimLongitude())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimLongitude(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_COORDINATE_SYSTEM,
				toNoNullString(currentOccurrence.getVerbatimCoordinateSystem())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimCoordinateSystem(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GEOREFERENCE_PROTOCOL,
				toNoNullString(currentOccurrence.getGeoreferenceProtocol())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGeoreferenceProtocol(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GEOREFERENCE_SOURCES,
				toNoNullString(currentOccurrence.getGeoreferenceSources())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGeoreferenceSources(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GEOREFERENCE_VERIFICATION_STATUS,
				toNoNullString(currentOccurrence
						.getGeoreferenceVerificationStatus())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGeoreferenceVerificationStatus(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GEOREFERENCE_REMARKS,
				toNoNullString(currentOccurrence.getGeoreferenceRemarks())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGeoreferenceRemarks(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.FOOTPRINT_WKT, toNoNullString(currentOccurrence
						.getFootprintWkt())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setFootprintWkt(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.FOOTPRINT_SPATIAL_FIT,
				toNoNullString(currentOccurrence.getFootprintSpatialFit())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setFootprintSpatialFit(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_NOMENCLATURAL_CODE,
				toNoNullString(currentOccurrence.getAcceptedNomenclaturalCode())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedNomenclaturalCode(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DEC_LAT_IN_WGS84,
				toNoNullString(currentOccurrence.getDecLatInWgs84())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDecLatInWgs84(value);
			}
		});
		geoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DEC_LONG_IN_WGS84,
				toNoNullString(currentOccurrence.getDecLongInWgs84())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDecLongInWgs84(value);
			}
		});
		if (firstLoad) {
			geoItems.removeItem(geoItems.getChild(0));
		}
	}

	private void loadPrintView() {
		// printView.clearTable();
		// for (int i = 0; i < detailTree.getItemCount(); i++) {
		// DetailItem item = (DetailItem) detailTree.getItem(i);
		// printView.addTable(item);
		// }
		// // printView.setPixelSize(Window.getClientWidth(), Window
		// // .getClientHeight());
		// // printView.show();
		// mainSp.setWidget(printView);
	}

	/**
	 * Loads the current occurrence require fields and value to the first item
	 * of the detail tree;
	 */
	private void loadRequiredItems() {
		boolean firstLoad = requiredItems.getChildCount() == 1;
		requiredItems.addFieldEditorItem(new FieldEditor(FieldConstants.ID,
				toNoNullString(currentOccurrence.getId())) {

			public void onOccurrenceUpdated() {
				// do nothing.
			}
		});
		/*
		 * requiredItems.addFieldEditorItem(new
		 * FieldEditor(FieldConstants.OWNER,
		 * toNoNullString(currentOccurrence.getOwnerEmail())) { public void
		 * onOccurrenceUpdated() { // do nothing } });
		 */
		String email = null;
		if (OccurrenceSummary.isEmailVisisble(currentOccurrence)) {
			email = currentOccurrence.getOwnerEmail();
		} else {
			email = constants.EmailNotShow();
		}
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.OWNER_EMAIL, toNoNullString(email)) {

			public void onOccurrenceUpdated() {
				// do nothing
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.TIME_CREATED, toNoNullString(currentOccurrence
						.getTimeCreated())) {

			public void onOccurrenceUpdated() {
				// do nothing
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.LAST_UPDATED, toNoNullString(currentOccurrence
						.getLastUpdated())) {

			public void onOccurrenceUpdated() {
				// do nothing
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VALIDATED, toNoNullString(currentOccurrence
						.isValidated())) {

			public void onOccurrenceUpdated() {
				// do nothing
			}
		});

		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VETTING_ERROR, toNoNullString(currentOccurrence
						.getVettingError())) {

			public void onOccurrenceUpdated() {
				// do nothing
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VALIDATION_ERROR,
				toNoNullString(currentOccurrence.getValidationError())) {

			public void onOccurrenceUpdated() {
				// do nothing
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(FieldConstants.PUBLIC,
				toNoNullString(currentOccurrence.isPublic_())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setPublic_(Boolean.parseBoolean(value));
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VETTABLE, toNoNullString(currentOccurrence
						.isVettable())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVettable(Boolean.parseBoolean(value));
			}
		});

		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.REVIEWED, toReviewedString(currentOccurrence
						.getReviewed())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVetted(Boolean.parseBoolean(value));
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.TAPIR_ACCESSIBLE,
				toNoNullString(currentOccurrence.isTapirAccessible())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setTapirAccessible(Boolean
						.parseBoolean(value));
			}
		});

		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.BASIS_OF_RECORD,
				toNoNullString(currentOccurrence.getBasisOfRecord())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setBasisOfRecord(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.YEAR_COLLECTED, toNoNullString(currentOccurrence
						.getYearCollected())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setYearCollected(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(FieldConstants.GENUS,
				toNoNullString(currentOccurrence.getGenus())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGenus(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.SPECIFIC_EPITHET,
				toNoNullString(currentOccurrence.getSpecificEpithet())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setSpecificEpithet(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DECIMAL_LATITUDE,
				toNoNullString(currentOccurrence.getDecimalLatitude())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDecimalLatitude(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.DECIMAL_LONGITUDE,
				toNoNullString(currentOccurrence.getDecimalLongitude())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setDecimalLongitude(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.GEODETIC_DATUM, toNoNullString(currentOccurrence
						.getGeodeticDatum())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setGeodeticDatum(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.NOMENCLATURAL_CODE,
				toNoNullString(currentOccurrence.getNomenclaturalCode())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setNomenclaturalCode(value);
			}
		});
		requiredItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.COORDINATE_UNCERTAINTY_IN_METERS,
				toNoNullString(currentOccurrence
						.getCoordinateUncertaintyInMeters())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setCoordinateUncertaintyInMeters(value);
			}
		});

		if (firstLoad) {
			requiredItems.removeItem(requiredItems.getChild(0));
		}
	}

	private void loadReviewerTable() {
		reviewersReviewed.setLoaded(false);
		DataSwitch.get().getOccurrenceReviewsOf(currentOccurrence.getId(),
				new AsyncCallback<List<OccurrenceReview>>() {

					public void onFailure(Throwable caught) {
						Window.confirm(caught.getMessage());
						reviewersReviewed.setError(caught.getMessage());
					}

					public void onSuccess(List<OccurrenceReview> result) {
						reviewersReviewed.clear();
						for (OccurrenceReview occReview : result) {
							reviewersReviewed.addReviewer(occReview);
						}
						reviewersReviewed.setLoaded(true);
					}
				});

	}

	private void loadTaxoItems() {
		boolean firstLoad = taxoItems.getChildCount() == 1;
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.VERBATIM_SPECIES,
				toNoNullString(currentOccurrence.getVerbatimSpecies())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setVerbatimSpecies(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_KINGDOM,
				toNoNullString(currentOccurrence.getAcceptedKingdom())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedKingdom(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_PHYLUM,
				toNoNullString(currentOccurrence.getAcceptedPhylum())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedPhylum(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_CLASS, toNoNullString(currentOccurrence
						.getAcceptedClass())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedClass(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_ORDER, toNoNullString(currentOccurrence
						.getAcceptedOrder())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedOrder(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_SUBORDER,
				toNoNullString(currentOccurrence.getAcceptedSuborder())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedSuborder(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_FAMILY,
				toNoNullString(currentOccurrence.getAcceptedFamily())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedFamily(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_SUBFAMILY,
				toNoNullString(currentOccurrence.getAcceptedSubfamily())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedSubfamily(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_GENUS, toNoNullString(currentOccurrence
						.getAcceptedGenus())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedGenus(value);
			}
		});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_SUBGENUS,
				toNoNullString(currentOccurrence.getAcceptedSubgenus())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedSubgenus(value);
			}
		});
		taxoItems
				.addFieldEditorItem(new FieldEditor(
						FieldConstants.ACCEPTED_SPECIFIC_EPITHET,
						toNoNullString(currentOccurrence
								.getAcceptedSpecificEpithet())) {

					public void onOccurrenceUpdated() {
						String value = input.getText();
						currentOccurrence.setAcceptedSpecificEpithet(value);
					}
				});
		taxoItems.addFieldEditorItem(new FieldEditor(
				FieldConstants.ACCEPTED_SPECIES,
				toNoNullString(currentOccurrence.getAcceptedSpecies())) {

			public void onOccurrenceUpdated() {
				String value = input.getText();
				currentOccurrence.setAcceptedSpecies(value);
			}
		});
		if (firstLoad) {
			taxoItems.removeItem(taxoItems.getChild(0));
		}
	}

	/**
	 * Loads all user comment belong to current selected occurrence.
	 */
	private void loadUserComment() {
		OccurrenceCommentQuery query = new OccurrenceCommentQuery(0, 100);
		query.addFilter("oid = " + currentOccurrence.getId());
		DataSwitch.get().fetch(query,
				new AsyncCallback<OccurrenceCommentQuery>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getLocalizedMessage());
						GWT.log(caught.getLocalizedMessage(), caught);

					}

					public void onSuccess(OccurrenceCommentQuery result) {
						detailComment.commentTable.clear();
						if (result == null) {
							return;
						}
						for (OccurrenceComments comment : result.getResults()) {
							detailComment.addComment(comment);
						}
						loadReviewerTable();
					}

				});
	}

	private void maybeSwitchView(String historyToken) {
		// boolean isPrintView = Boolean
		// .parseBoolean(getIsPrintViewFromHistoryToken(historyToken));
		// if (isPrintView) {
		// if (mainSp.getWidget() != printView) {
		// loadPrintView();
		// mainSp.setWidget(printView);
		// }
		// } else {
		// if (mainSp.getWidget() != mainPanel) {
		// mainSp.setWidget(mainPanel);
		// }
		// }
	}

	private void onHistoryChanged(String historyToken) {
		historyState.setHistoryToken(historyToken);
		String id = historyState.getHistoryParameters(UrlParam.ID).toString();
		if (id == null
				|| (currentOccurrence != null && currentOccurrence.getId()
						.equals(id))) {
			// maybeSwitchView(historyToken);
			return;
		}

		updateDetailView(id);
	}

	/**
	 * Restores all fields to the their original values and reset marker
	 * position.
	 */
	private void restoreOriginal() {
		requiredItems.restoreOriginalValues();
		envItems.restoreOriginalValues();
		coreItems.restoreOriginalValues();
		curaItems.restoreOriginalValues();
		geoItems.restoreOriginalValues();
		taxoItems.restoreOriginalValues();
		try {
			newLat = Double.parseDouble(currentOccurrence.getDecimalLatitude());
			newLng = Double
					.parseDouble(currentOccurrence.getDecimalLongitude());
			updateMarkerPosition();
		} catch (NumberFormatException e) {
			smallMap.setCenter();
		}
	}

	/**
	 * Fires onSaved to save all occurrence changes then call
	 * {@link DataSwitch#update(String, java.util.Set, com.google.gwt.user.client.rpc.AsyncCallback)}
	 * 
	 * @param updatedView
	 *            TODO
	 * 
	 */
	private void saveChanges(final boolean updatedView) {
		boolean update = Window.confirm(constants.UpdateConfirm());
		if (!update) {
			return;
		}
		requiredItems.fireOnSaved();
		envItems.fireOnSaved();
		coreItems.fireOnSaved();
		curaItems.fireOnSaved();
		geoItems.fireOnSaved();
		taxoItems.fireOnSaved();
		String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
		Set<Occurrence> occurrences = new HashSet<Occurrence>();
		occurrences.add(currentOccurrence);
		setUpdateButtonsEnable(false);

		DataSwitch.get().update(sessionId, occurrences,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						GWT.log(caught.getMessage(), caught);
						setUpdateButtonsEnable(true);
					}

					/**
					 * If result != null, update is a successful. Clears the
					 * {@link DataSwitch} cache.
					 * 
					 * @param result
					 */
					public void onSuccess(String result) {
						if (result != null) {
							if (updatedView) {
								updateDetailView(currentOccurrence.getId() + "");
							}
							isDataUnsaved = false;
						} else {
							Window.confirm(constants.UnexpectedError());
						}
					}

				});
	}

	private void setCommentEnable(boolean enabled) {
		if (enabled) {
			if (commentMapVp.getWidgetIndex(addCommentLink) < 0) {
				commentMapVp.insert(addCommentLink, 1);
				commentMapVp.add(commentEditor);
			}
		} else {
			commentMapVp.remove(addCommentLink);
			commentMapVp.remove(commentEditor);
		}
	}

	/**
	 * Sets whether a user allow to edit fields in {@link DetailView}.
	 * 
	 * @param editable
	 */
	private void setFieldsEditable(boolean editable) {
		smallMap.setMarkerDragEnabled(editable);
		if (editable) {
			instruction.setHTML(constants.DetailInstruction() + "&nbsp;&nbsp;");
		} else {
			instruction.setHTML(constants.NotAllowToEdit() + "&nbsp;&nbsp;");
		}
		requiredItems.setFieldsEditable(editable);
		coreItems.setFieldsEditable(editable);
		envItems.setFieldsEditable(editable);
		curaItems.setFieldsEditable(editable);
		taxoItems.setFieldsEditable(editable);
		geoItems.setFieldsEditable(editable);
	}

	/**
	 * Enables or disables "Save Changes" buttons
	 * 
	 * @param enabled
	 *            true for enables
	 */
	private void setUpdateButtonsEnable(boolean enabled) {
		updateButtonsTop.save.setEnabled(enabled);
		updateButtonsBottom.save.setEnabled(enabled);
	}

	/**
	 * Converts the given object as no null String. if it is null or empty
	 * return "----".
	 * 
	 * @param obj
	 *            to be convert to String
	 * @return Object toString if it is not null or empty
	 */
	private String toNoNullString(Object obj) {
		String rtn = "----";
		boolean isString = (obj instanceof String);
		if (obj != null) {
			if (!isString || !obj.equals("")) {
				rtn = obj.toString();
			}
		}

		return rtn;
	}

	private String toReviewedString(Boolean reviewed) {
		if (reviewed == null) {
			return constants.Waiting();
		}
		if (reviewed) {
			return constants.Positive();
		}
		return constants.Negative();
	}

	/**
	 * Updates this occurrence view with a given occurrence id;
	 * 
	 * @param occurrenceId
	 *            occurrence id
	 */
	private void updateDetailView(String occurrenceId) {
		OccurrenceQuery query = new OccurrenceQuery(0, 1);
		query.addBaseFilter("id = " + occurrenceId);
		String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
		DataSwitch.get().fetch(sessionId, query,
				new AsyncCallback<OccurrenceQuery>() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						if (Window.confirm(constants.DetailNotViewable() + " "
								+ caught.toString())) {
							History.back();
						} else {
							History.back();
						}
					}

					public void onSuccess(OccurrenceQuery result) {
						try {
							onOccurrenceSelected(result.getResults().iterator()
									.next(), false);
						} catch (Exception e) {
							onFailure(e);
						}
					}
				});
	}

	/**
	 * Update marker position on the map with the given latitude and longitude.
	 * 
	 * @param latLng
	 */
	private void updateMarkerPosition() {
		if (!isInBound()) {
			smallMap.setCenter();
		} else {
			LatLng point = LatLng.newInstance(newLat, newLng);
			smallMap.setMarker(point);
		}
	}

	@Override
	public void onPageSizeChange(int newPageSize) {
		ApplicationView.getApplication().getOccurrenceView()
				.setPageSize(newPageSize);
		// on recharge les donn�es
		requestData(1);
	}

	@Override
	public DataPager<Occurrence> getDataPagerWidget() {
		return pagerWidget.pager;
	}

	@Override
	public OccurrencePagerWidget getOccurrencePagerWidget() {
		return pagerWidget;
	}
}
