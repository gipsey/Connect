package org.davidd.connect.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.connection.event.OnAuthFailedEvent;
import org.davidd.connect.connection.event.OnAuthSucceededEvent;
import org.davidd.connect.connection.event.OnConnectionFailedEvent;
import org.davidd.connect.connection.event.OnConnectionSucceededEvent;
import org.davidd.connect.manager.PreferencesManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DisplayUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends ConnectionActivity {

    @Bind(R.id.progressBar_layout)
    protected View mainProgressBarView;

    @Bind(R.id.status_textView)
    protected TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        user = PreferencesManager.instance().getUser();

        if (MyConnectionManager.instance().isConnected()) {
            // the connection already created
            UserManager.instance().setCurrentUser(user);
            ActivityUtils.navigate(this, ControlActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, true);
        } else if (user != null) {
            // the connection is not created but there is a user saved in preferences
            startConnection();
        } else {
            // there is no previous user saved in preferences, so should navigate to login screen
            ActivityUtils.navigate(SplashActivity.this, LoginActivity.class, true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionSucceeded(OnConnectionSucceededEvent event) {
        super.onConnectionSucceeded(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionFailed(OnConnectionFailedEvent event) {
        hideProgressBar();
        showFailedAlert(getConnectionFailedMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthSucceeded(OnAuthSucceededEvent event) {
        hideProgressBar();
        UserManager.instance().setCurrentUser(user);

        super.onAuthSucceeded(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthFailed(OnAuthFailedEvent event) {
        hideProgressBar();
        showFailedAlert(getLoginFailedMessage());
    }

    private void startConnection() {
        mainProgressBarView.setVisibility(View.VISIBLE);
        statusTextView.setText(getString(R.string.connecting, user.getUserJIDProperties().getJID()));
        connect(user.getUserJIDProperties().getJID(), user.getPassword());
    }

    private void showFailedAlert(String message) {
        DisplayUtils.showOkAlertDialog(this, message,
                "Retry", new Runnable() {
                    @Override
                    public void run() {
                        startConnection();
                    }
                },
                "Sign out", new Runnable() {
                    @Override
                    public void run() {
                        UserManager.instance().logOut();
                        ActivityUtils.navigate(SplashActivity.this, LoginActivity.class, true);
                    }
                });
    }

    private void hideProgressBar() {
        mainProgressBarView.setVisibility(View.INVISIBLE);
        statusTextView.setText(null);
    }
}