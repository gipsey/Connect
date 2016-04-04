package org.davidd.connect.model;

import org.jivesoftware.smack.packet.Message;

import java.util.Date;

public class MyMessage {

    private User sender;
    private User receiver;
    private Date date;

    private Message message;

    public MyMessage(User sender, User receiver, Date date, Message message) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
