package com.fsryan.tools.logging

import kotlinx.coroutines.CoroutineScope

//TODO: make block a suspend fun
internal expect fun launch(block: suspend CoroutineScope.() -> Unit)
internal expect fun cancelLoggingInternal()