package com.qubit.android.sdk.internal.session;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.TimingLogger;
import com.google.gson.Gson;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.common.model.QBEventImpl;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.session.event.SessionEventGenerator;
import com.qubit.android.sdk.internal.session.model.SessionDataModel;
import com.qubit.android.sdk.internal.session.model.SessionEvent;
import com.qubit.android.sdk.internal.session.model.SessionResponseImpl;
import com.qubit.android.sdk.internal.session.repository.SessionRepository;
import com.qubit.android.sdk.internal.session.repository.SessionRepositoryProvider;
import com.qubit.android.sdk.internal.util.DateTimeUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class SessionServiceImpl implements SessionService {

  private static final QBLogger LOGGER = QBLogger.getFor("SessionService");

  private static final long SESSION_VALIDITY_PERIOD_MS = DateTimeUtils.minToMs(30);
  private static final String VIEW_TYPE_POSTFIX = "view";
  private static final String SESSION_EVENT_TYPE = "qubit.session";

  private final SessionEventGenerator sessionEventGenerator;
  private final SessionRepositoryProvider sessionRepositoryProvider;

  private Handler handler;
  private boolean isStarted = false;
  private Gson gson;
  private SessionRepository sessionRepository;

  private SessionDataModel currentSessionData;

  public SessionServiceImpl(SessionRepositoryProvider sessionRepositoryProvider,
                            SessionEventGenerator sessionEventGenerator) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.sessionEventGenerator = sessionEventGenerator;
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException("SessionService is already started");
    }

    HandlerThread thread = new HandlerThread("SessionServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    sessionRepository = sessionRepositoryProvider.provide(handler);

    handler.post(new InitialSessionLoadTask());

    isStarted = true;
  }

  @Override
  public Future<SessionResponse> getOrCreateSession(String eventType, long nowEpochTimeMs) {
    GetOrCreateSessionTask task = new GetOrCreateSessionTask(eventType, nowEpochTimeMs);
    handler.post(task);
    return task;
  }


  private class InitialSessionLoadTask implements Runnable {
    @Override
    public void run() {
      currentSessionData = sessionRepository.load();
      LOGGER.d("Session loaded from local store: " + currentSessionData);
    }
  }

  private final class GetOrCreateSessionTask extends FutureTask<SessionResponse> implements Runnable {

    private GetOrCreateSessionTask(final String eventType, final long nowEpochTimeMs) {
      super(new Callable<SessionResponse>() {
        @Override
        public SessionResponse call() throws Exception {
          return getOrCreateSessionSynch(eventType, nowEpochTimeMs);
        }
      });
    }

  }

  private SessionResponse getOrCreateSessionSynch(String eventType, long nowEpochTimeMs) {
    LOGGER.d("getOrCreateSessionSynch() eventType: " + eventType);
    TimingLogger timings = new TimingLogger("qb-sdk", "getOrCreateSession");
    timings.reset();

    try {
      boolean isNewSession = false;
      if (!isCurrentSessionValid(nowEpochTimeMs)) {
        currentSessionData = createNextSession(currentSessionData, nowEpochTimeMs);
        isNewSession = true;
      }
      timings.addSplit("created");

      currentSessionData = registerEvent(eventType, nowEpochTimeMs, currentSessionData);
      timings.addSplit("registeredEvent");
      sessionRepository.save(currentSessionData);
      timings.addSplit("saved");

      QBEvent sessionEvent = isNewSession ? generateSessionEvent(currentSessionData) : null;
      timings.addSplit("event generated");

      return new SessionResponseImpl(currentSessionData, sessionEvent);
    } finally {
      timings.dumpToLog();
    }
  }


  private boolean isCurrentSessionValid(long nowEpochTimeMs) {
    return currentSessionData != null
        && currentSessionData.getLastEventTs() + SESSION_VALIDITY_PERIOD_MS > nowEpochTimeMs;
  }

  private static SessionDataModel createNextSession(SessionData oldSessionData, long nowEpochTimeMs) {
    if (oldSessionData == null) {
      return new SessionDataModel(1, nowEpochTimeMs, nowEpochTimeMs);
    } else {
      return new SessionDataModel(oldSessionData.getSessionNumber() + 1, nowEpochTimeMs, nowEpochTimeMs,
          oldSessionData.getViewNumber(), oldSessionData.getViewTs());
    }
  }

  private static SessionDataModel registerEvent(String eventType, long nowEpochTimeMs, SessionData sessionData) {
    SessionDataModel newSessionData = new SessionDataModel(sessionData);
    if (isViewEvent(eventType)) {
      newSessionData.incrementViewNumber();
      newSessionData.incrementSessionViewNumber();
      newSessionData.setViewTs(nowEpochTimeMs);
    }
    newSessionData.setLastEventTs(nowEpochTimeMs);
    return newSessionData;
  }

  private static boolean isViewEvent(String eventType) {
    int postfixIndex = eventType.length() - VIEW_TYPE_POSTFIX.length();
    return postfixIndex >= 0
        && eventType.substring(postfixIndex).equalsIgnoreCase(VIEW_TYPE_POSTFIX);
  }

  private QBEvent generateSessionEvent(SessionData sessionData) {
    SessionEvent sessionEvent = sessionEventGenerator.generateSessionEvent(sessionData);
    return new QBEventImpl(SESSION_EVENT_TYPE, getGson().toJsonTree(sessionEvent).getAsJsonObject());
  }

  private Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

}
