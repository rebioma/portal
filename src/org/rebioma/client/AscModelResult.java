package org.rebioma.client;

import java.util.List;

import org.rebioma.client.bean.AscModel;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AscModelResult implements IsSerializable {

	private List<AscModel> results;
	private int count;
	private List<AscModel> resultsM;
	private int countM;

  public AscModelResult() {

  }

  public AscModelResult(List<AscModel> results, int count) {
    super();
    this.results = results;
    this.count = count;
  }

  public AscModelResult(List<AscModel> results, int count, List<AscModel> resultsM, int countM) {
	this(results, count);
	this.resultsM = resultsM;
	this.countM = countM;
  }

  /**
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * @return the results
   */
  public List<AscModel> getResults() {
    return results;
  }

  /**
   * @param count the count to set
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * @param results the results to set
   */
  public void setResults(List<AscModel> results) {
    this.results = results;
  }
  
  public List<AscModel> getResultsM() {
	  return resultsM;
  }
	
  public void setResultsM(List<AscModel> resultsM) {
	  this.resultsM = resultsM;
  }
	
  public int getCountM() {
	  return countM;
  }
	
  public void setCountM(int countM) {
	  this.countM = countM;
  }
  
}