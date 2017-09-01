package com.qubit.android.sdk.internal.eventtracker.connector;

public class EventContext {

  private final String id;
  private long sessionNumber;
  private long sessionTs;
  private long sessionViewNumber;
  private long viewNumber;
  private long viewTs;

  public EventContext(String id) {
    this.id = id;
  }

  public void setSessionData(long sessionNumber, long sessionTs, long sessionViewNumber) {
    this.sessionNumber = sessionNumber;
    this.sessionTs = sessionTs;
    this.sessionViewNumber = sessionViewNumber;
  }

  public void setViewData(long viewNumber, long viewTs) {
    this.viewNumber = viewNumber;
    this.viewTs = viewTs;
  }

  public String getId() {
    return id;
  }

  public long getSessionNumber() {
    return sessionNumber;
  }

  public long getSessionTs() {
    return sessionTs;
  }

  public long getSessionViewNumber() {
    return sessionViewNumber;
  }

  public long getViewNumber() {
    return viewNumber;
  }

  public long getViewTs() {
    return viewTs;
  }

  public void setSessionNumber(long sessionNumber) {
    this.sessionNumber = sessionNumber;
  }

  public void setSessionTs(long sessionTs) {
    this.sessionTs = sessionTs;
  }

  public void setSessionViewNumber(long sessionViewNumber) {
    this.sessionViewNumber = sessionViewNumber;
  }

  public void setViewNumber(long viewNumber) {
    this.viewNumber = viewNumber;
  }

  public void setViewTs(long viewTs) {
    this.viewTs = viewTs;
  }
}
