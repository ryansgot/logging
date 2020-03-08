package com.fsryan.tools.logging.android

import android.content.Context
import android.content.res.Resources
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.analytics.EventProperties
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class AppCenterAnalyticsEventLogger : ContextSpecificEventLogger {

    private val countableAttrs: MutableMap<String, Long> = ConcurrentHashMap()
    private val userProperties: MutableMap<String, String> = ConcurrentHashMap()
    private val doubleProperties: MutableSet<String> = CopyOnWriteArraySet()
    private val longProperties: MutableSet<String> = CopyOnWriteArraySet()
    private val booleanProperties: MutableSet<String> = CopyOnWriteArraySet()

    override fun id() = "appcenter"

    override fun initialize(context: Context) {
        storePropertyNamesInto(context, "fs_appcenter_double_properties", doubleProperties)
        storePropertyNamesInto(context, "fs_appcenter_long_properties", longProperties)
        storePropertyNamesInto(context, "fs_appcenter_boolean_properties", booleanProperties)
    }

    override fun addAttr(attrName: String, attrValue: String) {
        userProperties[attrName] = attrValue
        try {
            countableAttrs[attrName] = attrValue.toLong()
        } catch (nfe: NumberFormatException) {}
    }

    override fun removeAttr(attrName: String) {
        userProperties.remove(attrName)
    }

    override fun incrementAttrValue(attrName: String) = addAttr(attrName, ((countableAttrs[attrName] ?: 0L) + 1).toString())

    override fun addEvent(eventName: String, attrs: Map<String, String>) = Analytics.trackEvent(
        eventName,
        EventProperties().apply {
            (userProperties + attrs).forEach { (key, value) ->
                when {
                    doubleProperties.contains(key) -> set(key, value.toDouble())
                    longProperties.contains(key) -> set(key, value.toLong())
                    booleanProperties.contains(key) -> set(key, value.toBoolean())
                    else -> set(key, value)
                }
            }
        }
    )

    private fun storePropertyNamesInto(context: Context, stringArrayName: String, dest: MutableSet<String>) {
        val arrayRes = context.resources.getIdentifier(stringArrayName, "array", context.packageName)
        if (arrayRes == 0) {
            throw IllegalStateException("must have string-array resource $stringArrayName")
        }
        try {
            dest.addAll(context.resources.getStringArray(arrayRes))
        } catch (rnfe : Resources.NotFoundException) {
            throw IllegalArgumentException("Error finding string-array resource: '$stringArrayName' ($arrayRes)")
        }
    }
}