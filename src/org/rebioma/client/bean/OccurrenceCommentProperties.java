package org.rebioma.client.bean;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
 
public interface OccurrenceCommentProperties extends PropertyAccess<OccurrenceCommentModel> {
  
	ModelKeyProvider<OccurrenceCommentModel> id();
 
	ValueProvider<OccurrenceCommentModel, String> email();
 
	ValueProvider<OccurrenceCommentModel, String> firstName();
 
	ValueProvider<OccurrenceCommentModel, String> lastName();
 
	ValueProvider<OccurrenceCommentModel, String> commentDetail();
}