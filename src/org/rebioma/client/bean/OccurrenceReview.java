package org.rebioma.client.bean;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OccurrenceReview implements IsSerializable {

  private String name;
  private String email;
  private Integer occurrenceId;
  private Boolean reviewed;
  private Date reviewedDate;

  public OccurrenceReview() {

  }

  public OccurrenceReview(String name, String email, Integer occurrenceId,
      Boolean reviewed, Date reviewedDate) {
    this.name = name;
    this.email = email;
    this.occurrenceId = occurrenceId;
    this.reviewed = reviewed;
    this.reviewedDate = reviewedDate;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @return the firstName
   */
  public String getName() {
    return name;
  }

  /**
   * @return the occurrenceId
   */
  public Integer getOccurrenceId() {
    return occurrenceId;
  }

  /**
   * @return the reviewed
   */
  public Boolean getReviewed() {
    return reviewed;
  }

  /**
   * @return the reviewedDate
   */
  public Date getReviewedDate() {
    return reviewedDate;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setName(String firstName) {
    this.name = firstName;
  }

  /**
   * @param occurrenceId the occurrenceId to set
   */
  public void setOccurrenceId(Integer occurrenceId) {
    this.occurrenceId = occurrenceId;
  }

  /**
   * @param reviewed the reviewed to set
   */
  public void setReviewed(Boolean reviewed) {
    this.reviewed = reviewed;
  }

  /**
   * @param reviewedDate the reviewedDate to set
   */
  public void setReviewedDate(Date reviewedDate) {
    this.reviewedDate = reviewedDate;
  }

}
