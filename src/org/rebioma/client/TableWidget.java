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
package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple table widget that supported RPC call.
 * 
 * Note: to make the table resizes correctly on resize onChange go to .CSS file
 * and make the headers width percentages added up to be 100%
 * 
 * @author Tri
 * 
 */
public class TableWidget extends Composite {

  /**
   * A dialog box for displaying error messages.
   */
  private static class ErrorDialog extends DialogBox implements ClickHandler {
    private final HTML body = new HTML("");

    /**
     * Intitialize ErrorDialog widget.
     */
    public ErrorDialog() {
      setStylePrimaryName("Table-ErrorDialog");
      Button closeButton = new Button(constants.Close(), this);
      VerticalPanel panel = new VerticalPanel();
      panel.setSpacing(4);
      panel.add(body);
      panel.add(closeButton);
      panel.setCellHorizontalAlignment(closeButton, VerticalPanel.ALIGN_RIGHT);
      setWidget(panel);
    }

    /**
     * Gets the content of this error message.
     * 
     * @return the HTML text of this error message.
     */
    public String getBody() {
      return body.getHTML();
    }

    /**
     * Hides this error when "close" button is clicked.
     * 
     * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.user
     * .client.ui.Widget)
     */
    public void onClick(ClickEvent event) {
      hide();
    }

    /**
     * Sets an HTML message for this error message.
     * 
     * @param html a html text.
     */
    public void setBody(String html) {
      body.setHTML(html);
    }
  }

  /**
   * 
   * Manages all header click events of this table.
   * 
   * @author Tri
   * 
   */
  private class HeaderClickHandler implements ClickHandler {

    Map<String, List<ClickHandler>> headerEventMap = new HashMap<String, List<ClickHandler>>();

    /**
     * Add a click listener belong to the given header.
     * 
     * @param header the header's name for this click event
     * @param clickListener click event of the given header
     */
    public void addClickHandler(String header, ClickHandler clickListener) {
      String key = header.trim().toLowerCase();
      if (!headerEventMap.containsKey(key)) {
        headerEventMap.put(key, new ArrayList<ClickHandler>());
      }
      headerEventMap.get(key).add(clickListener);
    }

    public void onClick(ClickEvent event) {
      List<ClickHandler> collection = headerEventMap.get(header);
      if (collection != null) {
        for (ClickHandler click : collection) {
          // click.onClick(grid.getWidget(row, cell));
        }
      }

    }

  }

  private static final AppConstants constants = ApplicationView.getConstants();

  private final int columnCount;

  // private final Grid grid = new Grid();

  private final SimplePanel footerPanel = new SimplePanel();
  private final HorizontalPanel header = new HorizontalPanel();
  private final Map<String, Integer> headerColMap = new HashMap<String, Integer>();
  private final SimplePanel headerContainer = new SimplePanel();

  private final HeaderClickHandler headerListener = new HeaderClickHandler();
  private String name;
  private final VerticalPanel outer = new VerticalPanel();

  private final VerticalPanel table = new VerticalPanel();

  /**
   * Initializes this TableWidget.
   * 
   * @param dataProvider a TableDataProvider for this table.
   * @param columns column header names for this table.
   * @param columnStyles column header CSS style names associates with column
   * header names.
   * @param rowCount total row of this table
   */
  public TableWidget(String columns[], String columnStyles[], int rowCount) {
    if (columns.length == 0) {
      throw new IllegalArgumentException(
              "expecting a positive number of columns");
    }

    if (columnStyles != null && columns.length != columnStyles.length) {
      throw new IllegalArgumentException("expecting as many styles as columns");
    }

    columnCount = columns.length;
    headerContainer.setWidget(header);
    headerContainer.setStyleName("header");

    initWidget(outer);
    table.setStyleName("table");
    initTableOutLine();
    initHeader(columns, columnStyles, rowCount);
    setStyleName("Table-TableWidget");
    outer.setSpacing(0);

    footerPanel.setStyleName("footer");
    // table.addTableListener(headerListener);
  }

