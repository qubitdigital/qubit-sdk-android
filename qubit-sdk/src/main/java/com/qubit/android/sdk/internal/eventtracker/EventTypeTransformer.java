package com.qubit.android.sdk.internal.eventtracker;

import com.qubit.android.sdk.internal.configuration.Configuration;

public class EventTypeTransformer {

  private static final String DOT = ".";

  private final String namespace;
  private final String vertical;

  public EventTypeTransformer(String namespace, String vertical) {
    this.namespace = namespace;
    this.vertical = vertical;
  }

  public EventTypeTransformer(Configuration configuration) {
    this.namespace = configuration.getNamespace();
    this.vertical = configuration.getVertical();
  }

  public String transform(String sourceEventType) {
    return transformEventType(sourceEventType, namespace, vertical);
  }

  private static String transformEventType(String sourceEventType, String namespace, String vertical) {
    return sourceEventType.startsWith("qubit.")
        ? sourceEventType
        : addNamespace(addVertical(sourceEventType, vertical), namespace);
  }

  private static String addVertical(String sourceEventType, String vertical) {
    if (vertical == null || vertical.isEmpty()
        || sourceEventType.contains(DOT)
        || sourceEventType.startsWith(vertical)) {
      return sourceEventType;
    }
    String capitalizedSourceType = sourceEventType.substring(0, 1).toUpperCase() + sourceEventType.substring(1);
    return vertical + capitalizedSourceType;
  }

  private static String addNamespace(String sourceEventType, String namespace) {
    if (namespace == null || namespace.isEmpty() || sourceEventType.contains(DOT)) {
      return sourceEventType;
    }
    return namespace + DOT + sourceEventType;
  }

}
