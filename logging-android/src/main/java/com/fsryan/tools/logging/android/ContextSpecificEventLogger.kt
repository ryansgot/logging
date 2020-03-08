package com.fsryan.tools.logging.android

import android.content.Context
import com.fsryan.tools.logging.FSEventLogger

/**
 * An interface to enable Android application-specific initialization of
 * an event logger.
 */
interface ContextSpecificEventLogger : FSEventLogger {
    /**
     * Implementations should assume they will be called at or close to
     * `onCreate()` of the `Application`. In this function, implementations
     * should do any initialization. Also, you can look for the following
     * `string-array` values in order to know which attributes correspond to
     * `Long`, `Boolean`, or `Double` types:
     * - `fs_logging_long_properties`
     * - `fs_logging_boolean_properties`
     * - `fs_logging_double_properties`
     *
     * These arrays should tell you the types of some properties so that you
     * can log them as specific types.
     */
    fun initialize(context: Context)
}