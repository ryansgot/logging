package com.fsryan.tools.logging.android

import android.content.Context
import android.content.res.Resources
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import com.fsryan.tools.logging.FSEventLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

internal val sessionStartTimeMillis = System.currentTimeMillis()

/**
 * An interface to enable Android application-specific initialization of
 * an event logger.
 */
abstract class ContextSpecificEventLogger : FSEventLogger {

    protected val countableAttrs: MutableMap<String, Long> = ConcurrentHashMap()
    protected val doubleProperties: MutableSet<String> = CopyOnWriteArraySet()
    protected val longProperties: MutableSet<String> = CopyOnWriteArraySet()
    protected val booleanProperties: MutableSet<String> = CopyOnWriteArraySet()

    @Volatile protected lateinit var appUptimeAttrName: String
    @Volatile protected lateinit var timestampAttrName: String

    /**
     * Implementations should assume they will be called at or close to
     * `onCreate()` of the `Application`. In this function, implementations
     * should do any initialization. Also, you can look for the following
     * `string-array` values in order to know which attributes correspond to
     * `Long`, `Boolean`, or `Double` types:
     * - `fs_logging_long_properties`
     * - `fs_logging_boolean_properties`
     * - `fs_logging_double_properties`
     *
     * These arrays should tell you the types of some properties so that you
     * can log them as specific types.
     */
    @CallSuper
    @MainThread
    open fun initialize(context: Context) {
        storePropertyNamesInto(context, "fs_logging_double_properties", doubleProperties)
        storePropertyNamesInto(context, "fs_logging_long_properties", longProperties)
        storePropertyNamesInto(context, "fs_logging_boolean_properties", booleanProperties)
        appUptimeAttrName = context.getString(R.string.fs_logging_app_uptime_attr_name)
        timestampAttrName = context.getString(R.string.fs_logging_timestamp_attr_name)
        longProperties.add(appUptimeAttrName)
        longProperties.add(timestampAttrName)
    }

    @AnyThread
    override fun addAttr(attrName: String, attrValue: String) {
        try {
            countableAttrs[attrName] = attrValue.toLong()
        } catch (nfe: NumberFormatException) {}
    }

    @AnyThread
    override fun removeAttr(attrName: String) {
        countableAttrs.remove(attrName)
    }

    @AnyThread
    override fun incrementAttrValue(attrName: String) {
        val current = countableAttrs[attrName] ?: 0L
        addAttr(attrName, (current + 1).toString())
    }

    protected fun isDoubleProperty(attrName: String) = doubleProperties.contains(attrName)
    protected fun isLongProperty(attrName: String) = longProperties.contains(attrName)
    protected fun isBooleanProperty(attrName: String) = booleanProperties.contains(attrName)
    protected fun addDefaultAttrsTo(attrs: Map<String, String>): Map<String, String> {
        val currentTimeMillis = System.currentTimeMillis()
        val defaultAttrs = mapOf(
            appUptimeAttrName to (currentTimeMillis - sessionStartTimeMillis).toString(),
            timestampAttrName to currentTimeMillis.toString()
        )
        return attrs + defaultAttrs
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