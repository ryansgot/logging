package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSEventLog
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A means of making assertions that an event was logged.
 */
object FSLoggingAssertions {

    private val testEventLogger: TestFSEventLogger?
        get() = FSEventLog.testLogger() as? TestFSEventLogger

    /**
     * Check whether the test logger has been added. Note that this _DOES NOT_
     * force you to make logging synchronous in your tests because, ostensibly,
     * you could have a good reason for multi-threading your tests.
     */
    @JvmStatic
    fun ensureEnvironment() {
        if (testEventLogger == null) {
            throw AssertionError("test event logger is not configured. Please add com.fsryan.tools.logging.test.TestFSEventLogger to your resources/META-INF/services/com.fsryan.tools.logging.FSEventLogger file")
        }
    }

    /**
     * Wipe out the data stored in the test event logger. You should do this
     * between each test invocation or apply the [FSLoggingTestExtension]
     * extension.
     */
    @JvmStatic
    fun resetTestFSEventLogger() = testEventLogger?.reset()

    @JvmStatic
    fun assertTimesIncrementedCountableAttr(
        attrName: String,
        expected: Int,
        errorMessage: String? = null
    ) {
        val actual = timesIncrementedCountOfAttr(attrName)
        if (expected != actual) {
            throw AssertionError(errorMessage ?: "Expected incremented attr ($attrName) $expected times, but was $actual")
        }
    }

    /**
     * Assert the stored attributes (added with [FSEventLog.addAttr] or
     * [FSEventLog.addAttrs]). All of the [expected] attrs must be present.
     */
    @JvmStatic
    @Deprecated(message = "this is an older, inflexible API", replaceWith = ReplaceWith("assertAllAttrsStored"))
    fun assertAllStoredAttrs(
        expected: Map<String, String>,
        errorMessage: String? = null
    ) = assertAllAttrsStored(expectedAttrs = expected, errorMessage = errorMessage)

    @JvmStatic
    fun assertAllAttrsStored(
        expectedAttrs: Map<String, String>,
        allowUnexpectedAttrNames: Boolean = false,
        errorMessage: String? = null
    ) {
        FSCollectionAssertions.assertMapContents(
            desc = errorMessage ?: "Stored attrs are not equal",
            expectedContents = expectedAttrs,
            actual = storedAttrs(),
            allowExcess = allowUnexpectedAttrNames
        )
    }

    @JvmStatic
    fun assertAttrStored(attrName: String, expectedValue: String, errorMessage: String? = null) {
        FSCollectionAssertions.assertMapContains(
            desc = errorMessage,
            expectedKey = attrName,
            expectedValue = expectedValue,
            actualValues = storedAttrs()
        )
    }

    @JvmStatic
    fun assertAnalyticSequence(
        eventName: String,
        expectedSequence: List<Map<String, String>>
    ) {
        val actual = sentEvents(eventName)
        expectedSequence.forEachIndexed { index, expected ->
            if (index == actual.size) {
                fail("expected event at position $index, but it did not exist; expected size = ${expected.size}; actual size = ${actual.size}")
            }
            FSCollectionAssertions.assertMapEquals(
                desc = "attrs for event ($eventName) at position $index did not match",
                expected = expected,
                actual = actual[index]
            )
        }
    }

    /**
     * Asserts that no analytics were sent matching an event name
     */
    @JvmStatic
    fun assertNoAnalyticsSentWithName(eventName: String) {
        FSCollectionAssertions.assertListEquals(
            expected = emptyList(),
            actual = sentEvents(eventName)
        )
    }

    @JvmStatic
    @JvmOverloads
    fun assertAnalyticSent(
        eventName: String,
        expectedAttributes: Map<String, String>,
        errorMessage: String? = null,
        allowUnexpectedAttrNames: Boolean = false
    ) {
        sentEvents(eventName).forEach { actual ->
            try {
                FSCollectionAssertions.assertMapContents(
                    expectedContents = expectedAttributes,
                    actual = actual,
                    allowExcess = allowUnexpectedAttrNames
                )
                return
            } catch (assertionError: AssertionError) {
                // do nothing
            }
        }
        fail(errorMessage ?: "analytic not sent; expected eventName '$eventName', attrs = $expectedAttributes")
    }

    @JvmStatic
    fun timesIncrementedCountOfAttr(attrName: String): Int = testEventLogger?.timesAttrValueIncremented(attrName) ?: 0

    @JvmStatic
    fun storedAttrs(): Map<String, String> = testEventLogger?.storedAttrs() ?: emptyMap()

    @JvmStatic
    fun sentEvents(eventName: String): List<Map<String, String>> = testEventLogger?.sentEvents(eventName) ?: emptyList()
}