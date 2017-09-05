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
import com.qubit.android.sdk.internal.logging.QBLogger;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public class NetworkStateServiceImpl implements NetworkStateService {

  private static final QBLogger LOGGER = QBLogger.getFor("NetworkStateService");

  private final Context appContext;
  private final Collection<NetworkStateListener> listeners = new CopyOnWriteArraySet<>();
  private Handler handler;
  private boolean isConnected = false;

  public NetworkStateServiceImpl(Context appContext) {
    this.appContext = appContext;
    isConnected = isConnected();
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
      boolean newIsConnected = isConnected();
      LOGGER.d("Handling Message from Connectivity service. isConnected: " + newIsConnected);
      if (isConnected != newIsConnected) {
        isConnected = newIsConnected;
        notifyListenersNetworkStateChange();
      }
    }
  }

  private boolean isConnected() {
    ConnectivityManager manager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  private final class RegisterListenerTask implements Runnable {
    private final NetworkStateListener listener;

    private RegisterListenerTask(NetworkStateListener listener) {
      this.listener = listener;
    }

    @Override
    public void run() {
      listeners.add(listener);
      notifyListenerNetworkStateChange(listener);
    }
  }

  private void notifyListenersNetworkStateChange() {
    for (NetworkStateListener listener : listeners) {
      notifyListenerNetworkStateChange(listener);
    }
  }

  private void notifyListenerNetworkStateChange(NetworkStateListener listener) {
    if (listener != null) {
      listener.onNetworkStateChange(isConnected);
    }
  }



}
