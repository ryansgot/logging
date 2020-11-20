# Logging-Test

Contains the base objects/classes associated with testing logging including a test configuration for logging, [FSTestLoggingConfig](src/main/java/com/fsryan/tools/logging/test/FSTestLoggingConfig.kt), and an [FSEventLogger for testing](src/main/java/com/fsryan/tools/logging/test/TestFSEventLogger.kt).

## How To
Declare a dependency upon logging-test in your build.gradle file's testImplementation configuration:
```groovy
testImplementation "com.fsryan.tools:logging-test:$fs_logging_version"
```

Add a `com.fsryan.tools.logging.FSLoggingConfig` file to your test resources to declare the testing config (usually src/test/resources/META-INF/services/) with the line below:
```
com.fsryan.tools.logging.test.FSTestLoggingConfig
```

Add a `com.fsryan.tools.logging.FSEventLogger` file to your test resources to declare the testing config (usually src/test/resources/META-INF/services/) with the line below:
```
com.fsryan.tools.logging.test.TestFSEventLogger
```