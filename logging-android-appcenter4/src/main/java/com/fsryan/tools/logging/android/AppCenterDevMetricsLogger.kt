package com.fsryan.tools.logging.android

import android.content.Context
import com.fsryan.tools.logging.FSDevMetricsLogger
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.analytics.EventProperties

class AppCenterDevMetricsLogger : ContextSpecificDevMetricsLogger {

    override fun initialize(context: Context) = FSAppCenter.ensureInitialized(context)

    override fun id() = "appcenter"

    override fun watch(msg: String, attrs: Map<String, String>) {
        if (FSAppCenter.analyticsEnabled.get()) {
            sendToAppCenter("watch", msg, attrs)
        }
    }

    override fun info(msg: String, attrs: Map<String, String>) {
        if (FSAppCenter.analyticsEnabled.get()) {
            sendToAppCenter("info", msg, attrs)
        }
    }

    override fun metric(operationName: String, durationNanos: Long) {
        if (FSAppCenter.analyticsEnabled.get()) {
            Analytics.trackEvent("devMetric", EventProperties().apply {
                set("operation", operationName)
                set("duration", durationNanos)
            })
        }
    }

    private fun sendToAppCenter(
        type: String,
        msg: String?,
        attrs: Map<String, String>
    ) {
        Analytics.trackEvent("devLog", attrs + mapOf(
            "devLogType" to type,
            "devMessage" to (msg ?: "")
        ))
    }
}