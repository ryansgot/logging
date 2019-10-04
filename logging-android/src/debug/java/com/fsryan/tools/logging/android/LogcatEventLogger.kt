package com.fsryan.tools.logging.android

import android.util.Log
import com.fsryan.tools.logging.FSEventLogger

class LogcatEventLogger : FSEventLogger {
    override fun id() = "logcat"

    override fun addAttr(attrName: String, attrValue: String) {
        Log.i("FSEventLog", "addAttr('$attrName', '$attrValue')")
    }

    override fun incrementAttrValue(attrName: String) {
        Log.i("FSEventLog", "incrementAttrValue('$attrName')")
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        Log.i("FSEventLog", "addEvent('$eventName', $attrs)")
    }
}