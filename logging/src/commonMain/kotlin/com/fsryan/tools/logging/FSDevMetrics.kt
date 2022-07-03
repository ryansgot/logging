package com.fsryan.tools.logging

import kotlinx.datetime.Clock
import kotlin.collections.LinkedHashMap
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.random.Random

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
object FSDevMetrics {

    internal val loggers: LinkedHashMap<String, FSDevMetricsLogger> = createAllDevMetricsLoggers()
    internal val metricMap: MutableMap<String, MutableMap<Int, Long>> = mutableMapOf()

    /**
     * Either sends the alarm specifically to the [destinations] when supplied,
     * or sends the info to all registered [loggers] when [destinations] not
     * supplied. Add supplemental attributes via the [attrs] parameter.
     * @see [FSDevMetricsLogger.alarm]
     */
    @JvmStatic
    @JvmOverloads
    fun alarm(t: Throwable, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { alarm(t, attrs) }
        }
    }

    /**
     * Either sends the watch specifically to the [destinations] when supplied,
     * or sends the watch to all registered [loggers] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.watch]
     */
    @JvmStatic
    @JvmOverloads
    fun watch(msg: String, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) {
                watch(msg, attrs)
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
    @JvmStatic
    @JvmOverloads
    fun startTimedOperation(operationName: String, operationId: Int = Random.nextInt()): Int {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val nanos = now.nanosecondsOfSecond
        launch {
            val startTime = seconds * 1_000_000_000 + nanos
            var current = metricMap[operationName]
            if (current == null) {
                current = mutableMapOf()
                metricMap[operationName] = current
            }
            current[operationId] = startTime
        }
        return operationId
    }

    /**
     * Cancels the timer for the operation.
     */
    @JvmStatic
    fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            metricMap[operationName]?.remove(operationId)
        }
    }

    /**
     * Commits the timed operation with the name [operationName] and id
     * [operationId] input to the [destinations] (or all destinations if none
     * specified).
     */
    @JvmStatic
    @JvmOverloads
    fun commitTimedOperation(
        operationName: String,
        operationId: Int,
        vararg destinations: String = emptyArray()
    ) {
        val now = Clock.System.now()
        val seconds = now.epochSeconds
        val nanos = now.nanosecondsOfSecond
        launch {
            val stopTime = seconds * 1_000_000_000 + nanos
            metricMap[operationName]?.remove(operationId)?.let { startTime ->
                val diff = stopTime - startTime
                activeLoggers().onSomeOrAll(destinations) { metric(operationName, diff) }
            }
        }
    }

    /**
     * Either sends the info specifically to the [destinations] when supplied,
     * or sends the info to all registered [loggers] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.info]
     */
    @JvmStatic
    @JvmOverloads
    fun info(
        msg: String,
        attrs: Map<String, String> = emptyMap(),
        vararg destinations: String = emptyArray()
    ) {
        launch {
            activeLoggers().onSomeOrAll(destinations) {
                info(msg, attrs)
            }
        }
    }

    private fun activeLoggers() = when (loggingConfig.testEnv) {
        true -> loggers.supportingTestEnvironment()
        false -> loggers
    }
}