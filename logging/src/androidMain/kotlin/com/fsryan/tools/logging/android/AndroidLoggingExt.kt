@file:JvmName("FSLogging")

package com.fsryan.tools.logging.android

import android.app.Application
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog

/**
 * A shortcut for ensuring all [ContextSpecificDevMetricsLogger] and
 * [ContextSpecificEventLogger] instances are initialized. You should call this
 * function in your application's onCreate like this:
 * ```
 * class App: Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         initFSLogging()
 *     }
 * }
 * ```
 */
fun Application.initFSLogging() {
    FSDevMetrics.onLoggersOfType(ContextSpecificDevMetricsLogger::class) {
        initialize(this@initFSLogging)
    }
    FSEventLog.onLoggersOfType(ContextSpecificEventLogger::class) {
        initialize(this@initFSLogging)
    }
}