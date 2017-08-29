package com.qubit.android.sdk.internal.network;

public interface NetworkStateService {

  interface NetworkStateListener {
    void onNetworkStateChange(boolean isConnected);
  }

  void registerNetworkStateListener(NetworkStateListener networkStateListener);

  boolean isConnected();

}
