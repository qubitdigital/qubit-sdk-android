package com.qubit.android.sdk.internal.session.model;

import com.qubit.android.sdk.internal.session.SessionData;

public class SessionDataModel implements SessionData {

  private long sessionNumber;
  private long sessionTs;
  private long lastEventTs;
  private Long viewNumber;
  private Long sessionViewNumber;
  private Long viewTs;


  public SessionDataModel(long sessionNumber, long sessionTs, long lastEventTs) {
    this.sessionNumber = sessionNumber;
    this.sessionTs = sessionTs;
    this.lastEventTs = lastEventTs;
  }

  public SessionDataModel(long sessionNumber, long sessionTs, long lastEventTs, Long viewNumber, Long viewTs) {
    this.sessionNumber = sessionNumber;
    this.sessionTs = sessionTs;
    this.lastEventTs = lastEventTs;
    this.viewNumber = viewNumber;
    this.viewTs = viewTs;
  }

  public SessionDataModel(SessionData sessionData) {
    this.sessionNumber = sessionData.getSessionNumber();
    this.sessionTs = sessionData.getSessionTs();
    this.lastEventTs = sessionData.getLastEventTs();
    this.viewNumber = sessionData.getViewNumber();
    this.sessionViewNumber = sessionData.getSessionViewNumber();
    this.viewTs = sessionData.getViewTs();
  }

  @Override
  public long getSessionNumber() {
    return sessionNumber;
  }

  @Override
  public Long getViewNumber() {
    return viewNumber;
  }

  public void incrementViewNumber() {
    viewNumber = incrementValue(viewNumber);
  }

  @Override
  public Long getSessionViewNumber() {
    return sessionViewNumber;
  }

  public void incrementSessionViewNumber() {
    sessionViewNumber = incrementValue(sessionViewNumber);
  }

  @Override
  public Long getViewTs() {
    return viewTs;
  }

  public void setViewTs(long viewTs) {
    this.viewTs = viewTs;
  }

  @Override
  public long getSessionTs() {
    return sessionTs;
  }

  @Override
  public long getLastEventTs() {
    return lastEventTs;
  }

  public void setLastEventTs(long lastEventTs) {
    this.lastEventTs = lastEventTs;
  }

  private static Long incrementValue(Long previousValue) {
    return previousValue == null ? 1 : previousValue + 1;
  }


  @Override
  public String toString() {
    return "SessionDataModel{"
        + "sessionNumber=" + sessionNumber
        + ", sessionTs=" + sessionTs
        + ", lastEventTs=" + lastEventTs
        + ", viewNumber=" + viewNumber
        + ", sessionViewNumber=" + sessionViewNumber
        + ", viewTs=" + viewTs
        + '}';
  }
}
