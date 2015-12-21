package org.davidd.connect.connection;

import android.support.annotation.Nullable;
import android.util.Log;

import org.davidd.connect.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Debre
 *         on 2015/12/12
 */
public class ConnectionManager implements XmppConnectionListener {
    public static final String TAG = ConnectionManager.class.getSimpleName();

    private static ConnectionManager sConnectionManager;
    private static List<XmppConnectionListener> sXmppConnectionListeners = new ArrayList<>();

    private XmppConnection mXmppConnection;

    public static ConnectionManager instance(@Nullable XmppConnectionListener listener) {
        if (listener != null) {
            sXmppConnectionListeners.add(listener);
        }

        if (sConnectionManager == null) {
            sConnectionManager = new ConnectionManager();
        }
        return sConnectionManager;
    }

    private ConnectionManager() {
        mXmppConnection = new XmppConnection(this);
    }

    public XmppConnection getXmppConnection() {
        return mXmppConnection;
    }

    @Override
    public void onConnectionSuccess() {
        Log.d(TAG, AppConstants.METHOD + new Object(){}.getClass().getEnclosingMethod().getName());

        for (XmppConnectionListener listener : sXmppConnectionListeners) {
            listener.onConnectionSuccess();
        }
    }

    @Override
    public void onConnectionFailed(ErrorMessage message) {
        Log.d(TAG, AppConstants.METHOD + new Object(){}.getClass().getEnclosingMethod().getName());

        for (XmppConnectionListener listener : sXmppConnectionListeners) {
            listener.onConnectionFailed(message);
        }
    }

    @Override
    public void onAuthenticationSuccess() {
        Log.d(TAG, AppConstants.METHOD + new Object(){}.getClass().getEnclosingMethod().getName());

        for (XmppConnectionListener listener : sXmppConnectionListeners) {
            listener.onAuthenticationSuccess();
        }
    }

    @Override
    public void onAuthenticationFailed(ErrorMessage message) {
        Log.d(TAG, AppConstants.METHOD + new Object(){}.getClass().getEnclosingMethod().getName());

        for (XmppConnectionListener listener : sXmppConnectionListeners) {
            listener.onAuthenticationFailed(message);
        }
    }
}
