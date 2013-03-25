/**
 * 
 */
package org.rebioma.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Mikajy
 *
 */
public class PageSizeListBox extends ListBox implements ChangeHandler{
	
	private List<PageSizeChangeHandler> pageSizeChangeHandlers = new ArrayList<PageSizeChangeHandler>();
	
	private static final int DEFAULT_PAGE_SIZE = 10;
	
	public PageSizeListBox() {
		super();
		addChangeHandler(this);
	}

	public PageSizeListBox(boolean isMultipleSelect) {
		super(isMultipleSelect);
		addChangeHandler(this);
	}

	public PageSizeListBox(Element element) {
		super(element);
		addChangeHandler(this);
	}

	public void addPageChangeListener(PageSizeChangeHandler pageSizeChangeHandler){
		if(pageSizeChangeHandler != null && !pageSizeChangeHandlers.contains(pageSizeChangeHandler)){
			pageSizeChangeHandlers.add(pageSizeChangeHandler);
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Object source = event.getSource();
		if(source == this){//par mesure de sécurité, on fait ce teste
			int selectedIndex = this.getSelectedIndex();
			String value = this.getValue(selectedIndex);
			int pageSize = DEFAULT_PAGE_SIZE;
			try{
				pageSize = Integer.parseInt(value);
			}catch(NumberFormatException e){
				//on ne fait rien
			}
			for(PageSizeChangeHandler handler: pageSizeChangeHandlers){
				handler.onPageSizeChange(pageSize);
			}
		}
	}
}
