package com.fsryan.tools.logging.android

import android.content.Context
import android.content.pm.PackageManager

/**
 * You configure the behavior of [CrashingDevMetricsLogger] via your app's
 * AndroidManifest.xml file. You must include a `meta-data` tag as a child
 * of the `application` tag as below:
 * ```xml
 * <application>
 *   <meta-data android:name="crashing_dev_metrics_level" android:value="watch" />
 * </application>
 * ```
 *
 * There are three valid values for crashing_dev_metrics_level:
 * * `info` -> crashes on any logging at all (info, watch, and alarm)
 * * `watch` -> crashes on watch and alarm
 * * `alarm` -> crashes on alarm only
 *
 * If you add this tag, but do not use one of these values, then the app will
 * not crash when this logger is invoked.
 */
class CrashingDevMetricsLogger : ContextSpecificDevMetricsLogger {

    @Volatile private var crashLevel: String = ""

    override fun initialize(context: Context) = context.applicationContext.run {
        val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        crashLevel = info.metaData?.getString("crashing_dev_metrics_level", "") ?: ""
    }

    override fun id() = "crashonalarm"
    override fun info(msg: String, info: String?, extraInfo: String?) = crashIfLevelIs("info")
    override fun watch(msg: String, info: String?, extraInfo: String?) = crashIfLevelIs("watch")
    override fun alarm(t: Throwable) = crashIfLevelIs("alarm", t)

    private fun crashIfLevelIs(level: String, withThrowable: Throwable? = null) {
        if (crashLevel == "") {
            return
        }

        if (crashLevel == "info"
            || level == crashLevel
            || (level == "alarm" && crashLevel == "watch")) {
            throw RuntimeException(withThrowable ?: Exception())
        }
    }
}