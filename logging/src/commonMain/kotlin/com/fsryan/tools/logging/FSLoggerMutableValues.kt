package com.fsryan.tools.logging

import kotlin.jvm.JvmInline

internal data class FSLoggerMutableValues<L:FSLogger>(
    val loggers: MutableMap<String, L> = mutableMapOf(),
    val metrics: FSMetricsContext = FSMetricsContext()
)

internal interface FSMetricsContext {
    /**
     * Overwrites any preexisting metric for the [opName] and [opId]
     */
    fun add(opName: String, opId: Int, metric: FSTimedOpData)

    /**
     * Consumes the [FSTimedOpData] associated with the [opName] and [opId]
     * before passing it back, if it exists.
     *
     * @return the [FSTimedOpData] that existed for the [opName] and [opId], `null`
     * if no [FSTimedOpData] existed for the [opName] and [opId].
     */
    fun consumeOperationMetric(opName: String, opId: Int): FSTimedOpData?
}

internal fun FSMetricsContext(): FSMetricsContext = MapBackedFSMetricsContext(mutableMapOf())

/**
 * Metrics are always two-stage analytics. The first stage is to capture some
 * state concerning the metric. [startTime] is always captured. Any other data
 * must be captured in the [startAttrs]
 */
internal data class FSTimedOpData(
    /**
     * Typically in millis, the start time captures the UTC epoch time.
     */
    val startTime: Long,

    /**
     * The state that was captured to hold about the metric to be logged.
     */
    val startAttrs: Map<String, String>
)


@JvmInline
internal value class MapBackedFSMetricsContext(
    private val backing: MutableMap<String, MutableMap<Int, FSTimedOpData>>
): FSMetricsContext {
    override fun add(opName: String, opId: Int, metric: FSTimedOpData) {
        val current = backing.getOrPut(opName) { mutableMapOf() }
        current[opId] = metric
    }

    override fun consumeOperationMetric(opName: String, opId: Int): FSTimedOpData? {
        val currentOps = backing[opName] ?: return null
        val current = currentOps.remove(opId)
        if (currentOps.isEmpty()) {
            // cleanup of map when there are no operations so we don't have
            // unnecessary objects sitting around.
            backing.remove(opName)
        }
        return current
    }
}