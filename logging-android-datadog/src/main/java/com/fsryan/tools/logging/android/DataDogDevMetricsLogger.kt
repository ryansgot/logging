package com.fsryan.tools.logging.android

import android.content.Context

class DataDogDevMetricsLogger : ContextSpecificDevMetricsLogger {

    override fun id() = "datadog"

    override fun initialize(context: Context) = FSDataDog.ensureInitialized(context)

    override fun watch(
        msg: String,
        info: String?,
        extraInfo: String?,
        attrs: Map<String, String>
    ) = FSDataDog.logger().w(
        message = msg,
        attributes = attrs + infoExtraInfoMap(info, extraInfo)
    )

    override fun info(
        msg: String,
        info: String?,
        extraInfo: String?,
        attrs: Map<String, String>
    ) = FSDataDog.logger().i(
        message = msg,
        attributes = attrs + infoExtraInfoMap(info, extraInfo)
    )

    override fun metric(operationName: String, durationNanos: Long) = FSDataDog.logger().i(
        message = "devMetric",
        attributes = mapOf(
            "operation" to operationName,
            "duration" to durationNanos
        )
    )

    override fun alarm(t: Throwable, attrs: Map<String, String>) = FSDataDog.logger().wtf(
        message = "alarm",
        throwable = t,
        attributes = attrs
    )

    private fun infoExtraInfoMap(info: String?, extraInfo: String?) = when (info) {
        null -> when (extraInfo) {
            null -> emptyMap()
            else -> mapOf("extraInfo" to extraInfo)
        }
        else -> when (extraInfo) {
            null -> mapOf("info" to info)
            else -> mapOf("info" to info, "extraInfo" to extraInfo)
        }
    }
}