package org.rebioma.client;

import java.util.Set;

import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;

class ReviewerCommentPopup extends MultiLinePromptMessageBox {
  	
	  private CheckBox checkBox;
	  private boolean isAll;
	  private boolean review;
	  private boolean reviewed;
//	  private TextArea commentArea;
//	  private Label header;
//	  private Button submitButton;
//	  private VerticalPanel mainContainer;
	  private Set<Integer> occurrenceIds;
	  private ReviewHandler reviews;
	  private AppConstants constants;
	  
	  public ReviewerCommentPopup(ReviewHandler reviews, AppConstants constants) {
		  this("", DetailView.DEFAULT_COMMENT_TEXT, reviews, constants);
	  }
	  
	  public ReviewerCommentPopup(String title, String placeHolder, ReviewHandler reviews, AppConstants constants) {
	    	super(title, "");
	    	this.reviews = reviews;
	    	this.constants = constants;
	    	checkBox = new CheckBox();
//	    	getTextArea().setAllowBlank(false);
	    	getTextArea().setEmptyText(placeHolder);
	    	getTextArea().setHeight(70);
	    	getButtonById(PredefinedButton.OK.name()).setText(constants.Submit());
	    	getButtonById(PredefinedButton.OK.name()).disable();
	    	getButtonById(PredefinedButton.OK.name()).addSelectHandler(selectHandler);
	    	checkBox.setBoxLabel("Do you want to send an email right now?");
	    	contentAppearance.getContentElement(getElement()).appendChild(checkBox.getElement());
	    	getTextArea().addKeyUpHandler(new KeyUpHandler() {
				
				@Override
				public void onKeyUp(KeyUpEvent event) {
					getButtonById(PredefinedButton.OK.name()).setEnabled(!getTextArea().getText().trim().equals(""));
				}
			});
	    	setPixelSize(300, 170);
	  }
	    
	  public void display(boolean isAll, boolean reviewed, boolean review,
		        Set<Integer> occurrenceIds, String header) {
		  getButtonById(PredefinedButton.OK.name()).setText(constants.Submit());
		  setHeadingText(header);
		  checkBox.setValue(false);
		  setEnable(true);
		  this.isAll = isAll;
		  this.reviewed = reviewed;
		  this.review = review;
		  this.occurrenceIds = occurrenceIds;
		  super.show();
		  getTextArea().clear();
		  getTextArea().setHeight(70);
		  setPixelSize(300, 170);
	  }
	  
	  SelectHandler selectHandler = new SelectHandler() {

		  @Override
		  public void onSelect(SelectEvent event) {
			  if (review) {
				  String comment = getTextArea().getText().trim();
				  if(DetailView.DEFAULT_COMMENT_TEXT.equals(comment))
					  comment = "";
				  if (!comment.isEmpty() || Window.confirm(constants.NoCommentReview())) {
					  setEnable(false);
//					  comment += constants.commentLeftWhenReviewed();  
					  if (isAll) {
						  reviews.reviewAllRecords(reviewed, comment, checkBox.getValue());
					  } else {
						  reviews.reviewRecords(occurrenceIds, reviewed, comment, checkBox.getValue());
					  }
				  }
			  } else {
				  String comment = getTextArea().getText().trim();
				  if (!comment.isEmpty() || Window.confirm(constants.NoCommentReview())) {
					  setEnable(false);
					  if (isAll) {
//						  reviewAllRecords(reviewed, comment, checkBox.getValue());
					  } else {
						  reviews.commentRecords(occurrenceIds, comment, checkBox.getValue());
					  }
				  }
			  }
		  }
	      

	  };
	  
	  public void setEnable(boolean enabled) {
		  getButtonById(PredefinedButton.OK.name()).setEnabled(enabled);
	      if (enabled) {
	    	  getButtonById(PredefinedButton.OK.name()).setText(constants.Submit());
	      } else {
	    	  getButtonById(PredefinedButton.OK.name()).setText(constants.Submitting());
	      }
	    }
}