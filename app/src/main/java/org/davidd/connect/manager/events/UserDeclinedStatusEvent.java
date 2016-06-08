package org.davidd.connect.manager.events;

import org.davidd.connect.model.User;

/**
 *
 */
public class UserDeclinedStatusEvent {

    private User user;
    private boolean success;

    public UserDeclinedStatusEvent(User user, boolean success) {
        this.user = user;
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
