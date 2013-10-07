package org.rebioma.client;

import org.rebioma.client.gxt.treegrid.MailingTab;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.SpeciesExplorerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MailingTabView extends ComponentView implements ClickHandler {

	private final Widget mailingTab;
	
	private final VerticalPanel verticalPanel;
	
	private MailingTabView() {
		this(null);
	}
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
			.create(SpeciesExplorerService.class);

	private MailingTabView(View parent) {
		super(parent, false);
		verticalPanel = new VerticalPanel();
		mailingTab = new MailingTab().getWidget(); 
//		mailingTab.setWidth("100%");
//		mailingTab.setHeight((Window.getClientHeight() - 115)  + "px");
	    verticalPanel.add(mailingTab);
		initWidget(verticalPanel);
		resize(Window.getClientWidth(), (Window.getClientHeight() - 115));
		History.addValueChangeHandler(this);
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		resize(event.getWidth(), event.getHeight());
	}
	
	@Override
	protected void resize(final int width, int height) {
		int w = width - 20;
		verticalPanel.setWidth(w + "px");
//		mailingTab.getWidget().setWidth(w  + "px");
		mailingTab.setPixelSize(w, height - mailingTab.getAbsoluteTop() - 5 );

	}
	
	public static ViewInfo init(final View parent, final String name,
			final String historyName) {
		return new ViewInfo() {

			@Override
			protected View constructView() {
				return new MailingTabView(parent);
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
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if(source == null){
		}else{
		}
	}

	@Override
	protected void resetToDefaultState() {
		// TODO Auto-generated method stub
		
	}
	
}
