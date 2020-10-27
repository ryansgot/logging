# Logging Android NewRelic

Builds upon the [logging](../logging/README.md) and [logging-android](../logging-android/README.md) libraries, providing specific loggers for New Relic.

## Configuring

You can use the android manifest to configure NewRelic.

### The New Relic GENERATED_TOKEN
In order to keep your app's New Relic GENERATED_TOKEN out of source control, you should use a gradle property and then add it to your manifest using the android gradle plugin's manifest placeholders as below:
```kotlin
  android {
    defaultConfig {
      manifestPlaceholders["myapp.my_newrelic_generated_token"] = project.findProperty("myapp.newrelic.token") ?: ""
    }
  }
```

You then need to supply the project property some way that [gradle supports](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties).

You MUST then add the following meta-data to your AndroidManifest.xml:

```xml
<application>
  <meta-data android:name="fsryan.nrgt" android:value="${myapp.my_newrelic_generated_token}" />
</application>
```

You can, of course, set your newrelic token as a a plaintext string in your project's repository. You shouldn't do this, though.

### New Relic Event Types
You can configure the event type of the [NewRelicEventLogger](src/main/java/com/fsryan/tools/logging/android/newrelic/NewRelicEventLogger.kt) with the following meta data:
```xml
<application>
  <meta-data android:name="fsryan.nr_event_type" android:value="AnlayticsEventType" />
</application>
```
You can configure the event type(s) of the [NewRelicDevMetricsLogger](src/main/java/com/fsryan/tools/logging/android/newrelic/NewRelicDevMetricsLogger.kt) with the following meta data:
```xml
<application>
  <meta-data android:name="fsryan.nr_dev_metric_info_event_type" android:value="InfoEventType" />
  <meta-data android:name="fsryan.nr_dev_metric_watch_event_type" android:value="WatchEventType" />
</application>
```

### Other configurable options (none are required, and if you do not supply an option, the new relic default will be applied):

You don't have to set any of the below, and you can, of course, configure new relic any way you want without using this app. This data is set in the same way as fsryan.nrgt.

* New Relic Feature Flags
  * fsryan.nrffae -> boolean to enable/disable `FeatureFlag.AnalyticsEvents`
  * fsryan.nrffcre -> boolean to enable/disable `FeatureFlag.CrashReporting`
  * fsryan.nrffhe -> boolean to enable/disable `FeatureFlag.HandledExceptions`
  * fsryan.nrffit -> boolean to enable/disable `FeatureFlag.InteractionTracing`
  * fsryan.nrffdi -> boolean to enable/disable `FeatureFlag.DefaultInteractions`
  * fsryan.nrffnr -> boolean to enable/disable `FeatureFlag.NetworkRequests`
  * fsryan.nrffner -> boolean to enable/disable `FeatureFlag.NetworkErrorRequests`
  * fsryan.nrffhrbce -> boolean to enable/disable `FeatureFlag.HttpResponseBodyCapture`
* New Relic Builder options
  * fsryan.nrae -> boolean to enable/disable analytics events
  * fsryan.nrhrbce -> boolean to enable/disable HTTP Response body capturing
  * fsryan.nrcre -> boolean to enable/disable crash reporting
  * fsryan.nrav -> String to override the default application version
  * fsryan.nrab -> String to override the default application build
  * fsryan.nrle -> boolean to enable/disable logging
  * fsryan.nrll -> String to set the log level; can be one of ERROR, WARNING, INFO, VERBOSE, DEBUG, AUDIT
  * fsryan.nrca -> String to override the default collector address
  * fsryan.nrcca -> String to override the default crash collector address
