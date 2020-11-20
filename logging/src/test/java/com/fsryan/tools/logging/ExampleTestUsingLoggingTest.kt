package com.fsryan.tools.logging

import com.fsryan.tools.logging.test.FSLoggingAssertions
import com.fsryan.tools.logging.test.junit5.FSLoggingTestExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(FSLoggingTestExtension::class)
class ExampleTestUsingLoggingTest {

    @ParameterizedTest(name = "[{index}] input: {0} -> expected stored attrs: {1}")
    @MethodSource("addAttrsInput")
    fun addAttrs(attrsToAdd: List<Pair<String, String>>, expectedAttrs: Map<String, String>) {
        attrsToAdd.forEach { FSEventLog.addAttr(it.first, it.second) }
        FSLoggingAssertions.assertAllStoredAttrs(expectedAttrs)
    }

    @Test
    fun exampleFailingTestOfAllAttrs() {
        try {
            FSEventLog.addAttr("attr1", "value1")
            FSEventLog.addAttr("attr2", "value2")
            // should not pass because must throw assertion error when not all attrs are present
            FSLoggingAssertions.assertAllStoredAttrs(mapOf("attr1" to "value1", "attr2" to "value2", "attr3" to "value3"))
        } catch (assertionError: AssertionError) {
            return  //expected
        }
        fail("should have thrown assertion error")
    }

    @Test
    fun exampleFailingTestOfMatchingAttr() {
        try {
            FSEventLog.addAttr("attr1", "value1")
            FSEventLog.addAttr("attr2", "value2")
            // should not pass because must throw assertion error when not all attrs are present
            FSLoggingAssertions.assertAttrStored("attr3", "value3")
        } catch (assertionError: AssertionError) {
            return  // expected
        }
        fail("should have thrown assertion error")
    }

    @ParameterizedTest(name = "[{index}] inputEvent: {0}; inputEventAttrs: {1} -> expected added events: {1}")
    @MethodSource("sendEventsInput")
    fun sendEvents(
        eventName: String,
        eventAttrs: List<Map<String, String>>
    ) {
        eventAttrs.forEach { attrs -> FSEventLog.addEvent(eventName, attrs) }
        FSLoggingAssertions.assertAnalyticSequence(
            eventName = eventName,
            expected = eventAttrs
        )
    }

    @Test
    fun exampleFailingTestOfSendingEventMissingAttr() {
        try {
            FSEventLog.addEvent("event")
            // should not pass because must throw assertion error when not all attrs are present
            FSLoggingAssertions.assertAnalyticSent("event", expectedAttributes = mapOf("attr1" to "value1"))
        } catch (assertionError: AssertionError) {
            return  // expected
        }
        fail("should have thrown assertion error")
    }

    @Test
    fun exampleFailingTestOfSendingEventWrongAttrValue() {
        try {
            FSEventLog.addEvent("event", mapOf("attr1" to "value1"))
            // should not pass because must throw assertion error when not all attrs are present
            FSLoggingAssertions.assertAnalyticSent("event", expectedAttributes = mapOf("attr1" to "value2"))
        } catch (assertionError: AssertionError) {
            return  // expected
        }
        fail("should have thrown assertion error")
    }

    @Test
    fun exampleFailingTestOfSendingEventWrongSequence() {
        try {
            FSEventLog.addEvent("event", mapOf("attr1" to "value1"))
            FSEventLog.addEvent("event", mapOf("attr2" to "value2"))
            // should not pass because must throw assertion error when not all attrs are present
            FSLoggingAssertions.assertAnalyticSequence(
                eventName = "event",
                expected = listOf(mapOf("attr2" to "value2"), mapOf("attr1" to "value1"))
            )
        } catch (assertionError: AssertionError) {
            return  // expected
        }
        fail("should have thrown assertion error")
    }

    companion object {
        @JvmStatic
        fun addAttrsInput() = listOf(
            arguments(
                listOf("attr1" to "value1", "attr1" to "value2"),
                mapOf("attr1" to "value2")
            ),
            arguments(
                listOf("attr1" to "value1", "attr2" to "value2"),
                mapOf("attr1" to "value1", "attr2" to "value2")
            )
        )

        @JvmStatic
        fun sendEventsInput() = listOf(
            arguments(
                "event",
                listOf(mapOf("event1attr1" to "event1value1"))
            ),
            arguments(
                "event",
                listOf(
                    mapOf("event1attr1" to "event1value1", "event1attr2" to "event1value2"),
                    mapOf("event2attr1" to "event2value2")
                )
            )
        )
    }
}