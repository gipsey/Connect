package org.davidd.connect.manager;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.connection.ErrorMessage;
import org.davidd.connect.connection.MyConnectionListener;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.connection.MyDisconnectionListener;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.model.UserPresence;
import org.davidd.connect.model.UserPresenceType;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class RosterManager implements RosterListener, MyConnectionListener, MyDisconnectionListener {

    private static RosterManager rosterManager;

    private List<User> userContacts = new ArrayList<>();
    private Set<UserContactsUpdatedListener> userContactsUpdatedListeners = new HashSet<>();
    private Roster roster;

    private RosterManager() {
        MyConnectionManager.instance().addConnectionListener(this);
        MyConnectionManager.instance().addDisconnectionListener(this);
    }

    public static RosterManager instance() {
        if (rosterManager == null) {
            rosterManager = new RosterManager();
        }
        return rosterManager;
    }

    @Override
    public void onConnectionSuccess() {
        L.d(new Object() {});

        if (roster == null) {
            roster = Roster.getInstanceFor(MyConnectionManager.instance().getXmppTcpConnection());
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            roster.addRosterListener(this);
        }
    }

    @Override
    public void onConnectionFailed(ErrorMessage message) {
    }

    @Override
    public void onAuthenticationSuccess() {
    }

    @Override
    public void onAuthenticationFailed(ErrorMessage message) {
    }

    @Override
    public void onDisconnect() {
        L.d(new Object() {});

        if (roster != null) {
            roster.removeRosterListener(this);
            roster = null;
        }
    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        L.d(new Object() {}, addresses.toString());

        contactsWereUpdated();
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        L.d(new Object() {}, addresses.toString());

        contactsWereUpdated();
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        L.d(new Object() {}, addresses.toString());

        contactsWereUpdated();
    }

    @Override
    public void presenceChanged(Presence presence) {
        L.d(new Object() {}, presence.toString());

        final User user = new User(new UserJIDProperties(presence.getFrom().toString()));
        user.setUserPresence(getUserPresenceForUser(user.getUserJIDProperties()));
        EventBus.getDefault().post(new UserPresenceChangedMessage(user));

        contactsWereUpdated();
    }

    public void sendPresence(UserPresenceType userPresence, String status) throws Exception {
        Presence.Type type = null;
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

    public void addUserContactsUpdatedListener(UserContactsUpdatedListener listener) {
        L.d(new Object() {});

        if (listener != null) {
            userContactsUpdatedListeners.add(listener);
        }
    }

    public void removeUserContactsUpdatedListener(UserContactsUpdatedListener listener) {
        L.d(new Object() {});

        userContactsUpdatedListeners.remove(listener);
    }

    public List<User> getUserContacts() {
        L.d(new Object() {});

        updateUserContacts();
        return userContacts;
    }

    public RosterEntry getRosterEntryForUser(UserJIDProperties userJIDProperties) {
        L.d(new Object() {});

        try {
            return roster.getEntry(JidCreate.bareFrom(userJIDProperties.getJID()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserPresence getUserPresenceForUser(UserJIDProperties userJIDProperties) {
        L.d(new Object() {});

        try {
            Presence presence = roster.getPresence(JidCreate.bareFrom(userJIDProperties.getJID()));
            return new UserPresence(presence);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void contactsWereUpdated() {
        L.d(new Object() {});

        updateUserContacts();
        notifyUserContactsUpdatedListeners();
    }

    private void updateUserContacts() {
        userContacts.clear();
        Set<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {
            if (entry.getType() == RosterPacket.ItemType.to || entry.getType() == RosterPacket.ItemType.both) {
                UserPresence userPresence = new UserPresence(roster.getPresence(entry.getJid()));
                User user = new User(new UserJIDProperties(entry.getJid().toString()), entry, userPresence);
                userContacts.add(user);
            }
        }
    }

    private void notifyUserContactsUpdatedListeners() {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (UserContactsUpdatedListener listener : userContactsUpdatedListeners) {
                    listener.userContactsUpdated(Collections.unmodifiableList(userContacts));
                }
            }
        });
    }

    public interface UserContactsUpdatedListener {
        void userContactsUpdated(List<User> userContacts);
    }
}
