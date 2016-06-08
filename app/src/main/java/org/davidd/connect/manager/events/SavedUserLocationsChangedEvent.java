package org.davidd.connect.manager.events;

import org.davidd.connect.model.User;

public class SavedUserLocationsChangedEvent {

    public User user;

    public SavedUserLocationsChangedEvent(User user) {
        this.user = user;
    }
}
