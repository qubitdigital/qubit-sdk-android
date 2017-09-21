package com.qubit.android.sdk.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.qubit.android.sdk.internal.common.repository.DatabaseInitializer;
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationConnectorBuilder;
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationConnectorBuilderImpl;
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationRepository;
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationRepositoryImpl;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;
import com.qubit.android.sdk.internal.eventtracker.EventTrackerImpl;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilder;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilderImpl;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepository;
import com.qubit.android.sdk.internal.eventtracker.repository.SQLiteEventsRepository;
import com.qubit.android.sdk.internal.initialization.SecureAndroidIdDeviceIdProvider;
import com.qubit.android.sdk.internal.lookup.LookupServiceImpl;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnectorBuilder;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnectorBuilderImpl;
import com.qubit.android.sdk.internal.lookup.repository.LookupRepository;
import com.qubit.android.sdk.internal.lookup.repository.LookupRepositoryImpl;
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
    ConfigurationConnectorBuilder configurationConnectorBuilder = new ConfigurationConnectorBuilderImpl(trackingId);
    this.configurationService =
        new ConfigurationServiceImpl(networkStateService, configurationRepository, configurationConnectorBuilder);

    String deviceId = new SecureAndroidIdDeviceIdProvider(appContext).getDeviceId();

    LookupRepository lookupRepository = new LookupRepositoryImpl(appContext);
    LookupConnectorBuilder lookupConnectorBuilder = new LookupConnectorBuilderImpl(trackingId, deviceId);
    lookupService = new LookupServiceImpl(configurationService, networkStateService,
        lookupRepository, lookupConnectorBuilder);

    SessionRepository sessionRepository = new SessionRepositoryImpl(appContext);
    ScreenSizeProvider screenSizeProvider = new ScreenSizeProviderImpl(appContext);
    AppPropertiesProvider appPropertiesProvider = new ManifestAppPropertiesProvider(appContext);
    SessionEventGenerator sessionEventGenerator =
        new SessionEventGeneratorImpl(screenSizeProvider, appPropertiesProvider);
    sessionService = new SessionServiceImpl(lookupService, sessionRepository, sessionEventGenerator);

//    EventsRepository eventsRepository = new EventsRepositoryMock();
    Future<SQLiteDatabase> databaseFuture =
        new DatabaseInitializer(appContext, SQLiteEventsRepository.tableInitializer()).initDatabaseAsync();
    EventsRepository eventsRepository = new SQLiteEventsRepository(databaseFuture);
    EventsRestAPIConnectorBuilder eventsRestAPIConnectorBuilder = new EventsRestAPIConnectorBuilderImpl(trackingId);
    this.eventTracker = new EventTrackerImpl(trackingId, deviceId,
        configurationService, networkStateService, sessionService, lookupService,
        eventsRepository, eventsRestAPIConnectorBuilder);
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
