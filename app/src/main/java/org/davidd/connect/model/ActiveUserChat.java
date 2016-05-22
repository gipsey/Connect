package org.davidd.connect.model;

public class ActiveUserChat extends ActiveBaseChat {

    private User userToChatWith;

    public ActiveUserChat(MyMessage myMessage, User userToChatWith) {
        super(myMessage);
        this.userToChatWith = userToChatWith;
    }

    public User getUserToChatWith() {
        return userToChatWith;
    }

    public void setUserToChatWith(User userToChatWith) {
        this.userToChatWith = userToChatWith;
    }
}
