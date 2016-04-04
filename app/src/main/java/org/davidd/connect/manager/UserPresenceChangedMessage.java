package org.davidd.connect.manager;

import org.davidd.connect.model.User;

public class UserPresenceChangedMessage {

    private final User user;

    public UserPresenceChangedMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
