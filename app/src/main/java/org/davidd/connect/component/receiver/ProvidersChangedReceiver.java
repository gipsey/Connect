package org.davidd.connect.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import org.davidd.connect.component.service.LocationService;
import org.davidd.connect.debug.L;

public class ProvidersChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(new Object() {}, "-");

        if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, LocationService.class);
            serviceIntent.putExtra(LocationService.LOCATION_CHANGED_TAG, "");

            context.startService(serviceIntent);
        }
    }
}