package com.fsryan.tools.logging

import io.mockk.mockk

abstract class MockKFSEventLogger(var wrappedMock: FSEventLogger = mockk(relaxed = true)) : FSEventLogger {
    override fun runInTestEnvironment(): Boolean = true
    override fun addAttr(attrName: String, attrValue: String) = wrappedMock.addAttr(attrName, attrValue)
    override fun removeAttr(attrName: String) = wrappedMock.removeAttr(attrName)
    override fun incrementAttrValue(attrName: String) = wrappedMock.incrementAttrValue(attrName)
    override fun addEvent(eventName: String, attrs: Map<String, String>) = wrappedMock.addEvent(eventName, attrs)
    override fun sendTimedOperation(
        operationName: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        durationAttrName: String?,
        startTimeMillisAttrName: String?,
        endTimeMillisAttrName: String?,
        startAttrs: Map<String, String>,
        endAttrs: Map<String, String>
    ) {
        wrappedMock.sendTimedOperation(
            operationName,
            startTimeMillis,
            endTimeMillis,
            durationAttrName,
            startTimeMillisAttrName,
            endTimeMillisAttrName,
            startAttrs,
            endAttrs
        )
    }
}

class MockKFSEventLogger1 : MockKFSEventLogger() {
    override fun id(): String = "mfsel1"
}

class MockKFSEventLogger2 : MockKFSEventLogger() {
    override fun id(): String = "mfsel2"
}

class MockKFSEventLogger3 : MockKFSEventLogger() {
    override fun id(): String = "mfsel3"
}