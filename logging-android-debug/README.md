# Logging Android Debug

Android-specific concerns related to logging that you should use if you want extra loggers for . In particular, useful loggers for debugging on Android are included in this library.

## Features

You would want to use `logging-android-debug` in your debug configuration because it contains the following extra loggers:
- [CrashingDevMetricsLogger](src/main/kotlin/com/fsryan/tools/logging/android/CrashingDevMetricsLogger.kt) - for forcing an app crash when a configurable level of log occurs (info, watch, or alarm)
- [LogcatDevMetricsLogger](src/main/kotlin/com/fsryan/tools/logging/android/LogcatDevMetricsLogger.kt) - for logging dev metrics to Logcat
- [InternalFileDevMetricsLogger](src/main/kotlin/com/fsryan/tools/logging/android/InternalFileDevMetricsLogger.kt) - for logging to a file in the internal storage directory
- [LogcatEventLogger](src/main/kotlin/com/fsryan/tools/logging/android/LogcatEventLogger.kt) - for logging Analytics events to Logcat
- [InternalFileEventLogger](src/main/kotlin/com/fsryan/tools/logging/android/InternalFileEventLogger.kt) - for logging Analytics events to a file in the internal storage directory

It's arguable that any of these loggers have any place in a release configuration.

## Typed Event attributes
Implementations of [ContextSpecificEventLogger](../logging/src/androidMain/kotlin/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) can get access to information about the type of an attribute that has been attached to an event. The way you communicate this information to the underlying [ContextSpecificEventLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) is by overriding these [string-array resources](src/main/res/values/arrays.xml). These allow you to define attr names that correspond to `Double`, `Boolean`, and `Long` data. If you use them, then beware that the app will crash if you do not provide attrs that can be coerced from `String` to the appropriate type. Known examples of `FSEventLogger` implementations that make use of typed attr information are:
- [AppCenterAnalyticsEventLogger](../logging-android-appcenter3/src/main/java/com/fsryan/tools/logging/android/AppCenterAnalyticsEventLogger.kt)
- [FirebaseAnalyticsEventLogger](../logging-android-firebase/src/main/java/com/fsryan/tools/logging/android/FirebaseAnalyticsEventLogger.kt)
- [NewRelicEventLogger](../logging-android-newrelic/src/main/java/com/fsryan/tools/logging/android/newrelic/NewRelicEventLogger.kt)
- [UrbainAirshipEventLogger](../logging-android-urbanairship/src/main/java/com/fsryan/tools/logging/android/urbanairship/UrbanAirshipEventLogger.kt)

## Automatically Added attributes
There are two attributes that will get added automatically:
1. `app_uptime` -> the amount of time your application has been up.
2. `timestamp` -> the time that the event was logged (a long value representing the device's epoch time)

You can override these attribute names by adding the following string resources:
```xml
<resources>
  <string name="fs_logging_app_uptime_attr_name" translatable="false">MyUptimeAttrName</string>
  <string name="fs_logging_timestamp_attr_name" translatable="false">MyTimestampAttrName</string>
</resources>
```