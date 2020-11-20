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

## Usage
All tests use a shared instance of the test logger :(. I'm sorry--this is done for expedience at the moment. Eventually, we'll create a means of attaching new instances of test loggers to the underlying logging mechansims.

Therefore, for JUnit4 you should: put this in your test class:

```
@Before
fun clearTestLogger() {
    FSLoggingAssertions.resetTestFSEventLogger()
}
```

This library provides an extension for JUnit5 that handles the lifecycle for you. You can annotate you test this way:
```
@ExtendWith(FSLoggingTestExtension::class)
class MyTest {

    @Test
    fun someTest() {
        doSomethingWithLogging()
        FSLoggingTestAssertions.assertAnalyticSent("event name", expectedAttributes = mapOf())
    }
}
```