package com.fsryan.tools.logging

import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * The single place you need to use in order to log developer-centric events.
 * This object does not do any logging on its own, rather, it distributes the
 * task of logging to the registered implementations of [FSDevMetricsLogger].
 *
 * Registration of [FSDevMetricsLogger] instances occurs via [addLogger]. On
 * Android/JVM, you can also register instances via [Java SPI]
 * [Java SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html).
 * The order that the [FSDevMetricsLogger] instances are invoked is the order
 * in which they're specified in the
 * `resources/META-INF/services/com.fsryan.tools.logging.FSDevMetricsLogger`
 * file.
 *
 * Threading: this is a thread-safe object. Call this object's functions on any
 * thread. The logging task will be distributed immediately to the logging
 * thread and not block the calling thread.
 *
 * Your implementations of [FSDevMetricsLogger] will only ever be accessed by
 * these functions on that logging thread. However, if you want to do any
 * external configuration, then you must handle threading for your
 * [FSDevMetricsLogger] classes.
 */
expect object FSDevMetrics {

    /**
     * Adds a logger to the configured set of [FSDevMetricsLogger] instances.
     * > NOTE: If you add a logger that has the same id as another logger
     * instance, this logger will _OVERWRITE_ the previously-configured logger.
     * @param logger the [FSDevMetricsLogger] to register with [FSDevMetrics]
     */
    fun addLogger(logger: FSDevMetricsLogger)

    /**
     * Either sends the alarm specifically to the [destinations] when supplied,
     * or sends the info to all registered [FSDevMetricsLogger] instances when
     * the [destinations] argument is not supplied. Add supplemental attributes
     * via the [attrs] parameter.
     *
     * When a [Throwable] is thrown, you may suspect that your app could never
     * access that code. It's ideal to add an alarm in that case so that you
     * can find out when it happens and track down the root cause.
     * @param t the [Throwable] instance to alarm on.
     * @param attrs any additional data to add to the alarm
     * @param destinations the ids of the [FSDevMetricsLogger]s to which to
     * send this alarm. If you don't specify, all registered
     * [FSDevMetricsLogger] instances will be invoked.
     * @see [FSDevMetricsLogger.alarm]
     */
    fun alarm(t: Throwable, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray())

    /**
     * Either sends the watch specifically to the [destinations] when supplied,
     * or sends the watch to all registered [FSDevMetricsLogger]s when
     * [destinations] not supplied.
     * @param msg The message that describes what you were watching for
     * @param info An outdated parameter that probably shouldn't be used, adds
     * a key to the output [attrs] called `"info"` with the value passed in
     * @param extraInfo An outdated parameter that probably shouldn't be used,
     * adds a key to the output [attrs] called `"info"` with the value passed
     * in
     * @param attrs additional attrs to log with this watch.
     * @param destinations the ids of the [FSDevMetricsLogger]s to which to
     * send this watch. If you don't specify, all registered
     * [FSDevMetricsLogger] instances will be invoked.
     */
    fun watch(
        msg: String,
        info: String? = null,
        extraInfo: String? = null,
        attrs: Map<String, String> = emptyMap(),
        vararg destinations: String = emptyArray()
    )

    /**
     * Starts a timer for the operation [operationName] and returns the
     * [operationId] used along with the [operationName] to either
     * [cancelTimedOperation] or to [commitTimedOperation]. If you do not
     * supply the [operationId] yourself, then a randomly generated id will be
     * returned.
     * @param operationName The name of the operation you're timing.
     * @param operationId The id of the specific operation you're timing. This
     * allows for multiple concurrent operations with the same name to be
     * timed.
     * @return the operationId. Use the operationId as a means of committing or
     * canceling this timed operation
     * @see commitTimedOperation
     * @see cancelTimedOperation
     */
    fun startTimedOperation(operationName: String, operationId: Int = Random.nextInt()): Int

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
     * @param destinations the ids of the [FSDevMetricsLogger]s to which to
     * send this timed operation. If you don't specify, all registered
     * [FSDevMetricsLogger] instances will be invoked.
     */
    fun commitTimedOperation(
        operationName: String,
        operationId: Int,
        vararg destinations: String = emptyArray()
    )

    /**
     * Either sends the info log specifically to the [destinations] when
     * supplied, or sends the info to all registered [FSDevMetricsLogger]s when
     * [destinations] not supplied
     * @param msg The message that describes the info
     * @param info An outdated parameter that probably shouldn't be used, adds
     * a key to the output [attrs] called `"info"` with the value passed in
     * @param extraInfo An outdated parameter that probably shouldn't be used,
     * adds a key to the output [attrs] called `"info"` with the value passed
     * in
     * @param attrs additional attrs to log with this watch.
     * @param destinations the ids of the [FSDevMetricsLogger]s to which to
     * send this watch. If you don't specify, all registered
     * [FSDevMetricsLogger] instances will be invoked.
     */
    fun info(
        msg: String,
        info: String? = null,
        extraInfo: String? = null,
        attrs: Map<String, String> = emptyMap(),
        vararg destinations: String = emptyArray()
    )

    /**
     * Enables post-instantiation configuration of [FSDevMetricsLogger]
     * instances of a type the class [T]. Ideally, this function is called very
     * early in the application lifecycle.
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
    fun <T: FSDevMetricsLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit)
}

internal fun combineLegacyInfosWithAttrs(attrs: Map<String, String>, info: String?, extraInfo: String?): Map<String, String> {
    return when (info) {
        null -> when (extraInfo) {
            null -> attrs
            else -> attrs.plus("extraInfo" to extraInfo)
        }
        else -> when (extraInfo) {
            null -> attrs.plus("info" to info)
            else -> attrs.plus(arrayOf("info" to info, "extraInfo" to extraInfo))
        }
    }
}