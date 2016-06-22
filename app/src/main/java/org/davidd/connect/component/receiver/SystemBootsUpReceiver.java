package org.davidd.connect.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.davidd.connect.component.fragment.SettingsFragment;
import org.davidd.connect.connection.ConnectionService;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.PreferencesManager;
import org.davidd.connect.util.DataUtils;

public class SystemBootsUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(new Object() {}, "-");

        if (PreferencesManager.instance().getSettingsValue(SettingsFragment.BOOT_KEY, false)) {
            if (DataUtils.isNetworkAvailable(context)) {
                Intent serviceIntent = new Intent(context, ConnectionService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
