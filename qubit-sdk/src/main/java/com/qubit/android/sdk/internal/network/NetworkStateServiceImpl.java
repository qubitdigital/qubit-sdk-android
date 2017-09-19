package com.qubit.android.sdk.internal.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import com.qubit.android.sdk.internal.common.service.QBService;
import com.qubit.android.sdk.internal.logging.QBLogger;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public class NetworkStateServiceImpl extends QBService implements NetworkStateService {

  private static final String SERVICE_NAME = "NetworkStateService";
  private static final QBLogger LOGGER = QBLogger.getFor(SERVICE_NAME);

  private final Context appContext;
  private final Collection<NetworkStateListener> listeners = new CopyOnWriteArraySet<>();
  private final BroadcastReceiver broadcastReceiver;
  private NetworkInfo networkInfo;

  public NetworkStateServiceImpl(Context appContext) {
    super(SERVICE_NAME);
    this.appContext = appContext;
    networkInfo = requestForActiveNetworkInfo();
    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        LOGGER.d("Message from Connectivity service");
        postTask(new ReceiveConnectivityActionTask());
      }
    };
  }

  @Override
  protected void onStart() {
    appContext.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
  }

  @Override
  protected void onStop() {
    appContext.unregisterReceiver(broadcastReceiver);
  }

  @Override
  public void registerNetworkStateListener(final NetworkStateListener listener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.add(listener);
        notifyListenerNetworkStateChange(listener, isConnected(networkInfo));
      }
    });
  }

  @Override
  public void unregisterNetworkStateListener(final NetworkStateListener listener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.remove(listener);
      }
    });
  }

  private final class ReceiveConnectivityActionTask implements Runnable {
    @Override
    public void run() {
      NetworkInfo oldNetworkInfo = networkInfo;
      NetworkInfo newNetworkInfo = requestForActiveNetworkInfo();
      boolean oldIsConnected = isConnected(networkInfo);
      boolean newIsConnected = isConnected(newNetworkInfo);
      LOGGER.d("Handling Message from Connectivity service. isConnected: " + newIsConnected);
      networkInfo = newNetworkInfo;
      if (oldIsConnected != newIsConnected
          || newIsConnected && !areSameNetworks(oldNetworkInfo, newNetworkInfo)) {
        notifyListenersNetworkStateChange(newIsConnected);
      }
    }
  }

  private NetworkInfo requestForActiveNetworkInfo() {
    ConnectivityManager manager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    LOGGER.d("Network Info: " + networkInfo);
    return networkInfo;
  }

  private static boolean areSameNetworks(NetworkInfo networkInfo1, NetworkInfo networkInfo2) {
    return networkInfo1.getType() == networkInfo2.getType()
        && TextUtils.equals(networkInfo1.getExtraInfo(), networkInfo2.getExtraInfo());
  }

  private static boolean isConnected(NetworkInfo networkInfo) {
    return networkInfo != null && networkInfo.isConnected();
  }

  private void notifyListenersNetworkStateChange(boolean isConnected) {
    for (NetworkStateListener listener : listeners) {
      notifyListenerNetworkStateChange(listener, isConnected);
    }
  }

  private void notifyListenerNetworkStateChange(NetworkStateListener listener, boolean isConnected) {
    if (listener != null) {
      listener.onNetworkStateChange(isConnected);
    }
  }

}
