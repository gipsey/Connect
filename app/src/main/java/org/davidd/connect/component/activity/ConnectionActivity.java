package org.davidd.connect.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.component.service.LocationService;
import org.davidd.connect.connection.ConnectionService;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.connection.event.OnAuthFailedEvent;
import org.davidd.connect.connection.event.OnAuthSucceededEvent;
import org.davidd.connect.connection.event.OnConnectionFailedEvent;
import org.davidd.connect.connection.event.OnConnectionSucceededEvent;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;
import org.davidd.connect.util.DisplayUtils;
import org.greenrobot.eventbus.EventBus;

public abstract class ConnectionActivity extends Activity {

    // User which would like to log in
    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onConnectionSucceeded(OnConnectionSucceededEvent event) {
    }

    public void onConnectionFailed(OnConnectionFailedEvent event) {
        L.d(new Object() {}, event.errorMessage.getMessage());

        DisplayUtils.showOkAlertDialog(this, getConnectionFailedMessage());
    }

    public void onAuthSucceeded(OnAuthSucceededEvent event) {
        L.d(new Object() {});

        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra(LocationService.AUTH_SUCCEEDED_TAG, "");
        startService(serviceIntent);

        ActivityUtils.navigate(this, ControlActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, true);
    }

    public void onAuthFailed(OnAuthFailedEvent event) {
        L.d(new Object() {}, event.errorMessage.getMessage());

        DisplayUtils.showOkAlertDialog(this, getLoginFailedMessage());
    }

    protected void connect(String user, String password) {
        Intent serviceIntent = new Intent(this, ConnectionService.class);
        serviceIntent.putExtra(ConnectionService.USER_TAG, user);
        serviceIntent.putExtra(ConnectionService.PASSWORD_TAG, password);
        startService(serviceIntent);
    }

    protected String getServiceName() {
        String serviceName = MyConnectionManager.instance().getServiceName();
        if (DataUtils.isEmpty(serviceName)) {
            serviceName = getString(R.string.server);
        }
        return serviceName;
    }

    protected String getConnectionFailedMessage() {
        return getString(R.string.connection_failed, getServiceName());
    }

    protected String getLoginFailedMessage() {
        return getString(R.string.login_failed, getServiceName());
    }
}
