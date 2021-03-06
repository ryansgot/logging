package com.fsryan.tools.logging

import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FSEventLogTest {

    @BeforeEach
    fun resetMocks() {
        testLogger1().wrappedMock = mockk(relaxed = true)
        testLogger2().wrappedMock = mockk(relaxed = true)
        testLogger3().wrappedMock = mockk(relaxed = true)
    }

    @Test
    @DisplayName("Should log to each logger in the correct order")
    fun logToEachDestination() {
        val attrName = "attrName"
        val attrValue = "attrValue"
        FSEventLog.addAttr(attrName, attrValue)
        verifyOrder {
            testLogger1().wrappedMock.addAttr(attrName, attrValue)
            testLogger2().wrappedMock.addAttr(attrName, attrValue)
            testLogger3().wrappedMock.addAttr(attrName, attrValue)
        }
    }

    @Test
    @DisplayName("Should log to each logger in the order they keys are given")
    fun logToDestinationSubset() {
        val eventName = "attrName"
        val attrs = mapOf("attrName" to "attrValue")
        FSEventLog.addEvent(eventName, attrs, testLogger3().id(), testLogger2().id())
        verifyOrder {
            testLogger3().wrappedMock.addEvent(eventName, attrs)
            testLogger2().wrappedMock.addEvent(eventName, attrs)
        }
        verify(exactly = 0) { testLogger1().wrappedMock.addEvent(any(), any()) }
    }

    @Test
    @DisplayName("Should log to one logger when only one logger's id given")
    fun logToSingleDestination() {
        val attrName = "attrName"
        FSEventLog.incrementCountableAttr(attrName, testLogger1().id())

        verify { testLogger1().wrappedMock.incrementAttrValue(attrName) }
        verify(exactly = 0) { testLogger3().wrappedMock.incrementAttrValue(any()) }
        verify(exactly = 0) { testLogger2().wrappedMock.incrementAttrValue(any()) }
    }

    @Test
    @DisplayName("Should log to two destinations whne two logger ids are given")
    fun logToTwoDestinations() {
        val attrs = mapOf("attrName" to "attrValue")
        FSEventLog.addAttrs(attrs, testLogger1().id(), testLogger3().id())
        verifyOrder {
            testLogger1().wrappedMock.addAttrs( attrs)
            testLogger3().wrappedMock.addAttrs( attrs)
        }
        verify(exactly = 0) { testLogger1().wrappedMock.addEvent(any(), any()) }
    }

    private fun testLogger1() = FSEventLog.loggersOfType(MockKFSEventLogger1::class.java).first()
    private fun testLogger2() = FSEventLog.loggersOfType(MockKFSEventLogger2::class.java).first()
    private fun testLogger3() = FSEventLog.loggersOfType(MockKFSEventLogger3::class.java).first()
}