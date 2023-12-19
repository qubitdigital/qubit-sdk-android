# Qubit Android SDK

Installation of the QubitSDK, to provide event tracking and lookup. To make use of this SDK, please contact your Qubit Customer Success representative.

# Releases

| VERSION | UPDATES |
|---|---|
| 2.0.2 | Added ability to set custom device identifier.
| 2.0.1 | Resolve caching issue when campaigns are paused.
| 2.0.0 | Major release, bringing support for Placement API. Upgrade to 2.* to use this feature.
| 1.4.1 | Handle potential regression where /experiences endpoint does not return expected payload.
| 1.4.0 | Production release for A/B testing & data collection.


# Getting started

## Dependency

In `build.gradle` of your Android application module (usually *$projectRoot/app/build.gradle*) add the following in the dependencies section:

```
dependencies   {
    compile  'com.qubit:qubit-sdk-android:2.0.2'
}
```

## Initialization

Provide application context and `tracking ID` (log level is optional - see more: [Logging](#logging)) and call `start` method to initialize SDK. You might place this code in your `Application` file:

```java
@Override
public void onCreate() {
    super.onCreate();

    QubitSDK.initialization()
        .inAppContext(this)
        .withTrackingId("YOUR_TRACKING_ID")
        .withLogLevel(QBLogLevel.DEBUG)
        .start();
}
```

## Permissions
Qubit's Android SDK needs the following permissions to communicate with the server and to detect network connectivity (they are added in library manifest):

```java
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
  <uses-permission android:name="android.permission.INTERNET" />
```

Note that you don't have to add these permissions to manifest of your application.

## Custom device identifier

By default Qubit SDK uses system `Settings.Secure.ANDROID_ID` value as a device identifier. However it is possible to use custom value instead by calling `QubitSDK.restartWithCustomDeviceId("yourNewDeviceID")`. 
Calling `restartWithCustomDeviceId(null)` restores default behaviour. 
Changing the device identifier once the SDK has already been started will restart the SDK. 
This will clear all caches and resend startup events.

To retrieve your currently set `trackingId` and `deviceId`, you use the corresponding getters:

```java
System.out.println(QubitSDK.getTrackingId());
System.out.println(QubitSDK.getDeviceId());
```

# Send events

## Sending events
QProtocol (QP) is Qubit’s industry standard, extensible data layer. To send a QP event, call the `sendEvent` method taking `QBEvent` object as an argument, as per the following example. The following example emits a standard `ecView` event, but this data can be modified to send any data you wish to send, based on Qubit's event schema:

```java
QubitSDK.tracker().sendEvent(QBEvents.fromJsonString("ecView", viewJson));
```

where `viewJson` takes the example form (this may vary depending on custom schema configuration):

```
{
    "type": "home",
    "subtypes": ["Women", "Dresses", "Cocktail Dresses"]
}
```

## Creating events

`QBEvents` class provides several methods that allows you to create an event as `QBEvent` object.

### From a json as `String`:

Example:

```java
String jsonString = "{ \"type\" : \"home\" }";

QubitSDK.tracker().sendEvent(QBEvents.fromJsonString("ecView", jsonString));
```

### From `JsonObject`:

Example:

```java
JsonObject jsonObject = new JsonObject();
jsonObject.addProperty("type", "home");

QubitSDK.tracker().sendEvent(QBEvents.fromJson("ecView", jsonObject));
```

### From `Object`:

Example:

```java
public class EcViewEventData {
    private String type;

    // some code
}
```

```java
EcViewEventData object = new EcViewEventData();
object.setType("home");

QubitSDK.tracker().sendEvent(QBEvents.fromObject("ecView", object));
```

## Enabling/disabling tracking
To disable/enable message dispatch on event occurrence use the following method:

```java
QubitSDK.tracker().enable(false); // disable
QubitSDK.tracker().enable(true); // enable
```

Note that tracking is enabled by default so you don't need to enable it if you've never disabled it anywhere.

# Experiences

Use `getExperiences()` to integrate Experiences into your app.

Kotlin snippet:
```kotlin    
QubitSDK.getExperiences(
    listOfExperienceIds,
    { experienceList -> experienceList.forEach { it.shown() } },
    { throwable -> Log.e(TAG, "Error: ", throwable) },
    222,
    false,
    true
)
```

Java snippet:
```java
QubitSDK.getExperiences(
    listOfExperienceIds,
    experienceList -> {
      for (Experience experience : experienceList) {
        experience.shown();
      }
      return Unit.INSTANCE;
    },
    throwable -> {
      Log.d(TAG, throwable.toString());
      return Unit.INSTANCE;
    }, 222, false, true
);
```

where `variation`, `preview`, `ignoreSegments` are optional parameters.

# Placements

Use `getPlacement()` to add Qubit Placements into your app. 

## Kotlin

```kotlin
QubitSDK.getPlacement(
    "83f6b528-9336-11eb-a8b3",
    PlacementMode.LIVE,
    PlacementPreviewOptions("1ybrhki9RvKWpA", "AUuQ_8z7SV-Fw"),
    { placement ->

        // get our payload
        placement?.content

        // send an impression event
        placement?.impression()

        // send a clickthrough event
        placement?.clickthrough()
    },
    { throwable -> Log.e(TAG, "Failed to fetch placement", throwable) }
)
```
A placement has two callbacks defined: `impression` and `clickthrough`. These can be invoked explicitly from `Placement` object:

```
placement.impression()
placement.clickthrough()
```

or through separate method from `QubitSDK` which expects URL to be requested:

```
QubitSDK.sendCallbackRequest(placement.impressionUrl)
```


# Tracker Properties
You can get the `trackingID` and `deviceID` from the QubitSDK via the following methods:
```
QubitSDK.getTrackingId();
QubitSDK.getDeviceId();
```

# Logging

You can specify which level of logs from SDK do you prefer to print in Logcat. You can do it during initialization (see: [Initialization](#initialization)). 

The default log level is `WARN`. You can turn off logs by setting `SILENT` log level.
