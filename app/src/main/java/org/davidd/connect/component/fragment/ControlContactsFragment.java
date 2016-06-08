package org.davidd.connect.component.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.davidd.connect.R;
import org.davidd.connect.component.adapter.ContactGroup;
import org.davidd.connect.component.adapter.ContactsExpandableListAdapter;
import org.davidd.connect.manager.RefreshRoster;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.manager.events.UserAcceptedStatusEvent;
import org.davidd.connect.manager.events.UserDeclinedStatusEvent;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlContactsFragment extends ControlTabFragment {

    @Bind(R.id.contacts_expandableListView)
    ExpandableListView expandableListView;

    @Bind(R.id.addContact_floatingActionButton)
    FloatingActionButton addContactFloatingActionButton;

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

        addContactFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewContactAlert();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserContacts(RosterManager.instance().getUserContacts());
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPagesSelected() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshRoster refreshRoster) {
        showUserContacts(RosterManager.instance().getUserContacts());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userAcceptedStatusEvent(UserAcceptedStatusEvent event) {
        if (event.isSuccess()) {
            Toast.makeText(getActivity(), "Contact " + event.getUser().getUserJIDProperties().getName() + " was added successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Contact " + event.getUser().getUserJIDProperties().getName() + " wasn't added", Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userDeclinedStatusEvent(UserDeclinedStatusEvent event) {
        if (event.isSuccess()) {
            Toast.makeText(getActivity(), "Contact " + event.getUser().getUserJIDProperties().getName() + " was declined successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Contact " + event.getUser().getUserJIDProperties().getName() + " wasn't declined", Toast.LENGTH_LONG).show();
        }
    }

    private void showUserContacts(List<User> userContacts) {
        contactGroups.clear();
        contactGroups.addAll(updateContactSubscriptions(RosterManager.instance().getUserSubscriptions()));
        contactGroups.addAll(updateContactGroups(userContacts));
        contactsExpandableListAdapter.notifyDataSetChanged();
    }

    private List<ContactGroup> updateContactSubscriptions(List<User> userSubscriptions) {
        List<ContactGroup> groups = new ArrayList<>();

        if (userSubscriptions.isEmpty()) {
            return groups;
        }

        ContactGroup group = new ContactGroup("They wait for me...", new ArrayList<User>());
        group.setWaitingForApproval(true);
        for (User user : userSubscriptions) {
            group.getUsers().add(user);
        }
        groups.add(group);

        return groups;
    }

    private List<ContactGroup> updateContactGroups(List<User> userContacts) {
        List<ContactGroup> groups = new ArrayList<>();

        for (User user : userContacts) {
            if (user.getRosterEntry().getGroups().isEmpty()) {
                String groupName = getString(R.string.unfiled_group_name);
                ContactGroup contactGroup = getGroupFroName(groupName, groups);
                contactGroup.getUsers().add(user);
            } else {
                for (RosterGroup rosterGroup : user.getRosterEntry().getGroups()) {
                    String groupName = rosterGroup.getName();
                    ContactGroup contactGroup = getGroupFroName(groupName, groups);
                    contactGroup.getUsers().add(user);
                }
            }
        }

        return groups;
    }

    private ContactGroup getGroupFroName(String name, List<ContactGroup> groups) {
        for (ContactGroup group : groups) {
            if (group.getGroupName().equals(name)) {
                return group;
            }
        }

        ContactGroup group = new ContactGroup(name, new ArrayList<User>());
        groups.add(group);

        return group;
    }

    private void showNewContactAlert() {
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setHint("Enter contact's name with domain");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(layoutParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contactAddingAttempt(editText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setView(editText);

        builder.create().show();
    }

    private void contactAddingAttempt(String userJid) {
        User user = new User(new UserJIDProperties(userJid));
        RosterManager.instance().addContact(user);
    }
}
