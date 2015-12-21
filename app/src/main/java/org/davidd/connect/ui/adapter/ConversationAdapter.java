package org.davidd.connect.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.model.Message;

import java.util.List;

/**
 * @author David Debre
 *         on 2015/12/13
 */
public class ConversationAdapter extends ArrayAdapter<Message> {
    private int mResourceId;

    public ConversationAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        mResourceId = resource;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(mResourceId, parent, false);

        TextView sender = (TextView) convertView.findViewById(R.id.chat_row_sender);
        TextView message = (TextView) convertView.findViewById(R.id.chat_row_message);

        sender.setText(getItem(position).getSender().getUsername());
        message.setText(getItem(position).getMessage());

        int gravity = UserManager.instance().getCurrentUser().getId().equals(
                getItem(position).getSender().getId()) ?
                Gravity.RIGHT : Gravity.LEFT;

        if (isPreviousUserTheSame(position)) {
            sender.setVisibility(View.GONE);
        } else {
            sender.setVisibility(View.VISIBLE);
            sender.setGravity(gravity);
        }

        message.setGravity(gravity);

        return convertView;
    }

    private boolean isPreviousUserTheSame(int position) {
        if (position == 0) {
            return false;
        }

        return getItem(position - 1).getSender().getId()
                .equals(getItem(position).getSender().getId());
    }
}
