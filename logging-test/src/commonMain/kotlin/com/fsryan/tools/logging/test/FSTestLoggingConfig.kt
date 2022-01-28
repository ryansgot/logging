package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSLoggingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FSTestLoggingConfig : FSLoggingConfig {
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    override val testEnv: Boolean = true
}