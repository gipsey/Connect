package org.davidd.connect.component.event;

import org.davidd.connect.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomsUpdatedEvent {

    private List<Room> rooms = new ArrayList<>();

    public RoomsUpdatedEvent() {
    }

    public RoomsUpdatedEvent(List<Room> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);
    }
}
