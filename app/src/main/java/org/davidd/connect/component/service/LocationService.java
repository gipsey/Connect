package org.davidd.connect.component.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

import org.davidd.connect.debug.L;
import org.davidd.connect.manager.MyLocationManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationService extends Service {

    public static final String LOCATION_CHANGED_TAG = "LocationChangedTag";

    private ExecutorService executorService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        L.d(new Object() {}, LocationService.class.getSimpleName() + " was created.");
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        L.d(new Object() {}, " method");

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (intent == null) {
                    L.d(new Object() {}, "Intent is null, which means that service was recreated.");

                    setLocationReceivingProgress();
                } else if (intent.hasExtra(LOCATION_CHANGED_TAG)) {
                    L.d(new Object() {}, "Intent contains LOCATION_CHANGED_TAG");

                    setLocationReceivingProgress();
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(new Object() {}, LocationService.class.getSimpleName() + " has stopped working.");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        L.d(new Object() {}, LocationService.class.getSimpleName() + " task was removed");
    }

    private void setLocationReceivingProgress() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MyLocationManager.instance().startReceivingLocations();
        } else {
            MyLocationManager.instance().stopReceivingLocations();
            stopSelf();
        }
    }
}