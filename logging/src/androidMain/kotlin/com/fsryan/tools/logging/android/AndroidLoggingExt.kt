@file:JvmName("FSLogging")

package com.fsryan.tools.logging.android

import android.app.Application
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog
import com.fsryan.tools.logging.loggersOfType

fun Application.initFSLogging() {
    FSDevMetrics.loggersOfType(ContextSpecificDevMetricsLogger::class.java)
        .forEach { it.initialize(this) }
    FSEventLog.loggersOfType(ContextSpecificEventLogger::class.java)
        .forEach { it.initialize(this) }
}