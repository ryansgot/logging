package com.fsryan.tools.loggingtestapp.appcenter

import android.os.Bundle
import android.view.View
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AppCompatActivity
import com.fsryan.tools.logging.FSDevMetrics
import com.fsryan.tools.logging.FSEventLog
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.android.synthetic.main.activity_test.*
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        crashButton.setOnClickListener {
            Crashes.generateTestCrash()
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
        logAnalyticsButton.setOnClickListener {
            FSEventLog.addEvent(
                eventName = "example_add_event",
                attrs = mapOf(
                    "key1" to "val1",
                    randomStringArrayValue(R.array.fs_appcenter_double_properties) to Random.nextDouble().toString(),
                    randomStringArrayValue(R.array.fs_appcenter_long_properties) to Random.nextLong().toString(),
                    randomStringArrayValue(R.array.fs_appcenter_boolean_properties) to Random.nextBoolean().toString()
                )
            )
        }
        timedOperationButton.setOnClickListener(object: View.OnClickListener {

            private var opId = -1

            override fun onClick(v: View?) {
                if (timedOperationButton.text.toString() == "Start Timed Operation") {
                    timedOperationButton.text = "Commit Timed Operation"
                    opId = FSDevMetrics.startTimedOperation("example timed operation")
                } else {
                    FSDevMetrics.commitTimedOperation(operationName = "example timed operation", operationId = opId)
                    timedOperationButton.text = "Start Timed Operation"
                }
            }

        })
    }

    private fun randomStringArrayValue(@ArrayRes id: Int): String = resources.getStringArray(id).random()
}