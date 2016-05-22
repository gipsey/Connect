package org.davidd.connect.model;

import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import java.util.Date;

public class MyMessage {

    private User sender;
    private EntityBareJid entityToChatWith; // can be a group or a user
    private Date date;

    private Message message;

    public MyMessage(User sender, EntityBareJid entityToChatWith, Date date, Message message) {
        this.sender = sender;
        this.entityToChatWith = entityToChatWith;
        this.date = date;
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public EntityBareJid getEntityToChatWith() {
        return entityToChatWith;
    }

    public void setEntityToChatWith(EntityBareJid entityToChatWith) {
        this.entityToChatWith = entityToChatWith;
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
