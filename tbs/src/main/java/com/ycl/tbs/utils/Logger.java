package com.ycl.tbs.utils;

import android.util.Log;

public class Logger {


    private static final String TAG = "Logger-Tbs";

    public static boolean enable = true;

    public static void e(String msg, Throwable tr) {
        if (enable) {
            Log.e(TAG, msg, tr);
        }
    }

    public static void e(String msg) {
        e(msg, null);
    }


    public static void i(String msg) {
        if (enable) {
            Log.i(TAG, msg);
        }
    }

}
