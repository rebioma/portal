package org.rebioma.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class PopupMessage extends PopupPanel {

  public static PopupMessage getInstance() {
    if (instance == null) {
      instance = new PopupMessage();
    }
    return instance;
  }

  private final HTML htmlWidget = new HTML();

  private final Timer showTimer = new Timer() {
    @Override
    public void run() {
      hide();
    }
  };

  private static PopupMessage instance = null;

  public PopupMessage(String html) {
    super(true);
    setWidget(htmlWidget);
    setMessage(html);
  }

  private PopupMessage() {
    super(true);
    setWidget(htmlWidget);
  }

  public void setMessage(String html) {
    htmlWidget.setHTML(html);
    htmlWidget.setStyleName("popup");
  }

  public void showMessage(String htmlMsg) {
    setMessage(htmlMsg);
    center();
    show();
    showTimer.schedule(5000);
  }
}
