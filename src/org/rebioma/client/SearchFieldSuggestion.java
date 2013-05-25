package org.rebioma.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * A {@link SuggestBox} wrapper to limit the user input key to available term
 * only.
 * 
 * @author Tri
 * 
 */
public class SearchFieldSuggestion extends Composite implements
        SelectionHandler<Suggestion>, KeyDownHandler, ClickHandler,
        KeyUpHandler {
  /**
   * A listener is used to notified a term is selected.
   * 
   * @author Tri
   * 
   */
  public interface TermSelectionListener {
    void onTermSelected(String term);
  }

  private final SuggestBox suggestionBox;
  private final MultiWordSuggestOracle suggestOracle;
  private final List<TermSelectionListener> termSelectionListeners = new ArrayList<TermSelectionListener>();

  public SearchFieldSuggestion() {
    suggestOracle = new MultiWordSuggestOracle();
    suggestionBox = new SuggestBox(suggestOracle);
    suggestionBox.addSelectionHandler(this);
    suggestionBox.addKeyDownHandler(this);
    suggestionBox.getTextBox().addClickHandler(this);
    suggestionBox.addKeyUpHandler(this);
    suggestionBox.setWidth("200px");
    initWidget(suggestionBox);
    suggestionBox.setStyleName("SearchSuggestion");
    suggestionBox.setPopupStyleName("gwt-SuggestBoxPopup SuggestFields");
  }

  public void addSearchTerm(String terms) {
    suggestOracle.add(terms);
  }

  public void addSearchTerms(Collection<String> term) {
    suggestOracle.addAll(term);
  }

  public void addTermSelectionListener(TermSelectionListener e) {
    termSelectionListeners.add(e);
  }

  public void onClick(ClickEvent event) {
    // suggestionBox.setText("");
  }

  /**
   * Cancel the current key if it the current words + current key don't get any
   * suggestion.
   */
  public void onKeyDown(KeyDownEvent event) {
    int keyCode = event.getNativeKeyCode();
    // no need to look ahead one step of suggestion phase if the key is
    // backspace, arrow, or any modifier key.
    if (isSpecialKeys(event)) {
      return;
    }
    suggestOracle.requestSuggestions(new Request(suggestionBox.getText()
            + (char) keyCode), new Callback() {
      public void onSuggestionsReady(Request request, Response response) {
        int size = response.getSuggestions().size();
        if (size == 0) {
          suggestionBox.getTextBox().cancelKey();
        }
      }
    });
  }

  public void onKeyUp(KeyUpEvent event) {
    int keyCode = event.getNativeKeyCode();
    // no need to check for possible suggestion phases if the key is
    // backspace, arrow, or any modifier key.
    if (isSpecialKeys(event)) {
      return;
    }
    suggestOracle.requestSuggestions(new Request(suggestionBox.getText()),
            new Callback() {
              public void onSuggestionsReady(Request request, Response response) {
                Collection<Suggestion> suggestions = (Collection<Suggestion>) response
                        .getSuggestions();
                int size = suggestions.size();
                if (size == 1) {
                  suggestionBox.setText(suggestions.iterator().next()
                          .getReplacementString());
                }
              }
            });

  }

  public void onSelection(SelectionEvent<Suggestion> event) {
    for (TermSelectionListener listener : termSelectionListeners) {
      listener.onTermSelected(event.getSelectedItem().getReplacementString());
    }
    suggestionBox.setText("");
    Scheduler.get().scheduleDeferred(new ScheduledCommand(){
      public void execute() {
        suggestionBox.hideSuggestionList();

      }

    });
  }

  public void setDeafaultSuggestions(Collection<String> terms) {
    suggestOracle.setDefaultSuggestionsFromText(terms);
  }

  private boolean isSpecialKeys(KeyCodeEvent event) {
    int keyCode = event.getNativeKeyCode();
    return keyCode == KeyCodes.KEY_BACKSPACE || KeyCodeEvent.isArrow(keyCode)
            || event.isAnyModifierKeyDown() || keyCode == KeyCodes.KEY_ENTER
            || keyCode == KeyCodes.KEY_TAB || keyCode == KeyCodes.KEY_ESCAPE;
  }
}
