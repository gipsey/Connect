package org.davidd.connect.ui.activity;

import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.connection.ConnectionConstants;
import org.davidd.connect.connection.ConnectionManager;
import org.davidd.connect.connection.ErrorMessage;
import org.davidd.connect.connection.XmppConnection;
import org.davidd.connect.connection.XmppConnectionListener;
import org.davidd.connect.ui.fragment.ChatFragment;
import org.davidd.connect.util.ActivityUtils;

/**
 * @author David Debre
 *         on 2015/12/01
 */
public class ControlActivity extends DrawerActivity implements XmppConnectionListener {
    private XmppConnection mXmppConnection;

    private ChatFragment mChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

//        connectToXmppServer();

        openChatFragment(savedInstanceState);
    }

    @Override
    public void onConnectionSuccess() {
        ActivityUtils.showToast(this, "onConnectionSuccess");

        mXmppConnection.login(ConnectionConstants.MOCK_USER_1_NAME,
                ConnectionConstants.MOCK_USER_1_PASSWORD);
    }

    @Override
    public void onConnectionFailed(ErrorMessage message) {
        ActivityUtils.showToast(this, "onConnectionFailed");
        ActivityUtils.showToast(this, message.getMessage());
    }

    @Override
    public void onAuthenticationSuccess() {
        ActivityUtils.showToast(this, "onAuthenticationSuccess");

        openChatFragment(null);
    }

    @Override
    public void onAuthenticationFailed(ErrorMessage message) {
        ActivityUtils.showToast(this, "onAuthenticationFailed");
        ActivityUtils.showToast(this, message.getMessage());
    }

    private void connectToXmppServer() {
        mXmppConnection = ConnectionManager.instance(this).getXmppConnection();
        mXmppConnection.connect();
    }

    private void openChatFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        mChatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG);

        if (mChatFragment == null) {
            mChatFragment = new ChatFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, mChatFragment, ChatFragment.TAG)
                .addToBackStack(ChatFragment.TAG)
                .commit();
    }
}