package com.fsryan.tools.logging.android

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.datadog.android.Datadog
import com.datadog.android.DatadogConfig
import com.datadog.android.log.Logger
import kotlin.IllegalStateException

object FSDataDog {

    @Volatile private var logger: Logger? = null

    @AnyThread
    fun logger() = logger ?: throw IllegalStateException("Must call ensureInitialized before getting logger")

    @MainThread
    fun ensureInitialized(context: Context) {
        if (Datadog.isInitialized()) {
            return
        }

        val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val clientToken = appInfo.metaData?.getString("fsddsec")
            ?: throw IllegalStateException("application info must have fsddsec meta-data on it")

        val datadogConfigBuilder = DatadogConfig.Builder(clientToken)
        if (appInfo.metaData?.getBoolean("fsddeu", false) == true) {
            datadogConfigBuilder.useEUEndpoints()
        } else {
            datadogConfigBuilder.useUSEndpoints()
        }

        Datadog.initialize(context, datadogConfigBuilder.build())

        val loggerBuilder = Logger.Builder()
            .setNetworkInfoEnabled(appInfo.metaData?.getBoolean("fsdd-network-info-enabled", false) ?: false)
            .setLogcatLogsEnabled(appInfo.metaData?.getBoolean("fsdd-logcat-logs-enabled", false) ?: false)
            .setDatadogLogsEnabled(appInfo.metaData?.getBoolean("fsdd-datadog-logs-enabled", true) ?: true)
            .setBundleWithTraceEnabled(appInfo.metaData?.getBoolean("fsdd-bundle-with-trace-enabled", true) ?: true)
            .setSampleRate(appInfo.metaData?.getFloat("fsdd-sample-rate", 1F) ?: 1F)
            .setServiceName(appInfo.metaData?.getString("fsdd-service-name", "android") ?: "android")
        val loggerName = appInfo.metaData?.getString("fsdd-logger-name", "fsddlog") ?: "fsddlog"
        val appendThreadName = appInfo.metaData?.getBoolean("fsdd-logger-name-append-thread-name", false) ?: false

        logger = when (appendThreadName) {
            true -> loggerBuilder.setLoggerName("$loggerName-${Thread.currentThread().name}")
            false -> loggerBuilder.setLoggerName(loggerName)
        }.build()
    }
}