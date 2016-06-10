package org.davidd.connect.manager;

import org.davidd.connect.connection.MyConnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO delete if won't be used
 */
class MessageManager {

    private OfflineMessageManager offlineManager;

    private MessageManager() {
        offlineManager = new OfflineMessageManager(MyConnectionManager.instance().getXmppTcpConnection());
    }

    public List<Message> getAllMessage() {
        List<Message> messages = new ArrayList<>();

        try {
            messages = offlineManager.getMessages();
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | InterruptedException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return messages;
    }
}