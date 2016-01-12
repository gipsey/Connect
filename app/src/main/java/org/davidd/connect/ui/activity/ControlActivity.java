package org.davidd.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.model.User;
import org.davidd.connect.ui.fragment.ContactsFragment;
import org.davidd.connect.ui.fragment.ControlFragment;
import org.davidd.connect.util.ActivityUtils;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ControlActivity extends NavigationActivity implements ContactsFragment.NavigationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.control_activity_title);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drawer_frame_layout, new ControlFragment(), ControlFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void navigationToChatRequested(User userToChatWith) {
        String userAsJsonFormattedString = createGsonWithExcludedFields().toJson(userToChatWith);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.USER_TO_CHAT_WITH, userAsJsonFormattedString);

        ActivityUtils.navigate(this, ChatActivity.class, bundle,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, false);
    }
}