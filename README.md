# Logging Overview

This repository contains the logging modules that you can leverage for logging within you applications. The values of the library are the following:
* Do not prescribe the values, tags, messages, or otherwise that can be logged
* Separate the destination of the log from the act of logging--allowing the consumer to determine the destination via plugins
* Loggers should be pluggable--allowing different (and even platform-specific) behaviors
* Consumers should get to determine the logging thread
* Different variants of the application should be able to be configured independently from one another.
* Logging should be relevant for both developers (app behavior) and business (user behavior)

## Dual purpose of logging

In an ideal scenario, you would know exactly what your users wanted, how they want to interact with your application, and how the application can maximize its value prior to developing the application. Additionally, in an ideal scenario, you'd develop an application that is capable of meeting the customer need and providing maximum value. This suite of libraries attempts to provide an API around the measurement and analytics tools you use to determine success/failure at both indicators of success.

Logging events facilitates two purposes:
1. The Developer gets a sense for how the app is behaving to determine successful implementation
2. The product manager gets a sense for how users are using the app to determine possible value maximization

## Artifact Breakup

There are six artifacts produced by this project:
1. group: com.fsryan.tools, artifact: logging, packaging: jar
2. group: com.fsryan.tools, artifact: logging-android, packaging: aar
3. group: com.fsryan.tools, artifact: logging-android-appcenter3, packaging: aar
4. group: com.fsryan.tools, artifact: logging-android-firebase, packaging: aar
5. group: com.fsryan.tools, artifact: logging-android-datadog, packaging: aar
6. group: com.fsryan.tools, artifact: logging-android-newrelic, packaging: aar

Each artifact has a different purpose, and they build upon one-another. The combination of libraries that you should use depends upon your environment

| Library                    | Analytics Framework | Platform    |
| -------------------------- | ------------------- | ----------- |
| logging                    | nonspecific         | JVM/Android |
| logging-android            | nonspecific         | Android     |
| logging-android-appcenter3 | Microsoft AppCenter | Android     |
| logging-android-firebase   | Google Firebase     | Android     |
| logging-android-datadog    | Datadog             | Android     |
| logging-android-newrelic   | NewRelic            | Android     |
| logging-test               | None                | JVM/Android |

So, if you have a JVM project, then you can only use the logging library at this time. However, if you have an Android project, then you can at least get some Android-specific behaviors. The extent to which you can benefit depends upon whether you either use Google Firebase, Microsoft AppCenter, DataDog, or NewRelic.

### Logging artifact

The logging artifact contains code that is not bound to the android framework. This is the core logging code that consumers are intended to interface with as well as interfaces that consumers are intended to implement. It will happily run on the JVM or android runtime just as easily. This artifact facilitates the developer and the business concerns of logging. The [FSEventLog](logging/src/main/java/com/fsryan/tools/logging/FSEventLog.kt) object supports the business concerns of logging--tracking user behaviors, events, etc. The [FSDevMetrics](logging/src/main/java/com/fsryan/tools/logging/FSDevMetrics.kt) object supports the developer concerns of logging--tracking alarming conditions, conditions the developers want to watch, and other info the developers are concerned about.

In order to log something that tracks user behavior, consumers must register an implementation of the [FSEventLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) via the application's resources via the `META-INF/services/com.fsryan.tools.logging.FSEventLogger` resources file. Each line of this file must be the fully-qualified class name of the [FSEventLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) implementation. The order in which these loggers are listed is the same as the order in which they will be invoked.

The process of logging something for developer purposes is similar, however, consumers must register an implementation of [FSDevMetricsLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) via the applications's resources via `META-INF/services/com.fsryan.tools.logging.FSDevMetricsLogger`

In order to avoid an infinite loop, you should not log from within a logger unless you filter out the destination that is performing the logging. [FSEventLog](logging/src/main/java/com/fsryan/tools/logging/FSEventLog.kt) and [FSDevMetrics](logging/src/main/java/com/fsryan/tools/logging/FSDevMetrics.kt)'s public API allow you to specify which destinations you want to log to. These destinations are resolved by the ID logger. 

### logging-android artifact

This contains implementations of [FSEventLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) that are specific to Android.

### logging-android-appcenter artifact

This contains implementations of [FSEventLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) that are specific to Microsoft AppCenter's Analytics Android integration. The AppCenter crashes and analytics libraries do not have a built-in way to ensure the libraries are initialized early on in the application's lifecycle, so the [AppCenterInitializationProvider](logging-android-appcenter/src/main/java/com/fsryan/tools/logging/android/AppCenterInitializationProvider.kt) was added to this library to fill that void.

### logging-android-firebase artifact

This contains implementations of [FSEventLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) that are specific to Google Firebase's Analytics and Crashlytics Android integration.

## How to register a Logger

If you're using a gradle project, then your project's resources are going to be found in `src/main/resources` (unless you have customized the source set). Assuming the default location, you'll need to add the following files: 
* `src/main/resources/META-INF/services/com.fsryan.tools.logging.FSEventLogger`
* `src/main/resources/META-INF/services/com.fsryan.tools.logging.FSDevMetricsLogger`

The lines of each file should be the fully-qualified names of the classes that implement the [FSEventLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/main/java/com/fsryan/tools/logging/FSLoggers.kt) interfaces respectively.

## How to configure logging

Currently, you can only configure the thread on which logging occurs. This is especially useful for testing. See [FSTestLoggingConfig](logging-test/src/main/java/com/fsryan/tools/logging/test/FSTestLoggingConfig.kt) for an example that performs logging synchronously. However, you can configure a custom configuration by adding the `src/main/resources/META-INF/services/com.fsryan.tools.logging.FSLoggingConfig` file in your resources and providing one line that has your custom implementation of the `FSLoggingConfig` interface. This will allow you to choose the executor that executes the logging work. I recommend using a single-threaded executor. However, if you fail to register your configuration, then a default single threaded `Executor` will be created by this library to handle the work associated with logging. You should be especially mindful of the executor being used to log when you write custom loggers. Additionally, you can declare whether your app is running in test mode or not.

## Example integrations:

- [JVM project](java-testapp)
- [Kotlin Project](kotlin-testapp)
- [Android Project](android-loggingtestapp)
