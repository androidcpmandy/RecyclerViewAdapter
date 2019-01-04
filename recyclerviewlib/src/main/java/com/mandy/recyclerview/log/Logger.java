package com.mandy.recyclerview.log;

import android.text.TextUtils;
import android.util.Log;

public class Logger {
    private final static int ERROR = 0;
    private final static int WARN = 1;
    private final static int INFO = 2;
    private final static int VERBOSE = 3;

    private final static String CLASS_NAME = Logger.class.getCanonicalName();
    private final static String TAG = "mandy";
    private static int logLevel = ERROR;
    //    private static boolean isDebug = BuildConfig.DEBUG;
    private static boolean isDebug;
    private static String logTag = TAG;
    private static StringBuilder stringBuilder = new StringBuilder();

    public static void setDebuggable(boolean debuggable) {
        isDebug = debuggable;
    }

    public static void init(boolean debug, String tag, int level) {
        isDebug = debug;
        logTag = tag;
        logLevel = level;
    }

    public static void log(String msg) {
        log("", msg);
    }

    public static void log(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        stringBuilder.setLength(0);
        String tempTag;
        if (!TextUtils.isEmpty(tag)) {
            tempTag = tag;
        } else {
            tempTag = logTag;
        }
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        boolean find = false;
        for (StackTraceElement element : elements) {
            if (find) {
                if (element.getClassName().equalsIgnoreCase(CLASS_NAME)) {
                    continue;
                }
                stringBuilder.append("(");
                stringBuilder.append(element.getFileName());
                stringBuilder.append(" :");
                stringBuilder.append(element.getLineNumber());
                stringBuilder.append(")");
                switch (logLevel) {
                    case ERROR:
                        Log.e(tempTag, stringBuilder.toString());
                        Log.e(tempTag, msg);
                        break;
                    case WARN:
                        Log.w(tempTag, stringBuilder.toString());
                        Log.w(tempTag, msg);
                        break;
                    case INFO:
                        Log.i(tempTag, stringBuilder.toString());
                        Log.i(tempTag, msg);
                        break;
                    case VERBOSE:
                        Log.v(tempTag, stringBuilder.toString());
                        Log.v(tempTag, msg);
                        break;
                }
                break;
            }
            if ("log".equalsIgnoreCase(element.getMethodName())) {
                find = true;
            }
        }
    }
}
