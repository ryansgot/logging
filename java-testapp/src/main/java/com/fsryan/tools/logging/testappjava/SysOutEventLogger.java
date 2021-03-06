package com.fsryan.tools.logging.testappjava;

import com.fsryan.tools.logging.FSEventLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;

public class SysOutEventLogger implements FSEventLogger {

    @Override
    public String id() {
        return "sysout";
    }

    @Override
    public void addAttr(@Nonnull String attrName, @Nonnull String attrValue) {
        System.out.println("add attr " + attrName + "=" + attrValue);
    }

    @Override
    public void removeAttr(@Nonnull String attrName) {
        System.out.println("remove attr " + attrName);
    }

    @Override
    public void incrementAttrValue(@Nonnull String attrName) {
        System.out.println("incrementCountableAttr " + attrName);
    }

    @Override
    public void addEvent(@Nonnull String eventName, @Nonnull Map<String, String> attrs) {
        System.out.println("addEvent " + eventName + "; attrs = " + attrs);
    }

    @Override
    public boolean runInTestEnvironment() {
        return false;
    }

    @Override
    public void sendTimedOperation(
            @Nonnull String operationName,
            long startTimeMillis,
            long endTimeMillis,
            @Nullable String durationAttrName,
            @Nullable String startTimeMillisAttrName,
            @Nullable String endTimeMillisAttrName,
            @Nonnull Map<String, String> startAttrs,
            @Nonnull Map<String, String> endAttrs
    ) {
        System.out.println("timed operation " + operationName + "; startMillis = " + startTimeMillis + "; endMillis = " + endTimeMillis + "; startAttrs = " + startAttrs + "; endAttrs = " + endAttrs);
    }
}
