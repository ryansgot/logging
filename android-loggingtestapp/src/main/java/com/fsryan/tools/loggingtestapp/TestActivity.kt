package com.fsryan.tools.loggingtestapp

import android.os.Bundle
import android.view.View
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog
import com.fsryan.tools.loggingtestapp.databinding.ActivityTestBinding
import com.fsryan.tools.loggingtestapp.databinding.AddAttrDialogBinding
import com.fsryan.tools.loggingtestapp.databinding.RemoveAttrDialogBinding
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    var addAttrDialog: AlertDialog? = null
    var removeAttrDialog: AlertDialog? = null
    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crashButton.setOnClickListener {
            throw RuntimeException("TEST CRASH")
        }
        binding.alarmButton.setOnClickListener {
            FSDevMetrics.alarm(Exception("This is an exception"))
        }
        binding.watchButton.setOnClickListener {
            FSDevMetrics.watch(
                msg = "watch message",
                attrs = mapOf("info" to "watch info", "extraInfo" to "watch extra info")
            )
        }
        binding.infoButton.setOnClickListener {
            FSDevMetrics.info(
                msg = "info message",
                attrs = mapOf("info" to "info info", "extraInfo" to "info extra info")
            )
        }
        binding.addAttrButton.setOnClickListener {
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
        binding.removeAttrButton.setOnClickListener {
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
        binding.logAnalyticsButton.setOnClickListener {
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
        binding.timedOperationButton.setOnClickListener(object: View.OnClickListener {

            private var opId = -1

            override fun onClick(v: View?) {
                if (binding.timedOperationButton.text.toString() == getString(R.string.start_timed_operation)) {
                    binding.timedOperationButton.setText(R.string.commit_timed_operation)
                    opId = FSDevMetrics.startTimedOperation("example timed operation")
                } else {
                    FSDevMetrics.commitTimedOperation(operationName = "example timed operation", operationId = opId)
                    binding.timedOperationButton.setText(R.string.start_timed_operation)
                }
            }

        })
    }

    private fun getOrCreateAddAttrDialog() = addAttrDialog ?: AlertDialog.Builder(this).let { builder ->
        val addAttrBinding = AddAttrDialogBinding.inflate(layoutInflater)
        builder.setTitle("ADD ATTR")
        .setView(addAttrBinding.root)
        .setPositiveButton("SET") { dialog, _ ->
            val attrName = addAttrBinding.addAttrNameEntry.text.toString()
            val attrValue = addAttrBinding.addAttrValueEntry.text.toString()
            FSEventLog.addAttr(attrName, attrValue)
        }.setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
        .create()
        .also { addAttrDialog = it }
    }


    private fun getOrCreateRemveAttrDialog() = removeAttrDialog ?: AlertDialog.Builder(this).let { builder ->
        val removeAttrBinding = RemoveAttrDialogBinding.inflate(layoutInflater)
        builder.setTitle("REMOVE ATTR")
        .setView(removeAttrBinding.root)
        .setPositiveButton("REMOVE") { dialog, _ ->
            val attrName = removeAttrBinding.removeAttrNameEntry.text.toString()
            FSEventLog.removeAttr(attrName)
        }.setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
        .create()
        .also { removeAttrDialog = it }
    }

    private fun randomStringArrayValue(@ArrayRes id: Int): String = resources.getStringArray(id).random()
}