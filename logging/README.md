# Logging

A multiplatform Kotlin library with basic classes that form the facade for logging analytics and dev metrics.

See [Using in your projects](#using-in-your-projects) for the instructions how to setup the dependencies in your project.

See [Configuring the logging library](#configuring-the-logging-library) for instructions how to configure once you've added the dependencies.

See [Interacting with the logging library](#interacting-with-the-logging-library) for instructions of how to use the logging library once configured.

## Design overview

TLDR: This library has the objects that implement the facade. You'll need to use them if you want to use this library.

Logging analytics and dev metrics is typically a cross-cutting concern of an app or suite of apps. In to cope with such a cross-cutting concern, one may decide to use dependency injection, but while that is advantageous from a testing perspective and for its ability to leverage logging state in multiple places, it has a disadvantage in that it complicates your dependency graph. As an alternative, one may choose to instantiate new logging objects that are bound to the scope of the objects for which they perform logging, but this comes with the disadvantage of making it difficult to share state.

> **important**
> The approach this logging tool, however, is the facade design pattern, with the facade being a configurable, singleton object that is globally accessible. The facade pattern is advantageous because you do not have to inject the facade anywhere, and it can store a logging context that gets leveraged everywhere. Furthermore, if you develop libraries, and you need logging, you can add the facade to your library without forcing consumers to log to a particular destination, and at worst, the logging lines are a no-op.

## Features

### Android Features
Contains implementations of [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) that are specific to Android. The 

#### Context-Specific abstraction for logging
If you've written an Android app, you've worked with the Android [Context](https://developer.android.com/reference/android/content/Context) class. The [ContextSpecificEventLogger](src/androidMain/kotlin/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) abstract class extends the [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interface, but allows for attaching additional values/features/configuration derived from the Android [Context](https://developer.android.com/reference/android/content/Context). The [ContextSpecificDevMetricsLogger](src/androidMain/kotlin/com/fsryan/tools/logging/android/ContextSpecificDevMetricsLogger.kt) interface has a similar purpose.

#### Typed Event attributes
Implementations of [ContextSpecificEventLogger](src/androidMain/kotlin/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) can get access to information about the type of an attribute that has been attached to an event. The way you communicate this information to the underlying [ContextSpecificEventLogger](src/main/java/com/fsryan/tools/logging/android/ContextSpecificEventLogger.kt) is by overriding these [string-array resources](src/main/res/values/arrays.xml). These allow you to define attr names that correspond to `Double`, `Boolean`, and `Long` data. If you use them, then beware that the app will crash if you do not provide attrs that can be coerced from `String` to the appropriate type. Known examples of `FSEventLogger` implementations that make use of typed attr information are:
- [AppCenterAnalyticsEventLogger (version3)](../logging-android-appcenter3/src/main/java/com/fsryan/tools/logging/android/AppCenterAnalyticsEventLogger.kt)
- [AppCenterAnalyticsEventLogger (version4)](../logging-android-appcenter4/src/main/java/com/fsryan/tools/logging/android/AppCenterAnalyticsEventLogger.kt)
- [FirebaseAnalyticsEventLogger](../logging-android-firebase/src/main/java/com/fsryan/tools/logging/android/FirebaseAnalyticsEventLogger.kt)
- [NewRelicEventLogger](../logging-android-newrelic/src/main/java/com/fsryan/tools/logging/android/newrelic/NewRelicEventLogger.kt)
- [UrbainAirshipEventLogger](../logging-android-urbanairship/src/main/java/com/fsryan/tools/logging/android/urbanairship/UrbanAirshipEventLogger.kt)

#### Automatically Added attributes
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

#### Proguard/Minification Support
You can freely use proguard in your apps, and Java's SPI (which uses reflection under-the-hood) will still work thanks to the [consumer proguard rules file](consumer-proguard-rules.pro) that gets published with the logging-android library.

### iOS Features

There are two examples that ship with the iOS logging library that leverage [NSLog](https://developer.apple.com/documentation/foundation/1395275-nslog):
* [NSLogEventLogger](src/iosMain/kotlin/com/fsryan/tools/logging/NSLogEventLogger.kt)
* [NSLogDevMetricsLogger](src/iosMain/kotlin/com/fsryan/tools/logging/NSLogDevMetricsLogger.kt)

You could add these if you want, but that would just help you to debug whether your metrics were being logged, and I don't recommend this for release app usage.

### JavaScript Features
There are no special features. You have to implement your own loggers. There are plenty of examples. See [Android Features](#android-features).

### JVM Features
There are no special features. You have to implement your own loggers. There are plenty of examples. See [Android Features](#android-features).

### Native Features
There are no special features. You have to implement your own loggers. There are plenty of examples. See [Android Features](#android-features).

## Interacting with the logging library

The facade is implemented in the following two objects:
* [FSDevMetrics](src/commonMain/kotlin/com/fsryan/tools/logging/FSDevMetrics.kt)
* [FSEventLog](src/commonMain/kotlin/com/fsryan/tools/logging/FSEventLog.kt)

If you want to log a dev metric, for example, you don't need to interact with a logger that logs to a specific destination. Instead, interact with the [FSDevMetrics](src/commonMain/kotlin/com/fsryan/tools/logging/FSDevMetrics.kt) singleton.

```kotlin
FSDevMetrics.info(msg = "The info log message", attrs = mapOf("customAttr1" to "value1", "customAttr2" to "value2"))
```

The info metric will be sent out to all configured [FSDevMetricsLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt)s because the `destinations` argument has not been supplied.

And if you want to log an analytics event, the same concept applies:

```kotlin
FSEventLog.addEvent(eventName = "my_event_name", attrs = mapOf("customAttr1" to "value1", "customAttr2" to "value2"))
```

## Configuring the logging library

The point of the facade ([FSDevMetrics](src/commonMain/kotlin/com/fsryan/tools/logging/FSDevMetrics.kt and [FSEventLog](src/commonMain/kotlin/com/fsryan/tools/logging/FSEventLog.kt)) is to provide an interface for logging, distributing the task of logging to the configured loggers. A logger is an implementation of one of the following interfaces:
* [FSDevMetricsLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt)
* [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt)

Instances of those interfaces must be added to [FSEventLog](src/commonMain/kotlin/com/fsryan/tools/logging/FSEventLog.kt) and [FSDevMetrics](src/commonMain/kotlin/com/fsryan/tools/logging/FSDevMetrics.kt) respectively.

### In a non-JVM/Android project

At the moment, there is no solely build-based mechanism to configure your [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) instances (when not on JVM/Android). So as early as possible in your application's lifecycle, you should register an implementation of the [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt):
```kotlin
val myEventLogger = /* construct your FSEventLogger instance here */
FSEventLog.addLogger(myEventLogger)
```
Or for adding a dev metrics loggers
```kotlin
val myDevMetricsLogger = /* construct your FSDevMetricsLogger instance here */
FSDevMetrics.addLogger(myDevMetricsLogger)
```

> **Note**
> Loggers are identified by their id. If you add two logger instances with the same ID, the last one will overwrite the first one. If you add two loggers that log to the same destination, you'll get duplicated logs.

### In a JVM/Android project

While it is possible to configure the logging library the same ways as [in a non-JVM/Android project](#in-a-jvmandroid-project), you may also leverage [Java SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) to configure loggers.

To do so:
1. Create a plaintext file like this:
   ```
   app
   +--src
      +--main
         +--resources
            +--META-INF
               +--services
                  +--com.fsryan.tools.logging.FSEventLogger
   ```
2. Add one line to this file per instance of [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) that you wish to instantiate and use for logging. The class must not be abstract, and the fully-qualified class name is required. The order in which these loggers are listed is the same as the order in which they will be invoked.

> **Note**
> Why SPI? This is especially advantageous when you have both a debug and release variant of your app with different logging concerns or if you have multiple flavors of your app that need to log to different destinations because each flavor/build type may have different configuration files.

For logging dev metrics, follow the same pattern:
1. Create a plaintext file like this:
   ```
   app
   +--src
      +--main
         +--resources
            +--META-INF
               +--services
                  +--com.fsryan.tools.logging.FSDevMetricsLogger
   ```
2. Add one line to this file per instance of [FSDevMetricsLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) that you wish to instantiate and use for logging. The class must not be abstract, and the fully-qualified class name is required. The order in which these loggers are listed is the same as the order in which they will be invoked.

> **Note**
> If you use Java SPI to configure logging, your [FSEventLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) implementations must have a no-arg constructor. 

## Using in your projects

> **Note**
> This library is not yet 100% stable and may undergo some interface changes before a full 1.0.0 release.

The library is published to Maven Central.

The library is compatible with the Kotlin Standard Library. Kotlin lower than `1.3.0` may not work properly.

### Gradle

- Add the Maven Central repository if it is not already there:

```kotlin
repositories {
    mavenCentral()
}
```

- In multiplatform projects, add a dependency to the commonMain source set dependencies
```kotlin
kotlin {
    sourceSets {
        commonMain {
             dependencies {
                 implementation("com.fsryan.tools:logging:0.4.0")
             }
        }
    }
}
```

- To use the library in a single-platform project, add a dependency to the dependencies block.

```groovy
dependencies {
    implementation("com.fsryan.tools:logging:0.4.0")
}
```