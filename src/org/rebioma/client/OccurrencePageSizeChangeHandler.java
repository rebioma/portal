package org.rebioma.client;
import org.rebioma.client.bean.Occurrence;


public interface OccurrencePageSizeChangeHandler extends PageSizeChangeHandler{
	
	 public DataPager<Occurrence> getDataPagerWidget();
	 
	 public OccurrencePagerWidget getOccurrencePagerWidget();
}
