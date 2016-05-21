package org.davidd.connect.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.model.Room;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RoomsArrayAdapter extends ArrayAdapter<Room> {

    private static int resourceId = R.layout.room_row;
    private AddOccupantsListener addOccupantsListener;
    private LayoutInflater inflater;

    public RoomsArrayAdapter(Context context, AddOccupantsListener addOccupantsListener) {
        super(context, resourceId, new ArrayList<Room>());
        this.addOccupantsListener = addOccupantsListener;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setupContact(getItem(position));

        viewHolder.addNewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOccupantsListener.addOccupants(getItem(position));
            }
        });

        return convertView;
    }

    public interface AddOccupantsListener {
        void addOccupants(Room room);
    }

    class ViewHolder {

        @Bind(R.id.roomName_textView)
        TextView roomNameTextView;

        @Bind(R.id.occupantsNumber_textView)
        TextView occupantsNumberTextView;

        @Bind(R.id.addNew_imageButton)
        ImageButton addNewImageButton;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setupContact(Room room) {
            roomNameTextView.setText(room.getMuc().getRoom().toString());
            occupantsNumberTextView.setText(String.valueOf(room.getMuc().getOccupantsCount()));
        }
    }
}
