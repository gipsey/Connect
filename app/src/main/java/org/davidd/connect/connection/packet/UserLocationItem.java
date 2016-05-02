package org.davidd.connect.connection.packet;

import android.text.TextUtils;

import org.jivesoftware.smackx.pubsub.Item;

public class UserLocationItem extends Item {

    public static final String NODE = "http://jabber.org/protocol/geoloc";

    private final double latitude, longitude;

    public UserLocationItem(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getNode() {
        return NODE;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toXML() {
        super.toXML();
        if (TextUtils.isEmpty(NODE)) {
            throw new IllegalArgumentException("NODE is null or empty");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("<item>");
        builder.append("<geoloc xmlns='");
        builder.append(NODE);
        builder.append("' xml:lang='en'>");
        builder.append("<lat>");
        builder.append(latitude);
        builder.append("</lat>");
        builder.append("<lon>");
        builder.append(longitude);
        builder.append("</lon>");
        builder.append("</geoloc>");
        builder.append("</item>");

        return builder.toString();
    }
}
