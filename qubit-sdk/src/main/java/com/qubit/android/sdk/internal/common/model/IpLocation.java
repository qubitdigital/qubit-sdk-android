package com.qubit.android.sdk.internal.common.model;

public class IpLocation {

  String country;
  String countryCode;
  String region;
  String regionCode;
  String area;
  String areaCode;
  String city;
  String cityCode;
  Double longitude;
  Double latitude;

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getRegionCode() {
    return regionCode;
  }

  public void setRegionCode(String regionCode) {
    this.regionCode = regionCode;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getAreaCode() {
    return areaCode;
  }

  public void setAreaCode(String areaCode) {
    this.areaCode = areaCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  @Override
  public String toString() {
    return "IpLocation{"
        + "country='" + country + '\''
        + ", countryCode='" + countryCode + '\''
        + ", region='" + region + '\''
        + ", regionCode='" + regionCode + '\''
        + ", area='" + area + '\''
        + ", areaCode='" + areaCode + '\''
        + ", city='" + city + '\''
        + ", cityCode='" + cityCode + '\''
        + ", longitude=" + longitude
        + ", latitude=" + latitude
        + '}';
  }
}
