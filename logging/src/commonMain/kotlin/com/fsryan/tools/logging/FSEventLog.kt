package com.fsryan.tools.logging

import kotlinx.datetime.Clock
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.random.Random

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
 * Registration of [FSEventLogger] instances occurs via
 * [Java SPI](https://www.baeldung.com/java-spi).
 * The order that the [JvmAndroidFSEventLogActual] instances are invoked is the order in which
 * they're specified in the
 * `resources/META-INF/services/com.fsryan.tools.logging.FSDevMetricsLogger`
 * file.
 *
 * You are encouraged as an app developer to write your own extension functions
 * on this object that allow you to develop a contract for particular kinds of
 * events. Furthermore, adding extension functions will allow you to add a
 * measure of type safety that the API does not allow.
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
    internal val loggers: LinkedHashMap<String, FSEventLogger> = createAllEventLoggers()

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
    internal val timedOperationMap: MutableMap<String, MutableMap<Int, Pair<Long, Map<String, String>>>> = mutableMapOf()

    /**
     * Attributes are named values that are persisted throughout a session.
     * These values can be modified by either calling this method again with a
     * different value, by calling the [addAttrs] bulk addition method with the
     * same [attrName] as a key of the input map, or by calling
     * [incrementCountableAttr] for countable attrs.
     *
     * By passing in a specific value for [destinations], you can limit the
     * destination of the attr addition to one or more loggers.
     *
     * @see addAttrs
     * @see incrementCountableAttr
     */
    @JvmStatic
    @JvmOverloads
    fun addAttr(attrName: String, attrValue: String, vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { addAttr(attrName, attrValue)}
        }
    }

    /**
     * Because attributes are persisted throughout a session, instead of
     * changing the value of an attr, you may want to remove the attr entirely.
     * Do so by calling this function.
     *
     * @see addAttr
     * @see removeAttrs
     */
    @JvmStatic
    @JvmOverloads
    fun removeAttr(attrName: String, vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { removeAttr(attrName) }
        }
    }

    /**
     * Because attributes are persisted throughout a session, instead of
     * changing the value of an attr, you may want to remove the attr entirely.
     * Do so by calling this function.
     *
     * @see addAttr
     */
    @JvmStatic
    @JvmOverloads
    fun removeAttrs(attrNames: Iterable<String>, vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { removeAttrs(attrNames) }
        }
    }

    /**
     * This is the same as [addAttr], but in bulk. The keys of [attrs] are the
     * attr names, and their corresponding values are the attr values.
     *
     * By passing in a specific value for [destinations], you can limit the
     * destination of the attr additions to one or more loggers.
     *
     * @see addAttr
     * @see incrementCountableAttr
     */
    @JvmStatic
    @JvmOverloads
    fun addAttrs(attrs: Map<String, String>, vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { addAttrs(attrs) }
        }
    }

    /**
     * If your attr is countable (meaning that it is parsable to a Long), then
     * this method will increase the value by 1.
     *
     * By passing in a specific value for [destinations], you can limit the
     * destination of the attr increment to one or more loggers.
     *
     * @see addAttr
     * @see addAttrs
     */
    @JvmStatic
    @JvmOverloads
    fun incrementCountableAttr(attrName: String, vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { incrementAttrValue(attrName) }
        }
    }

    /**
     * Log an event with the name [eventName]. This log will take the
     * event-specific attributes within the [attrs] map. If you input a key in
     * this [attrs] map that collides with an attr that you have previously
     * added (via [addAttr], [addAttrs], or [incrementCountableAttr]), then the
     * ultimate behavior is defined by each registered [FSEventLogger]
     * implementation.
     *
     * By passing in a specific value for [destinations], you can limit the
     * destination of the event to one or more loggers.
     */
    @JvmStatic
    @JvmOverloads
    fun addEvent(eventName: String, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray()) {
        launch {
            activeLoggers().onSomeOrAll(destinations) { addEvent(eventName, attrs) }
        }
    }

    /**
     * Starts a timer for the operation [operationName] and returns the
     * [operationId] used along with the [operationName] to either
     * [cancelTimedOperation] or to [commitTimedOperation]. If you do not
     * supply the [operationId] yourself, then a randomly generated id will be
     * returned.
     *
     * You can optionally supply some [startAttrs] in order to capture some
     * context to be referenced when you later [commitTimedOperation]
     * (supposing) that you need to commit the timed operation at some point
     * where you may lose access.
     */
    @JvmStatic
    @JvmOverloads
    fun startTimedOperation(
        operationName: String,
        operationId: Int = Random.nextInt(),
        startAttrs: Map<String, String> = emptyMap()
    ): Int {
        val now = Clock.System.now()
        launch {
            val startTime = now.toEpochMilliseconds()
            var current = timedOperationMap[operationName]
            if (current == null) {
                current = mutableMapOf()
                timedOperationMap[operationName] = current
            }
            current[operationId] = Pair(startTime, startAttrs)
        }
        return operationId
    }

    /**
     * Cancels the timer for the operation.
     */
    @JvmStatic
    fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            timedOperationMap[operationName]?.remove(operationId)
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
        durationAttrName: String? = null,
        startTimeMillisAttrName: String? = null,
        endTimeMillisAttrName: String? = null,
        endAttrs: Map<String, String> = emptyMap(),
        vararg destinations: String = emptyArray()
    ) {
        val endTimeMillis = Clock.System.now()
        launch {
            timedOperationMap[operationName]?.remove(operationId)?.let { (startTimeMillis, startAttrs) ->
                activeLoggers().onSomeOrAll(destinations) {
                    sendTimedOperation(
                        operationName = operationName,
                        startTimeMillis = startTimeMillis,
                        endTimeMillis = endTimeMillis.toEpochMilliseconds(),
                        durationAttrName = durationAttrName,
                        startTimeMillisAttrName = startTimeMillisAttrName,
                        endTimeMillisAttrName = endTimeMillisAttrName,
                        startAttrs = startAttrs,
                        endAttrs = endAttrs
                    )
                }
            }
        }
    }

    fun testLogger(): FSEventLogger? {
        return loggers.asIterable()
            .firstOrNull { it.value.runInTestEnvironment() }
            ?.value
    }

    private fun activeLoggers() = when (loggingConfig.testEnv) {
        true -> loggers.supportingTestEnvironment()
        false -> loggers
    }
}

