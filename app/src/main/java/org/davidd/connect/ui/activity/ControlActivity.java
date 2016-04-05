package org.davidd.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.model.User;
import org.davidd.connect.ui.fragment.ControlFragment;
import org.davidd.connect.util.ActivityUtils;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ControlActivity extends NavigationActivity implements NavigateToChatListener {

    public static final String CONTROL_FRAGMENT_ITEM_BUNDLE_KEY = "ControlFragmentItemBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.control_activity_title);

        if (savedInstanceState == null) {
            ControlFragment controlFragment = new ControlFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(CONTROL_FRAGMENT_ITEM_BUNDLE_KEY, getIntent().getIntExtra(CONTROL_FRAGMENT_ITEM_BUNDLE_KEY, -1));
            controlFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drawer_frame_layout, controlFragment, ControlFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void navigateToChat(User userToChatWith) {
        String userAsJsonFormattedString = createGsonWithExcludedFields().toJson(userToChatWith);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.USER_TO_CHAT_WITH, userAsJsonFormattedString);

        ActivityUtils.navigate(this, ChatActivity.class, bundle,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, false);
    }
}