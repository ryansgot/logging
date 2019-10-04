package com.fsryan.tools.logging

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Register this via [Java SPI](https://www.baeldung.com/java-spi). This exists
 * mainly to provide a means of configuring tests
 */
interface FSLoggingConfig {
    fun createExecutor(): Executor
}

/**
 * Base interface for loggers. [FSDevMetrics] and [FSEventLog] look up their
 * associated loggers by their Ids. These lookups are set when [FSDevMetrics]
 * and [FSEventLog] classes are loaded (respectively). The mechanism for
 * registering a logger is the
 * [Java SPI](https://www.baeldung.com/java-spi).
 * @see FSDevMetricsLogger
 * @see FSEventLogger
 */
interface FSLogger {
    /**
     * How this particular [FSLogger] is located. If multiple loggers have
     * duplicate ids, then the last logger specified will overwrite the
     * previous loggers.
     */
    fun id(): String
}

/**
 * Implementations of [FSDevMetricsLogger] are intended to log events that have
 * specific developer benefit. If you need to log an event that should be
 * tracked by non-developers, such as screen views, then consider using
 * [FSEventLogger] instead. You can log to an [FSDevMetricsLogger] via
 * [FSDevMetrics].
 */
interface FSDevMetricsLogger : FSLogger {
    /**
     * An alarm is a condition that we believe should not occur. If it does
     * occur, then we want to be alerted.
     */
    fun alarm(t: Throwable) {}

    /**
     * Use to watch to monitor conditions where the severity is low enough that
     * we want to pull for updates as opposed to having them pushed to us.
     */
    fun watch(msg: String, info: String? = null, extraInfo: String? = null) {}

    /**
     * Something we would like to inform ourselves about, but that is not
     * severe.
     */
    fun info(msg: String, info: String? = null, extraInfo: String? = null) {}
}

/**
 * Implementations of [FSEventLogger] are intended to log events that track how
 * our users are using the application. If you need to log an event for
 * developer benefit, then consider logging to an [FSDevMetricsLogger] instead.
 * You can log to an [FSEventLogger] via [FSEventLog].
 */
interface FSEventLogger : FSLogger {
    /**
     * Add an attribute. Ostensibly, this will cause some effect when further
     * events are logged, this is up to the individual [FSEventLogger]
     */
    fun addAttr(attrName: String, attrValue: String)

    /**
     * Increment an attribute value. The attribute should be countable, but
     * since this is not guaranteed, implementations should gracefully handle
     * the case in which the attribute is not countable.
     */
    fun incrementAttrValue(attrName: String)

    /**
     * Log an event. The optional [attrs] parameter will add specific
     * attributes to the event.
     */
    fun addEvent(eventName: String, attrs: Map<String, String> = emptyMap())
}

/**
 * Add multiple attributes at one time in a map.
 */
fun FSEventLogger.addAttrs(attrs: Map<String, String>) = attrs.entries.forEach { addAttr(it.key, it.value) }

/**
 * Run on all when [keys] is empty and run on specific entries when [keys] is
 * nonempty
 *
 * does not crash when a key is not found
 */
internal fun <T> Map<String, T>.onSomeOrAll(keys: Array<out String>, block: T.() -> Unit) = when {
    keys.isEmpty() -> values.forEach { it.block() }
    else -> keys.forEach { key -> get(key)?.block() }
}

internal fun createDefaultConfig(threadNamePrefix: String) = object: FSLoggingConfig {
    override fun createExecutor(): Executor = Executors.newSingleThreadExecutor { r ->
        Executors.defaultThreadFactory().newThread(r).apply {
            name = "$threadNamePrefix-$name"
            priority = Thread.MIN_PRIORITY
        }
    }
}