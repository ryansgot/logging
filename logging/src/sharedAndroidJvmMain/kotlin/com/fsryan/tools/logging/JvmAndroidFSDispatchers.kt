package com.fsryan.tools.logging

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
private val logDispatcher: CoroutineContext = newSingleThreadContext("fslogging")
internal val logScope = CoroutineScope(SupervisorJob() + logDispatcher)

internal actual fun launch(block: suspend CoroutineScope.() -> Unit) {
    logScope.launch(block = block)
}
internal actual fun cancelLoggingInternal() {
    logScope.cancel()
}
