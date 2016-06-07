package org.davidd.connect.manager;

import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.model.UserPresence;
import org.davidd.connect.model.UserPresenceType;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class RosterManager implements RosterListener, StanzaListener {

    private static RosterManager rosterManager;

    private RosterManager() {
    }

    public static RosterManager instance() {
        if (rosterManager == null) {
            rosterManager = new RosterManager();
        }
        return rosterManager;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
        if (!(packet instanceof Presence)) {
            return;
        }

        Presence presence = (Presence) packet;
        if (presence.getType() == Presence.Type.subscribe) {






            EventBus.getDefault().post(new RefreshRoster());
        }
    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        L.d(new Object() {}, addresses.toString());

        EventBus.getDefault().post(new RefreshRoster());
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        L.d(new Object() {}, addresses.toString());

        EventBus.getDefault().post(new RefreshRoster());
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        L.d(new Object() {}, addresses.toString());

        EventBus.getDefault().post(new RefreshRoster());
    }

    @Override
    public void presenceChanged(Presence presence) {
        L.d(new Object() {}, presence.toString());

        final User user = new User(new UserJIDProperties(presence.getFrom().toString()));
        user.setUserPresence(getUserPresenceForUser(user.getUserJIDProperties()));
        EventBus.getDefault().post(new UserPresenceChangedMessage(user));

        EventBus.getDefault().post(new RefreshRoster());
    }

    public void sendPresence(UserPresenceType userPresence, String status) throws Exception {
        Presence.Type type;
        Presence.Mode mode = null;
        switch (userPresence) {
            case AVAILABLE:
                type = Presence.Type.available;
                mode = Presence.Mode.available;
                break;
            case AWAY:
                type = Presence.Type.available;
                mode = Presence.Mode.away;
                break;
            case DO_NOT_DISTURB:
                type = Presence.Type.available;
                mode = Presence.Mode.dnd;
                break;
            default:
                type = Presence.Type.unavailable;
        }

        Presence presence = new Presence(type);
        if (mode != null) {
            presence.setMode(mode);
        }
        presence.setStatus(status);

        MyConnectionManager.instance().sendPresence(presence);
    }

    public RosterEntry getRosterEntryForUser(UserJIDProperties userJIDProperties) {
        L.d(new Object() {});

        try {
            return MyConnectionManager.instance().getRoster().getEntry(JidCreate.bareFrom(userJIDProperties.getJID()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserPresence getUserPresenceForUser(UserJIDProperties userJIDProperties) {
        L.d(new Object() {});

        try {
            Presence presence = MyConnectionManager.instance().getRoster().getPresence(JidCreate.bareFrom(userJIDProperties.getJID()));
            return new UserPresence(presence);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getUserContacts() {
        List<User> userContacts = new ArrayList<>();
        Set<RosterEntry> entries = MyConnectionManager.instance().getRoster().getEntries();

        for (RosterEntry entry : entries) {
            if (entry.getType() == RosterPacket.ItemType.to || entry.getType() == RosterPacket.ItemType.both) {
                UserPresence userPresence = new UserPresence(MyConnectionManager.instance().getRoster().getPresence(entry.getJid()));
                User user = new User(new UserJIDProperties(entry.getJid().toString()), entry, userPresence);
                userContacts.add(user);
            }
        }

        return userContacts;
    }

    public List<User> getUserSubscriptions() {
        List<User> userContacts = new ArrayList<>();
        Set<RosterEntry> entries = MyConnectionManager.instance().getRoster().getEntries();

        for (RosterEntry entry : entries) {
            if (entry.getType() == RosterPacket.ItemType.none) {
                UserPresence userPresence = new UserPresence(MyConnectionManager.instance().getRoster().getPresence(entry.getJid()));
                User user = new User(new UserJIDProperties(entry.getJid().toString()), entry, userPresence);
                userContacts.add(user);
            }
        }

        return userContacts;
    }

    public void acceptUserSubscription(User user) {
        try {
            MyConnectionManager.instance().getRoster().createEntry(JidCreate.bareFrom(user.getUserJIDProperties().getNameAndDomain()), user.getUserJIDProperties().getName(), null);
            EventBus.getDefault().post(new RefreshRoster());
        } catch (SmackException.NotLoggedInException | SmackException.NoResponseException | SmackException.NotConnectedException | InterruptedException | XmppStringprepException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
    }

    public void declineUserSubscription(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyConnectionManager.instance().getRoster().removeEntry(user.getRosterEntry());

                    EventBus.getDefault().post(new RefreshRoster());
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
