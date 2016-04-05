package org.davidd.connect.ui.activity;

import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.ui.fragment.UserFragment;

public class UserActivity extends NavigationActivity {

    public static final String USER_BUNDLE_TAG = "UserBundleTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.user_activity_title);

        // TODO: investigate how fragment is opened

        if (savedInstanceState == null) {
            UserFragment userFragment = new UserFragment();
            Bundle bundle = new Bundle();
            bundle.putString(USER_BUNDLE_TAG, getIntent().getStringExtra(USER_BUNDLE_TAG));
            userFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drawer_frame_layout, userFragment, UserFragment.TAG)
                    .commit();
        }
    }
}
