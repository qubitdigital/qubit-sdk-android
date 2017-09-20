package com.qubit.android.sdk.internal.session.event;

import com.qubit.android.sdk.internal.lookup.LookupData;
import com.qubit.android.sdk.internal.session.SessionData;
import com.qubit.android.sdk.internal.session.model.SessionEvent;

public interface SessionEventGenerator {

  SessionEvent generateSessionEvent(SessionData sessionData, LookupData lookupData);

}
