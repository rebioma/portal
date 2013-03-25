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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ASCFileDrawer {
  public static class ImageParams {
    public int alpha = 0xFF; // opaque
    public int minColor = 0x0000FF; // blue
    public int maxColor = 0xFF0000; // red
    public int width = 256;
    public int height = 256;
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err
          .println("Syntax: <programName> inputASCFileName outputPNGFileName");
      System.exit(1);
    }

    String inputFileName = args[0];
    String outputFileName = args[1];

    ASCFileReader asc = new ASCFileReader(inputFileName);

    ImageParams iParams = new ImageParams();
    iParams.width = asc.nCols();
    iParams.height = asc.nRows();
    iParams.alpha = 128; // 50% transparency

    BufferedImage im = renderASCFile(asc, iParams,
        GeoProjection.GMAPS_MERCATOR_PROJECTION, asc.SWlat(), asc.SWlng(), asc
            .NElat(), asc.NElng());

    FileOutputStream os = new FileOutputStream(outputFileName);
    ImageIO.write(im, "png", os);

    System.out.println("Wrote image " + outputFileName);
  }

  public static BufferedImage renderASCFile(ASCFileReader asc,
      ImageParams iParams, GeoProjection proj, double south, double west,
      double north, double east) throws IOException {

    BufferedImage im = new BufferedImage(iParams.width, iParams.height,
        BufferedImage.TYPE_INT_ARGB);

    if (north < asc.SWlat() || south > asc.NElat() || east < asc.SWlng()
        || west > asc.NElng())
      return im;

    double north_norm = proj.toY(north);
    double south_norm = proj.toY(south);
    double west_norm = proj.toX(west);
    double east_norm = proj.toX(east);

    double pixelHeight = (north_norm - south_norm) / iParams.height;
    double pixelWidth = (east_norm - west_norm) / iParams.width;
    double range = asc.maxDataValue() - asc.minDataValue();

    int r1 = (iParams.minColor >> 16) & 0xFF;
    int g1 = (iParams.minColor >> 8) & 0xFF;
    int b1 = (iParams.minColor) & 0xFF;

    int r2 = (iParams.maxColor >> 16) & 0xFF;
    int g2 = (iParams.maxColor >> 8) & 0xFF;
    int b2 = (iParams.maxColor) & 0xFF;

    double dr = r2 - r1;
    double dg = g2 - g1;
    double db = b2 - b1;

    for (int y = 0; y < iParams.height; y++) {
      double lat_norm = north_norm - (y + 0.5) * pixelHeight;
      double lat = proj.toLat(lat_norm);
      for (int x = 0; x < iParams.width; x++) {
        double lng_norm = west_norm + (x + 0.5) * pixelWidth;
        double lng = proj.toLng(lng_norm);
        double val = asc.getValue(lat, lng);

        double c = (val - asc.minDataValue()) / range;

        int rgba = 0; // transparent, black
        if (val != asc.noDataValue()) {
          int r = (int) (r1 + dr * c);
          int g = (int) (g1 + dg * c);
          int b = (int) (b1 + db * c);
          rgba = (iParams.alpha << 24) | (r << 16) | (g << 8) | b;
        }
        im.setRGB(x, y, rgba);
      }
    }

    return im;
  }

  public static BufferedImage renderHorizontalGradient(ImageParams iParams)
      throws IOException {
    BufferedImage im = new BufferedImage(iParams.width, iParams.height,
        BufferedImage.TYPE_INT_ARGB);

    int r1 = (iParams.minColor >> 16) & 0xFF;
    int g1 = (iParams.minColor >> 8) & 0xFF;
    int b1 = (iParams.minColor) & 0xFF;

    int r2 = (iParams.maxColor >> 16) & 0xFF;
    int g2 = (iParams.maxColor >> 8) & 0xFF;
    int b2 = (iParams.maxColor) & 0xFF;

    double dr = (double) (r2 - r1) / iParams.width;
    double dg = (double) (g2 - g1) / iParams.width;
    double db = (double) (b2 - b1) / iParams.width;

    for (int x = 0; x < iParams.width; x++) {
      int r = (int) (r1 + x * dr);
      int g = (int) (g1 + x * dg);
      int b = (int) (b1 + x * db);
      int rgb = (iParams.alpha << 24) | (r << 16) | (g << 8) | b;
      for (int y = 0; y < iParams.height; y++) {
        im.setRGB(x, y, rgb);
      }
    }
    return im;
  }
}
