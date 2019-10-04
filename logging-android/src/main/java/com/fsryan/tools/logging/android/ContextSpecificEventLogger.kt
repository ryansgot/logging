package com.fsryan.tools.logging.android

import android.content.Context
import com.fsryan.tools.logging.FSEventLogger

interface ContextSpecificEventLogger : FSEventLogger {
    fun initialize(context: Context)
}