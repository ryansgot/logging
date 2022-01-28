package com.fsryan.tools.logging.test

internal fun fail(message: String) {
    throw AssertionError(message)
}