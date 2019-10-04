@file:JvmName("DevMetricsUtil")

package com.fsryan.tools.logging

import java.util.ServiceLoader
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import kotlin.collections.LinkedHashMap

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

    init {
        val loader = ServiceLoader.load(FSDevMetricsLogger::class.java)
        loader.forEach { loggers[it.id()] = it }

        if (loggers.isEmpty()) {
            println("WARNING: no FSDevMetrics found")
        }

        val config = ServiceLoader.load(FSLoggingConfig::class.java).firstOrNull() ?: createDefaultConfig("FSDevMetrics")
        executor = config.createExecutor()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : FSDevMetricsLogger> loggersOfType(cls: Class<T>): List<T> = loggers.values
        .filter { cls.isAssignableFrom(it.javaClass) }
        .map { it as T }

    /**
     * Either sends the alarm specifically to the [dest] when supplied, or
     * sends the info to all registered [loggers] when [dest] not supplied
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