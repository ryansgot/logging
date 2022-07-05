@file:JvmName("FSLogging")

package com.fsryan.tools.logging.android

import android.app.Application
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog

fun Application.initFSLogging() {
    FSDevMetrics.onLoggersOfType(ContextSpecificDevMetricsLogger::class) {
        initialize(this@initFSLogging)
    }
    FSEventLog.onLoggersOfType(ContextSpecificEventLogger::class) {
        initialize(this@initFSLogging)
    }
}