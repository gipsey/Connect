package org.davidd.connect.component.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.davidd.connect.R;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.model.User;
import org.davidd.connect.xmpp.GeolocationItem;

import java.util.List;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String USER_BUNDLE_TAG = "UserBundleTagForLocation";

    private GoogleMap mMap;

    private User userToGetLocationsFor;
    private List<GeolocationItem> locationsForUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userToGetLocationsFor = createGsonWithExcludedFields().fromJson(getIntent().getStringExtra(USER_BUNDLE_TAG), User.class);
        locationsForUser = LocationEventManager.instance().getGeolocationItemsForUser(userToGetLocationsFor);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationsForUser != null) {
            for (GeolocationItem item : locationsForUser) {
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(item.getLatitude(), item.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Here is " + userToGetLocationsFor.getUserJIDProperties().getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        } else {
            Toast.makeText(this, "No user to show location for", Toast.LENGTH_SHORT).show();
        }
    }
}
