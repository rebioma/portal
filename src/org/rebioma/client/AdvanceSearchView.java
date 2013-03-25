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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.SearchFieldSuggestion.TermSelectionListener;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * A view to enable advance search with a suggestion box to search in specific
 * fields.
 * 
 * Each time search criteria(s) is/are added add a new history token which
 * contains the current search queries in the url.
 * 
 * The history token url for the advance search is.
 * view=Advance&asearch=searchQuery&asearch=searchQuery&...
 * 
 * Search query format: database field name + space + operator + space + search
 * value.
 * 
 * When history is changed by either refresh, back, or forward button, search
 * queries queue is restore and display.
 * 
 * @author Tri
 * 
 */
public class AdvanceSearchView extends ComponentView implements
        TermSelectionListener, ClickHandler, MouseOutHandler, MouseOverHandler,
        KeyUpHandler, BlurHandler {
  /**
   * Encodes an advance search field type: {@link ValueType}, database field
   * name, client human readable name, and fixedValues if any.
   * 
   * @author Tri
   * 
   */
  public static class ASearchType {
    /**
     * The {@link ValueType} for a {@link SearchField}
     */
    private final ValueType type;
    /**
     * The database field name presentation of a search field that associated
     * with the {@link #clientName}.
     */
    private final String databaseField;
    /**
     * The human readable name of a search field which associated with
     * {@link #databaseField}.
     */
    private final String clientName;
    /**
     * The possible search values for a {@link ValueType#FIXED} of a search
     * field. These values are not used if {@link ValueType} is not FIXED.
     */
    private final String fixedValues[];

    private final String link;

    /**
     * Construct a ASearchType with a {@link ValueType}, a human readable name,
     * its database representation, and its fixed values if ValueType is FIXED.
     * 
     * @param type the {@link ValueType} for this search type.
     * @param clientName the human readable name for its database field.
     * @param databaseField the database representation of the client name.
     * @param fixedValues the possiable search values if {@link ValueType} is
     * FIXED.
     */
    public ASearchType(ValueType type, String clientName, String databaseField,
            String fixedValues[], String link) {
      this.type = type;
      this.databaseField = databaseField;
      this.fixedValues = fixedValues;
      this.clientName = clientName;
      this.link = link;
    }

    /**
     * Gets the human readable name of {@link databaseField} for this
     * {@link ASearchType}.
     * 
     * @return the human readable name as {@link String}
     */
    public String getClientName() {
      return clientName;
    }

    /**
     * Gets the database representation of {@link #clientName} for this
     * {@link ASearchType}.
     * 
     * @return the database representation as {@link String}
     */
    public String getDatabaseField() {
      return databaseField;
    }

    /**
     * Gets the possible search values for this {@link ASearchType}.
     * 
     * Note: these values are only used if the {@link #type} is
     * {@link ValueType#FIXED}.
     * 
     * @return the possible search values as an array of {@link String}
     */
    public String[] getFixedValues() {
      return fixedValues;
    }

    public String getLink() {
      return link;
    }

    /**
     * Gets the {@link ValueType} for this {@link ASearchType}.
     * 
     * @return the {@link ValueType}.
     */
    public ValueType getType() {
      return type;
    }
  }

  /**
   * A value type for a {@link SearchField}. There are 3 possible values: FIXED,
   * TEXT, and DATE. Base on which ValueType is used the SearchField will
   * display a propriated user interface for it.
   * 
   * @author Tri
   * 
   */
  public enum ValueType {
    /**
     * If FIXED type is used then the {@link SearchField} should only allows
     * user to selected from a list of possible search values.
     */
    FIXED,
    /**
     * If TEXT type is used then the {@link SearchField} can be any thing so a
     * {@link TextBox} should be used for accepted search input.
     */
    TEXT,
    /**
     * If DATE type is used then the {@link SearchField} should only accept date
     * time value.
     */
    DATE,
    /**
     * If NUMBER type is user then the {@link SearchField} would be a range of
     * numbers.
     */
    NUMBER;
  }

  /**
   * A SearchArea contains 2 opposite {@link SearchField}. If
   * {@link SearchField#type} is {@link ValueType#FIXED} or
   * {@link ValueType#TEXT} then the first field is using = or like operator and
   * the second field is != or !like operator. If it is {@link ValueType#DATE}
   * then the first field is before field which using >= operator, and the
   * second field is after which is using <= operator.
   * 
   * Note: !like operator is not a real SQL operator. In the server !like is
   * convert to propriated SQL operator.
   * 
   * @author tri
   * 
   */
  private class SearchArea extends Composite {
    /**
     * The name label for this SearchArea (i.e "Search in Darwin Core").
     */
    private final HTML searchLabel;
    /**
     * The first {@link SearchField} from the left.
     */
    private final SearchField normalField;
    /**
     * The opposite {@link SearchField} of {@link normalField}.
     */
    private SearchField negateField = null;
    /**
     * The {@link ASearchType} of this search area contains information to
     * communicated with the server.
     */
    private final ASearchType type;

    /**
     * Construct a SearchArea with a {@link ASearchType}.
     * 
     * @param type {@link ASearchType} for constructing this SearchArea.
     */
    public SearchArea(ASearchType type) {
      String clientName = type.getClientName();
      String databaseName = type.getDatabaseField();
      this.type = type;
      HorizontalPanel labelHp = new HorizontalPanel();
      if (!type.getLink().equals("")) {
        searchLabel = new HTML(constants.SearchIn());
        searchLabel.setStyleName("SearchLabel");
        HTML clientLink = new HTML("<a href='" + type.getLink()
                + "' target='_blank'>" + clientName + "</a>");
        clientLink.setStyleName("link");
        clientLink.addStyleName("SearchLabel");
        labelHp.add(searchLabel);
        labelHp.add(clientLink);
      } else {
        searchLabel = new HTML(constants.SearchIn() + " " + clientName);
        searchLabel.setStyleName("SearchLabel");
        labelHp.add(searchLabel);
      }
      labelHp.setSpacing(5);

      normalField = new SearchField(type, clientName, databaseName, false);
      HorizontalPanel mainHp = new HorizontalPanel();
      mainHp.add(normalField);
      if (type.getType() != ValueType.NUMBER
              && type.getType() != ValueType.DATE) {
        negateField = new SearchField(type, clientName, databaseName, true);
        mainHp.add(negateField);
      }

      mainHp.setSpacing(5);

      VerticalPanel areaVp = new VerticalPanel();
      areaVp.add(labelHp);
      areaVp.add(mainHp);
      areaVp.setStyleName("SearchArea");

      initWidget(areaVp);
    }

    /**
     * Gets database field representation of this {@link SearchArea}.
     * 
     * @return database field representation as String.
     */
    public String getDatabaseField() {
      return type.getDatabaseField();
    }

    /**
     * Gets the operator of the second {@link SearchField} from the left.
     * 
     * @return the operator of the second SearchField as String.
     */
    public String getNegateOp() {
      if (negateField == null) {
        return "";
      } else {
        return negateField.getOperator();
      }
    }

    /**
     * Gets the query of the second {@link SearchField} from the left.
     * 
     * @return empty if {@link #negateField} is empty, return databaseField +
     * space + operator + space + searchValue otherwise.
     */
    public String getNegateQuery() {
      String searchValue = negateField.getSearchValue();
      if (!negateField.getOperator().equals("!empty")
              && searchValue.trim().equals("")) {
        return "";
      }
      return type.getDatabaseField() + " " + negateField.getOperator() + " "
              + searchValue;
    }

    /**
     * Gets the search value of the second {@link SearchField} from the left.
     * 
     * @return the search value of the second SearchField
     */
    public String getNegateSearch() {
      if (negateField == null) {
        return "";
      } else {
        return negateField.getSearchValue();
      }
    }

    /**
     * Gets the operator of the first {@link SearchField} from the left.
     * 
     * @return the operator of the first SearchField as String.
     */
    public String getNormalOp() {
      return normalField.getOperator();
    }

    /**
     * Gets the query of the first {@link SearchField} from the left.
     * 
     * @return empty if {@link #normalField} is empty, return databaseField +
     * space + operator + space + searchValue otherwise.
     */
    public String getNormalQuery() {
      String searchValue = normalField.getSearchValue();
      if (!normalField.getOperator().equals("empty")
              && searchValue.trim().equals("")) {
        return "";
      }
      return type.getDatabaseField() + " " + normalField.getOperator() + " "
              + searchValue;
    }

    /**
     * Gets the search value of the first {@link SearchField} from the left.
     * 
     * @return the search value of the first SearchField
     */
    public String getNormalSearch() {
      return normalField.getSearchValue();
    }

    /**
     * Gets the {@link ASearchType} is used to construct this SearchArea.
     * 
     * @return this {@link ASearchType}
     */
    public ASearchType getType() {
      return type;
    }

    /**
     * Sets the focus of this SearchArea to be the first {@link SearchField}.
     * 
     * @param focus true if focused.
     */
    public void setFocus(boolean focus) {
      normalField.setFocus(focus);
    }

  }

  /**
   * A SearchField is a dynamic search interface base on {@link ValueType}. If
   * the {@link #type} is {@link ValueType#FIXED} then the search input area is
   * a {@link ListBox} of all its possible values with the operator is =. If it
   * is {@link ValueType#TEXT} then the search input area is a TextBox with the
   * operator is whether =, like, !like, or !=. If it is {@link ValueType#DATE}
   * then the search input area is a {@link TextBox} with the operator is
   * whether >= or <=.
   * 
   * @author Tri
   * 
   */
  private class SearchField extends Composite {
    /**
     * The search input box for {@link ValueType#TEXT} and
     * {@link ValueType#DATE}.
     */
    private TextBox searchTextBox;
    /**
     * The search input box for {@link ValueType#FIXED}.
     */
    private ListBox searchListBox;

    private String radioButtonGroup;

    private RadioButton startsWith;
    private RadioButton exact;
    private RadioButton contains;
    private RadioButton in;
    private RadioButton empty;

    private RadioButton equals;
    private RadioButton notEquals;
    private RadioButton lessThan;
    private RadioButton moreThan;
    private RadioButton lessThanOrEquals;
    private RadioButton moreThanOrEquals;
    /**
     * True to use "!" operator or <= operator if {@link ValueType#DATE}.
     */
    private final boolean notOperator;
    /**
     * The {@link ValueType} for this SearchField.
     */
    private final ValueType type;

    /**
     * Construct a SearchField with a {@link ASearchType}, a human readable name
     * for this field and its database representation, and a boolean is
     * determine whether the operator should be; between != or =, !like or like,
     * and >= or <=.
     * 
     * @param type {@link ASearchType} for this field.
     * @param clientName human readable name.
     * @param databaseField the database representation of the clientName.
     * @param notOperator true if ! or <= is used.
     */
    public SearchField(ASearchType type, String clientName,
            String databaseField, boolean notOperator) {
      HorizontalPanel mainHp = new HorizontalPanel();
      HTML fieldLabel = new HTML();
      HTML tipLabel = new HTML();
      tipLabel.setStyleName("Tip");
      fieldLabel.setWidth("60px");
      fieldLabel.setStyleName("Label");
      mainHp.add(fieldLabel);
      initWidget(mainHp);
      mainHp.setSpacing(2);
      mainHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      setStyleName("SearchField");
      this.type = type.getType();
      this.notOperator = notOperator;

      switch (type.getType()) {
      case FIXED:
        searchListBox = new ListBox();
        searchListBox.setStyleName("ListBox");
        searchListBox.addItem(constants.Select() + " " + clientName + "...",
                databaseField);
        searchTextBox = null;
        empty = null;
        for (String value : type.getFixedValues()) {
          searchListBox.addItem(value);
        }
        if (notOperator) {
          fieldLabel.setHTML((constants.Exclude()));
        } else {
          fieldLabel.setHTML((constants.Include()));
        }
        mainHp.add(searchListBox);
        break;
      case TEXT:
        VerticalPanel vp = new VerticalPanel();
        searchTextBox = new TextBox();
        HorizontalPanel radioHp = new HorizontalPanel();
        radioButtonGroup = System.currentTimeMillis() + "";
        startsWith = new RadioButton(radioButtonGroup, constants.StartsWith());
        startsWith.setFormValue("sw");
        startsWith.setValue(true);
        exact = new RadioButton(radioButtonGroup, constants.ExactMatch());
        exact.setFormValue("=");
        contains = new RadioButton(radioButtonGroup, constants.Contains());
        in = new RadioButton(radioButtonGroup, "in");
        empty = new RadioButton(radioButtonGroup, constants.Empty());
        empty.setFormValue("empty");
        in.setFormValue("in");
        contains.setFormValue("like");
        // radioHp.add(new Label(constants.StartsWith()));
        radioHp.add(startsWith);
        // radioHp.add(new Label(constants.ExactMatch()));
        radioHp.add(exact);
        // radioHp.add(new Label(constants.Contains()));
        radioHp.add(contains);
        radioHp.add(in);
        radioHp.add(empty);
        searchListBox = null;
        searchTextBox.setStyleName("Search-Text");
        if (notOperator) {
          fieldLabel.setHTML((constants.Exclude()));
        } else {
          fieldLabel.setHTML((constants.Include()));
        }
        vp.add(searchTextBox);
        vp.add(radioHp);
        mainHp.add(vp);
        // mainHp.add(exactCheckBox);
        break;
      case DATE:
        HorizontalPanel radioDateHp = new HorizontalPanel();
        radioButtonGroup = System.currentTimeMillis() + "";
        equals = new RadioButton(radioButtonGroup, "=");
        equals.setFormValue("=");
        notEquals = new RadioButton(radioButtonGroup, "!=");
        notEquals.setFormValue("!=");
        lessThan = new RadioButton(radioButtonGroup, "<");
        lessThan.setFormValue("<");
        moreThan = new RadioButton(radioButtonGroup, ">");
        moreThan.setFormValue(">");
        lessThanOrEquals = new RadioButton(radioButtonGroup, "<=");
        lessThanOrEquals.setFormValue("<=");
        moreThanOrEquals = new RadioButton(radioButtonGroup, ">=");
        moreThanOrEquals.setFormValue(">=");
        in = new RadioButton(radioButtonGroup, "in");
        in.setFormValue("in");
        empty = new RadioButton(radioButtonGroup, constants.Empty());
        empty.setFormValue("empty");
        radioDateHp.add(equals);
        radioDateHp.add(notEquals);
        radioDateHp.add(lessThan);
        radioDateHp.add(moreThan);
        radioDateHp.add(lessThanOrEquals);
        radioDateHp.add(moreThanOrEquals);
        radioDateHp.add(in);
        radioDateHp.add(empty);
        equals.setValue(true);
        searchTextBox = new TextBox();
        tipLabel.setText("YYYY-MM-DD [HH:MM:SS]");
        searchTextBox.setStyleName("Search-Text");
        searchListBox = null;
        fieldLabel.setHTML("");
        VerticalPanel searchPanel = new VerticalPanel();
        searchPanel.add(searchTextBox);
        searchPanel.add(tipLabel);
        searchPanel.add(radioDateHp);
        mainHp.add(searchPanel);
        break;
      case NUMBER:
        HorizontalPanel radioNumHp = new HorizontalPanel();
        radioButtonGroup = System.currentTimeMillis() + "";
        equals = new RadioButton(radioButtonGroup, "=");
        equals.setFormValue("=");
        notEquals = new RadioButton(radioButtonGroup, "!=");
        notEquals.setFormValue("!=");
        lessThan = new RadioButton(radioButtonGroup, "<");
        lessThan.setFormValue("<");
        moreThan = new RadioButton(radioButtonGroup, ">");
        moreThan.setFormValue(">");
        lessThanOrEquals = new RadioButton(radioButtonGroup, "<=");
        lessThanOrEquals.setFormValue("<=");
        moreThanOrEquals = new RadioButton(radioButtonGroup, ">=");
        moreThanOrEquals.setFormValue(">=");
        in = new RadioButton(radioButtonGroup, "in");
        in.setFormValue("in");
        radioNumHp.add(equals);
        radioNumHp.add(notEquals);
        radioNumHp.add(lessThan);
        radioNumHp.add(moreThan);
        radioNumHp.add(lessThanOrEquals);
        radioNumHp.add(moreThanOrEquals);
        radioNumHp.add(in);
        equals.setValue(true);
        searchTextBox = new TextBox();
        searchListBox = null;
        searchTextBox.setStyleName("Search-Text");
        fieldLabel.setHTML("");
        VerticalPanel numVp = new VerticalPanel();
        numVp.add(searchTextBox);
        numVp.add(radioNumHp);
        mainHp.add(numVp);
        break;
      }
    }

    /**
     * Gets this SearchField operator.
     * 
     * If {@link #notOperator} is true:<br>
     * != is returned for {@link ValueType#FIXED} and {@link ValueType#TEXT} if
     * exactChecBox is checked. <br>
     * !like is return for {@link ValueType#TEXT} if {@link #exactCheckBox} is
     * unchecked. <br>
     * >= is returned for {@link ValueType#DATE}.
     * <p>
     * 
     * if {@link #notOperator} is false: <br>
     * = is returned for {@link ValueType#FIXED} and {@link ValueType#TEXT} if
     * exactChecBox is checked. <br>
     * like is return for {@link ValueType#TEXT} if {@link #exactCheckBox} is
     * unchecked. <br>
     * <= is returned for {@link ValueType#DATE}.
     * 
     * 
     * @return proriated operator of this field base on the given type.
     */
    public String getOperator() {
      switch (type) {
      case FIXED:
        return notOperator ? "!=" : "=";
      case TEXT:
        String op = "";
        if (startsWith.getValue()) {
          op = startsWith.getFormValue();
        } else if (exact.getValue()) {
          op = exact.getFormValue();
        } else if (contains.getValue()) {
          op = contains.getFormValue();
        } else if (in.getValue()) {
          op = in.getFormValue();
        } else if (empty.getValue()) {
          op = empty.getFormValue();
        } else {
          return "";
        }
        return notOperator ? ("!" + op) : op;
      case DATE:
        if (equals.getValue()) {
          return equals.getFormValue();
        } else if (notEquals.getValue()) {
          return notEquals.getFormValue();
        } else if (lessThan.getValue()) {
          return lessThan.getFormValue();
        } else if (lessThanOrEquals.getValue()) {
          return lessThanOrEquals.getFormValue();
        } else if (moreThan.getValue()) {
          return moreThan.getFormValue();
        } else if (moreThanOrEquals.getValue()) {
          return moreThanOrEquals.getFormValue();
        } else if (in.getValue()) {
          return in.getFormValue();
        } else if (empty.getValue()) {
          return empty.getFormValue();
        } else {
          return "";
        }
      case NUMBER:
        if (equals.getValue()) {
          return equals.getFormValue();
        } else if (notEquals.getValue()) {
          return notEquals.getFormValue();
        } else if (lessThan.getValue()) {
          return lessThan.getFormValue();
        } else if (lessThanOrEquals.getValue()) {
          return lessThanOrEquals.getFormValue();
        } else if (moreThan.getValue()) {
          return moreThan.getFormValue();
        } else if (moreThanOrEquals.getValue()) {
          return moreThanOrEquals.getFormValue();
        } else if (in.getValue()) {
          return in.getFormValue();
        } else {
          return "";
        }
      }
      return null;
    }

    /**
     * Gets the proriated search value from whether the {@link #searchTextBox}
     * or {@link #searchListBox} base on {@link #type}.
     * 
     * @return the search value as String
     */
    public String getSearchValue() {
      switch (type) {
      case FIXED:
        int selectedIndex = searchListBox.getSelectedIndex();
        if (selectedIndex <= 0) {
          return "";
        }
        return searchListBox.getItemText(searchListBox.getSelectedIndex());
      case TEXT:
      case DATE:
      case NUMBER:
        return searchTextBox.getText();
      }
      return "";
    }

    /**
     * Restores {@link #exactCheckBox} state from the given operator.
     * 
     * @param operator
     */
    public void restoreCheckedFromOperator(String operator) {
      if (operator.equalsIgnoreCase(startsWith.getFormValue())) {
        startsWith.setValue(true);
      } else if (operator.equalsIgnoreCase(exact.getFormValue())) {
        exact.setValue(true);
      } else if (operator.equals(contains.getFormValue())) {
        contains.setValue(true);
      }
    }

    /**
     * Sets to this SearchField if focused is true.
     * 
     * @param focused true if the SearchField should be focused.
     */
    public void setFocus(boolean focused) {
      switch (type) {
      case FIXED:
        searchListBox.setFocus(focused);
        break;
      case TEXT:
      case DATE:
      case NUMBER:
        searchTextBox.setFocus(focused);
        break;
      }

    }

    /**
     * Sets value on the propriated search input box base on {@link #type}.
     * 
     * @param value
     */
    public void setValue(String value) {
      switch (type) {
      case FIXED:
        for (int i = 0; i < searchListBox.getItemCount(); i++) {
          if (searchListBox.getItemText(i).equalsIgnoreCase(value)) {
            searchListBox.setSelectedIndex(i);
            return;
          }
        }
        searchListBox.setSelectedIndex(0);
        break;
      case TEXT:
      case DATE:
      case NUMBER:
        searchTextBox.setValue(value);
        break;
      }
    }

  }

  /**
   * The column index for counter column in the {@link #queriesTable}.
   */
  private static final int COUNTER_COL = 0;
  /**
   * The column index for human readable search field column in the
   * {@link #queriesTable}
   */
  private static final int CLIENT_NAME_COL = 1;
  /**
   * The column index for operator column in the {@link #queriesTable}
   */
  private static final int OPERATOR_COL = 2;
  /**
   * The column index for search value column in the {@link #queriesTable}
   */
  private static final int SEARCH_VALUE_COL = 3;
  /**
   * The column index for remove img column in the {@link #queriesTable}
   */
  private static final int REMOVE_COL = 4;

  private static final int START_SEARCH_FIELD_INDEX = 2;

  /**
   * Initialize AdvanceSearchView if it not already initialized and return its
   * instance.
   * 
   * @param parent the parent {@link View} of the {@link AdvanceSearchView}
   * @param clickable TODO
   * @return the View instance represent the AdvanceSearchView
   */
  public static ViewInfo init(final View parent, final Clickable clickable) {
    return new ViewInfo() {

      protected View constructView() {
        return new AdvanceSearchView(parent, clickable);
      }

      protected String getHisTokenName() {
        return ADVANCE;
      }

      protected String getName() {
        return ADVANCE;
      }

    };
  }

  /**
   * A previous view name that because switch to this view.
   */
  private String previousViewName;
  /**
   * A {@link VeritcalPanel} contains all {@link SearchArea}.
   */
  private final VerticalPanel searchAreasVp = new VerticalPanel();
  /**
   * A {@link ScrollPanel} for this view to control where scroll bar should
   * appear.
   */
  private final ScrollPanel mainSp = new ScrollPanel();
  /**
   * A {@link TextBox} to edit search value in {@link #queriesTable}.
   */
  private final TextBox editTextBox = new TextBox();

  private final HistoryState historyState = new HistoryState() {

    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case VIEW:
        return stringValue(param);
      case ASEARCH:
        return listValue(param);
      }
      return "";
    }

  };
  /**
   * The {@link SearchFieldSuggestion} to suggest and control user selected
   * SearchArea input.
   */
  private final SearchFieldSuggestion searchFieldSuggestionBox = new SearchFieldSuggestion();

  /**
   * The {@link Button} to to add current search criteria into
   * {@link #queriesTable}.
   */
  private final Button addSearchCriteriaButton = new Button(constants
          .AddSearchCriteria());

  /**
   * A {@link Grid} table contains all current search criteria when search
   * button is clicked.
   */
  private final Grid queriesTable = new Grid(0, 6);

  /**
   * The {@link Button} for clear the {@link #queriesTable}.
   */
  private final Button clearQueriesButton = new Button(constants
          .ClearCriteria());
  private final Button searchButton = new Button(constants.Search());

  /**
   * A {@link Map} from field search to its {@link ASearchType}.
   */
  private final Map<String, ASearchType> fieldSearchTypesMap = new HashMap<String, ASearchType>();

  /**
   * The reference to currently editing search value row.
   */
  private HTML editingSearchValue = null;
  private List<String> defaultSuggestionList = null;

  private final HTML tableLabel = new HTML(constants.SearchQueries());

  private final Clickable clickable;

  /**
   * Construct a {@link AdvanceSearchView} with given parent {@link View}.
   * 
   * @param parent the View display this View.
   * @param query TODO
   */
  private AdvanceSearchView(View parent, Clickable clickable) {
    super(parent, false);
    this.clickable = clickable;
    HorizontalPanel mainHp = new HorizontalPanel();
    VerticalPanel vp = new VerticalPanel();
    tableLabel.setStyleName("Label");
    vp.add(tableLabel);
    vp.add(queriesTable);
    HorizontalPanel buttonHp = new HorizontalPanel();
    buttonHp.setSpacing(5);
    buttonHp.add(searchButton);
    buttonHp.add(clearQueriesButton);
    vp.add(buttonHp);
    vp.setCellHorizontalAlignment(tableLabel,
            HasHorizontalAlignment.ALIGN_CENTER);
    queriesTable.setStyleName("QueriesTable");
    mainSp.setWidget(mainHp);
    initWidget(mainSp);
    mainHp.add(searchAreasVp);
    mainHp.add(vp);
    mainHp.setStyleName("AdvanceSearch");
    queriesTable.setCellPadding(2);
    queriesTable.setCellSpacing(0);
    queriesTable.setBorderWidth(0);
    clearQueriesButton.setVisible(false);
    searchButton.setVisible(false);
    tableLabel.setVisible(false);
    clearQueriesButton.addClickHandler(this);
    searchButton.addClickHandler(this);
    HorizontalPanel hp = new HorizontalPanel();
    HTML groupLb = new HTML(constants.AddSearchField());
    groupLb.setStyleName("Label");
    hp.add(groupLb);
    hp.add(searchFieldSuggestionBox);
    hp.setSpacing(5);

    HTML instructionLb = new HTML(constants.AdvanceSearchInstruction());
    instructionLb.setStyleName("Label");
    searchAreasVp.add(instructionLb);
    searchAreasVp.add(hp);
    searchAreasVp.add(addSearchCriteriaButton);
    searchAreasVp.setWidth("500px");
    searchAreasVp.setSpacing(10);
    searchFieldSuggestionBox.addTermSelectionListener(this);
    addSearchCriteriaButton.setVisible(false);
    addSearchCriteriaButton.addClickHandler(this);
    addSearchCriteriaButton.addStyleName("AddButton");
    queriesTable.addClickHandler(this);

    editTextBox.addKeyUpHandler(this);
    editTextBox.setStyleName("EditBox");
    editTextBox.addBlurHandler(this);
    // DOM.sinkEvents(queriesTable.getElement(), Event.MOUSEEVENTS);
    // DOM.setEventListener(queriesTable.getElement(), this);
    historyState.setHistoryToken(History.getToken());

  }

  /**
   * Adds all the search {@link ASearchType#clientName} and padding its search
   * group in the front to the {@link #searchFieldSuggestionBox}.
   * 
   * @param searchGroup the search group for the given asearchType
   * @param asearchTypes the list of {@link ASearchType} for the given search
   * group.
   * @param isDefault true to show the default suggestion if nothing is type.
   */
  public void addSearchFields(String searchGroup,
          List<ASearchType> asearchTypes, boolean isDefault) {
    if (isDefault) {
      defaultSuggestionList = new ArrayList<String>();
    }
    String displayMsg = constants.SearchBy();
    if (searchGroup == null) {
      displayMsg += "...";
    } else {
      displayMsg += " " + searchGroup;
    }

    for (ASearchType asearchType : asearchTypes) {
      String clientName = asearchType.getClientName();
      String term = searchGroup + " - " + clientName;
      if (isDefault) {
        defaultSuggestionList.add(term);
      }
      searchFieldSuggestionBox.addSearchTerm(term);
      fieldSearchTypesMap.put(term.toLowerCase(), asearchType);
      fieldSearchTypesMap.put(clientName.toLowerCase(), asearchType);
      fieldSearchTypesMap.put(asearchType.getDatabaseField().toLowerCase(),
              asearchType);
    }
    if (isDefault) {
      searchFieldSuggestionBox.setDeafaultSuggestions(defaultSuggestionList);
    }
  }

  /**
   * Clears the {@link #queriesTable}.
   */
  public void clearSearch() {
    clearQueriesButton.setVisible(false);
    searchButton.setVisible(false);
    tableLabel.setVisible(false);
    queriesTable.clear();
    queriesTable.resizeRows(0);
  }

  /**
   * Gets the previous view name which this view switch from.
   * 
   * @return previous view name as String
   */
  public String getPreviousViewName() {
    return previousViewName;
  }

  /**
   * Gets a {@link Set} of String represents search filters for this advance
   * search. Each filter is in the following format:<br>
   * database field name + space + operator + space + search value.
   * 
   * @return {@link Set} of String filter.
   */
  public Set<String> getSearchFilters() {
    Set<String> searchFilters = new HashSet<String>();
    for (int row = 0; row < queriesTable.getRowCount(); row++) {
      searchFilters.add(getRowFilter(row));
    }
    return searchFilters;
  }

  /**
   * Gets this view history token in the following format:<br>
   * asearch=searchQuery&asearch=searchQuery&...<br>
   * searchQuery=database field name + space + operator + space + search value.
   * 
   * @see org.rebioma.client.ComponentView#historyToken()
   */

  public String historyToken() {
    StringBuilder tokensBuilder = new StringBuilder();
    String asearchParam = UrlParam.ASEARCH.lower() + "=";
    for (int i = 0; i < queriesTable.getRowCount(); i++) {
      String clientName = queriesTable.getText(i, CLIENT_NAME_COL)
              .toLowerCase();
      String operator = queriesTable.getText(i, OPERATOR_COL);
      String searchValue = queriesTable.getText(i, SEARCH_VALUE_COL);
      String databaseField = fieldSearchTypesMap.get(clientName)
              .getDatabaseField();
      tokensBuilder.append("&" + asearchParam + databaseField + " " + operator
              + " " + searchValue);
    }
    return tokensBuilder.toString();
  }

  /**
   * When the {@link editTextBox} save the currently edit search value in
   * {@link #queriesTable}.
   */
  public void onBlur(BlurEvent event) {
    Object source = event.getSource();
    if (source == editTextBox) {
      saveCurrentEditingValue();
    }

  }

  public void onClick(ClickEvent event) {
    Object source = event.getSource();
    if (source == addSearchCriteriaButton) {
      Set<SearchArea> searchAreasToRemove = new HashSet<SearchArea>();
      for (int i = START_SEARCH_FIELD_INDEX; i < searchAreasVp
              .getWidgetIndex(addSearchCriteriaButton); i++) {
        SearchArea searchArea = (SearchArea) searchAreasVp.getWidget(i);
        searchAreasToRemove.add(searchArea);
        String normalSearch = searchArea.getNormalSearch();
        String negateSearch = searchArea.getNegateSearch();
        ASearchType searchType = searchArea.getType();
        if (searchArea.getNormalOp().equals("empty")
                || !normalSearch.equals("")) {
          addCriteria(searchType.getClientName(), searchArea.getNormalOp(),
                  normalSearch);
        }
        if (searchArea.getNegateOp().equals("!empty")
                || !negateSearch.equals("")) {
          addCriteria(searchType.getClientName(), searchArea.getNegateOp(),
                  negateSearch);
        }
      }
      addSearchCriteriaButton.setVisible(false);
      for (SearchArea searchArea : searchAreasToRemove) {
        searchAreasVp.remove(searchArea);
      }
      addHistoryItem(false);
    } else if (source == clearQueriesButton) {
      clearSearch();
      addHistoryItem(false);
    } else if (source == searchButton) {
      clickable.click();
    } else if (source == queriesTable) {
      Cell cell = queriesTable.getCellForEvent(event);
      if (cell == null) {
        return;
      }
      int row = cell.getRowIndex();
      int col = cell.getCellIndex();
      if (col == SEARCH_VALUE_COL) {
        // saveCurrentEditingValue();
        setEditSearchValue(row);
      } else if (col == REMOVE_COL) {
        removeCriteria(row);
        addHistoryItem(false);
      }
    }
  }

  public void onKeyUp(KeyUpEvent event) {
    Object source = event.getSource();
    int keyCode = event.getNativeKeyCode();
    if (source == editTextBox) {
      if (keyCode == KeyCodes.KEY_ENTER) {
        saveCurrentEditingValue();
      }
    }

  }

  public void onMouseOut(MouseOutEvent event) {
    Object source = event.getSource();
    Widget widget = (Widget) source;
    widget.removeStyleName("hover");
    widget.addStyleName("out");
  }

  public void onMouseOver(MouseOverEvent event) {
    Object source = event.getSource();
    Widget widget = (Widget) source;
    widget.removeStyleName("out");
    widget.addStyleName("hover");
  }

  public void onTermSelected(String term) {
    addSearchArea(fieldSearchTypesMap.get(term.toLowerCase()));
  }

  public void setPreviousViewName(String previousViewName) {
    this.previousViewName = previousViewName;
  }

  protected void handleOnValueChange(String historyToken) {
    restoreFromHisToken(historyToken);
  }

  protected boolean isMyView(String value) {
    historyState.setHistoryToken(value);
    String view = historyState.getHistoryParameters(UrlParam.VIEW) + "";
    return view.equalsIgnoreCase(ADVANCE);
  }

  protected void resetToDefaultState() {

    restoreFromHisToken(History.getToken());

  }

  protected void resize(int width, int height) {
    height = height - mainSp.getAbsoluteTop();
    if (height < 0) {
      height = 1;
    }
    mainSp.setPixelSize(width, height);
  }

  /**
   * Adds a search query to the {@link #queriesTable}.
   * 
   * @param clientName
   * @param op
   * @param searchValue
   */
  private void addCriteria(String clientName, String op, String searchValue) {
    if (searchValue.equals("")) {
      searchValue = "&nbsp;";
    }
    for (int i = 0; i < queriesTable.getRowCount(); i++) {
      String clientNameCol = queriesTable.getText(i, CLIENT_NAME_COL);
      String opCol = queriesTable.getText(i, OPERATOR_COL);
      if (clientNameCol.equals(clientName) && opCol.equals(op)) {
        HTML searchValueHTML = (HTML) queriesTable.getWidget(i,
                SEARCH_VALUE_COL);
        searchValueHTML.setHTML(searchValue);
        return;
      }
    }
    int newRowIndex = queriesTable.getRowCount();
    queriesTable.insertRow(newRowIndex);
    queriesTable.setText(newRowIndex, COUNTER_COL, newRowIndex + 1 + ".");
    queriesTable.setText(newRowIndex, CLIENT_NAME_COL, clientName);
    queriesTable.setText(newRowIndex, OPERATOR_COL, op);
    HTML searchValueHTML = new HTML(searchValue);
    searchValueHTML.addStyleName("out");
    searchValueHTML.addMouseOutHandler(this);
    searchValueHTML.addMouseOverHandler(this);
    queriesTable.setWidget(newRowIndex, SEARCH_VALUE_COL, searchValueHTML);
    queriesTable.setHTML(newRowIndex, REMOVE_COL, "<img src='"
            + "images/redX.png" + "'/>");
    CellFormatter cellFormatter = queriesTable.getCellFormatter();
    cellFormatter.setVerticalAlignment(newRowIndex, REMOVE_COL,
            HasVerticalAlignment.ALIGN_MIDDLE);
    cellFormatter.setStyleName(newRowIndex, COUNTER_COL, "cell");
    cellFormatter.setStyleName(newRowIndex, CLIENT_NAME_COL, "cell");
    cellFormatter.setStyleName(newRowIndex, OPERATOR_COL, "cell");
    cellFormatter.setStyleName(newRowIndex, SEARCH_VALUE_COL, "cell");
    cellFormatter.setStyleName(newRowIndex, REMOVE_COL, "link cell");

    clearQueriesButton.setVisible(true);
    searchButton.setVisible(true);
    tableLabel.setVisible(true);
  }

  private void addSearchArea(ASearchType searchType) {
    searchAreasVp.insert(new SearchArea(searchType), searchAreasVp
            .getWidgetIndex(addSearchCriteriaButton));
    addSearchCriteriaButton.setVisible(true);
  }

  private String getRowFilter(int row) {
    String clientName = queriesTable.getText(row, CLIENT_NAME_COL)
            .toLowerCase();
    String operator = queriesTable.getText(row, OPERATOR_COL);
    String searchValue = queriesTable.getText(row, SEARCH_VALUE_COL);
    String databaseField = fieldSearchTypesMap.get(clientName)
            .getDatabaseField();
    return databaseField + " " + operator + " " + searchValue;
  }

  private int getSearchValueRow(Widget searchField) {
    for (int row = 0; row < queriesTable.getRowCount(); row++) {
      if (queriesTable.getWidget(row, SEARCH_VALUE_COL) == searchField) {
        return row;
      }
    }
    return -1;
  }

  private void removeCriteria(int rowIndex) {
    queriesTable.removeRow(rowIndex);
    for (int i = 0; i < queriesTable.getRowCount(); i++) {
      queriesTable.setText(i, 0, (i + 1) + ".");
    }
    if (queriesTable.getRowCount() == 0) {
      clearQueriesButton.setVisible(false);
      tableLabel.setVisible(false);
    }
  }

  @SuppressWarnings("unchecked")
  private void restoreFromHisToken(String token) {
    historyState.setHistoryToken(token);
    List<String> asearchValues = (List<String>) historyState
            .getHistoryParameters(UrlParam.ASEARCH);
    clearSearch();

    if (asearchValues == null || asearchValues.isEmpty()) {
      return;
    }
    for (String asearchValue : asearchValues) {
      String fieldValues[] = asearchValue.split(" ");
      int fieldIndex = 0;
      int fieldValuesLen = fieldValues.length;
      if (fieldValuesLen < 2) {
        continue;
      }
      String databaseName = "";
      String operator = "";
      String searchValue = "";
      while (fieldIndex != fieldValuesLen) {
        databaseName = fieldValues[fieldIndex];
        fieldIndex++;
        if (!databaseName.equals(" ")) {
          break;
        }
      }
      if (fieldIndex == fieldValuesLen) {
        continue;
      }
      while (fieldIndex != fieldValuesLen) {
        operator = fieldValues[fieldIndex];
        fieldIndex++;
        if (!operator.equals(" ")) {
          break;
        }
      }
      if (fieldIndex == fieldValuesLen && !operator.equalsIgnoreCase("empty")
              && !operator.equalsIgnoreCase("!empty")) {
        continue;
      }

      while (fieldIndex != fieldValuesLen) {
        searchValue += fieldValues[fieldIndex] + " ";
        fieldIndex++;
      }
      ASearchType selectedType = fieldSearchTypesMap.get(databaseName
              .toLowerCase());
      String clientName = selectedType.getClientName();
      addCriteria(clientName, operator, searchValue.trim());
    }
  }

  private void saveCurrentEditingValue() {
    if (editingSearchValue != null) {
      String newSearchValue = editTextBox.getText();
      int editingRow = getSearchValueRow(editTextBox);
      String operator = queriesTable.getHTML(editingRow, OPERATOR_COL);
      if (!operator.equalsIgnoreCase("empty")
              && !operator.equalsIgnoreCase("!empty")
              && newSearchValue.trim().equals("")) {
        removeCriteria(editingRow);
        addHistoryItem(false);
      } else {
        boolean isNew = !editingSearchValue.equals(newSearchValue);
        editingSearchValue.setText(newSearchValue);
        editingSearchValue.removeStyleName("hover");
        editingSearchValue.addStyleName("out");
        queriesTable
                .setWidget(editingRow, SEARCH_VALUE_COL, editingSearchValue);
        editingSearchValue = null;
        if (isNew) {
          addHistoryItem(false);
        }
      }
    }
  }

  private void setEditSearchValue(int row) {
    Widget widget = queriesTable.getWidget(row, SEARCH_VALUE_COL);
    if (widget != editTextBox) {
      editingSearchValue = (HTML) widget;
      editTextBox.setText(editingSearchValue.getText());
      queriesTable.setWidget(row, SEARCH_VALUE_COL, editTextBox);
      editTextBox.setFocus(true);
      editTextBox.selectAll();
    }
  }
}