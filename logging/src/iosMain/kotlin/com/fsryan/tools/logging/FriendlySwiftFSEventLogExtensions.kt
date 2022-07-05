package com.fsryan.tools.logging


fun FSEventLog.addAttr(attrName: String, attrValue: String, destinations: List<String>) {
    addAttr(attrName = attrName, attrValue = attrValue, destinations = destinations.toTypedArray())
}
fun FSEventLog.addAttr(attrName: String, attrValue: String) {
    addAttr(attrName = attrName, attrValue = attrValue, destinations = emptyArray())
}

fun FSEventLog.removeAttrs(attrNames: Iterable<String>, destinations: List<String>) {
    removeAttrs(attrNames = attrNames, destinations = destinations.toTypedArray())
}
fun FSEventLog.removeAttrs(attrNames: Iterable<String>) {
    removeAttrs(attrNames = attrNames, destinations = emptyArray())
}

fun FSEventLog.removeAttr(attrName: String, destinations: List<String>) {
    removeAttr(attrName = attrName, destinations = destinations.toTypedArray())
}
fun FSEventLog.removeAttr(attrName: String) {
    removeAttr(attrName = attrName, destinations = emptyArray())
}

fun FSEventLog.addAttrs(attrs: Map<String, String>, destinations: List<String>) {
    addAttrs(attrs = attrs, destinations = destinations.toTypedArray())
}
fun FSEventLog.addAttrs(attrs: Map<String, String>) {
    addAttrs(attrs = attrs, destinations = emptyArray())
}

fun FSEventLog.incrementCountableAttr(attrName: String, destinations: List<String>) {
    incrementCountableAttr(attrName = attrName, destinations = destinations.toTypedArray())
}
fun FSEventLog.incrementCountableAttr(attrName: String) {
    incrementCountableAttr(attrName = attrName, destinations = emptyArray())
}

fun FSEventLog.addEvent(eventName: String, attrs: Map<String, String>, destinations: List<String>) {
    addEvent(eventName = eventName, attrs = attrs, destinations = destinations.toTypedArray())
}
fun FSEventLog.addEvent(eventName: String, attrs: Map<String, String>) {
    addEvent(eventName = eventName, attrs = attrs, destinations = emptyArray())
}

fun FSEventLog.commitTimedOperation(
    operationName: String,
    operationId: Int,
    durationAttrName: String? = null,
    startTimeMillisAttrName: String? = null,
    endTimeMillisAttrName: String? = null,
    endAttrs: Map<String, String> = emptyMap(),
    destinations: List<String>
) {
    commitTimedOperation(
        operationName = operationName,
        operationId = operationId,
        durationAttrName = durationAttrName,
        startTimeMillisAttrName = startTimeMillisAttrName,
        endTimeMillisAttrName = endTimeMillisAttrName,
        endAttrs = endAttrs,
        destinations = destinations.toTypedArray()
    )
}

fun FSEventLog.commitTimedOperation(
    operationName: String,
    operationId: Int,
    durationAttrName: String? = null,
    startTimeMillisAttrName: String? = null,
    endTimeMillisAttrName: String? = null,
    endAttrs: Map<String, String> = emptyMap()
) {
    commitTimedOperation(
        operationName = operationName,
        operationId = operationId,
        durationAttrName = durationAttrName,
        startTimeMillisAttrName = startTimeMillisAttrName,
        endTimeMillisAttrName = endTimeMillisAttrName,
        endAttrs = endAttrs,
        destinations = emptyArray()
    )
}