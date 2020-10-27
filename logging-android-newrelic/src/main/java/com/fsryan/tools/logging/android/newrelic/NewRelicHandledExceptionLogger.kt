package com.fsryan.tools.logging.android.newrelic

import android.content.Context
import com.fsryan.tools.logging.android.ContextSpecificDevMetricsLogger
import com.newrelic.agent.android.NewRelic

class NewRelicHandledExceptionLogger: ContextSpecificDevMetricsLogger {
    override fun initialize(context: Context) {
        context.startNewRelicIfNecessary()
    }

    override fun id(): String = "newrelic_nonfatal"

    override fun alarm(t: Throwable, attrs: Map<String, String>) {
        if (t is Exception) {
            NewRelic.recordHandledException(t, attrs)
        }
    }
}