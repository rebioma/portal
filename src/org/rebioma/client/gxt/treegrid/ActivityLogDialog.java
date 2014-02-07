package org.rebioma.client.gxt.treegrid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.ListView;
import org.rebioma.client.bean.Activity;
import org.rebioma.client.bean.Activity.ActivityProperties;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.ActivityService;
import org.rebioma.client.services.ActivityServiceAsync;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
//			if(yFormat.format(object).equals("2013"))
//		setClosable(false);
//		setHeaderVisible(false);
//				Mask.mask((XElement) panel.getElement(), "Loading...");
//				Mask.unmask((XElement) panel.getElement());
public class ActivityLogDialog extends Dialog implements ClickHandler, MouseOverHandler, MouseOutHandler {

	private final ActivityServiceAsync activityService = GWT
			.create(ActivityService.class);

	private final Grid<Activity> grid;
	
	private final RpcProxy<PagingLoadConfig, PagingLoadResult<Activity>> proxy;
	private PagingLoadConfig conf;
	private HorizontalPanel hp;
	private VerticalPanel panel;
	private HTML allLink;
	private HTML commentLink;
	private HTML reviewLink;
	private HTML cancelLink;
	private HTML searchLink;
	private HTML detailHtml;
	private String currentLog = "review";
	private String style = "style='padding-right:10px'";
	private PagingToolBar toolBar;
	private AppConstants constants;
	
	public interface HtmlTemplate extends XTemplates {
		@XTemplate("<img src='{src}'/>")
		SafeHtml render(String src);
		@XTemplate("<div style='white-space: normal; background: none repeat scroll 0% 0% white; border: 1px dashed rgb(208, 208, 208); padding: 0px 5px;' id='d_id'><h3 style='display: block;font-weight: bold;color: #222; font-size:14px'><span style='float: right; font-weight: normal; text-align:right; width: 130px; display: block; clear: none;color:#999'>{date}</span>Action: <span style='font-weight:normal'>{action} </span> <span style='font-style:italic; font-weight: normal; text-align:right; clear: none;color:#999'>({occurrenceCount} occurrences)</span></h3> <b>Comment:</b> {comment}</div>")
		SafeHtml detail(ActivityDetail activity);
	}
	
	private HtmlTemplate htmlTemplate = GWT.create(HtmlTemplate.class);
	
	private final String UP_URL = "images/up.png";
	
	private final String COMMENT_URL = "images/comment.png";
	
	private final String QUEST_URL = "images/quest.gif";
	
	private String sId;
	
	private Activity selectedAcivity;
	
	private AbstractCell<Boolean> reviewedCell = new AbstractCell<Boolean>() {
		 
		@Override
		public void render(Context context, Boolean val, SafeHtmlBuilder sb) {
			String src = ListView.NULL_URL;
			if(val == null)
				src = COMMENT_URL;
			else if((boolean)val)
				src = UP_URL;
			else 
				src = QUEST_URL;
			sb.append(htmlTemplate.render(src));
		}
	};
	
