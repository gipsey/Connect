package org.davidd.connect.manager;

import org.davidd.connect.debug.Samples;
import org.davidd.connect.model.Conversation;
import org.davidd.connect.model.Message;
import org.davidd.connect.model.User;

import java.util.ArrayList;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class MessageManager {
    private static MessageManager sMessageManager;

    private Conversation mConversation;

    private MessageManager() {
        mConversation = new Conversation();

        mConversation.setMessages(Samples.getSampleConversationList1());
        mConversation.setUsers(Samples.getSampleConversationList1Participants());

//        mConversation.setMessages(new ArrayList<Message>());
//        mConversation.setUsers(new ArrayList<User>());
    }

    public static MessageManager instance() {
        if (sMessageManager == null) {
            sMessageManager = new MessageManager();
        }
        return sMessageManager;
    }

    public Conversation getConversation() {
        return mConversation;
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
    }
}
