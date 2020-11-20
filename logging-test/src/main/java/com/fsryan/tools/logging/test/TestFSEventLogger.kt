package com.fsryan.tools.logging.test

import com.fsryan.tools.logging.FSEventLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A Basic event logger that captures the events and attrs added to be logged.
 * Notably, the added attrs are not logged along with the event, so you do not
 * need to make assertions based upon the stored attributes in addition to the
 * added events.
 */
class TestFSEventLogger: FSEventLogger {

    private val storedAttrs = ConcurrentHashMap<String, String>()
    private val sentEvents = ConcurrentHashMap<String, CopyOnWriteArrayList<Map<String, String>>>()
    private val timesAttrIncremented = ConcurrentHashMap<String, Int>()

    override fun id(): String = "__test_event_logger"
    override fun runInTestEnvironment(): Boolean = true

    override fun addAttr(attrName: String, attrValue: String) {
        storedAttrs[attrName] = attrValue
    }

    override fun removeAttr(attrName: String) {
        storedAttrs.remove(attrName)
    }

    override fun incrementAttrValue(attrName: String) {
        synchronized(timesAttrIncremented) {
            var current = timesAttrIncremented[attrName] ?: 0
            timesAttrIncremented[attrName] = current + 1
        }
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        synchronized(this) {
            val current = sentEvents.getOrPut(eventName) { CopyOnWriteArrayList<Map<String, String>>() }
            current.add(attrs)
        }
    }

    internal fun reset() {
        synchronized(this) {
            storedAttrs.clear()
            sentEvents.clear()
            timesAttrIncremented.clear()
        }
    }

    internal fun timesAttrValueIncremented(attrName: String): Int = timesAttrIncremented[attrName] ?: 0
    internal fun storedAttrs(): Map<String, String> = storedAttrs.toMap()
    internal fun sentEvents(eventName: String): List<Map<String, String>> = sentEvents[eventName]?.toList() ?: emptyList()
}