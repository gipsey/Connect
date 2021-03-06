package org.davidd.connect.debug;

import android.util.Log;

/**
 * This class stands for Logging
 */
public class L {

    public static void d(String message) {
        Log.d("-", message);
    }

    public static void d(Object object) {
        d(object, "-");
    }

    public static void d(Object object, String message) {
        String className = object.getClass().getName();
        Log.d(className + " : " + object.getClass().getEnclosingMethod().getName(), message);
    }

    public static void e(Object object) {
        e(object, "");
    }

    public static void e(Object object, String message) {
        String className = object.getClass().getName();
        Log.e(className + " : " + object.getClass().getEnclosingMethod().getName(), message);
    }

    public static void ex(Throwable throwable) {
        throwable.printStackTrace();
    }
}