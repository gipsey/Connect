package org.davidd.connect.component.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.component.activity.MapsActivity;
import org.davidd.connect.component.activity.UserActivity;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.manager.RosterManager;
import org.davidd.connect.model.User;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ContactsExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity activity;

    private List<ContactGroup> contactGroups;
    private ExpandableListView expandableListView;
    private LayoutInflater inflater;

    public ContactsExpandableListAdapter(Context context, List<ContactGroup> contactGroups, ExpandableListView expandableListView) {
        super();
        this.activity = (Activity) context;
        this.contactGroups = contactGroups;
        this.expandableListView = expandableListView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return contactGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return contactGroups.get(groupPosition).getUsers().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return contactGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return contactGroups.get(groupPosition).getUsers().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 1000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_group_row, parent, false);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        viewHolder.setup(contactGroups.get(groupPosition).getGroupName());

        expandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ContactViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_row, parent, false);
            viewHolder = new ContactViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ContactViewHolder) convertView.getTag();
        }

        final User user = contactGroups.get(groupPosition).getUsers().get(childPosition);
        setupContact(viewHolder, (ContactGroup) getGroup(groupPosition), user);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public User getUser(int groupPosition, int childPosition) {
        return contactGroups.get(groupPosition).getUsers().get(childPosition);
    }

    public void clear() {
        contactGroups.clear();
        notifyDataSetChanged();
    }

    private void setupContact(ContactViewHolder viewHolder, ContactGroup group, final User user) {
        viewHolder.firstLetterTextView.setText(String.valueOf(user.getUserJIDProperties().getJID().charAt(0)));
        viewHolder.userNameTextView.setText(user.getUserJIDProperties().getNameAndDomain());

        if (group.isWaitingForApproval()) {
            viewHolder.statusTextView.setVisibility(View.GONE);
            viewHolder.userLocationImageButton.setVisibility(View.GONE);
            viewHolder.userProfileImageButton.setVisibility(View.GONE);
            viewHolder.availabilityImageView.setVisibility(View.GONE);
            viewHolder.rightBottomTextView.setVisibility(View.GONE);

            viewHolder.addUserImageButton.setVisibility(View.VISIBLE);
            viewHolder.addUserImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RosterManager.instance().acceptUserSubscription(user);
                }
            });

            viewHolder.removeUserImageButton.setVisibility(View.VISIBLE);
            viewHolder.removeUserImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RosterManager.instance().declineUserSubscription(user);
                }
            });
        } else {
            String status = user.getUserPresence().getPresence().getStatus();
            if (DataUtils.isEmpty(status)) {
                viewHolder.statusTextView.setVisibility(View.GONE);
                viewHolder.statusTextView.setText(null);
            } else {
                viewHolder.statusTextView.setVisibility(View.VISIBLE);
                viewHolder.statusTextView.setText(status);
            }

            if (LocationEventManager.instance().getGeolocationItemsForUser(user) == null) {
                viewHolder.userLocationImageButton.setVisibility(View.GONE);
            } else {
                viewHolder.userLocationImageButton.setVisibility(View.VISIBLE);
            }

            viewHolder.rightBottomTextView.setVisibility(View.VISIBLE);
            viewHolder.rightBottomTextView.setText(user.getUserPresence().getUserPresenceType().getStatus());

            viewHolder.availabilityImageView.setVisibility(View.VISIBLE);
            viewHolder.availabilityImageView.setImageResource(
                    ContactsHelper.getImageResourceFromUserPresence(user.getUserPresence().getUserPresenceType()));

            if (viewHolder.userLocationImageButton.getVisibility() == View.VISIBLE) {
                viewHolder.userLocationImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(MapsActivity.USER_BUNDLE_TAG, createGsonWithExcludedFields().toJson(user));
                        ActivityUtils.navigate(activity, MapsActivity.class, bundle, false);
                    }
                });
            }

            viewHolder.userProfileImageButton.setVisibility(View.VISIBLE);
            viewHolder.userProfileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(UserActivity.USER_BUNDLE_TAG, createGsonWithExcludedFields().toJson(user));
                    ActivityUtils.navigate(activity, UserActivity.class, bundle, false);
                }
            });

            viewHolder.addUserImageButton.setVisibility(View.GONE);
            viewHolder.removeUserImageButton.setVisibility(View.GONE);
        }
    }

    class GroupViewHolder {

        @Bind(R.id.group_name)
        TextView groupNameTextView;

        private GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setup(String groupName) {
            groupNameTextView.setText(groupName);
        }
    }
}
