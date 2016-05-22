package org.davidd.connect.model;

import org.jivesoftware.smackx.muc.MultiUserChat;

public class ActiveRoomChat extends ActiveBaseChat {

    private MultiUserChat multiUserChat;

    public ActiveRoomChat(MyMessage myMessage, MultiUserChat multiUserChat) {
        super(myMessage);
        this.multiUserChat = multiUserChat;
    }

    public MultiUserChat getMultiUserChat() {
        return multiUserChat;
    }

    public void setMultiUserChat(MultiUserChat multiUserChat) {
        this.multiUserChat = multiUserChat;
    }
}
