package org.davidd.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.davidd.connect.R;
import org.davidd.connect.ui.fragment.ChatFragment;

public class ChatActivity extends AppCompatActivity {

    public static final String USER_TO_CHAT_WITH = "UserToChatWith";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString(USER_TO_CHAT_WITH, getIntent().getStringExtra(USER_TO_CHAT_WITH));
            chatFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, chatFragment, ChatFragment.TAG)
                    .commit();
        }
    }
}
