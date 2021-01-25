package com.fsryan.tools.logging.android.urbanairship

import android.content.Context
import android.content.pm.PackageManager
import com.fsryan.tools.logging.android.ContextSpecificEventLogger
import com.urbanairship.UAirship
import com.urbanairship.analytics.CustomEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class UrbanAirshipEventLogger: ContextSpecificEventLogger() {

    private val storedAttrs = ConcurrentHashMap<String, String>()
    private val identifierAttrs = CopyOnWriteArraySet<String>()

    override fun id(): String = "urbanairship"

    override fun initialize(context: Context) {
        ensureInitialized(context)
        val appInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val identifierAttrArrayRes = appInfo.metaData.getInt("fsryan.log.ua.identifier_attrs", 0)
        if (identifierAttrArrayRes != 0) {
            identifierAttrs.addAll(context.resources.getStringArray(identifierAttrArrayRes))
        }

        super.initialize(context)
    }

    override fun addAttr(attrName: String, attrValue: String) {
        when {
            attrName == trackScreenAttrName -> trackScreen(attrValue)
            identifierAttrs.contains(attrName) -> addIdentifier(attrName, attrValue)
            else -> super.addAttr(attrName, attrValue).also { storedAttrs[attrName] = attrValue }
        }
    }

    override fun removeAttr(attrName: String) {
        when {
            identifierAttrs.contains(attrName) -> removeIdentifier(attrName)
            else -> storedAttrs.remove(attrName)
        }
        super.removeAttr(attrName)
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        val actualAttrs = addDefaultAttrsTo(attrs) + storedAttrs
        val builder = CustomEvent.newBuilder(eventName)
        actualAttrs.entries.forEach { entry ->
            with(entry) {
                when {
                    isBooleanProperty(key) -> builder.addProperty(key, value.toBoolean())
                    isLongProperty(key) -> builder.addProperty(key, value.toLong())
                    isDoubleProperty(key) -> builder.addProperty(key, value.toDouble())
                    else -> builder.addProperty(key, value)
                }
            }
        }
        builder.build().track()
    }

    private fun trackScreen(screenName: String) {
        UAirship.shared().analytics.trackScreen(screenName)
    }

    private fun addIdentifier(key: String, value: String) {
        UAirship.shared().analytics.editAssociatedIdentifiers().addIdentifier(key, value)
    }

    private fun removeIdentifier(key: String) {
        UAirship.shared().analytics.editAssociatedIdentifiers().removeIdentifier(key)
    }
}