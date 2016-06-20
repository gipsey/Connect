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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveBaseChat baseChat = (ActiveBaseChat) o;

        return myMessage.getEntityToChatWith().equals(baseChat.myMessage.getEntityToChatWith());
    }

    @Override
    public int hashCode() {
        return myMessage.getEntityToChatWith().hashCode();
    }
}
