package com.fsryan.tools.logging

import kotlinx.coroutines.CoroutineScope

internal expect fun launch(block: suspend CoroutineScope.() -> Unit)
internal expect fun cancelLoggingInternal()