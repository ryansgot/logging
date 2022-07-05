package com.fsryan.tools.logging

import co.touchlab.stately.isolate.IsolateState
import kotlinx.datetime.Clock
import kotlin.reflect.KClass

/**
 * The single place you need to use in order to log developer-centric events.
 * This object does not do any logging on its own, rather, it either logs to
 * a registered implementation of [FSDevMetricsLogger] (when provided a
 * nonempty the dest parameter) or to all registered implementations of
 * [FSDevMetricsLogger] when the dest parameter is not provided.
 *
 * Registration of [FSDevMetricsLogger] instances occurs via
 * [Java SPI](https://www.baeldung.com/java-spi).
 * The order that the [FSDevMetricsLogger] instances are invoked is the order
 * in which they're specified in the
 * `resources/META-INF/services/com.fsryan.tools.logging.FSDevMetricsLogger`
 * file.
 *
 * Threading: any thread
 * Note that each method call will distribute the work to the executor you have
 * created in your implementation of [FSLoggingConfig]. If you do not supply an
 * [FSLoggingConfig], then the work will be distributed to a single-threaded
 * [ExecutorService] created by this library for handling all [FSDevMetrics]
 * operations.
 */
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

    /**
     * Either sends the alarm specifically to the [destinations] when supplied,
     * or sends the info to all registered [state] when [destinations] not
     * supplied. Add supplemental attributes via the [attrs] parameter.
     * @see [FSDevMetricsLogger.alarm]
     */
    actual fun alarm(t: Throwable, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    alarm(t, attrs)
                }
            }
        }
    }

    /**
     * Either sends the watch specifically to the [destinations] when supplied,
     * or sends the watch to all registered [state] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.watch]
     */
    actual fun watch(msg: String, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    watch(msg, attrs)
                }
            }
        }
    }

    /**
     * Starts a timer for the operation [operationName] and returns the
     * [operationId] used along with the [operationName] to either
     * [cancelTimedOperation] or to [commitTimedOperation]. If you do not
     * supply the [operationId] yourself, then a randomly generated id will be
     * returned.
     */
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

    /**
     * Cancels the timer for the operation.
     */
    actual fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            state.access { mutableValues ->
                mutableValues.metrics.consumeOperationMetric(operationName, operationId)
            }
        }
    }

    /**
     * Commits the timed operation with the name [operationName] and id
     * [operationId] input to the [destinations] (or all destinations if none
     * specified).
     */
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

    /**
     * Either sends the info specifically to the [destinations] when supplied,
     * or sends the info to all registered [state] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.info]
     */
    actual fun info(
        msg: String,
        attrs: Map<String, String>,
        vararg destinations: String
    ) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    info(msg, attrs)
                }
            }
        }
    }

    /**
     * Enables post-instantiation configuration of [FSDevMetricsLogger] instances
     * of a type the class [T]. Ideally, this function is called very early in the
     * application's lifecycle.
     */
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