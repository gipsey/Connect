package org.davidd.connect.xmpp;

import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.EventElementType;

public class GeolocationEventElement extends EventElement {

    public GeolocationEventElement(GeolocationItem geolocationItem) {
        super(EventElementType.items, geolocationItem);
    }

    @Override
    public GeolocationItem getEvent() {
        return (GeolocationItem) super.getEvent();
    }
}
