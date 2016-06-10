package org.davidd.connect.db;

import io.realm.RealmObject;

/**
 *
 */
public class RealmMessage extends RealmObject {

    private boolean isNormalChat;
    private String partnerEntityNameAndDomain; // room or user bare jid
    private String senderNameAndDomain; // room participant or user participant bare jid
    private String message;
    private Long timestampInUtc;

    public boolean isNormalChat() {
        return isNormalChat;
    }

    public void setIsNormalChat(boolean isNormalChat) {
        this.isNormalChat = isNormalChat;
    }

    public String getPartnerEntityNameAndDomain() {
        return partnerEntityNameAndDomain;
    }

    public void setPartnerEntityNameAndDomain(String partnerEntityNameAndDomain) {
        this.partnerEntityNameAndDomain = partnerEntityNameAndDomain;
    }

    public String getSenderNameAndDomain() {
        return senderNameAndDomain;
    }

    public void setSenderNameAndDomain(String senderNameAndDomain) {
        this.senderNameAndDomain = senderNameAndDomain;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestampInUtc() {
        return timestampInUtc;
    }

    public void setTimestampInUtc(Long timestampInUtc) {
        this.timestampInUtc = timestampInUtc;
    }
}
