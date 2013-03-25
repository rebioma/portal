package org.rebioma.client;

import org.rebioma.client.bean.Occurrence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OccurrencePager extends DataPager<Occurrence> {

  public OccurrencePager(int pageSize) {
    this(pageSize, new OccurrenceQuery());
  }

  public OccurrencePager(int pageSize, OccurrenceQuery query) {
    super(pageSize, query);
  }

  public OccurrenceQuery getQuery() {
    return (OccurrenceQuery) super.getQuery();
  }

  protected void requestData(final PageCallback<Occurrence> cb) {
    String sessionId = ApplicationView.getSessionId();
    DataSwitch.get().fetch(sessionId, getQuery(),
            new AsyncCallback<OccurrenceQuery>() {
              public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
                Window.alert(caught.getMessage());
                cb.onPageReady(null);
                return;
              }

              public void onSuccess(OccurrenceQuery result) {
                if (totalDataCount == UNDEFINED) {
                  totalDataCount = result.getCount();
                }
                // totalDataCount = result.getCount();
                cb.onPageReady(result.getResults());
              }
            });

  }
}