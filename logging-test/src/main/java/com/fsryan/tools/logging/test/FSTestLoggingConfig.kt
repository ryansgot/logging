package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSLoggingConfig
import java.util.concurrent.Executor

class FSTestLoggingConfig : FSLoggingConfig {
    override fun createExecutor() = Executor { r -> r.run() }
    override fun isTestEnvironment(): Boolean = true
}