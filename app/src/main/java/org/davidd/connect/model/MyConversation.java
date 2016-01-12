package org.davidd.connect.model;

import org.jivesoftware.smack.chat.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MyConversation {

    private List<MyMessage> messageList = new ArrayList<>();

    public List<MyMessage> getMessageList() {
        return messageList;
    }
}
