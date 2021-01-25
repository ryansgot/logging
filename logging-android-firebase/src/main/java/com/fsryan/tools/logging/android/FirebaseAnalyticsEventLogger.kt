package com.fsryan.tools.logging.android

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class FirebaseAnalyticsEventLogger : ContextSpecificEventLogger() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun id() = "firebase"

    override fun initialize(context: Context) {
        firebaseAnalytics = Firebase.analytics
        super.initialize(context)
    }

    override fun addAttr(attrName: String, attrValue: String) {
        firebaseAnalytics.setUserProperty(attrName, attrValue)
        super.addAttr(attrName, attrValue)
    }

    override fun removeAttr(attrName: String) {
        firebaseAnalytics.setUserProperty(attrName, null)
        super.removeAttr(attrName)
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        firebaseAnalytics.logEvent(eventName, Bundle().apply {
            addDefaultAttrsTo(attrs).entries.forEach {
                when {
                    isDoubleProperty(it.key) -> putDouble(it.key, it.value.toDouble())
                    isLongProperty(it.key) -> putLong(it.key, it.value.toLong())
                    isBooleanProperty(it.key) -> putBoolean(it.key, it.value.toBoolean())
                    else -> putString(it.key, it.value)
                }
            }
        })
    }
}