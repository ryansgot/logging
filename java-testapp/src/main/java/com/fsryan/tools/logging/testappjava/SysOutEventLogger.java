package com.fsryan.tools.logging.testappjava;

import com.fsryan.tools.logging.FSEventLogger;

import javax.annotation.Nonnull;
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
}