  /**
   * Initializes this TableWidget.
   * 
   * @param dataProvider a TableDataProvider for this table.
   * @param columns column header names for this table.
   * @param columnStyles column header CSS style names associates with column
   * header names.
   * @param rowCount total row of this table
   */
  public TableWidget(Widget columns[], String columnStyles[], int rowCount) {
    if (columns.length == 0) {
      throw new IllegalArgumentException(
              "expecting a positive number of columns");
    }

    if (columnStyles != null && columns.length != columnStyles.length) {
      throw new IllegalArgumentException("expecting as many styles as columns");
    }
    columnCount = columns.length;
    headerContainer.setWidget(header);
    headerContainer.setStyleName("header");

    initWidget(outer);
    table.setStyleName("table");
    initTableOutLine();
    initHeader(columns, columnStyles, rowCount);
    setStyleName("Table-TableWidget");
    outer.setSpacing(0);

    footerPanel.setStyleName("footer");
    // table.addTableListener(headerListener);
    // grid.setCellPadding(0);
    // grid.setCellSpacing(0);
  }

  /**
   * Adds a click listener to go first button.
   * 
   * @param listener Clicklistener
   */
  public void addFirstClickHandler(ClickHandler listener) {

  }

  /**
   * Adds table's footer
   */
  public void addFooter() {
    outer.add(footerPanel);
  }

  public void addHeaderClickHandler(String header, ClickHandler clickListener) {
    headerListener.addClickHandler(header, clickListener);
  }

  /**
   * Gets the cell widget of the given row and column.
   * 
   * @param row of this table
   * @param col of this table
   * @return the cell widget of the given row and column.
   */
  public Widget getCellWidget(int row, int col) {
    return getRow(row).getWidget(col);
  }

  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Gets data table row count.
   * 
   * @return the data table row count.
   */
  public int getDataRowCount() {
    return table.getWidgetCount();
  }

  public Widget getFooterWidget() {
    return footerPanel.getWidget();
  }

  /**
   * Gets the name of this table.
   * 
   * @return the name of this table
   */
  public String getName() {
    return name;
  }

  @Override
  public int getOffsetHeight() {
    return headerContainer.getOffsetHeight() + table.getOffsetHeight()
            + footerPanel.getOffsetHeight();
  }

  /**
   * 
   * Gets the row that the column widget w is in. (Gets the index of its parent
   * in context of the grandparent)
   * 
   * @param w: is the Column widget in which the row it is in should be returned
   * @return the row of w. Returns -1 if an invalid call is made to getRowIndex.
   */
  public int getRowIndex(Widget w) {
    Widget rowParent = w.getParent();
    if (rowParent != null) {
      Widget tempGrandParent = rowParent.getParent();
      return table.getWidgetIndex(tempGrandParent);
    } else {
      return -1;
    }
  }

  public VerticalPanel getTable() {
    return table;
  }

  /**
   * Removes tables footer.
   */
  public void removeFooter() {
    outer.remove(footerPanel);
  }

  /**
   * Removes the given row from the data table.
   * 
   * @param row the row number belong to current view.
   */
  public void removeRow(int row) {
    HorizontalPanel rowPanel = getRow(row);
    rowPanel.clear();
    table.remove(row);
  }

  public void resetHeader(String columns[], String columnStyles[]) {
    header.clear();
    initHeader(columns, columnStyles, 0);
  }

  public void setFooterWidget(Widget footer) {
    footerPanel.setWidget(footer);
  }

  /**
   * Sets this table name.
   * 
   * @param name of this table.
   * @param isHTML true if the given name is HTML text.
   */
  public void setName(String name, boolean isHTML) {
    this.name = name;
    Widget tableNameWidget;
    if (isHTML) {
      tableNameWidget = new HTML(name);
    } else {
      tableNameWidget = new Label(name);
    }
    outer.insert(tableNameWidget, 1);
    tableNameWidget.setStyleName("table-name");
  }

