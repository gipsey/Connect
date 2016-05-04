package org.davidd.connect.connection;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.connection.packetListener.AcceptAllStanzaFilter;
import org.davidd.connect.connection.packetListener.AllIncomingPacketListener;
import org.davidd.connect.connection.packetListener.AllOutgoingPacketListener;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.MyChatManager;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.util.DataUtils;
import org.davidd.connect.xmpp.AllPepEventListener;
import org.davidd.connect.xmpp.GeolocationExtensionProvider;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.pep.PEPManager;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MyConnectionManager implements ConnectionListener {

    public static final int PORT = 5222;
    public static final int PORT_SSL = 5223;
    public static final int CONNECTION_TIMEOUT = 5000; // in milliseconds
    public static final int RECONNECTION_DELAY = 3; // in seconds

    private static MyConnectionManager connectionManager;
    private List<MyConnectionListener> myConnectionListeners = new ArrayList<>();
    private List<MyDisconnectionListener> myDisconnectionListeners = new ArrayList<>();
    private XMPPTCPConnection xmppTcpConnection;

    private boolean ENABLE_DEBUG_MODE = true;

    private ChatManager chatManager;
    private PubSubManager pubSubManager;
    private PEPManager pepManager;

    private MyConnectionManager() {
    }

    public static MyConnectionManager instance() {
        if (connectionManager == null) {
            connectionManager = new MyConnectionManager();
        }
        return connectionManager;
    }

    /**
     * Ensure that your connection is connected and user is logged in.
     */
    public static void triggerServerServiceDiscoveryInformation(XMPPTCPConnection xmppTcpConnection) {
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppTcpConnection);
        try {
            sdm.discoverInfo(null);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | InterruptedException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensure that your connection is connected and user is logged in.
     */
    public static boolean printAndReturnIfCreateNodesAndPublishItemsAreWorking(XMPPTCPConnection xmppTcpConnection) {
        boolean canCreate = false;

        try {
            canCreate = MyConnectionManager.instance().getPubSubManager().canCreateNodesAndPublishItems();
        } catch (SmackException.NoResponseException | SmackException.NotConnectedException | XMPPException.XMPPErrorException | InterruptedException e) {
            e.printStackTrace();
        }

        L.d("Server '" + xmppTcpConnection.getServiceName() + "' can create nodes = " + canCreate);

        return canCreate;
    }

    public void addConnectionListener(MyConnectionListener listener) {
        if (listener != null && !myConnectionListeners.contains(listener)) {
            myConnectionListeners.add(listener);
        }
    }

    public void removeConnectionListener(MyConnectionListener listener) {
        myConnectionListeners.remove(listener);
    }

    public void addDisconnectionListener(MyDisconnectionListener listener) {
        if (listener != null && !myDisconnectionListeners.contains(listener)) {
            myDisconnectionListeners.add(listener);
        }
    }

    public void removeDisconnectionListener(MyDisconnectionListener listener) {
        myDisconnectionListeners.remove(listener);
    }

    @Nullable
    public String getServiceName() {
        if (xmppTcpConnection == null) {
            return null;
        }
        return xmppTcpConnection.getServiceName().getDomain().toString();
    }

    public void connect(@NonNull final UserJIDProperties JIDProperties, @NonNull final String password, @Nullable final MyConnectionListener connectionListener) {
        L.d(new Object() {}, "JID: " + JIDProperties.getJID() + ", password: " + password + ", MyConnectionListener: " + connectionListener);

        if (!JIDProperties.isNameAndDomainValid()) {
            onConnectionFailed(new ErrorMessage("JID's name and/or domain are null or empty."));
        }

        if (DataUtils.isEmpty(password)) {
            onConnectionFailed(new ErrorMessage("Password is null or empty."));
        }

        // Tear down the connection if exists, because we allow only one connection at the same time
        if (xmppTcpConnection != null) {
            L.d(new Object() {}, "Disconnection started");
            disconnectAsync(new MyDisconnectionListener() {
                @Override
                public void onDisconnect() {
                    createConnection(JIDProperties, password, connectionListener);
                }
            });
        } else {
            createConnection(JIDProperties, password, connectionListener);
        }
    }

    /**
     * Call this only from {@link MyConnectionManager#connect(UserJIDProperties, String, MyConnectionListener)}
     * after checking the parameters.
     */
    private void createConnection(final UserJIDProperties JIDProperties, final String password, final MyConnectionListener connectionListener) {
        L.d(new Object() {}, "JID: " + JIDProperties.getJID() + ", password: " + password);

        new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected Throwable doInBackground(Void... params) {
                try {
                    // Create the connection object
                    XMPPTCPConnectionConfiguration.Builder builder = initializeConnection();

                    builder.setUsernameAndPassword(JIDProperties.getName(), password);
                    builder.setServiceName(JidCreate.from(JIDProperties.getDomain()).asDomainBareJid());
                    builder.setHost(JIDProperties.getDomain());
                    builder.setSendPresence(false);

                    xmppTcpConnection = new XMPPTCPConnection(builder.build());
                    xmppTcpConnection.addConnectionListener(MyConnectionManager.this);

                    ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(xmppTcpConnection);
                    reconnectionManager.enableAutomaticReconnection();

                    addConnectionListener(connectionListener);

                    ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppTcpConnection);
                    sdm.addFeature("http://jabber.org/protocol/geoloc");
                    sdm.addFeature("http://jabber.org/protocol/geoloc+notify");

                    EntityCapsManager capsManager = EntityCapsManager.getInstanceFor(xmppTcpConnection);
                    capsManager.enableEntityCaps();

                    ProviderManager.addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new GeolocationExtensionProvider());

                    pepManager = PEPManager.getInstanceFor(xmppTcpConnection);
                    pepManager.addPEPListener(AllPepEventListener.getInstance());

                    xmppTcpConnection.connect();
                } catch (SmackException | XMPPException | IOException | InterruptedException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable throwable) {
                if (throwable != null) {
                    if (throwable instanceof SmackException.AlreadyConnectedException) {
                        onConnectionSuccess();
                    } else {
                        onConnectionFailed(new ErrorMessage("Connection failed to " + xmppTcpConnection.getServiceName() + ", " + xmppTcpConnection.getHost()));
                    }
                }
            }
        }.execute();
    }

    private XMPPTCPConnectionConfiguration.Builder initializeConnection() {
        XMPPTCPConnection.setUseStreamManagementDefault(true);

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
        builder.setPort(PORT);
        builder.setConnectTimeout(CONNECTION_TIMEOUT);
        builder.setCompressionEnabled(false);

        if (ENABLE_DEBUG_MODE) {
            builder.setDebuggerEnabled(true);
            SmackConfiguration.DEBUG = true;
            System.setProperty("smack.debugEnabled", "true");
            // System.setProperty("smack.debuggerClass", "org.jivesoftware.smackx.debugger.EnhancedDebugger");
        } else {
            builder.setDebuggerEnabled(false);
            SmackConfiguration.DEBUG = false;
            System.setProperty("smack.debugEnabled", "false");
        }

        try {
            TLSUtils.acceptAllCertificates(builder);
            TLSUtils.disableHostnameVerificationForTlsCertificicates(builder);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        return builder;
    }

    private void disconnectAsync(final MyDisconnectionListener disconnectionListener) {
        xmppTcpConnection.removeConnectionListener(this);
        tearDownManagersOnDisconnect();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                xmppTcpConnection.disconnect();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                disconnectionListener.onDisconnect();
                for (MyDisconnectionListener listener : myDisconnectionListeners) {
                    listener.onDisconnect();
                }
            }
        }.execute();
    }

    public void disconnectSync() {
        tearDownManagersOnDisconnect();
        xmppTcpConnection.disconnect();
    }

    /**
     * Invoke this only if the connection is already made and it's non anonymous.
     */
    public void login() {
        new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected Throwable doInBackground(Void... params) {
                try {
                    xmppTcpConnection.login();
                } catch (XMPPException | SmackException | IOException | InterruptedException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable throwable) {
                if (throwable != null) {
                    onAuthenticationFailed(new ErrorMessage("Login failed"));
                }
            }
        }.execute();
    }

    public void sendPresence(Presence presence) throws Exception {
        xmppTcpConnection.sendStanza(presence);
    }

    @Override
    public void connected(XMPPConnection connection) {
        L.d(new Object() {});
        onConnectionSuccess();
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        L.d(new Object() {});
        onAuthenticationSuccess();
    }

    @Override
    public void connectionClosed() {
        L.d(new Object() {});
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        L.d(new Object() {});
    }

    @Override
    public void reconnectionSuccessful() {
        L.d(new Object() {});
    }

    @Override
    public void reconnectingIn(int seconds) {
        L.d(new Object() {});
    }

    @Override
    public void reconnectionFailed(Exception e) {
        L.d(new Object() {});
    }

    private void onConnectionSuccess() {
        L.d(new Object() {});

        initializeManagersOnConnectionSuccess();

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (MyConnectionListener listener : myConnectionListeners) {
                    listener.onConnectionSuccess();
                }
            }
        });
    }

    private void onConnectionFailed(final ErrorMessage message) {
        L.d(new Object() {}, message.getMessage());

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (MyConnectionListener listener : myConnectionListeners) {
                    listener.onConnectionFailed(message);
                }
            }
        });
    }

    private void onAuthenticationSuccess() {
        L.d(new Object() {});

        initializeManagersOnAuthSuccess();

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (MyConnectionListener listener : myConnectionListeners) {
                    listener.onAuthenticationSuccess();
                }
            }
        });
    }

    private void onAuthenticationFailed(final ErrorMessage message) {
        L.d(new Object() {}, message.getMessage());

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (MyConnectionListener listener : myConnectionListeners) {
                    listener.onAuthenticationFailed(message);
                }
            }
        });
    }

    private void initializeManagersOnConnectionSuccess() {
        // add packet listeners
        if (ENABLE_DEBUG_MODE) {
            xmppTcpConnection.addAsyncStanzaListener(AllIncomingPacketListener.instance(), new AcceptAllStanzaFilter());
            xmppTcpConnection.addPacketInterceptor(AllOutgoingPacketListener.instance(), new AcceptAllStanzaFilter());
        }

        // set up chat manager
        chatManager = ChatManager.getInstanceFor(xmppTcpConnection);
        chatManager.addChatListener(MyChatManager.instance());
    }

    private void tearDownManagersOnDisconnect() {
        // remove packet listeners
        if (ENABLE_DEBUG_MODE) {
            xmppTcpConnection.removePacketInterceptor(AllIncomingPacketListener.instance());
            xmppTcpConnection.removeAsyncStanzaListener(AllOutgoingPacketListener.instance());
        }

        pubSubManager = null;
        pepManager = null;

        // tear down chat manager
        //     if (chatManager != null) {
        //        chatManager.removeChatListener(MyChatManager.instance());
        //        chatManager = null;
        //     }
    }

    private void initializeManagersOnAuthSuccess() {
        pubSubManager = PubSubManager.getInstance(xmppTcpConnection, xmppTcpConnection.getUser().asBareJid());
//        pubSubManager = PubSubManager.getInstance(xmppTcpConnection, check with domain not with the id of the user); // TODO

        if (ENABLE_DEBUG_MODE) {
            triggerServerServiceDiscoveryInformation(xmppTcpConnection);
            printAndReturnIfCreateNodesAndPublishItemsAreWorking(xmppTcpConnection);
        }
    }

    /**
     * You wanna call this only when the connection succeeded and then xmppTcpConnection isn't null
     */
    public XMPPTCPConnection getXmppTcpConnection() {
        return xmppTcpConnection;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public PubSubManager getPubSubManager() {
        return pubSubManager;
    }

    public PEPManager getPepManager() {
        return pepManager;
    }
}
