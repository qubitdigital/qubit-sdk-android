package com.qubit.android.sdk.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.qubit.android.sdk.internal.common.repository.DatabaseInitializer;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepository;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepositoryImpl;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;
import com.qubit.android.sdk.internal.eventtracker.EventTrackerImpl;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilder;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilderImpl;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepository;
import com.qubit.android.sdk.internal.eventtracker.repository.SQLLiteEventsRepository;
import com.qubit.android.sdk.internal.initialization.SecureAndroidIdDeviceIdProvider;
import com.qubit.android.sdk.internal.network.NetworkStateServiceImpl;
import com.qubit.android.sdk.internal.session.SessionServiceImpl;
import com.qubit.android.sdk.internal.session.event.SessionEventGenerator;
import com.qubit.android.sdk.internal.session.event.SessionEventGeneratorImpl;
import com.qubit.android.sdk.internal.session.repository.SessionRepository;
import com.qubit.android.sdk.internal.session.repository.SessionRepositoryImpl;
import java.util.concurrent.Future;

public class SDK {

  private NetworkStateServiceImpl networkStateService;
  private ConfigurationServiceImpl configurationService;
  private SessionServiceImpl sessionService;
  private EventTrackerImpl eventTracker;

  public SDK(Context appContext, String trackingId) {
    this.networkStateService = new NetworkStateServiceImpl(appContext);

    ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl(appContext);
    this.configurationService =
        new ConfigurationServiceImpl(trackingId, networkStateService, configurationRepository);

//    EventsRepository eventsRepository = new EventsRepositoryMock();
    SessionRepository sessionRepository = new SessionRepositoryImpl();
    SessionEventGenerator sessionEventGenerator = new SessionEventGeneratorImpl();
    sessionService = new SessionServiceImpl(sessionRepository, sessionEventGenerator);

    Future<SQLiteDatabase> databaseFuture =
        new DatabaseInitializer(appContext, SQLLiteEventsRepository.tableInitializer()).initDatabaseAsync();
    EventsRepository eventsRepository = new SQLLiteEventsRepository(databaseFuture);
    String deviceId = new SecureAndroidIdDeviceIdProvider(appContext).getDeviceId();
    EventsRestAPIConnectorBuilder eventsRestAPIConnectorBuilder = new EventsRestAPIConnectorBuilderImpl(trackingId);
    this.eventTracker = new EventTrackerImpl(trackingId, deviceId,
        configurationService, networkStateService, sessionService, eventsRepository, eventsRestAPIConnectorBuilder);
  }

  public void start() {
    networkStateService.start();
    configurationService.start();
    sessionService.start();
    eventTracker.start();
  }

  public EventTrackerImpl getEventTracker() {
    return eventTracker;
  }

}
