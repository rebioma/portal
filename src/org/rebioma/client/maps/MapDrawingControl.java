package org.rebioma.client.maps;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.drawinglib.DrawingControlOptions;
import com.google.gwt.maps.client.drawinglib.DrawingManager;
import com.google.gwt.maps.client.drawinglib.DrawingManagerOptions;
import com.google.gwt.maps.client.drawinglib.OverlayType;
import com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapEvent;
import com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapHandler;
import com.google.gwt.maps.client.overlays.Polygon;
import com.google.gwt.maps.client.overlays.PolygonOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class MapDrawingControl extends Composite{
	
	private Polygon polygon;
	
	List<MapDrawingControlListener> mapDrawingControlListeners = new ArrayList<MapDrawingControlListener>();
	
	public MapDrawingControl(MapWidget map, ControlPosition position) {
		//ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
	    draw(map, position);
	}
	
	public void addListener(MapDrawingControlListener listener){
		if(mapDrawingControlListeners != null){
			mapDrawingControlListeners.add(listener);
		}
	}

	private void draw(MapWidget map, ControlPosition position){
		//http://code.google.com/p/gwt-maps-api/source/browse/trunk/Apis_Maps_Test/src/com/gonevertical/maps/testing/client/maps/DrawingMapWidget.java
		DrawingControlOptions drawingControlOptions = DrawingControlOptions.newInstance();
	    drawingControlOptions.setPosition(position);
	    //drawingControlOptions.setDrawingModes(OverlayType.values());
	    drawingControlOptions.setDrawingModes(OverlayType.POLYGON);
	    
	    PolygonOptions polygonOptions = PolygonOptions.newInstance();
	    polygonOptions.setStrokeColor("#ff0000");//couleur du contour
	    polygonOptions.setStrokeOpacity(1);//opacité du contour
	    polygonOptions.setFillColor("#ffff00");//couleur de l'interieur du polygone
	    polygonOptions.setFillOpacity(0.5);//opacité de l'interieur du polygone
	    polygonOptions.setClickable(false);
//	    CircleOptions circleOptions = CircleOptions.newInstance();
	    //circleOptions.setFillColor("FF6633");

	    DrawingManagerOptions options = DrawingManagerOptions.newInstance();
	    options.setMap(map);
	    options.setPolygonOptions(polygonOptions);
	    /*options.setDrawingMode(OverlayType.CIRCLE);
	    options.setCircleOptions(circleOptions);*/
	    options.setDrawingControlOptions(drawingControlOptions);

	    DrawingManager o = DrawingManager.newInstance(options);
	    

//	    o.addCircleCompleteHandler(new CircleCompleteMapHandler() {
//	      public void onEvent(CircleCompleteMapEvent event) {
//	        Circle circle = event.getCircle();
//	        GWT.log("circle completed radius=" + circle.getRadius());
//	      }
//	    });
//
//	    o.addMarkerCompleteHandler(new MarkerCompleteMapHandler() {
//	      public void onEvent(MarkerCompleteMapEvent event) {
//	        Marker marker = event.getMarker();
//	        GWT.log("marker completed position=" + marker.getPosition());
//	      }
//	    });
//
//	    o.addOverlayCompleteHandler(new OverlayCompleteMapHandler() {
//	      public void onEvent(OverlayCompleteMapEvent event) {
//	        OverlayType ot = event.getOverlayType();
//	        GWT.log("marker completed OverlayType=" + ot.toString());
//
//	        if (ot == OverlayType.CIRCLE) {
//	          Circle circle = event.getCircle();
//	          GWT.log("radius=" + circle.getRadius());
//	        }
//
//	        if (ot == OverlayType.MARKER) {
//	          Marker marker = event.getMarker();
//	          GWT.log("position=" + marker.getPosition());
//	        }
//
//	        if (ot == OverlayType.POLYGON) {
//	          Polygon polygon = event.getPolygon();
//	          GWT.log("paths=" + polygon.getPaths().toString());
//	        }
//
//	        if (ot == OverlayType.POLYLINE) {
//	          Polyline polyline = event.getPolyline();
//	          GWT.log("paths=" + polyline.getPath().toString());
//	        }
//
//	        if (ot == OverlayType.RECTANGLE) {
//	          Rectangle rectangle = event.getRectangle();
//	          GWT.log("bounds=" + rectangle.getBounds());
//	        }
//	        GWT.log("marker completed OverlayType=" + ot.toString());
//	      }
//	    });

	    o.addPolygonCompleteHandler(new PolygonCompleteMapHandler() {
	      public void onEvent(PolygonCompleteMapEvent event) {
	    	  if(polygon != null){
	    		  polygon.setMap(null);
	    	  }
	        polygon = event.getPolygon();
	        GWT.log("Polygon completed paths=" + polygon.getPath().toString());
	        //mapDrawingControlListeners
	        for(MapDrawingControlListener listener: mapDrawingControlListeners){
	        	//normalement il n'y a que le mapView
	        	listener.polygonDrawingCompleteHandler(polygon);
	        }
	      }
	    });

//	    o.addPolylineCompleteHandler(new PolylineCompleteMapHandler() {
//	      public void onEvent(PolylineCompleteMapEvent event) {
//	        Polyline polyline = event.getPolyline();
//	        GWT.log("Polyline completed paths=" + polyline.getPath().toString());
//	      }
//	    });
//
//	    o.addRectangleCompleteHandler(new RectangleCompleteMapHandler() {
//	      public void onEvent(RectangleCompleteMapEvent event) {
//	        Rectangle rectangle = event.getRectangle();
//	        GWT.log("Rectangle completed bounds=" + rectangle.getBounds().getToString());
//	      }
//	    });


	}
	
	public void clearPolygon(){
		 if(polygon != null){
	      	  polygon.setMap(null);//effacer le polygon du map
		      	for(MapDrawingControlListener listener: mapDrawingControlListeners){
		        	//normalement il n'y a que le mapView
		        	listener.polygonDeletedHandler();
		        }
	     }
	}

}
