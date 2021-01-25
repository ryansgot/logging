package com.fsryan.tools.logging.android

import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Importantly, this does not hold on to any attrs--it just logs the fact that
 * the attr was added/removed. So you'll have to look at your log in order to
 * tell what attrs would have been logged.
 */
class LogcatEventLogger : ContextSpecificEventLogger() {

    private val userProperties: MutableMap<String, String> = ConcurrentHashMap()

    override fun id() = "logcat"

    override fun addAttr(attrName: String, attrValue: String) {
        userProperties[attrName] = attrValue
        super.addAttr(attrName, attrValue)
        Log.i("FSEventLog", "addAttr('$attrName', '$attrValue')")
    }

    override fun removeAttr(attrName: String) {
        userProperties.remove(attrName)
        super.removeAttr(attrName)
        Log.i("FSEventLog", "removeAttr('$attrName')")
    }

    override fun incrementAttrValue(attrName: String) {
        super.incrementAttrValue(attrName)
        Log.i("FSEventLog", "incrementCountableAttr('$attrName')")
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        val actualAttrs = userProperties + addDefaultAttrsTo(attrs)
        Log.i("FSEventLog", "addEvent('$eventName', $actualAttrs)")
    }
}