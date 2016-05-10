package org.davidd.connect.connection;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.davidd.connect.debug.L;
import org.davidd.connect.manager.MyNotificationManager;
import org.davidd.connect.manager.PreferencesManager;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionService extends Service {

    public static final String USER_TAG = "UserTag";
    public static final String PASSWORD_TAG = "PasswordTag";

    ExecutorService executorService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        L.d(new Object() {}, ConnectionService.class.getSimpleName() + " was created.");

        executorService = Executors.newSingleThreadExecutor();

//        startInForeground(); // TODO: no need for notification just to make the service untouchable
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        L.d(new Object() {}, " method");

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (intent == null) { // the service was recreated
                    L.d(new Object() {}, "Intent is null, which means that service was recreated.");
                    logInWithPreferencesData();
                } else {
                    String userName = intent.getStringExtra(USER_TAG);
                    String password = intent.getStringExtra(PASSWORD_TAG);

                    if (userName != null && password != null) {
                        // log in
                        L.d(new Object() {}, "User and password sent in intent");
                        MyConnectionManager.instance().connect(new UserJIDProperties(userName), password);
                    } else {
                        // system booted up or network is available; try log in with credentials from preferences
                        L.d(new Object() {}, "System just booted up or network got available");

                        logInWithPreferencesData();
                    }
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(new Object() {}, ConnectionService.class.getSimpleName() + " has stopped working.");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        L.d(new Object() {}, ConnectionService.class.getSimpleName() + " task was removed");
    }

    private void startInForeground() {
        Notification notification =
                MyNotificationManager.instance().createServiceMainNotification(this);

        startForeground(MyNotificationManager.CONNECTION_SERVICE_NOTIFICATION_ID, notification);
    }

    private void logInWithPreferencesData() {
        User user = PreferencesManager.instance().getUser();
        if (user == null) {
            // if there is no user in the preferences we stop this service
            stopSelf();
        } else {
            // if there is a user in preferences then we attempt to log in
            MyConnectionManager.instance().connect(user.getUserJIDProperties(), user.getPassword());
        }
    }
}