  public void showRecord(int pageSize, int startRow, Widget data[][]) {
    int totalRow = data.length;
    int destColCount = header.getWidgetCount();
    resize(totalRow, pageSize, startRow);
    for (int row = startRow, rowData = 0; row < startRow
            + (table.getWidgetCount() - (startRow % pageSize)); row++, rowData++) {
      Widget[] srcRowData = data[rowData];
      HorizontalPanel rowPanel = getRow(rowData);
      rowPanel.clear();
      assert (srcRowData.length == destColCount) : " "
              + constants.ColMismatch();
      rowPanel.setStyleName("table-row");
      for (int srcColIndex = 0; srcColIndex < destColCount; ++srcColIndex) {
        Widget cellWidget = srcRowData[srcColIndex];
        cellWidget.setStyleName("col-"
                + header.getWidget(srcColIndex).getStyleName());
        rowPanel.add(cellWidget);
        // grid.setWidget(destRowIndex, srcColIndex, cellWidget);
        // grid.getCellFormatter().setStyleName(destRowIndex, srcColIndex,
        // " row");
      }
    }
  }

  private HorizontalPanel getRow(int row) {
    return (HorizontalPanel) ((SimplePanel) table.getWidget(row)).getWidget();
  }

  /**
   * A helper to initialize table widget with columns names are normal strings.
   * 
   * @param columns column header names for this table.
   * @param columnStyles column header CSS style names associates with column
   * header names.
   * @param rowCount total row of this table
   */
  private void initHeader(String[] columns, String[] columnStyles, int rowCount) {
    // Set up the header row. It's one greater than the number of visible
    // rows.
    //
    // grid.resize(rowCount + 1, columns.length);
    // header.setStyleName("header");
    for (int i = 0, n = columns.length; i < n; i++) {
      // grid.setText(0, i, columns[i]);
      Label headerLb = new Label(columns[i]);
      header.add(headerLb);
      headerColMap.put(columns[i], i);
      if (columnStyles != null) {
        headerLb.setStyleName(columnStyles[i]);
      }
    }
    table.clear();
  }

  /**
   * A helper to initialize table widget with columns names are widgets.
   * 
   * @param columns column header names for this table.
   * @param columnStyles column header CSS style names associates with column
   * header names.
   * @param rowCount total row of this table
   */
  private void initHeader(Widget[] columns, String[] columnStyles, int rowCount) {
    // Set up the header row. It's one greater than the number of visible
    // rows.
    //
    // header.setStyleName("header");
    for (int i = 0, n = columns.length; i < n; i++) {
      header.add(columns[i]);
      if (columnStyles != null) {
        header.setStyleName(columnStyles[i]);
      }
    }
    table.clear();
  }

  /**
   * Sets table view after the navigation bar.
   */
  private void initTableOutLine() {
    outer.clear();
    // outer.add(navbar);
    outer.add(headerContainer);
    outer.add(table);
    outer.add(footerPanel);
  }

  /**
   * Resizes the data table with the given row count.
   * 
   * @param rowCount number of row of the current view.
   */
  private void resize(int rowCount, int pageSize, int startRow) {
    if (rowCount == 0) {
      table.clear();
      return;
    }
    if (rowCount >= pageSize || startRow % pageSize == 0) {
      table.clear();
    }
    for (int row = 0; row < rowCount % (pageSize + 1); row++) {
      SimplePanel panel = new SimplePanel();
      HorizontalPanel rowPanel = new HorizontalPanel();
      panel.setWidget(rowPanel);
      panel.setStyleName("row");
      table.add(panel);
    }
    if (rowCount < pageSize) {
      // grid.resizeRows(rowCount + 1);
    } else {
      // grid.resizeRows(pageSize + 1);
    }
  }

}
