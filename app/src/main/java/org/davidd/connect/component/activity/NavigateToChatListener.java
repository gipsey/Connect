package org.davidd.connect.component.activity;

import org.davidd.connect.model.User;
import org.jivesoftware.smackx.muc.MultiUserChat;

public interface NavigateToChatListener {

    void navigateToChat(User userToChatWith);

    void navigateToChat(MultiUserChat muc);
}
