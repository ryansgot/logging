package com.fsryan.tools.logging.android

import android.content.Context
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class FirebaseAnalyticsMetricsLogger: ContextSpecificDevMetricsLogger {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun id() = "firebase"

    override fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)
        firebaseAnalytics = Firebase.analytics
    }

    override fun watch(msg: String, attrs: Map<String, String>) = send("watch", msg, attrs)

    override fun info(msg: String, attrs: Map<String, String>) = send("info", msg, attrs)

    override fun metric(operationName: String, durationNanos: Long) {
        firebaseAnalytics.logEvent("devMetric", Bundle().apply {
            putString("operation", operationName)
            putLong("duration", durationNanos)
        })
    }

    private fun send(
        type: String,
        msg: String?,
        attrs: Map<String, String>
    ) = firebaseAnalytics.logEvent("devLog", Bundle().apply {
        putString("devLogType", type)
        putString("devMessage", msg)
        attrs.entries.forEach { putString(it.key, it.value) }
    })
}