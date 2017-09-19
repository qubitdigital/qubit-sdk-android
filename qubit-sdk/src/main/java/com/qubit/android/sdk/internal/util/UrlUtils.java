package com.qubit.android.sdk.internal.util;

public final class UrlUtils {

  private static final String URL_PREFIX_HTTP = "http://";
  private static final String URL_PREFIX_HTTPS = "https://";

  private UrlUtils() {
  }

  public static String addProtocol(String endpoint, boolean defaultHttps) {
    if (endpoint.startsWith(URL_PREFIX_HTTP) || endpoint.startsWith(URL_PREFIX_HTTPS)) {
      return endpoint;
    } else {
      return (defaultHttps ? URL_PREFIX_HTTPS : URL_PREFIX_HTTP) + endpoint;
    }
  }

}
