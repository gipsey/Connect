package org.davidd.connect.manager;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.GeolocationDebugger;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.davidd.connect.xmpp.GeolocationEventElement;
import org.davidd.connect.xmpp.GeolocationItem;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.pep.PEPManager;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles sending and receiving geolocation packets.
 */
public class LocationEventManager {

    private static LocationEventManager locationEventManager;

    private Context contextForDebugOnly;
    private Map<User, List<GeolocationItem>> savedLocations;

    private LocationEventManager() {
        savedLocations = new HashMap<>();
    }

    public static LocationEventManager instance() {
        if (locationEventManager == null) {
            locationEventManager = new LocationEventManager();
        }
        return locationEventManager;
    }

    public void debugPressed(Context context) {
        contextForDebugOnly = context;
        GeolocationDebugger.startPublishingLocations();
    }

    @Nullable
    public List<GeolocationItem> getGeolocationItemsForUser(User user) {
        return savedLocations.get(user);
    }

    public void geolocationEventReceived(GeolocationEventElement event) {
        GeolocationItem item = event.getEvent();
        User publisher = event.getEventPublisher();

        L.d(new Object() {}, "GEOLOC ITEM = " + item.toXML());

        if (publisher == null) {
            throw new IllegalStateException("Event publisher is null.");
        }

        if (savedLocations.containsKey(publisher)) {
            savedLocations.get(publisher).add(item);
        } else {
            List<GeolocationItem> items = new ArrayList<>();
            items.add(item);
            savedLocations.put(publisher, items);
        }
    }

    public void sendUserLocationItem(Location location) {
        GeolocationItem item = new GeolocationItem(location.getLatitude(), location.getLongitude());
        sendUserLocationItem(item);
    }

    /**
     * @param item the GeolocationItem to be sent
     */
    public void sendUserLocationItem(GeolocationItem item) {
        L.d(new Object() {});

        createGeolocationNodeIfDoesNotExist();

        XMPPTCPConnection con = MyConnectionManager.instance().getXmppTcpConnection();
        PEPManager pepManager = PEPManager.getInstanceFor(con);

        try {
            pepManager.publish(item, GeolocationItem.NODE);
        } catch (SmackException.NotConnectedException | XMPPException.XMPPErrorException |
                SmackException.NoResponseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LeafNode createGeolocationNode() {
        L.d(new Object() {});

        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setPersistentItems(false);
        form.setDeliverPayloads(true);
        form.setAccessModel(AccessModel.open);
        form.setPublishModel(PublishModel.open);
        form.setSubscribe(true);

        LeafNode leafNode = null;
        try {
            leafNode = (LeafNode) MyConnectionManager.instance().getPubSubManager()
                    .createNode(GeolocationItem.NODE, form);
        } catch (SmackException.NotConnectedException | XMPPException.XMPPErrorException |
                SmackException.NoResponseException | InterruptedException e) {
            e.printStackTrace();
        }

        return leafNode;
    }

    private LeafNode createGeolocationNodeIfDoesNotExist() {
        L.d(new Object() {});

        PubSubManager pubSubManager = MyConnectionManager.instance().getPubSubManager();

        try {
            pubSubManager.deleteNode(GeolocationItem.NODE);
        } catch (SmackException.NotConnectedException | XMPPException.XMPPErrorException |
                SmackException.NoResponseException | InterruptedException e) {
            e.printStackTrace();
        }

        return createGeolocationNode();
    }
}
