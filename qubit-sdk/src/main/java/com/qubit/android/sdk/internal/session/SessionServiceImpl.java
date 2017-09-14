package com.qubit.android.sdk.internal.session;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.google.gson.Gson;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.common.model.QBEventImpl;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.session.event.SessionEventGenerator;
import com.qubit.android.sdk.internal.session.model.NewSessionRequestImpl;
import com.qubit.android.sdk.internal.session.model.SessionDataModel;
import com.qubit.android.sdk.internal.session.model.SessionEvent;
import com.qubit.android.sdk.internal.session.model.SessionForEventImpl;
import com.qubit.android.sdk.internal.session.repository.SessionRepository;
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
  private final SessionRepository sessionRepository;

  private Handler handler;
  private boolean isStarted = false;
  private Gson gson;

  private SessionDataModel currentSessionData;

  public SessionServiceImpl(SessionRepository sessionRepository,
                            SessionEventGenerator sessionEventGenerator) {
    this.sessionRepository = sessionRepository;
    this.sessionEventGenerator = sessionEventGenerator;
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException("SessionService is already started");
    }

    HandlerThread thread = new HandlerThread("SessionServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    handler.post(new InitialSessionLoadTask());

    isStarted = true;
  }

  @Override
  public Future<SessionForEvent> getSessionDataForNextEvent(String eventType, long nowEpochTimeMs) {
    GetSessionDataForNextEventTask task = new GetSessionDataForNextEventTask(eventType, nowEpochTimeMs);
    handler.post(task);
    return task;
  }


  private class InitialSessionLoadTask implements Runnable {
    @Override
    public void run() {
      currentSessionData = sessionRepository.load();
      LOGGER.d("Session loaded from local store: " + currentSessionData);
      if (currentSessionData != null) {
        currentSessionData.setLastEventTs(0);
      }
    }
  }

  private final class GetSessionDataForNextEventTask extends FutureTask<SessionForEvent> implements Runnable {

    private GetSessionDataForNextEventTask(final String eventType, final long nowEpochTimeMs) {
      super(new Callable<SessionForEvent>() {
        @Override
        public SessionForEvent call() throws Exception {
          return getSessionDataForNextEventSynch(eventType, nowEpochTimeMs);
        }
      });
    }

  }

  private SessionForEvent getSessionDataForNextEventSynch(String eventType, long nowEpochTimeMs) {
    LOGGER.d("getSessionDataForNextEventSynch() eventType: " + eventType);

    NewSessionRequestImpl newSession = createNewSessionIfNeeded(currentSessionData, nowEpochTimeMs);

    SessionDataModel sessionDataBeforeEvent = newSession != null ? newSession.getSessionData() : currentSessionData;

    SessionDataModel sessionDataModelOfEvent =
        createSessionDataForEvent(sessionDataBeforeEvent, eventType, nowEpochTimeMs);

    currentSessionData = sessionDataModelOfEvent;
    sessionRepository.save(currentSessionData);

    return new SessionForEventImpl(sessionDataModelOfEvent, newSession);
  }

  private static SessionDataModel createSessionDataForEvent(SessionData oldSessionData, String eventType,
                                                            long nowEpochTimeMs) {
    SessionDataModel newSessionData = new SessionDataModel(oldSessionData);
    registerEvent(newSessionData, eventType, nowEpochTimeMs);
    return newSessionData;
  }

  private NewSessionRequestImpl createNewSessionIfNeeded(SessionData oldSessionData, long nowEpochTimeMs) {
    if (isSessionValid(oldSessionData, nowEpochTimeMs)) {
      return null;
    }
    SessionDataModel newSessionData = createNextSession(oldSessionData, nowEpochTimeMs);
    registerEvent(newSessionData, SESSION_EVENT_TYPE, nowEpochTimeMs);
    QBEvent sessionEvent = generateSessionEvent(newSessionData);
    return new NewSessionRequestImpl(sessionEvent, newSessionData);
  }

  private static boolean isSessionValid(SessionData sessionData, long nowEpochTimeMs) {
    return sessionData != null
        && sessionData.getLastEventTs() + SESSION_VALIDITY_PERIOD_MS > nowEpochTimeMs;
  }

  private static SessionDataModel createNextSession(SessionData oldSessionData, long nowEpochTimeMs) {
    if (oldSessionData == null) {
      return new SessionDataModel(1, nowEpochTimeMs, nowEpochTimeMs);
    } else {
      return new SessionDataModel(oldSessionData.getSessionNumber() + 1, nowEpochTimeMs, nowEpochTimeMs,
          oldSessionData.getViewNumber(), oldSessionData.getViewTs());
    }
  }

  private static void registerEvent(SessionDataModel sessionData, String eventType, long nowEpochTimeMs) {
    if (isViewEvent(eventType)) {
      sessionData.incrementViewNumber();
      sessionData.incrementSessionViewNumber();
      sessionData.setViewTs(nowEpochTimeMs);
    }
    sessionData.setLastEventTs(nowEpochTimeMs);
    sessionData.incrementSessionEventsNumber();
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
