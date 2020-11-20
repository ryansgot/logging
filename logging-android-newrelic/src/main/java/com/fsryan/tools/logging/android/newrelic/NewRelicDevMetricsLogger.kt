package com.fsryan.tools.logging.android.newrelic

import android.content.Context
import android.content.pm.PackageManager
import com.fsryan.tools.logging.android.ContextSpecificDevMetricsLogger

import com.newrelic.agent.android.NewRelic
import com.newrelic.agent.android.instrumentation.MetricCategory
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class NewRelicDevMetricsLogger: ContextSpecificDevMetricsLogger {

    private val infoType = AtomicReference<String>()
    private val watchType = AtomicReference<String>()

    override fun id(): String = "newrelic"

    override fun initialize(context: Context) {
        context.startNewRelicIfNecessary()
        val appInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        infoType.set(appInfo.metaData.getString("fsryan.nr_dev_metric_info_event_type") ?: "DevMetricInfo")
        watchType.set(appInfo.metaData.getString("fsryan.nr_dev_metric_info_event_type") ?: "DevMetricWatch")
    }

    override fun watch(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        NewRelic.recordCustomEvent(watchType.get(), collapseAndCleanAttrs(msg, info, extraInfo, attrs))
    }

    override fun info(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>) {
        NewRelic.recordCustomEvent(infoType.get(), collapseAndCleanAttrs(msg, info, extraInfo, attrs))
    }

    override fun metric(operationName: String, durationNanos: Long) {
        NewRelic.recordMetric(
            operationName.capitalize(Locale.ROOT),
            MetricCategory.NONE.categoryName,
            durationNanos.toDouble()
        )
    }

    private fun collapseAndCleanAttrs(msg: String, info: String?, extraInfo: String?, attrs: Map<String, String>): Map<String, String> {
        val append = mutableMapOf<String, String>()
        append["msg"] = msg
        info?.let { append["info"] = it }
        extraInfo?.let { append["extraInfo"] = it }
        return (attrs + append).filterValues { it.isNotEmpty() }
    }
}