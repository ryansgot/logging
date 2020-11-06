package com.fsryan.tools.logging.android.urbanairship

import com.fsryan.tools.logging.FSDevMetricsLogger

import com.urbanairship.analytics.CustomEvent

class UrbanAirshipDevMetricsLogger: FSDevMetricsLogger {

    override fun id(): String = "urbanairship"

    override fun watch(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) = logMessage(
        type = "watch",
        msg = msg,
        info = info,
        extraInfo = extraInfo,
        attrs = attrs
    )

    override fun info(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) = logMessage(
        type = "info",
        msg = msg,
        info = info,
        extraInfo = extraInfo,
        attrs = attrs
    )

    override fun metric(operationName: String, durationNanos: Long) {
        CustomEvent.newBuilder("devMetric")
            .addProperty("operation", operationName)
            .addProperty("duration", durationNanos)
            .build()
            .track()
    }

    private fun logMessage(type: String, msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        val builder = CustomEvent.newBuilder("devLog")
            .addProperty("devLogType", type)
            .addProperty("devMessage", msg)
        collapseAttrs(msg, info, extraInfo, attrs).entries.forEach { entry ->
            builder.addProperty(entry.key, entry.value)
        }
        builder.build().track()
    }

    private fun collapseAttrs(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>): Map<String, String> {
        val append = mutableMapOf<String, String>()
        append["msg"] = msg
        info?.let { append["info"] = it }
        extraInfo?.let { append["extraInfo"] = it }
        return attrs + append
    }
}