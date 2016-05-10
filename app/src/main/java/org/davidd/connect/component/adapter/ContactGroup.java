package org.davidd.connect.component.adapter;

import org.davidd.connect.model.User;

import java.util.List;

public class ContactGroup {

    private String groupName;
    private List<User> users;

    public ContactGroup(String groupName, List<User> users) {
        this.groupName = groupName;
        this.users = users;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<User> getUsers() {
        return users;
    }
}
