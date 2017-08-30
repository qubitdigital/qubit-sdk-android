package com.qubit.android.sdk.internal.configuration;

import android.content.Context;

public interface ConfigurationRepository {

  void save(Context context, Configuration configuration);
  Configuration load(Context context);

}
