package com.qubit.android.sdk.internal.configuration.connector;

public final class ConfigurationResponse {

  public enum Status {
    OK, NOT_FOUND, ERROR
  }

  private final Status status;
  private final ConfigurationRestModel configuration;

  private ConfigurationResponse(Status status, ConfigurationRestModel configuration) {
    this.status = status;
    this.configuration = configuration;
  }

  public static ConfigurationResponse ok(ConfigurationRestModel configuration) {
    return new ConfigurationResponse(Status.OK, configuration);
  }

  public static ConfigurationResponse notFound() {
    return new ConfigurationResponse(Status.NOT_FOUND, null);
  }

  public static ConfigurationResponse error() {
    return new ConfigurationResponse(Status.ERROR, null);
  }

  public Status getStatus() {
    return status;
  }

  public ConfigurationRestModel getConfiguration() {
    return configuration;
  }
}
