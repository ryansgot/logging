@file:JvmName("FSNewRelicUtil")

package com.fsryan.tools.logging.android.newrelic

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.fsryan.tools.logging.FSEventLog
import com.newrelic.agent.android.FeatureFlag
import com.newrelic.agent.android.NewRelic
import com.newrelic.agent.android.logging.AgentLog



/**
 * Extend the capabilities of [FSEventLog] to allow for logging events of a
 * different event type, which is not made available to [FSEventLog] out of the
 * box.
 */
@AnyThread
fun FSEventLog.logToNewRelicWithEventType(
    eventType: String,
    eventName: String,
    attrs: MutableMap<String, String> = mutableMapOf(),
    vararg otherDestinations: String = emptyArray()
) {
    val actualDestArray = Array(otherDestinations.size + 1) { idx ->
        when (idx < otherDestinations.size) {
            true -> otherDestinations[idx]
            false -> "newrelic"
        }
    }
    attrs["__event_type"] = eventType
    addEvent(eventName, attrs, *actualDestArray)
}

@MainThread
internal fun Context.startNewRelicIfNecessary() {
    if (NewRelic.isStarted()) {
        return
    }
    val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)

    with(appInfo) {
        val newRelicToken = metaData.getString("fsryan.nrgt") ?: throw IllegalStateException("fsryan.nrgt required")

        // feature flag config
        metaData.boolOrNull("fsryan.nrffae")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.AnalyticsEvents) }
        metaData.boolOrNull("fsryan.nrffcre")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.CrashReporting) }
        metaData.boolOrNull("fsryan.nrffhe")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.HandledExceptions) }
        metaData.boolOrNull("fsryan.nrffit")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.InteractionTracing) }
        metaData.boolOrNull("fsryan.nrffdi")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.DefaultInteractions) }
        metaData.boolOrNull("fsryan.nrffnr")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.NetworkRequests) }
        metaData.boolOrNull("fsryan.nrffner")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.NetworkErrorRequests) }
        metaData.boolOrNull("fsryan.nrffhrbce")?.let { toggleNewRelicFeatureFlag(it, FeatureFlag.HttpResponseBodyCapture) }

        // builder config
        var newRelic = NewRelic.withApplicationToken(newRelicToken)
        metaData.boolOrNull("fsryan.nrae")?.let { newRelic = newRelic.withAnalyticsEvents(it) }
        metaData.boolOrNull("fsryan.nrhrbce")?.let { newRelic = newRelic.withHttpResponseBodyCaptureEnabled(it) }
        metaData.boolOrNull("fsryan.nrcre")?.let { newRelic = newRelic.withCrashReportingEnabled(it) }
        metaData.getString("fsryan.nrav")?.let { newRelic = newRelic.withApplicationVersion(it) }
        metaData.getString("fsryan.nrab")?.let { newRelic = newRelic.withApplicationBuild(it) }
        metaData.boolOrNull("fsryan.nrle")?.let { newRelic = newRelic.withLoggingEnabled(it) }
        when (metaData.getString("fsryan.nrll")) {
            "ERROR" -> AgentLog.ERROR
            "WARNING" -> AgentLog.WARNING
            "INFO" -> AgentLog.INFO
            "VERBOSE" -> AgentLog.VERBOSE
            "DEBUG" -> AgentLog.DEBUG
            "AUDIT" -> AgentLog.AUDIT
            else -> null
        }?.let { newRelic = newRelic.withLogLevel(it) }
        metaData.getString("fsryan.nrca")?.let { newRelic = newRelic.usingCollectorAddress(it) }
        metaData.getString("fsryan.nrcca")?.let { newRelic = newRelic.usingCrashCollectorAddress(it) }
        newRelic.start(applicationContext)
    }
}


private fun Bundle.boolOrNull(key: String) = when (containsKey(key)) {
    true -> getBoolean(key)
    else -> null
}


private fun toggleNewRelicFeatureFlag(enable: Boolean, featureFlag: FeatureFlag) {
    when (enable) {
        true -> NewRelic.enableFeature(featureFlag)
        false -> NewRelic.disableFeature(featureFlag)
    }
}