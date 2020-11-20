package com.fsryan.tools.logging.test.junit5

import com.fsryan.tools.logging.test.FSLoggingAssertions
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class FSLoggingTestExtension: BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        FSLoggingAssertions.ensureEnvironment()
        FSLoggingAssertions.resetTestFSEventLogger()
    }

    override fun afterEach(context: ExtensionContext?) {
        FSLoggingAssertions.resetTestFSEventLogger()
    }
}