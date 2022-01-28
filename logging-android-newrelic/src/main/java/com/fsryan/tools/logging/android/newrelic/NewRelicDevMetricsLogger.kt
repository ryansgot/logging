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
        // TODO: do not support the meta-data approach in the 1.x release
        val appInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)

        setValueOfType(
            context = context,
            appInfo = appInfo,
            refToSet = infoType,
            legacyMetaDataKey = "fsryan.nr_dev_metric_info_event_type",
            nameStringRes = R.string.fs_dev_metrics_logger_info_newrelic_event_type
        )
        setValueOfType(
            context = context,
            appInfo = appInfo,
            refToSet = watchType,
            legacyMetaDataKey = "fsryan.nr_dev_metric_watch_event_type",
            nameStringRes = R.string.fs_dev_metrics_logger_watch_newrelic_event_type
        )
    }

    override fun watch(msg: String, attrs: Map<String, String>) {
        NewRelic.recordCustomEvent(watchType.get(), collapseAndCleanAttrs(msg, attrs))
    }

    override fun info(msg: String, attrs: Map<String, String>) {
        NewRelic.recordCustomEvent(infoType.get(), collapseAndCleanAttrs(msg, attrs))
    }

    override fun metric(operationName: String, durationNanos: Long) {
        NewRelic.recordMetric(
            operationName.capitalize(Locale.ROOT),
            MetricCategory.NONE.categoryName,
            durationNanos.toDouble()
        )
    }

    private fun collapseAndCleanAttrs(msg: String, attrs: Map<String, String>): Map<String, String> {
        val append = mutableMapOf<String, String>()
        append["msg"] = msg
        return (attrs + append).filterValues { it.isNotEmpty() }
    }
}