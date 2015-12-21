package org.davidd.connect.model;

import java.util.Date;

/**
 * @author David Debre
 *         on 2015/12/13
 */
public class Message {
    private User mSender;
    private String mMessage;
    private Date mDate;

    public Message() {
    }

    public Message(User sender, String message) {
        mSender = sender;
        mMessage = message;
    }

    public User getSender() {
        return mSender;
    }

    public void setSender(User sender) {
        mSender = sender;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
