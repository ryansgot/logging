package com.fsryan.tools.logging

import kotlin.jvm.JvmStatic

/**
 * Base interface for loggers. [FSDevMetrics] and [FSEventLog] look up their
 * associated loggers by their Ids. These lookups are set when [FSDevMetrics]
 * and [FSEventLog] classes are loaded (respectively).
 * @see FSDevMetricsLogger
 * @see FSEventLogger
 */
interface FSLogger {
    /**
     * How this particular [FSLogger] is identified. If multiple loggers have
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
            cancelLoggingInternal()
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
     * This function is for understanding when something has happened in the
     * app that may be of concern. Keep in mind that you'll likely want to
     * query whatever analytics backend you have given [msg] and [attrs]
     * arguments passed here. Add supplemental attributes via the [attrs]
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
     * Add an attribute to the analytics event context. The actual effect of
     * adding an attribute is specific to the implementation of [FSEventLogger]
     * receiving the attr. However, in general, an attr that is added via this
     * function is intended to be logged for every event, whereas, an event
     * attr (passed in on the [addEvent] function) is only relevant for that
     * specific event.
     * @param attrName The name of the attr to add
     * @param attrValue The string value of the attr to add
     */
    fun addAttr(attrName: String, attrValue: String)

    /**
     * Attrs are sent for every event. This function allows you to remove an
     * attr from the analytics event context. Implementations are free to do
     * what they wish if the attr does not exist at the time of removal.
     * @param attrName The name of the attr to remove
     */
    fun removeAttr(attrName: String)

    /**
     * Increment an attr value in the analytics event context. The attribute
     * should be countable, but since this is not guaranteed, implementations
     * should gracefully handle the case in which the attr is not countable.
     * @param attrName the name of the attr to increment
     */
    fun incrementAttrValue(attrName: String)

    /**
     * Log an event. The optional [attrs] parameter will add specific
     * attributes for this event only.
     * @param eventName the name of the event to add
     * @param attrs any extra attributes that should be added to this specific
     * event
     */
    fun addEvent(eventName: String, attrs: Map<String, String> = emptyMap())

    /**
     * Log a timed operation given the arguments passed in.
     * > Note: This is about user behavior--not about application performance.
     * > A possible use case would be to start a timed operation when the user
     * > views a screen, and then commit that timed operation when the user
     * > leaves a screen.
     * @param operationName The name of the operation--doubles as the event
     * name.
     * @param startTimeMillis The time the timed operation was started.
     * @param endTimeMillis The time the timed operation ended.
     * @param durationAttrName The name of the duration attr--will be ignored
     * if null
     * @param startTimeMillisAttrName The name of the start time millis attr--
     * will be ignored if null
     * @param endTimeMillisAttrName The name of the end timem millis attr--will
     * be ignored if null
     * @param startAttrs The attrs that were stored when the timed operation
     * was started
     * @param endAttrs The attrs that were added when the timed operation was
     * completed.
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
        val durationAttrs = mutableMapOf<String, String>()
        durationAttrName?.let { durationAttrs[it] = (endTimeMillis - startTimeMillis).toString() }
        startTimeMillisAttrName?.let { durationAttrs[it] = startTimeMillis.toString() }
        endTimeMillisAttrName?.let { durationAttrs[it] = endTimeMillis.toString() }
        val attrs = startAttrs + endAttrs + durationAttrs
        addEvent(operationName, attrs)
    }
}

/**
 * Add multiple attributes at one time in a map.
 * > Note: Do not call this on your own. It is NOT threadsafe.
 * @param attrs a map of attrName -> attrValue for all attributes to add
 */
fun FSEventLogger.addAttrs(attrs: Map<String, String>) {
    attrs.entries.forEach { addAttr(it.key, it.value) }
}

/**
 * Removes multiple attrs at one time.
 * > Note: Do not call this on your own. It is NOT threadsafe.
 * @param attrs an [Iterable] of the attr names to remove
 */
fun FSEventLogger.removeAttrs(attrNames: Iterable<String>) {
    attrNames.forEach { removeAttr(it) }
}

/**
 * Run on all when [keys] is empty and run on specific entries when [keys] is
 * nonempty
 * Threading: NOT thread safe
 *
 * does not crash when a key is not found
 * @see onSomeOrAll to access via isolate state
 */
internal inline fun <T:Any> Map<String, T>.onSomeOrAll(keys: Array<out String>, block: T.() -> Unit) {
    when {
        keys.isEmpty() -> values.forEach { it.block() }
        else -> keys.forEach { key -> get(key)?.block() }
    }
}

internal fun <T:FSLogger> Map<String, T>.supportingTestEnvironment() = filterValues { it.runInTestEnvironment() }

internal expect fun createInitialDevMetricsLoggers(): MutableMap<String, FSDevMetricsLogger>
internal expect fun createInitialEventLoggers(): MutableMap<String, FSEventLogger>