	AbstractCell<Long> nbrFormat = new AbstractCell<Long>() {
		@Override
		public void render(Context context, Long object, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<span " + style + ">" + 
					NumberFormat.getDecimalFormat().format(object) + "</span>");
		}
	};
		
	DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/y");
	DateTimeFormat dateHFormat = DateTimeFormat.getFormat("dd/MM/y HH");
	DateTimeFormat currentYformat = DateTimeFormat.getFormat("d MMM");
	DateTimeFormat yFormat = DateTimeFormat.getFormat("y");
	
	AbstractCell<Date> dateCFormat = new AbstractCell<Date>() {
		@Override
		public void render(Context context, Date object, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<span " + style + ">" + format(object) + "</span>");
        }
	};

	public ActivityLogDialog(AppConstants constants) {
		super();
		this.constants = constants;
		setResizable(false);
		setAllowTextSelection(false);
//		setClosable(false);
		setBodyBorder(false);
		setWidth(650);
		setHeight(400);
		setHideOnButtonClick(true);
		setModal(true);
		cancelLink = new HtmlLink("Cancel action", this, this, this);
		cancelLink.setStyleName("a-link");
		cancelLink.addStyleName("disable");
		searchLink = new HtmlLink("Search", this, this, this);
		searchLink.setStyleName("a-link");
		searchLink.addStyleName("disable");
		getButtonBar().insert(new HTML("<b> | </b>"), 0);
		getButtonBar().insert(cancelLink, 0);
//		getButtonBar().insert(new HTML("<b> | </b>"), 0);
//		getButtonBar().insert(searchLink, 0);
		setHeadingHtml("<h1 style='text-align:center; font-size:15px'>Activity Log</h1>");
//		setHeaderVisible(false);
		sId = ApplicationView.getSessionId();
		ActivityProperties props = GWT
				.create(ActivityProperties.class);

		proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<Activity>>() {
			@Override
			public void load(PagingLoadConfig config, AsyncCallback<PagingLoadResult<Activity>> callback) {
				conf = config;
				activityService.getActivity(config, sId, currentLog, callback);
			}
		};
		ListStore<Activity> store = new ListStore<Activity>(
				props.key());
		PagingLoader<PagingLoadConfig, PagingLoadResult<Activity>> loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<Activity>>(
				proxy);
		loader.setRemoteSort(true);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, Activity, PagingLoadResult<Activity>>(store));
		
		toolBar = new PagingToolBar(50);
		toolBar.addStyleName("text");
		toolBar.getElement().getStyle().setProperty("borderBottom", "white");
		toolBar.bind(loader);
		toolBar.setHeight("29px");

		ColumnConfig<Activity, Date> dateC = new ColumnConfig<Activity, Date>(
				props.date(), 90, "Date");
		dateC.setCell(dateCFormat);
		dateC.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		ColumnConfig<Activity, Boolean> actionC = new ColumnConfig<Activity, Boolean>(
				props.action(), 70, "Action");
		actionC.setCell(reviewedCell);
		actionC.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		ColumnConfig<Activity, String> commentC = new ColumnConfig<Activity, String>(
				props.comment(), 200, "Comment");
		ColumnConfig<Activity, Long> occCountC = new ColumnConfig<Activity, Long>(
				props.occurrenceCount(), 100, "Occurrences");
		occCountC.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		occCountC.setCell(nbrFormat);
		List<ColumnConfig<Activity, ?>> l = new ArrayList<ColumnConfig<Activity, ?>>();
		l.add(commentC);
		l.add(actionC);
		l.add(occCountC);
		l.add(dateC);
		ColumnModel<Activity> cm = new ColumnModel<Activity>(l);
		grid = new Grid<Activity>(store, cm);
		grid.addStyleName("text");
		grid.getElement().getStyle().setProperty("borderLeft", "solid 1px #D0D0D0");
		grid.getElement().getStyle().setProperty("background", "none");
		grid.getView().setAutoExpandColumn(commentC);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(false);
		grid.setBorders(false);
		grid.setAllowTextSelection(false);
		grid.setLoadMask(true);
		grid.setLoader(loader);
		grid.setColumnReordering(false);
		grid.setStateful(true);
		grid.addStyleName("grid");
		grid.getSelectionModel().addSelectionChangedHandler(gridSelection);
		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.getElement().getStyle().setProperty("borderLeft", "solid 1px #D0D0D0");
	    con.add(grid, new VerticalLayoutData(1, 1));
	    panel = new VerticalPanel();
	    hp = new HorizontalPanel();
		hp.getElement().getStyle().setProperty("background", "white");
		hp.getElement().getStyle().setProperty("marginTop", "15px");
	    VerticalPanel vp = new VerticalPanel();
		allLink = new HtmlLink("All", this, this, this);
		allLink.setStyleName("log-all");
		allLink.setWidth("120px");
		vp.add(allLink);
		commentLink = new HtmlLink("Comment", this, this, this);
		commentLink.setStyleName("log-link");
		vp.add(commentLink);
		vp.addStyleName("list");
		reviewLink = new HtmlLink("Reviewing", this, this, this);
		reviewLink.setStyleName("log-link");
		reviewLink.addStyleName("active");
		vp.add(reviewLink);
		hp.add(vp);
		hp.setCellWidth(vp, "200px");
		hp.add(con);
		con.setHeight("286px");
		panel.add(hp);
		toolBar.getElement().getStyle().setProperty("marginTop", "1px");
		panel.add(toolBar);
		panel.setCellVerticalAlignment(hp, HasVerticalAlignment.ALIGN_BOTTOM);
		panel.setCellVerticalAlignment(toolBar, HasVerticalAlignment.ALIGN_TOP);
		panel.setHeight("30px");
		detailHtml = new HtmlLink("", this, this, this);
		panel.add(detailHtml);
		panel.setCellHeight(detailHtml, "50px");
		add(panel);
		load();
	}
	
	protected String format(Date object) {
		String date = dateFormat.format(object);
		if(yFormat.format(object).equals(yFormat.format(new Date())))
			date = currentYformat.format(object);
    	return date;
	}

	SelectionChangedHandler<Activity> gridSelection = new SelectionChangedHandler<Activity>() {
		@Override
		public void onSelectionChanged(SelectionChangedEvent<Activity> event) {
			if (event.getSelection().size() > 0) {
				selectedAcivity = event.getSelection().get(0);
				detailHtml.setHTML(htmlTemplate.detail(new ActivityDetail(selectedAcivity)));
				setLinkEnable(true);
			} else {
				selectedAcivity = null;
				setLinkEnable(false);
			}
		}
	};
	
	private void showDetail(boolean show) {
		if(show)grid.getParent().setHeight(284 - detailHtml.getOffsetHeight() +"px");
		else grid.getParent().setHeight("286px");
	}
	
	private void setLinkEnable(boolean enable) {
		if(enable) {
			removeStyle(cancelLink, "disable");
			removeStyle(searchLink, "disable");
		} else {
			cancelLink.addStyleName("disable");
			searchLink.addStyleName("disable");
		}
		showDetail(enable);
	}

	public void load() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			@Override
			public void execute() {
				grid.getStore().clear();
//				Mask.mask((XElement) panel.getElement(), "Loading...");
				grid.getLoader().load();
				toolBar.forceLayout();
//				Mask.unmask((XElement) panel.getElement());
			}
		});
	}
	
	public void showActivity(int userId, String type) {
		if(type != null){
			setHeadingText(type);
			this.show();
			Scheduler.get().scheduleDeferred(new ScheduledCommand(){
				@Override
				public void execute() {
					grid.getStore().clear();
					Mask.mask(grid.getElement(), "Loading...");
				}
			});
			proxy.load(conf, new AsyncCallback<PagingLoadResult<Activity>>() {
				@Override
				public void onFailure(Throwable caught) {
					hide();
					Window.alert(caught.getLocalizedMessage());
				}
				
				@Override
				public void onSuccess(PagingLoadResult<Activity> result) {
					grid.setLoadMask(false);
					grid.getStore().replaceAll((List<Activity>) result);
					Mask.unmask(grid.getElement());
				}
			});
		}
		
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Object source = event.getSource();
		if(((HTML)source).getStyleName().contains("active"))return;
		if(source == allLink) {
			setStyles(allLink, "m-hover");
		} else if(source == commentLink) {
			setStyles(commentLink, "m-hover");
		} else if(source == reviewLink) {
			setStyles(reviewLink, "m-hover");
		} else if(source == cancelLink) {
			setStyles(cancelLink, "m-hover");
		} else if(source == detailHtml) {
			setStyle(detailHtml, "d-hover");
		} else if(source == searchLink) {
			setStyles(searchLink, "m-hover");
		} else {
			removeStyle(null, "m-hover");
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Object source = event.getSource();
		if(source == allLink) {
			removeStyle(allLink,"m-hover");
		} else if(source == commentLink) {
			removeStyle(commentLink, "m-hover");
		} else if(source == reviewLink) {
			removeStyle(reviewLink, "m-hover");
		} else if(source == cancelLink) {
			removeStyle(cancelLink, "m-hover");
		} else if(source == detailHtml) {
			removeStyle(detailHtml, "d-hover");
		} else if(source == searchLink) {
			removeStyle(searchLink, "m-hover");
		} else {
			removeStyle(null, "m-hover");
		}
	}
	
	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if(source == allLink) {
			currentLog = "all";
			load();
			setStyles(allLink, "active");
		} else if(source == commentLink) {
			currentLog = "comment";
			load();
			setStyles(commentLink, "active");
		} else if(source == reviewLink) {
			currentLog = "review";
			load();
			setStyles(reviewLink, "active");
		} else if(source == cancelLink) {
			WarningMessageBox box = new WarningMessageBox("Confirm", "Are you sure you want to do that?");
	        box.addHideHandler(hideHandler);
	        box.show();
		} else if(source == searchLink) {
		}
	}
	
	private void removeStyle(HTML html, String style) {
		if(html==null) return;
		html.setStyleName(html.getStyleName().replace(style, " ").trim());
	}
	
	
	private void setStyles(HTML html, String style) {
		removeStyle(allLink, style);
		removeStyle(commentLink, style);
		removeStyle(reviewLink, style);
		removeStyle(cancelLink, style);
		removeStyle(searchLink, style);
		if(html==null) return;
		html.addStyleName(style);
	}
	
	private void setStyle(HTML html, String style) {
		removeStyle(html, style);
		if(html==null) return;
		html.addStyleName(style);
	}
	
	private class HtmlLink extends HTML {

		public HtmlLink(String html, ClickHandler clickHandler, MouseOverHandler mouseOverHandler, 
				MouseOutHandler mouseOutHandler) {
			super(html);
			addClickHandler(clickHandler);
			addMouseOverHandler(mouseOverHandler);
			addMouseOutHandler(mouseOutHandler);
		}
		
	}
	
	public class ActivityDetail {
		
		private Long occurrenceCount;
		private String action;
		private String date;
		private String comment;
		
		public Long getOccurrenceCount() {
			return occurrenceCount;
		}
		public void setOccurrenceCount(Long occurrenceCount) {
			this.occurrenceCount = occurrenceCount;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		
		public ActivityDetail(Activity activity) {
			super();
			this.occurrenceCount = activity.getOccurrenceCount();
			this.action = setAction(activity.getAction());
			this.date = dateHFormat.format(activity.getDate()) + "h";
			this.comment = activity.getComment()==null?"":activity.getComment();
		}
		
		public ActivityDetail() {
			super();
		}
		
		private String setAction(Boolean action) {
			if(action==null) return "Comment";
			if(action) return constants.PositivelyReview();
			else return constants.NegativelyReview();
		}
		
	}
	
	final HideHandler hideHandler = new HideHandler() {
		@Override
		public void onHide(HideEvent event) {
			Dialog btn = (Dialog) event.getSource();
			if(btn.getHideButton().getText().trim().equalsIgnoreCase("yes")){
				Mask.mask((XElement) panel.getElement(), "Loading...");
				activityService.removeActivity(sId, selectedAcivity, new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if(result==null) {
							new AlertMessageBox("Error", "Session error").show();
						} else if (result) {
							Info.display("", "Success");
							load();
						} else {
							new AlertMessageBox("Error", "Please try agian.").show();
						}
						Mask.unmask((XElement) panel.getElement());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						new AlertMessageBox("Error", caught.getMessage()).show();
						Mask.unmask((XElement) panel.getElement());
					}
				});
			}
		}
	};

	public class WarningMessageBox extends MessageBox {

		public WarningMessageBox(String title, String message) {
			super(title, message);
			
			setIcon(ICONS.warning());
			setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
		}

	}
	
}
