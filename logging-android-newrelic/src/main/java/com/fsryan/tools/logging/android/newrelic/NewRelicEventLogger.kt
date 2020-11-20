package com.fsryan.tools.logging.android.newrelic

import android.content.Context
import android.content.pm.PackageManager
import com.fsryan.tools.logging.android.ContextSpecificEventLogger
import com.newrelic.agent.android.NewRelic
import java.util.concurrent.atomic.AtomicReference

class NewRelicEventLogger: ContextSpecificEventLogger() {

    private val eventType = AtomicReference<String>()

    override fun id(): String = "newrelic"

    override fun initialize(context: Context) {
        context.startNewRelicIfNecessary()
        val appInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        eventType.set(appInfo.metaData.getString("fsryan.nr_event_type") ?: "event")
        super.initialize(context)
    }

    override fun addAttr(attrName: String, attrValue: String) {
        // New Relic does not like empty string values
        if (attrName.isEmpty()) {
            return
        }
        // New Relic does not like empty string values
        if (attrValue.isEmpty()) {
            removeAttr(attrName)
            return
        }
        when {
            isBooleanProperty(attrName) -> NewRelic.setAttribute(attrName, attrValue.toBoolean())
            isLongProperty(attrName) || isDoubleProperty(attrName) -> NewRelic.setAttribute(attrName, attrValue.toDouble())
            else -> NewRelic.setAttribute(attrName, attrValue)
        }
    }

    override fun removeAttr(attrName: String) {
        NewRelic.removeAttribute(attrName)
    }

    override fun incrementAttrValue(attrName: String) {
        NewRelic.incrementAttribute(attrName)
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        val mutableAttrs = attrs.filterValues { it.isNotEmpty() }.toMutableMap()
        val eventTypeOverride = mutableAttrs.remove(ATTR_EVENT_TYPE_OVERRIDE)
        val actualAttrs = mutableAttrs.mapValues { entry ->
            when {
                isBooleanProperty(entry.key) -> entry.value.toBoolean()
                isLongProperty(entry.key) || isDoubleProperty(entry.key) -> entry.value.toDouble()
                else -> entry.value
            }
        }
        NewRelic.recordCustomEvent(eventTypeOverride ?: eventType.get(), eventName, actualAttrs)
    }
}