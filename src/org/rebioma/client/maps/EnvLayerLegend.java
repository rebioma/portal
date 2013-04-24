/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.client.maps;

import org.rebioma.client.DataSwitch;
import org.rebioma.client.bean.AscData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EnvLayerLegend extends TileLayerLegend {

  private final AscData data;
  private final Image img = new Image();
  private final Label minLabel = new Label();
  private final Label maxLabel = new Label();
  private final HTML valueHtml = new HTML();
  protected LatLng lookupPoint;
  protected Double lookupValue;

  public EnvLayerLegend(AscData data) {
    super();
    this.data = data;
    img.setUrl(GWT.getModuleBaseURL() + "ascOverlay?legend=1");
    minLabel.setText("" + data.getMinValue());
    maxLabel.setText("" + data.getMaxValue());
    this.addLegend();
  }

  /**
   * Note that if the lookup returns a null result, the callback is not
   * executed.
   */
  @Override
  public void lookupValue(final LatLng point, final LegendCallback callback) {
    DataSwitch.get().getValue(data.getId(), point.getLatitude(),
            point.getLongitude(), new AsyncCallback<Double>() {
              public void onFailure(Throwable caught) {
              }

              public void onSuccess(Double result) {
                lookupPoint = point;
                lookupValue = result;
                String value = result == null ? "" : result.toString();
                setDisplay(point, value);
                callback.onLookup(point, value);
              }
            });
  }

  @Override
  public void setDisplay(LatLng point, String value) {
    if (value == null) {
      value = "";
    }
    String pointText = point.getToUrlValue(7);
    if (value.length() < 1) {
      valueHtml.setHTML("No Data @ " + pointText);
    } else {
      valueHtml.setHTML(value + " " + data.getUnits() + " @ " + pointText);
    }
    this.setVisible(true);
  }

  @Override
  protected DialogBox getDetails() {
    final DialogBox dialogBox = new DialogBox();
    String metadata;
    dialogBox.setText(dataSummary());
    VerticalPanel dialogContents = new VerticalPanel();
    dialogContents.setSpacing(4);
    dialogBox.setWidget(dialogContents);
    HTML info = new HTML(dataAsHtml());
    dialogContents.add(info);
    metadata = "<a href='" + data.getMetadata() + "' target='_blank'>Metadata</a>";
    HTML link = new HTML(metadata);
    link.setStyleName("metadatalink");
    dialogContents.add(link);
    dialogContents.setCellHorizontalAlignment(info,HasHorizontalAlignment.ALIGN_LEFT);
    
    Button closeButton = new Button("Close", new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });
    
    dialogContents.add(closeButton);
    dialogContents.setCellHorizontalAlignment(closeButton,HasHorizontalAlignment.ALIGN_LEFT);
    return dialogBox;
  }

  @Override
  protected Widget getLegendWidget() {
    // Panel for mix/max labels and legend image:
    HorizontalPanel topHp = new HorizontalPanel();
    topHp.setSpacing(5);
    topHp.add(minLabel);
    topHp.add(img);
    topHp.add(maxLabel);
    // Panel for value, coordinates, and details link:
    HorizontalPanel bottomHp = new HorizontalPanel();
    bottomHp.setWidth("100%");
    bottomHp.add(valueHtml);
    bottomHp.setCellWidth(valueHtml, "360px");
    valueHtml.setStyleName("map-LegendValue");
    valueHtml.setHTML("Click map for values...");
    HTML detailsLink = new HTML("Details");
    detailsLink.setStyleName("map-LegendDetailLink");
    detailsLink.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        showDetails();
      }
    });
    bottomHp.add(detailsLink);
    bottomHp.setCellHorizontalAlignment(detailsLink,
            HasHorizontalAlignment.ALIGN_RIGHT);
    // Panel that contains top and bottom panels:
    VerticalPanel vp = new VerticalPanel();
    vp.setStylePrimaryName("rebioma-legendWidget");
    vp.add(topHp);
    vp.add(bottomHp);
    vp.setWidth("380px");
    return vp;
  }

  /**
   * Arrondi d'un double avec n �l�ments apr�s la virgule.
   * @param a La valeur � convertir.
   * @param n Le nombre de d�cimales � conserver.
   * @return La valeur arrondi � n d�cimales.
   */
  public static double floor(double a, int n) {
  	double p = Math.pow(10.0, n);
  	return Math.floor((a*p)+0.5) / p;
  }
  
  private String dataAsHtml() {
	double width1, width2, cellsize = 0;
	width1 = (data.getEastBoundary() - data.getWestBoundary());
	width2 = Double.valueOf(data.getWidth());
	width2 = floor(width2,6);
	cellsize = width1 / width2 ;
	StringBuilder builder = new StringBuilder();
	builder.append("<div id=\"content\">");
    builder.append("<P>");
    builder.append("<b>Year Sampled:</b> " + data.getYear());
    builder.append("<BR>");
    builder.append("<b>Minimum Value:</b> " + data.getMinValue());
    builder.append("<BR>");
    builder.append("<b>Maximum Value:</b> " + data.getMaxValue());
    builder.append("<BR>");
    builder.append("<b>Units:</b> " + data.getUnits());
    builder.append("<BR>");
    builder.append("<b>Type:</b> " + data.getVariableType());
    builder.append("<BR>");
    builder.append("<b>Cell Size:</b> " + cellsize);
    builder.append("<BR>");
    builder.append("<b>Row Count:</b> " + data.getHeight());
    builder.append("<BR>");
    builder.append("<b>Column Count:</b> " + data.getWidth());
    builder.append("</div>");
    return builder.toString();
  }

  private String dataSummary() {
    return data.getEnvDataType() + " - " + data.getEnvDataSubtype() + " "
            + data.getYear();
  }
}
