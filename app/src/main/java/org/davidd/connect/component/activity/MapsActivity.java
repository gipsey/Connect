package org.davidd.connect.component.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ocpsoft.pretty.time.PrettyTime;

import org.davidd.connect.R;
import org.davidd.connect.component.adapter.MapMarkerInfoWindowAdapter;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.manager.MyMultiUserChatManager;
import org.davidd.connect.manager.UserManager;
import org.davidd.connect.manager.events.SavedUserLocationsChangedEvent;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.xmpp.GeolocationItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.EntityFullJid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class MapsActivity extends BaseAppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    public static final String USER_BUNDLE_TAG = "UserBundleTagForLocation";
    public static final String ROOM_NAME_BUNDLE_TAG = "RoomBundleTagForLocation";

    private GoogleMap mMap;

    private List<User> usersToGetLocationsFor;
    private List<Marker> visibleMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        usersToGetLocationsFor = new ArrayList<>();

        if (getIntent().getStringExtra(USER_BUNDLE_TAG) != null) {
            User user = createGsonWithExcludedFields().fromJson(getIntent().getStringExtra(USER_BUNDLE_TAG), User.class);
            usersToGetLocationsFor.add(user);

            setTitle(user.getUserJIDProperties().getNameAndDomain());
        } else if (getIntent().getStringExtra(ROOM_NAME_BUNDLE_TAG) != null) {
            String roomName = getIntent().getStringExtra(ROOM_NAME_BUNDLE_TAG);
            MultiUserChat muc = MyMultiUserChatManager.instance().getMucByFullName(roomName);

            for (EntityFullJid roomJid : muc.getOccupants()) {
                String userName = roomJid.getResourcepart().toString();
                usersToGetLocationsFor.add(new User(new UserJIDProperties(userName)));
            }

            setTitle(roomName);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        if (mMap != null && usersToGetLocationsFor.contains(savedUserLocationsChangedEvent.user)) {
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

        mMap.setOnMapLoadedCallback(this);
        mMap.setInfoWindowAdapter(new MapMarkerInfoWindowAdapter(this));

        updateLocationOnMap();
    }

    @Override
    public void onMapLoaded() {
        modifyCameraPosition();
    }

    private void updateLocationOnMap() {
        for (Marker marker : visibleMarkers) {
            marker.remove();
        }
        visibleMarkers.clear();

        Map<User, GeolocationItem> items = getLocationsForUsersList();

        if (items.isEmpty()) {
            Toast.makeText(this, "Location(s) is not available for the selected entity", Toast.LENGTH_SHORT).show();
            return;
        }

        for (User user : items.keySet()) {
            GeolocationItem item = items.get(user);
            LatLng latLng = new LatLng(item.getLat(), item.getLon());

            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(formatTitle(user, item))
                            .snippet(formatSnippet(user, item))
            );
            visibleMarkers.add(marker);
        }

        try {
            modifyCameraPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyCameraPosition() {
        if (!visibleMarkers.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker m : visibleMarkers) {
                builder.include(m.getPosition());
            }
            LatLngBounds bounds = builder.build();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 300, getResources().getDisplayMetrics()));
            mMap.animateCamera(cameraUpdate);
        }
    }

    private Map<User, GeolocationItem> getLocationsForUsersList() {
        Map<User, GeolocationItem> items = new LinkedHashMap<>();

        for (User user : usersToGetLocationsFor) {
            GeolocationItem item = LocationEventManager.instance().getGeolocationItemsForUser(user);
            if (item != null) {
                items.put(user, item);
            }
        }

        return items;
    }

    private String formatTitle(User user, GeolocationItem item) {
        String s;

        if (UserManager.instance().getCurrentUser().equals(user)) {
            s = "It's my location";
        } else {
            s = user.getUserJIDProperties().getNameAndDomain();
        }

        return s;
    }

    private String formatSnippet(User user, GeolocationItem item) {
        String s = "";

        if (!TextUtils.isEmpty(item.getLocality())) {
            s += item.getLocality();
        } else {
            if (!TextUtils.isEmpty(item.getArea())) {
                s += item.getArea();
            }
        }

        if (!TextUtils.isEmpty(item.getStreet())) {
            s += "," + item.getStreet();
        }

        if (item.getSpeed() > 0) {
            s += "\n";
            s += "Moving with " + (3.6 * item.getSpeed()) + " km/h";
        }

        if (!TextUtils.isEmpty(item.getTimestamp())) {
            try {
                long time = Long.parseLong(item.getTimestamp());
                s += "\n";

                SimpleDateFormat dateFormat = new SimpleDateFormat();

                String convertedToLocalTimeZone = dateFormat.format(new Date(time));
                Date date = dateFormat.parse(convertedToLocalTimeZone);

                s += "Captured " + new PrettyTime().format(date);
            } catch (NumberFormatException | ParseException ignored) {
            }
        }

        return s;
    }
}
