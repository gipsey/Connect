package org.davidd.connect.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.connection.ErrorMessage;
import org.davidd.connect.connection.MyConnectionListener;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;
import org.davidd.connect.util.DisplayUtils;

public abstract class ConnectionActivity extends Activity implements MyConnectionListener {

    // User which would like to log in
    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConnectionSuccess() {
        L.d(new Object() {});

        MyConnectionManager.instance().login();
    }

    @Override
    public void onConnectionFailed(ErrorMessage message) {
        L.d(new Object() {}, message.getMessage());

        DisplayUtils.showOkAlertDialog(this, getConnectionFailedMessage());
    }

    @Override
    public void onAuthenticationSuccess() {
        L.d(new Object() {});

        ActivityUtils.navigate(this, ControlActivity.class, true);
    }

    @Override
    public void onAuthenticationFailed(ErrorMessage message) {
        L.d(new Object() {}, message.getMessage());

        DisplayUtils.showOkAlertDialog(this, getLoginFailedMessage());
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
