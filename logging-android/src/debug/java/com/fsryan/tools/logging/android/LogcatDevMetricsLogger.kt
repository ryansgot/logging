package com.fsryan.tools.logging.android

import android.util.Log
import com.fsryan.tools.logging.FSDevMetricsLogger

class LogcatDevMetricsLogger : FSDevMetricsLogger {

    override fun id() = "logcat"

    override fun alarm(t: Throwable, attrs: Map<String, String>) {
        Log.e("FSDevMetrics", "attrs: $attrs", t)
    }

    override fun watch(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        Log.w("FSDevMetrics", "msg: $msg; attrs: ${combine(info, extraInfo, attrs)}")
    }

    override fun info(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        Log.i("FSDevMetrics", "msg: $msg; attrs: ${combine(info, extraInfo, attrs)}")
    }

    override fun metric(operationName: String, durationNanos: Long) {
        Log.i("FSDevMetrics", "metric: '$operationName' took $durationNanos nanos")
    }
}

internal fun combine(info: String?, extraInfo: String?, attrs: Map<String, String>) = attrs + mapOf(
    "info" to (info ?: ""),
    "extraInfo" to (extraInfo ?: "")
)