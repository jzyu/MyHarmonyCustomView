package com.example.demo00.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class Log {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x01, "MyDemo");

    /**
     * From: width=%d, height=%f, text=%s
     * To  : width=%{public}d, height=%{public}f, text=%{public}s
     * 不支持类似 %02d 这样的补位
     */
    public static String replaceFormat(String logMessageFormat) {
        return logMessageFormat.replaceAll("%([d|f|s])", "%{public}$1");
    }

    public static void debug(String tag, String format, Object... args) {
        HiLog.debug(label, tag + " " + replaceFormat(format), args);
    }
    public static void info(String tag, String format, Object... args) {
        HiLog.info(label, tag + " " + replaceFormat(format), args);
    }
    public static void warn(String tag, String format, Object... args) {
        HiLog.warn(label, tag + " " + replaceFormat(format), args);
    }
    public static void error(String tag, String format, Object... args) {
        HiLog.error(label, tag + " " + replaceFormat(format), args);
    }
}
