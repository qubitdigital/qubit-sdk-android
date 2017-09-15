package com.qubit.android.sdk.internal.eventtracker.connector;

public class EventContext {

  private final String id;
  private final int sample;
  private Long sessionNumber;
  private Long sessionTs;
  private Long sessionViewNumber;
  private Long viewNumber;
  private Long viewTs;
  private Integer timezoneOffset;

  public EventContext(String id, int sample) {
    this.id = id;
    this.sample = sample;
  }

  public void setSessionData(Long sessionNumber, Long sessionTs, Long sessionViewNumber,
                             Long viewNumber, Long viewTs) {
    this.sessionNumber = sessionNumber;
    this.sessionTs = sessionTs;
    this.sessionViewNumber = sessionViewNumber;
    this.viewNumber = viewNumber;
    this.viewTs = viewTs;
  }

  public void setViewData(long viewNumber, long viewTs) {
    this.viewNumber = viewNumber;
    this.viewTs = viewTs;
  }

  public String getId() {
    return id;
  }

  public int getSample() {
    return sample;
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

  public Integer getTimezoneOffset() {
    return timezoneOffset;
  }

  public void setTimezoneOffset(Integer timezoneOffset) {
    this.timezoneOffset = timezoneOffset;
  }
}
