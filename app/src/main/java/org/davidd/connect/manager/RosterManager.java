package org.davidd.connect.manager;

import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.events.UserAcceptedStatusEvent;
import org.davidd.connect.manager.events.UserDeclinedStatusEvent;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.model.UserPresence;
import org.davidd.connect.model.UserPresenceType;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
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
public class RosterManager implements RosterListener {

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
            if (entry.getType() == RosterPacket.ItemType.from) {
                UserPresence userPresence = new UserPresence(MyConnectionManager.instance().getRoster().getPresence(entry.getJid()));
                User user = new User(new UserJIDProperties(entry.getJid().toString()), entry, userPresence);
                userContacts.add(user);
            }
        }

        return userContacts;
    }

    public void acceptUserSubscription(User user) {
        try {
            Presence packet = new Presence(Presence.Type.subscribed);
            packet.setTo(JidCreate.bareFrom(user.getUserJIDProperties().getNameAndDomain()));
            MyConnectionManager.instance().getXmppTcpConnection().sendStanza(packet);

            addContact(user);
        } catch (XmppStringprepException | InterruptedException | SmackException.NotConnectedException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new UserAcceptedStatusEvent(user, false));
        }
    }

    public void addContact(User user) {
        try {
            MyConnectionManager.instance().getRoster().createEntry(
                    JidCreate.bareFrom(user.getUserJIDProperties().getNameAndDomain()),
                    user.getUserJIDProperties().getName(),
                    null);

            EventBus.getDefault().post(new RefreshRoster());
            EventBus.getDefault().post(new UserAcceptedStatusEvent(user, true));
        } catch (XmppStringprepException | InterruptedException | SmackException.NotConnectedException | SmackException.NotLoggedInException | SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new UserAcceptedStatusEvent(user, false));
        }
    }

    public void declineUserSubscription(User user) {
        try {
            Presence packet = new Presence(Presence.Type.unsubscribed);
            packet.setTo(JidCreate.bareFrom(user.getUserJIDProperties().getNameAndDomain()));
            MyConnectionManager.instance().getXmppTcpConnection().sendStanza(packet);

            EventBus.getDefault().post(new RefreshRoster());
            EventBus.getDefault().post(new UserDeclinedStatusEvent(user, true));
        } catch (XmppStringprepException | InterruptedException | SmackException.NotConnectedException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new UserDeclinedStatusEvent(user, false));
        }
    }
}
