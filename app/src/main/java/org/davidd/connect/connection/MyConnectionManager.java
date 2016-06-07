package org.davidd.connect.connection;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.davidd.connect.connection.event.OnAuthFailedEvent;
import org.davidd.connect.connection.event.OnAuthSucceededEvent;
import org.davidd.connect.connection.event.OnConnectionFailedEvent;
import org.davidd.connect.connection.event.OnConnectionSucceededEvent;
import org.davidd.connect.connection.event.OnDisconnectEvent;
import org.davidd.connect.connection.event.OnRegistrationProcessFinishedEvent;
import org.davidd.connect.connection.packetListener.AcceptAllStanzaFilter;
import org.davidd.connect.connection.packetListener.AllIncomingPacketListener;
import org.davidd.connect.connection.packetListener.AllOutgoingPacketListener;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.MyChatManager;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.model.UserPresenceType;
import org.davidd.connect.util.DataUtils;
import org.davidd.connect.xmpp.AllPepEventListener;
import org.davidd.connect.xmpp.GeolocationExtensionProvider;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.pep.PEPManager;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MyConnectionManager implements ConnectionListener {

    public static final int PORT = 5222;
    public static final int PORT_SSL = 5223;
    public static final int CONNECTION_AND_PACKET_TIMEOUT = 6000; // in milliseconds

    private static MyConnectionManager connectionManager;
    private XMPPTCPConnection xmppTcpConnection;

    private boolean ENABLE_DEBUG_MODE = true;

    private boolean createConnectionForRegistration;
    private User userToRegisterWith;

    private Roster roster;
    private ChatManager chatManager;
    private MultiUserChatManager multiUserChatManager;
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

    @Nullable
    public String getServiceName() {
        if (xmppTcpConnection == null) {
            return null;
        }
        return xmppTcpConnection.getServiceName().getDomain().toString();
    }

    void connect(@NonNull final UserJIDProperties JIDProperties, @NonNull final String password) {
        L.d(new Object() {}, "JID: " + JIDProperties.getJID() + ", password: " + password);

        if (!JIDProperties.isNameAndDomainValid()) {
            EventBus.getDefault().post(new OnConnectionFailedEvent(new ErrorMessage("JID's name and/or domain are null or empty.")));
        }

        if (DataUtils.isEmpty(password)) {
            EventBus.getDefault().post(new OnConnectionFailedEvent(new ErrorMessage("Password is null or empty.")));
        }

        userToRegisterWith = null;
        createConnectionForRegistration = false;

        // Tear down the connection if exists, because we allow only one connection at the same time
        if (xmppTcpConnection != null) {
            L.d(new Object() {}, "Disconnection started");
            disconnectAsync(new MyDisconnectionListener() {
                @Override
                public void onDisconnect() {
                    createConnection(JIDProperties, password);
                }
            });
        } else {
            createConnection(JIDProperties, password);
        }
    }

    public void register(User user) {
        userToRegisterWith = user;
        createConnectionForRegistration = true;
        createConnection(user.getUserJIDProperties(), user.getPassword());
    }

    /**
     * Invoke this only if the connection is already made and it's non anonymous.
     */
    private void login() {
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
                    EventBus.getDefault().post(new OnAuthFailedEvent(new ErrorMessage("Login failed")));
                }
            }
        }.execute();
    }

    /**
     * Call this only from {@link MyConnectionManager#connect(UserJIDProperties, String)}
     * after checking the parameters.
     */
    private void createConnection(final UserJIDProperties JIDProperties, final String password) {
        L.d(new Object() {}, "JID: " + JIDProperties.getJID() + ", password: " + password);

        new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected Throwable doInBackground(Void... params) {
                try {
                    xmppTcpConnection = createConnectionInstance(JIDProperties, password);
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
                    if (createConnectionForRegistration) {
                        EventBus.getDefault().post(new OnRegistrationProcessFinishedEvent(throwable));
                    } else {
                        if (throwable instanceof SmackException.AlreadyConnectedException) {
                            connected(xmppTcpConnection);
                        } else {
                            EventBus.getDefault().post(new OnConnectionFailedEvent(new ErrorMessage(
                                    "Connection failed to " + xmppTcpConnection.getServiceName() + ", " + xmppTcpConnection.getHost())));
                        }
                    }
                }
            }
        }.execute();
    }

    private XMPPTCPConnection createConnectionInstance(final UserJIDProperties JIDProperties, final String password) throws XmppStringprepException {
        // Create the connection object
        XMPPTCPConnectionConfiguration.Builder builder = initializeConnection();

        builder.setUsernameAndPassword(JIDProperties.getName(), password);
        builder.setXmppDomain(JidCreate.from(JIDProperties.getDomain()).asDomainBareJid());
        builder.setHost(JIDProperties.getDomain());
        builder.setSendPresence(false);

        XMPPTCPConnection xmppTcpConnection = new XMPPTCPConnection(builder.build());
        xmppTcpConnection.setPacketReplyTimeout(CONNECTION_AND_PACKET_TIMEOUT);
        xmppTcpConnection.addConnectionListener(MyConnectionManager.this);

        if (!createConnectionForRegistration) {
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(xmppTcpConnection);
            reconnectionManager.enableAutomaticReconnection();

            ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppTcpConnection);
            sdm.addFeature("http://jabber.org/protocol/geoloc");
            sdm.addFeature("http://jabber.org/protocol/geoloc+notify");

            EntityCapsManager capsManager = EntityCapsManager.getInstanceFor(xmppTcpConnection);
            capsManager.enableEntityCaps();

            ProviderManager.addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new GeolocationExtensionProvider());

            pepManager = PEPManager.getInstanceFor(xmppTcpConnection);
            pepManager.addPEPListener(AllPepEventListener.getInstance());
        }

        return xmppTcpConnection;
    }

    private XMPPTCPConnectionConfiguration.Builder initializeConnection() {
        XMPPTCPConnection.setUseStreamManagementDefault(true);

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        builder.setPort(PORT);
        builder.setConnectTimeout(CONNECTION_AND_PACKET_TIMEOUT);
        builder.setCompressionEnabled(true);

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

    public void disconnectAsync() {
        disconnectAsync(null);
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
                if (disconnectionListener != null) {
                    disconnectionListener.onDisconnect();
                }
                EventBus.getDefault().post(new OnDisconnectEvent());
            }
        }.execute();
    }

    public void sendPresence(Presence presence) throws Exception {
        xmppTcpConnection.sendStanza(presence);
    }

    @Override
    public void connected(XMPPConnection connection) {
        L.d(new Object() {});

        if (createConnectionForRegistration) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Jid jid = JidCreate.entityBareFrom(userToRegisterWith.getUserJIDProperties().getJID());
                        Localpart userName = jid.getLocalpartOrNull();

                        Map<String, String> map = new HashMap<>();
                        map.put("username", userName.toString());
                        map.put("name", userName.toString());
                        map.put("password", userToRegisterWith.getPassword());
                        map.put("email", "asd@asd.asd");
                        map.put("creationDate", "" + System.currentTimeMillis() / 1000L);

                        AccountManager accountManager = AccountManager.getInstance(xmppTcpConnection);
                        accountManager.sensitiveOperationOverInsecureConnection(true);
                        accountManager.createAccount(userName, userToRegisterWith.getPassword(), map);

                        EventBus.getDefault().post(new OnRegistrationProcessFinishedEvent(null));
                    } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException | SmackException.NoResponseException | InterruptedException | XmppStringprepException e) {
                        e.printStackTrace();

                        EventBus.getDefault().post(new OnRegistrationProcessFinishedEvent(e));
                    }
                }
            }).start();
        } else {
            initializeManagersOnConnectionSuccess();
            EventBus.getDefault().post(new OnConnectionSucceededEvent());

            login();
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        L.d(new Object() {});

        initializeManagersOnAuthSuccess();
        EventBus.getDefault().post(new OnAuthSucceededEvent());
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

    private void initializeManagersOnConnectionSuccess() {
        // add packet listeners
        if (ENABLE_DEBUG_MODE) {
            xmppTcpConnection.addAsyncStanzaListener(AllIncomingPacketListener.instance(), new AcceptAllStanzaFilter());
            xmppTcpConnection.addPacketInterceptor(AllOutgoingPacketListener.instance(), new AcceptAllStanzaFilter());
        }

        // set up roster
        roster = Roster.getInstanceFor(MyConnectionManager.instance().getXmppTcpConnection());
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        roster.addRosterListener(RosterManager.instance());

        xmppTcpConnection.addAsyncStanzaListener(RosterManager.instance(), new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                return stanza instanceof Presence;
            }
        });

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

        if (roster != null) {
            roster.removeRosterListener(RosterManager.instance());
            roster = null;
        }

        if (chatManager != null) {
            chatManager.removeChatListener(MyChatManager.instance());
            chatManager = null;
        }

        if (multiUserChatManager != null) {
            multiUserChatManager = null;
        }

        pubSubManager = null;
        pepManager = null;
    }

    private void initializeManagersOnAuthSuccess() {
        pubSubManager = PubSubManager.getInstance(xmppTcpConnection, xmppTcpConnection.getUser().asBareJid());

        try {
            RosterManager.instance().sendPresence(UserPresenceType.AVAILABLE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set up multi user chat manager
        multiUserChatManager = MultiUserChatManager.getInstanceFor(xmppTcpConnection);

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

    public boolean isConnected() {
        return getXmppTcpConnection() != null && getXmppTcpConnection().isConnected();
    }

    public Roster getRoster() {
                return roster;
//        return Roster.getInstanceFor(xmppTcpConnection);
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MultiUserChatManager getMultiUserChatManager() {
        return multiUserChatManager;
    }

    public PubSubManager getPubSubManager() {
        return pubSubManager;
    }

    public PEPManager getPepManager() {
        return pepManager;
    }

    interface MyDisconnectionListener {
        void onDisconnect();
    }
}
