package org.davidd.connect.component.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.davidd.connect.R;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.manager.SavedUserLocationsChangedEvent;
import org.davidd.connect.model.User;
import org.davidd.connect.xmpp.GeolocationItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String USER_BUNDLE_TAG = "UserBundleTagForLocation";

    private GoogleMap mMap;

    private User userToGetLocationsFor;
    private List<Marker> visibleMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userToGetLocationsFor = createGsonWithExcludedFields().fromJson(getIntent().getStringExtra(USER_BUNDLE_TAG), User.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userLocationChanged(SavedUserLocationsChangedEvent savedUserLocationsChangedEvent) {
        if (mMap != null) {
            updateLocationOnMap();
        }
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
    @SuppressWarnings("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        updateLocationOnMap();
    }

    private void updateLocationOnMap() {
        for (Marker marker : visibleMarkers) {
            marker.remove();
        }
        visibleMarkers.clear();

        GeolocationItem item = LocationEventManager.instance().getGeolocationItemsForUser(userToGetLocationsFor);

        if (item == null) {
            Toast.makeText(this, "Location is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        addAMarkerToMap(item);

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker m : visibleMarkers) {
//            builder.include(m.getPosition());
//        }
//        LatLngBounds bounds = builder.build();

        LatLng latLng = new LatLng(item.getLat(), item.getLon());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
    }

    private void addAMarkerToMap(GeolocationItem item) {
        LatLng latLng = new LatLng(item.getLat(), item.getLon());

        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Here is " + userToGetLocationsFor.getUserJIDProperties().getName()));
        visibleMarkers.add(marker);
    }
}
