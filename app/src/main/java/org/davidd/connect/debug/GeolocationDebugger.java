package org.davidd.connect.debug;

import org.davidd.connect.xmpp.GeolocationItem;

import java.util.ArrayList;
import java.util.List;

public class GeolocationDebugger {

    public static final GeolocationItem berlin = new GeolocationItem("berlin", 52.52426800, 13.40629000, "berlinDATUM");
    public static final GeolocationItem london = new GeolocationItem("london", 51.50735090, -0.12775830, "londonDATUM");
    public static final GeolocationItem moscow = new GeolocationItem("moscow", 55.75582600, 37.61730000, "moscowDATUM");
    public static final GeolocationItem tokyo = new GeolocationItem("tokyo", 35.68948750, 139.69170640, "tokyoDATUM");
    public static final GeolocationItem sydney = new GeolocationItem("sydney", -33.86748690, 151.20699020, "sydneyDATUM");
    public static final GeolocationItem newYork = new GeolocationItem("newYork", 40.71278370, -74.00594130, "newYorkDATUM");
    public static final GeolocationItem clujManastur = new GeolocationItem("clujManastur", 46.75215988, 23.55634404, "clujManasturDATUM");
    public static final GeolocationItem clujBaciu = new GeolocationItem("clujBaciu", 46.79213406, 23.52441502, "clujBaciuDATUM");
    public static final GeolocationItem clujBucuresti1 = new GeolocationItem("clujBucuresti1", 46.78529499, 23.61359311, "clujBucuresti1DATUM");
    public static final GeolocationItem clujBucuresti2 = new GeolocationItem("clujBucuresti2", 46.78553008, 23.60921574, "clujBucuresti2DATUM");
    public static final GeolocationItem clujBucuresti3 = new GeolocationItem("clujBucuresti3", 46.78558885, 23.60612584, "clujBucuresti3DATUM");
    public static final GeolocationItem clujBucuresti4 = new GeolocationItem("clujBucuresti4", 46.78576516, 23.60054684, "clujBucuresti4DATUM");

    public static final List<GeolocationItem> items = new ArrayList<>();

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
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                long wait = 3000;
//                try {
//                    LocationEventManager.instance().sendUserLocationItem(berlin);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(london);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(moscow);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(tokyo);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(sydney);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(newYork);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(clujManastur);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(clujBaciu);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(clujBucuresti1);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(clujBucuresti2);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(clujBucuresti3);
//                    Thread.sleep(wait);
//                    LocationEventManager.instance().sendUserLocationItem(clujBucuresti4);
//                    Thread.sleep(wait);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
