package org.davidd.connect.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.davidd.connect.R;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.User;
import org.davidd.connect.ui.adapter.ContactGroup;
import org.davidd.connect.ui.adapter.ContactsExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactsFragment extends ControlActivityFragment implements RosterManager.UserContactsUpdatedListener {

    @Bind(R.id.contacts_expandableListView)
    ExpandableListView contactsExpandableListView;

    private ContactsExpandableListAdapter contactsExpandableListAdapter;
    private List<ContactGroup> contactGroups = new ArrayList<>();
    private NavigationListener navigationListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigationListener = (NavigationListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        contactsExpandableListAdapter = new ContactsExpandableListAdapter(getActivity(), contactGroups, contactsExpandableListView);

        contactsExpandableListView.setAdapter(contactsExpandableListAdapter);
        contactsExpandableListView.setGroupIndicator(null);

        contactsExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        contactsExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                navigationListener.navigationToChatRequested(
                        contactsExpandableListAdapter.getUser(groupPosition, childPosition));
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        RosterManager.instance().addUserContactsUpdatedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserContacts(RosterManager.instance().getUserContacts());
    }

    @Override
    public void onStop() {
        super.onStop();
        RosterManager.instance().removeUserContactsUpdatedListener(this);
    }

    @Override
    public void onPagesSelected() {
    }

    @Override
    public void userContactsUpdated(List<User> userContacts) {
        showUserContacts(userContacts);
    }

    private void showUserContacts(List<User> userContacts) {
        contactGroups.clear();
        updateContactGroups(userContacts);

        contactsExpandableListAdapter.notifyDataSetChanged();
    }

    private void updateContactGroups(List<User> userContacts) {
        for (User user : userContacts) {
            String groupName;
            if (user.getRosterEntry().getGroups().isEmpty()) {
                groupName = getString(R.string.unfiled_group_name);
            } else {
                groupName = user.getRosterEntry().getGroups().get(0).getName();
            }

            ContactGroup group = getGroupFroName(groupName);
            group.getUsers().add(user);
        }
    }

    private ContactGroup getGroupFroName(@NonNull String name) {
        for (ContactGroup group : contactGroups) {
            if (group.getGroupName().equals(name)) {
                return group;
            }
        }

        ContactGroup group = new ContactGroup(name, new ArrayList<User>());
        contactGroups.add(group);
        return group;
    }

    public interface NavigationListener {
        void navigationToChatRequested(User user);
    }
}