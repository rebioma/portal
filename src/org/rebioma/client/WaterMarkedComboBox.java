package org.rebioma.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class WaterMarkedComboBox<T> extends ComboBox<T> implements BlurHandler,
		FocusHandler {
	
	String watermark;
    HandlerRegistration blurHandler;
    HandlerRegistration focusHandler;
    
    private void init(){
        this.setStylePrimaryName("rebioma-searchinput");
    }
    
	public WaterMarkedComboBox(ComboBoxCell<T> cell) {
		super(cell);
		init();
	}

	public WaterMarkedComboBox(ListStore<T> store,
			LabelProvider<? super T> labelProvider, ListView<T, ?> listView,
			TriggerFieldAppearance appearance) {
		super(store, labelProvider, listView, appearance);
		init();
	}

	public WaterMarkedComboBox(ListStore<T> store,
			LabelProvider<? super T> labelProvider, ListView<T, ?> listView) {
		super(store, labelProvider, listView);
		init();
	}

	public WaterMarkedComboBox(ListStore<T> store,
			LabelProvider<? super T> labelProvider,
			SafeHtmlRenderer<T> renderer, TriggerFieldAppearance appearance) {
		super(store, labelProvider, renderer, appearance);
		init();
	}

	public WaterMarkedComboBox(ListStore<T> store,
			LabelProvider<? super T> labelProvider, SafeHtmlRenderer<T> renderer) {
		super(store, labelProvider, renderer);
		init();
	}

	public WaterMarkedComboBox(ListStore<T> store,
			LabelProvider<? super T> labelProvider,
			TriggerFieldAppearance appearance) {
		super(store, labelProvider, appearance);
		init();
	}

	public WaterMarkedComboBox(ListStore<T> store,
			LabelProvider<? super T> labelProvider) {
		super(store, labelProvider);
		init();
	}

	@Override
	public void onFocus(FocusEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlur(BlurEvent event) {
		// TODO Auto-generated method stub

	}

}
