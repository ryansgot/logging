package com.fsryan.tools.logging

import platform.Foundation.NSLog

class NSLogDevMetricsLogger: FSDevMetricsLogger {
    override fun id(): String = "nslogdevmetrics"

    override fun alarm(t: Throwable, attrs: Map<String, String>) {
        NSLog("[nslogdevmetrics] alarm(t=$t; attrs=$attrs)")
    }

    override fun watch(msg: String, attrs: Map<String, String>) {
        NSLog("[nslogdevmetrics] watch(msg='$msg'; attrs=$attrs)")
    }

    override fun info(msg: String, attrs: Map<String, String>) {
        NSLog("[nslogdevmetrics] alarm(msg='$msg'; attrs=$attrs)")
    }

    override fun metric(operationName: String, durationNanos: Long) {
        NSLog("[nslogdevmetrics] metric(operationName='$operationName'; durationNanos=$durationNanos)")
    }
}