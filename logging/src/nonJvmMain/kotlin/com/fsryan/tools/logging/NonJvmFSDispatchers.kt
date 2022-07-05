package com.fsryan.tools.logging

import com.autodesk.coroutineworker.CoroutineWorker
import kotlinx.coroutines.CoroutineScope

internal actual fun launch(block: suspend CoroutineScope.() -> Unit) {
    CoroutineWorker.execute {
        block()
    }
}
internal actual fun cancelLoggingInternal() {
    //TODO
}