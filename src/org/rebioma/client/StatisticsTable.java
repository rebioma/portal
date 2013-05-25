package org.rebioma.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A table contains users information that query from the server.
 * 
 * @author Tri
 * 
 */
public class StatisticsTable extends Composite implements ResizeHandler,
    PageListener<StatisticModel> {

  /**
   * OnChecked is fire when a check box in the table is changed value where by
   * clicking on a check box or by clicking on All or None action
   * 
   * @author Tri
   * 
   */
  public interface CheckedClickListener {
    void onChecked(String email, Integer id, boolean checked, int row);
  }

  /**
   * Table hears to statistics table col mapping.
   */
  public enum Criteria {
    TITLE_TAB(0, null, "", "stat-title", null), PRIVATE_DATA(1, constants
        .privateData(), constants.privateData(), "private-data", "privateData"), PUBLIC_DATA(
        2, constants.publicData(), constants.publicData(), "public-data",
        "publicData"), RELIABLE(3, constants.reliable(), constants.reliable(),
        "reliable", "reliable"), AWAITING(4, constants.awaiting(),
        constants.awaiting(), "awaiting", "awaiting"), QUESTIONABLE(4, constants.questionable(),
                constants.questionable(), "questionable", "questionable"), INVALIDATED(4, constants.invalidated(),
                        constants.invalidated(), "invalidated", "invalidated"), TOTAL(4, constants.all(),
                                constants.all(), "total", "all");

    private int index;
    private String instruction;
    private String header;
    private String style;
    private String param;

    Criteria(int index, String instruction, String header, String style,
        String param) {
      this.index = index;
      this.instruction = instruction;
      this.header = header;
      this.style = style;
      this.param = param;
    }

    public String getHeader() {
      return header;
    }

    public int getIndex() {
      return index;
    }

    public String getInstruction() {
      return instruction;
    }

    public String getParam() {
      return param;
    }

    public String getStyle() {
      return style;
    }
  }

  /**
   * This class wires with
   * {@link DataSwitch#fetchUser(String, UserQuery, AsyncCallback)} call to
   * request users from {@link UserQuery}
   * 
   * @author Tri
   * 
   */
  private class StatDataPager extends DataPager<StatisticModel> {
    public StatDataPager(int pageSize) {
      super(pageSize, new Query<StatisticModel>());
    }

   

    protected void requestData(final PageCallback<StatisticModel> cb) {
    	/*List<StatisticModel> data = new ArrayList<StatisticModel>();
    	for(int i=0; i<21;i++){
    		StatisticModel model = new StatisticModel("nom personne", "236", "456");
    		data.add(model);
    	}*/
    	/*totalDataCount = data.size();
    	cb.onPageReady(data);*/
      
     
    }

	

  }

  private static final AppConstants constants = ApplicationView.getConstants();
  private final static String[] resultsHeader = {
      Criteria.TITLE_TAB.getHeader(), Criteria.PRIVATE_DATA.getHeader(),
      Criteria.PUBLIC_DATA.getHeader(), 
      Criteria.RELIABLE.getHeader(),
      Criteria.AWAITING.getHeader(),
      Criteria.QUESTIONABLE.getHeader(),
      Criteria.INVALIDATED.getHeader(),
      Criteria.TOTAL.getHeader()};
  private final static String[] columnStyles = { Criteria.TITLE_TAB.getStyle(),
      Criteria.PRIVATE_DATA.getStyle(), Criteria.PUBLIC_DATA.getStyle(),
     Criteria.RELIABLE.getStyle(), 
      Criteria.AWAITING.getStyle(),
      Criteria.QUESTIONABLE.getStyle(), 
      Criteria.INVALIDATED.getStyle(),
      Criteria.TOTAL.getStyle()};
  private final StatDataPager dataPager = new StatDataPager(ROW_COUNT);
  private final PagerWidget<StatisticModel> statPager = new PagerWidget<StatisticModel>(dataPager) {
  };
 
  private final VerticalPanel mainVp = new VerticalPanel();
  private final ScrollPanel mainSp = new ScrollPanel();
  private final ActionTool actionTool = new ActionTool(true) {

	@Override
	protected void setCheckedAll(boolean checked) {
		// TODO Auto-generated method stub
		
	}

   

  };
  public static final int ROW_COUNT = 10;
 private final TableWidget statTable;
  private final List<CheckedClickListener> checkedListeners = new ArrayList<CheckedClickListener>();
  List<StatisticModel> currentStats = null;
  
  
  

  /**
   * Call this(true).
   */
  public StatisticsTable() {
    this(true);
  }

  /**
   * Initialize UsersTable with the action list box if showedActionBar is true.
   * 
   * @param showedActionBar true to show action list box.
   */
  public StatisticsTable(boolean showedActionBar) {
    statTable = new TableWidget(resultsHeader, columnStyles, ROW_COUNT);
    HorizontalPanel actionHp = new HorizontalPanel();
    actionHp.setWidth("100%");
    if (showedActionBar) {
      actionHp.add(actionTool);
      actionHp.setCellHorizontalAlignment(actionTool,
          HasHorizontalAlignment.ALIGN_LEFT);
      actionHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      actionTool.setDefaultSelection(0);
    }
    actionHp.add(statPager);
    actionHp.setCellHorizontalAlignment(statPager,
        HasHorizontalAlignment.ALIGN_RIGHT);
    mainSp.setWidget(mainVp);
    mainVp.add(actionHp);
    mainVp.add(statTable);
    initWidget(mainSp);
    statPager.addPageListener(this);
    Window.addResizeHandler(this);
    Scheduler.get().scheduleDeferred(new ScheduledCommand(){
      public void execute() {
        resize(Window.getClientWidth(), Window.getClientHeight());

      }

    });
  }

  public void addAction(String action, Command actionCommand) {
    actionTool.addAction(action, actionCommand);
  }

  public void addCheckedListener(CheckedClickListener listener) {
    checkedListeners.add(listener);
  }

  public void addWidget(Widget w) {
    actionTool.addWidget(w);
  }

  public int getPageSize() {
    return dataPager.getPageSize();
  }

  
  public StatisticModel getUser(int row) {
    return currentStats.get(row);
  }

  

  public void onResize(ResizeEvent event) {
    resize(event.getWidth(), event.getHeight());
  }

  public void removeCheckedListener(CheckedClickListener listener) {
    checkedListeners.remove(listener);
  }

  public void resetTable() {
    statPager.init(1);
  }

 
  protected void resize(int width, int height) {
    height = height - (mainSp.getAbsoluteTop());
    if (height > mainVp.getOffsetHeight()) {
      height = mainVp.getOffsetHeight();
    }
    if (height < 0) {
      height = -1;
    }
    mainSp.setPixelSize(statTable.getOffsetWidth() + 20, height);
  }

  /*private Widget[][] constructTableContent(List<StatisticModel> statsList) {
    Widget widgetContent[][] = new Widget[statsList.size()][];
    int i = 0;
    for (StatisticModel statistic : statsList) {
      Widget rowsWidget[] = new Widget[resultsHeader.length];
      
      final StatisticModel currentStat = statistic;
      final int row = i;
      rowsWidget[Criteria.TITLE_TAB.getIndex()] = new Label(currentStat.getTitle());
      rowsWidget[Criteria.PRIVATE_DATA.getIndex()] = new Label(currentStat.getPrivateData());
      rowsWidget[Criteria.PUBLIC_DATA.getIndex()] = new Label(currentStat.getPrivateData());
    // rowsWidget[Criteria.EMAIL.getIndex()] = new Label(user.getEmail());    
      rowsWidget[Criteria.RELIABLE.getIndex()] = new Label(currentStat.getPublicData());
      widgetContent[i] = rowsWidget;
      i++;
    }
    return widgetContent;
  }*/

 

 
@Override
public void onPageLoaded(List<StatisticModel> data, int pageNumber) {
	/*Widget rowsWidget[][] = constructTableContent(data);
    int start = (statPager.getCurrentPageNumber() - 1)
        * statPager.getPageSize();
    statTable.showRecord(statPager.getPageSize(), start, rowsWidget);
    currentStats = data;
    resize(Window.getClientWidth(), Window.getClientHeight());*/
	
}
}
