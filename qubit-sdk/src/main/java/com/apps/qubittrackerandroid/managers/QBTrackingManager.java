package com.apps.qubittrackerandroid.managers;

import com.qubit.android.sdk.api.QubitSDK;
import com.qubit.android.sdk.api.tracker.event.QBEvents;

/**
 * Class created only for temporary backward compatibility with com.apps.qubittrackerandroid:qubit_tracker library.
 * Use QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(type, jsonString)) instead.
 * @deprecated
 */
public final class QBTrackingManager {

  private QBTrackingManager() {
  }

  /**
   * Method created only for temporary backward compatibility with com.apps.qubittrackerandroid:qubit_tracker library.
   * Use QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(type, jsonString)) instead.
   * @deprecated
   */
  public static QBTrackingManager sharedManager() {
    return new QBTrackingManager();
  }

  /**
   * Method created only for temporary backward compatibility with com.apps.qubittrackerandroid:qubit_tracker library.
   * Use QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(type, jsonString)) instead.
   * @deprecated
   * @param type
   * @param data
   */
  public void registerEvent(String type, String data) {
    QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(type, data));
  }

  /**
   * Method created only for temporary backward compatibility with com.apps.qubittrackerandroid:qubit_tracker library.
   * Use QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(type, jsonString)) instead.
   * @deprecated
   * @param type
   */
  public void registerEvent(String type) {
    QubitSDK.tracker().sendEvent(QBEvents.fromJsonString(type, "{}"));
  }

}
