package com.qubit.android.sdk.internal.common.service;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.qubit.android.sdk.internal.logging.QBLogger;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

public abstract class QBService {

  private final String serviceName;
  private Handler handler;
  private boolean isStarted = false;
  private boolean isStopped = false;

  public QBService(String serviceName) {
    this.serviceName = serviceName;
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException(serviceName + " is already started");
    }
    if (isStopped) {
      throw new IllegalStateException(serviceName + " cannot be started after stopping");
    }

    HandlerThread thread = new HandlerThread("QB." + serviceName + "Thread", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());
    isStarted = true;
    onStart();
  }

  public synchronized void stop() {
    if (!isStarted) {
      throw new IllegalStateException(serviceName + " cannot be stopped, because it is not started.");
    }
    if (isStopped) {
      throw new IllegalStateException(serviceName + " has been already stopped.");
    }
    QBLogger.getFor(serviceName).i("Service is stopping.");
    onStop();
    if (Build.VERSION.SDK_INT >= JELLY_BEAN_MR2) {
      handler.getLooper().quitSafely();
    } else {
      handler.getLooper().quit();
    }
    isStopped = true;
  }

  protected boolean isRunning() {
    return isStarted && !isStopped;
  }

  protected void postTask(Runnable task) {
    if (isRunning()) {
      handler.post(task);
    }
  }

  protected void postTaskDelayed(Runnable task, long delayMillis) {
    if (isRunning()) {
      handler.postDelayed(task, delayMillis);
    }
  }

  protected void removeTask(Runnable task) {
    handler.removeCallbacks(task);
  }

  protected abstract void onStart();

  protected abstract void onStop();

}
