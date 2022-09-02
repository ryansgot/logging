package com.fsryan.tools.logging

import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * A single place to perform event logging. This will distribute the log event
 * or attr to the destination of your choice. When you use any of the methods,
 * you must either choose the destinations of the event/attribute or allow the
 * event/attribute to go to all known destinations.
 * If you choose to specify the destination, then either one or zero
 * [FSEventLogger] instances will be invoked (depending upon whether there is a
 * match. If you do not specify the destination, then the event will be sent to
 * _all_ registered [FSEventLogger] instances will be invoked.
 *
 * Registration of [FSEventLogger] instances occurs via [addLogger]. On
 * Android/JVM, you can also register instances via [Java SPI]
 * [Java SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html).
 * The order that the [FSEventLogger] instances are invoked is the order
 * in which they're specified in the
 * `resources/META-INF/services/com.fsryan.tools.logging.FSEventLogger`
 * file.
 *
 * Threading: this is a thread-safe object. Call this object's functions on any
 * thread. The logging task will be distributed immediately to the logging
 * thread and not block the calling thread.
 *
 * Your implementations of [FSEventLogger] will only ever be accessed by these
 * functions on that logging thread. However, if you want to do any external
 * configuration, then you must handle threading for your [FSEventLogger]
 * classes.
 */
expect object FSEventLog {

    /**
     * adds a logger to the configured set of [FSEventLogger] instances.
     * > NOTE: If you add a logger that has the same id as another logger
     * instance, this logger will OVERWRITE the previously-configured logger.
     * @param logger the [FSEventLogger] to register with [FSEventLog]
     */
    fun addLogger(logger: FSEventLogger)

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
     * @param attrName the attrName of the attr to remove.
     * @param attrValue the string value of the attr to add.
     * @param destinations the ids of the [FSEventLogger]s to which the attr
     * should be added. If you don't specify, all registered [FSEventLogger]
     * instances will be invoked.
     * @see addAttrs
     * @see incrementCountableAttr
     */
    fun addAttr(attrName: String, attrValue: String, vararg destinations: String = emptyArray())

    /**
     * Because attributes are persisted throughout a session, instead of
     * changing the value of an attr, you may want to remove the attr entirely.
     * Do so by calling this function.
     *
     * @param attrName the attrName of the attr to remove.
     * @param destinations the ids of the [FSEventLogger]s from which to
     * remove the attr. If you don't specify, all registered [FSEventLogger]
     * instances will be invoked.
     * @see addAttr
     * @see removeAttrs
     */
    fun removeAttr(attrName: String, vararg destinations: String = emptyArray())

    /**
     * Because attributes are persisted throughout a session, instead of
     * changing the value of an attr, you may want to remove the attr entirely.
     * Do so by calling this function.
     *
     * @param attrNames the attrNames of all attrs to remove.
     * @param destinations the ids of the [FSEventLogger]s from which to
     * remove the attrs. If you don't specify, all registered [FSEventLogger]
     * instances will be invoked.
     * @see addAttr
     * @see removeAttr
     */
    fun removeAttrs(attrNames: Iterable<String>, vararg destinations: String = emptyArray())

    /**
     * This is the same as [addAttr], but in bulk. The keys of [attrs] are the
     * attr names, and their corresponding values are the attr values.
     *
     * By passing in a specific value for [destinations], you can limit the
     * destination of the attr additions to one or more loggers.
     *
     * @param attrs the attrName -> attrValue map of all attrs to add.
     * @param destinations the ids of the [FSEventLogger]s to which to add the
     * attrs. If you don't specify, all registered [FSEventLogger] instances
     * will be invoked.
     * @see addAttr
     * @see incrementCountableAttr
     */
    fun addAttrs(attrs: Map<String, String>, vararg destinations: String = emptyArray())

    /**
     * If your attr is countable (meaning that it is parsable to a Long), then
     * this method will increase the value by 1.
     *
     * By passing in a specific value for [destinations], you can limit the
     * destination of the attr increment to one or more loggers.
     * @param attrName the name of the attr to increment
     * @param destinations the ids of the [FSEventLogger]s to which to
     * increment the attr value. If you don't specify, all registered
     * [FSEventLogger] instances will be invoked.
     * @see addAttr
     * @see addAttrs
     */
    fun incrementCountableAttr(attrName: String, vararg destinations: String = emptyArray())

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
     * @param eventName the name of the event to log
     * @param attrs any additional attrs that are specific to this event
     * @param destinations the ids of the [FSEventLogger]s to which to send
     * this event. If you don't specify, all registered [FSEventLogger]
     * instances will be invoked.
     */
    fun addEvent(eventName: String, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray())

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
     * @param operationName The name of the operation you're timing.
     * @param operationId The id of the specific operation you're timing. This
     * allows for multiple concurrent operations with the same name to be
     * timed.
     * @param startAttrs The attrs that you want to capture for later logging
     * when [commitTimedOperation] is called.
     * @return the operationId. Use the operationId as a means of committing or
     * canceling this timed operation
     * @see commitTimedOperation
     * @see cancelTimedOperation
     */
    fun startTimedOperation(
        operationName: String,
        operationId: Int = Random.nextInt(),
        startAttrs: Map<String, String> = emptyMap()
    ): Int

    /**
     * Cancels the timer for the operation.
     * @param operationName The name of the operation you're timing.
     * @param operationId The id of the specific operation you're timing
     */
    fun cancelTimedOperation(operationName: String, operationId: Int)


    /**
     * Commits the timed operation with the name [operationName] and id
     * [operationId] input to the [destinations] (or all destinations if none
     * specified).
     * @param operationName The name of the operation you're timing.
     * @param operationId The id of the specific operation you're timing
     * @param durationAttrName the name of the duration attr--ignored if null
     * @param startTimeMillisAttrName the name of the start time millis attr--
     * ignored if null
     * @param endTimeMillisAttrName the name of the end time millis attr--
     * ignored if null
     * @param endAttrs the attrs that you want to add when committing the timed
     * operation.
     * @param destinations the ids of the [FSEventLogger]s to which to end this
     * timed operation. If you don't specify, all registered [FSEventLogger]
     * instances will be invoked.
     */
    fun commitTimedOperation(
        operationName: String,
        operationId: Int,
        durationAttrName: String? = null,
        startTimeMillisAttrName: String? = null,
        endTimeMillisAttrName: String? = null,
        endAttrs: Map<String, String> = emptyMap(),
        vararg destinations: String = emptyArray()
    )

    /**
     * Enables post-instantiation configuration of [FSEventLogger] instances of
     * a type the class [T]. Ideally, this function is called very early in the
     * application lifecycle.
     *
     * > NOTE: The [perform] function will be confined to the same thread on
     * which logging occurs.
     *
     * > NOTE: On non-JVM platforms, you should be careful to not pass
     * references to mutable state in the [perform] function, as updates to
     * that same mutable state elsewhere could cause a crash. If you need to
     * capture a reference in the [perform] function, your best bet is to
     * create a deep copy of that reference that is _LOCAL_ to your current
     * function. If deep copying is not practical, then compute all values that
     * you will need to capture in the [perform] function ahead of time and
     * capture the computed values instead.
     * @param cls The [KClass] of the loggers on which you want to [perform] an
     * operation
     * @param perform the operation you want to perform on all logers of type
     * [cls]
     */
    fun <T: FSEventLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit)
}

