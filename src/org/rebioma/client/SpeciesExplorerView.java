package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.View.ViewState;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.User;
import org.rebioma.client.gxt.treegrid.SpeciesExplorerPanel;
import org.rebioma.client.gxt.treegrid.SpeciesMoreInformationDialog;
import org.rebioma.client.gxt.treegrid.SpeciesStatistiqueDialog;
import org.rebioma.client.gxt3.treegrid.CheckBoxTreeGridListener;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.SpeciesExplorerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

public class SpeciesExplorerView extends ComponentView implements ClickHandler, ChangeHandler, CheckBoxTreeGridListener<SpeciesTreeModel> {

	private final Button searchButton;
	
	private final Button uncheckAllButton;
	
	private final SpeciesStatistiqueDialog speciesStatistiqueDialog; 
	
	private final SpeciesMoreInformationDialog speciesMoreInformationDialog;
	
	final Map<String, Integer> typeIndexMap = new HashMap<String, Integer>();
	
	/**
	 * The list of occurrence types.
	 */
	private final ListBox searchTypeBox = new ListBox();
	

	private final ListBox resultFilterLb = new ListBox();
	private final ListBox invalidatedLb = new ListBox();
	private final ListBox sharedListBox = new ListBox();
	private final HTML updateTaxonomieLink = new HTML(constants.LoadTaxonomyCsv());
	private final SpeciesExplorerPanel speciesExplorerPanel;
	//final SpeciesInfoPanel infoPanel;
	
	//boutons de recherche 
	/*private final Button findButton;
	private final Button filterButton ;
	private final Button resetButton;*/
	/**
	 * The main widget wrapped by this composite.
	 */
	final FlowPanel mainHp;
	
	private final HorizontalPanel toolHp = new HorizontalPanel();
	
	private final VerticalPanel verticalPanel;
	
	private List<OccurrenceSearchListener> occurrenceSearchListeners = null; 
	
	private SpeciesExplorerView() {
		this(null);
	}
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
			.create(SpeciesExplorerService.class);

	private SpeciesExplorerView(View parent) {
		super(parent, false);
		final Label searchLabel = new Label(" " + constants.Search() + " ");
		searchLabel.setStyleName("searchLabel");
		searchButton = new Button(constants.Search());
		uncheckAllButton = new Button(constants.ClearAllSelected());
		
		resultFilterLb.addItem(constants.Both(), "both");
		resultFilterLb.addItem(constants.Public(), "public");
		resultFilterLb.addItem(constants.Private(), "private");
		
		invalidatedLb.addItem(constants.AllValidationError(), OccurrenceView.SearchForm.ALL_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.ALL_ERROR.toLowerCase(), 0);
		invalidatedLb.addItem(constants.InvalidYearCollected(), OccurrenceView.SearchForm.YEAR_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.YEAR_ERROR.toLowerCase(), 1);
		invalidatedLb.addItem(constants.InvalidGenus(), OccurrenceView.SearchForm.GENUS_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.GENUS_ERROR.toLowerCase(), 2);
		invalidatedLb.addItem(constants.InvalidSpecificEpthet(), OccurrenceView.SearchForm.SPECIFIC_EPTHET_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.SPECIFIC_EPTHET_ERROR.toLowerCase(), 3);

		invalidatedLb.addItem(constants.InvalidDecimalLatitude(),

				OccurrenceView.SearchForm.DECIMAL_LAT_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.DECIMAL_LAT_ERROR.toLowerCase(), 4);

		invalidatedLb.addItem(constants.InvalidDecimalLongitude(),

				OccurrenceView.SearchForm.DECIMAL_LNG_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.DECIMAL_LNG_ERROR.toLowerCase(), 5);

		invalidatedLb.addItem(constants.InvalidTaxonomicClassification(),

				OccurrenceView.SearchForm.TAXO_ERROR);
		typeIndexMap.put(OccurrenceView.SearchForm.TAXO_ERROR.toLowerCase(), 6);
		updateTaxonomieLink.setStyleName("revalidatelink");

		invalidatedLb.setStyleName("ResultFilter");
		invalidatedLb.setStyleName("ResultFilter");
		searchTypeBox.setStyleName("TypeBox");
		sharedListBox.setStyleName("SharedListBox");
		resultFilterLb.setStyleName("ResultFilter");
		speciesStatistiqueDialog = new SpeciesStatistiqueDialog();
		speciesMoreInformationDialog = new SpeciesMoreInformationDialog();
		
		searchButton.addClickHandler(this);
		uncheckAllButton.addClickHandler(this);
		searchTypeBox.addChangeHandler(this);
		resultFilterLb.addChangeHandler(this);
		mainHp = new FlowPanel();
		mainHp.add(searchLabel);
		mainHp.add(searchTypeBox);
		mainHp.add(searchButton);
		mainHp.add(uncheckAllButton);
		//initWidget(mainHp);
		mainHp.setStyleName("Search-Form");
		updateTaxonomieLink.addClickHandler(this);
		toolHp.add(mainHp);
		toolHp.add(updateTaxonomieLink);
		toolHp.setWidth("100%");
		//toolHp.setCellVerticalAlignment(updateTaxonomieLink, HasVerticalAlignment.ALIGN_MIDDLE);
		toolHp.setCellHorizontalAlignment(updateTaxonomieLink, HasHorizontalAlignment.ALIGN_RIGHT);
		toolHp.setCellVerticalAlignment(mainHp,
				HasVerticalAlignment.ALIGN_MIDDLE);
		toolHp.setStyleName("OccurrenceView-ToolBar");//on utilise le css de OccurrenceView
		
		verticalPanel = new VerticalPanel();
		verticalPanel.add(toolHp); 
		verticalPanel.setCellHeight(toolHp, "25px");
		speciesExplorerPanel = new SpeciesExplorerPanel(); 
//		speciesExplorerPanel.getTreeGrid().setWidth(Window.getClientWidth()-20 +"px");
		verticalPanel.add(speciesExplorerPanel.getToolBarHaut());
	    verticalPanel.add(speciesExplorerPanel.getTreeGrid());
	    speciesExplorerPanel.addCheckBoxGridListener(this);
		initWidget(verticalPanel);
		resize(Window.getClientWidth(), (Window.getClientHeight() - 142));
		History.addValueChangeHandler(this);
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		resize(event.getWidth(), event.getHeight());
	}
	
