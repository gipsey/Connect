package org.davidd.connect.component.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ocpsoft.pretty.time.PrettyTime;

import org.davidd.connect.component.activity.MapsActivity;
import org.davidd.connect.component.activity.UserActivity;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.model.ActiveBaseChat;
import org.davidd.connect.model.ActiveRoomChat;
import org.davidd.connect.model.ActiveUserChat;
import org.davidd.connect.model.User;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ActiveChatsAdapter extends ArrayAdapter<ActiveBaseChat> {

    private Activity activity;
    private LayoutInflater inflater;
    private int resource;

    public ActiveChatsAdapter(Context context, int resource) {
        super(context, resource);

        activity = (Activity) context;
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

        ActiveBaseChat baseChat = getItem(position);

        if (baseChat instanceof ActiveUserChat) {
            ActiveUserChat userChat = (ActiveUserChat) baseChat;

            setupWithUser(viewHolder, userChat);
            final User user = userChat.getUserToChatWith();

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

            viewHolder.userProfileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(UserActivity.USER_BUNDLE_TAG, createGsonWithExcludedFields().toJson(user));
                    ActivityUtils.navigate(activity, UserActivity.class, bundle, false);
                }
            });
        } else {
            setupWithRoom(viewHolder, (ActiveRoomChat) getItem(position));
        }

        return convertView;
    }

    private void setupWithUser(ContactViewHolder viewHolder, ActiveUserChat chat) {
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
            viewHolder.rightBottomTextView.setText(new PrettyTime().format(chat.getMyMessage().getDate()));
        }

        if (LocationEventManager.instance().getGeolocationItemsForUser(chat.getUserToChatWith()) == null) {
            viewHolder.userLocationImageButton.setVisibility(View.GONE);
        } else {
            viewHolder.userLocationImageButton.setVisibility(View.VISIBLE);
        }

        viewHolder.availabilityImageView.setVisibility(View.VISIBLE);
        viewHolder.availabilityImageView.setImageResource(
                ContactsHelper.getImageResourceFromUserPresence(chat.getUserToChatWith().getUserPresence().getUserPresenceType()));
    }

    private void setupWithRoom(ContactViewHolder viewHolder, ActiveRoomChat roomChat) {
        viewHolder.firstLetterTextView.setText(String.valueOf(roomChat.getMultiUserChat().getRoom().toString().charAt(0)));
        viewHolder.userNameTextView.setText(roomChat.getMultiUserChat().getRoom().toString());

        if (roomChat.getMyMessage() == null || DataUtils.isEmpty(roomChat.getMyMessage().getMessage().getBody())) {
            viewHolder.statusTextView.setVisibility(View.GONE);
            viewHolder.statusTextView.setText(null);

            viewHolder.rightBottomTextView.setVisibility(View.GONE);
            viewHolder.rightBottomTextView.setText(null);
        } else {
            String lastMessage = roomChat.getMyMessage().getMessage().getBody();
            if (roomChat.getMyMessage().getSender().equals(UserManager.instance().getCurrentUser())) {
                lastMessage = "<b>You:</b>  " + lastMessage;
            }
            viewHolder.statusTextView.setVisibility(View.VISIBLE);
            viewHolder.statusTextView.setText(Html.fromHtml(lastMessage));

            viewHolder.rightBottomTextView.setVisibility(View.VISIBLE);
            viewHolder.rightBottomTextView.setText(new PrettyTime().format(roomChat.getMyMessage().getDate()));
        }

        viewHolder.userLocationImageButton.setVisibility(View.GONE);

        viewHolder.availabilityImageView.setVisibility(View.GONE);
    }

    public List<ActiveUserChat> getActiveUserChats() {
        List<ActiveUserChat> userChats = new ArrayList<>();

        for (int i = 0; i < getCount(); i++) {
            if (getItem(i) instanceof ActiveUserChat) {
                userChats.add((ActiveUserChat) getItem(i));
            }
        }

        return userChats;
    }
}
