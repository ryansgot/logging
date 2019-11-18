package com.fsryan.tools.logging.android

import com.fsryan.tools.logging.FSDevMetricsLogger
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.analytics.EventProperties

class AppCenterDevMetricsLogger : FSDevMetricsLogger {
    override fun id() = "appcenter"
    override fun watch(msg: String, info: String?, extraInfo: String?) = sendToAppCenter("watch", msg, info, extraInfo)
    override fun info(msg: String, info: String?, extraInfo: String?) = sendToAppCenter("info", msg, info, extraInfo)
    override fun metric(operationName: String, durationNanos: Long) = Analytics.trackEvent("devMetric", EventProperties().apply {
        set("operation", operationName)
        set("duration", durationNanos)
    })
    private fun sendToAppCenter(type: String, msg: String?, info: String?, extra: String?) = Analytics.trackEvent("devLog", mapOf(
        "devLogType" to type,
        "devMessage" to (msg ?: ""),
        "devInfo" to (info ?: ""),
        "devExtraInfo" to (extra ?: "")
    ))
}