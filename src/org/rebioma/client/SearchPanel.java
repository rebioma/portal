/**
 * 
 */
package org.rebioma.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Mikajy
 *
 */
public class SearchPanel extends HorizontalPanel implements /*ClickHandler, */KeyUpHandler{
	
	private WatermarkedTextBox searchTextBox;
	private Button searchButton;
	private ClickHandler clickHander;

	public SearchPanel(ClickHandler clickHandler) {
		super();
		searchTextBox = new WatermarkedTextBox();
		searchTextBox.setWatermark("Search");
		searchButton = new Button();
		searchButton.setStylePrimaryName("rebioma-search");
//		searchButton.setText("Search");
		this.add(searchTextBox);
		this.add(searchButton);
		searchButton.addClickHandler(clickHandler);
		searchTextBox.addKeyUpHandler(this);
	}
	
	public void reset(){
		String watermark = this.searchTextBox.getWatermark();
		this.searchTextBox.setText("");
		this.searchTextBox.setWatermark(watermark);
	}
	
	public String getText(){
		String text = this.searchTextBox.getText();
		return text;
	}
	
	public void addButtonStyleName(String styleName){
		this.searchButton.addStyleName(styleName);
	}
	
	public void addTextBoxStyleName(String styleName){
		this.searchTextBox.addStyleName(styleName);
	}
	
	public void setWatermark(String hint){
		searchTextBox.setWatermark(hint);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		//Lancer la recherche quand on click sur la touche ENTRER du clavier
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			searchButton.click();
		}
		
	}

//	@Override
//	public void onClick(ClickEvent event) {
//		// TODO Auto-generated method stub
//		
//	}

	public Button getSearchButton() {
		return searchButton;
	}
	
}
