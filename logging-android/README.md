# Logging Android

Android-specific concerns related to logging. In particular, interfaces for context-specific handling of logging are defined in this library.

## Features

There are two artifacts output by this library `logging-android` and `logging-android-debug`. You would want to use `logging-android` in your release configuration because it has a (slightly) smaller footprint than that of `logging-android-debug`. The main difference is that `logging-android-debug` contains The following extra loggers:
- [CrashingDevMetricsLogger](src/debug/java/com/fsryan/tools/logging/android/CrashingDevMetricsLogger.kt) - for forcing an app crash when a configurable level of log occurs (info, watch, or alarm)
- [LogcatDevMetricsLogger](src/debug/java/com/fsryan/tools/logging/android/LogcatDevMetricsLogger.kt) - for logging dev metrics to Logcat
- [InternalFileDevMetricsLogger](src/debug/java/com/fsryan/tools/logging/android/InternalFileDevMetricsLogger.kt) - for logging to a file in the internal storage directory
- [LogcatEventLogger](src/debug/java/com/fsryan/tools/logging/android/LogcatEventLogger.kt) - for logging Analytics events to Logcat
- [InternalFileEventLogger](src/debug/java/com/fsryan/tools/logging/android/InternalFileEventLogger.kt) - for logging Analytics events to a file in the internal storage directory
Both variants of the library contain the [ContextSpecificDevMetricsLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificDevMetricsLogger.kt) and [ContextSpecificEventLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) interfaces. These interfaces allow you to use an android context to either resolve the logger or resolve resources that you can use within the logger's functions. Additionally attaching the context works best in `onCreate()` of your `Application` as in the [android-firebase-testapp Application class](../android-firebase-testapp/src/main/java/com/fsryan/tools/loggingtestapp/firebase/App.kt).

## Typed Event attributes
Implementations of [ContextSpecificEventLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) can get access to information about the type of an attribute that has been attached to an event. The way you communicate this information to the underlying [ContextSpecificEventLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) is by overriding these [string-array resources](src/main/res/values/arrays.xml). These allow you to define attr names that correspond to `Double`, `Boolean`, and `Long` data. If you use them, then beware that the app will crash if you do not provide attrs that can be coerced from `String` to the appropriate type. Known examples of `FSEventLogger` implementations that make use of typed attr information are:
- [AppCenterAnalyticsEventLogger](../logging-android-appcenter3/src/main/java/com/fsryan/tools/logging/android/AppCenterAnalyticsEventLogger.kt)
- [FirebaseAnalyticsEventLogger](../logging-android-firebase/src/main/java/com/fsryan/tools/logging/android/DataDogEventLogger.kt)
- [DataDogEventLogger](../logging-android-datadog/src/main/java/com/fsryan/tools/logging/android/FirebaseAnalyticsEventLogger.kt)
- [NewRelicEventLogger](../logging-android-newrelic/src/main/java/com/fsryan/tools/logging/android/newrelic/NewRelicEventLogger.kt)
- [UrbainAirshipEventLogger](../logging-android-urbanairship/src/main/java/com/fsryan/tools/logging/android/urbanairship/UrbainAirshipEventLogger.kt)

## duration attribute name for FSEventLog timed operations:
You can specify the attribute name for any analytics events that are sent via creating timed operations by adding the following to your `AndroidManifest.xml` file:
```xml
<meta-data android:name="fsryan.log.elapsed_time_attr_name" android:value="my_timed_operation_event_attr_name" />
```