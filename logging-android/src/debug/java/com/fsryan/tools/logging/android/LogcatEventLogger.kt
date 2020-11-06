package com.fsryan.tools.logging.android

import android.util.Log
import com.fsryan.tools.logging.FSEventLogger

/**
 * Importantly, this does not hold on to any attrs--it just logs the fact that
 * the attr was added/removed. So you'll have to look at your log in order to
 * tell what attrs would have been logged.
 */
class LogcatEventLogger : FSEventLogger {
    override fun id() = "logcat"

    override fun addAttr(attrName: String, attrValue: String) {
        Log.i("FSEventLog", "addAttr('$attrName', '$attrValue')")
    }

    override fun removeAttr(attrName: String) {
        Log.i("FSEventLog", "removeAttr('$attrName')")
    }

    override fun incrementAttrValue(attrName: String) {
        Log.i("FSEventLog", "incrementCountableAttr('$attrName')")
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        Log.i("FSEventLog", "addEvent('$eventName', $attrs)")
    }

    override fun sendTimedOperation(
        operationName: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        startAttrs: Map<String, String>,
        endAttrs: Map<String, String>
    ) {
        Log.i("FSEventLog", "sendTimedOperation($operationName, startMillis = $startTimeMillis, endMillis = $endTimeMillis, startAttrs = $startAttrs, endAttrs = $endAttrs)")
    }
}