package org.davidd.connect.manager;

import org.davidd.connect.model.User;

public class UserManager {

    private static UserManager userManager;
    private User currentUser;

    private UserManager() {
    }

    public static UserManager instance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setUserFromDatabase() {
        if (!isUserSignedIn()) {
            currentUser = PreferencesManager.instance().getUser();
        }
    }

    public boolean isUserSignedIn() {
        return getCurrentUser() != null;
    }

    public void logOut() {
        currentUser = null;
        PreferencesManager.instance().clearUser();
    }
}
