package com.qubit.android.sdk.internal.session.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qubit.android.sdk.internal.common.model.IpLocation;

public class SessionEvent {

  Long firstViewTs;
  Long lastViewTs;
  Long firstConversionTs;
  Long lastConversionTs;

  IpLocation ipLocation;
  String ipAddress;

  String deviceType;
  String deviceName;

  String osName;
  String osVersion;

  String appType;
  String appName;
  String appVersion;

  Integer screenWidth;
  Integer screenHeight;

  public JsonObject toJsonObject(Gson gson) {
    return toJsonObject(gson, this);
  }

  public static JsonObject toJsonObject(Gson gson, SessionEvent sessionEvent) {
    return gson.toJsonTree(sessionEvent).getAsJsonObject();
  }

  public Long getFirstViewTs() {
    return firstViewTs;
  }

  public void setFirstViewTs(Long firstViewTs) {
    this.firstViewTs = firstViewTs;
  }

  public Long getLastViewTs() {
    return lastViewTs;
  }

  public void setLastViewTs(Long lastViewTs) {
    this.lastViewTs = lastViewTs;
  }

  public Long getFirstConversionTs() {
    return firstConversionTs;
  }

  public void setFirstConversionTs(Long firstConversionTs) {
    this.firstConversionTs = firstConversionTs;
  }

  public Long getLastConversionTs() {
    return lastConversionTs;
  }

  public void setLastConversionTs(Long lastConversionTs) {
    this.lastConversionTs = lastConversionTs;
  }

  public IpLocation getIpLocation() {
    return ipLocation;
  }

  public void setIpLocation(IpLocation ipLocation) {
    this.ipLocation = ipLocation;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getOsName() {
    return osName;
  }

  public void setOsName(String osName) {
    this.osName = osName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getAppType() {
    return appType;
  }

  public void setAppType(String appType) {
    this.appType = appType;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public Integer getScreenWidth() {
    return screenWidth;
  }

  public void setScreenWidth(Integer screenWidth) {
    this.screenWidth = screenWidth;
  }

  public Integer getScreenHeight() {
    return screenHeight;
  }

  public void setScreenHeight(Integer screenHeight) {
    this.screenHeight = screenHeight;
  }
}
