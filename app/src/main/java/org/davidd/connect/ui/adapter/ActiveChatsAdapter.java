package org.davidd.connect.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.davidd.connect.manager.UserManager;
import org.davidd.connect.model.ActiveChat;
import org.davidd.connect.util.DataUtils;

import java.util.List;

public class ActiveChatsAdapter extends ArrayAdapter<ActiveChat> {

    private final List<ActiveChat> activeChats;
    private LayoutInflater inflater;
    private int resource;

    public ActiveChatsAdapter(Context context, int resource, List<ActiveChat> activeChats) {
        super(context, resource, activeChats);

        this.resource = resource;
        this.activeChats = activeChats;

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
        viewHolder.firstLetterTextView.setText(String.valueOf(chat.getUserToChatWith().getUserJIDProperties().getJID().charAt(0)));
        viewHolder.userNameTextView.setText(chat.getUserToChatWith().getUserJIDProperties().getNameAndDomain());

        if (chat.getMyMessage() == null || DataUtils.isEmpty(chat.getMyMessage().getMessage().getBody())) {
            viewHolder.statusTextView.setVisibility(View.GONE);
            viewHolder.statusTextView.setText(null);

            viewHolder.rightBottomTextView.setVisibility(View.GONE);
            viewHolder.rightBottomTextView.setText(null);
        } else {
            String lastMessage = chat.getMyMessage().getMessage().getBody();
            if (chat.getMyMessage().getSender().equals(UserManager.instance().getCurrentUser())) {
                lastMessage = "<b>You:</b>  " + lastMessage;
            }
            viewHolder.statusTextView.setVisibility(View.VISIBLE);
            viewHolder.statusTextView.setText(Html.fromHtml(lastMessage));

            viewHolder.rightBottomTextView.setVisibility(View.VISIBLE);
            viewHolder.rightBottomTextView.setText("TODOmin");
        }

        viewHolder.availabilityImageView.setImageResource(
                ContactsHelper.getImageResourceFromUserPresence(chat.getUserToChatWith().getUserPresence().getUserPresenceType()));
    }

    public List<ActiveChat> getActiveChats() {
        return activeChats;
    }
}
