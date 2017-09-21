package com.qubit.android.sdk.internal.eventtracker;

import com.qubit.android.sdk.internal.configuration.Configuration;

public class EventTypeTransformer {

  private static final String DOT = ".";

  private final String namespace;

  public EventTypeTransformer(String namespace) {
    this.namespace = namespace;
  }

  public EventTypeTransformer(Configuration configuration) {
    this.namespace = configuration.getNamespace();
  }

  public String transform(String sourceEventType) {
    return transformEventType(sourceEventType, namespace);
  }

  private static String transformEventType(String sourceEventType, String namespace) {
    return sourceEventType.startsWith("qubit.")
        ? sourceEventType
        : addNamespace(sourceEventType, namespace);
  }

  private static String addNamespace(String sourceEventType, String namespace) {
    if (namespace == null || namespace.isEmpty() || sourceEventType.contains(DOT)) {
      return sourceEventType;
    }
    return namespace + DOT + sourceEventType;
  }

}
