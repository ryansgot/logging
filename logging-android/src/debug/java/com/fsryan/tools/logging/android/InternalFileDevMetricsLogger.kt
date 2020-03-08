package com.fsryan.tools.logging.android

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * You configure the behavior of [InternalFileDevMetricsLogger] via your app's
 * AndroidManifest.xml file. You must include a `meta-data` tag as a child
 * of the `application` tag as below:
 * ```xml
 * <application>
 *   <meta-data android:name="internal_dev_metrics_log" android:value="internal/log/path" />
 * </application>
 * ```
 *
 * If you do not provide this meta data, then the app will crash.
 *
 * When [initialize] is called, [InternalFileDevMetricsLogger] will create the
 * necessary directory and file and go ahead and log the current time at app
 * start to it.
 */
class InternalFileDevMetricsLogger : ContextSpecificDevMetricsLogger {

    private lateinit var outputFile: File

    override fun initialize(context: Context) = context.applicationContext.run {
        val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val path = info.metaData?.getString("internal_dev_metrics_log", null) ?: throw IllegalStateException("application meta-data named internal_dev_metrics_log required")
        outputFile = File(context.filesDir, path)
        val parentDir = checkNotNull(outputFile.parentFile)
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw IllegalStateException("Could not create parent directory for dev metrics log file: $outputFile")
        }
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw IllegalStateException("Could not create dev metrics log file: $outputFile")
        }

        outputFile.appendText("\n\n\nApp Initialized: ${Date()}\n\n\n")
    }

    override fun id(): String = "internalfile"

    override fun alarm(t: Throwable, attrs: Map<String, String>) {
        val stacktrace = StringWriter().use { sw ->
            PrintWriter(sw).use { pw ->
                t.printStackTrace(pw)
                sw.toString()
            }
        }
        outputFile.appendText("\n${Date()} [ALARM]\n$stacktrace")
        attrs.printAll("\t")
    }

    override fun watch(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        outputFile.appendText("\n${Date()} [WATCH] $msg")
        combine(info, extraInfo, attrs).printAll("\t")
    }

    override fun info(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        outputFile.appendText("\n${Date()} [INFO] $msg")
        combine(info, extraInfo, attrs).printAll("\t")
    }

    override fun metric(operationName: String, durationNanos: Long) {
        outputFile.appendText("\n${Date()} [METRIC] '$operationName' took $durationNanos nanos")
    }

    private fun Map<String, String>.printAll(prefix: String) = entries.forEach {
        outputFile.appendText("\n$prefix${it.key}=${it.value}")
    }
}