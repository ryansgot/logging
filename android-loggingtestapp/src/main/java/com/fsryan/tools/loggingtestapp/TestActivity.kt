package com.fsryan.tools.loggingtestapp

import android.os.Bundle
import android.view.View
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.add_attr_dialog.*
import kotlinx.android.synthetic.main.remove_attr_dialog.*
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    var addAttrDialog: AlertDialog? = null
    var removeAttrDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        crashButton.setOnClickListener {
            throw RuntimeException("TEST CRASH")
        }
        alarmButton.setOnClickListener {
            FSDevMetrics.alarm(Exception("This is an exception"))
        }
        watchButton.setOnClickListener {
            FSDevMetrics.watch(
                msg = "watch message",
                info = "watch info",
                extraInfo = "watch extra info"
            )
        }
        infoButton.setOnClickListener {
            FSDevMetrics.info(
                msg = "info message",
                info = "info info",
                extraInfo = "info extra info"
            )
        }
        addAttrButton.setOnClickListener {
            getOrCreateRemveAttrDialog().run {
                if (isShowing) {
                    dismiss()
                }
            }
            getOrCreateAddAttrDialog().run {
                if (isShowing) {
                    dismiss()
                }
                show()
            }
        }
        removeAttrButton.setOnClickListener {
            getOrCreateAddAttrDialog().run {
                if (isShowing) {
                    dismiss()
                }
            }
            getOrCreateRemveAttrDialog().run {
                if (isShowing) {
                    dismiss()
                }
                show()
            }
        }
        logAnalyticsButton.setOnClickListener {
            FSEventLog.addEvent(
                eventName = "example_add_event",
                attrs = mapOf(
                    "key1" to "val1",
                    randomStringArrayValue(R.array.fs_logging_double_properties) to Random.nextDouble().toString(),
                    randomStringArrayValue(R.array.fs_logging_long_properties) to Random.nextLong().toString(),
                    randomStringArrayValue(R.array.fs_logging_boolean_properties) to Random.nextBoolean().toString()
                )
            )
        }
        timedOperationButton.setOnClickListener(object: View.OnClickListener {

            private var opId = -1

            override fun onClick(v: View?) {
                if (timedOperationButton.text.toString() == getString(R.string.start_timed_operation)) {
                    timedOperationButton.setText(R.string.commit_timed_operation)
                    opId = FSDevMetrics.startTimedOperation("example timed operation")
                } else {
                    FSDevMetrics.commitTimedOperation(operationName = "example timed operation", operationId = opId)
                    timedOperationButton.setText(R.string.start_timed_operation)
                }
            }

        })
    }

    private fun getOrCreateAddAttrDialog() = addAttrDialog ?: AlertDialog.Builder(this)
        .setTitle("ADD ATTR")
        .setView(R.layout.add_attr_dialog)
        .setPositiveButton("SET") { dialog, _ ->
            val attrName = (dialog as AlertDialog).addAttrNameEntry.text.toString()
            val attrValue = dialog.addAttrValueEntry.text.toString()
            FSEventLog.addAttr(attrName, attrValue)
        }.setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
        .create()
        .also { addAttrDialog = it }

    private fun getOrCreateRemveAttrDialog() = removeAttrDialog ?: AlertDialog.Builder(this)
        .setTitle("REMOVE ATTR")
        .setView(R.layout.remove_attr_dialog)
        .setPositiveButton("REMOVE") { dialog, _ ->
            val attrName = (dialog as AlertDialog).removeAttrNameEntry.text.toString()
            FSEventLog.removeAttr(attrName)
        }.setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
        .create()
        .also { removeAttrDialog = it }

    private fun randomStringArrayValue(@ArrayRes id: Int): String = resources.getStringArray(id).random()
}