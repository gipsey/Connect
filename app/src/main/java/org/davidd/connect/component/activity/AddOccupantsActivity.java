package org.davidd.connect.component.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.davidd.connect.R;
import org.davidd.connect.component.fragment.AddRoomOccupantsFragment;

public class AddOccupantsActivity extends AppCompatActivity {

    public static final String ROOM_TAG = "RoomTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_occupant);

        if (savedInstanceState == null) {
            AddRoomOccupantsFragment addRoomOccupantsFragment = new AddRoomOccupantsFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ROOM_TAG, getIntent().getStringExtra(ROOM_TAG));
            addRoomOccupantsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, addRoomOccupantsFragment, AddRoomOccupantsFragment.TAG)
                    .commit();
        }
    }
}
