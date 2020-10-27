package com.fsryan.tools.logging.android

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * You configure the behavior of [InternalFileEventLogger] via your app's
 * AndroidManifest.xml file. You must include a `meta-data` tag as a child
 * of the `application` tag as below:
 * ```xml
 * <application>
 *   <meta-data android:name="internal_event_log" android:value="internal/log/path" />
 * </application>
 * ```
 *
 * If you do not provide this meta data, then the app will crash.
 *
 * When [initialize] is called, [InternalFileEventLogger] will create the
 * necessary directory and file and go ahead and log the current time at app
 * start to it.
 */
class InternalFileEventLogger : ContextSpecificEventLogger() {

    private lateinit var outputFile: File
    private val userProperties: MutableMap<String, String> = ConcurrentHashMap()

    override fun id(): String = "internalfile"

    override fun initialize(context: Context) = context.applicationContext.run {
        super.initialize(context)
        val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val path = info.metaData?.getString("internal_event_log", null) ?: throw IllegalStateException("application meta-data named internal_event_log required")
        outputFile = File(context.filesDir, path)
        val parentDir = checkNotNull(outputFile.parentFile)
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw IllegalStateException("Could not create parent directory for event log file: $outputFile")
        }
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw IllegalStateException("Could not create event log file: $outputFile")
        }

        outputFile.appendText("\n\n\nApp Initialized: ${Date()}\n\n\n")
    }

    override fun addAttr(attrName: String, attrValue: String) {
        userProperties[attrName] = attrValue
        super.addAttr(attrName, attrValue)
    }

    override fun removeAttr(attrName: String) {
        userProperties.remove(attrName)
        super.removeAttr(attrName)
    }

    override fun addEvent(eventName: String, attrs: Map<String, String>) {
        val actualAttrs = userProperties + attrs
        outputFile.appendText("\n${Date()} [EVENT] $eventName")
        actualAttrs.entries.forEach { outputFile.appendText("\n\t${it.key}=${it.value}") }
    }
}