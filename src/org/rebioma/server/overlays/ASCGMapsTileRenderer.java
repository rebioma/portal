/*
 * Copyright 2008 University of California at Berkeley.
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
package org.rebioma.server.overlays;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.rebioma.server.inject.InjectableHttpServlet;

public class ASCGMapsTileRenderer extends InjectableHttpServlet {
  private static class RequestFormatError extends Exception {
  }

  private static class RequestParams {
    public int tile_x, tile_y, zoom;
    public String fileName;
    public boolean legend;
  }

  public static final String DEFAULT_ASC_DIR = "/opt/asc";

  private static final long serialVersionUID = 1L;

  private static final int LEGEND_WIDTH = 280;

  private static final int LEGEND_HEIGHT = 15;

  private static final int MAX_COLOR = 0xFF0000;

  private static final int MIN_COLOR = 0xFF;

  private static Logger log = Logger.getLogger(ASCGMapsTileRenderer.class);

  private static final int TILE_WIDTH = 256;

  private static final int TILE_HEIGHT = 256;

  private static final String IMG_FORMAT = "png";

  @Override
  public void service(ServletRequest req, ServletResponse res) {
    try {
      RequestParams params = getRequestParams(req);
      if (params.legend) {
        System.out.println("render legend");
        renderLegend(res, params);
      } else {
        System.out.println("render image");
        renderImage(res, params);
      }
    } catch (RequestFormatError err) {
      log.error("Invalid request");
      handleError(res);
    } catch (Throwable t) {
      log.error("Unexpected error", t);
      t.printStackTrace();
      handleError(res);
    }
  }

  private ASCFileReader getReader(String fileName) throws IOException {
    // System.out.println("asc file request: " + fileName);
    String completePath = StoragePathManager.getStoragePath(fileName, super
        .getServletContext().getRealPath("/"));
    // System.out.println("Complete path: " + completePath);
    return ASCReaderProvider.getReader(completePath);
  }

  private RequestParams getRequestParams(ServletRequest req)
      throws RequestFormatError {
    RequestParams params = new RequestParams();
    try {
      String x = req.getParameter("x");
      String y = req.getParameter("y");
      String z = req.getParameter("z");
      String legend = req.getParameter("legend");

      if (x != null)
        params.tile_x = Integer.parseInt(x);
      if (y != null)
        params.tile_y = Integer.parseInt(y);
      if (z != null)
        params.zoom = Integer.parseInt(z);

      params.legend = (legend != null && legend.equals("1"));
      params.fileName = req.getParameter("f");

    } catch (Throwable t) {
      throw new RequestFormatError();
    }

    return params;
  }

  /**
   * Output to write as the HTTP response in case of an error
   */
  private void handleError(ServletResponse res) {
    // Log error.
  }

  private void renderImage(ServletResponse res, RequestParams params)
      throws IOException {
    ASCFileDrawer.ImageParams iParams = new ASCFileDrawer.ImageParams();
    iParams.width = TILE_WIDTH;
    iParams.height = TILE_HEIGHT;
    iParams.minColor = MIN_COLOR;
    iParams.maxColor = MAX_COLOR;

    res.setContentType("image/" + IMG_FORMAT);

    OutputStream os = res.getOutputStream();

    ASCFileReader asc = getReader(params.fileName);

    double south = GMapsUtil.south(params.tile_x, params.tile_y, params.zoom);
    double north = GMapsUtil.north(params.tile_x, params.tile_y, params.zoom);
    double west = GMapsUtil.west(params.tile_x, params.tile_y, params.zoom);
    double east = GMapsUtil.east(params.tile_x, params.tile_y, params.zoom);
    System.out.println("south: " + south);
    System.out.println("north: " + north);
    System.out.println("west: " + west);
    System.out.println("east: " + east);
    BufferedImage im = ASCFileDrawer.renderASCFile(asc, iParams,
        GeoProjection.GMAPS_MERCATOR_PROJECTION, south, west, north, east);

    try {
      ImageIO.write(im, IMG_FORMAT, os);
    } catch (IOException e) {

    }
  }

  private void renderLegend(ServletResponse res, RequestParams params) {
    ASCFileDrawer.ImageParams iParams = new ASCFileDrawer.ImageParams();
    iParams.height = LEGEND_HEIGHT;
    iParams.width = LEGEND_WIDTH;
    iParams.minColor = MIN_COLOR;
    iParams.maxColor = MAX_COLOR;

    try {
      res.setContentType("image/" + IMG_FORMAT);
      BufferedImage im = ASCFileDrawer.renderHorizontalGradient(iParams);
      ImageIO.write(im, IMG_FORMAT, res.getOutputStream());
    } catch (IOException e) {

    }
  }
}
