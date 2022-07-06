package com.fsryan.tools.logging

import java.util.ServiceLoader
import kotlin.collections.LinkedHashMap

internal actual fun createInitialDevMetricsLoggers(): MutableMap<String, FSDevMetricsLogger> {
    return loadInstancesOf("FSDevMetricsLoggers")
}
internal actual fun createInitialEventLoggers(): MutableMap<String, FSEventLogger> {
    return loadInstancesOf("FSEventLoggers")
}

private inline fun <reified T: FSLogger> loadInstancesOf(type: String): MutableMap<String, T> {
    val loader = ServiceLoader.load(T::class.java)
    val loggers = loader?.filterNotNull()?.toList() ?: emptyList()
    if (loggers.isEmpty()) {
        println("WARNING: no configured $type")
    }
    return loggers.associateBy { it.id() }.toMutableMap()
}