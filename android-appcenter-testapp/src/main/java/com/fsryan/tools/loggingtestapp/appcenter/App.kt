package com.fsryan.tools.loggingtestapp.appcenter

import android.app.Application
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog
import com.fsryan.tools.logging.android.NonFatalDevMetricsLogger
import com.fsryan.tools.logging.android.initFSLogging

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FSDevMetrics.info(
            msg = "pre init can log to loggers that do not need to be initialized",
            destinations = *arrayOf("logcat")
        )
        FSEventLog.addEvent(
            eventName = "pre init can log to loggers that do not need to be initialized",
            destinations = *arrayOf("logcat")
        )
        initFSLogging()
        // Regardless of what the library says, you can set the
        // NonFatalDevMetricsLogger to enabled
        FSDevMetrics.loggersOfType(NonFatalDevMetricsLogger::class.java).forEach { it.enabled.set(true) }
    }
}