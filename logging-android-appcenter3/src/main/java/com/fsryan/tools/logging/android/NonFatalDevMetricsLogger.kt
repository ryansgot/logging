package com.fsryan.tools.logging.android

import android.os.SystemClock
import com.fsryan.tools.logging.FSDevMetricsLogger
import com.microsoft.appcenter.crashes.Crashes

/**
 * AppCenter does not appear to support non-fatal exceptions. Even worse, these
 * exception stacktraces will likely get cut off due to length limit
 * restrictions. I'm afraid at this time, this is the best I can do.
 */
class NonFatalDevMetricsLogger : FSDevMetricsLogger {

    // This is not 100% accurate, but it should be close enough for our
    // purposes. The idea here is that we want to understand when alarms occur
    // in relation to when the application was started
    private val appStartTimeMillis = System.currentTimeMillis()

    override fun id() = "nonfatal"

    override fun alarm(t: Throwable, attrs: Map<String, String>) {
        if (!FSAppCenter.crashesEnabled.get()) {
            return
        }

        Crashes.trackError(t, attrs + mapOf(
            "system_uptime" to SystemClock.uptimeMillis().toString(),
            "app_uptime" to (System.currentTimeMillis() - appStartTimeMillis).toString()
        ), emptyList())
    }
}