package org.davidd.connect.manager;

import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyMultiUserChatManager implements InvitationListener {

    private static MyMultiUserChatManager myMultiUserChatManager;

    private MyMultiUserChatManager() {
    }

    public static MyMultiUserChatManager instance() {
        if (myMultiUserChatManager == null) {
            myMultiUserChatManager = new MyMultiUserChatManager();
        }
        return myMultiUserChatManager;
    }

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {

    }

    public List<DomainBareJid> getMUCServices() {
        List<DomainBareJid> services = new ArrayList<>();

        try {
            services = getMUCManager().getXMPPServiceDomains();
        } catch (XMPPException.XMPPErrorException |
                SmackException.NoResponseException | InterruptedException |
                SmackException.NotConnectedException e) {
            L.ex(e);
        }

        return services;
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
                SmackException.NotConnectedException | MultiUserChatException.NotAMucServiceException e) {
            L.ex(e);
        }

        return hostedRooms;
    }

    public Map<DomainBareJid, List<HostedRoom>> getAllHostedRoomsOnServerByService() {
        List<DomainBareJid> allService = getMUCServices();
        Map<DomainBareJid, List<HostedRoom>> allRoomsByService = new LinkedHashMap<>();

        for (DomainBareJid serviceJid : allService) {
            allRoomsByService.put(serviceJid, getHostedRoomsOfService(serviceJid));
        }

        return allRoomsByService;
    }

    public List<HostedRoom> getAllHostedRoomsOnServer() {
        List<DomainBareJid> allService = getMUCServices();
        List<HostedRoom> allRooms = new ArrayList<>();

        for (DomainBareJid serviceJid : allService) {
            allRooms.addAll(getHostedRoomsOfService(serviceJid));
        }

        return allRooms;
    }

    public DomainBareJid getServiceOfRoom(String localPartOfRoomName) {
        Map<DomainBareJid, List<HostedRoom>> allRoomsByService = getAllHostedRoomsOnServerByService();

        for (DomainBareJid service : allRoomsByService.keySet()) {
            List<HostedRoom> hostedRooms = allRoomsByService.get(service);

            for (HostedRoom room : hostedRooms) {
                if (room.getJid().getLocalpart().toString().equals(localPartOfRoomName)) {
                    return service;
                }
            }
        }

        return null;
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

    /**
     * @param jidOfTheNewRoomAsString should be a valid room name(akarmi@conference.localhost)
     */
    public MultiUserChat createInstantRoom(String jidOfTheNewRoomAsString) throws XmppStringprepException {
        EntityBareJid entityBareJid = JidCreate.entityBareFrom(jidOfTheNewRoomAsString);

        return createInstantRoom(entityBareJid);
    }

    /**
     * @param jidOfTheNewRoom should be a valid room name(akarmi@conference.localhost)
     */
    public MultiUserChat createInstantRoom(EntityBareJid jidOfTheNewRoom) {
        return getMUCManager().getMultiUserChat(jidOfTheNewRoom);
    }

    private MultiUserChatManager getMUCManager() {
        return MyConnectionManager.instance().getMultiUserChatManager();
    }
}
