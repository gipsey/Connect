package org.davidd.connect.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 *
 */
public class RealmConversation extends RealmObject {

    @PrimaryKey
    private String partnerEntityNameAndDomain; // room or user bare jid
    private RealmList<RealmMessage> messages;

    public String getPartnerEntityNameAndDomain() {
        return partnerEntityNameAndDomain;
    }

    public void setPartnerEntityNameAndDomain(String partnerEntityNameAndDomain) {
        this.partnerEntityNameAndDomain = partnerEntityNameAndDomain;
    }

    public RealmList<RealmMessage> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<RealmMessage> messages) {
        this.messages = messages;
    }
}