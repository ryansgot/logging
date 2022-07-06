package com.fsryan.tools.logging

import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.reflect.KClass

actual object FSDevMetrics {

    internal val mutableValues = FSLoggerMutableValues(loggers = createInitialDevMetricsLoggers())

    @JvmStatic
    actual fun addLogger(logger: FSDevMetricsLogger) {
        launch {
            mutableValues.loggers[logger.id()] = logger
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun alarm(t: Throwable, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                alarm(t, attrs)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun watch(
        msg: String,
        info: String?,
        extraInfo: String?,
        attrs: Map<String, String>,
        vararg destinations: String
    ) {
        launch {
            val actualAttrs = combineLegacyInfosWithAttrs(attrs = attrs, info = info, extraInfo = extraInfo)
            mutableValues.loggers.onSomeOrAll(destinations) {
                watch(msg, actualAttrs)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun startTimedOperation(operationName: String, operationId: Int): Int {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val nanos = now.nanosecondsOfSecond
        launch {
            val startTime = seconds * 1_000_000_000 + nanos
            mutableValues.metrics.add(operationName, operationId, FSTimedOpData(startTime, emptyMap()))
        }
        return operationId
    }

    @JvmStatic
    actual fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            mutableValues.metrics.consumeOperationMetric(operationName, operationId)
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun commitTimedOperation(
        operationName: String,
        operationId: Int,
        vararg destinations: String
    ) {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val nanos = now.nanosecondsOfSecond
        launch {
            val stopTime = seconds * 1_000_000_000 + nanos
            mutableValues.metrics.consumeOperationMetric(operationName, operationId)?.let { metric ->
                val diff = stopTime - metric.startTime
                mutableValues.loggers.onSomeOrAll(destinations) {
                    metric(operationName, diff)
                }
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun info(
        msg: String,
        info: String?,
        extraInfo: String?,
        attrs: Map<String, String>,
        vararg destinations: String
    ) {
        launch {
            val actualAttrs = combineLegacyInfosWithAttrs(attrs, info = info, extraInfo = extraInfo)
            mutableValues.loggers.onSomeOrAll(destinations) {
                info(msg, actualAttrs)
            }
        }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    actual fun <T: FSDevMetricsLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit) {
        launch {
            mutableValues.loggers.values.forEach { logger ->
                if (cls.isInstance(logger)) {
                    (logger as T).perform()
                }
            }
        }
    }
}