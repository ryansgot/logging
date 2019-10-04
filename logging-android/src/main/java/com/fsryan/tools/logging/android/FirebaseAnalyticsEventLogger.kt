package com.fsryan.tools.logging.android

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.concurrent.ConcurrentHashMap

class FirebaseAnalyticsEventLogger : ContextSpecificEventLogger {

    @Volatile private lateinit var fbAnalytics: FirebaseAnalytics
    private val countableAttrs: MutableMap<String, Long> = ConcurrentHashMap()

    override fun initialize(context: Context) {
        fbAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    override fun id() = "gtm"

    override fun addAttr(attrName: String, attrValue: String) {
        fbAnalytics.setUserProperty(attrName, attrValue)
        try {
            countableAttrs[attrName] = attrValue.toLong()
        } catch (nfe: NumberFormatException) {}
    }

    override fun incrementAttrValue(attrName: String) {
        val current = countableAttrs[attrName] ?: 0L
        addAttr(attrName, (current + 1).toString())
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        fbAnalytics.logEvent(eventName, Bundle().apply {
            attrs.entries.forEach { putString(it.key, it.value) }
        })
    }
}