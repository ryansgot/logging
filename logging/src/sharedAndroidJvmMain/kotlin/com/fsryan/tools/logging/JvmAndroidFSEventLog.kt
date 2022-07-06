package com.fsryan.tools.logging

import kotlinx.datetime.Clock
import kotlin.reflect.KClass

actual object FSEventLog {

    internal val mutableValues = FSLoggerMutableValues(loggers = createInitialEventLoggers())

    @JvmStatic
    actual fun addLogger(logger: FSEventLogger) {
        launch {
            mutableValues.loggers[logger.id()] = logger
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun addAttr(attrName: String, attrValue: String, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                addAttr(attrName, attrValue)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun removeAttr(attrName: String, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                removeAttr(attrName)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun removeAttrs(attrNames: Iterable<String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                removeAttrs(attrNames)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun addAttrs(attrs: Map<String, String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                addAttrs(attrs)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun incrementCountableAttr(attrName: String, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                incrementAttrValue(attrName)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    actual fun addEvent(eventName: String, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            mutableValues.loggers.onSomeOrAll(destinations) {
                addEvent(eventName, attrs)
            }
        }
    }

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

    @JvmStatic
    actual fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            mutableValues.metrics.consumeOperationMetric(operationName, operationId)
        }
    }

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