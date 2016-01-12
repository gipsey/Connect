package org.davidd.connect.model;

import android.support.annotation.NonNull;

import java.util.Date;

public class MyMessage {

    private User sender;
    private User receiver;
    private String message;
    private Date date;

    public MyMessage(@NonNull User sender, @NonNull User receiver, @NonNull String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public MyMessage(@NonNull User sender, @NonNull User receiver, @NonNull String message, Date date) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
