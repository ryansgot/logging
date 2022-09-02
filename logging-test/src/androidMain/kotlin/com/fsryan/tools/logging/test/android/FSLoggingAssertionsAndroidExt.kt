@file:JvmName("AndroidFSLoggingAssertions")

package com.fsryan.tools.logging.test.android

import android.os.Handler
import android.os.Looper
import com.fsryan.tools.logging.test.FSLoggingAssertions
import com.fsryan.tools.logging.test.TestSentEvent
import java.util.concurrent.CountDownLatch

/**
 * Asserts that the attr with name after dispatching the request to the main
 * thread and then waits on the response.
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param attrName the name of the attribute for which you're checking the
 * increment count
 * @param expected the expected number of times the attribute was
 * incremented
 */
fun FSLoggingAssertions.assertTimesIncrementedCountableAttrOnMainThread(
    attrName: String,
    expected: Int
) = runBlockingOnMainThread {
    assertTimesIncrementedCountableAttr(attrName, expected)
}

/**
 * Asserts that all of the expected attrs are added to the logging
 * context. There are two modes of this assertion: strict and non-strict.
 * This function assumes non-strict by default.
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param expectedAttrs the attrs you expect to be added to the logging
 * context
 * @param strict whether the [expectedAttrs] are the only attrs that should
 * be added to the logging context, defaults to `false`
 */
@JvmOverloads
fun FSLoggingAssertions.assertAllAttrsAddedOnMainThread(
    expectedAttrs: Map<String, String>,
    strict: Boolean = false
) = runBlockingOnMainThread {
    assertAllAttrsAdded(expectedAttrs, strict)
}

/**
 * Asserts that an attr was added to the logging context.
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param attrName the name of the attr you're expecting to have been added
 * @param expectedValue the expected value of the attr you're expecting to
 * have been added.
 */
fun FSLoggingAssertions.assertAttrAddedOnMainThread(attrName: String, expectedValue: String) {
    runBlockingOnMainThread {
        assertAttrAdded(attrName, expectedValue)
    }
}

/**
 * Asserts analytics for an event are sent in sequence. This checks that
 * both the event names and attributes are in the exact order that you sent
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param expectedSequence the [TestSentEvent]s that you're expecting to be
 * sent in the order you're expecting the events to be sent
 * @param strictAttrs whether the attr matching must be strict, defaults to
 * false.
 * @param strict whether the [expectedSequence] are the only events that
 * may be logged. Defaults to false, allowing additional events.
 */
@JvmOverloads
fun FSLoggingAssertions.assertAnalyticSequenceForEventOnMainThread(
    expectedSequence: List<TestSentEvent>,
    strictAttrs: Boolean = false,
    strict: Boolean = false
) = runBlockingOnMainThread {
    assertAnalyticSequenceForEvent(expectedSequence, strictAttrs, strict)
}

/**
 * Asserts that no analytics were sent matching an event name
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param eventName the event name that you are expecting not to be sent.
 */
fun FSLoggingAssertions.assertNoAnalyticsSentWithNameOnMainThread(eventName: String) {
    runBlockingOnMainThread {
        assertNoAnalyticsSentWithName(eventName)
    }
}

/**
 * Asserts that [expectedCount] analytics were sent having event name
 * [eventName].
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param eventName the event name that you are expecting not to be sent.
 * @param expectedCount the expected count of events having that name
 */
fun FSLoggingAssertions.assertAnalyticCountSentWithNameOnMainThread(
    eventName: String,
    expectedCount: Int
) = runBlockingOnMainThread {
    assertAnalyticCountSentWithName(eventName, expectedCount)
}

/**
 * Asserts that the analytic with the [eventName] and [expectedAttributes]
 * was sent.
 *
 * You will want to use this when the code that is doing logging performs the
 * logging due to some asynchronous rendering on the main thread or posting of
 * a message to the main looper. This function _WILL_ block the current thread
 * and do the thread handoff if the calling thread is not the main thread.
 *
 * @param eventName The expected event name
 * @param expectedAttributes the expected attributes that must be on the
 * event with name [eventName]
 * @param strict when true, this will fail if there is not an exact match
 * [expectedAttributes] for the [eventName]. Defaults to `false`
 */
@JvmOverloads
fun FSLoggingAssertions.assertAnalyticSentOnMainThread(
    eventName: String,
    expectedAttributes: Map<String, String>,
    strict: Boolean = false
) = runBlockingOnMainThread {
    assertAnalyticSent(eventName, expectedAttributes, strict)
}

private fun Handler.defer(func: () -> Unit): CountDownLatch {
    val mutex = CountDownLatch(1)
    post {
        func()
        mutex.countDown()
    }
    return mutex
}

private val handler = Handler(Looper.getMainLooper())

private fun runBlockingOnMainThread(func: () -> Unit) {
    if (Looper.getMainLooper().thread == Thread.currentThread()) {
        func()
    } else {
        handler.defer(func).await()
    }
}