package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSEventLog
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * A means of making assertions that an event was logged as well as logging
 * context attributes got logged.
 */
object FSLoggingAssertions {

    /**
     * Check whether the test logger has been added. Note that this _DOES NOT_
     * force you to make logging synchronous in your tests because, ostensibly,
     * you could have a good reason for multi-threading your tests.
     */
    @JvmStatic
    fun ensureEnvironment() {
        FSEventLog.addLogger(TestFSEventLogger)
    }

    /**
     * Wipe out the data stored in the test event logger. You should do this
     * between each test invocation.
     */
    @JvmStatic
    fun resetTestFSEventLogger() {
        TestFSEventLogger.reset()
    }

    /**
     * Asserts that the attr with name was incremented the expected number of
     * times.
     *
     * @param attrName the name of the attribute for which you're checking the
     * increment count
     * @param expected the expected number of times the attribute was
     * incremented
     */
    @JvmStatic
    fun assertTimesIncrementedCountableAttr(attrName: String, expected: Int) {
        val actual = TestFSEventLogger.timesAttrValueIncremented(attrName)
        assertEquals(
            expected,
            actual,
            message = "Expected $expected times to increment attr '$attrName', but was $actual"
        )
    }

    /**
     * Asserts that all of the expected attrs are added to the logging
     * context. There are two modes of this assertion: strict and non-strict.
     * This function assumes non-strict by default.
     *
     * @param expectedAttrs the attrs you expect to be added to the logging
     * context
     * @param strict whether the [expectedAttrs] are the only attrs that should
     * be added to the logging context, defaults to `false`
     */
    @JvmStatic
    @JvmOverloads
    fun assertAllAttrsAdded(expectedAttrs: Map<String, String>, strict: Boolean = false) {
        createAttrErrorMessage(expectedAttrs, TestFSEventLogger.storedAttrs(), strict)?.let {
            fail(it)
        }
    }

    /**
     * Asserts that an attr was added to the logging context with the correct
     * value
     *
     * @param attrName the name of the attr you're expecting to have been added
     * @param expectedValue the expected value of the attr you're expecting to
     * have been added.
     */
    @JvmStatic
    fun assertAttrAdded(attrName: String, expectedValue: String) {
        var found: String? = null
        val storedAttrs = TestFSEventLogger.storedAttrs()
        storedAttrs.forEach { (key, actual) ->
            if (key == attrName) {
                found = actual
            }
        }
        if (found == null) {
            fail("expected attr '$attrName'='$expectedValue'; but was not present in attrs: $storedAttrs")
        }
        if (found != expectedValue) {
            fail("expected attr '$attrName'='$expectedValue'; attr with same name was found with wrong value: '$found'")
        }
    }

    /**
     * Asserts analytics for an event are sent in sequence. This checks that
     * both the event names and attributes are in the exact order that you sent
     *
     * @param expectedSequence the [TestSentEvent]s that you're expecting to be
     * sent in the order you're expecting the events to be sent
     * @param strictAttrs whether the attr matching must be strict, defaults to
     * false.
     * @param strict whether the [expectedSequence] are the only events that
     * may be logged. Defaults to false, allowing additional events.
     */
    @JvmStatic
    @JvmOverloads
    fun assertAnalyticSequenceForEvent(expectedSequence: List<TestSentEvent>, strictAttrs: Boolean = false, strict: Boolean = false) {
        val errorMessages = TestFSEventLogger.sentEvents().mapIndexed { idx, actualSentEvent ->
            when {
                idx >= expectedSequence.size -> when (strict) {
                    true -> "[$idx]\nExpected to have no further analytics, but there was $actualSentEvent"
                    false -> null
                }
                else -> expectedSequence[idx].let { expectedSentEvent ->
                    when (expectedSentEvent.name) {
                        actualSentEvent.name -> createAttrErrorMessage(
                            expectedAttrs = expectedSequence[idx].attrs,
                            actualAttrs = actualSentEvent.attrs,
                            strict = strictAttrs
                        )?.let { "[$idx]\n$it" }
                        else -> "[$idx]\nExpected event to have event name '${expectedSentEvent.name}', but was $actualSentEvent"
                    }
                }
            }
        }.filterNotNull()
        if (errorMessages.isNotEmpty()) {
            fail(errorMessages.joinToString(separator = "\n"))
        }
    }

