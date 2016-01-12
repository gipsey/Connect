package org.davidd.connect.ui.adapter;

import android.support.annotation.NonNull;

import org.davidd.connect.R;
import org.davidd.connect.model.UserPresenceType;

/**
 * Helper class for contacts.
 */
public class ContactsHelper {

    public static int getImageResourceFromUserPresence(@NonNull UserPresenceType userPresenceType) {
        switch (userPresenceType) {
            case AVAILABLE:
                return R.drawable.ic_status_available;
            case AWAY:
                return R.drawable.ic_status_away;
            case DO_NOT_DISTURB:
                return R.drawable.ic_status_dnd;
            case OFFLINE:
            default:
                return R.drawable.ic_status_unavailable;
        }
    }
}
