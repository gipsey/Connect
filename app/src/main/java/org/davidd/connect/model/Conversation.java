package org.davidd.connect.model;

import org.davidd.connect.debug.Samples;

import java.util.List;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class Conversation {
    private List<Message> mMessages;
    private List<User> mUsers;

    public Conversation() {
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        mUsers = users;
    }
}
