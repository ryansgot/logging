package com.fsryan.tools.logging.testappjava;

import com.fsryan.tools.logging.FSDevMetrics;
import com.fsryan.tools.logging.FSEventLog;
import com.fsryan.tools.logging.FSLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.emptyMap;

public class Main {

    public static void main(String[] args) {
        int opId = FSDevMetrics.startTimedOperation("full_run", ThreadLocalRandom.current().nextInt());
        FSDevMetrics.alarm(new Exception("Some exception"), emptyMap());
        FSDevMetrics.watch("watch msg", "watch info", "watch extra info", emptyMap());
        FSDevMetrics.info("info msg", "info info", "info extra info", emptyMap());
        FSEventLog.addAttr("attr name", "attr val");
        FSEventLog.addEvent("event name", new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }});
        FSEventLog.incrementCountableAttr("attr to increment");
        FSDevMetrics.commitTimedOperation("full_run", opId);
        FSLogger.cancelLogging();
    }
}
