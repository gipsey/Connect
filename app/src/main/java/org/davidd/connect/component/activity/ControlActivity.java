package org.davidd.connect.component.activity;

import android.content.Intent;
import android.os.Bundle;

import org.davidd.connect.R;
import org.davidd.connect.component.fragment.ControlFragment;
import org.davidd.connect.model.Room;
import org.davidd.connect.model.User;
import org.davidd.connect.util.ActivityUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ControlActivity extends NavigationActivity implements NavigateToChatListener, NavigateToAddOccupantsListener {

    public static final String CONTROL_FRAGMENT_ITEM_BUNDLE_KEY = "ControlFragmentItemBundleKey";

    private ControlFragment controlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setTitle(R.string.control_activity_title);
            controlFragment = new ControlFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(CONTROL_FRAGMENT_ITEM_BUNDLE_KEY, getIntent().getIntExtra(CONTROL_FRAGMENT_ITEM_BUNDLE_KEY, -1));
            controlFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.drawer_frame_layout, controlFragment, ControlFragment.TAG)
                    .commit();
        } else {
            controlFragment = (ControlFragment) getSupportFragmentManager().findFragmentByTag(ControlFragment.TAG);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void navigateToChat(User userToChatWith) {
        String userAsJsonFormattedString = createGsonWithExcludedFields().toJson(userToChatWith);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.USER_TO_CHAT_WITH, userAsJsonFormattedString);

        ActivityUtils.navigate(this, ChatActivity.class, bundle,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, false);
    }

    @Override
    public void navigateToChat(MultiUserChat muc) {
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.ROOM_NAME_TAG, muc.getRoom().toString());

        ActivityUtils.navigate(this, ChatActivity.class, bundle,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, false);
    }

    @Override
    public void navigateToAddOccupants(Room room) {
        Bundle bundle = new Bundle();
        bundle.putString(AddOccupantsActivity.ROOM_TAG, room.getMuc().getRoom().toString());

        ActivityUtils.navigate(this, AddOccupantsActivity.class, bundle,
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, false);
    }

    public void showFragmentByIndex(int index) {
        controlFragment.showFragmentByIndex(index);
    }
}