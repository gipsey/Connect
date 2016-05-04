package org.davidd.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.davidd.connect.R;
import org.davidd.connect.debug.L;
import org.davidd.connect.manager.GeolocationManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;

public abstract class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout drawerHeaderMainLayout = (LinearLayout) navigationView.getHeaderView(0);
        drawerHeaderMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMyProfile();
            }
        });

        ImageView userPhotoImageView = (ImageView) drawerHeaderMainLayout.findViewById(R.id.user_photo_imageView);
        userPhotoImageView.setImageBitmap(UserManager.instance().getCurrentUser().getUserPhoto());

        TextView userNameTextView = (TextView) drawerHeaderMainLayout.findViewById(R.id.user_name_textView);
        userNameTextView.setText(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID());

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_my_profile:
                navigateToMyProfile();
                break;
            case R.id.drawer_active_chats:
                navigateToControlActivity(0);
                break;
            case R.id.drawer_contacts:
                navigateToControlActivity(1);
                break;
            case R.id.drawer_debug:
                GeolocationManager.getInstance().debugPressed(this);
                break;
            case R.id.drawer_about:
                L.d(new Object() {}, "drawer_about");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToMyProfile() {
        Bundle bundle = new Bundle();
        bundle.putString(UserActivity.USER_BUNDLE_TAG,
                DataUtils.createGsonWithExcludedFields().toJson(UserManager.instance().getCurrentUser()));
        ActivityUtils.navigate(this, UserActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP, false);
    }

    private void navigateToControlActivity(int fragmentIndexToShow) {
        if (this instanceof ControlActivity) {
            ControlActivity controlActivity = (ControlActivity) this;
            controlActivity.showFragmentByIndex(fragmentIndexToShow);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(ControlActivity.CONTROL_FRAGMENT_ITEM_BUNDLE_KEY, fragmentIndexToShow);
            ActivityUtils.navigate(this, ControlActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP, false);
        }
    }
}
