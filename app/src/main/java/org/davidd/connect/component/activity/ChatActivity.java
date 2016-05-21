package org.davidd.connect.component.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.davidd.connect.R;
import org.davidd.connect.component.fragment.ChatFragment;
import org.davidd.connect.component.fragment.MucFragment;

public class ChatActivity extends AppCompatActivity {

    public static final String USER_TO_CHAT_WITH = "UserToChatWith";
    public static final String ROOM_NAME_TAG = "RoomNameTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            Fragment fragment = null;
            String tag = null;
            Bundle bundle = new Bundle();

            if (getIntent().getStringExtra(USER_TO_CHAT_WITH) != null) {
                fragment = new ChatFragment();
                tag = ChatFragment.TAG;

                bundle.putString(USER_TO_CHAT_WITH, getIntent().getStringExtra(USER_TO_CHAT_WITH));
                fragment.setArguments(bundle);
            } else if (getIntent().getStringExtra(ROOM_NAME_TAG) != null) {
                fragment = new MucFragment();
                tag = MucFragment.TAG;

                bundle.putString(ROOM_NAME_TAG, getIntent().getStringExtra(ROOM_NAME_TAG));
                fragment.setArguments(bundle);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, tag)
                    .commit();
        }
    }
}
