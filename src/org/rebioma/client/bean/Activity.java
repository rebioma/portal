package org.rebioma.client.bean;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class Activity implements Serializable {
	
	/**
	 * 
	 */
	public interface ActivityProperties extends PropertyAccess<Activity> {

		@Path("id")
		ModelKeyProvider<Activity> key();

		ValueProvider<Activity, Long> occurrenceCount();

		ValueProvider<Activity, Boolean> action();

		ValueProvider<Activity, Date> date();

		ValueProvider<Activity, String> comment();

	}
	
	private String id;
	private Long occurrenceCount;
	private Boolean action;
	private Date date;
	private String comment;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getOccurrenceCount() {
		return occurrenceCount;
	}
	public void setOccurrenceCount(Long occurrenceCount) {
		this.occurrenceCount = occurrenceCount;
	}
	public Boolean getAction() {
		return action;
	}
	public void setAction(Boolean action) {
		this.action = action;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Activity(String id, Long occurrenceCount, Boolean action, Date date,
			String comment) {
		super();
		this.id = id;
		this.occurrenceCount = occurrenceCount;
		this.action = action;
		this.date = date;
		this.comment = comment;
	}
	public Activity() {
		super();
	}
	
	
}
