package com.fsryan.tools.logging

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Register this via [Java SPI](https://www.baeldung.com/java-spi). This exists
 * mainly to provide a means of configuring tests, however, you should consider
 * using it if the thread on which logging occurs is particularly important to
 * you.
 *
 * If you do not supply your own [FSLoggingConfig] via SPI, then you will get a
 * unique [Executors.newSingleThreadExecutor] for each of [FSDevMetrics] and
 * [FSEventLog] to use.
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
     * An alarm is a condition that you believe should not occur. If it does
     * occur, then using this function declares your intent to be alerted while
     * not crashing the app.
     */
    fun alarm(t: Throwable) {}

    /**
     * Use to watch to monitor conditions where the severity is low enough that
     * you would want to specifically query your analytics system for these
     * kind of events.
     */
    fun watch(msg: String, info: String? = null, extraInfo: String? = null) {}

    /**
     * Use info to monitor events that you have some interest in, but are not
     * particularly worrisome as to warrant either a [watch] or an [alarm]
     */
    fun info(msg: String, info: String? = null, extraInfo: String? = null) {}

    /**
     * Use to measure the performance characteristics of an operation. The
     * [FSDevMetrics] object has a handy set of functions that you should call
     * with respect to metrics: [FSDevMetrics.startTimedOperation],
     * [FSDevMetrics.commitTimedOperation], and
     * [FSDevMetrics.cancelTimedOperation] are relevant to note (so don't call
     * this function yourself).
     */
    fun metric(operationName: String, durationNanos: Long) {}
}

/**
 * Implementations of [FSEventLogger] are intended to log events that track how
 * our users are using the application. If you need to log an event for
 * developer benefit, then consider logging to an [FSDevMetricsLogger] instead.
 * You can log to an [FSEventLogger] via [FSEventLog].
 */
interface FSEventLogger : FSLogger {
    /**
     * Add an attribute. The actual effect of adding an attribute is specific
     * to the implementaion of [FSEventLogger] that is receiving the attr.
     * However, in general, an attr is intended to be logged for every event,
     * whereas, an event attr (passed in on the [addEvent] function) is only
     * relevant for that specific event.
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