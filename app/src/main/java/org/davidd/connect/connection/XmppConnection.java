package org.davidd.connect.connection;

import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.davidd.connect.util.AppConstants;
import org.davidd.connect.util.DataUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * @author David Debre
 *         on 2015/12/12
 */
public class XmppConnection {
    public static final String TAG = XmppConnection.class.getSimpleName();

    private XMPPTCPConnection mXMPPTCPConnection;
    private XmppConnectionListener mXmppConnectionListener;

    XmppConnection(@NonNull XmppConnectionListener xmppConnectionListener) {
        mXmppConnectionListener = xmppConnectionListener;
        initializeConnection();
        setupConnectionListener();
    }

    private void initializeConnection() {
        XMPPTCPConnectionConfiguration.Builder builder =
                XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        builder.setServiceName(ConnectionConstants.SERVICE_NAME);
        builder.setHost(ConnectionConstants.HOST);
        builder.setPort(ConnectionConstants.PORT);
        builder.setConnectTimeout(ConnectionConstants.CONNECTION_TIMEOUT);
        builder.setDebuggerEnabled(true);

        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);

        mXMPPTCPConnection = new XMPPTCPConnection(builder.build());

        ReconnectionManager.getInstanceFor(mXMPPTCPConnection).disableAutomaticReconnection();
    }

    private void setupConnectionListener() {
        mXMPPTCPConnection.addConnectionListener(
                new ConnectionListener() {
                    @Override
                    public void connected(XMPPConnection connection) {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                        // TODO: what is this?
                        Looper.prepare();

                        mXmppConnectionListener.onConnectionSuccess();
                    }

                    @Override
                    public void authenticated(XMPPConnection connection, boolean resumed) {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                        mXmppConnectionListener.onAuthenticationSuccess();
                    }

                    @Override
                    public void connectionClosed() {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                    }

                    @Override
                    public void reconnectionSuccessful() {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                    }

                    @Override
                    public void reconnectingIn(int seconds) {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                    }

                    @Override
                    public void reconnectionFailed(Exception e) {
                        Log.d(TAG, AppConstants.METHOD + new Object() {
                        }.getClass().getEnclosingMethod().getName());

                    }
                }
        );
    }

    public void connect() {
        new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected Throwable doInBackground(Void... params) {
                try {
                    mXMPPTCPConnection.connect();
                } catch (SmackException | XMPPException | IOException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable throwable) {
                if (throwable != null) {
                    if (throwable instanceof SmackException.AlreadyConnectedException) {
                        mXmppConnectionListener.onConnectionSuccess();
                    } else {
                        mXmppConnectionListener.onConnectionFailed(
                                new ErrorMessage("Connection failed while trying to connect to server"));
                    }
                }
            }
        }.execute();
    }

    public void disconnect() {
        mXMPPTCPConnection.disconnect();
    }

    public void login() {
        login(mXMPPTCPConnection.getConfiguration().getUsername().toString(),
                mXMPPTCPConnection.getConfiguration().getPassword());
    }

    public void login(String userName, String password) {
        if (!mXMPPTCPConnection.isConnected()) {
            mXmppConnectionListener.onAuthenticationFailed(
                    new ErrorMessage("There is no connection with the server"));
            return;
        }

        try {
            if (DataUtils.isEmpty(userName) || DataUtils.isEmpty(password)) {
                if (mXMPPTCPConnection.isAnonymous()) {
                    mXmppConnectionListener.onAuthenticationFailed(
                            new ErrorMessage("You have to provide username and password"));
                } else {
                    mXMPPTCPConnection.login();
                }
            } else {
                mXMPPTCPConnection.login(userName, password);
            }
        } catch (XMPPException | IOException | SmackException e) {
            e.printStackTrace();
            mXmppConnectionListener.onAuthenticationFailed(
                    new ErrorMessage("Connection failed while trying to log in"));
        }
    }
}
