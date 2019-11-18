# Logging Android

Android-specific concerns related to logging. In particular, interfaces for context-specific handling of logging are defined in this library.

## Features

There are two artifacts output by this library `logging-android` and `logging-android-debug`. You would want to use `logging-android` in your release configuration because it has a (slightly) smaller footprint than that of `logging-android-debug`. The main difference is that `logging-android-debug` contains The following extra loggers:
- [CrashingDevMetricsLogger](src/debug/java/com/fsryan/tools/logging/android/CrashingDevMetricsLogger.kt) - for forcing an app crash when a configurable level of log occurs (info, watch, or alarm)
- [LogcatDevMetricsLogger](src/debug/java/com/fsryan/tools/logging/android/LogcatDevMetricsLogger.kt) - for logging dev metrics to Logcat
- [LogcatEventLogger](src/debug/java/com/fsryan/tools/logging/android/LogcatEventLogger.kt) - for logging Analytics events to Logcat

Both variants of the library contain the [ContextSpecificDevMetricsLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificDevMetricsLogger.kt) and [ContextSpecificEventLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) interfaces. These interfaces allow you to use an android context to either resolve the logger or resolve resources that you can use within the logger's functions. Additionally attaching the context works best in `onCreate()` of your `Application` as in the [android-firebase-testapp Application class](../android-firebase-testapp/src/main/java/com/fsryan/tools/loggingtestapp/firebase/App.kt). 