package com.fsryan.tools.logging.testappjava;

import com.fsryan.tools.logging.FSDevMetrics;
import com.fsryan.tools.logging.FSEventLog;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        int opId = FSDevMetrics.startTimedOperation("full_run");
        FSDevMetrics.alarm(new Exception("Some exception"));
        FSDevMetrics.watch("watch msg", "watch info", "watch extra info");
        FSDevMetrics.info("info msg", "info info", "info extra info");
        FSDevMetrics.signalShutdown();
        FSEventLog.addAttr("attr name", "attr val");
        FSEventLog.addEvent("event name", new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }});
        FSEventLog.incrementCountableAttr("attr to increment");
        FSDevMetrics.commitTimedOperation("full_run", opId);
        FSEventLog.signalShutdown();
    }
}
