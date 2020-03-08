package com.fsryan.tools.logging.android

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class FirebaseAnalyticsEventLogger : ContextSpecificEventLogger {

    @Volatile private lateinit var fbAnalytics: FirebaseAnalytics
    private val countableAttrs: MutableMap<String, Long> = ConcurrentHashMap()
    private val doubleProperties: MutableSet<String> = CopyOnWriteArraySet()
    private val longProperties: MutableSet<String> = CopyOnWriteArraySet()
    private val booleanProperties: MutableSet<String> = CopyOnWriteArraySet()


    override fun initialize(context: Context) {
        fbAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
        storePropertyNamesInto(context, "fs_logging_double_properties", doubleProperties)
        storePropertyNamesInto(context, "fs_logging_long_properties", longProperties)
        storePropertyNamesInto(context, "fs_logging_boolean_properties", booleanProperties)
    }

    override fun id() = "firebase"

    override fun addAttr(attrName: String, attrValue: String) {
        fbAnalytics.setUserProperty(attrName, attrValue)
        try {
            countableAttrs[attrName] = attrValue.toLong()
        } catch (nfe: NumberFormatException) {}
    }

    override fun removeAttr(attrName: String) {
        fbAnalytics.setUserProperty(attrName, null)
    }

    override fun incrementAttrValue(attrName: String) {
        val current = countableAttrs[attrName] ?: 0L
        addAttr(attrName, (current + 1).toString())
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        fbAnalytics.logEvent(eventName, Bundle().apply {
            attrs.entries.forEach {
                when {
                    doubleProperties.contains(it.key) -> putDouble(it.key, it.value.toDouble())
                    longProperties.contains(it.key) -> putLong(it.key, it.value.toLong())
                    booleanProperties.contains(it.key) -> putBoolean(it.key, it.value.toBoolean())
                    else -> putString(it.key, it.value)
                }
            }
        })
    }

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