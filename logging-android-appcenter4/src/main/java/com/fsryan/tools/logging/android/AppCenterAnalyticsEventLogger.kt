package com.fsryan.tools.logging.android

import android.content.Context
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.analytics.EventProperties
import java.util.concurrent.ConcurrentHashMap

class AppCenterAnalyticsEventLogger : ContextSpecificEventLogger() {

    private val userProperties: MutableMap<String, String> = ConcurrentHashMap()

    override fun id() = "appcenter"

    override fun initialize(context: Context) {
        FSAppCenter.ensureInitialized(context)
        super.initialize(context)
    }

    override fun addAttr(attrName: String, attrValue: String) {
        userProperties[attrName] = attrValue
        super.addAttr(attrName, attrValue)
    }

    override fun removeAttr(attrName: String) {
        userProperties.remove(attrName)
        super.removeAttr(attrName)
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        if (FSAppCenter.analyticsEnabled.get()) {
            Analytics.trackEvent(
                eventName,
                EventProperties().apply {
                    (userProperties + addDefaultAttrsTo(attrs)).forEach { (key, value) ->
                        when {
                            isDoubleProperty(key) -> set(key, value.toDouble())
                            isLongProperty(key) -> set(key, value.toLong())
                            isBooleanProperty(key) -> set(key, value.toBoolean())
                            else -> set(key, value)
                        }
                    }
                }
            )
        }
    }
}