	@Override
	protected void resize(final int width, int height) {
		int w = width - 20;
		toolHp.setWidth(w + "px");
		verticalPanel.setWidth(w + "px");
		speciesExplorerPanel.getTreeGrid().setWidth(w);
		speciesExplorerPanel.getTreeGrid().setHeight(height - speciesExplorerPanel.getTreeGrid().getAbsoluteTop() - 10 );
		//infoPanel.setWidth(w);
		Window.enableScrolling(toolHp.getOffsetWidth() - 10 > width);

	}
	
	private void addCheckedSpeciesToQuery(OccurrenceQuery query){
		List<SpeciesTreeModel> speciesTreeModels = speciesExplorerPanel.getCheckedSelection();

		if(speciesTreeModels != null && !speciesTreeModels.isEmpty()){
			Set<String> searchFilters = new HashSet<String>();
			for(SpeciesTreeModel model: speciesTreeModels){
				String sb = model.getLevelQueryFilter() + " = " 
						+ model.getLabel();
//				query.addSearchFilter(sb.toString(), true);
				searchFilters.add(sb);
			}
//			query.setSearchFilters(searchFilters);
			query.setDisjunctionSearchFilters(searchFilters);
		}
		
	}
	
	public void addOccurrenceSearchListener(OccurrenceSearchListener listener) {
		if (occurrenceSearchListeners == null) {
			occurrenceSearchListeners = new ArrayList<OccurrenceSearchListener>();
		}
		if(!occurrenceSearchListeners.contains(listener)){
			occurrenceSearchListeners.add(listener);
		}
		
	}

	@Override
	protected void resetToDefaultState() {
		onStateChanged(ApplicationView.getCurrentState());
	}

	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new SpeciesExplorerView(parent);
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
	
