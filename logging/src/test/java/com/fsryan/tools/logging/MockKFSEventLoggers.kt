package com.fsryan.tools.logging

import io.mockk.mockk

abstract class MockKFSEventLogger(var wrappedMock: FSEventLogger = mockk(relaxed = true)) : FSEventLogger {
    override fun addAttr(attrName: String, attrValue: String) = wrappedMock.addAttr(attrName, attrValue)
    override fun incrementAttrValue(attrName: String) = wrappedMock.incrementAttrValue(attrName)
    override fun addEvent(eventName: String, attrs: Map<String, String>) = wrappedMock.addEvent(eventName, attrs)
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