package org.davidd.connect.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.davidd.connect.model.ActiveChat;
import org.davidd.connect.util.DataUtils;

import java.util.List;

public class ActiveChatsAdapter extends ArrayAdapter<ActiveChat> {

    private LayoutInflater inflater;
    private int resource;

    public ActiveChatsAdapter(Context context, int resource, List<ActiveChat> objects) {
        super(context, resource, objects);

        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ContactViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ContactViewHolder) convertView.getTag();
        }

        setupContact(viewHolder, getItem(position));

        return convertView;
    }

    private void setupContact(ContactViewHolder viewHolder, ActiveChat chat) {
        viewHolder.firstLetterTextView.setText(String.valueOf(chat.getSender().getUserJIDProperties().getJID().charAt(0)));
        viewHolder.userNameTextView.setText(chat.getSender().getUserJIDProperties().getNameAndDomain());

        String lastMessage = chat.getLastMessage();
        if (DataUtils.isEmpty(lastMessage)) {
            viewHolder.statusTextView.setVisibility(View.GONE);
            viewHolder.statusTextView.setText(null);
        } else {
            viewHolder.statusTextView.setVisibility(View.VISIBLE);
            viewHolder.statusTextView.setText(lastMessage);
        }

        viewHolder.availabilityTextView.setText(chat.getSender().getUserPresence().getUserPresenceType().getStatus());
        viewHolder.availabilityImageView.setImageResource(
                ContactsHelper.getImageResourceFromUserPresence(chat.getSender().getUserPresence().getUserPresenceType()));
    }
}
