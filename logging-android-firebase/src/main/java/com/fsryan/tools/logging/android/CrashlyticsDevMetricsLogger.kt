package com.fsryan.tools.logging.android

import android.os.SystemClock
import com.crashlytics.android.Crashlytics
import com.fsryan.tools.logging.FSDevMetricsLogger
import java.util.concurrent.atomic.AtomicBoolean

class CrashlyticsDevMetricsLogger : FSDevMetricsLogger {

    val enabled = AtomicBoolean(BuildConfig.CRASHLYTICS_ENABLED_BY_DEFAULT)

    // This is not 100% accurate, but it should be close enough for our
    // purposes. The idea here is that we want to understand when alarms occur
    // in relation to when the application was started
    private val appStartTimeMillis = System.currentTimeMillis()

    override fun id() = "crashlytics"

    override fun alarm(t: Throwable, attrs: Map<String, String>) {
        if (!enabled.get()) {
            return
        }

        Crashlytics.setLong("system_uptime", SystemClock.uptimeMillis())
        Crashlytics.setLong("app_uptime", System.currentTimeMillis() - appStartTimeMillis)
        attrs.entries.forEach { Crashlytics.setString(it.key, it.value) }
        Crashlytics.logException(t)
    }
}