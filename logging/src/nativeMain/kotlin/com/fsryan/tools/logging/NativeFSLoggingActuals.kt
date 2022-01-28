package com.fsryan.tools.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal actual fun createLoggingConfig(defaultLoggingThreadName: String): FSLoggingConfig {
    return object: FSLoggingConfig {
        override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
        override val testEnv: Boolean = false
    }
}

internal actual fun createAllDevMetricsLoggers(): LinkedHashMap<String, FSDevMetricsLogger> {
    // TODO: we have to create a means of loading loggers through service locator
    return LinkedHashMap()
}
internal actual fun createAllEventLoggers(): LinkedHashMap<String, FSEventLogger> {
    // TODO: we have to create a means of loading loggers through service locator
    return LinkedHashMap()
}