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
import java.util.TreeMap;

import org.rebioma.client.AdvanceSearchView.ASearchType;
import org.rebioma.client.AdvanceSearchView.ValueType;
import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.DetailView.FieldConstants;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.UploadView.UploadListener;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.RevalidationResult;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.RevalidationService;
import org.rebioma.client.services.ServerPingService;
import org.rebioma.client.services.ServerPingServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view that supports searching occurrences and paging through results which
 * can be displayed as a map view, a list view, or a detail view.
 */
public class OccurrenceView extends ComponentView implements
		PageListener<Occurrence>, ClickHandler, OccurrenceSearchListener {

	/**
	 * Temporally solution for earth map type bug switching view bug. This
	 * extends PopupPanel to override {@link PopupPanel#hide(boolean)} to just
	 * set the {@link PopupPanel} invisible so that the widget is still attached
	 * to the DOM element but does not show it.
	 * 
	 * @author Tri
	 * 
	 */
	private class CustomPopupPanel extends PopupPanel {
		public CustomPopupPanel(View w) {
			super(false);
			setWidget(w);
			//int wi = Window.getClientWidth()-40;
			//setWidth(wi+"px");
			setStyleName(ComponentView.STYLE_NAME);
		}

		@Override
		public void hide(boolean autoClose) {
			setVisible(false);
			Widget view = getWidget();
			if (view instanceof MapView) {
				((MapView) view).setVisible(false);
			}

		}

		public void reshow() {
			show();
		}

		@Override
		public void show() {
			setPopupPosition(toolHp.getAbsoluteLeft()-1, toolHp.getAbsoluteTop()
					+ toolHp.getOffsetHeight());
			super.show();
			setVisible(true);
			View view = (View) getWidget();
			view.onShow();
			if (view instanceof MapView) {
				((MapView) view).setVisible(true);
			}
		}
	}

	private class QueryFiltersMap {
		private final Map<String, Set<String>> typeFiltersMap = new TreeMap<String, Set<String>>();
		private final Map<String, String> historyTypeValue = new HashMap<String, String>();

		public QueryFiltersMap() {
			historyTypeValue.put(constants.AllPositivelyReviewed(),
					ALL_POS_REVIEWED);
			historyTypeValue.put(constants.AllNegativelyReviewed(),
					ALL_NEG_REVIEWED);
			historyTypeValue.put(constants.AllAwaitingReview(),
					ALL_AWAIT_REVIEW);
			historyTypeValue.put(constants.AllInvalid(), ALL_INVALID);
			historyTypeValue.put(constants.AllOccurrences(), ALL_OCC);

			historyTypeValue.put(constants.MyPositivelyReviewed(),
					MY_POS_REVIEWED);
			historyTypeValue.put(constants.MyNegativelyReviewed(),
					MY_NEG_REVIEWED);
			historyTypeValue.put(constants.MyValidated(), MY_AWAITING_REVIEW);
			historyTypeValue.put(constants.MyInvalidated(), MY_INVALID);
			historyTypeValue.put(constants.MyOccurrences(), MY_OCCURRENCES);
			historyTypeValue.put(constants.OccurrencesToReview(),
					OCCURRENCES_TO_REVIEW);
			historyTypeValue.put(constants.MyOverallPositivelyReviewed(),
					MY_OVERALL_POS_REVIEW);
			historyTypeValue.put(constants.MyOverallNegativelyReviewed(),
					MY_OVERALL_NEG_REVIEW);
		}

		void addFilters(String type, OccurrenceQuery query, User user) {
			String typeTokenValue = historyTypeValue.get(type);
			typeFiltersMap.put(typeTokenValue, query.getFiltersFromProperty(
					type, user, ResultFilter.PUBLIC));
		}

		String getType(Set<String> filters) {
			for (String type : typeFiltersMap.keySet()) {
				Set<String> f = typeFiltersMap.get(type);
				if (filters.equals(f)) {
					return type;
				}
			}
			for (String type : typeFiltersMap.keySet()) {
				Set<String> f = typeFiltersMap.get(type);
				if (filters.containsAll(f)) {
					return type;
				}
			}
			return null;
		}

		void init(User loggedinUser) {
			typeFiltersMap.clear();
			for (String type : historyTypeValue.keySet()) {
				String value = historyTypeValue.get(type);
				if (loggedinUser == null && value.startsWith("my")) {
					continue;
				}
				addFilters(type, query, loggedinUser);
			}
		}
	}

	/**
	 * The SearchForm inner class defines a widget used for searching
	 * occurrences by type and species name.
	 * 
	 * The form submits queries by reseting the pager and query filters and then
	 * adding a new history token via getHistoryToken() to the {@link History}
	 * stack. The history token contains the form's search parameters which get
	 * processed by onHistoryChanged().
	 */
	protected class SearchForm extends Composite implements
			ViewStateChangeListener, ChangeHandler {
		/**
		 * The index for the default selected occurrence type in the list box.
		 */
		static final int DEFAULT_SELECTION_INDEX = 0;

		// TODO: The use of static index constants here is really bad design.
		// static final int INVALIDATED_INDEX = 3;
		// static final int MY_INVALIDATED_INDEX = 8;
		protected static final int ALL_TYPES_END_INDEX = 4;

		protected static final String QUICK_SEARCH = "QuickSearch";
		protected static final String VALIDATION_ERROR = "ValidationError";
		protected static final String ALL_SHARED_UNSHARED = "all";
		protected static final String SHARED_WITH_ME = "swm";
		protected static final String SHARED_BY_ME = "sbm";
		protected static final String UNSHARED_BY_ME = "uswm";
		protected static final String ALL_ERROR = "all";
		protected static final String YEAR_ERROR = "YearCollected";
		protected static final String GENUS_ERROR = "GENUS";
		protected static final String SPECIFIC_EPTHET_ERROR = "SpecificEpithet";
		protected static final String DECIMAL_LAT_ERROR = "DecimalLatitude";
		protected static final String DECIMAL_LNG_ERROR = "DecimalLongitude";
		protected static final String TAXO_ERROR = "Taxonomic classification";

		/**
		 * The list of occurrence types.
		 */
		final ListBox searchTypeBox = new ListBox();
		/**
		 * The main widget wrapped by this composite.
		 */
		final FlowPanel mainHp;
		/**
		 * The text box for entering the species name.
		 */
		final TextBox searchBox;
		/**
		 * A mapping of occurrence types to their list box index.
		 */
		final Map<String, Integer> typeIndexMap = new HashMap<String, Integer>();
		private final Button searchButton;
		private final HTML advanceLink = new HTML(constants.AdvanceSearch());
		private final ListBox resultFilterLb = new ListBox();
		private final ListBox invalidatedLb = new ListBox();
		private final Label forLabel = new Label(" " + constants.For() + " ");
		private final ListBox sharedListBox = new ListBox();

		// private final CheckBox clearAdvanceCb = new
		// CheckBox("No Advance Search");

		/**
		 * Constructs a new SearchForm.
		 */
		private SearchForm() {
			final Label searchLabel = new Label(" " + constants.Search() + " ");
			searchLabel.setStyleName("searchLabel");
			advanceLink.setStyleName("link");
			advanceLink.addStyleName("AdvanceLink");
			resultFilterLb.addItem(constants.Both(), "both");
			resultFilterLb.addItem(constants.Public(), "public");
			resultFilterLb.addItem(constants.Private(), "private");
			resultFilterLb.setStyleName("ResultFilter");
			invalidatedLb.addItem(constants.AllValidationError(), ALL_ERROR);
			typeIndexMap.put(ALL_ERROR.toLowerCase(), 0);
			invalidatedLb.addItem(constants.InvalidYearCollected(), YEAR_ERROR);
			typeIndexMap.put(YEAR_ERROR.toLowerCase(), 1);
			invalidatedLb.addItem(constants.InvalidGenus(), GENUS_ERROR);
			typeIndexMap.put(GENUS_ERROR.toLowerCase(), 2);
			invalidatedLb.addItem(constants.InvalidSpecificEpthet(),

			SPECIFIC_EPTHET_ERROR);
			typeIndexMap.put(SPECIFIC_EPTHET_ERROR.toLowerCase(), 3);

			invalidatedLb.addItem(constants.InvalidDecimalLatitude(),

			DECIMAL_LAT_ERROR);
			typeIndexMap.put(DECIMAL_LAT_ERROR.toLowerCase(), 4);

			invalidatedLb.addItem(constants.InvalidDecimalLongitude(),

			DECIMAL_LNG_ERROR);
			typeIndexMap.put(DECIMAL_LNG_ERROR.toLowerCase(), 5);

			invalidatedLb.addItem(constants.InvalidTaxonomicClassification(),

			TAXO_ERROR);
			typeIndexMap.put(TAXO_ERROR.toLowerCase(), 6);

			invalidatedLb.setStyleName("ResultFilter");
			searchTypeBox.setStyleName("TypeBox");
			sharedListBox.setStyleName("SharedListBox");
			forLabel.setStylePrimaryName("forLabel");
			forLabel.setWordWrap(false);

			// Clicking the button resets the pager, clears the query filters,
			// then
			// adds a new history item which fires
			// OccurrenceView.onHistoryChanged(),
			// which decodes the new history token and updates the
			// DataPagerWidget.
			searchButton = new Button(constants.Search());
			searchButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					search();
				}
			});

			// The search box clicks the search button if the enter key is
			// pressed:
			searchBox = new TextBox();
			searchBox.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						searchButton.click();
					}

				}
			});
			searchTypeBox.addChangeHandler(this);

			mainHp = new FlowPanel();
			mainHp.add(searchLabel);
			mainHp.add(searchTypeBox);
			mainHp.add(forLabel);
			mainHp.add(searchBox);
			mainHp.add(searchButton);
			mainHp.add(advanceLink);
			initWidget(mainHp);
			mainHp.setStyleName("Search-Form");
			advanceLink.addClickHandler(OccurrenceView.this);
			onStateChanged(ApplicationView.getCurrentState());
			resultFilterLb.addChangeHandler(this);
		}

		public String getCurrentStateToken() {
			StringBuilder sb = new StringBuilder();
			String searchQuery = constructHistoryUrl(UrlParam.QUERY);
			if (!searchQuery.equals("")) {
				sb.append(searchQuery + "&");
			}
			String myRecordsValue = constructHistoryUrl(UrlParam.RF);
			if (!myRecordsValue.equals("")) {
				sb.append(myRecordsValue + "&");
			}
			String advanceSearch = constructHistoryUrl(UrlParam.ASEARCH);
			if (!advanceSearch.equals("")) {
				sb.append(advanceSearch + "&");
			}
			if (searchForm.invalidatedLb.isAttached()) {
				sb.append(constructHistoryUrl(UrlParam.ERROR_TYPE) + "&");
			}
			if (searchForm.sharedListBox.isAttached()) {
				sb.append(constructHistoryUrl(UrlParam.ST) + "&");
			}
			sb.append(constructHistoryUrl(UrlParam.TYPE));
			return sb.toString();
		}

		public ResultFilter getResultFilter() {
			ResultFilter resultFilter;
			String filterValue = resultFilterLb.getValue(resultFilterLb
					.getSelectedIndex());
			if (filterValue.equals("private")) {
				resultFilter = ResultFilter.PRIVATE;
			} else if (filterValue.equals("public")) {
				resultFilter = ResultFilter.PUBLIC;
			} else if (filterValue.equals("both")) {
				resultFilter = ResultFilter.BOTH;
			} else {
				resultFilter = ResultFilter.PUBLIC;
			}
			return resultFilter;
		}

		/**
		 * Extracts the {@link UrlParam} QUERY parameter from a history token
		 * and returns it as a String. If the QUERY parameter does not exist in
		 * the history token, and empty string is returned.
		 * 
		 * @param historyToken
		 *            the history token
		 * @return the search query
		 */
		public String getSearchQuery(String historyToken) {
			historyState.setHistoryToken(historyToken);
			String searchTerm = historyState
					.getHistoryParameters(UrlParam.QUERY) + "";
			return searchTerm;
		}

		/**
		 * @return the selected occurrence type in the list box
		 */
		public String getSearchType() {
			int index = searchTypeBox.getSelectedIndex();
			return searchTypeBox.getItemText(index);
		}

		// /**
		// * Checks whether the search only return private records of currently
		// logged
		// * in user.
		// *
		// * @return true if this search is only for private records of
		// currently
		// * logged in user
		// */
		// public boolean isMyRecords() {
		// return privateCb.isAttached() && privateCb.isChecked();
		// }

		public boolean isResultFilterVisible() {
			return resultFilterLb.isAttached();
		}

		public void onChange(ChangeEvent event) {
			Object source = event.getSource();
			if (source == searchTypeBox) {
				int selectedIndex = searchTypeBox.getSelectedIndex();
				setMyRecordsEnable(selectedIndex > ALL_TYPES_END_INDEX);
				String type = searchTypeBox.getItemText(selectedIndex);
				setMyRecordsInvalid(type.equalsIgnoreCase(constants
						.AllInvalid())
						|| type.equalsIgnoreCase(constants.MyInvalid()));
				// setMyRecordsInvalid(selectedIndex == INVALIDATED_INDEX
				// || selectedIndex == MY_INVALIDATED_INDEX);
				setSharedType(getSearchType());
			} else if (source == resultFilterLb) {
				int selectedIndex = searchTypeBox.getSelectedIndex();
				setSharedType(getSearchType());
			}
			resize(Window.getClientWidth(), Window.getClientHeight());
		}

		/**
		 * Handles changes in application state.
		 * 
		 * If state is AUTHENTICATED, the list box displays the 'My' occurrence
		 * types used for searching occurrences that belong to the authenticated
		 * user along with the 'All' occurrence types used for searching public
		 * occurrences. Otherwise only the 'All' occurrence types are displayed.
		 * 
		 * Also updates the the occurrence list box selection and search box
		 * text from the current history token.
		 */
		public void onStateChanged(ViewState state) {
			searchTypeBox.clear();
			typeIndexMap.clear();
			searchBox.setText("");

			// All Positively Reviewed
			// All Negatively Reviewed
			// All Awaiting Review
			// All Invalidated
			// All Occurrences

			// All Positively Reviewed
			searchTypeBox.addItem(constants.AllPositivelyReviewed(),
					ALL_POS_REVIEWED);
			typeIndexMap
					.put(ALL_POS_REVIEWED, searchTypeBox.getItemCount() - 1);

			// All Negatively Reviewed
			searchTypeBox.addItem(constants.AllNegativelyReviewed(),
					ALL_NEG_REVIEWED);
			typeIndexMap
					.put(ALL_NEG_REVIEWED, searchTypeBox.getItemCount() - 1);

			// All Awaiting Review
			searchTypeBox.addItem(constants.AllAwaitingReview(),
					ALL_AWAIT_REVIEW);
			typeIndexMap
					.put(ALL_AWAIT_REVIEW, searchTypeBox.getItemCount() - 1);

			// All Invalidated
			searchTypeBox.addItem(constants.AllInvalid(), ALL_INVALID);
			typeIndexMap.put(ALL_INVALID, searchTypeBox.getItemCount() - 1);

			// All Occurrences
			searchTypeBox.addItem(constants.AllOccurrences(), ALL_OCC);
			typeIndexMap.put(ALL_OCC, searchTypeBox.getItemCount() - 1);

			switch (state) {
			case UNAUTHENTICATED:
				mainHp.remove(sharedListBox);
				break;

			// My Positively Reviewed
			// My Negatively Reviewed
			// My Awaiting Review
			// My Invalidated
			// My Occurrences
			// Occurrences to Review
			case ADMIN:
			case REVIEWER:
				// // Occurrences to Review
				searchTypeBox.addItem(constants.OccurrencesToReview(),
						OCCURRENCES_TO_REVIEW);
				typeIndexMap.put(OCCURRENCES_TO_REVIEW,
						searchTypeBox.getItemCount() - 1);
				// My Positively Reviewed
				searchTypeBox.addItem(constants.MyPositivelyReviewed(),
						MY_POS_REVIEWED);
				typeIndexMap.put(MY_POS_REVIEWED,
						searchTypeBox.getItemCount() - 1);

				// My Negatively Reviewed
				searchTypeBox.addItem(constants.MyNegativelyReviewed(),
						MY_NEG_REVIEWED);
				typeIndexMap.put(MY_NEG_REVIEWED,
						searchTypeBox.getItemCount() - 1);
			case RESEARCHER:
				// My overall Positively Reviewed
				searchTypeBox.addItem(constants.MyOverallPositivelyReviewed(),
						MY_OVERALL_POS_REVIEW);
				typeIndexMap.put(MY_OVERALL_POS_REVIEW,
						searchTypeBox.getItemCount() - 1);

				// My overall Negatively Reviewed
				searchTypeBox.addItem(constants.MyOverallNegativelyReviewed(),
						MY_OVERALL_NEG_REVIEW);
				typeIndexMap.put(MY_OVERALL_NEG_REVIEW,
						searchTypeBox.getItemCount() - 1);
				// My Awaiting Review
				searchTypeBox.addItem(constants.MyAwaitingReview(),
						MY_AWAITING_REVIEW);
				typeIndexMap.put(MY_AWAITING_REVIEW,
						searchTypeBox.getItemCount() - 1);
				// My Invalidated
				searchTypeBox.addItem(constants.MyInvalid(), MY_INVALID);
				typeIndexMap.put(MY_INVALID, searchTypeBox.getItemCount() - 1);

				// My Occurrences
				searchTypeBox
						.addItem(constants.MyOccurrences(), MY_OCCURRENCES);
				typeIndexMap.put(MY_OCCURRENCES,
						searchTypeBox.getItemCount() - 1);

				mainHp.insert(sharedListBox, 2);

				break;
			}

			restoreStatesFromHistory(History.getToken());

		}

		public void restoreStatesFromHistory(String historyToken) {
			historyState.setHistoryToken(historyToken);
			String currentView = activeViewInfo == null ? DEFAULT_VIEW
					: activeViewInfo.getName();
			if (currentView.equals(UPLOAD) || currentView.equals(ADVANCE)) {
				return;
			}
			String searchType = getListBoxText(searchTypeBox, UrlParam.TYPE,
					DEFAULT_SELECTION_INDEX);

			int searchTypeIndex = getTypeIndex(searchType,
					DEFAULT_SELECTION_INDEX);
			setMyRecordsEnable(searchTypeIndex > ALL_TYPES_END_INDEX);
			ResultFilter resultFilter = (ResultFilter) historyState
					.getHistoryParameters(UrlParam.RF);

			switch (resultFilter) {
			case PUBLIC:
				resultFilterLb.setSelectedIndex(1);
				break;
			case PRIVATE:
				resultFilterLb.setSelectedIndex(2);
				break;
			case BOTH:
				resultFilterLb.setSelectedIndex(0);
			}

			searchTypeBox.setSelectedIndex(searchTypeIndex);
			String type = searchTypeBox.getItemText(searchTypeIndex);
			boolean isInvalid = type.equalsIgnoreCase(constants.AllInvalid())
					|| type.equalsIgnoreCase(constants.MyInvalidated());
			setMyRecordsInvalid(isInvalid);
			// restoring invalidated box;
			if (isInvalid) {
				String errorType = getListBoxText(invalidatedLb,
						UrlParam.ERROR_TYPE, 0);
				invalidatedLb.setSelectedIndex(getTypeIndex(errorType, 0));

			}
			String searchText = getSearchQuery(historyToken);
			searchBox.setText(searchText);
			int pageNum = getPageNum(historyToken);
			query.setBaseFilters(query.getFiltersFromProperty(
					searchTypeBox.getItemText(searchTypeIndex),
					ApplicationView.getAuthenticatedUser(), resultFilter));
			Set<String> searchFilters = query.getSearchFilters();
			searchFilters.clear();
			if (!searchText.equals("")) {
				searchFilters.add(QUICK_SEARCH + " = " + searchText);
			}
			setSharedType(getSearchType());
			if (sharedListBox.isAttached()) {
				String sharedType = getListBoxText(sharedListBox, UrlParam.ST,
						0);
				sharedListBox.setSelectedIndex(getTypeIndex(sharedType, 0));
			}
			List<String> searchFieldValues = (List<String>) historyState
					.getHistoryParameters(UrlParam.ASEARCH);
			if (searchFieldValues != null) {
				for (String searchFieldValue : searchFieldValues) {
					searchFilters.add(searchFieldValue);
				}
			}
			// if (searchFieldValues != null) {
			// for (String asearchToken : searchFieldValues) {
			// Map<String, List<String>> asearchValues = historyState
			// .getAsearchFieldValues(asearchToken);
			// List<String> asearchIndexes = asearchValues
			// .get(HistoryState.ASEARCH_INDEX);
			// for (String asearchIndex : asearchIndexes) {
			// List<String> asearchTypes = asearchValues.get(asearchIndex);
			// for (String asearchType : asearchTypes) {
			// List<String> operators = asearchValues.get(asearchValues
			// + HistoryState.AOPERATOR);
			// for (String operator : operators) {
			// List<String> values = asearchValues.get(asearchType + operator
			// + HistoryState.ASEARCH_VALUE);
			// for (String value : values) {
			// searchFilters.add(asearchType + " " + operator + " " + value);
			// }
			// }
			// }
			// }
			// }
			// }
			addErrorQuery();
			addSharedSearchToQuery();
			query.requestData(pageNum);
			currentPageNum = pageNum;
		}

		public void search() {
			String searchText = searchBox.getText();
			String searchType = getSearchType();
			String activeView = activeViewInfo.getName();
			ResultFilter resultFilter = getResultFilter();

			query.setBaseFilters(query.getFiltersFromProperty(searchType,
					ApplicationView.getAuthenticatedUser(), resultFilter));
			if (!activeView.equalsIgnoreCase(ADVANCE)) {
				clearAdanceSearch();
			}
			ViewInfo advanceViewInfo = viewInfos.get(ADVANCE.toLowerCase());
			if (advanceViewInfo.isViewConstrcuted()) {
				AdvanceSearchView searchView = (AdvanceSearchView) advanceViewInfo
						.getView();
				query.setSearchFilters(searchView.getSearchFilters());
			}
			if (!activeView.equals(MAP) && !activeView.equals(LIST)) {
				switchView(DEFAULT_VIEW, false);
			}
			if (!searchText.equals("")) {
				query.addSearchFilter(QUICK_SEARCH + " = " + searchText);
			}
			
			addErrorQuery();
			addSharedSearchToQuery();
			GWT.log(query.getBaseFilters() + "");
			// for (String searchFilter : query.getSearchFilters()) {
			// System.err.println(searchFilter);
			// }
			resetToDefaultState();
			addHistoryItem(false);
			query.requestData(1);
		}

		public void setEnabled(boolean enabled) {
			searchButton.setEnabled(enabled);
			resultFilterLb.setSelectedIndex(1);
		}

		String getInvalidatedType() {
			return invalidatedLb.getValue(invalidatedLb.getSelectedIndex());
		}

		/**
		 * @return the search box text.
		 */
		String getSearchQuery() {
			return searchBox.getText();
		}

		String getSharedType() {
			int selectedIndex = sharedListBox.getSelectedIndex();
			if (selectedIndex < 0) {
				return "";
			}
			return sharedListBox.getValue(selectedIndex);
		}

		int getTypeIndex(String text, int defaultIndex) {
			int index = defaultIndex;
			if (typeIndexMap.containsKey(text)) {
				index = typeIndexMap.get(text.toLowerCase());
			}
			return index;
		}

		boolean  isAllSelected(String searchType) {
			return searchType.equalsIgnoreCase(constants.AllInvalid())
					|| searchType.equalsIgnoreCase(constants.AllOccurrences())
					|| searchType.equalsIgnoreCase(constants
							.AllPositivelyReviewed())
					|| searchType.equalsIgnoreCase(constants
							.AllNegativelyReviewed())
					|| searchType.equalsIgnoreCase(constants
							.AllAwaitingReview());
		}

		/**
		 * Adds {@link #privateCb} if enabled is true and remove it if enabled
		 * is false.
		 * 
		 * @param enabled
		 *            true to show my records only check box and false to
		 *            hide/remove my records only check box
		 */
		void setMyRecordsEnable(boolean enabled) {
			if (enabled) {
				mainHp.insert(resultFilterLb, 2);
			} else {
				mainHp.remove(resultFilterLb);
				// publicRb.setValue(true);
			}
			if (activeViewInfo != null) {
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						resize(Window.getClientWidth(),
								Window.getClientHeight());
					}
				});
			}
		}

		void setMyRecordsInvalid(boolean invalidated) {
			if (invalidated) {
				int beforeIndex = mainHp.getWidgetIndex(resultFilterLb) == -1 ? 2
						: 3;
				mainHp.insert(invalidatedLb, beforeIndex);
			} else {
				mainHp.remove(invalidatedLb);
			}
			// invalidatedLb.setSelectedIndex(0);
		}

		/**
		 * Sets the search box text.
		 * 
		 * @param text
		 *            the search text
		 */
		void setQueryText(String text) {
			searchBox.setText(text);
		}

		void setSharedType(String searchType) {
			ViewState viewState = ApplicationView.getCurrentState();
			sharedListBox.clear();
			switch (viewState) {
			case UNAUTHENTICATED:
				mainHp.remove(sharedListBox);
				break;
			case ADMIN:
			case REVIEWER:
			case RESEARCHER:
				if (isAllSelected(searchType)) {
					if (!sharedListBox.isAttached()) {
						mainHp.insert(sharedListBox, 2);
					}
					sharedListBox.addItem(constants.AllSharedUnshared(),
							ALL_SHARED_UNSHARED);
					typeIndexMap.put(ALL_SHARED_UNSHARED.toLowerCase(), 0);
					sharedListBox.addItem(constants.SharedWithMe(),
							SHARED_WITH_ME);
					typeIndexMap.put(SHARED_WITH_ME.toLowerCase(), 1);
				} else {
					String resultFilter = resultFilterLb
							.getValue(resultFilterLb.getSelectedIndex());
					if (resultFilter.equalsIgnoreCase("public")) {
						mainHp.remove(sharedListBox);
					} else {
						if (!sharedListBox.isAttached()) {
							mainHp.insert(sharedListBox, 2);
						}
						sharedListBox.addItem(constants.AllSharedUnshared(),
								ALL_SHARED_UNSHARED);
						typeIndexMap.put(ALL_SHARED_UNSHARED.toLowerCase(), 0);
						sharedListBox.addItem(constants.SharedByMe(),
								SHARED_BY_ME);
						typeIndexMap.put(SHARED_BY_ME.toLowerCase(), 1);
						sharedListBox.addItem(constants.UnsharedByMe(),
								UNSHARED_BY_ME);
						typeIndexMap.put(UNSHARED_BY_ME.toLowerCase(), 2);
					}

				}
				break;
			}
		}

		protected void clearAdanceSearch() {
			ViewInfo advanceViewInfo = viewInfos.get(ADVANCE.toLowerCase());
			if (advanceViewInfo.isViewConstrcuted()) {
				AdvanceSearchView searchView = (AdvanceSearchView) advanceViewInfo
						.getView();
				searchView.clearSearch();
			} else {
				query.setSearchFilters(new HashSet<String>());
				// addHistoryItem(false);
			}

		}

		protected void updateForm(String historyToken) {
			// String searchTerm = searchForm.getSearchQuery(historyToken);
			// String searchType = searchForm.getSearchType(historyToken);
			// searchBox.setText(searchTerm);
			// Integer index = typeIndexMap.get(searchType);
			// if (index == null) {
			// index = DEFAULT_SELECTION_INDEX;
			// }
			// listBox.setSelectedIndex(index);
		}

		private void addErrorQuery() {
			int searchIndex = searchTypeBox.getSelectedIndex();
			String type = searchTypeBox.getItemText(searchIndex);
			boolean isInvalid = type.equalsIgnoreCase(constants.AllInvalid())
					|| type.equalsIgnoreCase(constants.MyInvalid());
			if (isInvalid) {
				int sIndex = invalidatedLb.getSelectedIndex();
				if (sIndex > 0) {
					query.addSearchFilter(VALIDATION_ERROR + " like "
							+ invalidatedLb.getValue(sIndex));
				}
			}
		}

		private void addSharedSearchToQuery() {
			if (sharedListBox.isAttached()) {
				String sharedType = getSharedType();
				User user = ApplicationView.getAuthenticatedUser();
				if (user == null) {
					return;
				}
				if (sharedType.equalsIgnoreCase(SHARED_WITH_ME)) {
					query.addSearchFilter("sharedUsersCSV like "
							+ user.getEmail());
				} else if (sharedType.equalsIgnoreCase(SHARED_BY_ME)) {
					query.addSearchFilter("sharedUsersCSV !empty ");
				} else if (sharedType.equalsIgnoreCase(UNSHARED_BY_ME)) {
					query.addSearchFilter("sharedUsersCSV empty ");
				}
			}
		}

		private String getListBoxText(ListBox listBox, UrlParam urlParam,
				int defaultSelectedIndex) {
			String type = historyState.getHistoryParameters(urlParam) + "";
			if (type.equals("") && listBox.getItemCount() > 0) {
				type = listBox.getValue(defaultSelectedIndex);
			}
			return type;
		}

		/**
		 * Extracts the {@link UrlParam} TYPE parameter from a history token and
		 * returns it as a String. If the OCCURRENCE_TYPE parameter does not
		 * exist in the history token, the default selection is returned.
		 * 
		 * @param historyToken
		 *            the history token
		 * @return the occurrence type
		 */
		private String getSearchType(String historyToken) {
			historyState.setHistoryToken(historyToken);
			String type = historyState.getHistoryParameters(UrlParam.TYPE) + "";
			if (type.equals("")) {
				type = searchTypeBox.getValue(DEFAULT_SELECTION_INDEX);
			}
			return type;
		}

		private void setValidationError(String validationError) {
			int selectedIndex = 0;
			for (int i = 0; i < invalidatedLb.getItemCount(); i++) {
				String value = invalidatedLb.getValue(i);
				if (value.equalsIgnoreCase(validationError)) {
					selectedIndex = i;
					break;
				}
			}
			invalidatedLb.setSelectedIndex(selectedIndex);
		}
	}

	/**
	 * Constants history value for "All Occurrence" search selection
	 */
	public static final String ALL_OCC = "all occurrences";
	/**
	 * Constants history value for "All Vetted" search selection
	 */
	public static final String ALL_POS_REVIEWED = "all pos reviewed";
	/**
	 * Constants history value for "All Invalid" search selection
	 */
	public static final String ALL_INVALID = "all invalid";

	public static final String ALL_AWAIT_REVIEW = "all await review";

	/**
	 * Constants history value for "All Validated" search selection
	 */
	public static final String ALL_NEG_REVIEWED = "all neg reviewed";
	/**
	 * Constants history value for "All Validated Not Vetted" search selection
	 */
	public static final String ALL_VALIDATED_NOT_VETTED = "all validated not certify";

	/**
	 * Constants history value for "My Occurrence" search selection
	 */
	public static final String MY_POS_REVIEWED = "my pos reviewed";
	/**
	 * Constants history value for "My Vetted" search selection
	 */
	public static final String MY_NEG_REVIEWED = "my neg reviewed";
	/**
	 * Constants history value for "My Invalid" search selection
	 */
	public static final String MY_INVALID = "my invalid";
	/**
	 * Constants history value for "My Validated" search selection
	 */
	public static final String MY_AWAITING_REVIEW = "my awaiting review";

	public static final String MY_OVERALL_POS_REVIEW = "my overall pos review";
	public static final String MY_OVERALL_NEG_REVIEW = "my overall neg review";

	public static final String MY_OCCURRENCES = "my occurrences";
	/**
	 * Constants history value for "All Validated Not Vetted" search selection
	 */
	public static final String OCCURRENCES_TO_REVIEW = "occurrences to review";

	public static final String DEFAULT_VIEW = MAP;
	
	public static final int REVALIDATION_SERVER_PING_INTERVAL = 5 * 60 * 1000;//5mn

	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new OccurrenceView(parent);
			}

			@Override
			protected String getHisTokenName() {
				return historyName;
			}

			@Override
			protected String getName() {
				return name;
			}

		};
	}

	private final HTML mapLink = new HTML(constants.Map());
	private final HTML listLink = new HTML(constants.List());
	private final HTML uploadLink = new HTML(constants.Upload());
	private final HTML revalidateLink = new HTML(constants.Revalidate());
	private static final String FIELDS_LIST = "<a target='_blank' href='http://www.rebioma.net/index.php?option=com_content&view=article&id=52'>"
			+ "Terms List" + "</a>";
	private final HTML helpLink = new HTML(FIELDS_LIST);
	private final HorizontalPanel switchViewPanel = new HorizontalPanel();

	private final HorizontalPanel toolHp = new HorizontalPanel();

	private final Map<String, ViewInfo> viewInfos = new HashMap<String, ViewInfo>();

	private final OccurrenceQuery query;
	private int queryPageSize;

	private String lastView;
	private final SimplePanel mainSp = new SimplePanel();
	private ViewInfo activeViewInfo;
	private int currentPageNum = 1;
	private final QueryFiltersMap queryFiltersMap;
	private final SearchForm searchForm;
	private final VerticalPanel mainVp;
	private final Map<String, CustomPopupPanel> viewsMap = new HashMap<String, CustomPopupPanel>();
	private final HistoryState historyState = new HistoryState() {

		@Override
		public Object getHistoryParameters(UrlParam param) {
			switch (param) {
			case VIEW:
			case QUERY:
			case TYPE:
			case ST:
			case ERROR_TYPE:
				return stringValue(param);
			case PAGE:
				return integerValue(param);
			case RF:
				ResultFilter resultFilter = ResultFilter.PUBLIC;
				String resultFilterToken = stringValue(param);
				if (resultFilterToken.equalsIgnoreCase("both")) {
					resultFilter = ResultFilter.BOTH;
				} else if (resultFilterToken.equalsIgnoreCase("private")) {
					resultFilter = ResultFilter.PRIVATE;
				}
				return resultFilter;
			case ASEARCH:
				return listValue(param);
			}
			return "";
		}

	};

	private OccurrenceView() {
		this(null);
	}

	/**
	 * Note: This constructor sets the query's base filters.
	 * 
	 * @param parent
	 */
	private OccurrenceView(View parent) {
		super(parent, true);

		// Sets the default query to all positively reviewed.
		query = new OccurrenceQuery();
		query.setBaseFilters(query.getFiltersFromProperty(
				constants.AllPositivelyReviewed(), null));

		queryFiltersMap = new QueryFiltersMap();
		searchForm = new SearchForm();
		queryFiltersMap.init(ApplicationView.getAuthenticatedUser());
		mainVp = new VerticalPanel();
		initViews();
		// activeViewInfo = viewInfos.get(MAP);
		// View mapView = activeViewInfo.getView();

		// mapView.onValueChange(History.getToken());
		// toolHp.add(uploadLink);
		toolHp.add(searchForm);
		//toolHp.setWidth("100%");
		toolHp.add(switchViewPanel);
		toolHp.setCellVerticalAlignment(switchViewPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		toolHp.setCellVerticalAlignment(searchForm,
				HasVerticalAlignment.ALIGN_MIDDLE);

		// SimplePanel toolSp = new SimplePanel();
		// toolSp.setWidget(toolHp);
		toolHp.setStyleName("OccurrenceView-ToolBar");
		mainVp.add(toolHp);
		mapLink.setStyleName("maplink");
		listLink.setStyleName("listlink");
		uploadLink.setStyleName("uploadlink");
		revalidateLink.setStyleName("revalidatelink");

		// mainVp.add(mapView);
		switchViewPanel.setSpacing(5);
		mainSp.setWidget(mainVp);
		initWidget(mainSp);
		mainSp.addStyleName("OccurrenceView");
		// mainVp.setPixelSize(Window.getClientWidth(),
		// Window.getClientHeight());
		mainVp.setCellHeight(toolHp, "20px");
		addHistoryItem(false);
		Window.addResizeHandler(this);
		History.addValueChangeHandler(this);
		// History.fireCurrentHistoryState();
		String historyToken = History.getToken();
		if (historyToken.trim().equals("")) {
			switchView(DEFAULT_VIEW, true);
		} else {
			handleOnValueChange(historyToken);
		}
		// searchForm.restoreStatesFromHistory(History.getToken());

		mapLink.addClickHandler(this);
		listLink.addClickHandler(this);
		uploadLink.addClickHandler(this);
		revalidateLink.addClickHandler(this);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				resize(Window.getClientWidth(), Window.getClientHeight());
			}

		});
	}

	public CustomPopupPanel getPopupView(String viewName) {
		viewName = viewName.toLowerCase();
		CustomPopupPanel popupView = viewsMap.get(viewName);
		if (popupView == null) {
			popupView = new CustomPopupPanel(viewInfos.get(viewName).getView());
			viewsMap.put(viewName, popupView);
		}
		return popupView;
	}

	//
	// public void addHistoryItem(boolean issueEvent) {
	// if (historyButtonClicked || activeViewInfo == null) {
	// return;
	// }
	// String token = historyToken();
	// GWT.log(this.getClass() + " -- History.newItem(" + token + ", "
	// + issueEvent + ")", null);
	// History.newItem(token, issueEvent);
	// historyButtonClicked = false;
	// }

	@Override
	public String historyToken() {
		if (activeViewInfo == null) {
			return History.getToken();
		}
		StringBuilder token = new StringBuilder(
				constructHistoryUrl(UrlParam.VIEW));
		String activeViewToken = activeViewInfo.getView().historyToken();
		if (!activeViewToken.equals("")) {
			boolean startWithAnd = activeViewToken.startsWith("&");
			token.append((startWithAnd ? "" : "&") + activeViewToken);
		}
		String currentView = activeViewInfo.getName();
		if (currentView.equals(LIST) || currentView.equals(MAP)
				|| currentView.equals(DETAIL)) {
			token.append("&" + constructHistoryUrl(UrlParam.PAGE) + "&");
			token.append(searchForm.getCurrentStateToken());
		}
		return token.toString();
	}

	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender == mapLink) {
			switchView(MAP, false);
			addHistoryItem(false);
			searchForm.restoreStatesFromHistory(History.getToken());
		} else if (sender == listLink) {
			switchView(LIST, false);
			addHistoryItem(false);
			searchForm.restoreStatesFromHistory(History.getToken());
		} else if (sender == uploadLink) {
			switchView(UPLOAD, false);
			activeViewInfo.getView().resetToDefaultState();
			addHistoryItem(false);
		} else if (sender == searchForm.advanceLink) {
			switchView(ADVANCE, false);
			helpLink.setStyleName("helplink");
			switchViewPanel.add(helpLink);
			addHistoryItem(false);
		} else if (sender == revalidateLink){
			addHistoryItem(false);
		    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
			if(revalidateLink.getText().equals(constants.Revalidating())){
				/*RevalidationService.Proxy.get().cancelRevalidation(sessionId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						revalidateLink.setHTML(constants.Revalidate());
						
					}

					@Override
					public void onSuccess(Void result) {
						revalidateLink.setHTML(constants.Revalidate());
						
					}
				});*/
			}else{
				
				revalidateLink.setStyleName("revalidating", true);//add loading image
				final ServerPingServiceAsync pingService = ServerPingService.Proxy.get();
				final Timer sessionAliveTimer = new Timer() {
				   public void run() {
					   pingService.ping(new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Error while pinging the server", caught);
							}
							@Override
							public void onSuccess(Void result) {
								GWT.log("Ping success");
							}
					   });
				   }
				};
				sessionAliveTimer.scheduleRepeating(REVALIDATION_SERVER_PING_INTERVAL);
				
				RevalidationService.Proxy.get().revalidate(sessionId, new AsyncCallback<RevalidationResult>() {
					
					@Override
					public void onSuccess(RevalidationResult result) {
						GWT.log("Revalidation success");
						revalidateLink.setHTML(constants.Revalidate());
						new RevalidationResultPopup(result).show(); 
						revalidateLink.setStyleName("revalidatelink");
						revalidateLink.setStyleName("revalidatelink_success", true);
						//Window.alert(constants.RevalidationSuccess());
						sessionAliveTimer.cancel();
						
					}
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error while revalidating", caught);
						revalidateLink.setHTML(constants.Revalidate());
						revalidateLink.setStyleName("revalidatelink");
						revalidateLink.setStyleName("revalidatelink_error", true);
						Window.alert(caught.getLocalizedMessage());
						sessionAliveTimer.cancel();
					}
				});
				revalidateLink.setHTML(constants.Revalidating());
			}
			
		}
	}

	public void onPageLoaded(List<Occurrence> data, int pageNumber) {
		currentPageNum = pageNumber;

	}

	@Override
	public void onResize(ResizeEvent event) {
		resize(event.getWidth(), event.getHeight());

	}

	@Override
	public void onShow() {
		super.onShow();
		setVisible(true);
	}

	/**
	 * Removed uploadLink if (@link ViewState} the ViewState is
	 * {@link ViewState#UNAUTHENTICATED} add back if it is
	 * {@link ViewState#AUTHENTICATED}. When state is UNAUTHENTICATED and the
	 * current view is {@link UploadView} switch to default view.
	 */

	@Override
	public void onStateChanged(ViewState state) {
		if (!isMyView(parent.historyToken())) {
			return;
		}
		queryFiltersMap.init(ApplicationView.getAuthenticatedUser());
		searchForm.onStateChanged(state);
		
		revalidateLink.setHTML("");
		revalidateLink.setStyleName("link");
		switch (state) {
		case ADMIN:
			revalidateLink.setHTML(constants.Revalidate());
			revalidateLink.setStyleName("revalidatelink");
		case REVIEWER:
		case RESEARCHER:
			uploadLink.setHTML(constants.Upload());
			uploadLink.setStyleName("uploadlink");
			break;
		case UNAUTHENTICATED:
			uploadLink.setHTML("");
			uploadLink.setStyleName("link");
			if (activeViewInfo != null
					&& activeViewInfo.getName().equals(UPLOAD)) {
				switchView(DEFAULT_VIEW, false);
				addHistoryItem(false);
			}
			break;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			getPopupView(activeViewInfo.getName()).show();
		} else {
			getPopupView(activeViewInfo.getName()).hide();
		}
	};

	@Override
	protected void handleOnValueChange(String historyToken) {
		historyState.setHistoryToken(historyToken);
		String view = historyState.getHistoryParameters(UrlParam.VIEW) + "";
		if (view.equals("")) {
			view = DEFAULT_VIEW;
		}
		if (ApplicationView.getCurrentState().equals(ViewState.UNAUTHENTICATED)
				&& view.equalsIgnoreCase(UPLOAD)) {
			view = DEFAULT_VIEW;
		}
		switchView(view,
				view.equalsIgnoreCase(MAP) || view.equalsIgnoreCase(LIST)
						|| view.equalsIgnoreCase(DETAIL));
		historyButtonClicked = false;
		parent.historyButtonClicked = false;
	}

	@Override
	protected boolean isMyView(String value) {
		if (value.trim().equals("") && isDefaultView()) {
			return true;
		}
		List<String> views = ApplicationView.getHistoryTokenParamValues(value,
				UrlParam.VIEW.lower());
		if (views == null || views.isEmpty()) {
			return false;
		}
		String view = views.get(0);
		for (ViewInfo info : viewInfos.values()) {
			if (info.getName().equalsIgnoreCase(view)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void resetToDefaultState() {
		ViewInfo mapViewInfo = viewInfos.get(MAP.toLowerCase());
		if (mapViewInfo.isViewConstrcuted()) {
			mapViewInfo.getView().resetToDefaultState();
		}
		ViewInfo listViewInfo = viewInfos.get(LIST.toLowerCase());
		if (listViewInfo.isViewConstrcuted()) {
			listViewInfo.getView().resetToDefaultState();
		}
	}

	@Override
	protected void resize(final int width, int height) {
		if (!isMyView(History.getToken())) {
			return;
		}
		int w = width - 20;
		toolHp.setWidth(w + "px");
		height = height - mainSp.getAbsoluteTop();
		if (height <= 0) {
			height = 1;
		}
		mainSp.setPixelSize(w, height);
		if (activeViewInfo != null) {
			getPopupView(activeViewInfo.getName()).reshow();
		}
		Window.enableScrolling(toolHp.getOffsetWidth() - 10 > w);

	}

	@Override
	protected void switchView(String view, boolean isLoadRecord) {
		if (true) {
			switchView2(view, isLoadRecord);
			return;
		}
		view = view.toLowerCase();
		ViewInfo switchViewInfo = viewInfos.get(view);
		if (switchViewInfo == null) {
			switchViewInfo = viewInfos.get(DEFAULT_VIEW);
		}
		View previousView = activeViewInfo == null ? null : activeViewInfo
				.getView();
		if (previousView != null) {
			try {
				if (previousView instanceof MapView) {
					((MapView) previousView).temperalySwitchMapType();
				}
			} catch (Exception e) {
				Window.confirm("switching map type process");
			}
		}
		if (activeViewInfo != null) {

			mainVp.remove(activeViewInfo.getView());
		}
		switchViewPanel.clear();

		activeViewInfo = switchViewInfo;
		View switchView = null;

		try {
			switchView = switchViewInfo.getView();
		} catch (Exception e) {
			Window.confirm("Error switch view:" + e.getMessage());
		}
		try {
			try {
				mainVp.add(switchView);
			} catch (Exception e) {
				GWT.log(e.getMessage(), e);
				Window.confirm("add view process");
			}
			try {
				if (switchView instanceof MapView) {
					((MapView) switchView).switchBack();
				}
			} catch (Exception e) {
				GWT.log(e.getMessage(), e);
				GWT.log(e.getMessage(), e);
				Window.confirm("switching back process");
			}
		} catch (Exception e) {
			Window.confirm("Error adding map process: " + e.getMessage());
		}
		if (view.equals(DETAIL.toLowerCase())) {
			switchViewPanel.add(mapLink);
			switchViewPanel.add(listLink);
		} else if (view.equals(LIST.toLowerCase())) {
			switchViewPanel.add(mapLink);
			// if (isLoadRecord) {
			// searchForm.restoreStatesFromHistory(History.getToken());
			// }
		} else if (view.equals(MAP.toLowerCase())) {
			switchViewPanel.add(listLink);
			// if (isLoadRecord) {
			// searchForm.restoreStatesFromHistory(History.getToken());
			// }
		} else {
			switchViewPanel.add(mapLink);
			switchViewPanel.add(listLink);
		}
		activeViewInfo = switchViewInfo;
		if (isLoadRecord) {
			searchForm.restoreStatesFromHistory(History.getToken());
		}
		switchView.onShow();
	}

	/**
	 * Temporally solution for earth map type bug switching view bug.
	 * 
	 * @param view
	 *            a view name to be switch to
	 * @param isLoadRecord
	 *            true if record will be loaded.
	 */
	protected void switchView2(String view, final boolean isLoadRecord) {
		if (view.equals(ADVANCE)) { // If we are going into Advanced View turn
									// off
			// the old search
			searchForm.mainHp.remove(searchForm.advanceLink);
			searchForm.mainHp.remove(searchForm.forLabel);
			searchForm.mainHp.remove(searchForm.searchBox);
			searchForm.mainHp.remove(searchForm.searchButton);

			if (activeViewInfo == null) {
				lastView = MAP;
			} else {
				lastView = activeViewInfo.getName();
			}
		} else if (activeViewInfo != null
				&& activeViewInfo.getName().equalsIgnoreCase(ADVANCE)) { // if
																			// we
			/**
			 * are leaving Advanced View turn on the Simple Search
			 */
			searchForm.mainHp.add(searchForm.forLabel);
			searchForm.mainHp.add(searchForm.searchBox);
			searchForm.mainHp.add(searchForm.searchButton);
			searchForm.mainHp.add(searchForm.advanceLink);

		}
		view = view.toLowerCase();
		ViewInfo switchViewInfo = viewInfos.get(view);
		if (switchViewInfo == null) {
			switchViewInfo = viewInfos.get(DEFAULT_VIEW);
		}
		if (activeViewInfo != null) {
			getPopupView(activeViewInfo.getName()).hide();
			// mainVp.remove(activeViewInfo.getView());
		}
		switchViewPanel.clear();
		activeViewInfo = switchViewInfo;
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				CustomPopupPanel view = getPopupView(activeViewInfo.getName());
				if (isLoadRecord) {
					searchForm.restoreStatesFromHistory(History.getToken());
				}
				view.show();
			}
		});
		// View switchView = switchViewInfo.getView();

		// mainVp.add(switchView);
		// boolean isShowedUpload = ApplicationView.getCurrentState() ==
		// ViewState.AUTHENTICATED;
		// if (isShowedUpload) {
		switchViewPanel.add(uploadLink);
		switchViewPanel.add(revalidateLink);

		// }
		if (view.equals(DETAIL.toLowerCase())) {
			switchViewPanel.add(mapLink);
			switchViewPanel.add(listLink);
		} else if (view.equals(LIST.toLowerCase())) {
			switchViewPanel.add(mapLink);
			// if (isLoadRecord) {
			// searchForm.restoreStatesFromHistory(History.getToken());
			// }
		} else if (view.equals(MAP.toLowerCase())) {
			switchViewPanel.add(listLink);
			// if (isLoadRecord) {
			// searchForm.restoreStatesFromHistory(History.getToken());
			// }
		} else {
			switchViewPanel.add(mapLink);
			switchViewPanel.add(listLink);
			switchViewPanel.add(uploadLink);
		}
		activeViewInfo = switchViewInfo;
		initAdvanceFields();
		// switchView.onShow();
	}

	private String constructHistoryUrl(UrlParam urlParam) {
		String param = urlParam.lower() + "=";
		switch (urlParam) {
		case VIEW:
			if (activeViewInfo == null) {
				return "";
			}
			return param += activeViewInfo.getName();
		case PAGE:
			return param + currentPageNum;
		case QUERY:
			String searchText = searchForm.getSearchQuery();
			if (searchText.equals("")) {
				return "";
			}
			return param + searchText;
		case TYPE:
			Set<String> baseFilters = query.getBaseFilters();
			String type = queryFiltersMap.getType(baseFilters);
			return param + type;
		case ERROR_TYPE:
			return param + searchForm.getInvalidatedType();
		case ST:
			return param + searchForm.getSharedType();
		case ASEARCH:
			ViewInfo advanceViewInfo = viewInfos.get(ADVANCE.toLowerCase());
			if (advanceViewInfo.isViewConstrcuted()) {
				return advanceViewInfo.getView().historyToken();
			}
			// Set<String> searchFilters = query.getSearchFilters();
			StringBuilder tokenBuilder = new StringBuilder();
			for (String filter : query.getSearchFilters()) {
				if (filter.startsWith(SearchForm.QUICK_SEARCH)
						|| filter.startsWith(SearchForm.VALIDATION_ERROR)) {
					continue;
				}
				tokenBuilder.append(param + filter + "&");
			}
			if (tokenBuilder.length() != 0) {
				tokenBuilder.deleteCharAt(tokenBuilder.length() - 1);
			}
			return tokenBuilder.toString();
		case RF:
			if (!searchForm.isResultFilterVisible()) {
				return "";
			}
			return param
					+ searchForm.getResultFilter().toString().toLowerCase();
		}
		return "";
	}

	private boolean containsAcceptedSpecies() {
		Set<String> searchFilters = query.getSearchFilters();
		if (!searchFilters.isEmpty()) {
			for (String filter : searchFilters) {
				if (filter.trim().startsWith(SearchForm.QUICK_SEARCH)) {
					return true;
				}
			}
		}
		return false;
	}

	private int getPageNum(String historyToken) {
		historyState.setHistoryToken(historyToken);
		int pageNum = (Integer) historyState
				.getHistoryParameters(UrlParam.PAGE);
		if (pageNum == HistoryState.UNDEFINED) {
			pageNum = 1;
		}
		return pageNum;
	}

	private void initAdvanceFields() {
		boolean isAdvanceFieldActive = activeViewInfo.getName().equals(ADVANCE);
		if (isAdvanceFieldActive) {
			boolean advanceInit = activeViewInfo.isViewConstrcuted();
			if (!advanceInit) {
				List<ASearchType> darwinCoreFields = new ArrayList<ASearchType>();
				List<ASearchType> toxonomicFields = new ArrayList<ASearchType>();
				List<ASearchType> identificationFields = new ArrayList<ASearchType>();
				List<ASearchType> localityFields = new ArrayList<ASearchType>();
				List<ASearchType> collectingEventsFields = new ArrayList<ASearchType>();
				List<ASearchType> biologicalFields = new ArrayList<ASearchType>();
				List<ASearchType> referenceFields = new ArrayList<ASearchType>();
				List<ASearchType> curaExtFields = new ArrayList<ASearchType>();
				List<ASearchType> geoExtFields = new ArrayList<ASearchType>();
				List<ASearchType> recordInfoFields = new ArrayList<ASearchType>();
				AdvanceSearchView view = (AdvanceSearchView) activeViewInfo
						.getView();
				// Darwin Core
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_SPECIES,
								"AcceptedSpecies", null,
								"http://code.google.com/p/rebioma/wiki/AcceptedSpecies"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_SPECIES,
								"VerbatimSpecies", null,
								"http://code.google.com/p/rebioma/wiki/VerbatimedSpecies"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.GLOBAL_UNIQUE_IDENTIFIER,
								"GlobalUniqueIdentifier", null,
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/GlobalUniqueIdentifier"));
				darwinCoreFields
						.add(new ASearchType(ValueType.DATE,
								FieldConstants.DATE_LAST_MODIFIED,
								"DateLastModified", null,
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/DateLastModified"));
				darwinCoreFields
						.add(new ASearchType(ValueType.FIXED,
								FieldConstants.BASIS_OF_RECORD,
								"BasisOfRecord",
								new String[] { "FossilSpecimen",
										"HumanObservation", "LivingSpecimen",
										"MachineObservation", "MovingImage",
										"PreservedSpecimen", "SoundRecording",
										"StillImage", "OtherSpecimen",
										"Non-standardSpecimen" },
								"http://rs.tdwg.org/dwc/terms/index.htm#BasisOfRecord"));
				darwinCoreFields.add(new ASearchType(ValueType.NUMBER,
						FieldConstants.YEAR_COLLECTED, "YearCollected", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#YearSampled"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.INSTITUTION_CODE,
								"InstitutionCode", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#InstitutionCode"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.COLLECTION_CODE,
								"CollectionCode", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#CollectionCode"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.CATALOG_NUMBER, "CatalogNumber",
								null,
								"http://rs.tdwg.org/dwc/terms/index.htm#CatalogNumber"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.INFORMATION_WITHHELD,
								"InformationWidthHeld", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#InformationWithheld"));
				darwinCoreFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.SCIENTIFIC_NAME,
								"ScientificName", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#ScientificName"));
				darwinCoreFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.REMARKS, "Remarks", null, ""));
				// Taxonomic Elements
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.HIGHER_TAXON, "HigherTaxon", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#HigherTaxon"));
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.KINGDOM, "Kingdom", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Kingdom"));
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.PHYLUM, "Phylum", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Phylum"));
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.CLASS_, "Class", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Class"));
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.ORDER, "Order", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Order"));
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.FAMILY, "Family", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Family"));
				toxonomicFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.GENUS, "Genus", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Genus"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.SPECIFIC_EPITHET,
								"SpecificEpithet", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#SpecificEpithet"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.INFRASPECIFIC_RANK,
								"InfraspecificRank", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#InfraspecificRank"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.INFRASPECIFIC_EPITHET,
								"InfraspecificEpithet", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#InfraspecificEpithet"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.AUTHOR_YEAR_OF_SCIENTIFIC_NAME,
								"AuthorYearOfScientificName", null,
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/AuthorYearOfScientificName"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.NOMENCLATURAL_CODE,
								"NomenclaturalCode", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#NomenclaturalCode"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_CLASS, "AcceptedClass",
								null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_FAMILY,
								"AcceptedFamily", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));

				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_GENUS, "AcceptedGenus",
								null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_KINGDOM,
								"AcceptedKingdom", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_NOMENCLATURAL_CODE,
								"AcceptedNomenclaturalCode", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_ORDER, "AcceptedOrder",
								null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_PHYLUM,
								"AcceptedPhylum", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_SPECIFIC_EPITHET,
								"AcceptedSpecificEpithet", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_SUBFAMILY,
								"AcceptedSubfamily", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_SUBGENUS,
								"AcceptedSubgenus", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				toxonomicFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ACCEPTED_SUBORDER,
								"AcceptedSuborder", null,
								"http://code.google.com/p/rebioma/wiki/TaxonomicAuthority"));
				// Identification Elements
				identificationFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.IDENTIFICATION_QUALIFER,
								"IdentificationQualifier", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#IdentificationQualifier"));
				// Locality Elements
				localityFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.HIGHER_GEOGRAPHY,
								"HigherGeography", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#HigherGeography"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.CONTINENT, "Continent", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Continent"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.WATER_BODY, "WaterBody", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Waterbody"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.ISLAND_GROUP, "IslandGroup", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#IslandGroup"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.ISLAND, "Island", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Island"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.COUNTRY, "Country", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Country"));
				localityFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.STATE_PROVINCE,
								"State_Province", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#StateProvince"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.COUNTY, "County", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#County"));
				localityFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.LOCALITY, "Locality", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Locality"));
				localityFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.MINIMUM_ELEVATION_IN_METERS,
								"MinimumElevationInMeters", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#MinimumElevationInMeters"));
				localityFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.MAXIMUM_ELEVATION_IN_METERS,
								"MaximumElevationInMeters", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#MaximumElevationInMeters"));
				localityFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.MINIMUM_DEPTH_IN_METERS,
								"MinimumDepthInMeters", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#MinimumDepthInMeters"));
				localityFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.MAXIMUM_DEPTH_IN_METERS,
								"MaximumDepthInMeters", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#MaximumDepthInMeters"));
				// Collecting Events Elements
				collectingEventsFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.COLLECTING_METHOD,
								"CollectingMethod", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#CollectingMethod"));
				collectingEventsFields
						.add(new ASearchType(ValueType.FIXED,
								FieldConstants.VALID_DISTRIBUTION_FLAG,
								"ValidDistributionFlag", new String[] { "true",
										"false" },
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/ValidDistributionFlag"));
				collectingEventsFields
						.add(new ASearchType(ValueType.DATE,
								FieldConstants.EARLIEST_DATE_COLLECTED,
								"EarliestDateCollected", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#EarliestDateCollected"));
				collectingEventsFields
						.add(new ASearchType(ValueType.DATE,
								FieldConstants.LATEST_DATE_COLLECTED,
								"LatestDateCollected", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#LatestDateCollected"));
				collectingEventsFields.add(new ASearchType(ValueType.NUMBER,
						FieldConstants.DAY_OF_YEAR, "DayOfYear", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#DayOfYear"));
				collectingEventsFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.COLLECTOR, "Collector", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Collector"));
				// Biological Elements
				biologicalFields.add(new ASearchType(ValueType.FIXED,
						FieldConstants.SEX, "Sex", new String[] { "Male",
								"Female", "Hermaphroditic", "Unknown" },
						"http://rs.tdwg.org/dwc/terms/index.htm#Sex"));
				biologicalFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.LIFE_STAGE, "LifeStage", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#LifeStage"));
				biologicalFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.ATTRIBUTES, "Attributes", null,
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/Attributes"));
				// References Elements
				referenceFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.IMAGE_URL, "ImageURL", null,
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/ImageURL"));
				referenceFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.RELATED_INFORMATION,
								"RelatedInformation", null,
								"http://wiki.tdwg.org/twiki/bin/view/DarwinCore/RelatedInformation"));
				// Curatorial Extension Concept
				curaExtFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.CATALOG_NUMBER_NUMERIC,
								"CatalogNumberNumeric", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#CatalogNumberNumeric"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.IDENTIFIED_BY, "IdentifiedBy", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#IdentifiedBy"));
				curaExtFields
						.add(new ASearchType(ValueType.DATE,
								FieldConstants.DATE_IDENTIFIED,
								"DateIdentified", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#DateIdentified"));
				curaExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.CATALOG_NUMBER, "CatalogNumber",
								null,
								"http://rs.tdwg.org/dwc/terms/index.htm#CatalogNumber"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.FIELD_NUMBER, "FieldNumber", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#FieldNumber"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.FIELD_NOTES, "FieldNotes", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#FieldNotes"));
				curaExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_COLLECTING_DATE,
								"VerbatimCollectingDate", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimCollectingDate"));
				curaExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_ELEVATION,
								"VerbatimElevation", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimElevation"));
				curaExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_DEPTH, "VerbatimDepth",
								null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimDepth"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.PREPARATIONS, "Preparations", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Preparations"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.TYPE_STATUS, "TypeStatus", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#TypeStatus"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.GEN_BANK_NUMBER, "GenBankNumber", null,
						""));
				curaExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.OTHER_CATALOG_NUMBERS,
								"OtherCatalogNumbers", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#OtherCatalogNumbers"));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.RELATED_CATALOGED_ITEMS,
						"RelatedCatalogedItems", null, ""));
				curaExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.DISPOSITION, "Disposition", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#Disposition"));
				curaExtFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.INDIVIDUAL_COUNT,
								"IndividualCount", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#IndividualCount"));
				// Geospatial Extension Concept List
				geoExtFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.DECIMAL_LATITUDE,
								"DecimalLatitude", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#DecimalLatitude"));
				geoExtFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.DECIMAL_LONGITUDE,
								"DecimalLongitude", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#DecimalLongitude"));
				geoExtFields
						.add(new ASearchType(ValueType.NUMBER,
								FieldConstants.GEODETIC_DATUM, "GeodeticDatum",
								null,
								"http://rs.tdwg.org/dwc/terms/index.htm#GeodeticDatum"));
				geoExtFields
						.add(new ASearchType(
								ValueType.NUMBER,
								FieldConstants.COORDINATE_UNCERTAINTY_IN_METERS,
								"CoordinateUncertaintyInMeters", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#CoordinateUncertaintyInMeters"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.POINT_RADIUS_SPATIAL_FIT,
								"PointRadiusSpatialFit", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#PointRadiusSpatialFit"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_COORDINATES,
								"VerbatimCoordinates", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimCoordinates"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_LATITUDE,
								"VerbatimLatitude", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimLatitude"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_LONGITUDE,
								"VerbatimLongitude", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimLongitude"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.VERBATIM_COORDINATE_SYSTEM,
								"VerbatimCoordinateSystem", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#VerbatimCoordinateSystem"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.GEOREFERENCE_PROTOCOL,
								"GeoreferenceProtocol", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#GeoreferenceProtocol"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.GEOREFERENCE_SOURCES,
								"GeoreferenceSources", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#GeoreferenceSources"));
				geoExtFields
						.add(new ASearchType(
								ValueType.TEXT,
								FieldConstants.GEOREFERENCE_VERIFICATION_STATUS,
								"GeoreferenceVerificationStatus", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#GeoreferenceVerificationStatus"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.GEOREFERENCE_REMARKS,
								"GeoreferenceRemarks", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#GeoreferenceRemarks"));
				geoExtFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.FOOTPRINT_WKT, "FootprintWKT", null,
						"http://rs.tdwg.org/dwc/terms/index.htm#FootprintWKT"));
				geoExtFields
						.add(new ASearchType(ValueType.TEXT,
								FieldConstants.FOOTPRINT_SPATIAL_FIT,
								"FootprintSpatialFit", null,
								"http://rs.tdwg.org/dwc/terms/index.htm#FootprintSpatialFit"));
				// record Info Fields
				recordInfoFields.add(new ASearchType(ValueType.DATE,
						FieldConstants.TIME_CREATED, "TimeCreated", null,
						"http://code.google.com/p/rebioma/wiki/ReBioMaFields"));
				recordInfoFields.add(new ASearchType(ValueType.DATE,
						FieldConstants.LAST_UPDATED, "LastUpdated", null,
						"http://code.google.com/p/rebioma/wiki/ReBioMaFields"));
				// Added by Jenjy

				recordInfoFields.add(new ASearchType(ValueType.NUMBER,
						FieldConstants.ID, "Id", null,
						"http://code.google.com/p/rebioma/wiki/ReBioMaFields"));
				// recordInfoFields.add(new ASearchType(ValueType.NUMBER,
				// FieldConstants.OWNER, "Owner", null,
				// "http://code.google.com/p/rebioma/wiki/ReBioMaFields"));
				recordInfoFields.add(new ASearchType(ValueType.TEXT,
						FieldConstants.OWNER_EMAIL, "OwnerEmail", null,
						"http://code.google.com/p/rebioma/wiki/ReBioMaFields"));
				/*
				 * recordInfoFields.add(new ASearchType(ValueType.TEXT,
				 * FieldConstants.OWNER_NAME, "OwnerName", null,
				 * "http://code.google.com/p/rebioma/wiki/ReBioMaFields"));
				 */
				view.addSearchFields(constants.DarwinCore(), darwinCoreFields,
						true);
				view.addSearchFields(constants.Taxonomic(), toxonomicFields,
						false);
				view.addSearchFields(constants.Identification(),
						identificationFields, false);
				view.addSearchFields(constants.Locality(), localityFields,
						false);
				view.addSearchFields(constants.CollectingEvents(),
						collectingEventsFields, false);
				view.addSearchFields(constants.Biological(), biologicalFields,
						false);
				view.addSearchFields(constants.References(), referenceFields,
						false);
				view.addSearchFields(constants.CuratorialExtension(),
						curaExtFields, false);
				view.addSearchFields(constants.GeospatialExtension(),
						geoExtFields, false);
				view.addSearchFields(constants.RecordInfo(), recordInfoFields,
						false);

			}
			activeViewInfo.getView().resetToDefaultState();
		}
	}

	private void initViews() {
		ViewInfo detailView = DetailView.init(this, this, query);
		viewInfos.put(DETAIL.toLowerCase(), detailView);
		ViewInfo mapView;
		mapView = MapView.init(this, query, this, true,
				(OccurrenceListener) detailView);
		viewInfos.put(MAP.toLowerCase(), mapView);
		ViewInfo listView = ListView.init(this, query, this,
				(OccurrenceListener) detailView);
		viewInfos.put(LIST.toLowerCase(), listView);
		ViewInfo advanceView = AdvanceSearchView.init(this, new Clickable() {
			public void click() {
				searchForm.searchBox.setText("");
				searchForm.search();
			}

		});
		viewInfos.put(ADVANCE.toLowerCase(), advanceView);
		ViewInfo uploadView = UploadView.init(this, new UploadListener() {

			public void onUploadComplete() {
				searchForm.setEnabled(true);
				switchViewPanel.setVisible(true);

			}

			public void onUploadStart() {
				searchForm.setEnabled(false);
				switchViewPanel.setVisible(false);
			}

		});
		viewInfos.put(UPLOAD.toLowerCase(), uploadView);
	}
	
	public int getPageSize(){
		return this.queryPageSize;
	}
	
	public void setPageSize(int ps){
		this.queryPageSize = ps;
		query.setLimit(ps);
		//on partage � ses enfants
		for(Map.Entry<String, ViewInfo> entry: viewInfos.entrySet()){
			if(entry.getKey().equalsIgnoreCase(MAP) || entry.getKey().equalsIgnoreCase(LIST) || entry.getKey().equalsIgnoreCase(DETAIL)){
				View v = entry.getValue().getView();
				if(v instanceof OccurrencePageSizeChangeHandler){
					OccurrencePageSizeChangeHandler opsch = (OccurrencePageSizeChangeHandler)v;
					if(opsch.getDataPagerWidget() != null){
						opsch.getDataPagerWidget().setPageSize(ps);
					}
					if(opsch.getOccurrencePagerWidget() != null){
						opsch.getOccurrencePagerWidget().setPageSizeSelectedItem(Integer.toString(ps));
					}
				}
			}
		}
	}
	
	private void setFieldsValue(Map<String, Object> propertyMap){
		String searchTypeValue = (String)propertyMap.get(OccurrenceSearchListener.SEARCH_TYPE_VALUE_PROPERTY_KEY);
	    if(searchTypeValue != null){
	    		 int index = searchForm.typeIndexMap.get(searchTypeValue);
	    		 searchForm.searchTypeBox.setSelectedIndex(index);
	    		 //fire changeEvent manuellement
	    		 DomEvent.fireNativeEvent(Document.get().createChangeEvent(), searchForm.searchTypeBox.asWidget());
	    		 if(searchForm.invalidatedLb.isAttached()){
	    			 String errorLbValue = (String)propertyMap.get(OccurrenceSearchListener.ERROR_QUERY_VALUE_KEY);
	    			 if(errorLbValue != null){
	    				 int selectedIndex = -1;
		    			 for(int i=0;i< searchForm.invalidatedLb.getItemCount();i++){
		    				 String value = searchForm.invalidatedLb.getValue(i);
		    				 if(errorLbValue.equalsIgnoreCase(value)){
		    					 selectedIndex = i;
		    					 break;
		    				 }
		    			 }
		    			 if(selectedIndex >= 0){
		    				 searchForm.invalidatedLb.setItemSelected(selectedIndex, true);
		    			 }
	    			 }
	    		 }
	    		 if(searchForm.sharedListBox.isAttached()){
	    			 String sharedLbValue = (String)propertyMap.get(OccurrenceSearchListener.SHARED_VALUE_KEY);
	    			 if(sharedLbValue != null){
	    				 int selectedIndex = -1;
		    			 for(int i=0;i< searchForm.sharedListBox.getItemCount();i++){
		    				 String value = searchForm.sharedListBox.getValue(i);
		    				 if(sharedLbValue.equalsIgnoreCase(value)){
		    					 selectedIndex = i;
		    					 break;
		    				 }
		    			 }
		    			 if(selectedIndex >= 0){
		    				 searchForm.sharedListBox.setItemSelected(selectedIndex, true);
		    			 }
	    			 }
	    			 
	    		 }
	    		 if(searchForm.resultFilterLb.isAttached()){
	    			 String resultFilterValue = (String)propertyMap.get(OccurrenceSearchListener.RESULT_FILTER_VALUE_KEY);
	    			 if(resultFilterValue != null){
	    				 int selectedIndex = -1;
		    			 for(int i=0;i< searchForm.resultFilterLb.getItemCount();i++){
		    				 String value = searchForm.resultFilterLb.getValue(i);
		    				 if(resultFilterValue.equalsIgnoreCase(value)){
		    					 selectedIndex = i;
		    					 break;
		    				 }
		    			 }
		    			 if(selectedIndex >= 0){
		    				 searchForm.resultFilterLb.setItemSelected(selectedIndex, true);
		    			 }
	    			 }
	    		 }
	    		 
	    		 
	    }
	}

	@Override
	public void searchQuery(OccurrenceQuery newquery, Map<String, Object> propertyMap) {
		//search()
		//on efface toutes les filtres
		query.reinitFilters();
		searchForm.clearAdanceSearch();
		//on ajoute les nouveaus filtres
		query.setBaseFilters(newquery.getBaseFilters());
		query.setSearchFilters(newquery.getSearchFilters());
		query.setDisjunctionSearchFilters(newquery.getDisjunctionSearchFilters());
	    query.setResults(newquery.getResults());
	    query.setCount(new Integer(newquery.getCount()).intValue());
	    query.setCountTotalResults(new Boolean(newquery.countTotalResults).booleanValue());
	    query.orderingMap = newquery.orderingMap == null ? null : new ArrayList<OrderKey>(newquery.orderingMap);
	    ApplicationView.getApplication().switchView(ApplicationView.OCCURRENCES, true);
		String activeView = activeViewInfo.getName();
	    if (!activeView.equals(MAP) && !activeView.equals(LIST)) {
			switchView(DEFAULT_VIEW, false);
		}
	    setFieldsValue(propertyMap);
		//addErrorQuery();
		//addSharedSearchToQuery();
		GWT.log(query.getBaseFilters() + "");
		resetToDefaultState();
		addHistoryItem(false);
		query.requestData(1);
	}
}
