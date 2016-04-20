package org.davidd.connect.connection.packet;

import org.jivesoftware.smackx.pep.packet.PEPItem;

public class UserLocationPacket extends PEPItem {

    public static final String NODE = "http://jabber.org/protocol/geoloc";

    private double latitude, longitude;

    public UserLocationPacket(String id) {
        super(id);
    }

    public UserLocationPacket(String id, double latitude, double longitude) {
        super(id);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    String getNode() {
        return NODE;
    }

}
