package com.fsryan.tools.logging.android

import com.fsryan.tools.logging.FSEventLogger
import com.microsoft.appcenter.analytics.Analytics
import java.util.concurrent.ConcurrentHashMap

class AppCenterAnalyticsEventLogger : FSEventLogger {

    private val countableAttrs: MutableMap<String, Long> = ConcurrentHashMap()
    private val userProperties: MutableMap<String, String> = ConcurrentHashMap()

    override fun id() = "appcenter"

    override fun addAttr(attrName: String, attrValue: String) {
        userProperties[attrName] = attrValue
        try {
            countableAttrs[attrName] = attrValue.toLong()
        } catch (nfe: NumberFormatException) {}
    }

    override fun incrementAttrValue(attrName: String) {
        val current = countableAttrs[attrName] ?: 0L
        addAttr(attrName, (current + 1).toString())
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        Analytics.trackEvent(eventName, userProperties + attrs)
    }
}