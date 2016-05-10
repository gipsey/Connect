package org.davidd.connect.component.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.component.adapter.ContactGroup;
import org.davidd.connect.component.adapter.ContactsExpandableListAdapter;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactsFragment extends ControlActivityFragment implements RosterManager.UserContactsUpdatedListener {

    @Bind(R.id.contacts_expandableListView)
    ExpandableListView expandableListView;

    private ContactsExpandableListAdapter contactsExpandableListAdapter;
    private List<ContactGroup> contactGroups = new ArrayList<>();

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

        TextView emptyView = (TextView) view.findViewById(R.id.contacts_empty_view);
        emptyView.setText(R.string.no_contacts);

        expandableListView.setEmptyView(emptyView);

        contactsExpandableListAdapter = new ContactsExpandableListAdapter(getActivity(), contactGroups, expandableListView);

        expandableListView.setAdapter(contactsExpandableListAdapter);
        expandableListView.setGroupIndicator(null);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                navigateToChatListener.navigateToChat(contactsExpandableListAdapter.getUser(groupPosition, childPosition));
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
        for (int i = 0; i < userContacts.size(); i++) {
            String groupName;
            if (userContacts.get(i).getRosterEntry().getGroups().isEmpty()) {
                groupName = getString(R.string.unfiled_group_name);
            } else {
                groupName = userContacts.get(i).getRosterEntry().getGroups().get(0).getName();
            }

            ContactGroup group = getGroupFroName(groupName);
            group.getUsers().add(userContacts.get(i));
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
}
