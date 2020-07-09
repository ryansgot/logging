package com.fsryan.tools.logging.android

import android.content.Context
import android.content.res.Resources
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class DataDogEventLogger: ContextSpecificEventLogger {

    private val countableAttrs: MutableMap<String, Long> = ConcurrentHashMap()
    private val doubleProperties: MutableSet<String> = CopyOnWriteArraySet()
    private val longProperties: MutableSet<String> = CopyOnWriteArraySet()
    private val booleanProperties: MutableSet<String> = CopyOnWriteArraySet()

    override fun initialize(context: Context) {
        FSDataDog.ensureInitialized(context)
        storePropertyNamesInto(context, "fs_logging_double_properties", doubleProperties)
        storePropertyNamesInto(context, "fs_logging_long_properties", longProperties)
        storePropertyNamesInto(context, "fs_logging_boolean_properties", booleanProperties)
    }

    override fun id() = "datadog"

    override fun addAttr(attrName: String, attrValue: String) {
        try {
            countableAttrs[attrName] = attrValue.toLong()
        } catch (nfe: NumberFormatException) {}

        if (doubleProperties.contains(attrName)) {
            FSDataDog.logger().addAttribute(attrName, attrValue.toDouble())
            return
        }
        if (longProperties.contains(attrName)) {
            FSDataDog.logger().addAttribute(attrName, attrValue.toLong())
            return
        }
        if (booleanProperties.contains(attrName)) {
            FSDataDog.logger().addAttribute(attrName, attrValue.toBoolean())
            return
        }
        FSDataDog.logger().addAttribute(attrName, attrValue)
    }

    override fun removeAttr(attrName: String) = FSDataDog.logger().removeAttribute(attrName)

    override fun incrementAttrValue(attrName: String) {
        val current = countableAttrs[attrName] ?: 0L
        addAttr(attrName, (current + 1).toString())
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) = FSDataDog.logger().i(
        message = eventName,
        attributes = attrs.mapValues<String, String, Any> { entry ->
            when {
                doubleProperties.contains(entry.key) -> entry.value.toDouble()
                longProperties.contains(entry.key) -> entry.value.toLong()
                booleanProperties.contains(entry.key) -> entry.value.toBoolean()
                else -> entry.value
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