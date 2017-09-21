package com.qubit.android.sdk.internal.session;

import com.google.gson.Gson;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.common.model.QBEventImpl;
import com.qubit.android.sdk.internal.common.service.QBService;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.lookup.LookupData;
import com.qubit.android.sdk.internal.lookup.LookupService;
import com.qubit.android.sdk.internal.session.event.SessionEventGenerator;
import com.qubit.android.sdk.internal.session.model.NewSessionRequestImpl;
import com.qubit.android.sdk.internal.session.model.SessionDataModel;
import com.qubit.android.sdk.internal.session.model.SessionEvent;
import com.qubit.android.sdk.internal.session.model.SessionForEventImpl;
import com.qubit.android.sdk.internal.session.repository.SessionRepository;
import com.qubit.android.sdk.internal.util.DateTimeUtils;
import com.qubit.android.sdk.internal.util.Elvis;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class SessionServiceImpl extends QBService implements SessionService {

  private static final String SERVICE_NAME = "SessionService";
  private static final QBLogger LOGGER = QBLogger.getFor(SERVICE_NAME);

  private static final long SESSION_VALIDITY_PERIOD_MS = DateTimeUtils.minToMs(30);
  private static final String VIEW_TYPE_POSTFIX = "view";

  private final LookupService lookupService;
  private final SessionEventGenerator sessionEventGenerator;
  private final SessionRepository sessionRepository;

  private final LookupService.LookupListener lookupListener;

  private Gson gson;

  private SessionDataModel currentSessionData;

  private LookupData currentLookupData;

  private final Long lookupLockObject = System.currentTimeMillis();
  private List<GetSessionDataForNextEventTask> getTasksWaitingForLookup = new ArrayList<>();
  private boolean isLookupReceived = false;

  public SessionServiceImpl(LookupService lookupService, SessionRepository sessionRepository,
                            SessionEventGenerator sessionEventGenerator) {
    super(SERVICE_NAME);
    this.lookupService = lookupService;
    this.sessionRepository = sessionRepository;
    this.sessionEventGenerator = sessionEventGenerator;
    lookupListener = new LookupService.LookupListener() {
      @Override
      public void onLookupDataChange(LookupData lookupData) {
        postTask(new LookupChangeTask(lookupData));
      }
    };
  }


  @Override
  protected void onStart() {
    postTask(new InitialSessionLoadTask());
    lookupService.registerLookupListener(lookupListener);

  }

  @Override
  protected void onStop() {
    lookupService.unregisterLookupListener(lookupListener);
  }

  @Override
  public Future<SessionForEvent> getSessionDataForNextEvent(String eventType, long nowEpochTimeMs) {
    GetSessionDataForNextEventTask task = new GetSessionDataForNextEventTask(eventType, nowEpochTimeMs);

    synchronized (lookupLockObject) {
      if (isLookupReceived) {
        postTask(task);
      } else {
        LOGGER.d("Save GetTask in queue waiting for Lookup");
        getTasksWaitingForLookup.add(task);
      }
    }
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

  private class LookupChangeTask implements Runnable {
    private final LookupData lookupData;

    LookupChangeTask(LookupData lookupData) {
      this.lookupData = lookupData;
    }

    @Override
    public void run() {
      LOGGER.d("Lookup changed");
      currentLookupData = lookupData;
      // TODO
      synchronized (lookupLockObject) {
        if (!isLookupReceived) {
          isLookupReceived = true;
          if (!getTasksWaitingForLookup.isEmpty()) {
            LOGGER.d("Posting gathered Get*Task (" + getTasksWaitingForLookup.size() + ") to execution");
            for (GetSessionDataForNextEventTask waitingGetTask : getTasksWaitingForLookup) {
              postTask(waitingGetTask);
            }
            getTasksWaitingForLookup.clear();
          }
        }
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
    SessionDataModel newSessionData = createNextSession(oldSessionData, currentLookupData, nowEpochTimeMs);
    registerEvent(newSessionData, SESSION_EVENT_TYPE, nowEpochTimeMs);
    QBEvent sessionEvent = generateSessionEvent(newSessionData);
    return new NewSessionRequestImpl(sessionEvent, newSessionData);
  }

  private static boolean isSessionValid(SessionData sessionData, long nowEpochTimeMs) {
    return sessionData != null
        && sessionData.getLastEventTs() + SESSION_VALIDITY_PERIOD_MS > nowEpochTimeMs;
  }

  private static SessionDataModel createNextSession(SessionData oldSessionData, LookupData lookupData,
                                                    long nowEpochTimeMs) {
    long currentSessionNumber = maxFromNullable(
        oldSessionData != null ? oldSessionData.getSessionNumber() : 0,
        lookupData != null ? lookupData.getSessionNumber() : null);
    long nextSessionNumber = currentSessionNumber + 1;

    long viewNumber = maxFromNullable(
        oldSessionData != null ? oldSessionData.getViewNumber() : null,
        lookupData != null ? lookupData.getViewNumber() : null);

    long viewTs = maxFromNullable(
        oldSessionData != null ? oldSessionData.getViewTs() : null,
        lookupData != null ? lookupData.getLastViewTs() : null);

    return new SessionDataModel(nextSessionNumber, nowEpochTimeMs, nowEpochTimeMs, viewNumber, viewTs);
  }

  private static long maxFromNullable(Long number1, Long number2) {
    return Math.max(Elvis.getIfNotNull(number1, 0L), Elvis.getIfNotNull(number2, 0L));
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
    SessionEvent sessionEvent = sessionEventGenerator.generateSessionEvent(sessionData, currentLookupData);
    return new QBEventImpl(SESSION_EVENT_TYPE, getGson().toJsonTree(sessionEvent).getAsJsonObject());
  }

  private Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

}
