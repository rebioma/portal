package org.rebioma.client;

import java.util.List;

import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.TaxonomyService;
import org.rebioma.client.services.TaxonomyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

public class ThreatenedSpeciesListView extends ComponentView implements
		ClickHandler, ChangeHandler {
	private FlexTable flexTable = new FlexTable();
	private final TaxonomyServiceAsync txService = GWT
			.create(TaxonomyService.class);
	private VerticalPanel panel;
	private VerticalLayoutContainer vlc;
	private VerticalLayoutContainer vcontent;

	public ThreatenedSpeciesListView(View parent) {
		super(parent, false);
		final Label la=new Label("Scientific name");
		final Label lk=new Label("Kingdom");
		final Label lp=new Label("Phylum");
		final Label lc=new Label("Class");
		final Label lo=new Label("Order");
		final Label lf=new Label("Family");
		final Label lg=new Label("Genus");
		final Label ls=new Label("Specific epithet");
		vlc = new VerticalLayoutContainer();
		vcontent = new VerticalLayoutContainer();
		AbsolutePanel h1 = new AbsolutePanel();
		ContentPanel h = new ContentPanel();
		h.setBodyStyle("backgroundColor:#bac6d2");
		h.setHeaderVisible(false);
		h.add(vcontent);
		h1.add(new HTML(
				"<h1 style='text-align: center;Font-weight: BOLD;Font-size: 20px;color: grey;'>"
						+ constants.ThreatenedSpeciesList() + "</h1>"));
		HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
		final VerticalPanel vpiucnstatus = new VerticalPanel();
		VerticalLayoutContainer vcontenttable = new VerticalLayoutContainer();
		// Create a CellTable.
		final CellTable<Taxonomy> table = new CellTable<Taxonomy>();
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Add a text column
		TextColumn<Taxonomy> iucnstatus = new TextColumn<Taxonomy>() {
			@Override
			public String getValue(Taxonomy obj) {
				return obj.getIucn();
			}
		};
		table.addColumn(iucnstatus, constants.IUCN_status());
		flexTable.setCellSpacing(10);
		ScrollPanel scrollPanel = new ScrollPanel();
		flexTable.setWidth("100%");
		scrollPanel.add(flexTable);
		scrollPanel.setSize("300", "200");
		// Add a selection model to handle user selection.
		final SingleSelectionModel<Taxonomy> selectionModel = new SingleSelectionModel<Taxonomy>();
		table.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Taxonomy selected = selectionModel
								.getSelectedObject();
						if (selected != null) {
							String status = selected.getIucn();
							PopupMessage.getInstance()
									.showMessage("loading...");
							txService.threatenedSpecies(status,
									new AsyncCallback<List<Taxonomy>>() {
										@Override
										public void onFailure(Throwable arg0) {
										}

										@Override
										public void onSuccess(List<Taxonomy> rs) {
											flexTable.removeAllRows();
											for (int col = 0; col < 8; col++) {
												flexTable.getCellFormatter().addStyleName(0, col, "FlexTable_header");
											}
											for (int row = 1; row < (rs.size()+1); row++) {
												flexTable.setText(0, 0,la.getText());
												flexTable.setText(row, 0, rs.get(row-1)
														.getAcceptedSpecies());
												flexTable.setText(0, 1,
													lk.getText());
												flexTable.setText(row, 1, rs
														.get(row-1).getKingdom());
												flexTable.setText(0, 2,
													lp.getText());
												flexTable.setText(row, 2, rs
														.get(row-1).getPhylum());
												flexTable
													.setText(0, 3, lc.getText());
												flexTable.setText(row, 3, rs
														.get(row-1).getClass_());
												flexTable
													.setText(0, 4, lo.getText());
												flexTable.setText(row, 4, rs
														.get(row-1).getOrder());
												flexTable.setText(0, 5,
													lf.getText());
												flexTable.setText(row, 5, rs
														.get(row-1).getFamily());
												flexTable
														.setText(0, 6, lg.getText());
												flexTable.setText(row, 6, rs
														.get(row-1).getGenus());
												flexTable.setText(0, 7,
														ls.getText());
												flexTable.setText(row, 7, rs
														.get(row-1)
														.getSpecificEpithet());
											}
										}
									});
						}
					}
				});

		txService.getiucn_status(new AsyncCallback<List<Taxonomy>>() {
					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Taxonomy> result) {
							for (int i = 0; i < result.size(); i++)
							table.setRowCount(result.size(), true);
							// Push the data into the widget.
							table.setRowData(0, result);
						
					}

				});
		vpiucnstatus.add(table);
		vcontenttable.add(scrollPanel, new VerticalLayoutData(1, 400,
				new Margins(0)));
		vcontenttable.setScrollMode(ScrollMode.AUTO);
		hlc.add(vpiucnstatus, new HorizontalLayoutData(0.1, 450, new Margins(
				10, 10, 10, 10)));
		hlc.add(vcontenttable, new HorizontalLayoutData(0.9, 450, new Margins(
				10, 10, 10, 10)));
		vcontent.add(hlc, new VerticalLayoutData(1, 500, new Margins(0, 0, 50,
				0)));
		h1.setStyleName("box1-content content");
		vlc.add(h1, new VerticalLayoutData(1, 50, new Margins(0, 0, 0, 0)));
		vlc.add(h, new VerticalLayoutData(1, 450, new Margins(0)));
		panel = new VerticalPanel();
		panel.add(vlc);
		initWidget(panel);
	}

	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new ThreatenedSpeciesListView(parent);
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

	@Override
	public void onChange(ChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	public static AppConstants getConstants() {
		return constants;
	}

}
