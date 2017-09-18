package com.qubit.android.sdk.internal.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;
import com.qubit.android.sdk.internal.logging.QBLogger;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public class NetworkStateServiceImpl implements NetworkStateService {

  private static final QBLogger LOGGER = QBLogger.getFor("NetworkStateService");

  private final Context appContext;
  private final Collection<NetworkStateListener> listeners = new CopyOnWriteArraySet<>();
  private Handler handler;
  private NetworkInfo networkInfo;

  public NetworkStateServiceImpl(Context appContext) {
    this.appContext = appContext;
    networkInfo = requestForActiveNetworkInfo();
  }

  @Override
  public void registerNetworkStateListener(NetworkStateListener listener) {
    handler.post(new RegisterListenerTask(listener));
  }

  public void start() {
    HandlerThread thread = new HandlerThread("ConfigurationServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    appContext.registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        LOGGER.d("Message from Connectivity service");
        handler.post(new ReceiveConnectivityActionTask());
      }
    }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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

  private final class RegisterListenerTask implements Runnable {
    private final NetworkStateListener listener;

    private RegisterListenerTask(NetworkStateListener listener) {
      this.listener = listener;
    }

    @Override
    public void run() {
      listeners.add(listener);
      notifyListenerNetworkStateChange(listener, isConnected(networkInfo));
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
