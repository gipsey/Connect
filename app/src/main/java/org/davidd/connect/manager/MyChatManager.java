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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatManager implements ChatManagerListener, ChatMessageListener {

    private static MyChatManager myChatManager;
    // the used key is the user's name and domain
    private Map<String, MyConversation> conversations = new HashMap<>();
    // the used key is the user's name and domain
    private Map<String, List<MessageReceivedListener>> messageListeners = new HashMap<>();

    private MyChatManager() {
    }

    public static MyChatManager instance() {
        if (myChatManager == null) {
            myChatManager = new MyChatManager();
        }
        return myChatManager;
    }

    public void addMessageReceivedListener(User userToChatWith, MessageReceivedListener listener) {
        if (listener != null) {
            List<MessageReceivedListener> listenerList =
                    messageListeners.get(userToChatWith.getUserJIDProperties().getNameAndDomain());

            if (listenerList == null) {
                listenerList = new ArrayList<>();
            }

            listenerList.add(listener);

            messageListeners.put(userToChatWith.getUserJIDProperties().getNameAndDomain(), listenerList);
        }
    }

    public void removeMessageReceivedListener(User userToChatWith, MessageReceivedListener listener) {
        List<MessageReceivedListener> listenerList =
                messageListeners.get(userToChatWith.getUserJIDProperties().getNameAndDomain());

        listenerList.remove(listener);
        messageListeners.put(userToChatWith.getUserJIDProperties().getNameAndDomain(), listenerList);
    }

    @Nullable
    public MyMessage sendMessage(@NonNull User userToChatWith, String messageBody) {
        L.d(new Object() {});

        Message message = new Message();
        message.setType(Message.Type.chat);
        message.setBody(messageBody);
        message.setFrom(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID());
        message.setTo(userToChatWith.getUserJIDProperties().getJID());

        try {
            Chat chat = getChatManager().createChat(userToChatWith.getUserJIDProperties().getJID());
            chat.sendMessage(message);

            L.d(new Object() {}, "Message sent to: " + userToChatWith.getUserJIDProperties().getJID() + ", message: " +
                    message.getBody());

            Date date = DataUtils.getCurrentDate();

            saveMessageHistory(getSenderUser(chat, message), message, date);

            return new MyMessage(
                    UserManager.instance().getCurrentUser(),
                    userToChatWith,
                    messageBody,
                    date);

        } catch (SmackException.NotConnectedException e) {
            L.ex(e);
        }
        return null;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        L.d(new Object() {}, "Chat created to: " + chat.getParticipant() + ", locally: " + createdLocally);

        chat.addMessageListener(this);

        String userNameAndDomain = getUserNameAndDomain(chat);
        if (!conversations.containsKey(userNameAndDomain)) {
            conversations.put(userNameAndDomain, new MyConversation());
        }

        chatsUpdated();
    }

    /**
     * Create manager-level message listener in order to save new messages
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        L.d(new Object() {}, "Message from: " + chat.getParticipant());

        User sender = getSenderUser(chat, message);

        saveMessageHistory(sender, message, DataUtils.getCurrentDate());
        notifyMessageReceivedListeners(sender, message);

        chatsUpdated();
    }

    private void saveMessageHistory(User sender, Message message, Date date) {
        L.d(new Object() {});

        if (isMessage(message)) {
            L.d(new Object() {}, "Saving message: " + message.getBody() + ", from : " + sender.getUserJIDProperties().getNameAndDomain());

            MyMessage myMessage = new MyMessage(sender, getReceiverUser(message), message.getBody(), date);

            conversations.get(sender.getUserJIDProperties().getNameAndDomain()).getMessageList().add(myMessage);
        }
    }

    private void notifyMessageReceivedListeners(final User senderUser, final Message message) {
        L.d(new Object() {});

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                List<MessageReceivedListener> listenerList =
                        messageListeners.get(senderUser.getUserJIDProperties().getNameAndDomain());

                if (listenerList != null) {
                    for (MessageReceivedListener listener : listenerList) {
                        listener.messageReceived(message);
                    }
                }
            }
        });
    }

    /**
     * Called when messages were updated or new chats were created.
     */
    private void chatsUpdated() {
        L.d(new Object() {});

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

    private boolean isMessage(Message message) {
        return (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal)
                && !DataUtils.isEmpty(message.getBody());
    }

    private ChatManager getChatManager() {
        return MyConnectionManager.instance().getChatManager();
    }

    public interface MessageReceivedListener {
        void messageReceived(Message message);
    }
}
