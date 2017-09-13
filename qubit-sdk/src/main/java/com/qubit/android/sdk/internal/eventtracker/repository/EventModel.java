package com.qubit.android.sdk.internal.eventtracker.repository;

public class EventModel {

  private Long id;
  private String globalId;
  private String type;
  private String eventBody;
  private boolean wasTriedToSend;
  private long creationTimestamp;

  private Long contextViewNumber;
  private Long contextSessionNumber;
  private Long contextSessionViewNumber;
  private Long contextViewTimestamp;
  private Long contextSessionTimestamp;

  public EventModel() {
  }

  public EventModel(String globalId, String type, String eventBody, long creationTimestamp) {
    this.globalId = globalId;
    this.type = type;
    this.eventBody = eventBody;
    this.creationTimestamp = creationTimestamp;
  }

  public EventModel(Long id, String globalId, String type,
                    String eventBody, boolean wasTriedToSend, long creationTimestamp) {
    this.id = id;
    this.globalId = globalId;
    this.type = type;
    this.eventBody = eventBody;
    this.wasTriedToSend = wasTriedToSend;
    this.creationTimestamp = creationTimestamp;
  }

  public EventModel(Long id, String globalId, String type, String eventBody, boolean wasTriedToSend,
                    long creationTimestamp,
                    Long contextViewNumber, Long contextSessionNumber, Long contextSessionViewNumber,
                    Long contextViewTimestamp, Long contextSessionTimestamp) {
    this.id = id;
    this.globalId = globalId;
    this.type = type;
    this.eventBody = eventBody;
    this.wasTriedToSend = wasTriedToSend;
    this.creationTimestamp = creationTimestamp;
    this.contextViewNumber = contextViewNumber;
    this.contextSessionNumber = contextSessionNumber;
    this.contextSessionViewNumber = contextSessionViewNumber;
    this.contextViewTimestamp = contextViewTimestamp;
    this.contextSessionTimestamp = contextSessionTimestamp;
  }

  public EventModel(EventModel source) {
    this(source.id, source.globalId, source.type, source.eventBody, source.wasTriedToSend,
        source.creationTimestamp,
        source.contextViewNumber, source.contextSessionNumber, source.contextSessionViewNumber,
        source.contextViewTimestamp, source.contextSessionTimestamp);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getEventBody() {
    return eventBody;
  }

  public void setEventBody(String eventBody) {
    this.eventBody = eventBody;
  }

  public void setWasTriedToSend(boolean wasTriedToSend) {
    this.wasTriedToSend = wasTriedToSend;
  }

  public long getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(long creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public String getGlobalId() {
    return globalId;
  }

  public void setGlobalId(String globalId) {
    this.globalId = globalId;
  }

  public boolean getWasTriedToSend() {
    return this.wasTriedToSend;
  }

  public Long getContextViewNumber() {
    return contextViewNumber;
  }

  public void setContextViewNumber(Long contextViewNumber) {
    this.contextViewNumber = contextViewNumber;
  }

  public Long getContextSessionNumber() {
    return contextSessionNumber;
  }

  public void setContextSessionNumber(Long contextSessionNumber) {
    this.contextSessionNumber = contextSessionNumber;
  }

  public Long getContextSessionViewNumber() {
    return contextSessionViewNumber;
  }

  public void setContextSessionViewNumber(Long contextSessionViewNumber) {
    this.contextSessionViewNumber = contextSessionViewNumber;
  }

  public Long getContextViewTimestamp() {
    return contextViewTimestamp;
  }

  public void setContextViewTimestamp(Long contextViewTimestamp) {
    this.contextViewTimestamp = contextViewTimestamp;
  }

  public Long getContextSessionTimestamp() {
    return contextSessionTimestamp;
  }

  public void setContextSessionTimestamp(Long contextSessionTimestamp) {
    this.contextSessionTimestamp = contextSessionTimestamp;
  }

}
