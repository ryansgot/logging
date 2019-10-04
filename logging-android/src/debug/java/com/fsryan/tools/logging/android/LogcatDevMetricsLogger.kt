package com.fsryan.tools.logging.android

import android.util.Log
import com.fsryan.tools.logging.FSDevMetricsLogger
import com.fsryan.tools.logging.safeConcat

class LogcatDevMetricsLogger : FSDevMetricsLogger {
    override fun id() = "logcat"
    override fun alarm(t: Throwable) {
        Log.e("FSDevMetrics", "", t)
    }
    override fun watch(msg: String, info: String?, extraInfo: String?) {
        Log.w("FSDevMetrics", safeConcat(msg, info, extraInfo))
    }
    override fun info(msg: String, info: String?, extraInfo: String?) {
        Log.i("FSDevMetrics", safeConcat(msg, info, extraInfo))
    }
}