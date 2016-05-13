package org.davidd.connect.component.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.davidd.connect.R;
import org.davidd.connect.component.fragment.ControlActiveChatsFragment;
import org.davidd.connect.component.fragment.ControlContactsFragment;
import org.davidd.connect.component.fragment.ControlRoomsFragment;

public class ControlPagerAdapter extends FragmentPagerAdapter {

    public static final int NUMBER_OF_TABS = 3;
    private String[] mTitleArray;

    public ControlPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mTitleArray = context.getResources().getStringArray(R.array.control_view_tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ControlActiveChatsFragment();
            case 1:
                return new ControlContactsFragment();
            case 2:
                return new ControlRoomsFragment();
            default:
                throw new UnsupportedOperationException("There is no other fragment to inflate. The position is incorrect");
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleArray.length > position) {
            return mTitleArray[position];
        } else {
            return "";
        }
    }
}
