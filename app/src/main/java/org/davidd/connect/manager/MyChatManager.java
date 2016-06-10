package org.davidd.connect.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.component.event.MucMessageEvent;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.db.DbManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.ActiveBaseChat;
import org.davidd.connect.model.ActiveRoomChat;
import org.davidd.connect.model.ActiveUserChat;
import org.davidd.connect.model.MyConversation;
import org.davidd.connect.model.MyMessage;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.util.DataUtils;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatManager implements ChatManagerListener, ChatMessageListener {

    private static MyChatManager myChatManager;
    private List<ActiveBaseChat> activeBaseChats = new ArrayList<>();

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

            EntityBareJid partnerEntityBareJid = JidCreate.entityBareFrom(userToChatWith.getUserJIDProperties().getNameAndDomain());

            MyMessage myMessage = new MyMessage(
                    UserManager.instance().getCurrentUser(),
                    partnerEntityBareJid,
                    DataUtils.getCurrentDate(),
                    message.getBody());

            persistConversation(myMessage);

            return myMessage;
        } catch (SmackException.NotConnectedException | XmppStringprepException | InterruptedException e) {
            // TODO show an error message when this happens
            L.ex(e);
        }
        return null;
    }

    @Nullable
    public MyMessage sendMessage(@NonNull MultiUserChat muc, String messageBody) {
        L.d(new Object() {});

        Message message = new Message();
        message.setType(Message.Type.groupchat);
        message.setBody(messageBody);
        message.setFrom(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID());
        message.setTo(muc.getRoom());

        try {
            muc.sendMessage(message);

            L.d(new Object() {}, "Group message sent to: " + muc.getRoom() + ", message: " + message.getBody());

            MyMessage myMessage = new MyMessage(
                    MyMessage.Type.GROUP,
                    UserManager.instance().getCurrentUser(),
                    muc.getRoom(),
                    DataUtils.getCurrentDate(),
                    message.getBody());

            persistConversation(myMessage);

            return myMessage;
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            // TODO show an error message when this happens
            L.ex(e);
        }
        return null;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        L.d(new Object() {}, "Chat created to: " + chat.getParticipant() + ", locally: " + createdLocally);

        chat.addMessageListener(this);

        User participant = getPartnerUser(chat, null);
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

        if (!message.getType().equals(Message.Type.normal) && !message.getType().equals(Message.Type.chat)) {
            return;
        }

        if (TextUtils.isEmpty(message.getBody())) {
            return;
        }

        User partner = getPartnerUser(chat, message);
        EntityBareJid partnerEntityBareJid = null;
        try {
            partnerEntityBareJid = JidCreate.entityBareFrom(partner.getUserJIDProperties().getNameAndDomain());
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        MyMessage myMessage = new MyMessage(
                partner,
                partnerEntityBareJid,
                DataUtils.getCurrentDate(),
                message.getBody());

        persistConversation(myMessage);
        notifyMessageReceivedListeners(partner, myMessage);

        chatsUpdated(partner);
    }

    public void processMessage(MultiUserChat muc, Message message) {
        L.d(new Object() {}, "Message from = " + muc.getRoom().toString());

        if (!message.getType().equals(Message.Type.groupchat)) {
            return;
        }

        if (TextUtils.isEmpty(message.getBody())) {
            return;
        }

        User sender = new User(new UserJIDProperties(message.getFrom().getResourceOrNull().toString()));

        MyMessage myMessage = new MyMessage(
                MyMessage.Type.GROUP,
                sender,
                muc.getRoom(),
                DataUtils.getCurrentDate(),
                message.getBody());

        persistConversation(myMessage);

        EventBus.getDefault().post(new MucMessageEvent(muc, myMessage));

        // update active chats
        Integer itemPosition = null;
        for (int i = 0; i < activeBaseChats.size(); i++) {
            if (activeBaseChats.get(i) instanceof ActiveRoomChat) {
                ActiveRoomChat roomChat = (ActiveRoomChat) activeBaseChats.get(i);
                if (roomChat.getMultiUserChat().getRoom().equals(muc.getRoom())) {
                    itemPosition = i;
                    break;
                }
            }
        }

        ActiveRoomChat roomChat = new ActiveRoomChat(myMessage, muc);

        if (itemPosition == null) {
            activeBaseChats.add(roomChat);
        } else {
            activeBaseChats.remove(itemPosition.intValue());
            activeBaseChats.add(itemPosition, roomChat);
        }

        for (ChatUpdatedListener listener : chatUpdatedListeners) {
            listener.chatsUpdated(Collections.unmodifiableList(activeBaseChats));
        }
    }

    public List<ActiveBaseChat> getActiveBaseChats() {
        return Collections.unmodifiableList(activeBaseChats);
    }

    private void persistConversation(MyMessage myMessage) {
        DbManager.instance().saveMessage(myMessage);
    }

    public MyConversation loadConversation(String partnerEntityNameAndDomain) {
        return DbManager.instance().getConversation(partnerEntityNameAndDomain);
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
                        DbManager.instance().getConversation(participant.getUserJIDProperties().getNameAndDomain());

                participant.setRosterEntry(RosterManager.instance().getRosterEntryForUser(participant.getUserJIDProperties()));
                participant.setUserPresence(RosterManager.instance().getUserPresenceForUser(participant.getUserJIDProperties()));

                MyMessage myMessage = null;
                if (!myConversation.getMessageList().isEmpty()) {
                    myMessage = myConversation.getMessageList().get(myConversation.getMessageList().size() - 1);
                }
                ActiveUserChat activeUserChat = new ActiveUserChat(myMessage, participant);

                Integer itemPosition = null;
                for (int i = 0; i < activeBaseChats.size(); i++) {
                    if (activeBaseChats.get(i) instanceof ActiveUserChat) {
                        ActiveUserChat userChat = (ActiveUserChat) activeBaseChats.get(i);
                        if (userChat.getUserToChatWith().equals(activeUserChat.getUserToChatWith())) {
                            itemPosition = i;
                            break;
                        }
                    }
                }

                if (itemPosition == null) {
                    activeBaseChats.add(activeUserChat);
                } else {
                    activeBaseChats.remove(itemPosition.intValue());
                    activeBaseChats.add(itemPosition, activeUserChat);
                }

                for (ChatUpdatedListener listener : chatUpdatedListeners) {
                    listener.chatsUpdated(Collections.unmodifiableList(activeBaseChats));
                }

                // TODO: update notification list
                // MyNotificationManager.instance().showNewMessageNotification(getActivity(), contactsExpandableListAdapter.getUser(groupPosition, childPosition), "click");
            }
        });
    }

    private User getPartnerUser(Chat chat, Message message) {
        if (message != null && !DataUtils.isEmpty(message.getFrom())) {
            return new User(new UserJIDProperties(message.getFrom().toString()));
        } else {
            return new User(new UserJIDProperties(chat.getParticipant().toString()));
        }
    }

    private ChatManager getChatManager() {
        return MyConnectionManager.instance().getChatManager();
    }

    public interface MessageReceivedListener {
        void messageReceived(MyMessage myMessage);
    }

    public interface ChatUpdatedListener {
        void chatsUpdated(List<ActiveBaseChat> activeUserChats);
    }
}
