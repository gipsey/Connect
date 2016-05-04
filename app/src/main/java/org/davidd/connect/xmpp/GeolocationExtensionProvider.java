package org.davidd.connect.xmpp;

import org.davidd.connect.debug.L;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * Parses geolocation event.
 */
public class GeolocationExtensionProvider extends ExtensionElementProvider {

    // TODO ensure to parse here only if a geoloc arrives

    @Override
    public Element parse(XmlPullParser parser, int initialDepth) throws Exception {
        double latitude = 0, longitude = 0;
        String itemId = null, locality = null, datum = null;

        boolean stop = false;
        String openTag = null;

        while (!stop) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    openTag = parser.getName();
                    if ("item".equals(openTag)) {
                        itemId = parser.getAttributeValue("", "id");
                    }
                    break;
                case XmlPullParser.TEXT:
                    try {
                        if ("lat".equals(openTag)) {
                            latitude = Double.parseDouble(parser.getText());
                        } else if ("lon".equals(openTag)) {
                            longitude = Double.parseDouble(parser.getText());
                        } else if ("locality".equals(openTag)) {
                            locality = parser.getText();
                        } else if ("datum".equals(openTag)) {
                            datum = parser.getText();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    // Stop parsing when we hit </item>
                    stop = "item".equals(parser.getName());
                    openTag = null;
                    break;
            }
        }

        GeolocationItem item = new GeolocationItem(itemId, GeolocationItem.NODE);
        item.setLocality(locality);
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        item.setDatum(datum);

        L.d(new Object() {}, "GEOLOC EXTENSION PARSED = " + item.toXML());

        return new GeolocationEventElement(item);
    }
}
