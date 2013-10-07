package org.rebioma.client;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.gxt.treegrid.StatisticsPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StatisticsTabView extends ComponentView implements ClickHandler, ChangeHandler, PageListener<StatisticModel>{
	
	
	 /*public static final String HEADER_CSS_STYLES[] = new String[] {
	      "stat-title", "private-data", "public-data", "reliable", "awaiting",
	      "questionable", "invalidated", "total" };*/
	/* public static final String HEADER_CSS_STYLES[] = new String[] {
	      "accepted-species", "id", "id", "id", "owner-email",
	      "id", "id", "id" };
	 
	 public static final String HEADER_TITLE[] = new String[] {
	      "bilbil ", constants.privateData(),  constants.publicData(),constants.reliable(), constants.awaiting(),
	      constants.questionable(), constants.invalidated(), constants.all()};*/
	 
	
	
	public final static String PER_MANAGER_USER="stats per user";
	public final static String PER_PROVIDER_INSTITUT="stats per institut";
	public final static String PER_COLLECTION_CODE="stats per collection code";
	 private static final String DEFAULT_STYLE = "OccurrenceView-ListView";
	
	public static final int ROW_COUNT = 10;
	

	
	
	private final VerticalPanel mainVp;
	 
	
	
	 private final Widget statisticsPanel;
	
	private StatisticsTabView(){
		this(null);
	}
	
	  
	private StatisticsTabView(View parent){
		super(parent, false);
		
		
		mainVp = new VerticalPanel();
		
		mainVp.setSpacing(0);
		
		statisticsPanel = new StatisticsPanel().statisticsPanel("title");
		mainVp.setWidth("100%");
		statisticsPanel.setWidth(Window.getClientWidth()-20 +"px");
		mainVp.add(statisticsPanel);
		mainVp.setStyleName(DEFAULT_STYLE);
		initWidget(mainVp);
		resize(Window.getClientWidth(), (Window.getClientHeight() - 115));
		
		
	}
	
	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new StatisticsTabView(parent);
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
	public void onStateChanged(ViewState state) {
		
		
	}
	@Override
	public void onChange(ChangeEvent arg0) {
		
		
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		
		
		
	}

	@Override
	protected void resetToDefaultState() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onResize(ResizeEvent event) {
		
		resize(event.getWidth(), event.getHeight());
	}
	
	@Override
	protected void resize(final int width, int height) {
		int w = width - 20;
		int h = height - mainVp.getAbsoluteTop() - 5;
//		mainVp.setWidth(w + "px");
//		mainVp.setHeight(h + "px");
		statisticsPanel.setWidth(w+ "px");
		statisticsPanel.setHeight(h + "px");
		//infoPanel.setWidth(w);
		Window.enableScrolling(mainVp.getOffsetWidth() - 10 > width);

	}
	
	public String historyToken() {
		// TODO Auto-generated method stub
		return super.historyToken();
	}

	


	@Override
	public void onPageLoaded(List<StatisticModel> data, int pageNumber) {
		
		
	}
	
	
	

}
