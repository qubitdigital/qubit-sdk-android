package com.qubit.android.sdk.internal.eventtracker.connector;

public class EventMeta {

  private final String id;
  private final long ts;
  private final String type;
  private final String trackingId;
  private Long seq;
  private String source;
  private Long batchTs;

  public EventMeta(String id, long ts, String type, String trackingId) {
    this.id = id;
    this.ts = ts;
    this.type = type;
    this.trackingId = trackingId;
  }

  public EventMeta(String id, long ts, String type, String trackingId, long seq, String source, Long batchTs) {
    this.id = id;
    this.ts = ts;
    this.type = type;
    this.trackingId = trackingId;
    this.seq = seq;
    this.source = source;
    this.batchTs = batchTs;
  }


  public String getId() {
    return id;
  }

  public long getTs() {
    return ts;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public String getType() {
    return type;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Long getSeq() {
    return seq;
  }

  public void setSeq(Long seq) {
    this.seq = seq;
  }

  public Long getBatchTs() {
    return batchTs;
  }

  public void setBatchTs(Long batchTs) {
    this.batchTs = batchTs;
  }
}
