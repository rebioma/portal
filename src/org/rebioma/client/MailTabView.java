package org.rebioma.client;

import org.rebioma.client.gxt.treegrid.MailTabPanel;
import org.rebioma.client.gxt.treegrid.StatisticsPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MailTabView extends ComponentView implements ClickHandler{
	
	private final VerticalPanel mainVp;
	 private final Widget mailPanel;
	
	private MailTabView(){
		this(null);
	}
	
	  
	private MailTabView(View parent){
		super(parent, false);
		
		
		mainVp = new VerticalPanel();
		
		mainVp.setSpacing(5);
		// mainVp.setWidth("100%");
		
		mailPanel = new MailTabPanel().getWidget();
		mainVp.setWidth("100%");
		mainVp.add(mailPanel);
		
		initWidget(mainVp);
		
		
		
	}
	
	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new MailTabView(parent);
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
		

	}
	
	public String historyToken() {
		// TODO Auto-generated method stub
		return super.historyToken();
	}

	
	

	
	

}
