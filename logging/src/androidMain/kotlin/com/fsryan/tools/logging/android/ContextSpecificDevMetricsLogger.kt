package com.fsryan.tools.logging.android

import android.content.Context
import com.fsryan.tools.logging.FSDevMetricsLogger

/**
 * Sometimes, your [FSDevMetricsLogger] instance needs some information
 * accessible through the [Context]. Implement this interface if that is the
 * case.
 *
 * > Note: there is a [initFSLogging] function that you can use to ensure that
 * all registered [ContextSpecificDevMetricsLogger] instances will have their
 * [initialize] function called as early as possible in the app.
 */
interface ContextSpecificDevMetricsLogger : FSDevMetricsLogger {
    fun initialize(context: Context)
}