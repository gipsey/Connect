package org.davidd.connect.model;

import org.jxmpp.jid.EntityBareJid;

import java.util.Date;

public class MyMessage {

    private Type type;
    private User sender;
    private EntityBareJid entityToChatWith; // can be a group or a user
    private Date date;
    private String messageBody;

    public MyMessage(User sender, EntityBareJid entityToChatWith, Date date, String messageBody) {
        this(Type.NORMAL, sender, entityToChatWith, date, messageBody);
    }

    public MyMessage(Type type, User sender, EntityBareJid entityToChatWith, Date date, String messageBody) {
        this.type = type;
        this.sender = sender;
        this.entityToChatWith = entityToChatWith;
        this.date = date;
        this.messageBody = messageBody;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public enum Type {
        NORMAL("normal"),
        GROUP("group");

        private String value;

        Type(String value) {
            this.value = value;
        }
    }
}
