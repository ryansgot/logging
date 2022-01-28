package com.fsryan.tools.logging

import io.mockk.mockk

abstract class MockKFSDevMetricsLogger(var wrappedMock: FSDevMetricsLogger = mockk(relaxed = true)) :
    FSDevMetricsLogger {
    override fun alarm(t: Throwable, attrs: Map<String, String>) = wrappedMock.alarm(t, attrs)
    override fun watch(msg: String, attrs: Map<String, String>) = wrappedMock.watch(msg, attrs)
    override fun info(msg: String, attrs: Map<String, String>) = wrappedMock.info(msg, attrs)
}

class MockKFSDevMetricsLogger1 : MockKFSDevMetricsLogger() {
    override fun id() = "mfsdml1"
}

class MockKFSDevMetricsLogger2 : MockKFSDevMetricsLogger() {
    override fun id() = "mfsdml2"
}

class MockKFSDevMetricsLogger3 : MockKFSDevMetricsLogger() {
    override fun id() = "mfsdml3"
}