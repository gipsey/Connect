package org.davidd.connect.model;

public class ActiveChat {

    private User sender;
    private String lastMessage;

    public ActiveChat(User sender, String lastMessage) {
        this.sender = sender;
        this.lastMessage = lastMessage;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
