package org.davidd.connect.model;

import org.jivesoftware.smackx.muc.MultiUserChat;

public class Room {

    private MultiUserChat muc;

    public Room() {
    }

    public Room(MultiUserChat muc) {
        this.muc = muc;
    }

    public MultiUserChat getMuc() {
        return muc;
    }

    public void setMuc(MultiUserChat muc) {
        this.muc = muc;
    }
}
