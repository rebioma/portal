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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * ArcInfo ASCII Grid file parser
 * <portalPanel> 
 * Example file contents:
 * =============
 * ncols         2
 * nrows         2
 * xllcorner     43.183344973251
 * yllcorner     -25.64166487474
 * cellsize      0.0083333337679505
 * NODATA_value  -9999
 * -9999 -3.4
 * 0.1 2.111
 * =============
 * <portalPanel>
 * @author daen
 *
 */
public class ASCFileReader {
  
  private Double xllcorner, yllcorner;

  private Double cellSize;

  private Integer nRows, nCols;

  private Double noDataValue = -9999.0;
  
  private double minData =  Double.MAX_VALUE;
  private double maxData = -Double.MAX_VALUE;
  private long dataPointsRead = 0;

  private double[][] data; //[row][col]
  
  private String fileName;
  private long fileSize;

  private static enum ParserState {
    HEADER_KEY, HEADER_VAL, DATA_INIT, DATA_VAL
  }
  
  private class Parser {
    ParserState parserState = ParserState.HEADER_KEY;
    int r = 0;
    int c = 0;
    
    private boolean haveAllData() {
      return (parserState == ParserState.DATA_VAL && (r >= nRows || c >= nCols));
    }
    
    private void parseDataVal(double dataVal) {
      data[r][c] = dataVal;
      if (dataVal != noDataValue) {
        minData = Math.min(minData, dataVal);
        maxData = Math.max(maxData, dataVal);
      }
      
      // increment indeces
      c += 1;
      if (c >= nCols) {
        c = 0;
        r += 1;
      }
      dataPointsRead += 1;
    }

    private void parseHeaderKeyVal(String key, double val) {
      if (key.equals("ncols"))
        nCols = (int)val;
      else if (key.equals("nrows"))
        nRows = (int)val;
      else if (key.equals("xllcorner"))
        xllcorner = val;
      else if (key.equals("yllcorner"))
        yllcorner = val;
      else if (key.equals("cellsize"))
        cellSize = val;
      else if (key.equals("NODATA_value"))
        noDataValue = val;
    }
    
    public void readAll() throws IOException {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      StreamTokenizer st = new StreamTokenizer(reader);
      st.parseNumbers();
      st.wordChars('_', '_');
      
      String headerKey = "";
      int tokenType = st.nextToken();
      while (tokenType != StreamTokenizer.TT_EOF && !haveAllData()) {
        if (tokenType == StreamTokenizer.TT_EOL)
          continue;
        
        String tokenString = st.sval;
        double tokenNumber = st.nval;
        
        switch (parserState) {
        
        case HEADER_KEY:
          if (null == tokenString) {
            parserState = ParserState.DATA_INIT;
          } else {
            headerKey = tokenString;
            tokenType = st.nextToken();
            parserState = ParserState.HEADER_VAL;
          }
          break;
          
        case HEADER_VAL:
          parseHeaderKeyVal(headerKey, tokenNumber);
          tokenType = st.nextToken();
          parserState = ParserState.HEADER_KEY;
          break;
          
        case DATA_INIT:
          // sanity check
          if (xllcorner == null || yllcorner == null || cellSize == null || nRows == null || nCols == null)
            throw new IOException("Not all required parameters were read");
          data = new double[nRows][nCols];
          parserState = ParserState.DATA_VAL;
          break;
          
        case DATA_VAL:
          parseDataVal(tokenNumber);
          tokenType = st.nextToken();
          // no change to parserState
          break;
        } // end switch
      } // end while
      
      // sanity check
      if (nRows * nCols != dataPointsRead)
        throw new IOException("nRows * nCols != dataPointsRead");
        
    }
  }
  

  public double noDataValue() {
    return noDataValue;
  }
  
  public double SWlat() {
    return yllcorner;
  }
  
  public double SWlng() {
    return xllcorner;
  }
  
  public double NElat() {
    return yllcorner + nRows * cellSize;
  }
  
  public double NElng() {
    return xllcorner + nCols * cellSize;
  }
  
  public double minDataValue() {
    return minData;
  }
  
  public double maxDataValue() {
    return maxData;
  }
  
  public long fileSize() {
    return fileSize;
  }
  
  public long samplesCount() {
    return dataPointsRead;
  }
  
  public int nRows() {
    return nRows;
  }
  
  public int nCols() {
    return nCols;
  }
  
  public double getValue(double lat, double lng) {
    int col  = (int)((lng - xllcorner) / cellSize);
    int _row = (int)((lat - yllcorner) / cellSize);
    int row  = nRows - 1 - _row; // The origin of the grid is the upper left and terminus at the lower right.
    
    if (row >= 0 && row < nRows && col >= 0 && col < nCols)
      return data[row][col];
    else
      return noDataValue;
  }
  
  public ASCFileReader(String fileName) throws IOException {
    this.fileName = fileName;
    this.fileSize = new File(fileName).length();
    
    // parse file
    new Parser().readAll();
  }
  
  public static void main(String[] args) {
    try {
      String file = args[0];
      
      long curTime = System.currentTimeMillis();
      ASCFileReader r = new ASCFileReader(file);
      long elapsedMillis = System.currentTimeMillis() - curTime;
      
      double fileSizeMb = (double)r.fileSize() / Math.pow(2, 20);
      double elapsedSec = (double)elapsedMillis / 1000;
      
      System.out.printf("Read file %s (%.2f Mb) in %.2f sec%n", file, fileSizeMb, elapsedSec);
      System.out.printf("%d points, min=%.2f, max=%.2f%n", r.samplesCount(), r.minDataValue(), r.maxDataValue());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
