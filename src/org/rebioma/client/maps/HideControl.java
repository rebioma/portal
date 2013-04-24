package org.rebioma.client.maps;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.ApplicationView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HideControl extends CustomControl {
  private final HTML hideLink = new HTML(ApplicationView.getConstants()
          .HideControls());
  private final List<Widget> controlWidgets = new ArrayList<Widget>();

  public HideControl(ControlPosition position) {
    super(position);
    hideLink.setStyleName("link");
    // TODO Auto-generated constructor stub
  }

  public void addControlWidgetToHide(Widget w) {
    controlWidgets.add(w);
  }

  public boolean isSelectable() {
    // TODO Auto-generated method stub
    return false;
  }

  protected Widget initialize(MapWidget map) {
    hideLink.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        for (Widget w : controlWidgets) {
          w.setVisible(!w.isVisible());
        }
      }

    });
    return hideLink;
  }

}