    /**
     * Asserts that no analytics were sent matching an event name
     *
     * @param eventName the event name that you are expecting not to be sent.
     */
    @JvmStatic
    fun assertNoAnalyticsSentWithName(eventName: String) {
        assertAnalyticCountSentWithName(eventName, expectedCount = 0)
    }

    /**
     * Asserts that [expectedCount] analytics were sent having event name
     * [eventName].
     *
     * @param eventName the event name that you are expecting not to be sent.
     * @param expectedCount the expected count of events having that name
     */
    @JvmStatic
    fun assertAnalyticCountSentWithName(eventName: String, expectedCount: Int) {
        val actualEventsWithEventName = TestFSEventLogger.sentEvents().filter { it.name == eventName }
        if (actualEventsWithEventName.size != expectedCount) {
            fail(
                "Expected $expectedCount sent events with name '$eventName', but found ${actualEventsWithEventName.size} sent event(s):\n${
                    actualEventsWithEventName.joinToString(
                        separator = "\n"
                    )
                }"
            )
        }
    }

    /**
     * Asserts that the analytic with the [eventName] and [expectedAttributes]
     * was sent.
     *
     * @param eventName The expected event name
     * @param expectedAttributes the expected attributes that must be on the
     * event with name [eventName]
     * @param strict when true, this will fail if there is not an exact match
     * [expectedAttributes] for the [eventName]. Defaults to `false`
     */
    @JvmStatic
    @JvmOverloads
    fun assertAnalyticSent(eventName: String, expectedAttributes: Map<String, String>, strict: Boolean = false) {
        val errorMessages = TestFSEventLogger.sentEvents().mapIndexed { idx, actualSentEvent ->
            when (eventName) {
                actualSentEvent.name -> createAttrErrorMessage(
                    expectedAttrs = expectedAttributes,
                    actualAttrs = actualSentEvent.attrs,
                    strict = strict
                )?.let { "[$idx]\n$it" }
                else -> "[$idx]\nExpected event to have event name '$eventName', but was $actualSentEvent"
            }
        }
        val indexOfFirstMatch = errorMessages.indexOfFirst { it == null }
        if (indexOfFirstMatch == -1) {
            fail("Did not find matching event '$eventName' with attributes: $expectedAttributes\nFound:\n${errorMessages.joinToString(separator = "\n")}")
        }
    }

    private fun createAttrErrorMessage(expectedAttrs: Map<String, String>, actualAttrs: Map<String, String>, strict: Boolean): String? {
        val missingKeys = mutableSetOf<String>()
        val wrongValues = mutableMapOf<String, String>()
        val extraValues = mutableMapOf<String, String>()
        val keysOfCorrectValues = mutableSetOf<String>()

        actualAttrs.forEach { (key, actual) ->
            when (val expected = expectedAttrs[key]) {
                null -> extraValues[key] = actual
                else -> when (actual) {
                    expected -> keysOfCorrectValues.add(key)
                    else -> wrongValues[key] = actual
                }
            }
        }
        expectedAttrs.keys.forEach { expectedKey ->
            if (!actualAttrs.containsKey(expectedKey)) {
                missingKeys.add(expectedKey)
            }
        }

        val isFailure = (strict && extraValues.isNotEmpty())
                || wrongValues.isNotEmpty()
                || missingKeys.isNotEmpty()
        return when (isFailure) {
            true -> createFailureMessageForAttrs(
                expectedAttrs = expectedAttrs,
                missingKeys = missingKeys,
                wrongValues = wrongValues,
                extraValues = extraValues,
                keysOfCorrectValues = keysOfCorrectValues
            )
            false -> null
        }
    }

    private fun createFailureMessageForAttrs(
        expectedAttrs: Map<String, String>,
        missingKeys: Set<String>,
        wrongValues: Map<String, String>,
        extraValues: Map<String, String>,
        keysOfCorrectValues: Set<String>
    ): String? {
        return """
            Expected attrs:         $expectedAttrs
            Missing keys:           $missingKeys
            Wrong values:           $wrongValues
            Extra values:           $extraValues
            Keys of correct values: $keysOfCorrectValues
        """.trimIndent()
    }
}