package com.fsryan.tools.logging.android

import android.content.Context
import android.content.res.Resources
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class DataDogEventLogger: ContextSpecificEventLogger() {

    override fun initialize(context: Context) {
        FSDataDog.ensureInitialized(context)
        super.initialize(context)
    }

    override fun id() = "datadog"

    override fun addAttr(attrName: String, attrValue: String) {
        super.addAttr(attrName, attrValue)
        when {
            isDoubleProperty(attrName) -> FSDataDog.logger().addAttribute(attrName, attrValue.toDouble())
            isLongProperty(attrName) -> FSDataDog.logger().addAttribute(attrName, attrValue.toLong())
            isBooleanProperty(attrName) -> FSDataDog.logger().addAttribute(attrName, attrValue.toBoolean())
            else -> FSDataDog.logger().addAttribute(attrName, attrValue)
        }
    }

    override fun removeAttr(attrName: String) = FSDataDog.logger().removeAttribute(attrName)

    override fun addEvent(eventName: String, attrs: Map<String, String>) = FSDataDog.logger().i(
        message = eventName,
        attributes = addDefaultAttrsTo(attrs).mapValues<String, String, Any> { entry ->
            when {
                isDoubleProperty(entry.key) -> entry.value.toDouble()
                isLongProperty(entry.key) -> entry.value.toLong()
                isBooleanProperty(entry.key) -> entry.value.toBoolean()
                else -> entry.value
            }
        }
    )
}