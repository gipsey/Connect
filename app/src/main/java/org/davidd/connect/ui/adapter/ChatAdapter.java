package org.davidd.connect.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.model.MyMessage;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatAdapter extends ArrayAdapter<MyMessage> {

    private LayoutInflater inflater;
    private int resourceId;

    public ChatAdapter(Context context, int resource, List<MyMessage> objects) {
        super(context, resource, objects);
        resourceId = resource;

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ChatViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChatViewHolder) convertView.getTag();
        }

        viewHolder.setup(position);

        return convertView;
    }

    class ChatViewHolder {

        @Bind(R.id.chat_row_parent_layout)
        LinearLayout parentLayout;

        @Bind(R.id.chat_row_participant)
        TextView senderTextView;

        @Bind(R.id.chat_row_message_layout)
        LinearLayout messageLayout;

        @Bind(R.id.chat_row_message_textView)
        TextView messageTextView;

        ChatViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setup(int position) {
            senderTextView.setText(getItem(position).getSender().getUserJIDProperties().getNameAndDomain() + ":");
            messageTextView.setText(getItem(position).getMessage().getBody());

            boolean isItMe = UserManager.instance().getCurrentUser().getUserJIDProperties().getNameAndDomain()
                    .equals(getItem(position).getSender().getUserJIDProperties().getNameAndDomain());
            int gravity = isItMe ? Gravity.RIGHT : Gravity.LEFT;

            parentLayout.setGravity(gravity);

            senderTextView.setGravity(gravity);
            senderTextView.setVisibility(isPreviousUserTheSame(position) ? View.GONE : View.VISIBLE);

            messageLayout.setBackgroundResource(isItMe ? R.drawable.bubble_right : R.drawable.bubble_left);
        }

        private boolean isPreviousUserTheSame(int position) {
            return position != 0 && getItem(position - 1).getSender().getUserJIDProperties().getNameAndDomain()
                    .equals(getItem(position).getSender().getUserJIDProperties().getNameAndDomain());
        }
    }
}
