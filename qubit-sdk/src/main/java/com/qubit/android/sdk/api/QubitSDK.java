package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.initialization.InitializationBuilder;

public abstract class QubitSDK {

  public static InitializationBuilder initialization() {
    return new InitializationBuilder();
  }

}
