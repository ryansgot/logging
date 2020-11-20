package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSEventLog

/**
 * A means of making assertions that an event was logged.
 */
object FSLoggingAssertions {

    private val testEventLogger: TestFSEventLogger?
        get() = FSEventLog.loggersOfType(TestFSEventLogger::class.java).firstOrNull()

    @JvmStatic
    fun ensureEnvironment() {
        if (testEventLogger == null) {
            throw AssertionError("test event logger is not configured. Please add com.fsryan.tools.logging.test.TestFSEventLogger to your resources/META-INF/services/com.fsryan.tools.logging.FSEventLogger file")
        }
    }

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

    @JvmStatic
    fun assertAllStoredAttrs(
        expected: Map<String, String>,
        errorMessage: String? = null
    ) = FSCollectionAssertions.assertMapEquals(
        desc = errorMessage ?: "Stored attrs are not equal",
        expected = expected,
        actual = storedAttrs()
    )

    @JvmStatic
    fun assertAttrStored(
        attrName: String,
        expectedValue: String,
        errorMessage: String? = null
    ) {
        storedAttrs().forEach { entry ->
            if (entry.key == attrName) {
                if (entry.value != expectedValue) {
                    fail(errorMessage ?: "found attr ($attrName); expected value '$expectedValue', but was '${entry.value}")
                }
                return
            }
        }
        fail(errorMessage ?: "attr not stored; expected attrName '$attrName', expected value = $expectedValue")
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

    @JvmStatic
    fun assertAnalyticSent(
        eventName: String,
        expectedAttributes: Map<String, String>,
        errorMessage: String? = null
    ) {
        sentEvents(eventName).forEach { actual ->
            try {
                FSCollectionAssertions.assertMapEquals(
                    expected = expectedAttributes,
                    actual = actual
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