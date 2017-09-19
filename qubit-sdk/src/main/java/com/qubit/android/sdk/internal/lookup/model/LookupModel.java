package com.qubit.android.sdk.internal.lookup.model;

import com.qubit.android.sdk.internal.common.model.IpLocation;
import com.qubit.android.sdk.internal.lookup.LookupData;

public class LookupModel implements LookupData {

  private IpLocation ipLocation;
  private String ipAddress;
  private Long viewNumber;
  private Long sessionNumber;
  private Long conversionNumber;
  private Long entranceNumber;
  private Long firstViewTs;
  private Long firstConversionTs;
  private Long lastConversionTs;
  private Long lifetimeValue;
  private Long lastViewTs;
  private Long conversionCycleNumber;

  @Override
  public IpLocation getIpLocation() {
    return ipLocation;
  }

  public void setIpLocation(IpLocation ipLocation) {
    this.ipLocation = ipLocation;
  }

  @Override
  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  @Override
  public Long getViewNumber() {
    return viewNumber;
  }

  public void setViewNumber(Long viewNumber) {
    this.viewNumber = viewNumber;
  }

  @Override
  public Long getSessionNumber() {
    return sessionNumber;
  }

  public void setSessionNumber(Long sessionNumber) {
    this.sessionNumber = sessionNumber;
  }

  @Override
  public Long getConversionNumber() {
    return conversionNumber;
  }

  public void setConversionNumber(Long conversionNumber) {
    this.conversionNumber = conversionNumber;
  }

  @Override
  public Long getEntranceNumber() {
    return entranceNumber;
  }

  public void setEntranceNumber(Long entranceNumber) {
    this.entranceNumber = entranceNumber;
  }

  @Override
  public Long getFirstViewTs() {
    return firstViewTs;
  }

  public void setFirstViewTs(Long firstViewTs) {
    this.firstViewTs = firstViewTs;
  }

  @Override
  public Long getFirstConversionTs() {
    return firstConversionTs;
  }

  public void setFirstConversionTs(Long firstConversionTs) {
    this.firstConversionTs = firstConversionTs;
  }

  @Override
  public Long getLastConversionTs() {
    return lastConversionTs;
  }

  public void setLastConversionTs(Long lastConversionTs) {
    this.lastConversionTs = lastConversionTs;
  }

  @Override
  public Long getLifetimeValue() {
    return lifetimeValue;
  }

  public void setLifetimeValue(Long lifetimeValue) {
    this.lifetimeValue = lifetimeValue;
  }

  @Override
  public Long getLastViewTs() {
    return lastViewTs;
  }

  public void setLastViewTs(Long lastViewTs) {
    this.lastViewTs = lastViewTs;
  }

  @Override
  public Long getConversionCycleNumber() {
    return conversionCycleNumber;
  }

  public void setConversionCycleNumber(Long conversionCycleNumber) {
    this.conversionCycleNumber = conversionCycleNumber;
  }
}
