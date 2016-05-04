package org.davidd.connect.manager;

import android.content.Context;
import android.widget.Toast;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.debug.GeolocationDebugger;
import org.davidd.connect.debug.L;
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

/**
 * Handles sending and receiving geolocation packets.
 */
public class GeolocationManager {

    private static GeolocationManager geolocationManager;

    private Context contextForDebugOnly;

    private GeolocationManager() {
    }

    public static GeolocationManager getInstance() {
        if (geolocationManager == null) {
            geolocationManager = new GeolocationManager();
        }
        return geolocationManager;
    }

    public void debugPressed(Context context) {
        contextForDebugOnly = context;
        GeolocationDebugger.startPublishingLocations();
    }

    public void geolocationEventReceived(GeolocationEventElement event) {
        GeolocationItem item = event.getEvent();
        L.d(new Object() {}, "GEOLOC ITEM = " + item.toXML());
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
            showGeolocItemPublishingStatus(item, false);
            return;
        }

        showGeolocItemPublishingStatus(item, true);
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

    private void showGeolocItemPublishingStatus(final GeolocationItem item, final boolean succeeded) {
        ConnectApp.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(contextForDebugOnly, "Geoloc item '" + item.getLocality() + "' sent = " + succeeded, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
