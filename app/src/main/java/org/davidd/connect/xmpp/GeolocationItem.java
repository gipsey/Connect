package org.davidd.connect.xmpp;

import android.text.TextUtils;

import org.jivesoftware.smackx.pubsub.Item;

public class GeolocationItem extends Item {

    public static final String NODE = "http://jabber.org/protocol/geoloc";

    private double accuracy;
    private double alt;
    private double altaccuracy;
    private String area;
    private double bearing;
    private String building;
    private String country;
    private String countrycode;
    private String datum;
    private String description;
    private String floor;
    private double lat;
    private String locality;
    private double lon;
    private String postalcode;
    private String region;
    private String room;
    private double speed;
    private String street;
    private String text;
    private String timestamp;
    private String tzo;
    private String uri;

    public GeolocationItem(String itemId, String nodeId) {
        super(itemId, nodeId);
    }

    public GeolocationItem() {
        super();
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

        if (accuracy != 0.0) {
            builder.append("<accuracy>").append(accuracy).append("</accuracy>");
        }

        if (alt != 0.0) {
            builder.append("<alt>").append(alt).append("</alt>");
        }

        if (altaccuracy != 0.0) {
            builder.append("<altaccuracy>").append(altaccuracy).append("</altaccuracy>");
        }

        if (!TextUtils.isEmpty(area)) {
            builder.append("<area>").append(area).append("</area>");
        }

        if (bearing != 0.0) {
            builder.append("<bearing>").append(bearing).append("</bearing>");
        }

        if (!TextUtils.isEmpty(building)) {
            builder.append("<building>").append(building).append("</building>");
        }

        if (!TextUtils.isEmpty(country)) {
            builder.append("<country>").append(country).append("</country>");
        }

        if (!TextUtils.isEmpty(countrycode)) {
            builder.append("<countrycode>").append(countrycode).append("</countrycode>");
        }

        if (!TextUtils.isEmpty(datum)) {
            builder.append("<datum>").append(datum).append("</datum>");
        }

        if (!TextUtils.isEmpty(description)) {
            builder.append("<description>").append(description).append("</description>");
        }

        if (!TextUtils.isEmpty(floor)) {
            builder.append("<floor>").append(floor).append("</floor>");
        }

        builder.append("<lat>").append(lat).append("</lat>");

        if (!TextUtils.isEmpty(locality)) {
            builder.append("<locality>").append(locality).append("</locality>");
        }

        builder.append("<lon>").append(lon).append("</lon>");

        if (!TextUtils.isEmpty(postalcode)) {
            builder.append("<postalcode>").append(postalcode).append("</postalcode>");
        }

        if (!TextUtils.isEmpty(region)) {
            builder.append("<region>").append(region).append("</region>");
        }

        if (!TextUtils.isEmpty(room)) {
            builder.append("<room>").append(room).append("</room>");
        }

        if (speed != 0.0) {
            builder.append("<speed>").append(speed).append("</speed>");
        }

        if (!TextUtils.isEmpty(street)) {
            builder.append("<street>").append(street).append("</street>");
        }

        if (!TextUtils.isEmpty(text)) {
            builder.append("<text>").append(text).append("</text>");
        }

        if (!TextUtils.isEmpty(timestamp)) {
            builder.append("<timestamp>").append(timestamp).append("</timestamp>");
        }

        if (!TextUtils.isEmpty(tzo)) {
            builder.append("<tzo>").append(tzo).append("</tzo>");
        }

        if (!TextUtils.isEmpty(uri)) {
            builder.append("<uri>").append(uri).append("</uri>");
        }

        builder.append("</geoloc>");
        builder.append("</item>");

        return builder.toString();
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getAltaccuracy() {
        return altaccuracy;
    }

    public void setAltaccuracy(double altaccuracy) {
        this.altaccuracy = altaccuracy;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTzo() {
        return tzo;
    }

    public void setTzo(String tzo) {
        this.tzo = tzo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
