package com.qubit.android.sdk.internal.eventtracker.repository;

public class EventModel {

  private Long id;
  private String globalId;
  private String type;
  private String eventBody;
  private boolean wasTriedToSend;
  private long creationTimestamp;

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
}
