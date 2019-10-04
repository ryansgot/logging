package com.fsryan.tools.logging.android

import android.content.Context
import com.fsryan.tools.logging.FSDevMetricsLogger

interface ContextSpecificDevMetricsLogger : FSDevMetricsLogger {
    fun initialize(context: Context)
}