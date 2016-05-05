package org.davidd.connect.xmpp;

import android.text.TextUtils;

import org.jivesoftware.smackx.pubsub.Item;

public class GeolocationItem extends Item {

    public static final String NODE = "http://jabber.org/protocol/geoloc";

    public static final String ID_TAG = "id";
    public static final String LOCALITY_TAG = "locality";
    public static final String LAT_TAG = "lat";
    public static final String LONG_TAG = "lon";
    public static final String DATUM_TAG = "datum";

    private double latitude, longitude;
    private String locality, datum;

    public GeolocationItem(String itemId, String nodeId) {
        super(itemId, nodeId);
    }

    public GeolocationItem(String locality, double latitude, double longitude, String datum) {
        super();
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datum = datum;
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

        if (TextUtils.isEmpty(getId())) {
            builder.append("<item>");
        } else {
            builder.append("<item id='");
            builder.append(getId());
            builder.append("'>");
        }
        builder.append("<geoloc xmlns='");
        builder.append(NODE);
        builder.append("' xml:lang='en'>");
        builder.append("<locality>");
        builder.append(locality);
        builder.append("</locality>");
        builder.append("<lat>");
        builder.append(latitude);
        builder.append("</lat>");
        builder.append("<lon>");
        builder.append(longitude);
        builder.append("</lon>");
        builder.append("<datum>");
        builder.append(datum);
        builder.append("</datum>");
        builder.append("</geoloc>");
        builder.append("</item>");

        return builder.toString();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
