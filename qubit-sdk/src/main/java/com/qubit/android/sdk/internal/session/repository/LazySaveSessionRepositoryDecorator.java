package com.qubit.android.sdk.internal.session.repository;

import android.os.Handler;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.session.model.SessionDataModel;

public class LazySaveSessionRepositoryDecorator implements SessionRepository {

  private static final QBLogger LOGGER = QBLogger.getFor("LazySaveSessionRepository");

  private static final long LAZINESS_PERIOD_MS = 1000;

  private final SessionRepository sessionRepository;
  private final Handler handler;
  private final StoreSessionDataTask storeSessionDataTask = new StoreSessionDataTask();

  private SessionDataModel currentSessionData;
  private long currentSessionDataTime = 0;
  private Long firstSessionDataTime = null;
  private long lastSaveTime = 0;

  public LazySaveSessionRepositoryDecorator(SessionRepository sessionRepository, Handler handler) {
    this.sessionRepository = sessionRepository;
    this.handler = handler;
  }

  @Override
  public void save(SessionDataModel sessionData) {
    currentSessionData = sessionData;
    currentSessionDataTime = System.currentTimeMillis();
    if (firstSessionDataTime == null) {
      firstSessionDataTime = currentSessionDataTime;
    }
    scheduleNextSaveTask();
  }

  @Override
  public SessionDataModel load() {
    currentSessionData = sessionRepository.load();
    currentSessionDataTime = System.currentTimeMillis();
    return currentSessionData;
  }

  private class StoreSessionDataTask implements Runnable {
    @Override
    public void run() {
      sessionRepository.save(currentSessionData);
      lastSaveTime = System.currentTimeMillis();
      firstSessionDataTime = null;
    }
  }

  private void scheduleNextSaveTask() {
    handler.removeCallbacks(storeSessionDataTask);
    if (currentSessionData == null) {
      return;
    }

    Long timeMsToNextSafe = evaluateTimeMsToNextSafe();
    if (timeMsToNextSafe != null) {
      if (timeMsToNextSafe > 0) {
        handler.postDelayed(storeSessionDataTask, timeMsToNextSafe);
        LOGGER.d("Save scheduled for " + timeMsToNextSafe);
      } else {
        handler.post(storeSessionDataTask);
        LOGGER.d("Save scheduled for NOW");
      }
    }
  }

  /**
   * @return null - Nothing to save. 0 - Now. N - miliseconds to next save task
   */
  private Long evaluateTimeMsToNextSafe() {
    if (currentSessionDataTime <= lastSaveTime || firstSessionDataTime == null) {
      return null;
    }
    long now = System.currentTimeMillis();
    long nextSaveTime = firstSessionDataTime + LAZINESS_PERIOD_MS;
    return nextSaveTime > now ? nextSaveTime - now : 0;
  }

}
