package com.fsryan.tools.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.jvm.JvmStatic

internal val loggingConfig: FSLoggingConfig = createLoggingConfig("fslogging")
internal fun launch(block: CoroutineScope.() -> Unit) = loggingConfig.coroutineScope.launch(block = block)

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
    val coroutineScope: CoroutineScope
    val testEnv: Boolean
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

    /**
     * Whether this logger runs in a test environment.
     */
    fun runInTestEnvironment(): Boolean = false

    companion object {
        /**
         * Cancels all logging jobs. You can't undo this. Any subsequent logs
         * will be dropped.
         */
        @JvmStatic
        fun cancelLogging() {
            try {
                loggingConfig.coroutineScope.cancel()
            } catch (_: Exception) {

            }
        }
    }
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
     * not crashing the app. Add supplemental attributes via the [attrs]
     * parameter.
     */
    fun alarm(t: Throwable, attrs: Map<String, String> = emptyMap()) {}

    /**
     * Use to watch to monitor conditions where the severity is low enough that
     * you would want to specifically query your analytics system for these
     * kind of events. Add supplemental attributes via the [attrs]
     * parameter.
     */
    fun watch(msg: String, attrs: Map<String, String> = emptyMap()) {}

    /**
     * Use info to monitor events that you have some interest in, but are not
     * particularly worrisome as to warrant either a [watch] or an [alarm]. Add
     * supplemental attributes via the [attrs] parameter.
     */
    fun info(msg: String, attrs: Map<String, String> = emptyMap()) {}

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
     * Attrs are sent for every event. This function allows you to remove an
     * attr. Implementations are free to do what they wish if the attr does not
     * exist at the time of removal.
     */
    fun removeAttr(attrName: String)

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

    /**
     * Log a timed operation given the attrs
     */
    fun sendTimedOperation(
        operationName: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        durationAttrName: String? = null,
        startTimeMillisAttrName: String? = null,
        endTimeMillisAttrName: String? = null,
        startAttrs: Map<String, String> = emptyMap(),
        endAttrs: Map<String, String> = emptyMap()
    ) {
        launch {
            val durationAttrs = mutableMapOf<String, String>()
            durationAttrName?.let { durationAttrs[it] = (endTimeMillis - startTimeMillis).toString() }
            startTimeMillisAttrName?.let { durationAttrs[it] = startTimeMillis.toString() }
            endTimeMillisAttrName?.let { durationAttrs[it] = endTimeMillis.toString() }
            val attrs = startAttrs + endAttrs + durationAttrs
            addEvent(operationName, attrs)
        }
    }
}

/**
 * Add multiple attributes at one time in a map.
 */
fun FSEventLogger.addAttrs(attrs: Map<String, String>) {
    attrs.entries.forEach { addAttr(it.key, it.value) }
}

/**
 * Remove multiple attributes at one time.
 */
fun FSEventLogger.removeAttrs(attrNames: Iterable<String>) {
    attrNames.forEach { removeAttr(it) }
}

/**
 * Run on all when [keys] is empty and run on specific entries when [keys] is
 * nonempty
 *
 * does not crash when a key is not found
 */
internal inline fun <T> Map<String, T>.onSomeOrAll(keys: Array<out String>, block: T.() -> Unit) {
    when {
        keys.isEmpty() -> values.forEach { it.block() }
        else -> keys.forEach { key -> get(key)?.block() }
    }
}

internal fun <T:FSLogger> Map<String, T>.supportingTestEnvironment() = filterValues { it.runInTestEnvironment() }

internal expect fun createLoggingConfig(defaultLoggingThreadName: String): FSLoggingConfig
internal expect fun createAllDevMetricsLoggers(): LinkedHashMap<String, FSDevMetricsLogger>
internal expect fun createAllEventLoggers(): LinkedHashMap<String, FSEventLogger>