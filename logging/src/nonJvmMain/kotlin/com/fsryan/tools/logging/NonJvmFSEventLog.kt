package com.fsryan.tools.logging

import co.touchlab.stately.isolate.IsolateState
import kotlinx.datetime.Clock
import kotlin.reflect.KClass

actual object FSEventLog {
    /**
     * Threading: Accessed via isolated state--threadsafe
     *
     * Visibility: visible for inner access (avoids synthetic accessor)
     */
    internal val state = IsolateState {
        FSLoggerMutableValues(loggers = createInitialEventLoggers())
    }

    actual fun addLogger(logger: FSEventLogger) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers[logger.id()] = logger
            }
        }
    }

    actual fun addAttr(attrName: String, attrValue: String, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    addAttr(attrName, attrValue)
                }
            }
        }
    }

    actual fun removeAttr(attrName: String, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    removeAttr(attrName)
                }
            }
        }
    }

    actual fun removeAttrs(attrNames: Iterable<String>, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    removeAttrs(attrNames)
                }
            }
        }
    }

    actual fun addAttrs(attrs: Map<String, String>, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    addAttrs(attrs)
                }
            }
        }
    }

    actual fun incrementCountableAttr(attrName: String, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    incrementAttrValue(attrName)
                }
            }
        }
    }

    actual fun addEvent(eventName: String, attrs: Map<String, String>, vararg destinations: String) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.onSomeOrAll(destinations) {
                    addEvent(eventName, attrs)
                }
            }
        }
    }

    actual fun startTimedOperation(
        operationName: String,
        operationId: Int,
        startAttrs: Map<String, String>
    ): Int {
        // creating the instant prior to switching threads allows for more
        // accurate understanding of "now"
        val now = Clock.System.now()
        // TODO: check out whether the thread handoff is going to be a blocking
        //  call. We need to avoid waiting on the state.access function to complete
        launch {
            state.access { mutableValues ->
                mutableValues.metrics.add(
                    opName = operationName,
                    opId = operationId,
                    metric = FSTimedOpData(
                        startTime = now.toEpochMilliseconds(),
                        startAttrs = startAttrs
                    )
                )

            }
        }
        return operationId
    }

    actual fun cancelTimedOperation(operationName: String, operationId: Int) {
        launch {
            state.access { mutableValues ->
                mutableValues.metrics.consumeOperationMetric(operationName, operationId)
            }
        }
    }

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
            state.access { mutableValues ->
                mutableValues.metrics.consumeOperationMetric(operationName, operationId)
                    ?.let { metric ->
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
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T: FSEventLogger> onLoggersOfType(cls: KClass<T>, perform: T.() -> Unit) {
        launch {
            state.access { mutableValues ->
                mutableValues.loggers.values.forEach { logger ->
                    if (cls.isInstance(logger)) {
                        (logger as T).perform()
                    }
                }
            }
        }
    }
}