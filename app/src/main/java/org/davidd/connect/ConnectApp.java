package org.davidd.connect;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
