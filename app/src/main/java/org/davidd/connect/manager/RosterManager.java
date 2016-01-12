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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.util.ArrayList;
import java.util.Collection;
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
    private Set<PresenceChangedListener> presenceChangedListeners = new HashSet<>();
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
    public void entriesAdded(Collection<String> addresses) {
        L.d(new Object() {}, addresses.toString());

        contactsWereUpdated();
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
        L.d(new Object() {}, addresses.toString());

        contactsWereUpdated();
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        L.d(new Object() {}, addresses.toString());

        contactsWereUpdated();
    }

    @Override
    public void presenceChanged(Presence presence) {
        L.d(new Object() {}, presence.toString());

        notifyPresenceChangedListeners(presence);
        contactsWereUpdated();
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

    public void addPresenceChangedListener(PresenceChangedListener listener) {
        L.d(new Object() {});

        if (listener != null) {
            presenceChangedListeners.add(listener);
        }
    }

    public void removePresenceChangedListener(PresenceChangedListener listener) {
        L.d(new Object() {});

        presenceChangedListeners.remove(listener);
    }

    public List<User> getUserContacts() {
        L.d(new Object() {});

        updateUserContacts();
        return userContacts;
    }

    public RosterEntry getRosterEntryForUser(UserJIDProperties userJIDProperties) {
        L.d(new Object() {});

        return roster.getEntry(userJIDProperties.getJID());
    }

    public UserPresence getUserPresenceForUser(UserJIDProperties userJIDProperties) {
        L.d(new Object() {});

        Presence presence = roster.getPresence(userJIDProperties.getJID());
        if (presence == null) {
            return null;
        } else {
            return new UserPresence(roster.getPresence(userJIDProperties.getJID()));
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
                UserPresence userPresence = new UserPresence(roster.getPresence(entry.getUser()));
                User user = new User(new UserJIDProperties(entry.getUser()), entry, userPresence);
                userContacts.add(user);
            }
        }
    }

    private void notifyUserContactsUpdatedListeners() {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (UserContactsUpdatedListener listener : userContactsUpdatedListeners) {
                    listener.userContactsUpdated(userContacts);
                }
            }
        });
    }

    private void notifyPresenceChangedListeners(final Presence presence) {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                for (PresenceChangedListener listener : presenceChangedListeners) {
                    listener.presenceChanged(presence);
                }
            }
        });
    }

    public interface UserContactsUpdatedListener {
        void userContactsUpdated(List<User> userContacts);
    }

    public interface PresenceChangedListener {
        void presenceChanged(Presence presence);
    }
}
