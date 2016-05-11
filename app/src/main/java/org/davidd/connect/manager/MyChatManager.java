package org.davidd.connect.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.ActiveChat;
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
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatManager implements ChatManagerListener, ChatMessageListener {

    private static MyChatManager myChatManager;

    // the used key is the participant user's name and domain
    private Map<String, MyConversation> conversations = new HashMap<>(); // TODO: make persistent
    private List<ActiveChat> activeChats = new ArrayList<>(); // TODO: make persistent
    // the used key is the user's name and domain
    private Map<String, List<MessageReceivedListener>> messageListeners = new HashMap<>();
    private List<ChatUpdatedListener> chatUpdatedListeners = new ArrayList<>();

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

    public void addChatUpdatedListener(ChatUpdatedListener listener) {
        if (listener != null && !chatUpdatedListeners.contains(listener)) {
            chatUpdatedListeners.add(listener);
        }
    }

    public void removeChatUpdatedListener(ChatUpdatedListener listener) {
        chatUpdatedListeners.remove(listener);
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
            Chat chat = getChatManager().createChat(JidCreate.from(userToChatWith.getUserJIDProperties().getJID()).asEntityJidIfPossible());
            chat.sendMessage(message);

            L.d(new Object() {}, "Message sent to: " + userToChatWith.getUserJIDProperties().getJID()
                    + ", message: " + message.getBody());

            MyMessage myMessage = new MyMessage(
                    UserManager.instance().getCurrentUser(),
                    userToChatWith,
                    DataUtils.getCurrentDate(),
                    message);

            saveMessageHistory(userToChatWith, myMessage);

            return myMessage;
        } catch (SmackException.NotConnectedException | XmppStringprepException | InterruptedException e) {
            // TODO show an error message when this happens
            L.ex(e);
        }
        return null;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        L.d(new Object() {}, "Chat created to: " + chat.getParticipant() + ", locally: " + createdLocally);

        chat.addMessageListener(this);

        User participant = getParticipantUser(chat, null);
        if (!conversations.containsKey(participant.getUserJIDProperties().getNameAndDomain())) {
            conversations.put(participant.getUserJIDProperties().getNameAndDomain(), new MyConversation());
        }

        if (createdLocally) {
            chatsUpdated(participant);
        }
    }

    /**
     * Create manager-level message listener in order to save new messages
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        L.d(new Object() {}, "Message from = " + chat.getParticipant());

        User participant = getParticipantUser(chat, message);

        MyMessage myMessage = new MyMessage(
                participant,
                UserManager.instance().getCurrentUser(),
                DataUtils.getCurrentDate(),
                message);

        saveMessageHistory(participant, myMessage);
        notifyMessageReceivedListeners(participant, myMessage);

        chatsUpdated(participant);
    }

    public List<ActiveChat> getActiveChats() {
        return Collections.unmodifiableList(activeChats);
    }

    private void saveMessageHistory(User participant, MyMessage myMessage) {
        L.d(new Object() {});

        if (isMessage(myMessage.getMessage())) {
            L.d(new Object() {}, "Saving message: " + myMessage.getMessage().getBody() + ", from : " + myMessage.getMessage().getFrom() + ", to: " + myMessage.getMessage().getTo());

            conversations.get(participant.getUserJIDProperties().getNameAndDomain()).getMessageList().add(myMessage);
        }
    }

    private void notifyMessageReceivedListeners(final User senderUser, final MyMessage myMessage) {
        L.d(new Object() {});

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                List<MessageReceivedListener> listenerList =
                        messageListeners.get(senderUser.getUserJIDProperties().getNameAndDomain());

                if (listenerList != null) {
                    for (MessageReceivedListener listener : listenerList) {
                        listener.messageReceived(myMessage);
                    }
                }
            }
        });
    }

    /**
     * Called when messages were updated or new chats were created.
     */
    private void chatsUpdated(final User participant) {
        L.d(new Object() {});

        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                // update active chats list
                MyConversation myConversation =
                        conversations.get(participant.getUserJIDProperties().getNameAndDomain());

                participant.setRosterEntry(RosterManager.instance().getRosterEntryForUser(participant.getUserJIDProperties()));
                participant.setUserPresence(RosterManager.instance().getUserPresenceForUser(participant.getUserJIDProperties()));

                MyMessage myMessage = null;
                if (!myConversation.getMessageList().isEmpty()) {
                    myMessage = myConversation.getMessageList().get(myConversation.getMessageList().size() - 1);
                }
                ActiveChat activeChat = new ActiveChat(participant, myMessage);

                Integer itemPosition = getActiveChatPosition(activeChat);
                if (itemPosition == null) {
                    activeChats.add(activeChat);
                } else {
                    activeChats.remove(itemPosition.intValue());
                    activeChats.add(itemPosition, activeChat);
                }

                for (ChatUpdatedListener listener : chatUpdatedListeners) {
                    listener.chatsUpdated(Collections.unmodifiableList(activeChats));
                }

                // update notification list
                // MyNotificationManager.instance().showNewMessageNotification(getActivity(), contactsExpandableListAdapter.getUser(groupPosition, childPosition), "click");
            }
        });
    }

    private User getParticipantUser(Chat chat, Message message) {
        if (message != null && !DataUtils.isEmpty(message.getFrom())) {
            return new User(new UserJIDProperties(message.getFrom().toString()));
        } else {
            return new User(new UserJIDProperties(chat.getParticipant().toString()));
        }
    }

    private User getReceiverUser(Message message) {
        if (!DataUtils.isEmpty(message.getTo())) {
            return new User(new UserJIDProperties(message.getTo().toString()));
        } else {
            return UserManager.instance().getCurrentUser();
        }
    }

    private boolean isMessage(Message message) {
        return (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal)
                && !DataUtils.isEmpty(message.getBody());
    }

    private Integer getActiveChatPosition(ActiveChat activeChat) {
        for (int i = 0; i < activeChats.size(); i++) {
            if (activeChats.get(i).getUserToChatWith().equals(activeChat.getUserToChatWith())) {
                return i;
            }
        }
        return null;
    }

    private ChatManager getChatManager() {
        return MyConnectionManager.instance().getChatManager();
    }

    public interface MessageReceivedListener {
        void messageReceived(MyMessage myMessage);
    }

    public interface ChatUpdatedListener {
        void chatsUpdated(List<ActiveChat> activeChats);
    }
}
