package com.fsryan.tools.logging.testappkt

import com.fsryan.tools.logging.FSEventLogger

class SysOutEventLogger : FSEventLogger {
    override fun id(): String = "sysout"
    override fun addAttr(attrName: String, attrValue: String) = println("add attr $attrName=$attrValue")
    override fun removeAttr(attrName: String) = println("remove attr $attrName")
    override fun incrementAttrValue(attrName: String) = println("incrementCountableAttr $attrName")
    override fun addEvent(eventName: String, attrs: Map<String, String>) = println("addEvent $eventName; attrs = $attrs")
    override fun sendTimedOperation(
        operationName: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        startAttrs: Map<String, String>,
        endAttrs: Map<String, String>
    ) {
        println("timed operation $operationName; startMillis = $startTimeMillis; endMillis = $endTimeMillis; startAttrs = $startAttrs; endAttrs = $endAttrs")
    }
}