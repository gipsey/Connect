package org.davidd.connect.debug;

import org.davidd.connect.connection.ConnectionConstants;
import org.davidd.connect.model.Message;
import org.davidd.connect.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Debre
 *         on 2015/12/20
 */
public class Samples {

    public static List<Message> getSampleConversationList1() {
        List<Message> messages = new ArrayList<>();

        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Szia"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Itt vagy?"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "Hi"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "I'm here..."));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "But I can speak only english"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Szia"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Itt vagy?"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "Hi"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "I'm here..."));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "But I can speak only english"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Szia"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Itt vagy?"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "Hi"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "I'm here..."));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "But I can speak only english"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Szia"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Itt vagy?"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "Hi"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "I'm here..."));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "But I can speak only english"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Szia"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_1_ID,
                ConnectionConstants.MOCK_USER_1_NAME), "Itt vagy?"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "Hi"));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "I'm here..."));
        messages.add(new Message(new User(ConnectionConstants.MOCK_USER_2_ID,
                ConnectionConstants.MOCK_USER_2_NAME), "But I can speak only english"));

        return messages;
    }

    public static List<User> getSampleConversationList1Participants() {
        List<User> users = new ArrayList<>();

        users.add(new User(ConnectionConstants.MOCK_USER_1_ID, ConnectionConstants.MOCK_USER_1_NAME));
        users.add(new User(ConnectionConstants.MOCK_USER_2_ID, ConnectionConstants.MOCK_USER_2_NAME));

        return users;
    }
}
