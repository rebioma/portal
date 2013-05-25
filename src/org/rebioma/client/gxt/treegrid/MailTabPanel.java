package org.rebioma.client.gxt.treegrid;

import java.util.Date;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent.ParseErrorHandler;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TimeField;
import com.sencha.gxt.widget.core.client.form.validator.MinDateValidator;
import com.sencha.gxt.widget.core.client.info.Info;

public class MailTabPanel {
	public Widget getWidget(){
		FramedPanel form2 = new FramedPanel();
	    form2.setHeadingText("");
	    form2.setWidth(350);
	 
	    FieldSet fieldSet = new FieldSet();
	    fieldSet.setHeadingText("Emailing Setting");
	    fieldSet.setCollapsible(true);
	    form2.add(fieldSet);
	    
	    
	    VerticalLayoutContainer p = new VerticalLayoutContainer();
	    fieldSet.add(p);
	    TextButton startButton = new TextButton("start");
	    TextButton stopButton = new TextButton("stop");
	    p.add(startButton);
	    p.add(stopButton);
	    SimpleComboBox<String> month = new SimpleComboBox<String>(new StringLabelProvider<String>());
	    month.setTriggerAction(TriggerAction.ALL);
	    month.setEditable(false);
	    month.setWidth(100);
	    month.add("Monthly");
	    month.add("Weekly");
	    month.add("2 weeks");
	    month.setValue("Monthly");
	      
	    month.addSelectionHandler(new SelectionHandler<String>() {
	 
	        @Override
	        public void onSelection(SelectionEvent<String> event) {
	          
	        }
	      });
	    p.add(new FieldLabel(month, "Frequency"), new VerticalLayoutData(1, -1));
	    DateField date = new DateField();
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
	    date.addValidator(new MinDateValidator(new Date()));
	    p.add(new FieldLabel(date, "Starting date"), new VerticalLayoutData(1, -1));
	 
	    TimeField time = new TimeField();
	    time.addParseErrorHandler(new ParseErrorHandler() {
	 
	      @Override
	      public void onParseError(ParseErrorEvent event) {
	       
	      }
	    });
	 
	    time.setMinValue(new DateWrapper().clearTime().addHours(8).asDate());
	    time.setMaxValue(new DateWrapper().clearTime().addHours(18).addSeconds(1).asDate());
	    p.add(new FieldLabel(time, "Starting time"), new VerticalLayoutData(1, -1));
	    
	    return form2;
	}
	

}
