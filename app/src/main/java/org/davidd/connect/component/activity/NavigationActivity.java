package org.davidd.connect.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.davidd.connect.R;
import org.davidd.connect.component.fragment.SettingsFragment;
import org.davidd.connect.manager.PreferencesManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.util.ActivityUtils;
import org.davidd.connect.util.DataUtils;
import org.greenrobot.eventbus.EventBus;

public abstract class NavigationActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CHECK_SETTINGS = 1;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

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

        if (UserManager.instance().getCurrentUser() == null) {
            return;
        }

        Bitmap photo = UserManager.instance().getCurrentUser().getUserPhoto();
        userPhotoImageView.setImageBitmap(photo);

        TextView userNameTextView = (TextView) drawerHeaderMainLayout.findViewById(R.id.user_name_textView);
        userNameTextView.setText(UserManager.instance().getCurrentUser().getUserJIDProperties().getJID());

        if (PreferencesManager.instance().getSettingsValue(SettingsFragment.LOCATION_KEY, false)) {
            checkIfLocationSettingsAreSet();
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                }
        }
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
            case R.id.drawer_rooms:
                navigateToControlActivity(2);
                break;
            case R.id.drawer_logout:
                logOut();
                break;
            case R.id.drawer_settings:
                ActivityUtils.navigate(this, SettingsActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, false);
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

    private void checkIfLocationSettingsAreSet() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        googleApiClient.connect();

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // TODO make it adjustable

        LocationRequest mLocationRequestBalancedPowerAccuracy = new LocationRequest();
        mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy)
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(NavigationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ignored) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
}
