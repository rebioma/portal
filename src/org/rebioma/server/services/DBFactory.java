package org.rebioma.server.services;

public class DBFactory {

  private static UserDb userDb = null;
  private static RoleDb roleDb = null;
  private static OccurrenceDb occurrenceDb = null;
  private static AscDataDb ascDataDb = null;
  private static CollaboratorsDb collaboratorsDb = null;
  private static UpdateService updateService = null;
  private static SessionIdService sessionService = null;
  private static ValidationService validationService = null;
  private static OccurrenceCommentsService occurrenceCommentService = null;
  private static FileValidationServiceImpl fileValidationService = null;
  private static RecordReviewDb recordReviewDb;
  private static TaxonomicReviewerDb taxonomicReviewerDb;
  private static AscModelDb ascModelDb;

  public static AscDataDb getAscDataDb() {
    if (ascDataDb == null) {
      ascDataDb = new AscDataDbImpl();
    }
    return ascDataDb;
  }

  public static AscModelDb getAscModelDb() {
    if (ascModelDb == null) {
      ascModelDb = new AscModelDbImpl();
    }
    return ascModelDb;
  }

  public static CollaboratorsDb getCollaboratorsDb() {
    if (collaboratorsDb == null) {
      collaboratorsDb = new CollaboratorsDbImpl();
    }
    return collaboratorsDb;
  }

  public static FileValidationService getFileValidationService() {
    if (fileValidationService == null) {
      fileValidationService = new FileValidationServiceImpl();
    }
    return fileValidationService;
  }

  public static OccurrenceDb getOccurrenceDb() {
    if (occurrenceDb == null) {
      occurrenceDb = new OccurrenceDbImpl();
    }
    return occurrenceDb;
  }

  public static OccurrenceCommentsService getOccurrentCommentService() {
    if (occurrenceCommentService == null) {
      occurrenceCommentService = new OccurrenceCommentsServiceImpl();
    }
    return occurrenceCommentService;
  }

  public static RecordReviewDb getRecordReviewDb() {
    if (recordReviewDb == null) {
      recordReviewDb = new RecordReviewDbImpl();
    }
    return recordReviewDb;
  }

  public static RoleDb getRoleDb() {
    if (roleDb == null) {
      roleDb = new RoleDbImpl();
    }
    return roleDb;
  }

  public static SessionIdService getSessionIdService() {
    if (sessionService == null) {
      sessionService = new SessionIdServiceImpl();
    }
    return sessionService;
  }

  public static TaxonomicReviewerDb getTaxonomicReviewerDb() {
    if (taxonomicReviewerDb == null) {
      taxonomicReviewerDb = new TaxonomicReviewerDbImpl();
    }
    return taxonomicReviewerDb;
  }

  public static UpdateService getUpdateService() {
    if (updateService == null) {
      updateService = new UpdateServiceImpl();
    }
    return updateService;
  }

  public static UserDb getUserDb() {
    if (userDb == null) {
      userDb = new UserDbImpl();
    }
    return userDb;
  }

  public static ValidationService getValidationService() {
    if (validationService == null) {
      validationService = new ValidationServiceImpl();
    }
    return validationService;
  }

}
