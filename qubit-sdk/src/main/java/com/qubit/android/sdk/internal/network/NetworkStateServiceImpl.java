package com.qubit.android.sdk.internal.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.Collection;
import java.util.HashSet;

public class NetworkStateServiceImpl extends BroadcastReceiver implements NetworkStateService {

  private final Context context;
  private final Collection<NetworkStateListener> listeners = new HashSet<>();

  public NetworkStateServiceImpl(Context context) {
    this.context = context;
  }

  @Override
  public void registerNetworkStateListener(NetworkStateListener listener) {
    listeners.add(listener);
    boolean isConnected = isConnected(context);
    notifyListenerNetworkStateChange(listener, isConnected);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    boolean isConnected = isConnected(context);
    notifyListenersNetworkStateChange(isConnected);
  }

  private boolean isConnected(Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  private void notifyListenersNetworkStateChange(boolean isConnected) {
    for (NetworkStateListener listener : listeners) {
      if (listener != null) {
        listener.onNetworkStateChange(isConnected);
      }
    }
  }

  private void notifyListenerNetworkStateChange(NetworkStateListener listener, boolean isConnected) {
    if (listener != null) {
      listener.onNetworkStateChange(isConnected);
    }
  }

  public void start(Context context) {
    context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
  }
}
