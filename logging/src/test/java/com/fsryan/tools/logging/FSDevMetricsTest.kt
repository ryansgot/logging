package com.fsryan.tools.logging

import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.Exception

class FSDevMetricsTest {

    @BeforeEach
    fun resetMocks() {
        testLogger1().wrappedMock = mockk(relaxed = true)
        testLogger2().wrappedMock = mockk(relaxed = true)
        testLogger3().wrappedMock = mockk(relaxed = true)
    }

    @Test
    @DisplayName("Should log to each logger in the correct order")
    fun logToEachDestination() {
        val msg = "msg"
        val info = "info"
        val extraInfo = "extraInfo"
        val attrs = mapOf("attr1" to "attr1val", "attr2" to "attr2val")
        FSDevMetrics.watch(msg, info, extraInfo, attrs)
        verifyOrder {
            testLogger1().wrappedMock.watch(msg, info, extraInfo, attrs)
            testLogger2().wrappedMock.watch(msg, info, extraInfo, attrs)
            testLogger3().wrappedMock.watch(msg, info, extraInfo, attrs)
        }
    }

    @Test
    @DisplayName("Should log to each logger in the order they keys are given")
    fun logToDestinationSubset() {
        val msg = "msg"
        val info = "info"
        val extraInfo = "extraInfo"
        val attrs = mapOf("attr1" to "attr1val", "attr2" to "attr2val")
        FSDevMetrics.info(msg, info, extraInfo, attrs, testLogger3().id(), testLogger2().id())
        verifyOrder {
            testLogger3().wrappedMock.info(msg, info, extraInfo, attrs)
            testLogger2().wrappedMock.info(msg, info, extraInfo, attrs)
        }
        verify(exactly = 0) { testLogger1().wrappedMock.info(any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("Should log to one logger when only one logger's id given")
    fun logToSingleDestination() {
        val exception = Exception()
        val attrs = mapOf("attr1" to "attr1val", "attr2" to "attr2val")
        FSDevMetrics.alarm(exception, attrs, testLogger1().id())

        verify { testLogger1().wrappedMock.alarm(exception, attrs) }
        verify(exactly = 0) { testLogger3().wrappedMock.alarm(any(), any()) }
        verify(exactly = 0) { testLogger2().wrappedMock.alarm(any(), any()) }
    }
    
    private fun testLogger1() = FSDevMetrics.loggersOfType(MockKFSDevMetricsLogger1::class.java).first()
    private fun testLogger2() = FSDevMetrics.loggersOfType(MockKFSDevMetricsLogger2::class.java).first()
    private fun testLogger3() = FSDevMetrics.loggersOfType(MockKFSDevMetricsLogger3::class.java).first()
}