# Qubit Android SDK

Usage of the Qubit tracker library, to provide event tracking.

# Getting started

## Dependency
TODO: value in compile

In your project `build.gradle` (under 'app') add the following in the dependencies section:

```
dependencies   {
    compile  '????????'
}
```

## Initialization

Provide `Context` and `tracking ID` (log level is optional - see more: [Logging](#logging)) and call `start` method to initialize SDK. You might place this code in your `Application` file:

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

## Termination

Don't forget to terminate SDK by calling `release` method. It will release all resources used by SDK, including stopping all background threads.
If you placed your initialization code in `Application`, you could do it the following way:

```java
@Override
public void onTerminate() {
    super.onTerminate();

    QubitSDK.release();
}
```

## Permissions
Qubit Android SDK needs following permissions to communicate with the server and to detect network connectivity (they are added in library manifest):

```java
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
  <uses-permission android:name="android.permission.INTERNET" />
```

# Sending Events

## UV Events
Universal Variable is Qubit’s industry standard, extensible data layer. To send a Universal Variable event, call the `sendEvent` method taking `QBEvent` object as an argument, as per the following example. The following example emits a standard “User” event, but these data can be modified to send any data you wish to send, based on Qubit's event schema

```java
QubitSDK.tracker().sendEvent(QBEvents.fromJsonString("User", userJson));
```

where `userJson` takes the example form (this may vary depending on custom schema configuration):

```
{
  userId: "leonadeoliveira",
  currency: "USD",
  email: "leona@gmail.com",
  firstName: "Leona",
  firstSession: false,
  gender: "female",
  hasTransacted: true,
  lastName: "Deoliveira",
  language: "en-gb",
  title: "Ms",
  username: "leonadv"
}
```

## Creating Events

`QBEvents` class provides several methods that allows you to create an event as `QBEvent` object.

### From a json as `String`:

Example:

```java
String jsonString = "{ \"userId\" : \"leonadeoliveira\" }";

QubitSDK.tracker().sendEvent(QBEvents.fromJsonString("User", jsonString));
```

### From `JsonObject`:

Example:

```java
JsonObject jsonObject = new JsonObject();
jsonObject.addProperty("userId", "leonadeoliveira");

QubitSDK.tracker().sendEvent(QBEvents.fromJson("User", jsonObject));
```

### From `Object`:

Example:

```java
public class MyObject {
    private String userId;

    // some code
}
```

```java
MyObject object = new MyObject();
object.setUserId("leonadeoliveira");

QubitSDK.tracker().sendEvent(QBEvents.fromObject("User", object));
```

### From `Map`:

Example:

```java
Map<String, String> mapEvent = new HashMap<>();
mapEvent.put("userId", "leonadeoliveira");

QubitSDK.tracker().sendEvent(QBEvents.fromMap("User", mapEvent));
```

## Enabling/Disabling Tracking
To disable/enable message dispatch on event occurrence use the following method:

```java
QubitSDK.tracker().enable(false); // disable
QubitSDK.tracker().enable(true); // enable
```

Note that tracking is enabled by default so you don't need to enable it if you've never disabled it anywhere.

## Backward compatibility

If you are using in your app many calls that send events, migration from [the previous version of SDK](https://github.com/qubitdigital/android-tracker) might be time-consuming and error-prone.
In case of that, to make migration easier, the `QBTrackingManager` class corresponding to the one with the same name from the old SDK is provided.
It allows to obtain `QBTrackingManager` object by `sharedManager` method and send evens using one of `registerEvent` methods.

Example:

```java
QBTrackingManager.sharedManager().registerEvent("User", userJson);
```

Note that this class and its methods are deprecated and we recommend to eventually replace them by the new versions (see: [Sending events](#sending-events)).

Example how to replace deprecated method of sending events:

```java
QubitSDK.tracker().sendEvent(QBEvents.fromJsonString("User", userJson));
```

# Logging

You can specify which level of logs from SDK do you prefer to print in Logcat. You can do it during initialization (see: [Initialization](#initialization)). The default log level is `WARN`. You can turn off logs by setting `SILENT` log level.
