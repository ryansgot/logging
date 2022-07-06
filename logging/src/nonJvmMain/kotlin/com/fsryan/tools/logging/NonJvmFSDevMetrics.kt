package com.fsryan.tools.logging

import co.touchlab.stately.isolate.IsolateState
import kotlinx.datetime.Clock
import kotlin.reflect.KClass

actual object FSDevMetrics {

    internal val state = IsolateState {
        FSLoggerMutableValues(loggers = createInitialDevMetricsLoggers())
    }

    actual fun addLogger(logger: FSDevMetricsLogger) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers[logger.id()] = logger
            }
        }
    }

    actual fun alarm(t: Throwable, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    alarm(t, attrs)
                }
            }
        }
    }

    actual fun watch(
        msg: String,
        info: String?,
        extraInfo: String?,
        attrs: Map<String, String>,
        vararg destinations: String
    ) {
        launch {
            state.access { mutableValues ->
                val actualAttrs = combineLegacyInfosWithAttrs(attrs = attrs, info = info, extraInfo = extraInfo)
                mutableValues.loggers.onSomeOrAll(destinations) {
                    watch(msg, actualAttrs)
                }
            }
        }
    }

    actual fun startTimedOperation(operationName: String, operationId: Int): Int {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val nanos = now.nanosecondsOfSecond
        launch {
            state.access { mutableValues ->
                val startTime = seconds * 1_000_000_000 + nanos
                mutableValues.metrics.add(
                    operationName,
                    operationId,
                    FSTimedOpData(startTime, emptyMap())
                )
            }
        }
        return operationId
    }

    actual fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            state.access { mutableValues ->
                mutableValues.metrics.consumeOperationMetric(operationName, operationId)
            }
        }
    }

    actual fun commitTimedOperation(
        operationName: String,
        operationId: Int,
        vararg destinations: String
    ) {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val nanos = now.nanosecondsOfSecond
        launch {
            state.access { mutableValues ->
                val stopTime = seconds * 1_000_000_000 + nanos
                mutableValues.metrics.consumeOperationMetric(operationName, operationId)?.let { metric ->
                    val diff = stopTime - metric.startTime
                    mutableValues.loggers.onSomeOrAll(destinations) {
                        metric(operationName, diff)
                    }
                }
            }
        }
    }

    actual fun info(
        msg: String,
        info: String?,
        extraInfo: String?,
        attrs: Map<String, String>,
        vararg destinations: String
    ) {
        launch {
            state.access { mutableValues ->
                val actualAttrs = combineLegacyInfosWithAttrs(attrs, info = info, extraInfo = extraInfo)
                mutableValues.loggers.onSomeOrAll(destinations) {
                    info(msg, actualAttrs)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T: FSDevMetricsLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.values.forEach { logger ->
                    if (cls.isInstance(logger)) {
                        (logger as T).perform()
                    }
                }
            }
        }
    }
}