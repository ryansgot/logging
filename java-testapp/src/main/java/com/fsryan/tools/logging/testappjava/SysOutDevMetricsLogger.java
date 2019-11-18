package com.fsryan.tools.logging.testappjava;

import com.fsryan.tools.logging.DevMetricsUtil;
import com.fsryan.tools.logging.FSDevMetricsLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SysOutDevMetricsLogger implements FSDevMetricsLogger {
    @Nonnull
    @Override
    public String id() {
        return "sysout";
    }

    @Override
    public void alarm(@Nonnull Throwable t) {
        System.out.println("[FS/ALARM]: " + t);
    }

    @Override
    public void watch(@Nonnull String msg, @Nullable String info, @Nullable String extraInfo) {
        System.out.println("[FS/WATCH]: " + DevMetricsUtil.safeConcat(msg, info, extraInfo));
    }

    @Override
    public void info(@Nonnull String msg, @Nullable String info, @Nullable String extraInfo) {
        System.out.println("[FS/INFO]: " + DevMetricsUtil.safeConcat(msg, info, extraInfo));
    }

    @Override
    public void metric(@Nonnull String operationName, long durationNanos) {
        System.out.println("[FS/METRIC]: '" + operationName + "' took " + durationNanos + " nanos");
    }
}
