package org.rebioma.client;

import java.util.Map;

import org.rebioma.client.DetailView.FieldConstants;
import org.rebioma.client.bean.AscData;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceSummary;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link DialogBox} to show available occurrence's layer information.
 * <p>
 * CSS styles:<br>
 * .LayerInfo{}<br>
 * .LayerInfo .Field{}<br>
 * .LayerInfo .Value{}<br>
 * 
 * @author Tri
 * 
 */
public class LayerInfoPopup extends DialogBox {

  private class AscDataLoadHandler implements AscDataLoadListener {
    private Occurrence occurrence;

    public void onAscDataLoaded() {
      if (OccurrenceSummary.isAscLayersLoaded(occurrence)) {
        loadAscValues();
      } else {
        DataSwitch.get().loadAscData(occurrence, new AsyncCallback<Occurrence>() {
                  public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                    GWT.log(caught.getMessage(), caught);
                  }

                  public void onSuccess(Occurrence result) {
                    loadAscValues();
                  }

                });
      }

    }

    public void setLoadingInfo(Occurrence occurrence) {
      this.occurrence = occurrence;
    }

    private void loadAscValues() {
      AppConstants constants = ApplicationView.getConstants();
      layersValues.clear();
      HTML header = new HTML(constants.LayerName() + ", "
              + constants.FileName() + ", " + constants.Value() + ", "
              + constants.Unit() + ", " + constants.Year());
      layersValues.add(header);
      header.setStyleName(FIELD_STYLE);
      setAutoHideEnabled(true);
      
      addLayers(FieldConstants.DEMELEVATION, occurrence.getDemelevation());
      addLayers(FieldConstants.GEOL_STRECH, occurrence.getGeolStrech());
      
      addLayers(FieldConstants.PFC1950, occurrence.getPfc1950());
      addLayers(FieldConstants.PFC1970, occurrence.getPfc1970());
      addLayers(FieldConstants.PFC1990, occurrence.getPfc1990());
      addLayers(FieldConstants.PFC2000, occurrence.getPfc2000());

      addLayers(FieldConstants.REAL_MAT1950, occurrence.getRealMat1950());
      addLayers(FieldConstants.REAL_MAT2000, occurrence.getRealMat2000());
      addLayers(FieldConstants.REAL_MATFUTURE, occurrence.getRealMatfuture());
      
      addLayers(FieldConstants.MAXTEMP1950, occurrence.getMaxtemp1950());
      addLayers(FieldConstants.MAX_TEMP2000, occurrence.getMaxTemp2000());
      addLayers(FieldConstants.MAX_TEMPFUTURE, occurrence.getMaxTempfuture());

      addLayers(FieldConstants.MIN_TEMP1950, occurrence.getMinTemp1950());
      addLayers(FieldConstants.MIN_TEMP2000, occurrence.getMinTemp2000());
      addLayers(FieldConstants.MIN_TEMPFUTURE, occurrence.getMinTempfuture());
  
      addLayers(FieldConstants.REAL_MAR1950, occurrence.getRealMar1950());
      addLayers(FieldConstants.REAL_MAR2000, occurrence.getRealMar2000());
      addLayers(FieldConstants.REAL_MARFUTURE, occurrence.getRealMarfuture());

      addLayers(FieldConstants.MAX_PREC1950, occurrence.getMaxPerc1950());
      addLayers(FieldConstants.MAX_PREC2000, occurrence.getMaxPerc2000());
      addLayers(FieldConstants.MAX_PRECFUTURE, occurrence.getMaxPercfuture());
      
      addLayers(FieldConstants.MAX_PREC1950, occurrence.getMinPerc1950());
      addLayers(FieldConstants.MIN_PREC2000, occurrence.getMinPerc2000());
      addLayers(FieldConstants.MIN_PRECFUTURE, occurrence.getMinPercfuture());

      addLayers(FieldConstants.ETP_TOTAL1950, occurrence.getEtpTotal1950());
      addLayers(FieldConstants.ETP_TOTAL2000, occurrence.getEtpTotal2000());
      addLayers(FieldConstants.ETP_TOTALFUTURE, occurrence.getEtpTotalfuture());

      addLayers(FieldConstants.WBPOS1950, occurrence.getWbpos1950());
      addLayers(FieldConstants.WBPOS2000, occurrence.getWbpos2000());
      addLayers(FieldConstants.WBPOSFUTURE, occurrence.getWbposfuture());

      addLayers(FieldConstants.WBYEAR1950, occurrence.getWbyear1950());
      addLayers(FieldConstants.WBYEAR2000, occurrence.getWbyear2000());
      addLayers(FieldConstants.WBYEARFUTURE, occurrence.getWbyearfuture());
      if (layersValues.getWidgetCount() == 0) {
        layersValues.add(new HTML(ApplicationView.getConstants().NoLayerValues()));
      }

      layersValues.add(closeButton);
      layersValues.setCellHorizontalAlignment(closeButton,
              HasHorizontalAlignment.ALIGN_CENTER);
    }
  }

  private String getGeologyType(String value) {
	  String type = null;
	  if (value.equals("1.0")) {
		  type="Unconsolidated Sands";
	  } 
	  if (value.equals("2.0")) {
		  type="Lavas (including Basalts_Gabbros)";
	  }
	  if (value.equals("3.0")) {
		  type="Tertiary Limestones + Marls & Chalks";
	  }
	  if (value.equals("4.0")) {
		  type="Alluvial & Lake deposits";
	  }
	  if (value.equals("5.0")) {
		  type="Mesozoic Limestones + Marls (inc. 'Tsingy')";
	  }
	  if (value.equals("6.0")) {
		  type="Sandstones";
	  }
	  if (value.equals("7.0")) {
		  type="Basement Rocks (Ign & Met)";
	  }
	  if (value.equals("8.0")) {
		  type="Quartzites";
	  }
	  if (value.equals("9.0")) {
		  type="Marble (Cipolin)";
	  }
	  if (value.equals("10.0")) {
		  type="Mangrove Swamp";
	  }
	  if (value.equals("11.0")) {
		  type="Ultrabasics";
	  }
	return type;  
  }
  
  
  private interface AscDataLoadListener {
    void onAscDataLoaded();
  }

  private static Map<String, AscData> layersAscDataMap = null;
  private static boolean isLoadingAscDataMap = false;

  private final VerticalPanel layersValues = new VerticalPanel();
  private final Button closeButton;
  private static final String DEFAULT_STYLE_NAME = "LayerInfo";
  private static final String FIELD_STYLE = "Field";
  private static final String VALUE_STYLE = "Value";

  private static LayerInfoPopup instance = null;

  public static LayerInfoPopup getInstance() {
    if (instance == null) {
      instance = new LayerInfoPopup();
    }
    return instance;
  }

  private static void getAscDataMap(final AscDataLoadListener listener) {
    if (layersAscDataMap == null && !isLoadingAscDataMap) {
      isLoadingAscDataMap = true;
      DataSwitch.get().getAscDataMap(new AsyncCallback<Map<String, AscData>>() {
        public void onFailure(Throwable caught) {
          Window.alert(caught.getMessage());
          GWT.log(caught.getMessage(), caught);
          isLoadingAscDataMap = false;
        }

        public void onSuccess(Map<String, AscData> result) {
          if (result == null) {
            onFailure(new IllegalStateException("AscDataMap is null"));
          } else {
            layersAscDataMap = result;
            isLoadingAscDataMap = false;
            listener.onAscDataLoaded();
          }

        }

      });
    } else {
      listener.onAscDataLoaded();
    }
  }

  private final AscDataLoadHandler dataLoadHandler = new AscDataLoadHandler();

  /**
   * Initializes LayerInfoPopup and gives it a {@link #DEFAULT_STYLE_NAME}.
   */
  private LayerInfoPopup() {
    super(true);
    closeButton = new Button(ApplicationView.getConstants().Close());
    layersValues.add(closeButton);
    layersValues.setSpacing(5);
    closeButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        hide();
      }
    });
    setWidget(layersValues);
    addStyleName(DEFAULT_STYLE_NAME);
  }

  /**
   * Loads all available layers information of the given {@link Occurrence}. If
   * none is loaded, it will display "Has no layer values";
   * 
   * @param occurrence
   */
  public void loadLayersInfo(Occurrence occurrence) {
    AppConstants constants = ApplicationView.getConstants();
    dataLoadHandler.setLoadingInfo(occurrence);
    String tittle = "ReBioMa Id: " + occurrence.getId() + "<br>";
    String cord = constants.Coordinate() + " "
            + occurrence.getDecimalLatitude() + ", "
            + occurrence.getDecimalLongitude() + "<br>";
    String yearCollected = constants.YearCollected() + " "
            + occurrence.getYearCollected();
    setHTML(tittle + cord + yearCollected);
    super.setAutoHideEnabled(false);
    layersValues.clear();
    layersValues.add(
    		new HTML(ApplicationView.getConstants().Loading())
    		);
    layersValues.setStyleName("loading");
    center();
    getAscDataMap(dataLoadHandler);

    // layersValues.add(closeButton);
  }

  private void addLayers(String layerShortName, String value) {
    addLayers(layerShortName, FieldConstants.getFullFieldDescription(layerShortName), value);
  }

  private void addLayers(String layerShortName, String layerFullName, String value) {
    if (value == null) {
      return;
    }
   	
    AscData ascData = layersAscDataMap.get(layerFullName.toLowerCase());
    StringBuilder layer = new StringBuilder();
    layer.append("<a href='" + ascData.getMetadata() + "' target='_blank'>"
            + ascData.getEnvDataType() + " - " + ascData.getEnvDataSubtype()
            + "</a>,&nbsp;");
    layer.append(ascData.getFileName() + ",&nbsp;");
    if (layerShortName.equals("Geology Strech")){
    	layer.append(getGeologyType(value) + ",&nbsp;");
    } else {
    layer.append(value + ",&nbsp;");
    }
    layer.append(ascData.getUnits()+ ",&nbsp;");
    layer.append(ascData.getYear());

    HTML layerInfo = new HTML(layer.toString());
    layerInfo.setStyleName(VALUE_STYLE);
    layersValues.add(layerInfo);
  }

}
