package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSLoggingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val testLoggingScope: CoroutineScope = CoroutineScope(SupervisorJob())

class FSTestLoggingConfig : FSLoggingConfig {
    override val coroutineScope: CoroutineScope = testLoggingScope
    override val testEnv: Boolean = true
}