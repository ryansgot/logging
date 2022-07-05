@file:JvmName("FSNewRelicUtil")

package com.fsryan.tools.logging.android.newrelic

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.annotation.AnyThread
import androidx.annotation.StringRes
import com.fsryan.tools.logging.FSEventLog
import com.newrelic.agent.android.FeatureFlag
import com.newrelic.agent.android.NewRelic
import com.newrelic.agent.android.logging.AgentLog
import java.util.concurrent.atomic.AtomicReference

internal const val ATTR_EVENT_TYPE_OVERRIDE = "__event_type_override"

/**
 * Extend the capabilities of [FSEventLog] to allow for logging events of a
 * different event type, which is not made available to [FSEventLog] out of the
 * box. This is logged to only new relic by default, but you can change this by
 * supplying a [destinations] argument.
 */
@AnyThread
@JvmOverloads
fun FSEventLog.logWithEventType(
    eventType: String,
    eventName: String,
    attrs: MutableMap<String, String> = mutableMapOf(),
    vararg destinations: String = arrayOf("newrelic")
) {
    attrs[ATTR_EVENT_TYPE_OVERRIDE] = eventType
    addEvent(eventName, attrs, *destinations)
}

internal fun Context.startNewRelicIfNecessary() {
    // The main thread is the only safe thread on which to start newrelic, as
    // the underlying NewRelic.isStarted() function accesses unprotected state.
    // NewRelic does some of its own analytics gathering and such on the main
    // thread, so there really is not much other choice than to force the call
    // onto the main-thread.
    if (mainLooper.thread  != Thread.currentThread()) {
        Handler(mainLooper).post {
            startNewRelicIfNecessary()
        }
        return
    }

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

internal fun setValueOfType(
    context: Context,
    appInfo: ApplicationInfo,
    refToSet: AtomicReference<String>,
    legacyMetaDataKey: String,
    @StringRes nameStringRes: Int
) {
    val legacyEventType = appInfo.metaData.getString(legacyMetaDataKey)
    val resourceEventType = context.getString(nameStringRes)
    refToSet.set(legacyEventType ?: resourceEventType)
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