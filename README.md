# Qubit Android SDK

Usage of the Qubit SDK library, to provide event tracking.

# Getting started

## Dependency

In `build.gradle` of your Android application module (usually *$projectRoot/app/build.gradle*) add the following in the dependencies section:

```
dependencies   {
    compile  'com.qubit:qubit-sdk-android:1.2.1'
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

# Tracker Properties
You can get the `trackingID` and `deviceID` from the QubitSDK via the following methods:
```
QubitSDK.getTrackingId();
QubitSDK.getDeviceId();
```

## Backward compatibility

Migration from [the previous version of the SDK](https://github.com/qubitdigital/android-tracker) might be time-consuming and error-prone.

For this reason, in the current SDK you can send events in an exactly same way as before.

Example:

```java
QBTrackingManager.sharedManager().registerEvent("ecView", viewJson);
```

Note that this class and its methods are deprecated and we recommend to eventually replace them by the new versions (see: [Sending events](#sending-events)).

Example of how to replace deprecated method of sending events:

```java
QubitSDK.tracker().sendEvent(QBEvents.fromJsonString("ecView", viewJson));
```

# Logging

You can specify which level of logs from SDK do you prefer to print in Logcat. You can do it during initialization (see: [Initialization](#initialization)). The default log level is `WARN`. You can turn off logs by setting `SILENT` log level.
