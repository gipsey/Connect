package org.davidd.connect.debug;

import org.davidd.connect.connection.MyConnectionManager;
import org.davidd.connect.connection.packet.UserLocationItem;
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
import java.util.List;

public class UserLocationDebugger {

    public static final UserLocationItem berlin = new UserLocationItem(52.52426800, 13.40629000);
    public static final UserLocationItem london = new UserLocationItem(51.50735090, -0.12775830);
    public static final UserLocationItem moscow = new UserLocationItem(55.75582600, 37.61730000);
    public static final UserLocationItem tokyo = new UserLocationItem(35.68948750, 139.69170640);
    public static final UserLocationItem sydney = new UserLocationItem(-33.86748690, 151.20699020);
    public static final UserLocationItem newYork = new UserLocationItem(40.71278370, -74.00594130);
    public static final UserLocationItem clujManastur = new UserLocationItem(46.75215988, 23.55634404);
    public static final UserLocationItem clujBaciu = new UserLocationItem(46.79213406, 23.52441502);
    public static final UserLocationItem clujBucuresti1 = new UserLocationItem(46.78529499, 23.61359311);
    public static final UserLocationItem clujBucuresti2 = new UserLocationItem(46.78553008, 23.60921574);
    public static final UserLocationItem clujBucuresti3 = new UserLocationItem(46.78558885, 23.60612584);
    public static final UserLocationItem clujBucuresti4 = new UserLocationItem(46.78576516, 23.60054684);

    public static final List<UserLocationItem> items = new ArrayList<>();

    static {
        items.add(berlin);
        items.add(london);
        items.add(moscow);
        items.add(tokyo);
        items.add(sydney);
        items.add(newYork);
        items.add(clujManastur);
        items.add(clujBaciu);
        items.add(clujBucuresti1);
        items.add(clujBucuresti2);
        items.add(clujBucuresti3);
        items.add(clujBucuresti4);
    }

    public static void startPublishingLocations() {
        L.d(new Object() {});

//        createLeafNode();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    publishEvents();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static void publishEvents() {
        XMPPTCPConnection con = MyConnectionManager.instance().getXmppTcpConnection();
        PEPManager pepManager = PEPManager.getInstanceFor(con);

        long wait = 1500;
        try {
            pepManager.publish(berlin, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(london, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(moscow, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(tokyo, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(sydney, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(newYork, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(clujManastur, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(clujBaciu, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(clujBucuresti1, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(clujBucuresti2, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(clujBucuresti3, UserLocationItem.NODE);
            Thread.sleep(wait);
            pepManager.publish(clujBucuresti4, UserLocationItem.NODE);
            Thread.sleep(wait);
        } catch (SmackException.NotConnectedException | XMPPException.XMPPErrorException |
                SmackException.NoResponseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static LeafNode createLeafNode() {
        PubSubManager pubSubManager = MyConnectionManager.instance().getPubSubManager();
        String node = UserLocationItem.NODE;

        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setPersistentItems(false);
        form.setDeliverPayloads(true);
        form.setAccessModel(AccessModel.open);
        form.setPublishModel(PublishModel.open);
        form.setSubscribe(true);

        LeafNode leafNode = null;
        try {
            leafNode = (LeafNode) pubSubManager.createNode(node, form);
        } catch (SmackException.NotConnectedException | XMPPException.XMPPErrorException |
                SmackException.NoResponseException | InterruptedException e) {
            e.printStackTrace();
        }

        return leafNode;
    }
}
