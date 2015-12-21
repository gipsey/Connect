package org.davidd.connect.model;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class User {
    private String mId;
    private String mUsername;

    public User(String id, String username) {
        mId = id;
        mUsername = username;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }
}
