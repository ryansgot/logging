package com.fsryan.tools.logging.test

import co.touchlab.stately.isolate.IsolateState
import com.fsryan.tools.logging.FSEventLogger

/**
 * A Basic event logger that captures the events and attrs added to be logged.
 * Notably, the added attrs are not logged along with the event, so you do not
 * need to make assertions based upon the stored attributes in addition to the
 * added events.
 */
object TestFSEventLogger: FSEventLogger {

    internal data class State(
        val storedAttrs: MutableMap<String, String> = mutableMapOf(),
        val sentEvents: MutableList<TestSentEvent> = mutableListOf(),
        val timesAttrIncremented: MutableMap<String, Int> = mutableMapOf()
    ) {
        fun clear() {
            storedAttrs.clear()
            sentEvents.clear()
            timesAttrIncremented.clear()
        }
    }

    private val state = IsolateState { State() }

    override fun id(): String = "__test_event_logger"
    override fun runInTestEnvironment(): Boolean = true

    override fun addAttr(attrName: String, attrValue: String) {
        state.access {
            it.storedAttrs[attrValue] = attrValue
        }
    }

    override fun removeAttr(attrName: String) {
        state.access {
            it.storedAttrs.remove(attrName)
        }
    }

    override fun incrementAttrValue(attrName: String) {
        state.access {
            val current = it.timesAttrIncremented[attrName] ?: 0
            it.timesAttrIncremented[attrName] = current + 1
        }
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        state.access {
            it.sentEvents.add(TestSentEvent(name = eventName, attrs = attrs))
        }
    }

    internal fun reset() {
        state.access {
            it.clear()
        }
    }

    internal fun timesAttrValueIncremented(attrName: String): Int = state.access {
        it.timesAttrIncremented[attrName] ?: 0
    }
    internal fun storedAttrs(): Map<String, String> = state.access {
        it.storedAttrs.toMap()
    }
    internal fun sentEvents(): List<TestSentEvent> = state.access {
        it.sentEvents.toList()
    }
}

/**
 * A means of specifying the data on a sent event.
 */
data class TestSentEvent(val name: String, val attrs: Map<String, String>)