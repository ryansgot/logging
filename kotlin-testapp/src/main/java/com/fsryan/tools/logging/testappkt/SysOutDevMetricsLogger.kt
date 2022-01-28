package com.fsryan.tools.logging.testappkt

import com.fsryan.tools.logging.FSDevMetricsLogger

internal class SysOutDevMetricsLogger : FSDevMetricsLogger {
    override fun id(): String = "sysout"

    override fun alarm(t: Throwable, attrs: Map<String, String>) = println("[FS/ALARM]: $t; attrs: $attrs")

    override fun watch(msg: String, attrs: Map<String, String>) = println(
        "[FS/WATCH]: msg: $msg; attrs: $attrs}"
    )

    override fun info(msg: String, attrs: Map<String, String>) = println(
        "[FS/INFO]: msg: $msg; attrs: $attrs"
    )

    override fun metric(operationName: String, durationNanos: Long) = println("[FS/METRIC]: '$operationName' took $durationNanos nanos")
}