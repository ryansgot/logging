# Logging-Test

Contains the base objects/classes associated with testing logging, an [FSEventLogger for testing](src/main/java/com/fsryan/tools/logging/test/TestFSEventLogger.kt).

## How To
Declare a dependency upon logging-test in your build.gradle file's testImplementation configuration:
```kotlin
testImplementation("com.fsryan.tools:logging-test:$fs_logging_version")
// for connected android tests
androidTestImplementation("com.fsryan.tools:logging-test:$fs_logging_version")
// for multiplatform
kotlin {
    sourceSets {
        /* . . . */
        val commonTest by getting {
            dependencies {
                implementation("com.fsryan.tools:logging-test:$fs_logging_version")
            }
        }
        /* . . . */
    }
}
```

> **Note**
> For Java/Android, DO NOT add `com.fsryan.tools.logging.FSEventLogger` file to your test resources to declare the testing config (usually src/test/resources/META-INF/services/)

## Usage
All tests use a shared instance of the test logger. This means you need to clear the test logger each test.

Therefore, for JUnit4 you should: put this in your test class:

```
@Before
fun clearTestLogger() {
    FSLoggingAssertions.ensureEnvironment()
    FSLoggingAssertions.resetTestFSEventLogger()
}
```

For JUnit5, you should put this in your test class
```
class MyTest {

    @BeforeEach
    fun configureLogging() {
        FSLoggingAssertions.ensureEnvironment()
        FSLoggingAssertions.resetTestFSEventLogger()
    }
    
    @Test
    fun someTest() {
        doSomethingWithLogging()
        FSLoggingTestAssertions.assertAnalyticSent("event name", expectedAttributes = mapOf())
    }
}
```

> **Note**
> The JUnit5 ExtendWith annotation is no longer supported. If this is desirable, I'll add it back.
