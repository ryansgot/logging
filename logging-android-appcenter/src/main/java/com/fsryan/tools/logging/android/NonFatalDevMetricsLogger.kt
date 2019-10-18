package com.fsryan.tools.logging.android

import android.os.SystemClock
import com.fsryan.tools.logging.FSDevMetricsLogger
import com.fsryan.tools.logging.android.appcenter.BuildConfig
import com.microsoft.appcenter.Flags
import com.microsoft.appcenter.analytics.Analytics
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean

class NonFatalDevMetricsLogger : FSDevMetricsLogger {

    val enabled = AtomicBoolean(BuildConfig.APPCENTER_ENABLED_BY_DEFAULT)

    // This is not 100% accurate, but it should be close enough for our
    // purposes. The idea here is that we want to understand when alarms occur
    // in relation to when the application was started
    private val appStartTimeMillis = System.currentTimeMillis()

    override fun id() = "nonfatal"

    override fun alarm(t: Throwable) {
        if (!enabled.get()) {
            return
        }

        val stacktrace = StringWriter().use { sw ->
            PrintWriter(sw).use { pw ->
                t.printStackTrace(pw)
            }
            sw.toString()
        }

        Analytics.trackEvent(
            "nonfatal",
            mapOf(
                "system_uptime" to SystemClock.uptimeMillis().toString(),
                "app_uptime" to (System.currentTimeMillis() - appStartTimeMillis).toString(),
                "exception" to stacktrace
            ),
            Flags.CRITICAL
        )
    }
}