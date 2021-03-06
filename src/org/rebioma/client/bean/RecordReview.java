package org.rebioma.client.bean;

// Generated Mar 15, 2010 7:33:20 PM by Hibernate Tools 3.3.0.GA

import java.util.Date;

/**
 * RecordReview generated by hbm2java
 */
public class RecordReview implements java.io.Serializable {

  private Integer id;
  private Integer userId;
  private Integer occurrenceId;
  private Boolean reviewed;
  private Date reviewedDate;

  public RecordReview() {
  }

  public RecordReview(Integer userId, Integer occurrenceId, Date reviewedDate) {
    this.userId = userId;
    this.occurrenceId = occurrenceId;
    this.reviewedDate = reviewedDate;
  }

  public RecordReview(Integer userId, Integer occurrenceId, Boolean reviewed,
      Date reviewedDate) {
    this.userId = userId;
    this.occurrenceId = occurrenceId;
    this.reviewed = reviewed;
    this.reviewedDate = reviewedDate;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return this.userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getOccurrenceId() {
    return this.occurrenceId;
  }

  public void setOccurrenceId(Integer occurrenceId) {
    this.occurrenceId = occurrenceId;
  }

  public Boolean getReviewed() {
    return this.reviewed;
  }

  public void setReviewed(Boolean reviewed) {
    this.reviewed = reviewed;
  }

  public Date getReviewedDate() {
    return this.reviewedDate;
  }

  public void setReviewedDate(Date reviewedDate) {
    this.reviewedDate = reviewedDate;
  }

}
