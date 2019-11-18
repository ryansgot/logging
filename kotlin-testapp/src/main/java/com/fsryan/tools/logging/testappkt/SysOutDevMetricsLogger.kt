package com.fsryan.tools.logging.testappkt

import com.fsryan.tools.logging.FSDevMetricsLogger
import com.fsryan.tools.logging.safeConcat

internal class SysOutDevMetricsLogger : FSDevMetricsLogger {
    override fun id(): String = "sysout"
    override fun alarm(t: Throwable) = println("[FS/ALARM]: $t")
    override fun watch(msg: String, info: String?, extraInfo: String?) = println("[FS/WATCH]: ${safeConcat(msg, info, extraInfo)}")
    override fun info(msg: String, info: String?, extraInfo: String?) = println("[FS/INFO]: ${safeConcat(msg, info, extraInfo)}")
    override fun metric(operationName: String, durationNanos: Long) = println("[FS/METRIC]: '$operationName' took $durationNanos nanos")
}