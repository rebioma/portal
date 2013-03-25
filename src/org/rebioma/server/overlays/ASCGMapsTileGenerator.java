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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @deprecated Given path to an ASC file (ArcInfo raster data in ASCII format),
 *             generates 256x256 PNG tiles suitable for Google Maps tile
 *             overlays.
 * 
 * Deprecated in favor of generating tiles on request.
 * @author daen
 * 
 */
public class ASCGMapsTileGenerator {

  private static final int MIN_ZOOM = 0;
  private static final int MAX_ZOOM = 13;
  private static final int TILE_WIDTH = 256;
  private static final int TILE_HEIGHT = 256;
  private static final String OUTPUT_FORMAT = "png";

  public static void genTiles(String ascPath, String outputDir)
      throws IOException {
    checkPaths(ascPath, outputDir);

    final File asc = new File(ascPath);
    final String ascFileName = asc.getName();

    final ASCFileDrawer.ImageParams iParams = new ASCFileDrawer.ImageParams();
    iParams.width = TILE_WIDTH;
    iParams.height = TILE_HEIGHT;

    final GeoProjection proj = GeoProjection.GMAPS_MERCATOR_PROJECTION;

    ASCFileReader ascReader = new ASCFileReader(ascPath);
    double ascSouth = ascReader.SWlat();
    double ascWest = ascReader.SWlng();
    double ascNorth = ascReader.NElat();
    double ascEast = ascReader.NElng();

    int skipped = 0;
    for (int z = MIN_ZOOM; z < MAX_ZOOM; z++) {
      int nTiles = 1 << z;

      for (int y = 0; y < nTiles; y++) {
        for (int x = 0; x < nTiles; x++) {
          double tileSouth = GMapsUtil.south(x, y, z);
          double tileWest = GMapsUtil.west(x, y, z);
          double tileNorth = GMapsUtil.north(x, y, z);
          double tileEast = GMapsUtil.east(x, y, z);

          if (tileSouth > ascNorth || tileNorth < ascSouth
              || tileWest > ascEast || tileEast < ascWest) {
            skipped += 1;
            continue;
          }

          BufferedImage rendered = ASCFileDrawer.renderASCFile(ascReader,
              iParams, proj, tileSouth, tileWest, tileNorth, tileEast);

          String outputFileName = ascFileName + "_x" + x + "_y" + y + "_z" + z
              + "." + OUTPUT_FORMAT;
          File outputFile = new File(outputDir, outputFileName);

          ImageIO.write(rendered, OUTPUT_FORMAT, outputFile);

          System.out.println("Wrote " + outputFileName);
        }
      }
    }
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Syntax: <programName> ascFilePath outputDir");
      System.exit(1);
    }

    String ascPath = args[0];
    String outputPath = args[1];

    try {
      genTiles(ascPath, outputPath);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(2);
    }
  }

  private static void checkPaths(String ascPath, String outputDir)
      throws IOException {
    File asc = new File(ascPath);
    if (!asc.exists() || !asc.canRead())
      throw new IOException("File " + ascPath + " is not readable");

    File output = new File(outputDir);
    if (!output.exists() || !output.isDirectory() || !output.canWrite())
      throw new IOException("Directory " + outputDir + " is not writable");
  }
}
