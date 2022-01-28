package com.fsryan.tools.logging

/**
 * Retrieve all extensions of [FSDevMetricsLogger] that are assignable from
 * the class [T]. You may want to use this to affect some required
 * configuration of some underlying logger. Ideally, this function is
 * called very early in the application's lifecycle.
 */
@Suppress("UNCHECKED_CAST")
fun <T: FSDevMetricsLogger> FSDevMetrics.loggersOfType(cls: Class<T>): List<T> {
    return loggers.values
        .filter { cls.isAssignableFrom(it.javaClass) }
        .map { it as T }
}

/**
 * Retrieve all extensions of [FSEventLogger] that are assignable from the
 * class [T]. You may want to use this to affect some required
 * configuration of some underlying logger. Ideally, this function is
 * called very early in the application's lifecycle.
 */
@Suppress("UNCHECKED_CAST")
fun <T : FSEventLogger> FSEventLog.loggersOfType(cls: Class<T>): List<T> {
    return loggers.values
        .filter { cls.isAssignableFrom(it.javaClass) }
        .map { it as T }
}