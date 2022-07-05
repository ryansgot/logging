package com.fsryan.tools.logging

import kotlinx.datetime.Clock
import kotlin.reflect.KClass

actual object FSEventLog {
    /**
     * Threading: Accessed via isolated state--threadsafe
     *
     * Visibility: visible for inner access (avoids synthetic accessor)
     */
    internal val mutableValues = FSLoggerMutableValues(loggers = createInitialEventLoggers())

    @JvmStatic
    actual fun addLogger(logger: FSEventLogger) {
        launch {
            mutableValues.loggers[logger.id()] = logger
        }
    }

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
    actual fun addAttr(attrName: String, attrValue: String, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                addAttr(attrName, attrValue)
            }
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
    actual fun removeAttr(attrName: String, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                removeAttr(attrName)
            }
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
    actual fun removeAttrs(attrNames: Iterable<String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                removeAttrs(attrNames)
            }
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
    actual fun addAttrs(attrs: Map<String, String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                addAttrs(attrs)
            }
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
    actual fun incrementCountableAttr(attrName: String, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                incrementAttrValue(attrName)
            }
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
    actual fun addEvent(eventName: String, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                addEvent(eventName, attrs)
            }
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
    actual fun startTimedOperation(
        operationName: String,
        operationId: Int,
        startAttrs: Map<String, String>
    ): Int {
        // creating the instant prior to switching threads allows for more
        // accurate understanding of "now"
        val now = Clock.System.now()
        launch {
            mutableValues.metrics.add(
                opName = operationName,
                opId = operationId,
                metric = FSTimedOpData(
                    startTime = now.toEpochMilliseconds(),
                    startAttrs = startAttrs
                )
            )
        }
        return operationId
    }

    /**
     * Cancels the timer for the operation.
     */
    @JvmStatic
    actual fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            mutableValues.metrics.consumeOperationMetric(operationName, operationId)
        }
    }

    /**
     * Commits the timed operation with the name [operationName] and id
     * [operationId] input to the [destinations] (or all destinations if none
     * specified).
     */
    @JvmStatic
    @JvmOverloads
    actual fun commitTimedOperation(
        operationName: String,
        operationId: Int,
        durationAttrName: String?,
        startTimeMillisAttrName: String?,
        endTimeMillisAttrName: String?,
        endAttrs: Map<String, String>,
        vararg destinations: String
    ) {
        val endTimeMillis = Clock.System.now()
        launch {
            mutableValues.metrics.consumeOperationMetric(operationName, operationId)?.let { metric ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    sendTimedOperation(
                        operationName = operationName,
                        startTimeMillis = metric.startTime,
                        endTimeMillis = endTimeMillis.toEpochMilliseconds(),
                        durationAttrName = durationAttrName,
                        startTimeMillisAttrName = startTimeMillisAttrName,
                        endTimeMillisAttrName = endTimeMillisAttrName,
                        startAttrs = metric.startAttrs,
                        endAttrs = endAttrs
                    )
                }
            }
        }
    }

    /**
     * Enables post-instantiation configuration of [FSEventLogger] instances of a
     * type the class [T]. Ideally, this function is called very early in the
     * application's lifecycle.
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    actual fun <T: FSEventLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit) {
        launch {
            mutableValues.loggers.values.forEach { logger ->
                if (cls.isInstance(logger)) {
                    (logger as T).perform()
                }
            }
        }
    }
}