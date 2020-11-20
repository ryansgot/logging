# Logging

Contains the base objects/classes associated with logging. The two main objects you need to know at logging call sites are:
- [FSDevMetrics](src/main/java/com/fsryan/tools/logging/FSDevMetrics.kt)
- [FSEventLog](src/main/java/com/fsryan/tools/logging/FSEventLog.kt)

These objects provide the API necessary to perform logging analytics and developer-related events. You plug logging behaviors in by registering loggers via Java SPI. The two interfaces you'll need to implement to actually perform logging are:
- [FSDevMetricsLogger](src/main/java/com/fsryan/tools/logging/FSLogging.kt)
- [FSEventLogger](src/main/java/com/fsryan/tools/logging/FSLogging.kt)

The additional libraries that are a part of this same repository will contain some examples of implementations of the above interface.

The loggers you register via Java SPI will get invoked in the order they are declared in their respective `META-INF/services` file. You can also configure the `Executor` that actually executes logging as well via Java SPI. The interface you'll need to implement for this is [FSLoggingConfig](src/main/java/com/fsryan/tools/logging/FSLogging.kt)