	/**
	 * Adds {@link #privateCb} if enabled is true and remove it if enabled
	 * is false.
	 * 
	 * @param enabled
	 *            true to show my records only check box and false to
	 *            hide/remove my records only check box
	 */
	private void setMyRecordsEnable(boolean enabled) {
		if(ApplicationView.getCurrentState()!=ViewState.SUPERADMIN){
		
			if (enabled) {
				mainHp.insert(resultFilterLb, 2);
			} else {
				mainHp.remove(resultFilterLb);
				// publicRb.setValue(true);
			}
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			public void execute() {
				resize(Window.getClientWidth(),
						Window.getClientHeight());
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
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if(source == searchButton){
			String searchType = getSearchType();
			int idx = searchTypeBox.getSelectedIndex();
			String searchTypeValue = searchTypeBox.getValue(idx);
			//contruire un OccurrenceQuery
			//et y ajouté les filtres
			Map<String, Object> propertyMap = new HashMap<String, Object>();
			OccurrenceQuery query = new OccurrenceQuery();
			ResultFilter resultFilter = getResultFilter();
			Set<String> baseFilters = query.getFiltersFromProperty(searchType,
					ApplicationView.getAuthenticatedUser(), resultFilter);
			query.setBaseFilters(baseFilters);
			if(invalidatedLb.isAttached()){
				addErrorQuery(query);
				int sIndex = invalidatedLb.getSelectedIndex();
				if (sIndex > 0) {
					String errorValue = invalidatedLb.getValue(sIndex);
					propertyMap.put(OccurrenceSearchListener.ERROR_QUERY_VALUE_KEY, errorValue);
				}
			}
			if (sharedListBox.isAttached()) {
				addSharedSearchToQuery(query);
				propertyMap.put(OccurrenceSearchListener.SHARED_VALUE_KEY, getSharedType());
			}
			addCheckedSpeciesToQuery(query);
			
			propertyMap.put(OccurrenceSearchListener.RESULT_FILTER_VALUE_KEY, getResultFilterValue());
			propertyMap.put(OccurrenceSearchListener.SEARCH_TYPE_PROPERTY_KEY, searchType);
			propertyMap.put(OccurrenceSearchListener.SEARCH_TYPE_VALUE_PROPERTY_KEY, searchTypeValue);
			GWT.log(" SpeciesExplorer Search filters: " + query.getBaseFilters());
			for(OccurrenceSearchListener listener: occurrenceSearchListeners){
				listener.searchQuery(query, propertyMap);
			}
		}else if(source == updateTaxonomieLink){
			if(updateTaxonomieLink.getHTML().equals(constants.LoadTaxonomyCsv())){
				updateTaxonomieLink.setHTML(constants.Loading()+"...");
				speciesExplorerService.loadCsv(new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						updateTaxonomieLink.setHTML(constants.LoadTaxonomyCsv());
						Window.alert("Chargement du fichier csv effectué avec succès");
					}
					@Override
					public void onFailure(Throwable caught) {
						updateTaxonomieLink.setHTML(constants.LoadTaxonomyCsv());
						Window.alert(caught.getMessage());
					}
				});	
			}
		} else if(source == uncheckAllButton){
			speciesExplorerPanel.getTreeGrid().unCheckAll();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.rebioma.client.ComponentView#onStateChanged(org.rebioma.client.View.ViewState)
	 */
	@Override
	public void onStateChanged(ViewState state) {
		searchTypeBox.clear();
		typeIndexMap.clear();

		// All Positively Reviewed
		// All Negatively Reviewed
		// All Awaiting Review
		// All Invalidated
		// All Occurrences

		// All Positively Reviewed
		searchTypeBox.addItem(constants.AllPositivelyReviewed(),
				OccurrenceView.ALL_POS_REVIEWED);
		typeIndexMap
				.put(OccurrenceView.ALL_POS_REVIEWED, searchTypeBox.getItemCount() - 1);

		// All Negatively Reviewed
		searchTypeBox.addItem(constants.AllNegativelyReviewed(),
				OccurrenceView.ALL_NEG_REVIEWED);
		typeIndexMap
				.put(OccurrenceView.ALL_NEG_REVIEWED, searchTypeBox.getItemCount() - 1);

		// All Awaiting Review
		searchTypeBox.addItem(constants.AllAwaitingReview(),
				OccurrenceView.ALL_AWAIT_REVIEW);
		typeIndexMap
				.put(OccurrenceView.ALL_AWAIT_REVIEW, searchTypeBox.getItemCount() - 1);

		// All Invalidated
		searchTypeBox.addItem(constants.AllInvalid(), OccurrenceView.ALL_INVALID);
		typeIndexMap.put(OccurrenceView.ALL_INVALID, searchTypeBox.getItemCount() - 1);

		// All Occurrences
		searchTypeBox.addItem(constants.AllOccurrences(), OccurrenceView.ALL_OCC);
		typeIndexMap.put(OccurrenceView.ALL_OCC, searchTypeBox.getItemCount() - 1);
		updateTaxonomieLink.setHTML("");
		updateTaxonomieLink.setStyleName("");
		switch (state) {
		case SUPERADMIN:
		case UNAUTHENTICATED:
			searchTypeBox.setSelectedIndex(typeIndexMap.get(OccurrenceView.ALL_OCC));
			break;
		// My Positively Reviewed
		// My Negatively Reviewed
		// My Awaiting Review
		// My Invalidated
		// My Occurrences
		// Occurrences to Review
		case ADMIN:
			updateTaxonomieLink.setHTML(constants.LoadTaxonomyCsv());
			updateTaxonomieLink.setStyleName("revalidatelink");
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
			searchTypeBox.addItem(constants.MyInvalid(), OccurrenceView.MY_INVALID);
			typeIndexMap.put(OccurrenceView.MY_INVALID, searchTypeBox.getItemCount() - 1);

			// My Occurrences
			searchTypeBox
					.addItem(constants.MyOccurrences(), OccurrenceView.MY_OCCURRENCES);
			typeIndexMap.put(OccurrenceView.MY_OCCURRENCES,
					searchTypeBox.getItemCount() - 1);


			break;
		}
		//{WD
		int selectedIndex = searchTypeBox.getSelectedIndex();
		setMyRecordsEnable(selectedIndex > OccurrenceView.SearchForm.ALL_TYPES_END_INDEX);
		String type = searchTypeBox.getItemText(selectedIndex);
		setMyRecordsInvalid(type.equalsIgnoreCase(constants
				.AllInvalid())
				|| type.equalsIgnoreCase(constants.MyInvalid()));
		setSharedType(getSearchType());
		//}
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
			if (sharedType.equalsIgnoreCase(OccurrenceView.SearchForm.SHARED_WITH_ME)) {
				query.addSearchFilter("sharedUsersCSV like "
						+ user.getEmail());
			} else if (sharedType.equalsIgnoreCase(OccurrenceView.SearchForm.SHARED_BY_ME)) {
				query.addSearchFilter("sharedUsersCSV !empty ");
			} else if (sharedType.equalsIgnoreCase(OccurrenceView.SearchForm.UNSHARED_BY_ME)) {
				query.addSearchFilter("sharedUsersCSV empty ");
			}
		}
	}
	
	private String getResultFilterValue(){
		ResultFilter resultFilter;
		String filterValue = resultFilterLb.getValue(resultFilterLb
				.getSelectedIndex());
		return filterValue;
	}
	
	private ResultFilter getResultFilter(){
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
				query.addSearchFilter(OccurrenceView.SearchForm.VALIDATION_ERROR + " like "
						+ invalidatedLb.getValue(sIndex));
			}
		}
		
	}
	
	public static boolean  isAllSelected(String searchType) {
		return searchType.equalsIgnoreCase(constants.AllInvalid())
				|| searchType.equalsIgnoreCase(constants.AllOccurrences())
				|| searchType.equalsIgnoreCase(constants
						.AllPositivelyReviewed())
				|| searchType.equalsIgnoreCase(constants
						.AllNegativelyReviewed())
				|| searchType.equalsIgnoreCase(constants
						.AllAwaitingReview());
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
				typeIndexMap.put(OccurrenceView.SearchForm.ALL_SHARED_UNSHARED.toLowerCase(), 0);
				sharedListBox.addItem(constants.SharedWithMe(),
						OccurrenceView.SearchForm.SHARED_WITH_ME);
				typeIndexMap.put(OccurrenceView.SearchForm.SHARED_WITH_ME.toLowerCase(), 1);
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
							OccurrenceView.SearchForm.ALL_SHARED_UNSHARED);
					typeIndexMap.put(OccurrenceView.SearchForm.ALL_SHARED_UNSHARED.toLowerCase(), 0);
					sharedListBox.addItem(constants.SharedByMe(),
							OccurrenceView.SearchForm.SHARED_BY_ME);
					typeIndexMap.put(OccurrenceView.SearchForm.SHARED_BY_ME.toLowerCase(), 1);
					sharedListBox.addItem(constants.UnsharedByMe(),
							OccurrenceView.SearchForm.UNSHARED_BY_ME);
					typeIndexMap.put(OccurrenceView.SearchForm.UNSHARED_BY_ME.toLowerCase(), 2);
				}

			}
			break;
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Object source = event.getSource();
		if (source == searchTypeBox) {
			int selectedIndex = searchTypeBox.getSelectedIndex();
			setMyRecordsEnable(selectedIndex > OccurrenceView.SearchForm.ALL_TYPES_END_INDEX);
			String type = searchTypeBox.getItemText(selectedIndex);
			setMyRecordsInvalid(type.equalsIgnoreCase(constants
					.AllInvalid())
					|| type.equalsIgnoreCase(constants.MyInvalid()));
			// setMyRecordsInvalid(selectedIndex == INVALIDATED_INDEX
			// || selectedIndex == MY_INVALIDATED_INDEX);
			setSharedType(getSearchType());
		}else if(source == resultFilterLb){
			String searchType = getSearchType();
			setSharedType(searchType);
		}
	}

	@Override
	public void onTreeNodeMoreInformationIconClick(Event event,
			TreeNode<SpeciesTreeModel> node) {
		SpeciesTreeModel model = node.getModel();
		if(model != null){
			this.speciesMoreInformationDialog.showInformations(model);
		}
	}

	@Override
	public void onTreeNodeStatisticIconClick(Event event,
			TreeNode<SpeciesTreeModel> node) {
		SpeciesTreeModel model = node.getModel();
		if(model != null){
			this.speciesStatistiqueDialog.showStatistic(model);
		}
	}
	
}
