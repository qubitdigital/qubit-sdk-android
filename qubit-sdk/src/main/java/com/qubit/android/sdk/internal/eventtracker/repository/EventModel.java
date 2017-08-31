package com.qubit.android.sdk.internal.eventtracker.repository;

public class EventModel {
  private long id;
  private String type;
  private String eventBody;
  private boolean wasTriedToSend;
  private long creationTimestamp;

  public EventModel(long id, String type, String eventBody, long creationTimestamp) {
    this.id = id;
    this.type = type;
    this.eventBody = eventBody;
    this.creationTimestamp = creationTimestamp;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public boolean isWasTriedToSend() {
    return wasTriedToSend;
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
}
