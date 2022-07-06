# Logging Android Debug

FSEventLogger and FSDevMetricsLogger implementations that aid in debugging your app. You probably shouldn't add this artifact for your production (release) app.

## How does this help me?

It's useful to print your logs to Logcat. So the [LogcatDevMetricsLogger](src/main/kotlin/com/fsryan/tools/logging/android/LogcatDevMetricsLogger.kt) and [LogcatEventLogger](src/main/kotlin/com/fsryan/tools/logging/android/LogcatEventLogger.kt) have been added here to allow you to use logcat to verify your logs.

However, logcat has a limit. So it's also nice to be able to log to a file and then look at it afterwards or share the file with someone else. To this end, the [InternalFileEventLogger](src/main/kotlin/com/fsryan/tools/logging/android/InternalFileEventLogger.kt) and [InternalFileDevMetricsLogger](src/main/kotlin/com/fsryan/tools/logging/android/InternalFileDevMetricsLogger.kt) have been added.

You may also want to have certain dev metrics cause your app to crash in the debug variant, whereas, you log an info, watch, or alarm message in release. To this end, the [CrashingDevMetricsLogger](src/main/kotlin/com/fsryan/tools/logging/android/CrashingDevMetricsLogger.kt) was added.


## What else could you do?

Lots. Here's one Idea:

It may be interesting to create a dev metrics logger + event logger combo that responds to user requests to upload logs to some remote server or email them or something . . . I'll leave that to someone else to write.