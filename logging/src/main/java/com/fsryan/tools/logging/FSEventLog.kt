package com.fsryan.tools.logging

import java.util.ServiceLoader
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

/**
 * A single place to perform event logging. This will distribute the log event
 * or attr to the destination of your choice. When you use any of the methods,
 * you must either:
 * # Choose the dest(ination) of the event/attribute or
 * # Allow the event/attribute to go to all known destinations
 * If you choose to specify the destination, then either one or zero
 * [FSEventLogger] instances will be invoked (depending upon whether there is a
 * match. IF you do not specify the destination, then the event will be sent to
 * _all_ registered [FSEventLogger] instances will be invoked.
 *
 * Registration of [FSEventLog] instances occurs via
 * [Java SPI](https://www.baeldung.com/java-spi).
 * The order that the [FSEventLog] instances are invoked is the order in which
 * they're specified in the
 * `resources/META-INF/services/com.fsryan.tools.logging.FSDevMetricsLogger`
 * file.
 *
 * Threading: any thread
 * Note that each method call will distribute the work to the executor you have
 * created in your implementation of [FSLoggingConfig]. If you do not supply an
 * [FSLoggingConfig], then the work will be distributed to a single-threaded
 * [ExecutorService] created by this library for handling all [FSEventLog]
 * operations.
 */
object FSEventLog {

    /**
     * Threading: writes only during object initialization. Reads from logging
     * executor. Therefore the list is effectively immutable wrt readers.
     *
     * Visibility: visible for inner access (avoids synthetic accessor)
     */
    internal val loggers: LinkedHashMap<String, FSEventLogger> = LinkedHashMap()
    private val executor: Executor

    init {
        val loader = ServiceLoader.load(FSEventLogger::class.java)
        loader.forEach { loggers[it.id()] = it }

        if (loggers.isEmpty()) {
            println("WARNING: no FSEventLoggers found")
        }

        val config = ServiceLoader.load(FSLoggingConfig::class.java).firstOrNull() ?: createDefaultConfig("FSEventLog")
        executor = config.createExecutor()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : FSEventLogger> loggersOfType(cls: Class<T>): List<T> = loggers.values
        .filter { cls.isAssignableFrom(it.javaClass) }
        .map { it as T }

    @JvmStatic
    @JvmOverloads
    fun addAttr(attrName: String, attrValue: String, vararg destinations: String = emptyArray()) = executor.execute {
        loggers.onSomeOrAll(destinations) { addAttr(attrName, attrValue)}
    }

    @JvmStatic
    @JvmOverloads
    fun addAttrs(attrs: Map<String, String>, vararg destinations: String = emptyArray()) = executor.execute {
        loggers.onSomeOrAll(destinations) { addAttrs(attrs) }
    }

    @JvmStatic
    @JvmOverloads
    fun incrementAttrValue(attrName: String, vararg destinations: String = emptyArray()) = executor.execute {
        loggers.onSomeOrAll(destinations) { incrementAttrValue(attrName) }
    }

    @JvmStatic
    @JvmOverloads
    fun addEvent(eventName: String, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray()) = executor.execute {
        loggers.onSomeOrAll(destinations) { addEvent(eventName, attrs) }
    }

    @JvmStatic
    fun signalShutdown() {
        if (executor is ExecutorService) {
            executor.shutdown()
        }
    }
}

