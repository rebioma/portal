package org.rebioma.client;

import org.rebioma.client.gxt.treegrid.ThreatenedSpTab;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.SpeciesExplorerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ThreatenedSpeciesListView extends ComponentView implements
ClickHandler, ChangeHandler {

	private final Widget threatenedSTabW;

	private final ThreatenedSpTab threatenedSTab;

	private final VerticalPanel verticalPanel;

	private ThreatenedSpeciesListView() {
		this(null);
	}
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
			.create(SpeciesExplorerService.class);

	/****/
	public ThreatenedSpeciesListView(View parent) {

		super(parent, false);
		verticalPanel = new VerticalPanel();
		threatenedSTab = new ThreatenedSpTab();
		threatenedSTabW = threatenedSTab.getWidget(); 
		verticalPanel.add(threatenedSTabW);
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
		threatenedSTabW.setPixelSize(w, height - threatenedSTabW.getAbsoluteTop() - 5 );
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute()
            {
            	threatenedSTab.forceLayout();
            }
        });
		
	}
	
	/****/
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
