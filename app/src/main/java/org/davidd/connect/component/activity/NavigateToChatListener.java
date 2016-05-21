package org.davidd.connect.component.activity;

import org.davidd.connect.model.Room;
import org.davidd.connect.model.User;

public interface NavigateToChatListener {

    void navigateToChat(User userToChatWith);

    void navigateToChat(Room room);
}
