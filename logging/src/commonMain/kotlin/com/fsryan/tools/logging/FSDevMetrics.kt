package com.fsryan.tools.logging

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.random.Random
import kotlin.reflect.KClass

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
expect object FSDevMetrics {

    /**
     * adds a logger to the configured set of [FSDevMetricsLogger] instances.
     * > NOTE: If you add a logger that has the same id as another logger
     * instance, this logger will OVERWRITE the previously-configured logger.
     */
    fun addLogger(logger: FSDevMetricsLogger)

    /**
     * Either sends the alarm specifically to the [destinations] when supplied,
     * or sends the info to all registered [state] when [destinations] not
     * supplied. Add supplemental attributes via the [attrs] parameter.
     * @see [FSDevMetricsLogger.alarm]
     */
    @JvmStatic
    @JvmOverloads
    fun alarm(t: Throwable, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray())

    /**
     * Either sends the watch specifically to the [destinations] when supplied,
     * or sends the watch to all registered [state] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.watch]
     */
    @JvmStatic
    @JvmOverloads
    fun watch(msg: String, attrs: Map<String, String> = emptyMap(), vararg destinations: String = emptyArray())

    /**
     * Starts a timer for the operation [operationName] and returns the
     * [operationId] used along with the [operationName] to either
     * [cancelTimedOperation] or to [commitTimedOperation]. If you do not
     * supply the [operationId] yourself, then a randomly generated id will be
     * returned.
     */
    @JvmStatic
    @JvmOverloads
    fun startTimedOperation(operationName: String, operationId: Int = Random.nextInt()): Int

    /**
     * Cancels the timer for the operation.
     */
    @JvmStatic
    fun cancelTimedOperation(operationName: String, operationId: Int)

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
    )

    /**
     * Either sends the info specifically to the [destinations] when supplied,
     * or sends the info to all registered [state] when [destinations] not
     * supplied
     * @see [FSDevMetricsLogger.info]
     */
    @JvmStatic
    @JvmOverloads
    fun info(
        msg: String,
        attrs: Map<String, String> = emptyMap(),
        vararg destinations: String = emptyArray()
    )

    /**
     * Enables post-instantiation configuration of [FSDevMetricsLogger] instances
     * of a type the class [T]. Ideally, this function is called very early in the
     * application's lifecycle.
     *
     * > NOTE: The [perform] function will be confined to the same thread on
     * which logging occurs
     *
     * > NOTE: On non-JVM platforms, you should be careful to not pass
     * references to mutable state in the [perform] function, as updates to
     * that same mutable state elsewhere could cause a crash. If you need to
     * capture a reference in the [perform] function, your best bet is to
     * create a deep copy of that reference. If deep copying is not practical,
     * then compute all values that you will need to capture in the [perform]
     * function ahead of time.
     */
    fun <T: FSDevMetricsLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit)
}