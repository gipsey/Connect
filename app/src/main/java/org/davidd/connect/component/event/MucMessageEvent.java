package org.davidd.connect.component.event;

import org.davidd.connect.model.MyMessage;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class MucMessageEvent {

    private MultiUserChat muc;
    private MyMessage myMessage;

    public MucMessageEvent() {
    }

    public MucMessageEvent(MultiUserChat muc, MyMessage myMessage) {
        this.muc = muc;
        this.myMessage = myMessage;
    }

    public MultiUserChat getMuc() {
        return muc;
    }

    public void setMuc(MultiUserChat muc) {
        this.muc = muc;
    }

    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }
}
