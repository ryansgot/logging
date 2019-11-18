package com.fsryan.tools.logging.testappkt

import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog

fun main(args: Array<String>) {
    FSDevMetrics.startTimedOperation(operationName = "full_run", operationId = 1)
    FSDevMetrics.startTimedOperation(operationName = "full_run", operationId = 2)   // <-- to cancel
    FSDevMetrics.alarm(Exception("Some exception"))
    FSDevMetrics.watch("watch msg", "watch info", "watch extra info")
    FSDevMetrics.info("info msg", "info info", "info extra info")
    FSDevMetrics.signalShutdown()
    FSEventLog.addAttr("attr name", "attr val")
    FSEventLog.addEvent("event name", mapOf("key1" to "val1", "key2" to "val2"))
    FSEventLog.incrementCountableAttr("attr to increment")
    FSDevMetrics.cancelTimedOperation(operationName = "full_run", operationId = 2)   // <-- to cancel
    FSDevMetrics.commitTimedOperation(operationName = "full_run", operationId = 1)
    FSEventLog.signalShutdown()
}