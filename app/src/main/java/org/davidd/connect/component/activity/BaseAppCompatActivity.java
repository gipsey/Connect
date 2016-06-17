package org.davidd.connect.component.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.davidd.connect.connection.ConnectionService;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.connection.event.OnDisconnectEvent;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.util.ActivityUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 *
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    protected void logOut() {
        MyConnectionManager.instance().disconnectAsync();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisconnect(OnDisconnectEvent event) {
        String userName = UserManager.instance().getCurrentUser().getUserJIDProperties().getName();
        UserManager.instance().logOut();

        Intent intent = new Intent(this, ConnectionService.class);
        stopService(intent);

        Toast.makeText(this, "Bye " + userName + "!", Toast.LENGTH_SHORT).show();
        ActivityUtils.navigate(this, SplashActivity.class, true);
    }
}
