package org.davidd.connect.model;

public class ActiveChat {

    private User userToChatWith;
    private MyMessage myMessage;

    public ActiveChat(User userToChatWith, MyMessage myMessage) {
        this.userToChatWith = userToChatWith;
        this.myMessage = myMessage;
    }

    public User getUserToChatWith() {
        return userToChatWith;
    }

    public void setUserToChatWith(User userToChatWith) {
        this.userToChatWith = userToChatWith;
    }

    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }
}
