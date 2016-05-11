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
        GeolocationItem item = new GeolocationItem();

        boolean stop = false;
        String openTag = null;

        while (!stop) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    openTag = parser.getName();
                    if ("item".equals(openTag)) {
                        String itemId = parser.getAttributeValue("", "id");
                        item = new GeolocationItem(itemId, GeolocationItem.NODE);
                    }
                    break;
                case XmlPullParser.TEXT:
                    try {
                        if ("accuracy".equals(openTag)) {
                            item.setAccuracy(Double.parseDouble(parser.getText()));
                        } else if ("alt".equals(openTag)) {
                            item.setAlt(Double.parseDouble(parser.getText()));
                        } else if ("altaccuracy".equals(openTag)) {
                            item.setAltaccuracy(Double.parseDouble(parser.getText()));
                        } else if ("area".equals(openTag)) {
                            item.setArea(parser.getText());
                        } else if ("bearing".equals(openTag)) {
                            item.setBearing(Double.parseDouble(parser.getText()));
                        } else if ("building".equals(openTag)) {
                            item.setBuilding(parser.getText());
                        } else if ("country".equals(openTag)) {
                            item.setCountry(parser.getText());
                        } else if ("countrycode".equals(openTag)) {
                            item.setCountrycode(parser.getText());
                        } else if ("datum".equals(openTag)) {
                            item.setDatum(parser.getText());
                        } else if ("description".equals(openTag)) {
                            item.setDescription(parser.getText());
                        } else if ("floor".equals(openTag)) {
                            item.setFloor(parser.getText());
                        } else if ("lat".equals(openTag)) {
                            item.setLat(Double.parseDouble(parser.getText()));
                        } else if ("locality".equals(openTag)) {
                            item.setLocality(parser.getText());
                        } else if ("lon".equals(openTag)) {
                            item.setLon(Double.parseDouble(parser.getText()));
                        } else if ("postalcode".equals(openTag)) {
                            item.setPostalcode(parser.getText());
                        } else if ("region".equals(openTag)) {
                            item.setRegion(parser.getText());
                        } else if ("room".equals(openTag)) {
                            item.setRoom(parser.getText());
                        } else if ("speed".equals(openTag)) {
                            item.setSpeed(Double.parseDouble(parser.getText()));
                        } else if ("street".equals(openTag)) {
                            item.setStreet(parser.getText());
                        } else if ("text".equals(openTag)) {
                            item.setText(parser.getText());
                        } else if ("timestamp".equals(openTag)) {
                            item.setTimestamp(parser.getText());
                        } else if ("tzo".equals(openTag)) {
                            item.setTzo(parser.getText());
                        } else if ("uri".equals(openTag)) {
                            item.setUri(parser.getText());
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

        L.d(new Object() {}, "GEOLOC EXTENSION PARSED = " + item.toXML());

        return new GeolocationEventElement(null, item);
    }
}
