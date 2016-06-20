package org.davidd.connect.component.event;

import org.davidd.connect.model.MyMessage;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class ChatMessageEvent {

    private MyMessage myMessage;

    public ChatMessageEvent() {
    }

    public ChatMessageEvent(MyMessage myMessage) {
        this.myMessage = myMessage;
    }

    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }
}
