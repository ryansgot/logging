package com.fsryan.tools.logging.testappjava;

import com.fsryan.tools.logging.FSDevMetricsLogger;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SysOutDevMetricsLogger implements FSDevMetricsLogger {
    
    @Nonnull
    @Override
    public String id() {
        return "sysout";
    }

    @Override
    public void alarm(@Nonnull Throwable t, @Nonnull Map<String, String> attrs) {
        System.out.println("[FS/ALARM]: " + t + "; attrs = " + attrs);
    }

    @Override
    public void watch(@Nonnull String msg, @Nullable String info, @Nullable String extraInfo, @Nonnull Map<String, String> attrs) {
        System.out.println("[FS/WATCH]: " + "msg = " + msg + "; attrs = " + combine(info, extraInfo, attrs));
    }

    @Override
    public void info(@Nonnull String msg, @Nullable String info, @Nullable String extraInfo, @Nonnull Map<String, String> attrs) {
        System.out.println("[FS/INFO]: " + "msg = " + msg + "; attrs = " + combine(info, extraInfo, attrs));
    }

    @Override
    public void metric(@Nonnull String operationName, long durationNanos) {
        System.out.println("[FS/METRIC]: '" + operationName + "' took " + durationNanos + " nanos");
    }

    private String combine(@Nullable String info, @Nullable String extraInfo, @Nonnull Map<String, String> attrs) {
        Map<String, String> ret = new HashMap<>(attrs);
        ret.put("info", info == null ? "" : info);
        ret.put("extraInfo", extraInfo == null ? "" : extraInfo);
        return ret.toString();
    }

    @Override
    public boolean runInTestEnvironment() {
        return false;
    }
}
