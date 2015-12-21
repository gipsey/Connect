package org.davidd.connect.manager;

import org.davidd.connect.connection.ConnectionConstants;
import org.davidd.connect.debug.Samples;
import org.davidd.connect.model.Conversation;
import org.davidd.connect.model.User;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class UserManager {
    private static UserManager sUserManager;

    private User mCurrentUser;

    private UserManager() {
        mCurrentUser = new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME);
    }

    public static UserManager instance() {
        if (sUserManager == null) {
            sUserManager = new UserManager();
        }
        return sUserManager;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User currentUser) {
        mCurrentUser = currentUser;
    }
}
