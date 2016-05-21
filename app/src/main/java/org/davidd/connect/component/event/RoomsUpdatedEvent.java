package org.davidd.connect.component.event;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;

public class RoomsUpdatedEvent {

    private List<MultiUserChat> rooms = new ArrayList<>();

    public RoomsUpdatedEvent() {
    }

    public RoomsUpdatedEvent(List<MultiUserChat> rooms) {
        this.rooms = rooms;
    }

    public List<MultiUserChat> getRooms() {
        return rooms;
    }

    public void setRooms(List<MultiUserChat> rooms) {
        this.rooms = rooms;
    }
}
