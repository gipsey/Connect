package org.davidd.connect.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.debug.L;

public class MyLocationManager implements LocationListener {

    private static final long MIN_TIME = 1000; // in milliseconds
    private static final float MIN_DISTANCE = 2; // metin meters

    private static MyLocationManager myLocationManager;

    private LocationManager locationManager;
    private HandlerThread handlerThread;

    private MyLocationManager() {
    }

    public static MyLocationManager instance() {
        if (myLocationManager == null) {
            myLocationManager = new MyLocationManager();
        }
        return myLocationManager;
    }

    /**
     * Make sure that the following permissions are in the manifest file:
     * Manifest.permission.ACCESS_FINE_LOCATION
     * Manifest.permission.ACCESS_COARSE_LOCATION
     */
    @SuppressWarnings("ResourceType")
    public void startReceivingLocations() {
        clearLocationRelatedReferences();

        handlerThread = new HandlerThread("GPSThread");
        handlerThread.start();

        locationManager = (LocationManager) ConnectApp.instance().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this, handlerThread.getLooper());
    }

    /**
     * Make sure that the following permissions are in the manifest file:
     * Manifest.permission.ACCESS_FINE_LOCATION
     * Manifest.permission.ACCESS_COARSE_LOCATION
     */
    @SuppressWarnings("ResourceType")
    public void stopReceivingLocations() {
        clearLocationRelatedReferences();
    }

    @SuppressWarnings("ResourceType")
    private void clearLocationRelatedReferences() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }

        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        L.d(new Object() {}, "Location = " + location.toString());

        LocationEventManager.instance().sendUserLocationItem(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        L.d(new Object() {}, "Proovider = " + provider + ", status = " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        L.d(new Object() {}, "Proovider " + provider + " became Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        L.d(new Object() {}, "Proovider " + provider + " became Disabled");
    }
}
