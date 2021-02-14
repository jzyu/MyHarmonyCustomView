package com.example.demo00.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class Log {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x01, "MyDemo");

    public static String replaceFormat(String logMessageFormat) {
        return logMessageFormat
                .replaceAll("%d", "%{public}d")
                .replaceAll("%f", "%{public}f")
                .replaceAll("%s", "%{public}s");
    }

    public static void debug(String tag, String format, Object... args) {
        HiLog.debug(label, tag + " " + replaceFormat(format), args);
    }
}
