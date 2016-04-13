package org.davidd.connect.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.connection.ErrorMessage;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.manager.PreferencesManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DisplayUtils;

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

        if (user != null) {
            startConnection();
        } else { // there is no previous user saved in preferences
            ActivityUtils.navigate(SplashActivity.this, LoginActivity.class, true);
        }
    }

    @Override
    public void onConnectionSuccess() {
        super.onConnectionSuccess();
    }

    @Override
    public void onConnectionFailed(ErrorMessage message) {
        hideProgressBar();
        showFailedAlert(getConnectionFailedMessage());
    }

    @Override
    public void onAuthenticationSuccess() {
        hideProgressBar();
        UserManager.instance().setCurrentUser(user);
        super.onAuthenticationSuccess();
    }

    @Override
    public void onAuthenticationFailed(ErrorMessage message) {
        hideProgressBar();
        showFailedAlert(getLoginFailedMessage());
    }

    private void startConnection() {
        mainProgressBarView.setVisibility(View.VISIBLE);
        statusTextView.setText(getString(R.string.connecting, user.getUserJIDProperties().getJID()));
        MyConnectionManager.instance().connect(user.getUserJIDProperties(), user.getPassword(), this);
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