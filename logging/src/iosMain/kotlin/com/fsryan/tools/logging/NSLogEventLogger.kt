package com.fsryan.tools.logging

import platform.Foundation.NSLog

class NSLogEventLogger: FSEventLogger {
    override fun addAttr(attrName: String, attrValue: String) {
        NSLog("[nslogeventlog] addAttr(attrName = '$attrName'; attrValue = '$attrValue')")
    }

    override fun removeAttr(attrName: String) {
        NSLog("[nslogeventlog] removeAttr(attrName = '$attrName')")
    }

    override fun incrementAttrValue(attrName: String) {
        NSLog("[nslogeventlog] incrementAttrValue(attrName = '$attrName')")
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        NSLog("[nslogeventlog] addEvent(eventName = '$eventName'; attrs = $attrs)")
    }

    override fun id(): String = "nslogeventlog"
}