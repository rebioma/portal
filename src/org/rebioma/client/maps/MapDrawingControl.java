package org.rebioma.client.maps;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.drawinglib.DrawingControlOptions;
import com.google.gwt.maps.client.drawinglib.DrawingManager;
import com.google.gwt.maps.client.drawinglib.DrawingManagerOptions;
import com.google.gwt.maps.client.drawinglib.OverlayType;
import com.google.gwt.maps.client.events.overlaycomplete.OverlayCompleteMapEvent;
import com.google.gwt.maps.client.events.overlaycomplete.OverlayCompleteMapHandler;
import com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapEvent;
import com.google.gwt.maps.client.events.overlaycomplete.polygon.PolygonCompleteMapHandler;
import com.google.gwt.maps.client.overlays.Circle;
import com.google.gwt.maps.client.overlays.CircleOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.overlays.Polygon;
import com.google.gwt.maps.client.overlays.PolygonOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class MapDrawingControl extends Composite implements ClickHandler{
	
	private Polygon polygon;
	
	private Circle circle;
	
	private Marker marker;
	
	private DrawingManager drawingManager;
	
	private static final double CIRCLE_RADIUS = 10000d;//10kms
	
	private CircleOptions circleOptions = CircleOptions.newInstance();
	
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
//	    drawingControlOptions.setDrawingModes(OverlayType.values());
	    OverlayType[] overlayTypes = {OverlayType.POLYGON, OverlayType.MARKER};
	    drawingControlOptions.setDrawingModes(overlayTypes);
	    
	    MarkerOptions markerOptions = MarkerOptions.newInstance();
	    /*MarkerImage markerImage = MarkerImage.newInstance("");
	    markerOptions.setIcon(arg0);*/
	    markerOptions.setTitle("Search");
	    
	    PolygonOptions polygonOptions = PolygonOptions.newInstance();
	    polygonOptions.setStrokeColor("#ff0000");//couleur du contour
	    polygonOptions.setStrokeOpacity(1);//opacité du contour
	    polygonOptions.setFillColor("#ffff00");//couleur de l'interieur du polygone
	    polygonOptions.setFillOpacity(0.5);//opacité de l'interieur du polygone
	    polygonOptions.setClickable(false);
	    
	    circleOptions = CircleOptions.newInstance();

	    circleOptions.setFillColor("#F7FF3C");
	    circleOptions.setStrokeColor("#FF0000");
	    circleOptions.setStrokeWeight(1);
	    circleOptions.setFillOpacity(0.4d);
	    circleOptions.setStrokeOpacity(0.8d);
	    circleOptions.setRadius(CIRCLE_RADIUS);
	    DrawingManagerOptions options = DrawingManagerOptions.newInstance();
	    options.setMap(map);
	    options.setPolygonOptions(polygonOptions);
//	    options.setCircleOptions(circleOptions);
	    options.setMarkerOptions(markerOptions);
	    /*options.setDrawingMode(OverlayType.CIRCLE);
	    options.setCircleOptions(circleOptions);*/
	    options.setDrawingControlOptions(drawingControlOptions);

	    drawingManager = DrawingManager.newInstance(options);
	    
//	    o.setDrawingMode(drawingMode);
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
	    drawingManager.addOverlayCompleteHandler(new OverlayCompleteMapHandler() {
	      public void onEvent(OverlayCompleteMapEvent event) {
	        OverlayType ot = event.getOverlayType();
	        GWT.log("marker completed OverlayType=" + ot.toString());

//	        if (ot == OverlayType.CIRCLE) {
//	          Circle circle = event.getCircle();
//	          GWT.log("radius=" + circle.getRadius());
//	        }

	        if (ot == OverlayType.MARKER) {
	        	if(polygon != null){
	        		polygon.setMap(null);
	        	}
	        	if(marker != null){
	        		marker.setMap((MapWidget)null);
	        	}
	          if(circle != null){
	        	  circle.setMap(null);
	          }
	          marker = event.getMarker();
	          circleOptions.setCenter(marker.getPosition());
	          circleOptions.setMap(marker.getMap());
	          circle = Circle.newInstance(circleOptions);
	          circle.setMap(marker.getMap());
	          GWT.log("position=" + marker.getPosition());
	          for(MapDrawingControlListener listener: mapDrawingControlListeners){
//		        	//normalement il n'y a que le mapView
	        	  listener.circleDrawingCompleteHandler(circle);
		       }
	        }

	        if (ot == OverlayType.POLYGON) {
	          Polygon polygon = event.getPolygon();
	          GWT.log("paths=" + polygon.getPaths().toString());
	        }

//	        if (ot == OverlayType.POLYLINE) {
//	          Polyline polyline = event.getPolyline();
//	          GWT.log("paths=" + polyline.getPath().toString());
//	        }
//
//	        if (ot == OverlayType.RECTANGLE) {
//	          Rectangle rectangle = event.getRectangle();
//	          GWT.log("bounds=" + rectangle.getBounds());
//	        }
	        GWT.log("marker completed OverlayType=" + ot.toString());
	      }
	    });
	    
//	    o.addCircleCompleteHandler(new CircleCompleteMapHandler() {
//			@Override
//			public void onEvent(CircleCompleteMapEvent event) {
//				if(circle != null){
//					circle.setMap(null);
//		    	  }
//				circle = event.getCircle();
//		        GWT.log("Circle with radius" + circle.getRadius());
//		        //mapDrawingControlListeners
//		        for(MapDrawingControlListener listener: mapDrawingControlListeners){
//		        	//normalement il n'y a que le mapView
//		        	listener.circleDrawingCompleteHandler(circle);
//		        }
//			}
//		});

	    drawingManager.addPolygonCompleteHandler(new PolygonCompleteMapHandler() {
	      public void onEvent(PolygonCompleteMapEvent event) {
	    	  if(marker != null){
	        		marker.setMap((MapWidget)null);
	        	}
	          if(circle != null){
	        	  circle.setMap(null);
	          }
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
	
	public void clearOverlays(){
		boolean overlayDeleted = false;
		if(marker != null){
			marker.setMap((MapWidget)null);
			overlayDeleted = true;
		}
		if(circle != null){
			circle.setMap(null);
			overlayDeleted = true;
		}
		 if(polygon != null){
	      	  polygon.setMap(null);//effacer le polygon du map
	      	overlayDeleted = true;
		 }
		 if(overlayDeleted){
			 for(MapDrawingControlListener listener: mapDrawingControlListeners){
		        	//normalement il n'y a que le mapView
		        	listener.shapeDeleteHandler();
		        }
		 }
	}

	@Override
	public void onClick(ClickEvent evt) {
		Window.alert("Changed to " + drawingManager.getDrawingMode());
	}

}
