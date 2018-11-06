package com.qubit.android.sdk.internal.lookup;

public interface LookupService {

  void registerLookupListener(LookupListener listener);
  void unregisterLookupListener(LookupListener listener);

  interface LookupListener {
    void onLookupDataChange(LookupData lookupData);
  }
}
