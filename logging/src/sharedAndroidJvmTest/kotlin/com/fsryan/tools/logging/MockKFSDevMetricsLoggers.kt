package com.fsryan.tools.logging

import io.mockk.mockk

abstract class MockKFSDevMetricsLogger(var wrappedMock: FSDevMetricsLogger = mockk(relaxed = true)) :
    FSDevMetricsLogger {
    override fun alarm(t: Throwable, attrs: Map<String, String>) = wrappedMock.alarm(t, attrs)
    override fun watch(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) = wrappedMock.watch(msg, info, extraInfo, attrs)
    override fun info(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) = wrappedMock.info(msg, info, extraInfo, attrs)
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