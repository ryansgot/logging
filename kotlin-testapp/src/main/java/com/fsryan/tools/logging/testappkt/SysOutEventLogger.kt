package com.fsryan.tools.logging.testappkt

import com.fsryan.tools.logging.FSEventLogger

class SysOutEventLogger : FSEventLogger {
    override fun id(): String = "sysout"
    override fun addAttr(attrName: String, attrValue: String) {
        println("add attr $attrName=$attrValue")
    }

    override fun incrementAttrValue(attrName: String) {
        println("incrementCountableAttr $attrName")
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        println("addEvent $eventName; attrs = $attrs")
    }
}