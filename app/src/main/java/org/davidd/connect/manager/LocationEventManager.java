package org.davidd.connect.manager;

import android.support.annotation.Nullable;

import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.L;
import org.davidd.connect.model.User;
import org.davidd.connect.xmpp.GeolocationEventElement;
import org.davidd.connect.xmpp.GeolocationItem;
import org.greenrobot.eventbus.EventBus;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Handles sending and receiving geolocation packets.
 */
public class LocationEventManager {

    private static LocationEventManager locationEventManager;

    private Map<User, GeolocationItem> savedLocations; // TODO make persistent

    private LocationEventManager() {
        savedLocations = new HashMap<>();
    }

    public static LocationEventManager instance() {
        if (locationEventManager == null) {
            locationEventManager = new LocationEventManager();
        }
        return locationEventManager;
    }

    @Nullable
    public GeolocationItem getGeolocationItemsForUser(User user) {
        return savedLocations.get(user);
    }

    public void geolocationEventReceived(GeolocationEventElement event) {
        GeolocationItem item = event.getEvent();
        User publisher = event.getEventPublisher();

        L.d(new Object() {}, "GEOLOC ITEM = " + item.toXML());

        if (publisher == null) {
            throw new IllegalStateException("Event publisher is null.");
        }

        savedLocations.put(publisher, item);

        EventBus.getDefault().post(new SavedUserLocationsChangedEvent(publisher));
    }

    /**
     * @param item the GeolocationItem to be sent
     */
    public void sendUserLocationItem(GeolocationItem item) {
        L.d(new Object() {});

        savedLocations.put(UserManager.instance().getCurrentUser(), item);

        EventBus.getDefault().post(new SavedUserLocationsChangedEvent(UserManager.instance().getCurrentUser()));

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
