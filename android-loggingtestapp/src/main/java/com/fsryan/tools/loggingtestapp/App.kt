package com.fsryan.tools.loggingtestapp

import android.app.Application
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog
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
    }
}