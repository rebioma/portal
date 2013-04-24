package org.rebioma.client.maps;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.ApplicationView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HideControl extends HTML {
  private final List<Widget> controlWidgets = new ArrayList<Widget>();

  public HideControl() {
    super(ApplicationView.getConstants()
            .HideControls());
    setStyleName("link");
    addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          for (Widget w : controlWidgets) {
            w.setVisible(!w.isVisible());
          }
        }
      });
  }
  public void addControlWidgetToHide(Widget w) {
    controlWidgets.add(w);
  }
}
