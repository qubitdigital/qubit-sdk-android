package com.qubit.android.sdk.internal.network;

import android.content.Context;

public class NetworkStateServiceImpl implements NetworkStateService {

  private final Context context;

  public NetworkStateServiceImpl(Context context) {
    this.context = context;
  }

  @Override
  public void registerNetworkStateListener(NetworkStateListener networkStateListener) {
    // TODO
  }

  @Override
  public boolean isConnected() {
    // TODO
    return false;
  }
}
