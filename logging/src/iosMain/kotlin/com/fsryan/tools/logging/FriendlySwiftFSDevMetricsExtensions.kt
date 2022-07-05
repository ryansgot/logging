package com.fsryan.tools.logging

fun FSDevMetrics.alarm(t: Throwable, attrs: Map<String, String>, destinations: List<String>) {
    alarm(t = t, attrs = attrs, destinations = destinations.toTypedArray())
}
fun FSDevMetrics.alarm(t: Throwable, attrs: Map<String, String>) {
    alarm(t = t, attrs = attrs, destinations = emptyArray())
}

fun FSDevMetrics.watch(msg: String, attrs: Map<String, String>, destinations: List<String>) {
    watch(msg = msg, attrs = attrs, destinations = destinations.toTypedArray())
}
fun FSDevMetrics.watch(msg: String, attrs: Map<String, String>) {
    watch(msg = msg, attrs = attrs, destinations = emptyArray())
}

fun FSDevMetrics.commitTimedOperation(
    operationName: String,
    operationId: Int,
    destinations: List<String>
) {
    commitTimedOperation(
        operationName = operationName,
        operationId = operationId,
        destinations = destinations.toTypedArray()
    )
}

fun FSDevMetrics.commitTimedOperation(operationName: String, operationId: Int) {
    commitTimedOperation(
        operationName = operationName,
        operationId = operationId,
        destinations = emptyArray()
    )
}

fun FSDevMetrics.info(msg: String, attrs: Map<String, String>, destinations: List<String>) {
    info(msg = msg, attrs = attrs, destinations = destinations.toTypedArray())
}
fun FSDevMetrics.info(msg: String, attrs: Map<String, String>) {
    info(msg = msg, attrs = attrs, destinations = emptyArray())
}