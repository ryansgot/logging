package com.fsryan.tools.logging.android

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsMetricsLogger: ContextSpecificDevMetricsLogger {

    @Volatile private lateinit var fbAnalytics: FirebaseAnalytics

    override fun initialize(context: Context) {
        fbAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    override fun id() = "firebase"
    override fun watch(msg: String, info: String?, extraInfo: String?) = send("watch", msg, info, extraInfo)
    override fun info(msg: String, info: String?, extraInfo: String?) = send("info", msg, info, extraInfo)

    override fun metric(operationName: String, durationNanos: Long) = fbAnalytics.logEvent("devMetric", Bundle().apply {
        putString("operation", operationName)
        putLong("duration", durationNanos)
    })

    private fun send(type: String, msg: String?, info: String?, extra: String?) = fbAnalytics.logEvent("devLog", Bundle().apply {
        putString("devLogType", type)
        putString("devMessage", msg)
        putString("devInfo", info)
        putString("devExtraInfo", extra)
    })
}