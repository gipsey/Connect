package org.davidd.connect.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.MyConversation;
import org.davidd.connect.model.MyMessage;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.util.DataUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyChatManager implements ChatManagerListener, ChatMessageListener {

    public interface MessageReceivedListener {
        void messageReceived(Message message);
    }

    private static MyChatManager myChatManager;

    private Map<String, MyConversation> conversationsByUserNameAndDomain = new HashMap<>();
    private Map<String, MessageReceivedListener> messageReceivedListenersByUserNameAndDomain = new HashMap<>();

    private MyChatManager() {
    }

    public static MyChatManager instance() {
        if (myChatManager == null) {
            myChatManager = new MyChatManager();
        }
        return myChatManager;
    }

    public static boolean isValidChatMessage(Message message) {
        return (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal)
                && !DataUtils.isEmpty(message.getBody());
    }

    public void addMessageReceivedListener(User userToChatWith, MessageReceivedListener listener) {
        if (listener != null) {
            messageReceivedListenersByUserNameAndDomain.put(userToChatWith.getUserJIDProperties().getNameAndDomain(), listener);
        }
    }

    public void removeMessageReceivedListener(User userToChatWith) {
        messageReceivedListenersByUserNameAndDomain.remove(userToChatWith.getUserJIDProperties().getNameAndDomain());
    }

    @Nullable
    public MyMessage sendMessage(@NonNull User userToChatWith, String messageBody) {
        Message message = new Message();
        message.setType(Message.Type.chat);
        message.setBody(messageBody);
        message.setFrom(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID());
        message.setTo(userToChatWith.getUserJIDProperties().getJID());

        try {
            Chat chat = getChatManager().createChat(userToChatWith.getUserJIDProperties().getJID());
            chat.sendMessage(message);

            Date date = DataUtils.getCurrentDate();

            saveMessageInConversationHistory(chat, message, date);

            return new MyMessage(
                    UserManager.instance().getCurrentUser(),
                    userToChatWith,
                    messageBody,
                    date);
        } catch (SmackException.NotConnectedException e) {
            L.e(new Object() {}, e.getMessage());
            return null;
        }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        L.d(new Object() {}, "Chat created to: " + chat.getParticipant() + ", locally: " + createdLocally);

        chat.addMessageListener(this);

        String userNameAndDomain = getUserNameAndDomain(chat);
        if (!conversationsByUserNameAndDomain.containsKey(userNameAndDomain)) {
            conversationsByUserNameAndDomain.put(userNameAndDomain, new MyConversation());
        }

        messagesUpdated();
    }

    /**
     * Create manager-level message listener in order to save new messages
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        L.d(new Object() {}, "Message from: " + chat.getParticipant());

        saveMessageInConversationHistory(chat, message, DataUtils.getCurrentDate());
        notifyMessageReceivedListener(getSenderUser(chat, message), message);
    }

    private void saveMessageInConversationHistory(Chat chat, Message message, Date date) {
        if (isValidChatMessage(message)) {
            User sender = getSenderUser(chat, message);
            MyMessage myMessage = new MyMessage(sender, getReceiverUser(message), message.getBody(), date);

            conversationsByUserNameAndDomain.get(sender.getUserJIDProperties().getNameAndDomain()).getMessageList().add(myMessage);

            messagesUpdated();
        }
    }

    private void notifyMessageReceivedListener(User senderUser, final Message message) {
        final MessageReceivedListener listener =
                messageReceivedListenersByUserNameAndDomain.get(senderUser.getUserJIDProperties().getNameAndDomain());

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.messageReceived(message);
                }
            }
        });
    }

    /**
     * Called when messages were updated or new chat was created.
     */
    private void messagesUpdated() {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                // update active chats list

                // update notification list
                // MyNotificationManager.instance().showNewMessageNotification(getActivity(), contactsExpandableListAdapter.getUser(groupPosition, childPosition), "click");
            }
        });
    }

    private User getSenderUser(Chat chat, Message message) {
        if (!DataUtils.isEmpty(message.getFrom())) {
            return new User(new UserJIDProperties(message.getFrom()));
        } else {
            return new User(new UserJIDProperties(chat.getParticipant()));
        }
    }

    private User getReceiverUser(Message message) {
        if (!DataUtils.isEmpty(message.getTo())) {
            return new User(new UserJIDProperties(message.getTo()));
        } else {
            return UserManager.instance().getCurrentUser();
        }
    }

    private String getUserNameAndDomain(Chat chat) {
        return new UserJIDProperties(chat.getParticipant()).getNameAndDomain();
    }

    private ChatManager getChatManager() {
        return MyConnectionManager.instance().getChatManager();
    }
}
