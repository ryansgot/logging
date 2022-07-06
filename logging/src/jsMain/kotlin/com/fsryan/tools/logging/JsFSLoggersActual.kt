package com.fsryan.tools.logging

internal actual fun createInitialDevMetricsLoggers(): MutableMap<String, FSDevMetricsLogger> {
    // TODO: we have to create a means of loading loggers through service locator
    return mutableMapOf()
}
internal actual fun createInitialEventLoggers(): MutableMap<String, FSEventLogger> {
    // TODO: we have to create a means of loading loggers through service locator
    return mutableMapOf()
}