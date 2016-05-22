package org.davidd.connect.model;

public abstract class ActiveBaseChat {

    private MyMessage myMessage;

    public ActiveBaseChat(MyMessage myMessage) {
        this.myMessage = myMessage;
    }

    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }
}
