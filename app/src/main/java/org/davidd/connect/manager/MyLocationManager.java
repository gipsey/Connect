package org.davidd.connect.manager;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.component.fragment.SettingsFragment;
import org.davidd.connect.debug.L;
import org.davidd.connect.xmpp.GeolocationItem;

import java.io.IOException;
import java.util.List;

public class MyLocationManager implements LocationListener {

    // TODO make it adjustable
    private static final long MIN_TIME = 2000; // in milliseconds
    private static final float MIN_DISTANCE = 10; // metin meters

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

        if (PreferencesManager.instance().getSettingsValue(SettingsFragment.LOCATION_KEY, false)) {
            GeolocationItem item = buildGeolocationItemFromLocation(location);
            LocationEventManager.instance().sendUserLocationItem(item);
        }
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

    private GeolocationItem buildGeolocationItemFromLocation(Location location) {
        GeolocationItem item = new GeolocationItem();

        item.setAccuracy(location.getAccuracy());
        item.setAlt(location.getAltitude());
        item.setBearing(location.getBearing());
        item.setLat(location.getLatitude());
        item.setLon(location.getLongitude());
        item.setSpeed(location.getSpeed());
        item.setTimestamp(String.valueOf(location.getTime()));

        Geocoder geocoder = new Geocoder(ConnectApp.instance().getApplicationContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);

                item.setArea(address.getSubAdminArea());
                item.setCountry(address.getCountryName());
                item.setCountrycode(address.getCountryCode());
                item.setLocality(address.getLocality());
                item.setPostalcode(address.getPostalCode());
                item.setRegion(address.getAdminArea());
                item.setStreet(address.getThoroughfare());
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return item;
    }
}
