package org.davidd.connect.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.davidd.connect.R;
import org.davidd.connect.model.User;
import org.davidd.connect.ui.activity.ChatActivity;
import org.davidd.connect.ui.adapter.ControlPagerAdapter;
import org.davidd.connect.util.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class ControlFragment extends Fragment {

    public static final String TAG = ControlFragment.class.getName();

    public static final int VIEW_PAGER_DEFAULT_PAGE_POSITION = 1;

    @Bind(R.id.control_activity_sliding_tabs)
    protected TabLayout mainTabLayout;
    @Bind(R.id.control_activity_view_pager)
    protected ViewPager mainViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        ControlPagerAdapter controlPagerAdapter = new ControlPagerAdapter(getActivity(), getChildFragmentManager());

        mainViewPager.setAdapter(controlPagerAdapter);
        mainViewPager.setCurrentItem(VIEW_PAGER_DEFAULT_PAGE_POSITION);

        mainTabLayout.setupWithViewPager(mainViewPager);

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ControlActivityFragment fragment =
                        (ControlActivityFragment) getChildFragmentManager().getFragments().get(position);
                fragment.onPagesSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
