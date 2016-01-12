package org.davidd.connect;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import org.davidd.connect.manager.RosterManager;

/**
 * @author David Debre
 *         on 2015/12/01
 */
public class ConnectApp extends Application {

    private static ConnectApp connectApp;
    private static Handler mainHandler;

    public static ConnectApp instance() {
        return connectApp;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connectApp = this;
        mainHandler = new Handler(Looper.getMainLooper());

        RosterManager.instance(); // this will initialize the listeners
    }
}
