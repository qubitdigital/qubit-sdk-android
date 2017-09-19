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
import com.qubit.android.sdk.internal.lookup.LookupServiceImpl;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnectorBuilder;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnectorBuilderImpl;
import com.qubit.android.sdk.internal.network.NetworkStateServiceImpl;
import com.qubit.android.sdk.internal.session.SessionServiceImpl;
import com.qubit.android.sdk.internal.session.event.AppPropertiesProvider;
import com.qubit.android.sdk.internal.session.event.ManifestAppPropertiesProvider;
import com.qubit.android.sdk.internal.session.event.ScreenSizeProvider;
import com.qubit.android.sdk.internal.session.event.ScreenSizeProviderImpl;
import com.qubit.android.sdk.internal.session.event.SessionEventGenerator;
import com.qubit.android.sdk.internal.session.event.SessionEventGeneratorImpl;
import com.qubit.android.sdk.internal.session.repository.SessionRepository;
import com.qubit.android.sdk.internal.session.repository.SessionRepositoryImpl;
import java.util.concurrent.Future;

public class SDK {

  private NetworkStateServiceImpl networkStateService;
  private ConfigurationServiceImpl configurationService;
  private LookupServiceImpl lookupService;
  private SessionServiceImpl sessionService;
  private EventTrackerImpl eventTracker;

  public SDK(Context appContext, String trackingId) {
    this.networkStateService = new NetworkStateServiceImpl(appContext);

    ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl(appContext);
    this.configurationService =
        new ConfigurationServiceImpl(trackingId, networkStateService, configurationRepository);

    String deviceId = new SecureAndroidIdDeviceIdProvider(appContext).getDeviceId();

    LookupConnectorBuilder lookupConnectorBuilder = new LookupConnectorBuilderImpl(trackingId, deviceId);
    lookupService = new LookupServiceImpl(trackingId, deviceId, configurationService, networkStateService,
        lookupConnectorBuilder);

    SessionRepository sessionRepository = new SessionRepositoryImpl(appContext);
    ScreenSizeProvider screenSizeProvider = new ScreenSizeProviderImpl(appContext);
    AppPropertiesProvider appPropertiesProvider = new ManifestAppPropertiesProvider(appContext);
    SessionEventGenerator sessionEventGenerator =
        new SessionEventGeneratorImpl(screenSizeProvider, appPropertiesProvider);
    sessionService = new SessionServiceImpl(sessionRepository, sessionEventGenerator);

//    EventsRepository eventsRepository = new EventsRepositoryMock();
    Future<SQLiteDatabase> databaseFuture =
        new DatabaseInitializer(appContext, SQLLiteEventsRepository.tableInitializer()).initDatabaseAsync();
    EventsRepository eventsRepository = new SQLLiteEventsRepository(databaseFuture);
    EventsRestAPIConnectorBuilder eventsRestAPIConnectorBuilder = new EventsRestAPIConnectorBuilderImpl(trackingId);
    this.eventTracker = new EventTrackerImpl(trackingId, deviceId,
        configurationService, networkStateService, sessionService, eventsRepository, eventsRestAPIConnectorBuilder);
  }

  public void start() {
    networkStateService.start();
    configurationService.start();
    lookupService.start();
    sessionService.start();
    eventTracker.start();
  }

  public void stop() {
    eventTracker.stop();
    sessionService.stop();
    lookupService.stop();
    configurationService.stop();
    networkStateService.stop();
  }

  public EventTrackerImpl getEventTracker() {
    return eventTracker;
  }

}
