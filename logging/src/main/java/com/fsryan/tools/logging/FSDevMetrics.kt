@file:JvmName("DevMetricsUtil")

package com.fsryan.tools.logging

import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import kotlin.collections.LinkedHashMap
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

    /**
     * Threading: writes only during object initialization. Reads from logging
     * executor. Therefore the list is effectively immutable wrt readers.
     *
     * Visibility: visible for inner access (avoids synthetic accessor)
     */
    internal val loggers: LinkedHashMap<String, FSDevMetricsLogger> = LinkedHashMap()
    private val executor: Executor

    /**
     * Threading: writes/reads may happen on any thread, but in accordance with
     * the [ConcurrentHashMap] documentation:
     * > Retrieval operations (including get) generally do not block, so may
     * overlap with update operations (including put and remove). Retrievals
     * reflect the results of the most recently completed update operations
     * holding upon their onset.
     *
     * Visibility: visible for inner access (avoids synthetic accessor)
     */
    internal val metricMap: ConcurrentHashMap<String, ConcurrentHashMap<Int, Long>> = ConcurrentHashMap()

    init {
        val loader = ServiceLoader.load(FSDevMetricsLogger::class.java)
        loader.forEach { loggers[it.id()] = it }

        if (loggers.isEmpty()) {
            println("WARNING: no FSDevMetrics found")
        }

        val config = ServiceLoader.load(FSLoggingConfig::class.java).firstOrNull()
        executor = (config ?: createDefaultConfig("FSDevMetrics")).createExecutor()
    }

    /**
     * Retrieve all extensions of [FSDevMetricsLogger] that are assignable from
     * the class [T]. You may want to use this to affect some required
     * configuration of some underlying logger. Ideally, this function is
     * called very early in the application's lifecycle.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : FSDevMetricsLogger> loggersOfType(cls: Class<T>): List<T> = loggers.values
        .filter { cls.isAssignableFrom(it.javaClass) }
        .map { it as T }

    /**
     * Either sends the alarm specifically to the [destinations] when supplied,
     * or sends the info to all registered [loggers] when [destinations] not
     * supplied.
     * @see [FSDevMetricsLogger.alarm]
     */
    @JvmStatic
    @JvmOverloads
    fun alarm(t: Throwable, vararg destinations: String = emptyArray()) = executor.execute {
        loggers.onSomeOrAll(destinations) { alarm(t) }
    }

    /**
     * Either sends the watch specifically to the [destinations] when supplied,
     * or sends the watch to all registered [loggers] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.watch]
     */
    @JvmStatic
    @JvmOverloads
    fun watch(
        msg: String,
        info: String? = null,
        extraInfo: String? = null,
        vararg destinations: String = emptyArray()
    ) = executor.execute {
        loggers.onSomeOrAll(destinations) { watch(msg, info, extraInfo) }
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
        val startTime = System.nanoTime()
        var current = metricMap[operationName]
        if (current == null) {
            current = ConcurrentHashMap()
            metricMap[operationName] = current
        }
        current[operationId] = startTime
        return operationId
    }

    /**
     * Cancels the timer for the operation.
     */
    @JvmStatic
    fun cancelTimedOperation(operationName: String, operationId: Int) {
        metricMap[operationName]?.remove(operationId)
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
        val stopTime = System.nanoTime()
        metricMap[operationName]?.remove(operationId)?.let { startTime ->
            val diff = stopTime - startTime
            loggers.onSomeOrAll(destinations) { metric(operationName, diff) }
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
        info: String? = null,
        extraInfo: String? = null,
        vararg destinations: String = emptyArray()
    ) = executor.execute {
        loggers.onSomeOrAll(destinations) { info(msg, info, extraInfo) }
    }

    /**
     * If the [executor] is an [ExecutorService], shut down immediately. Use
     * this call if the event logging [executor] is preventing clean shutdown.
     *
     * After this call, further logging will fail.
     */
    @JvmStatic
    fun signalShutdown() {
        if (executor is ExecutorService) {
            executor.shutdown()
        }
    }
}

/**
 * Utility method to safely concatenate log elements
 */
fun safeConcat(msg: String?, info: String?, extraInfo: String?): String {
    val sb = StringBuilder()
    if (msg != null) {
        sb.append(msg)
    }

    if (info != null) {
        sb.append("/")
        sb.append(info)
    }

    if (extraInfo != null) {
        sb.append("/")
        sb.append(extraInfo)
    }

    return sb.toString()
}