package org.rebioma.client;

import java.util.List;

import org.rebioma.client.bean.AscModel;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AscModelResult implements IsSerializable {

  private List<AscModel> results;
  private int count;

  public AscModelResult() {

  }

  public AscModelResult(List<AscModel> results, int count) {
    super();
    this.results = results;
    this.count = count;
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

}
