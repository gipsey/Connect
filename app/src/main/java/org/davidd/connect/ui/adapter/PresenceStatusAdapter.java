package org.davidd.connect.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.model.UserPresenceType;

import java.util.ArrayList;
import java.util.List;

public class PresenceStatusAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<UserPresenceType> presences;

    public PresenceStatusAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        presences = new ArrayList<>();
        presences.add(UserPresenceType.AVAILABLE);
        presences.add(UserPresenceType.AWAY);
        presences.add(UserPresenceType.DO_NOT_DISTURB);
        presences.add(UserPresenceType.OFFLINE);
    }

    @Override
    public int getCount() {
        return presences.size();
    }

    @Override
    public Object getItem(int position) {
        return presences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.presence_status_row, parent, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.status_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.status_icon);

        UserPresenceType userPresenceType = (UserPresenceType) getItem(position);

        textView.setText(userPresenceType.getStatus());
        imageView.setImageResource(ContactsHelper.getImageResourceFromUserPresence(userPresenceType));

        return view;
    }
}