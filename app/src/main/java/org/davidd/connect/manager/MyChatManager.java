package org.davidd.connect.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.component.event.ChatMessageEvent;
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
import java.util.List;

public class MyChatManager implements ChatManagerListener, ChatMessageListener {

    private static MyChatManager myChatManager;
    private List<ActiveBaseChat> activeBaseChats = new ArrayList<>();

    private MyChatManager() {
    }

    public static MyChatManager instance() {
        if (myChatManager == null) {
            myChatManager = new MyChatManager();
        }
        return myChatManager;
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
            chatsUpdated(myMessage, userToChatWith);

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

            chatsUpdated(myMessage, muc);

            return myMessage;
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            L.ex(e);
        }
        return null;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        L.d(new Object() {}, "Chat created to: " + chat.getParticipant() + ", locally: " + createdLocally);
        chat.addMessageListener(this);
    }

    /**
     * Create manager-level message listener in order to save new messages
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        L.d(new Object() {}, "Message from = " + chat.getParticipant());

        if (message.getType() != Message.Type.normal && message.getType() != Message.Type.chat) {
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

        EventBus.getDefault().post(new ChatMessageEvent(myMessage));

        chatsUpdated(myMessage, partner);
    }

    public void processMessage(MultiUserChat muc, Message message) {
        L.d(new Object() {}, "Messag" +
                "e from = " + muc.getRoom().toString());

        if (message.getType() != Message.Type.groupchat) {
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

        chatsUpdated(myMessage, muc);
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

    private void chatsUpdated(final MyMessage myMessage, final User partner) {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                partner.setRosterEntry(RosterManager.instance().getRosterEntryForUser(partner.getUserJIDProperties()));
                partner.setUserPresence(RosterManager.instance().getUserPresenceForUser(partner.getUserJIDProperties()));

                ActiveUserChat activeUserChat = new ActiveUserChat(myMessage, partner);
                refreshActiveChatsList(activeUserChat);

                EventBus.getDefault().post(Collections.unmodifiableList(activeBaseChats));

                MyNotificationManager.instance().newMessageProcessed(myMessage);
            }
        });
    }

    private void chatsUpdated(final MyMessage myMessage, final MultiUserChat muc) {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                ActiveRoomChat roomChat = new ActiveRoomChat(myMessage, muc);
                refreshActiveChatsList(roomChat);

                EventBus.getDefault().post(Collections.unmodifiableList(activeBaseChats));

                MyNotificationManager.instance().newMessageProcessed(myMessage);
            }
        });
    }

    private void refreshActiveChatsList(ActiveBaseChat activeBaseChat) {
        Integer itemPosition = null;
        for (int i = 0; i < activeBaseChats.size(); i++) {
            if (activeBaseChat.equals(activeBaseChats.get(i))) {
                itemPosition = i;
                break;
            }
        }

        if (itemPosition == null) {
            activeBaseChats.add(activeBaseChat);
        } else {
            activeBaseChats.remove(itemPosition.intValue());
            activeBaseChats.add(itemPosition, activeBaseChat);
        }
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
}
