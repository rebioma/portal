package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.HomestatService;
import org.rebioma.client.services.HomestatServiceAsync;
import org.rebioma.client.services.TaxonomyService;
import org.rebioma.client.services.TaxonomyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
public class HomeView extends ComponentView implements ClickHandler,
		ChangeHandler {
	final TextBox searchBox;
	private final Button searchButton;
	final Map<String, Integer> typeIndexMap = new HashMap<String, Integer>();
	private final ListBox searchTypeBox = new ListBox();
	private final ListBox invalidatedLb = new ListBox();
	private final ListBox sharedListBox = new ListBox();
	private final ListBox resultFilterLb = new ListBox();
	private final Label forLabel = new Label(" " + constants.For() + " ");
	public final VerticalPanel mainHp;
	private List<OccurrenceSearchListener> occurrenceSearchListeners = null;
	private ContentPanel panel;
	private Label lbO;
	private Label lbCountUser;
	private Label lbCountSpecies;
	private VerticalLayoutContainer vlc;
	private VerticalLayoutContainer vside;
	private HorizontalLayoutContainer hContent;
	private VerticalPanel pobservation;
	private VerticalPanel pAddObservation;
	VerticalLayoutContainer layoutobs = new VerticalLayoutContainer();
	HorizontalLayoutContainer hstat = new HorizontalLayoutContainer();
	VerticalLayoutContainer hsideright = new VerticalLayoutContainer();
	VerticalLayoutContainer layoutuser = new VerticalLayoutContainer();
	VerticalLayoutContainer layoutsp = new VerticalLayoutContainer();
	private final HomestatServiceAsync statService = GWT
			.create(HomestatService.class);
	public HomeView(final View parent) {
		super(parent, false);
		final Label searchLabel = new Label(" " + constants.Search() + " ");
		searchLabel.setStyleName("searchLabel");
		searchButton = new Button(constants.Search());
		resultFilterLb.addItem(constants.Both(), "both");
		resultFilterLb.addItem(constants.Public(), "public");
		resultFilterLb.addItem(constants.Private(), "private");

		invalidatedLb.addItem(constants.AllValidationError(),
				OccurrenceView.SearchForm.ALL_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.ALL_ERROR.toLowerCase(), 0);
		invalidatedLb.addItem(constants.InvalidYearCollected(),
				OccurrenceView.SearchForm.YEAR_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.YEAR_ERROR.toLowerCase(), 1);
		invalidatedLb.addItem(constants.InvalidGenus(),
				OccurrenceView.SearchForm.GENUS_ERROR);
		typeIndexMap
				.put(OccurrenceView.SearchForm.GENUS_ERROR.toLowerCase(), 2);
		invalidatedLb.addItem(constants.InvalidSpecificEpthet(),
				OccurrenceView.SearchForm.SPECIFIC_EPTHET_ERROR);
		typeIndexMap.put(
				OccurrenceView.SearchForm.SPECIFIC_EPTHET_ERROR.toLowerCase(),
				3);

		invalidatedLb.addItem(constants.InvalidDecimalLatitude(),

		OccurrenceView.SearchForm.DECIMAL_LAT_ERROR);
		typeIndexMap.put(
				OccurrenceView.SearchForm.DECIMAL_LAT_ERROR.toLowerCase(), 4);

		invalidatedLb.addItem(constants.InvalidDecimalLongitude(),

		OccurrenceView.SearchForm.DECIMAL_LNG_ERROR);
		typeIndexMap.put(
				OccurrenceView.SearchForm.DECIMAL_LNG_ERROR.toLowerCase(), 5);

		invalidatedLb.addItem(constants.InvalidTaxonomicClassification(),

		OccurrenceView.SearchForm.TAXO_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.TAXO_ERROR.toLowerCase(), 6);
		invalidatedLb.setStyleName("style-listbox");
		invalidatedLb.setStyleName("style-listbox");
		searchTypeBox.setStyleName("style-listbox");
		sharedListBox.setStyleName("style-listbox");
		resultFilterLb.setStyleName("style-listbox");
		forLabel.setStylePrimaryName("forLabel");
		resultFilterLb.addChangeHandler(this);
		searchButton.addClickHandler(this);
		searchTypeBox.addChangeHandler(this);
		searchBox = new TextBox();
		searchBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchButton.click();
				}

			}
		});
		mainHp = new VerticalPanel();
		mainHp.add(new HTML(
				"<center style='Font-weight:BOLD;Font-size:20px; color:grey;'><h1 style='fontSize:16px;fonWeight:bold;color:grey;'>Search for Occurrence</h1></center>"));
		mainHp.add(searchTypeBox);
		mainHp.add(forLabel);
		mainHp.add(searchBox);
		mainHp.add(searchButton);
		mainHp.setStyleName("Search-Form");
		// /////////////////
		vlc = new VerticalLayoutContainer();
		vside = new VerticalLayoutContainer();
		lbO = new Label();
		lbCountUser = new Label();
		lbCountSpecies = new Label();
		final VerticalLayoutContainer h1 = new VerticalLayoutContainer();
		h1.setStyleName("box1-content");
		HorizontalPanel hpan = new HorizontalPanel();
		hpan.add(new HTML("<a target='_blank' href='http://rebioma.net/index.php/fr/2014-05-30-08-40-13/telechargement/doc_download/57-format-darwincore-2' style='margin: 10px 10px;'>"+constants.FormatDarwincore()+"</a>"));
		hpan.add(new HTML("<a target='_blank' href='https://sites.google.com/site/rebiomahelp/home/francais#up' style='margin: 10px 10px;'>"+constants.HowToUpload()+"</a>"));
		TextButton btn = new TextButton("upload");
		btn.setIcon(Resources.INSTANCE.upload());
		btn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if (ApplicationView.getCurrentState() != ViewState.UNAUTHENTICATED) {
					ApplicationView.getApplication().switchView(ApplicationView.OCCURRENCES, true);
					} else {
					MessageBox messageBox = new MessageBox("Message");
					messageBox.add(new HTML("<center>"+constants.lbl_PleaseLogin()+"</center>"));
					messageBox.show();
					}
			}

		});

		statService.getCountOccurrence(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(""+constants.verifyConnection());
			}

			@Override
			public void onSuccess(Integer result) {
				String rs="";
				rs = NumberFormat.getDecimalFormat().format(result);
				lbO.setText("" + rs);
				lbO.setStyleName("styleLabel");
			}

		});
		statService.getCountSpecies(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(""+constants.verifyConnection());
			}

			@Override
			public void onSuccess(Integer result) {
				String rs="";
				rs = NumberFormat.getDecimalFormat().format(result);
				lbCountSpecies.setText("" + rs);
				lbCountSpecies.setStyleName("styleLabel");
			}

		});
		statService.getCountUser(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(""+constants.verifyConnection());
			}

			@Override
			public void onSuccess(Integer result) {
				String rs="";
				rs = NumberFormat.getDecimalFormat().format(result);
				lbCountUser.setText("" + rs);
				lbCountUser.setStyleName("styleLabel");
			}

		});

		pAddObservation = new VerticalPanel();
		pAddObservation.setSpacing(3);
		mainHp.setStyleName("box1-content");
		pAddObservation.setStyleName("box1-content");
		hsideright.add(mainHp, new VerticalLayoutData(400, 250,
				new Margins(0, 0, 5, 0)));
		hsideright.add(pAddObservation, new VerticalLayoutData(400, 150, new Margins(0,
				0, 5, 0)));
		pAddObservation.add(new HTML(
				"<center style='Font-weight:BOLD;Font-size:20px; color:grey;'>"
						+ new Image(Resources.INSTANCE.add()) + " "
						+ constants.lbl_addObservation() + "</center>"));
		
		pAddObservation.add(btn);
		pAddObservation.add(new HTML("<h1 style='margin-left:10px;'>"+constants.lbl_registerUserOnly()+"</h1>"));
		pAddObservation.add(new HTML("<h1 style='margin-left:10px;'>"+constants.lbl_PleaseLogin()+"</h1>"));
		pAddObservation.add(hpan);
		hContent = new HorizontalLayoutContainer();
		VerticalPanel puser = new VerticalPanel();
		pobservation = new VerticalPanel();
		VerticalPanel pspecies = new VerticalPanel();
		AbsolutePanel h = new AbsolutePanel();
		h.add(vside);
		puser.setWidth("100%");
		HTML lbo = new HTML("<center><h1 style='Font-weight:BOLD;Font-size:20px; color:grey;'>" + constants.lbl_Observation()+"</center></h1>");
		HTML lbs = new HTML("<center><h1 style='Font-weight:BOLD;Font-size:20px; color:grey;'>" + constants.lbl_species()+"</center></h1>");
		HTML lbu =new HTML("<center><h1 style='Font-weight:BOLD;Font-size:20px; color:grey;'>" + constants.lbl_user()+"</center></h1>");		
		puser.add(lbu);
		puser.setSpacing(10);
		puser.add(lbCountUser);
		puser.add(new HTML("<center><h1>" + constants.lbl_usersaved() + "</h1></center>"));
		pobservation.setWidth("100%");
		pobservation.setSpacing(10);
		pobservation.add(lbo);
		pobservation.add(lbO);
		pobservation.add(new HTML("<center><h1 style:'font-weight:Bold;'>"
				+ constants.lbl_observationsaved() + "</h1></center>"));
		pspecies.setWidth("100%");
		pspecies.setSpacing(10);
		pspecies.add(lbs);
		pspecies.add(lbCountSpecies);
		pspecies.add(new HTML("<center><h1>" + constants.lbl_speciessaved() + "</h1></center>"));
		puser.setStyleName("box1");
		pobservation.setStyleName("box1");
		pspecies.setStyleName("box1");
		final HTML l = new HTML(
				constants.welcome()
						+ " "
						+ "<a target='_blank' href='https://sites.google.com/site/rebiomahelp/home/francais#mission'>"
						+ constants.more() + "</a>");
		final HorizontalPanel hp=new HorizontalPanel();
		hp.setStylePrimaryName("box1-content");
		h1.add(l, new VerticalLayoutData(1, 80,
				new Margins(20, 0, 5, 0)));
		h1.add(hstat, new VerticalLayoutData(1, 100, new Margins(50,
				0, 5, 30)));
		hstat.add(pobservation, new HorizontalLayoutData(0.33, 80,
				new Margins(0, 10, 0, 10)));
		hstat.add(puser, new HorizontalLayoutData(0.33, 80,
				new Margins(0, 10, 0, 10)));
		hstat.add(pspecies, new HorizontalLayoutData(0.33, 80,
				new Margins(0, 10, 0, 10)));
		hContent.add(hsideright, new HorizontalLayoutData(0.25, 500,
				new Margins(10, 10, 10, 10)));
		hContent.add(h1, new HorizontalLayoutData(0.75, 500, new Margins(10, 10,
				10, 20)));
		ContentPanel pContent = new ContentPanel();
		pContent.setBodyStyle("background: #E1F5A9");
		pContent.add(hContent);
		pContent.setHeaderVisible(false);
		vlc.add(pContent, new VerticalLayoutData(1, 500, new Margins(0)));
		vlc.setScrollMode(ScrollMode.AUTO);
		panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.add(vlc);
		initWidget(panel);	
	}

	public void addOccurrenceSearchListener(OccurrenceSearchListener listener) {
		if (occurrenceSearchListeners == null) {
			occurrenceSearchListeners = new ArrayList<OccurrenceSearchListener>();
		}
		if (!occurrenceSearchListeners.contains(listener)) {
			occurrenceSearchListeners.add(listener);
		}

	}

	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new HomeView(parent);
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

	@Override
	protected void resetToDefaultState() {
		onStateChanged(ApplicationView.getCurrentState());
	}

	private void setMyRecordsEnable(boolean enabled) {
		if (ApplicationView.getCurrentState() != ViewState.SUPERADMIN) {

			if (enabled) {
				mainHp.insert(resultFilterLb, 2);
			} else {
				mainHp.remove(resultFilterLb);
				// publicRb.setValue(true);
			}
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				resize(Window.getClientWidth(), Window.getClientHeight());
			}
		});
	}

	/**
	 * @return the selected occurrence type in the list box
	 */
	public String getSearchType() {
		int index = searchTypeBox.getSelectedIndex();
		return searchTypeBox.getItemText(index);
	}

	public String historyToken() {
		// TODO Auto-generated method stub
		return super.historyToken();
	}

	@Override
	public void onChange(ChangeEvent event) {
		Object source = event.getSource();
		if (source == searchTypeBox) {
			int selectedIndex = searchTypeBox.getSelectedIndex();
			setMyRecordsEnable(selectedIndex > OccurrenceView.SearchForm.ALL_TYPES_END_INDEX);
			String type = searchTypeBox.getItemText(selectedIndex);
			setMyRecordsInvalid(type.equalsIgnoreCase(constants.AllInvalid())
					|| type.equalsIgnoreCase(constants.MyInvalid()));
			// setMyRecordsInvalid(selectedIndex == INVALIDATED_INDEX
			// || selectedIndex == MY_INVALIDATED_INDEX);
			setSharedType(getSearchType());
		} else if (source == resultFilterLb) {
			String searchType = getSearchType();
			setSharedType(searchType);
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == searchButton) {
			String searchType = getSearchType();
			int idx = searchTypeBox.getSelectedIndex();
			String searchTypeValue = searchTypeBox.getValue(idx);
			String searchText = searchBox.getText();
			
			Map<String, Object> propertyMap = new HashMap<String, Object>();
			OccurrenceQuery query = new OccurrenceQuery();
			ResultFilter resultFilter = getResultFilter();
			Set<String> baseFilters = query.getFiltersFromProperty(searchType,
					ApplicationView.getAuthenticatedUser(), resultFilter);
			query.setBaseFilters(baseFilters);
			if (!searchText.equals("")) {
				query.addSearchFilter(OccurrenceView.SearchForm.QUICK_SEARCH + " = " + searchText);
			}
			if (invalidatedLb.isAttached()) {
				addErrorQuery(query);
				int sIndex = invalidatedLb.getSelectedIndex();
				if (sIndex > 0) {
					String errorValue = invalidatedLb.getValue(sIndex);
					propertyMap.put(
							OccurrenceSearchListener.ERROR_QUERY_VALUE_KEY,
							errorValue);
				}
			}
		
			if (sharedListBox.isAttached()) {
				addSharedSearchToQuery(query);
				propertyMap.put(OccurrenceSearchListener.SHARED_VALUE_KEY,
						getSharedType());
			}
			propertyMap.put(OccurrenceSearchListener.RESULT_FILTER_VALUE_KEY,
					getResultFilterValue());
			propertyMap.put(OccurrenceSearchListener.SEARCH_TYPE_PROPERTY_KEY,
					searchType);
			propertyMap.put(
					OccurrenceSearchListener.SEARCH_TYPE_VALUE_PROPERTY_KEY,
					searchTypeValue);
			GWT.log(" SpeciesExplorer Search filters: "
					+ query.getBaseFilters());
			for (OccurrenceSearchListener listener : occurrenceSearchListeners) {
				listener.searchQuery(query, propertyMap);
			}
		}
	}

	@Override
	public void onStateChanged(ViewState state) {
		searchTypeBox.clear();
		typeIndexMap.clear();
		searchBox.setText("");
		searchTypeBox.addItem(constants.AllPositivelyReviewed(),
				OccurrenceView.ALL_POS_REVIEWED);
		typeIndexMap.put(OccurrenceView.ALL_POS_REVIEWED,
				searchTypeBox.getItemCount() - 1);

		// All Negatively Reviewed
		searchTypeBox.addItem(constants.AllNegativelyReviewed(),
				OccurrenceView.ALL_NEG_REVIEWED);
		typeIndexMap.put(OccurrenceView.ALL_NEG_REVIEWED,
				searchTypeBox.getItemCount() - 1);

		// All Awaiting Review
		searchTypeBox.addItem(constants.AllAwaitingReview(),
				OccurrenceView.ALL_AWAIT_REVIEW);
		typeIndexMap.put(OccurrenceView.ALL_AWAIT_REVIEW,
				searchTypeBox.getItemCount() - 1);

		// All Invalidated
		searchTypeBox.addItem(constants.AllInvalid(),
				OccurrenceView.ALL_INVALID);
		typeIndexMap.put(OccurrenceView.ALL_INVALID,
				searchTypeBox.getItemCount() - 1);

		// All Occurrences
		searchTypeBox.addItem(constants.AllOccurrences(),
				OccurrenceView.ALL_OCC);
		typeIndexMap.put(OccurrenceView.ALL_OCC,
				searchTypeBox.getItemCount() - 1);
		switch (state) {
		case SUPERADMIN:
		case UNAUTHENTICATED:
			searchTypeBox.setSelectedIndex(typeIndexMap
					.get(OccurrenceView.ALL_OCC));
			break;
		case ADMIN:
		case REVIEWER:
			// // Occurrences to Review
			searchTypeBox.addItem(constants.OccurrencesToReview(),
					OccurrenceView.OCCURRENCES_TO_REVIEW);
			typeIndexMap.put(OccurrenceView.OCCURRENCES_TO_REVIEW,
					searchTypeBox.getItemCount() - 1);
			// My Positively Reviewed
			searchTypeBox.addItem(constants.MyPositivelyReviewed(),
					OccurrenceView.MY_POS_REVIEWED);
			typeIndexMap.put(OccurrenceView.MY_POS_REVIEWED,
					searchTypeBox.getItemCount() - 1);

			// My Negatively Reviewed
			searchTypeBox.addItem(constants.MyNegativelyReviewed(),
					OccurrenceView.MY_NEG_REVIEWED);
			typeIndexMap.put(OccurrenceView.MY_NEG_REVIEWED,
					searchTypeBox.getItemCount() - 1);
		case RESEARCHER:
			// My overall Positively Reviewed
			searchTypeBox.addItem(constants.MyOverallPositivelyReviewed(),
					OccurrenceView.MY_OVERALL_POS_REVIEW);
			typeIndexMap.put(OccurrenceView.MY_OVERALL_POS_REVIEW,
					searchTypeBox.getItemCount() - 1);

			// My overall Negatively Reviewed
			searchTypeBox.addItem(constants.MyOverallNegativelyReviewed(),
					OccurrenceView.MY_OVERALL_NEG_REVIEW);
			typeIndexMap.put(OccurrenceView.MY_OVERALL_NEG_REVIEW,
					searchTypeBox.getItemCount() - 1);
			// My Awaiting Review
			searchTypeBox.addItem(constants.MyAwaitingReview(),
					OccurrenceView.MY_AWAITING_REVIEW);
			typeIndexMap.put(OccurrenceView.MY_AWAITING_REVIEW,
					searchTypeBox.getItemCount() - 1);
			// My Invalidated
			searchTypeBox.addItem(constants.MyInvalid(),
					OccurrenceView.MY_INVALID);
			typeIndexMap.put(OccurrenceView.MY_INVALID,
					searchTypeBox.getItemCount() - 1);

			// My Occurrences
			searchTypeBox.addItem(constants.MyOccurrences(),
					OccurrenceView.MY_OCCURRENCES);
			typeIndexMap.put(OccurrenceView.MY_OCCURRENCES,
					searchTypeBox.getItemCount() - 1);

			break;
		}
		int selectedIndex = searchTypeBox.getSelectedIndex();
		setMyRecordsEnable(selectedIndex > OccurrenceView.SearchForm.ALL_TYPES_END_INDEX);
		String type = searchTypeBox.getItemText(selectedIndex);
		setMyRecordsInvalid(type.equalsIgnoreCase(constants.AllInvalid())
				|| type.equalsIgnoreCase(constants.MyInvalid()));
		setSharedType(getSearchType());
	}

	public static boolean isAllSelected(String searchType) {
		return searchType.equalsIgnoreCase(constants.AllInvalid())
				|| searchType.equalsIgnoreCase(constants.AllOccurrences())
				|| searchType.equalsIgnoreCase(constants
						.AllPositivelyReviewed())
				|| searchType.equalsIgnoreCase(constants
						.AllNegativelyReviewed())
				|| searchType.equalsIgnoreCase(constants.AllAwaitingReview());
	}

	void setMyRecordsInvalid(boolean invalidated) {
		if (invalidated) {
			int beforeIndex = mainHp.getWidgetIndex(resultFilterLb) == -1 ? 2
					: 3;
			mainHp.insert(invalidatedLb, beforeIndex);
		} else {
			mainHp.remove(invalidatedLb);
		}
		invalidatedLb.setSelectedIndex(0);
	}

	private String getSharedType() {
		int selectedIndex = sharedListBox.getSelectedIndex();
		if (selectedIndex < 0) {
			return "";
		}
		return sharedListBox.getValue(selectedIndex);
	}

	private void addSharedSearchToQuery(OccurrenceQuery query) {
		if (sharedListBox.isAttached()) {
			String sharedType = getSharedType();
			User user = ApplicationView.getAuthenticatedUser();
			if (user == null) {
				return;
			}
			if (sharedType
					.equalsIgnoreCase(OccurrenceView.SearchForm.SHARED_WITH_ME)) {
				query.addSearchFilter("sharedUsersCSV like " + user.getEmail());
			} else if (sharedType
					.equalsIgnoreCase(OccurrenceView.SearchForm.SHARED_BY_ME)) {
				query.addSearchFilter("sharedUsersCSV !empty ");
			} else if (sharedType
					.equalsIgnoreCase(OccurrenceView.SearchForm.UNSHARED_BY_ME)) {
				query.addSearchFilter("sharedUsersCSV empty ");
			}
		}
	}

	private String getResultFilterValue() {
		String filterValue = resultFilterLb.getValue(resultFilterLb
				.getSelectedIndex());
		return filterValue;
	}

	private ResultFilter getResultFilter() {
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

	private void addErrorQuery(OccurrenceQuery query) {
		int searchIndex = searchTypeBox.getSelectedIndex();
		String type = searchTypeBox.getItemText(searchIndex);
		boolean isInvalid = type.equalsIgnoreCase(constants.AllInvalid())
				|| type.equalsIgnoreCase(constants.MyInvalid());
		if (isInvalid) {
			int sIndex = invalidatedLb.getSelectedIndex();
			if (sIndex > 0) {
				query.addSearchFilter(OccurrenceView.SearchForm.VALIDATION_ERROR
						+ " like " + invalidatedLb.getValue(sIndex));
			}
		}

	}

	void setSharedType(String searchType) {
		ViewState viewState = ApplicationView.getCurrentState();
		sharedListBox.clear();
		switch (viewState) {
		case SUPERADMIN:
			mainHp.insert(resultFilterLb, 2);
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
						OccurrenceView.SearchForm.ALL_SHARED_UNSHARED);
				typeIndexMap.put(OccurrenceView.SearchForm.ALL_SHARED_UNSHARED
						.toLowerCase(), 0);
				sharedListBox.addItem(constants.SharedWithMe(),
						OccurrenceView.SearchForm.SHARED_WITH_ME);
				typeIndexMap.put(
						OccurrenceView.SearchForm.SHARED_WITH_ME.toLowerCase(),
						1);
			} else {
				String resultFilter = resultFilterLb.getValue(resultFilterLb
						.getSelectedIndex());
				if (resultFilter.equalsIgnoreCase("public")) {
					mainHp.remove(sharedListBox);
				} else {
					if (!sharedListBox.isAttached()) {
						mainHp.insert(sharedListBox, 2);
					}
					sharedListBox.addItem(constants.AllSharedUnshared(),
							OccurrenceView.SearchForm.ALL_SHARED_UNSHARED);
					typeIndexMap.put(
							OccurrenceView.SearchForm.ALL_SHARED_UNSHARED
									.toLowerCase(), 0);
					sharedListBox.addItem(constants.SharedByMe(),
							OccurrenceView.SearchForm.SHARED_BY_ME);
					typeIndexMap.put(OccurrenceView.SearchForm.SHARED_BY_ME
							.toLowerCase(), 1);
					sharedListBox.addItem(constants.UnsharedByMe(),
							OccurrenceView.SearchForm.UNSHARED_BY_ME);
					typeIndexMap.put(OccurrenceView.SearchForm.UNSHARED_BY_ME
							.toLowerCase(), 2);
				}

			}
			break;
		}
	}
	public static AppConstants getConstants() {
		return constants;
	}	
}
