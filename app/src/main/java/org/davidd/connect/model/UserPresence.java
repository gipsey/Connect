package org.davidd.connect.model;

import android.support.annotation.NonNull;

import org.davidd.connect.util.DataUtils;
import org.jivesoftware.smack.packet.Presence;

public class UserPresence {

    private Presence presence;
    private UserPresenceType userPresenceType;

    public UserPresence(@NonNull Presence presence) {
        this.presence = presence;
        userPresenceType = getTypeFromPresence();
    }

    public Presence getPresence() {
        return presence;
    }

    @NonNull
    public UserPresenceType getUserPresenceType() {
        return userPresenceType;
    }

    private UserPresenceType getTypeFromPresence() {
        if (presence.getType() == Presence.Type.available) {
            switch (presence.getMode()) {
                case away:
                case xa:
                    return UserPresenceType.AWAY;
                case dnd:
                    return UserPresenceType.DO_NOT_DISTURB;
                default:
                    return UserPresenceType.AVAILABLE;
            }
        } else {
            return UserPresenceType.OFFLINE;
        }
    }

    @Override
    public String toString() {
        String s = "---\n";

        s += formatLine("To", presence.getTo().toString());
        s += formatLine("From", presence.getFrom().toString());
        s += formatLine("userPresenceType", getTypeFromPresence().toString());
        s += formatLine("Type", presence.getType().toString());
        s += formatLine("Mode", presence.getMode().toString());
        s += formatLine("Status", presence.getStatus());
        s += formatLine("Priority", String.valueOf(presence.getPriority()));

        return s;
    }

    private String formatLine(String label, String data) {
        if (DataUtils.isEmpty(label)) {
            label = "<no_label>";
        }
        if (DataUtils.isEmpty(data)) {
            data = "<no_data>";
        }
        return label + " : " + data + "\n";
    }
}

