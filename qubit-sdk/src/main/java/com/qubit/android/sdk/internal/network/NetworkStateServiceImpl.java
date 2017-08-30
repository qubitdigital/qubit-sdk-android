package com.qubit.android.sdk.internal.network;

import android.content.Context;

public class NetworkStateServiceImpl implements NetworkStateService {

  private final Context context;

  private boolean isConnected = true;

  public NetworkStateServiceImpl(Context context) {
    this.context = context;
  }

  @Override
  public void registerNetworkStateListener(NetworkStateListener networkStateListener) {
    // TODO

    networkStateListener.onNetworkStateChange(isConnected);
  }

}
