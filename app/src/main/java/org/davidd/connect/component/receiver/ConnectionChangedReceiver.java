package org.davidd.connect.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.davidd.connect.connection.ConnectionService;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.util.DataUtils;

public class ConnectionChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(new Object() {}, "-");

        if (DataUtils.isNetworkAvailable(context) && !MyConnectionManager.instance().isConnected()) {
            Intent serviceIntent = new Intent(context, ConnectionService.class);
            context.startService(serviceIntent);
        }
    }
}
