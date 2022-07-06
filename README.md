# Logging Overview

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.fsryan.tools/logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.fsryan.tools%22%20AND%20a:%22logging%22)

This repository contains the logging modules that you can leverage for logging within you applications. The values of the library are the following:
* Do not prescribe the values, tags, messages, or otherwise that can be logged
* Separate the destination of the log from the act of logging--allowing the consumer to determine the destination via configuration
* Loggers should be pluggable--allowing different (and even platform-specific) behaviors
* Consumers should get to determine the logging thread
* Different variants of the application should be able to be configured independently
* Logging should be relevant for both developers (app behavior) and business (user behavior)

The above values are achieved through a breakup of artifacts produced by this repository and an application of the facade pattern. The facade only gives you the capability to log an analytic event or a dev metric--it doesn't send that log anywhere. It is the consumer's responsibility to configure the logging library to send those analytics and dev metrics to the desired destination.

## Artifact Breakup

The following table describes the artifacts produced by this repositoryEach artifact has a different purpose, and they build upon one-another. The combination of libraries that you should use depends upon your environment

| Library                                                            | Analytics Framework | Platform                                       |
|--------------------------------------------------------------------|---------------------|------------------------------------------------|
| [logging](logging/README.md)                                       | nonspecific         | Android/JVM/iOS/JavaScript/Mac(X64)/Linux(X64) |
| [logging-android-appcenter3](logging-android-appcenter3/README.md) | Microsoft AppCenter | Android                                        |
| [logging-android-appcenter4](logging-android-appcenter4/README.md) | Microsoft AppCenter | Android                                        |
| [logging-android-debug](logging-android-debug/README.md)           | nonspecific         | Android                                        |
| [logging-android-firebase](logging-android-firebase/README.md)     | Google Firebase     | Android                                        |
| [logging-android-newrelic](logging-android-newrelic/README.md)     | NewRelic            | Android                                        |
| [logging-android-newrelic](logging-android-urbanairship/README.md) | Urban Airship       | Android                                        |

So, if you have an Android project, not only do you have abstractions for a logging facade ([logging](logging/README.md)), but you also have an abstraction for integrating with Microsoft AppCenter, Google Firebase, NewRelic, and Urban Airship. If you don't have an Android app, then you need to implement the [FSEventLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interfaces yourself. 

### Logging

This is the base (multiplatform) library that you'll need to include for any project (if you are using one of the other artifacts, you'll get this artifact transitively). This library contains the core logging code that consumers are intended to use when logging analytics events or dev metrics and contains the definitions of the interfaces that consumers may implement to ensure their analytics events and dev metrics are sent to the appropriate destination. Learn more here: [logging](logging/README.md).

### logging-android-debug

You should likely only use this in your app's debug configuration. It has some valuable classes for adding analytics and dev metrics logs locally to the device. For more details, see [logging-android-debug](logging-android-debug/README.md).

### logging-android-appcenter3

This contains implementations of [FSEventLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interfaces specific to Microsoft AppCenter's Analytics Android integration (version 3). For more details, see [logging-android-appcenter3](logging-android-appcenter3/README.md).
You probably shouldn't use this one, though. Use it only if you happen to still be using their major version 3. Otherwise, use the library below.

### logging-android-appcenter4

This contains implementations of [FSEventLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interfaces specific to Microsoft AppCenter's Analytics Android integration (version 3). For more details, see [logging-android-appcenter4](logging-android-appcenter4/README.md).

### logging-android-firebase

This contains implementations of [FSEventLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interfaces specific to Google Firebase's Analytics and Crashlytics Android integration. For more details, see [logging-android-firebase](logging-android-firebase/README.md).

### logging-android-newrelic

This contains implementations of [FSEventLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interfaces specific to the [NewRelic Android Agent](https://docs.newrelic.com/docs/release-notes/mobile-release-notes/android-release-notes). For more details, see [logging-android-newrelic](logging-android-newrelic/README.md).

### logging-android-urbanairship

This contains implementations of [FSEventLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) and [FSDevMetricsLogger](logging/src/commonMain/kotlin/com/fsryan/tools/logging/FSLoggers.kt) interfaces specific to the [Airship analytics](https://docs.airship.com/platform/android/analytics-and-reporting/). For more details, see [logging-android-newrelic](logging-android-urbanairship/README.md).

## Example integrations:

- [JVM project](java-testapp)
- [Kotlin Project](kotlin-testapp)
- [Android Project](android-loggingtestapp)
- [iOS Project](iosTestApp)

## Dual purpose of logging

In an ideal scenario, you would know exactly what your users wanted, how they want to interact with your application, and how the application can maximize its value prior to developing the application. Additionally, in an ideal scenario, you'd develop an application that is capable of meeting the customer need and providing maximum value. This suite of libraries attempts to provide an API around the measurement and analytics tools you use to determine success/failure at both indicators of success.

Logging events facilitates two purposes:
1. The Developer gets a sense for how the app is behaving to determine successful implementation
2. The product manager gets a sense for how users are using the app to determine possible value maximization

## Gotchas

Like other libraries, there are some gotchas with this one. See the following

> **Warning**
> In order to avoid an infinite loop, you should not log from within a logger unless you specify the `destinations` parameter that the same logger will not be reinvoked or you have otherwise guaranteed that an infinite loop will not be created.
