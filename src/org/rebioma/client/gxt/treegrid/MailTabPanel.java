package org.rebioma.client.gxt.treegrid;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.rebioma.client.services.MailingService;
import org.rebioma.client.services.MailingServiceAsync;

//import com.google.gwt.core.shared.GWT;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent.ParseErrorHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TimeField;
import com.sencha.gxt.widget.core.client.info.Info;

public class MailTabPanel {
	
	private final MailingServiceAsync mailingService = MailingService.Proxy.get();
	
	public static interface MailingResources extends ClientBundle {
		
		@Source("com/sencha/gxt/theme/base/client/grid/loading.gif") 
		ImageResource loading();
		
	}
	
	private MailingResources ressources = GWT.create(MailingResources.class);
	private ToggleButton statusButton = new ToggleButton("On");
	private SimpleComboBox<String> month = new SimpleComboBox<String>(new StringLabelProvider<String>());
	private DateField date = new DateField();
	private TimeField time = new TimeField();
	private TextButton refresh = new TextButton("Refresh");
    private TextButton save = new TextButton("Save");
    private List<String> lFrequency = Arrays.asList(new String[]{"Weekly", "2 weeks", "Monthly"}); 
    private FramedPanel form2;
	public Widget getWidget(){
		form2 = new FramedPanel();
	    form2.setHeadingText("");
	    form2.setWidth("100%");
	    
	    FieldSet fieldSet = new FieldSet();
	    fieldSet.setHeadingText("Emailing Setting");
	    fieldSet.setCollapsible(true);
	    fieldSet.setHeight(155);
	    form2.add(fieldSet);
	    
	    
	    VerticalLayoutContainer p = new VerticalLayoutContainer();
	    fieldSet.add(p);
	    HorizontalPanel hp = new HorizontalPanel();
//	    statusButton.setValue(true);
//	    final TextButton refreshButton = new TextButton();
//	    refreshButton.setIcon(ressources.loading());
//	    statusButton.setWidth("60px");
	    hp.add(statusButton);
//	    hp.add(refreshButton);
	    p.add(new FieldLabel(hp, "System status"));
	    
	    month.setTriggerAction(TriggerAction.ALL);
	    month.setEditable(false);
	    month.setWidth(100);
	    month.add(lFrequency);
	      
	    month.addSelectionHandler(new SelectionHandler<String>() {
	 
	        @Override
	        public void onSelection(SelectionEvent<String> event) {
	          
	        }
	      });
	    p.add(new FieldLabel(month, "Frequency"), new VerticalLayoutData(1, -1));
	    date.addParseErrorHandler(new ParseErrorHandler() {
	 
	      @Override
	      public void onParseError(ParseErrorEvent event) {
	        Info.display("Parse Error", event.getErrorValue() + " could not be parsed as a date");
	      }
	    });
	 
	    date.addValueChangeHandler(new ValueChangeHandler<Date>() {
	 
	      @Override
	      public void onValueChange(ValueChangeEvent<Date> event) {
	        String v = event.getValue() == null ? "nothing"
	            : DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(event.getValue());
	 
	      }
	    });
//	    date.addValidator(new MinDateValidator(new Date()));
	    p.add(new FieldLabel(date, "Starting date"), new VerticalLayoutData(1, -1));
	 
	    time.addParseErrorHandler(new ParseErrorHandler() {
	 
	      @Override
	      public void onParseError(ParseErrorEvent event) {
	       
	      }
	    });
	 
	    time.setMinValue(new DateWrapper().clearTime().addHours(8).asDate());
	    time.setMaxValue(new DateWrapper().clearTime().addHours(18).addSeconds(1).asDate());
	    p.add(new FieldLabel(time, "Starting time"), new VerticalLayoutData(1, -1));
	    
	    statusButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				statusButton.setText(statusButton.getValue()?"On":"Off");
			}
		});
	    load();
	    form2.addButton(refresh);
	    form2.addButton(save);
	    refresh.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				refresh.setIcon(ressources.loading());
				refresh.setEnabled(false);
				load();
			}
		});
	    save.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				save.setIcon(ressources.loading());
				save.setEnabled(false);
				save();
			}
		});
		return form2;
	}
	
	public void load(){
		mailingService.getMailingStat(new AsyncCallback<String[]>() {
			
			@Override
			public void onSuccess(String[] result) {
//				Window.alert(result[0] + " " + result[1] + " " + result[2]);
				statusButton.setValue(Boolean.valueOf(result[0]));
				statusButton.setText(Boolean.valueOf(result[0])?"On":"Off");
				month.setValue(lFrequency.get(Integer.valueOf(result[1])));
				time.setValue(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(result[2]));
				date.setValue(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(result[2]));
				refresh.setIcon(null);
				refresh.setEnabled(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				com.google.gwt.user.client.Window.alert(caught.getMessage());
				refresh.setIcon(null);
				refresh.setEnabled(true);
			}
		});
	}

	public void save(){
		
		String frequency = month.getSelectedIndex()+"";
		String stat = statusButton.getValue()+"";
		String url = /*Window.Location.getUrl();*/Window.Location.getHref().split("#", 2)[0];
		String day = DateTimeFormat.getFormat("yyyy-MM-dd").format(date.getValue()) + " " +
				DateTimeFormat.getFormat("HH:mm:ss").format(time.getValue());
//		Window.alert(frequency + " " + stat + " " + url + " " + day);
		mailingService.setMailing(stat, frequency, day, url, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				com.google.gwt.user.client.Window.alert("Error :" + caught.getMessage());
				save.setIcon(null);
				save.setEnabled(true);
			}

			@Override
			public void onSuccess(Boolean result) {
				com.google.gwt.user.client.Window.alert(result?"Success!":"Error!");
				save.setIcon(null);
				save.setEnabled(true);
			}
		});
	}

}
