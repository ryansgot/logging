package com.fsryan.tools.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import java.util.ServiceLoader
import kotlin.collections.LinkedHashMap

internal actual fun createLoggingConfig(defaultLoggingThreadName: String): FSLoggingConfig {
    val loader = ServiceLoader.load(FSLoggingConfig::class.java)
    val configs = loader?.filterNotNull()?.toList() ?: emptyList()
    return when (configs.isEmpty()) {
        true -> object: FSLoggingConfig {
            override val coroutineScope: CoroutineScope = CoroutineScope(newSingleThreadContext(defaultLoggingThreadName))
            override val testEnv: Boolean = false
        }
        false -> configs.last()
    }
}

internal actual fun createAllDevMetricsLoggers(): LinkedHashMap<String, FSDevMetricsLogger> {
    return loadInstancesOf("FSDevMetricsLoggers")
}
internal actual fun createAllEventLoggers(): LinkedHashMap<String, FSEventLogger> {
    return loadInstancesOf("FSEventLoggers")
}

private inline fun <reified T: FSLogger> loadInstancesOf(type: String): LinkedHashMap<String, T> {
    val loader = ServiceLoader.load(T::class.java)
    val loggers = loader?.filterNotNull()?.toList() ?: emptyList()
    if (loggers.isEmpty()) {
        println("WARNING: no configured $type")
    }

    val ret = LinkedHashMap<String, T>()
    loggers.forEach { ret[it.id()] = it }
    return ret
}