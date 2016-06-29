package org.davidd.connect.manager;

import android.support.annotation.Nullable;

import org.davidd.connect.component.event.RoomsUpdatedEvent;
import org.davidd.connect.component.exception.RoomNameExistsException;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MucConfigFormManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;

public class MyMultiUserChatManager {

    public static final String SERVICE = "conference.localhost";
    private static MyMultiUserChatManager myMultiUserChatManager;

    private List<MultiUserChat> chats;

    private MyMultiUserChatManager() {
    }

    public static MyMultiUserChatManager instance() {
        if (myMultiUserChatManager == null) {
            myMultiUserChatManager = new MyMultiUserChatManager();
        }
        return myMultiUserChatManager;
    }

    public DomainBareJid getMUCService() {
        DomainBareJid jid = null;
        try {
            jid = JidCreate.domainBareFrom(SERVICE);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return jid;
    }

    /**
     * @param serviceJidAsString should be a valid service name(conference.localhost)
     */
    public List<HostedRoom> getHostedRoomsOfService(String serviceJidAsString) throws XmppStringprepException {
        DomainBareJid jid = JidCreate.domainBareFrom(serviceJidAsString);

        return getHostedRoomsOfService(jid);
    }

    /**
     * @param serviceJid should be a valid service name(conference.localhost)
     */
    public List<HostedRoom> getHostedRoomsOfService(DomainBareJid serviceJid) {
        List<HostedRoom> hostedRooms = new ArrayList<>();

        try {
            hostedRooms = getMUCManager().getHostedRooms(serviceJid);
        } catch (XMPPException.XMPPErrorException | SmackException.NoResponseException | InterruptedException |
                SmackException.NotConnectedException | MultiUserChatException.NotAMucServiceException | NullPointerException e) {
            L.ex(e);
        }

        {
            String s = "";
            for (HostedRoom r : hostedRooms) {
                s += " " + r.getJid();
            }
            L.d(new Object() {}, "Service = " + serviceJid.toString() + " " + s);
        }

        return hostedRooms;
    }

    public List<HostedRoom> getAllHostedRoomsOnServer() {
        List<HostedRoom> allRoomsByService = new ArrayList<>();

        DomainBareJid service = getMUCService();
        allRoomsByService.addAll(getHostedRoomsOfService(service));

        return allRoomsByService;
    }

    public List<EntityBareJid> getJoinedRoomsOfCurrentUser() {
        List<EntityBareJid> rooms = new ArrayList<>();

        try {
            rooms = getMUCManager().getJoinedRooms(
                    JidCreate.from(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID()).asEntityJidIfPossible());
        } catch (XMPPException.XMPPErrorException | XmppStringprepException |
                SmackException.NoResponseException | InterruptedException |
                SmackException.NotConnectedException e) {
            L.ex(e);
        }

        return rooms;
    }

    public void refreshUserRoomWithOwnerAffiliationAsync() {
        chats = null;
        getUserRoomWithOwnerAffiliationAsync();
    }

    public void getUserRoomWithOwnerAffiliationAsync() {
        if (chats == null || chats.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    chats = getUserRoomWithOwnerAffiliationSync();

                    for (final MultiUserChat chat : chats) {
                        chat.addMessageListener(new MessageListener() {
                            @Override
                            public void processMessage(Message message) {
                                MyChatManager.instance().processMessage(chat, message);
                            }
                        });
                    }

                    EventBus.getDefault().post(new RoomsUpdatedEvent(chats));
                }
            }).start();
        } else {
            EventBus.getDefault().post(new RoomsUpdatedEvent(chats));
        }
    }

    private List<MultiUserChat> getUserRoomWithOwnerAffiliationSync() {
        List<MultiUserChat> localChats = new ArrayList<>();

        List<HostedRoom> allRooms = getAllHostedRoomsOnServer(); // will list all rooms where the user has some privileges

        User me = UserManager.instance().getCurrentUser();

        for (HostedRoom room : allRooms) {
            MultiUserChat chat;
            try {
                chat = getMUCManager().getMultiUserChat(room.getJid());
                chat.join(Resourcepart.from(me.getUserJIDProperties().getJID()));
            } catch (SmackException.NoResponseException | XmppStringprepException |
                    MultiUserChatException.NotAMucServiceException | InterruptedException |
                    SmackException.NotConnectedException | XMPPException.XMPPErrorException | NullPointerException e) {
                e.printStackTrace();
                continue; // this means that the user doesn't have permission to this room
            }

            try {
                Thread.sleep(MyConnectionManager.CONNECTION_AND_PACKET_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String roomJidAndUser = room.getJid() + "/" + me.getUserJIDProperties().getJID();
            Occupant occupant = null;

            try {
                occupant = chat.getOccupant(JidCreate.entityFullFrom(roomJidAndUser));
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }

            if (occupant != null && occupant.getAffiliation() == MUCAffiliation.owner) {
                localChats.add(chat);
            }
        }

        return localChats;
    }

    public boolean roomExists(@Nullable EntityBareJid roomName) {
        List<HostedRoom> allRooms = getAllHostedRoomsOnServer();

        for (HostedRoom room : allRooms) {
            if (room.getJid().equals(roomName)) {
                return true;
            }
        }

        return false;
    }

    public boolean createRoom(String roomName) throws RoomNameExistsException {
        chats = null;

        EntityBareJid roomNameAsEntityJid = convertRoomNameToFullRoomJid(roomName);

        if (roomExists(roomNameAsEntityJid)) {
            throw new RoomNameExistsException();
        }

        try {
            MultiUserChat chat = getMUCManager().getMultiUserChat(roomNameAsEntityJid);
            chat.create(Resourcepart.from(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID()));

            Form answerForm = chat.getConfigurationForm().createAnswerForm();

            answerForm.setAnswer(FormField.FORM_TYPE, "http://jabber.org/protocol/muc#roomconfig");
            answerForm.setAnswer("muc#roomconfig_publicroom", false);
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            answerForm.setAnswer("muc#roomconfig_moderatedroom", false);
            answerForm.setAnswer(MucConfigFormManager.MUC_ROOMCONFIG_PASSWORDPROTECTEDROOM, false);
            answerForm.setAnswer(MucConfigFormManager.MUC_ROOMCONFIG_MEMBERSONLY, true);

            chat.sendConfigurationForm(answerForm);
        } catch (SmackException.NoResponseException | XmppStringprepException | MultiUserChatException.NotAMucServiceException | MultiUserChatException.MissingMucCreationAcknowledgeException | SmackException.NotConnectedException | MultiUserChatException.MucAlreadyJoinedException | InterruptedException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean addUsersToCreatedRoom(EntityBareJid room, List<User> usersList) {
        List<EntityBareJid> users = new ArrayList<>();

        for (User user : usersList) {
            try {
                users.add(JidCreate.entityBareFrom(user.getUserJIDProperties().getJID()));
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                return false;
            }
        }

        return addJidsToCreatedRoom(room, users);
    }

    public boolean addUsersToCreatedRoom(String roomName, List<User> usersList) {
        List<EntityBareJid> users = new ArrayList<>();

        for (User user : usersList) {
            try {
                users.add(JidCreate.entityBareFrom(user.getUserJIDProperties().getJID()));
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                return false;
            }
        }

        return addJidsToCreatedRoom(convertRoomNameToFullRoomJid(roomName), users);
    }

    /**
     * @param room makie sure that room exists
     */
    public boolean addJidsToCreatedRoom(EntityBareJid room, List<EntityBareJid> users) {
        try {
            MultiUserChat muc = getMUCManager().getMultiUserChat(room);
            if (!muc.isJoined()) {
                muc.join(Resourcepart.from(getCurrentUserJidAsString()));
            }
            muc.grantOwnership(users);
        } catch (SmackException.NoResponseException | XmppStringprepException | MultiUserChatException.NotAMucServiceException | InterruptedException | SmackException.NotConnectedException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public EntityBareJid convertRoomNameToFullRoomJid(String roomName) {
        try {
            return JidCreate.entityBareFrom(roomName + "@" + SERVICE);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MultiUserChatManager getMUCManager() {
        return MyConnectionManager.instance().getMultiUserChatManager();
    }

    private String getCurrentUserJidAsString() {
        return UserManager.instance().getCurrentUser().getUserJIDProperties().getJID();
    }

    private EntityBareJid getCurrentUserJid() {
        try {
            return JidCreate.entityBareFrom(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID());
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    getters and setters

    public MultiUserChat getMucByFullName(String roomName) {
        for (MultiUserChat chat : chats) {
            if (chat.getRoom().toString().equals(roomName)) {
                return chat;
            }
        }

        return null;
    }
}
