package com.qubit.android.sdk.internal.lookup;

import com.qubit.android.sdk.internal.common.model.IpLocation;
import java.math.BigDecimal;

public interface LookupData {

  IpLocation getIpLocation();
  String getIpAddress();
  Long getViewNumber();
  Long getSessionNumber();
  Long getConversionNumber();
  Long getEntranceNumber();
  Long getFirstViewTs();
  Long getFirstConversionTs();
  Long getLastConversionTs();
  BigDecimal getLifetimeValue();
  Long getLastViewTs();
  Long getConversionCycleNumber();

}
