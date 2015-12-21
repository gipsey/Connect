package org.davidd.connect;

import android.app.Application;

/**
 * @author David Debre
 *         on 2015/12/01
 */
public class MyApp extends Application {
    private static MyApp sMyApp;

    public static MyApp instance() {
        return sMyApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sMyApp = this;
    }
}
