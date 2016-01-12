package org.davidd.connect.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import org.davidd.connect.manager.UserManager;

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
                // TODO navigate to user screen
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_my_profile:
                L.d(new Object() {}, "drawer_my_profile");
                break;
            case R.id.drawer_active_chats:
                L.d(new Object() {}, "drawer_active_chats");
                break;
            case R.id.drawer_settings:
                L.d(new Object() {}, "drawer_settings");
                break;
            case R.id.drawer_about:
                L.d(new Object() {}, "drawer_about");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setTitle(@NonNull String title) {
        toolbar.setTitle(title);
    }
}
