package org.davidd.connect.component.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.model.Room;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RoomsArrayAdapter extends ArrayAdapter<Room> {

    private static int resourceId = R.layout.room_row;
    private Activity activity;
    private LayoutInflater inflater;

    public RoomsArrayAdapter(Context context) {
        super(context, resourceId, new ArrayList<Room>());
        this.activity = (Activity) context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setupContact(getItem(position));

        return convertView;
    }


    class ViewHolder {

        @Bind(R.id.roomName_textView)
        TextView roomNameTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setupContact(Room room) {
            roomNameTextView.setText(room.getRoomName());
        }
    }
}
