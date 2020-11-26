package org.rebioma.client;

import java.util.List;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.Style;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsFormatter;
import org.moxieapps.gwt.highcharts.client.labels.DataLabels;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsFormatter;
import org.moxieapps.gwt.highcharts.client.labels.Labels;
import org.moxieapps.gwt.highcharts.client.labels.PieDataLabels;
import org.moxieapps.gwt.highcharts.client.labels.XAxisLabels;
import org.moxieapps.gwt.highcharts.client.labels.YAxisLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.PiePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions;
import org.rebioma.client.bean.GraphicModel;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.GraphicService;
import org.rebioma.client.services.GraphicServiceAsync;
import org.rebioma.client.services.TaxonomyService;
import org.rebioma.client.services.TaxonomyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
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

public class GraphicsView extends ComponentView implements ClickHandler,
		ChangeHandler {
	private final GraphicServiceAsync graphicService = GWT
			.create(GraphicService.class);
	private final TaxonomyServiceAsync txService = GWT
			.create(TaxonomyService.class);
	private HorizontalPanel panel;
	private VerticalLayoutContainer vlc;
	private VerticalLayoutContainer vchart;

	public GraphicsView(View parent) {
		super(parent, false);
		vlc = new VerticalLayoutContainer();
		vchart = new VerticalLayoutContainer();
		Button btOcc = new Button(constants.lbl_Observation());
		Button btspecies = new Button(constants.AllSpecies());
		Button btTspecies = new Button(constants.TerrestrialSpecies());
		Button btMspecies = new Button(constants.MarineSpecies());
		btOcc.setStylePrimaryName("btstyle");
		btspecies.setStylePrimaryName("btstyle");
		btTspecies.setStylePrimaryName("btstyle");
		btMspecies.setStylePrimaryName("btstyle");
		final HorizontalPanel hp = new HorizontalPanel();
		final VerticalPanel hpkingdom = new VerticalPanel();
		final VerticalPanel hpPiechart = new VerticalPanel();
		final Button btAllspecies = new Button(constants.AllSpecies());
		final Button btAllTspecies = new Button(constants.TerrestrialSpecies());
		final Button btAllMspecies = new Button(constants.MarineSpecies());
		Label lbyear=new Label(constants.From_the_year());
		Label lba=new Label(constants.until());
		Button btsearch= new Button(constants.Search());
		final TextBox txyear1=new TextBox();
		final TextBox txyear2=new TextBox();
		VerticalPanel vpyear=new VerticalPanel();
		vpyear.add(new HTML("<h1 style='text-align: center;Font-weight: BOLD;Font-size: 20px;color: grey;'>"
		+constants.Search()+"</h1>"));
		vpyear.add(lbyear);
		vpyear.add(txyear1);
		vpyear.add(lba);
		vpyear.add(txyear2);
		vpyear.add(btsearch);
		vpyear.setSpacing(5);
		final VerticalPanel vpchartoccyear=new VerticalPanel();
		//vpchartoccyear.setHeaderVisible(false);
		vpchartoccyear.setWidth("100%");
		final HorizontalPanel hloccyear = new HorizontalPanel();
		hloccyear.add(vpyear);
		hloccyear.add(vpchartoccyear);
		AbsolutePanel h1 = new AbsolutePanel();
		ContentPanel h = new ContentPanel();
		h.setBodyStyle("backgroundColor:#bac6d2");
		h.setHeaderVisible(false);
		h.add(vchart);
		h1.add(new HTML(
				"<h1 style='text-align: center;Font-weight: BOLD;Font-size: 20px;color: grey;'>Graphics</h1>"));
		HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
		VerticalPanel vbt = new VerticalPanel();
		final VerticalPanel tab1 = new VerticalPanel();
		final VerticalPanel tab2 = new VerticalPanel();
		final HorizontalPanel hchart = new HorizontalPanel();
		hchart.setSpacing(3);
		VerticalLayoutContainer vcontentchart = new VerticalLayoutContainer();
		hchart.add(tab1);
		hchart.add(tab2);
		vbt.add(btOcc);
		vbt.add(btspecies);
		vbt.add(btTspecies);
		vbt.add(btMspecies);
		vcontentchart.add(hchart,
				new VerticalLayoutData(1, 405, new Margins(0)));
		vcontentchart.setScrollMode(ScrollMode.AUTO);
		// Create a CellTable for species by kingdom
		final CellTable<Taxonomy> table = new CellTable<Taxonomy>();
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		TextColumn<Taxonomy> kingdom = new TextColumn<Taxonomy>() {
			@Override
			public String getValue(Taxonomy obj) {
				return obj.getKingdom();
			}
		};
		table.addColumn(kingdom, "Kingdom");
		txService.getKingdom(new AsyncCallback<List<Taxonomy>>() {
			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onSuccess(List<Taxonomy> result) {
				for (int i = 0; i < result.size(); i++)
					table.setRowCount(result.size(), true);
				table.setRowData(0, result);
			}

		});
		final SingleSelectionModel<Taxonomy> selectionModel = new SingleSelectionModel<Taxonomy>();
		table.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Taxonomy selected = selectionModel.getSelectedObject();
						if (selected != null) {
							String kingdom = selected.getKingdom();
							final Chart chart = new Chart()
									.setType(Series.Type.PIE)
									.setChartTitleText(
											constants
													.SpeciesAccordingIUCNstatus()
													+ ": " + kingdom)
									.setPlotBackgroundColor((String) null)
									.setPlotBorderWidth(null)
									.setPlotShadow(false)
									.setPiePlotOptions(
											new PiePlotOptions()
													.setAllowPointSelect(true)
													.setCursor(
															PlotOptions.Cursor.POINTER)
													.setPieDataLabels(
															new PieDataLabels()
																	.setConnectorColor(
																			"#000000")
																	.setEnabled(
																			true)
																	.setColor(
																			"#000000")
																	.setFormatter(
																			new DataLabelsFormatter() {
																				public String format(
																						DataLabelsData dataLabelsData) {
																					return "<b>"
																							+ dataLabelsData
																									.getPointName()
																							+ "</b>: "
																							+ dataLabelsData
																									.getYAsLong();
																				}
																			})))
									.setLegend(
											new Legend()
													.setLayout(
															Legend.Layout.VERTICAL)
													.setAlign(
															Legend.Align.RIGHT)
													.setVerticalAlign(
															Legend.VerticalAlign.TOP)
													.setX(-100)
													.setY(100)
													.setFloating(true)
													.setBorderWidth(1)
													.setBackgroundColor(
															"#FFFFFF")
													.setShadow(true))
									.setToolTip(
											new ToolTip()
													.setFormatter(new ToolTipFormatter() {
														public String format(
																ToolTipData toolTipData) {
															return "<b>"
																	+ toolTipData
																			.getPointName()
																	+ "</b>: "
																	+ toolTipData
																			.getYAsLong();
														}
													}));

							graphicService
									.getCountSpeciesGpByKingdomGpByIUCN_Status(
											kingdom,
											new AsyncCallback<List<Taxonomy>>() {
												@Override
												public void onFailure(
														Throwable arg0) {
												}

												@Override
												public void onSuccess(
														List<Taxonomy> rs) {
													Point[] pt = new Point[rs
															.size()];
													for (int i = 0; i < rs
															.size(); i++)
														pt[i] = new Point(rs
																.get(i)
																.getIucn(), rs
																.get(i)
																.getCount());
													chart.addSeries(chart
															.createSeries()
															.setName(
																	constants
																			.lbl_species())
															.setPoints(pt));

												}

											});
							hpPiechart.clear();
							hpPiechart.add(chart);
						}
					}
				});
		// Create a CellTable for terrestrial species by kingdom
		final CellTable<Taxonomy> tablekt = new CellTable<Taxonomy>();
		tablekt.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		TextColumn<Taxonomy> kingdomt = new TextColumn<Taxonomy>() {
			@Override
			public String getValue(Taxonomy obj) {
				return obj.getKingdom();
			}
		};
		tablekt.addColumn(kingdomt, "Kingdom");
		txService.getKingdomT(new AsyncCallback<List<Taxonomy>>() {
			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onSuccess(List<Taxonomy> result) {
				for (int i = 0; i < result.size(); i++)
					tablekt.setRowCount(result.size(), true);
				tablekt.setRowData(0, result);
			}

		});
		final SingleSelectionModel<Taxonomy> selectionModelT = new SingleSelectionModel<Taxonomy>();
		tablekt.setSelectionModel(selectionModelT);
		selectionModelT
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Taxonomy selected = selectionModelT.getSelectedObject();
						if (selected != null) {
							String kingdom = selected.getKingdom();
							final Chart chart = new Chart()
									.setType(Series.Type.PIE)
									.setChartTitleText(
											constants
													.TerrestrialSpeciesAccordingIUCNStatus()
													+ ": " + kingdom)
									.setPlotBackgroundColor((String) null)
									.setPlotBorderWidth(null)
									.setPlotShadow(false)
									.setPiePlotOptions(
											new PiePlotOptions()
													.setAllowPointSelect(true)
													.setCursor(
															PlotOptions.Cursor.POINTER)
													.setPieDataLabels(
															new PieDataLabels()
																	.setConnectorColor(
																			"#000000")
																	.setEnabled(
																			true)
																	.setColor(
																			"#000000")
																	.setFormatter(
																			new DataLabelsFormatter() {
																				public String format(
																						DataLabelsData dataLabelsData) {
																					return "<b>"
																							+ dataLabelsData
																									.getPointName()
																							+ "</b>: "
																							+ dataLabelsData
																									.getYAsLong();
																				}
																			})))
									.setLegend(
											new Legend()
													.setLayout(
															Legend.Layout.VERTICAL)
													.setAlign(
															Legend.Align.RIGHT)
													.setVerticalAlign(
															Legend.VerticalAlign.TOP)
													.setX(-100)
													.setY(100)
													.setFloating(true)
													.setBorderWidth(1)
													.setBackgroundColor(
															"#FFFFFF")
													.setShadow(true))
									.setToolTip(
											new ToolTip()
													.setFormatter(new ToolTipFormatter() {
														public String format(
																ToolTipData toolTipData) {
															return "<b>"
																	+ toolTipData
																			.getPointName()
																	+ "</b>: "
																	+ toolTipData
																			.getYAsLong();
														}
													}));

							graphicService
									.getCountSpeciesTerrestreGpByKingdomGpByIUCN_Status(
											kingdom,
											new AsyncCallback<List<Taxonomy>>() {
												@Override
												public void onFailure(
														Throwable arg0) {
												}

												@Override
												public void onSuccess(
														List<Taxonomy> rs) {
													Point[] pt = new Point[rs
															.size()];
													for (int i = 0; i < rs
															.size(); i++)
														pt[i] = new Point(rs
																.get(i)
																.getIucn(), rs
																.get(i)
																.getCount());
													chart.addSeries(chart
															.createSeries()
															.setName(
																	constants
																			.lbl_species())
															.setPoints(pt));
												}
											});
							hpPiechart.clear();
							hpPiechart.add(chart);
						}
					}
				});
		// Create a CellTable for marine species by kingdom
		final CellTable<Taxonomy> tableM = new CellTable<Taxonomy>();
		tableM.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		TextColumn<Taxonomy> kingdomM = new TextColumn<Taxonomy>() {
			@Override
			public String getValue(Taxonomy obj) {
				return obj.getKingdom();
			}
		};
		tableM.addColumn(kingdomM, "Kingdom");
		txService.getKingdomM(new AsyncCallback<List<Taxonomy>>() {
			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onSuccess(List<Taxonomy> result) {
				for (int i = 0; i < result.size(); i++)
					tableM.setRowCount(result.size(), true);
				tableM.setRowData(0, result);
			}

		});
		final SingleSelectionModel<Taxonomy> selectionModelM = new SingleSelectionModel<Taxonomy>();
		tableM.setSelectionModel(selectionModelM);
		selectionModelM
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Taxonomy selected = selectionModelM.getSelectedObject();
						if (selected != null) {
							String kingdom = selected.getKingdom();
							final Chart chart = new Chart()
									.setType(Series.Type.PIE)
									.setChartTitleText(
											constants
													.MarineSpeciesAccordingIUCNStatus()
													+ ": " + kingdom)
									.setPlotBackgroundColor((String) null)
									.setPlotBorderWidth(null)
									.setPlotShadow(false)
									.setPiePlotOptions(
											new PiePlotOptions()
													.setAllowPointSelect(true)
													.setCursor(
															PlotOptions.Cursor.POINTER)
													.setPieDataLabels(
															new PieDataLabels()
																	.setConnectorColor(
																			"#000000")
																	.setEnabled(
																			true)
																	.setColor(
																			"#000000")
																	.setFormatter(
																			new DataLabelsFormatter() {
																				public String format(
																						DataLabelsData dataLabelsData) {
																					return "<b>"
																							+ dataLabelsData
																									.getPointName()
																							+ "</b>: "
																							+ dataLabelsData
																									.getYAsLong();
																				}
																			})))
									.setLegend(
											new Legend()
													.setLayout(
															Legend.Layout.VERTICAL)
													.setAlign(
															Legend.Align.RIGHT)
													.setVerticalAlign(
															Legend.VerticalAlign.TOP)
													.setX(-100)
													.setY(100)
													.setFloating(true)
													.setBorderWidth(1)
													.setBackgroundColor(
															"#FFFFFF")
													.setShadow(true))
									.setToolTip(
											new ToolTip()
													.setFormatter(new ToolTipFormatter() {
														public String format(
																ToolTipData toolTipData) {
															return "<b>"
																	+ toolTipData
																			.getPointName()
																	+ "</b>: "
																	+ toolTipData
																			.getYAsLong();
														}
													}));

							graphicService
									.getCountMSpeciesGpByKingdomAndIUCN_cat(
											kingdom,
											new AsyncCallback<List<Taxonomy>>() {
												@Override
												public void onFailure(
														Throwable arg0) {
												}

												@Override
												public void onSuccess(
														List<Taxonomy> rs) {
													Point[] pt = new Point[rs
															.size()];
													for (int i = 0; i < rs
															.size(); i++)
														pt[i] = new Point(rs
																.get(i)
																.getIucn(), rs
																.get(i)
																.getCount());
													chart.addSeries(chart
															.createSeries()
															.setName(
																	constants
																			.lbl_species())
															.setPoints(pt));
												}
											});
							hpPiechart.clear();
							hpPiechart.add(chart);
						}
					}
				});

		btOcc.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hchart.clear();
				txyear1.setText("");
				txyear2.setText("");
				vpchartoccyear.clear();
				vpchartoccyear.add(createChart());
				VerticalLayoutContainer vpocc = new VerticalLayoutContainer();
				vpocc.add(hloccyear, new VerticalLayoutData(1,
						405, new Margins(10)));
				vpocc.add(createOccColumnChart(), new VerticalLayoutData(1, 405,
						new Margins(10)));
				vpocc.setScrollMode(ScrollMode.AUTO);
				hchart.add(vpocc);
			}
		});
		btsearch.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String year1=txyear1.getText();
				String year2=txyear2.getText();
				final Chart chart = new Chart()
				.setType(Series.Type.AREA)
				.setChartTitleText(constants.OccurrencePerYear())
				.setAreaPlotOptions(
						new AreaPlotOptions().setMarker(new Marker()
								.setEnabled(false)
								.setSymbol(Marker.Symbol.CIRCLE).setRadius(2)
								.setHoverState(new Marker().setEnabled(true)))

				).setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return toolTipData.getSeriesName() +constants.lbl_count()+
								+ toolTipData.getYAsLong()
								+ "</b><br/>"+constants.Year()
								+ toolTipData.getXAsLong();
					}
				}));

		graphicService.getOccPerYearBetween2date(year1, year2,new AsyncCallback<List<Occurrence>>() {
			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Occurrence> rs) {
				String s[] = new String[rs.size()];
				Number[] c = new Number[rs.size()];
				Number[] ca = new Number[rs.size()];
				for (int j = 0; j < rs.size(); j++)
					s[j] = rs.get(j).getYear();
				for (int i = 0; i < rs.size(); i++)
					c[i] = rs.get(i).getCount();
				for (int i = 0; i < rs.size(); i++)
					ca[i] = rs.get(i).getCountreviewed();
				chart.getXAxis()
						.setCategories(s)
						.setLabels(
								new XAxisLabels()
										.setFormatter(new AxisLabelsFormatter() {
											public String format(
													AxisLabelsData axisLabelsData) {
												// clean, unformatted number for
												// year
												return String.valueOf(axisLabelsData
														.getValueAsLong());
											}
										}));

				chart.getYAxis()
						.setAxisTitleText(constants.lbl_count())
						.setLabels(
								new YAxisLabels()
										.setFormatter(new AxisLabelsFormatter() {
											public String format(
													AxisLabelsData axisLabelsData) {
												return axisLabelsData
														.getValueAsLong()
														/ 1000 + "k";
											}
										}));

				chart.addSeries(chart.createSeries()
						.setName(constants.alloccurrence()).setPoints(c));
				chart.addSeries(chart.createSeries()
						.setName(constants.allreliable()).setPoints(ca));
			}
		});
		vpchartoccyear.clear();
		vpchartoccyear.add(chart);
			}
		});
		btspecies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hchart.clear();
				hchart.add(tab1);
				hchart.add(tab2);
				tab1.clear();
				tab2.clear();
				hp.clear();
				hpkingdom.clear();
				hpPiechart.clear();
				tab1.add(createColumnChart());
				hpkingdom.add(btAllspecies);
				hpkingdom.add(table);
				hpPiechart.add(createPieChart());
				hp.setSpacing(5);
				hp.add(hpkingdom);
				hp.add(hpPiechart);
				tab2.add(hp);
			}
		});
		tab1.add(createColumnChart());
		hpkingdom.setSpacing(3);
		hpkingdom.add(btAllspecies);
		hpkingdom.add(table);
		hpPiechart.add(createPieChart());
		hp.setSpacing(5);
		hp.add(hpkingdom);
		hp.add(hpPiechart);
		tab2.add(hp);
		btAllspecies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hpPiechart.clear();
				hpPiechart.add(createPieChart());
			}
		});
		btAllTspecies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hpPiechart.clear();
				hpPiechart.add(createPieChartTspecies());
			}
		});
		btAllMspecies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hpPiechart.clear();
				hpPiechart.add(createPieChartMspecies());
			}
		});
		btTspecies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hchart.clear();
				hchart.add(tab1);
				hchart.add(tab2);
				tab1.clear();
				tab2.clear();
				hp.clear();
				hpkingdom.clear();
				hpPiechart.clear();
				tab1.add(createColumnChartTspecies());
				hpkingdom.add(btAllTspecies);
				hpkingdom.add(tablekt);
				hpPiechart.add(createPieChartTspecies());
				hp.setSpacing(5);
				hp.add(hpkingdom);
				hp.add(hpPiechart);
				tab2.add(hp);
			}
		});
		btMspecies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hchart.clear();
				hchart.add(tab1);
				hchart.add(tab2);
				hp.clear();
				tab1.clear();
				tab2.clear();
				hpkingdom.clear();
				hpPiechart.clear();
				tab1.add(createColumnChartMspecies());
				hpkingdom.add(btAllMspecies);
				hpkingdom.add(tableM);
				hpPiechart.add(createPieChartMspecies());
				hp.setSpacing(5);
				hp.add(hpkingdom);
				hp.add(hpPiechart);
				tab2.add(hp);
			}
		});
		hlc.add(vbt, new HorizontalLayoutData(0.1, 100, new Margins(10, 10, 10,
				10)));
		hlc.add(vcontentchart, new HorizontalLayoutData(0.9, 450, new Margins(
				10, 10, 10, 10)));
		vchart.add(hlc,
				new VerticalLayoutData(1, 500, new Margins(0, 0, 50, 0)));
		h1.setStyleName("box1-content content");
		vlc.add(h1, new VerticalLayoutData(1, 50, new Margins(0, 0, 0, 0)));
		vlc.add(h, new VerticalLayoutData(1, 450, new Margins(0)));
		panel = new HorizontalPanel();
		panel.add(vlc);
		initWidget(panel);

	}

	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new GraphicsView(parent);
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

	// Graphics occurrence
	public Chart createOccColumnChart() {
		final Chart chart = new Chart().setType(Series.Type.COLUMN)
				.setMargin(30, 100, 150, 80)
				.setChartTitleText(constants.OccurrencesByRegion())
				.setLegend(new Legend().setEnabled(false))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>"
								+ toolTipData.getXAsString()
								+ "</b><br/>"
								+ NumberFormat.getFormat("0.0").format(
										toolTipData.getYAsLong()) + ""
								+ constants.Species();
					}
				}));
		graphicService
				.getOccurrenceByRegion(new AsyncCallback<List<GraphicModel>>() {
					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<GraphicModel> rs) {
						String s[] = new String[rs.size()];
						Number[] c = new Number[rs.size()];
						for (int j = 0; j < rs.size(); j++)
							s[j] = rs.get(j).getNom_region();
						for (int i = 0; i < rs.size(); i++)
							c[i] = rs.get(i).getCount();
						chart.getXAxis()
								.setCategories(s)
								.setLabels(
										new XAxisLabels()
												.setRotation(-45)
												.setAlign(Labels.Align.RIGHT)
												.setStyle(
														new Style()
																.setFont("normal 13px Verdana, sans-serif")));
						chart.getYAxis().setAxisTitleText("Count").setMin(0);

						chart.addSeries(chart
								.createSeries()
								.setName(constants.lbl_count())
								.setPoints(c)
								.setPlotOptions(
										new ColumnPlotOptions()
												.setDataLabels(new DataLabels()
														.setEnabled(true)
														.setRotation(-90)
														.setColor("#FFFFFF")
														.setAlign(
																Labels.Align.RIGHT)
														.setX(-3)
														.setY(10)
														.setFormatter(
																new DataLabelsFormatter() {
																	public String format(
																			DataLabelsData dataLabelsData) {
																		return NumberFormat
																				.getFormat(
																						"0.0")
																				.format(dataLabelsData
																						.getYAsLong());
																	}
																})
														.setStyle(
																new Style()
																		.setFont("normal 13px Verdana, sans-serif")))));
					}
				});
		return chart;
	}

	public Chart createChart() {
		final Chart chart = new Chart()
				.setType(Series.Type.AREA)
				.setChartTitleText(constants.OccurrencePerYear())
				.setAreaPlotOptions(
						new AreaPlotOptions().setMarker(new Marker()
								.setEnabled(false)
								.setSymbol(Marker.Symbol.CIRCLE).setRadius(2)
								.setHoverState(new Marker().setEnabled(true)))

				).setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return toolTipData.getSeriesName() +constants.lbl_count()+
								+ toolTipData.getYAsLong()
								+ "</b><br/>"+constants.Year()
								+ toolTipData.getXAsLong();
					}
				}));

		graphicService.getOccPerYear(new AsyncCallback<List<Occurrence>>() {
			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Occurrence> rs) {
				String s[] = new String[rs.size()];
				Number[] c = new Number[rs.size()];
				Number[] ca = new Number[rs.size()];
				for (int j = 0; j < rs.size(); j++)
					s[j] = rs.get(j).getYear();
				for (int i = 0; i < rs.size(); i++)
					c[i] = rs.get(i).getCount();
				for (int i = 0; i < rs.size(); i++)
					ca[i] = rs.get(i).getCountreviewed();
				chart.getXAxis()
						.setCategories(s)
						.setLabels(
								new XAxisLabels()
										.setFormatter(new AxisLabelsFormatter() {
											public String format(
													AxisLabelsData axisLabelsData) {
												// clean, unformatted number for
												// year
												return String.valueOf(axisLabelsData
														.getValueAsLong());
											}
										}));

				chart.getYAxis()
						.setAxisTitleText(constants.lbl_count())
						.setLabels(
								new YAxisLabels()
										.setFormatter(new AxisLabelsFormatter() {
											public String format(
													AxisLabelsData axisLabelsData) {
												return axisLabelsData
														.getValueAsLong()
														/ 1000 + "k";
											}
										}));
				chart.addSeries(chart.createSeries()
						.setName(constants.alloccurrence()).setPoints(c));
				chart.addSeries(chart.createSeries()
						.setName(constants.allreliable()).setPoints(ca));
			}
		});
		return chart;
	}

	// Graphics All species
	public Chart createColumnChart() {
		final Chart chart = new Chart().setType(Series.Type.COLUMN)
				.setMargin(30, 100, 100, 80)
				.setChartTitleText(constants.StatisticsOfAllSpecies())
				.setLegend(new Legend().setEnabled(false))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>"
								+ toolTipData.getXAsString()
								+ "</b><br/>"
								+ NumberFormat.getFormat("0.0").format(
										toolTipData.getYAsLong()) + ""
								+ constants.Species();
					}
				}));
		graphicService
				.getCountSpeciesGpByKingdom(new AsyncCallback<List<Taxonomy>>() {
					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Taxonomy> rs) {
						String s[] = new String[rs.size()];
						Number[] c = new Number[rs.size()];
						for (int j = 0; j < rs.size(); j++)
							s[j] = rs.get(j).getKingdom();
						for (int i = 0; i < rs.size(); i++)
							c[i] = rs.get(i).getCount();
						chart.getXAxis()
								.setCategories(s)
								.setLabels(
										new XAxisLabels()
												.setRotation(-45)
												.setAlign(Labels.Align.RIGHT)
												.setStyle(
														new Style()
																.setFont("normal 13px Verdana, sans-serif")));
						chart.getYAxis()
								.setAxisTitleText(constants.lbl_count())
								.setMin(0);

						chart.addSeries(chart
								.createSeries()
								.setName(constants.lbl_count())
								.setPoints(c)
								.setPlotOptions(
										new ColumnPlotOptions()
												.setDataLabels(new DataLabels()
														.setEnabled(true)
														.setRotation(-90)
														.setColor("#FFFFFF")
														.setAlign(
																Labels.Align.RIGHT)
														.setX(-3)
														.setY(10)
														.setFormatter(
																new DataLabelsFormatter() {
																	public String format(
																			DataLabelsData dataLabelsData) {
																		return NumberFormat
																				.getFormat(
																						"0.0")
																				.format(dataLabelsData
																						.getYAsLong());
																	}
																})
														.setStyle(
																new Style()
																		.setFont("normal 13px Verdana, sans-serif")))));
					}
				});
		return chart;
	}

	public Chart createPieChart() {

		final Chart chart = new Chart()
				.setType(Series.Type.PIE)
				.setChartTitleText(constants.SpeciesAccordingIUCNstatus())
				.setPlotBackgroundColor((String) null)
				.setPlotBorderWidth(null)
				.setPlotShadow(false)
				.setPiePlotOptions(
						new PiePlotOptions()
								.setAllowPointSelect(true)
								.setCursor(PlotOptions.Cursor.POINTER)
								.setPieDataLabels(
										new PieDataLabels()
												.setConnectorColor("#000000")
												.setEnabled(true)
												.setColor("#000000")
												.setFormatter(
														new DataLabelsFormatter() {
															public String format(
																	DataLabelsData dataLabelsData) {
																return "<b>"
																		+ dataLabelsData
																				.getPointName()
																		+ "</b>: "
																		+ dataLabelsData
																				.getYAsLong();
															}
														})))
				.setLegend(
						new Legend().setLayout(Legend.Layout.VERTICAL)
								.setAlign(Legend.Align.RIGHT)
								.setVerticalAlign(Legend.VerticalAlign.TOP)
								.setX(-100).setY(100).setFloating(true)
								.setBorderWidth(1)
								.setBackgroundColor("#FFFFFF").setShadow(true))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>" + toolTipData.getPointName() + "</b>: "
								+ toolTipData.getYAsLong();
					}
				}));

		graphicService
				.getCountSpeciesGpByIUCN_Status(new AsyncCallback<List<Taxonomy>>() {

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Taxonomy> rs) {
						Point[] pt = new Point[rs.size()];
						for (int i = 0; i < rs.size(); i++)
							pt[i] = new Point(rs.get(i).getIucn(), rs.get(i)
									.getCount());
						chart.addSeries(chart.createSeries()
								.setName(constants.Species()).setPoints(pt));
					}
				});
		return chart;
	}

	// Graphics Terrestrial species
	public Chart createColumnChartTspecies() {
		final Chart chart = new Chart().setType(Series.Type.COLUMN)
				.setMargin(30, 100, 100, 80)
				.setChartTitleText(constants.TerrestrialSpecies())
				.setLegend(new Legend().setEnabled(false))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>"
								+ toolTipData.getXAsString()
								+ "</b><br/>"
								+ NumberFormat.getFormat("0.0").format(
										toolTipData.getYAsLong()) + ""
								+ constants.Species();
					}
				}));
		graphicService
				.getCountTSpeciesGpByKingdom(new AsyncCallback<List<Taxonomy>>() {
					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Taxonomy> rs) {
						String s[] = new String[rs.size()];
						Number[] c = new Number[rs.size()];
						for (int j = 0; j < rs.size(); j++)
							s[j] = rs.get(j).getKingdom();
						for (int i = 0; i < rs.size(); i++)
							c[i] = rs.get(i).getCount();
						chart.getXAxis()
								.setCategories(s)
								.setLabels(
										new XAxisLabels()
												.setRotation(-45)
												.setAlign(Labels.Align.RIGHT)
												.setStyle(
														new Style()
																.setFont("normal 13px Verdana, sans-serif")));
						chart.getYAxis()
								.setAxisTitleText(constants.lbl_count())
								.setMin(0);

						chart.addSeries(chart
								.createSeries()
								.setName(constants.lbl_count())
								.setPoints(c)
								.setPlotOptions(
										new ColumnPlotOptions()
												.setDataLabels(new DataLabels()
														.setEnabled(true)
														.setRotation(-90)
														.setColor("#FFFFFF")
														.setAlign(
																Labels.Align.RIGHT)
														.setX(-3)
														.setY(10)
														.setFormatter(
																new DataLabelsFormatter() {
																	public String format(
																			DataLabelsData dataLabelsData) {
																		return NumberFormat
																				.getFormat(
																						"0.0")
																				.format(dataLabelsData
																						.getYAsLong());
																	}
																})
														.setStyle(
																new Style()
																		.setFont("normal 13px Verdana, sans-serif")))));
					}

				});

		return chart;
	}

	public Chart createPieChartTspecies() {
		final Chart chart = new Chart()
				.setType(Series.Type.PIE)
				.setChartTitleText(
						constants.TerrestrialSpeciesAccordingIUCNStatus())
				.setPlotBackgroundColor((String) null)
				.setPlotBorderWidth(null)
				.setPlotShadow(false)
				.setPiePlotOptions(
						new PiePlotOptions()
								.setAllowPointSelect(true)
								.setCursor(PlotOptions.Cursor.POINTER)
								.setPieDataLabels(
										new PieDataLabels()
												.setConnectorColor("#000000")
												.setEnabled(true)
												.setColor("#000000")
												.setFormatter(
														new DataLabelsFormatter() {
															public String format(
																	DataLabelsData dataLabelsData) {
																return "<b>"
																		+ dataLabelsData
																				.getPointName()
																		+ "</b>: "
																		+ dataLabelsData
																				.getYAsLong();
															}
														})))
				.setLegend(
						new Legend().setLayout(Legend.Layout.VERTICAL)
								.setAlign(Legend.Align.RIGHT)
								.setVerticalAlign(Legend.VerticalAlign.TOP)
								.setX(-100).setY(100).setFloating(true)
								.setBorderWidth(1)
								.setBackgroundColor("#FFFFFF").setShadow(true))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>" + toolTipData.getPointName() + "</b>: "
								+ toolTipData.getYAsLong();
					}
				}));

		graphicService
				.getCountSpeciesTerrestreGpByIUCN_Status(new AsyncCallback<List<Taxonomy>>() {

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(List<Taxonomy> rs) {
						Point[] pt = new Point[rs.size()];
						for (int i = 0; i < rs.size(); i++)
							pt[i] = new Point(rs.get(i).getIucn(), rs.get(i)
									.getCount());
						chart.addSeries(chart.createSeries()
								.setName(constants.lbl_species()).setPoints(pt));
					}

				});
		return chart;
	}

	// Graphic Marine species
	public Chart createColumnChartMspecies() {
		final Chart chart = new Chart().setType(Series.Type.COLUMN)
				.setMargin(30, 100, 100, 80)
				.setChartTitleText(constants.MarineSpecies())
				.setLegend(new Legend().setEnabled(false))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>"
								+ toolTipData.getXAsString()
								+ "</b><br/>"
								+ NumberFormat.getFormat("0.0").format(
										toolTipData.getYAsLong()) + " species";
					}
				}));
		graphicService
				.getCountMSpeciesGpByKingdom(new AsyncCallback<List<Taxonomy>>() {
					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Taxonomy> rs) {
						String s[] = new String[rs.size()];
						Number[] c = new Number[rs.size()];
						for (int j = 0; j < rs.size(); j++)
							s[j] = rs.get(j).getKingdom();
						for (int i = 0; i < rs.size(); i++)
							c[i] = rs.get(i).getCount();
						chart.getXAxis()
								.setCategories(s)
								.setLabels(
										new XAxisLabels()
												.setRotation(-45)
												.setAlign(Labels.Align.RIGHT)
												.setStyle(
														new Style()
																.setFont("normal 13px Verdana, sans-serif")));
						chart.getYAxis()
								.setAxisTitleText(constants.lbl_count())
								.setMin(0);

						chart.addSeries(chart
								.createSeries()
								.setName(constants.lbl_count())
								.setPoints(c)
								.setPlotOptions(
										new ColumnPlotOptions()
												.setDataLabels(new DataLabels()
														.setEnabled(true)
														.setRotation(-90)
														.setColor("#FFFFFF")
														.setAlign(
																Labels.Align.RIGHT)
														.setX(-3)
														.setY(10)
														.setFormatter(
																new DataLabelsFormatter() {
																	public String format(
																			DataLabelsData dataLabelsData) {
																		return NumberFormat
																				.getFormat(
																						"0.0")
																				.format(dataLabelsData
																						.getYAsLong());
																	}
																})
														.setStyle(
																new Style()
																		.setFont("normal 13px Verdana, sans-serif")))));
					}
				});
		return chart;
	}

	public Chart createPieChartMspecies() {
		final Chart chart = new Chart()
				.setType(Series.Type.PIE)
				.setChartTitleText(constants.MarineSpeciesAccordingIUCNStatus())
				.setPlotBackgroundColor((String) null)
				.setPlotBorderWidth(null)
				.setPlotShadow(false)
				.setPiePlotOptions(
						new PiePlotOptions()
								.setAllowPointSelect(true)
								.setCursor(PlotOptions.Cursor.POINTER)
								.setPieDataLabels(
										new PieDataLabels()
												.setConnectorColor("#000000")
												.setEnabled(true)
												.setColor("#000000")
												.setFormatter(
														new DataLabelsFormatter() {
															public String format(
																	DataLabelsData dataLabelsData) {
																return "<b>"
																		+ dataLabelsData
																				.getPointName()
																		+ "</b>: "
																		+ dataLabelsData
																				.getYAsLong();
															}
														})))
				.setLegend(
						new Legend().setLayout(Legend.Layout.VERTICAL)
								.setAlign(Legend.Align.RIGHT)
								.setVerticalAlign(Legend.VerticalAlign.TOP)
								.setX(-100).setY(100).setFloating(true)
								.setBorderWidth(1)
								.setBackgroundColor("#FFFFFF").setShadow(true))
				.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					public String format(ToolTipData toolTipData) {
						return "<b>" + toolTipData.getPointName() + "</b>: "
								+ toolTipData.getYAsLong();
					}
				}));

		graphicService
				.getCountSpeciesMarinGpByIUCN_Status(new AsyncCallback<List<Taxonomy>>() {
					@Override
					public void onFailure(Throwable arg0) {
					}

					@Override
					public void onSuccess(List<Taxonomy> rs) {
						Point[] pt = new Point[rs.size()];
						for (int i = 0; i < rs.size(); i++)
							pt[i] = new Point(rs.get(i).getIucn(), rs.get(i)
									.getCount());
						chart.addSeries(chart.createSeries()
								.setName(constants.lbl_species()).setPoints(pt));
					}
				});
		return chart;
	}
}