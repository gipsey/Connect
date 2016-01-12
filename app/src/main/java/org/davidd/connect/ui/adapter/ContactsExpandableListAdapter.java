package org.davidd.connect.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.model.User;
import org.davidd.connect.util.DataUtils;

import java.util.List;

public class ContactsExpandableListAdapter extends BaseExpandableListAdapter {

    private List<ContactGroup> contactGroups;
    private ExpandableListView expandableListView;
    private LayoutInflater inflater;

    public ContactsExpandableListAdapter(Context context, List<ContactGroup> contactGroups, ExpandableListView expandableListView) {
        super();
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
            convertView = inflater.inflate(R.layout.contact_child_row, parent, false);
            viewHolder = new ContactViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ContactViewHolder) convertView.getTag();
        }

        viewHolder.setup(contactGroups.get(groupPosition).getUsers().get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public User getUser(int groupPosition, int childPosition) {
        return contactGroups.get(groupPosition).getUsers().get(childPosition);
    }

    private class GroupViewHolder {
        TextView groupNameTextView;

        GroupViewHolder(View view) {
            groupNameTextView = (TextView) view.findViewById(R.id.group_name);
        }

        void setup(String groupName) {
            groupNameTextView.setText(groupName);
        }
    }

    private class ContactViewHolder {
        TextView firstLetterTextView;
        TextView userNameTextView;
        TextView statusTextView;
        TextView availabilityTextView;
        ImageView availabilityImageView;

        ContactViewHolder(View view) {
            firstLetterTextView = (TextView) view.findViewById(R.id.name_initial_textView);
            userNameTextView = (TextView) view.findViewById(R.id.name_textView);
            statusTextView = (TextView) view.findViewById(R.id.presence_status_textView);
            availabilityTextView = (TextView) view.findViewById(R.id.availability_textView);
            availabilityImageView = (ImageView) view.findViewById(R.id.availability_imageView);
        }

        void setup(User user) {
            firstLetterTextView.setText(String.valueOf(user.getUserJIDProperties().getJID().charAt(0)));
            userNameTextView.setText(user.getUserJIDProperties().getNameAndDomain());

            String status = user.getUserPresence().getPresence().getStatus();
            if (DataUtils.isEmpty(status)) {
                statusTextView.setVisibility(View.GONE);
                statusTextView.setText(null);
            } else {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(status);
            }

            availabilityTextView.setText(user.getUserPresence().getUserPresenceType().getStatus());
            availabilityImageView.setImageResource(
                    ContactsHelper.getImageResourceFromUserPresence(user.getUserPresence().getUserPresenceType()));
        }
    }
}
