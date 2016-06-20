package org.davidd.connect.db;

import org.davidd.connect.model.MyConversation;
import org.davidd.connect.model.MyMessage;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 *
 */
public class DbManager {

    private static DbManager dbManager;

    private DbManager() {
    }

    public static DbManager instance() {
        if (dbManager == null) {
            dbManager = new DbManager();
        }

        return dbManager;
    }

    // INSERT

    public void saveMessage(MyMessage myMessage) {
        saveMessage(
                myMessage.getType() == MyMessage.Type.NORMAL,
                myMessage.getEntityToChatWith().toString(),
                myMessage.getSender().getUserJIDProperties().getNameAndDomain(),
                myMessage.getMessageBody(),
                myMessage.getDate().getTime());
    }

    public void saveMessage(boolean isNormalChat, String partnerEntityNameAndDomain, String senderNameAndDomain, String messageBody, Long timestampInUtc) {
        Realm realm = Realm.getDefaultInstance();

        RealmConversation conversation = createConversation(realm, partnerEntityNameAndDomain);
        RealmMessage message = createMessage(realm, isNormalChat, partnerEntityNameAndDomain, senderNameAndDomain, messageBody, timestampInUtc);

        realm.beginTransaction();
        conversation.getMessages().add(message);
        realm.commitTransaction();

        realm.close();
    }

    private RealmConversation createConversation(Realm realm, String partnerEntityNameAndDomain) {
        RealmConversation conversation = realm.where(RealmConversation.class).equalTo("partnerEntityNameAndDomain", partnerEntityNameAndDomain).findFirst();

        if (conversation == null) {
            realm.beginTransaction();

            conversation = realm.createObject(RealmConversation.class);

            conversation.setPartnerEntityNameAndDomain(partnerEntityNameAndDomain);

            realm.commitTransaction();
        }

        return conversation;
    }

    private RealmMessage createMessage(Realm realm, boolean isNormalChat, String partnerEntityNameAndDomain, String senderNameAndDomain, String message, Long timestampInUtc) {
        realm.beginTransaction();

        RealmMessage realmMessage = realm.createObject(RealmMessage.class);

        realmMessage.setIsNormalChat(isNormalChat);
        realmMessage.setPartnerEntityNameAndDomain(partnerEntityNameAndDomain);
        realmMessage.setSenderNameAndDomain(senderNameAndDomain);
        realmMessage.setMessage(message);
        realmMessage.setTimestampInUtc(timestampInUtc);

        realm.commitTransaction();

        return realmMessage;
    }

    // QUERY

    public MyConversation getConversation(String partnerEntityNameAndDomain) {
        Realm realm = Realm.getDefaultInstance();

        MyConversation myConversation =
                mapConversation(getRealmConversation(realm, partnerEntityNameAndDomain));

        realm.close();

        return myConversation;
    }

    private RealmConversation getRealmConversation(Realm realm, String partnerEntityNameAndDomain) {
        return realm
                .where(RealmConversation.class)
                .equalTo("partnerEntityNameAndDomain", partnerEntityNameAndDomain).findFirst();
    }

    // DELETE

    public void deleteAllConversation() {
        Realm realm = Realm.getDefaultInstance();

        final RealmResults<RealmConversation> conversations = realm.where(RealmConversation.class).findAll();
        final RealmResults<RealmMessage> messages = realm.where(RealmMessage.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                conversations.deleteAllFromRealm();
                messages.deleteAllFromRealm();
            }
        });

        realm.close();
    }

    // MAPPING

    private MyConversation mapConversation(RealmConversation realmConversation) {
        if (realmConversation == null) {
            return new MyConversation();
        }

        MyConversation myConversation = new MyConversation();

        RealmList<RealmMessage> messages = realmConversation.getMessages();
        for (RealmMessage realmMessage : messages) {
            myConversation.getMessageList().add(mapMessage(realmMessage));
        }

        return myConversation;
    }

    private MyMessage mapMessage(RealmMessage realmMessage) {
        // TODO jani@
        MyMessage.Type type = realmMessage.isNormalChat() ? MyMessage.Type.NORMAL : MyMessage.Type.GROUP;
        User sender = new User(new UserJIDProperties(realmMessage.getSenderNameAndDomain()));
        EntityBareJid entityToChatWith = null;
        try {
            entityToChatWith = JidCreate.entityBareFrom(realmMessage.getPartnerEntityNameAndDomain());
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Date date = new Date(realmMessage.getTimestampInUtc());

        return new MyMessage(type, sender, entityToChatWith, date, realmMessage.getMessage());
    }
}
