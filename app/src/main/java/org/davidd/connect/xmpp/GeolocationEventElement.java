package org.davidd.connect.xmpp;

import org.davidd.connect.model.User;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.EventElementType;

public class GeolocationEventElement extends EventElement {

    private User eventPublisher;

    public GeolocationEventElement(User eventPublisher, GeolocationItem geolocationItem) {
        super(EventElementType.items, geolocationItem);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public GeolocationItem getEvent() {
        return (GeolocationItem) super.getEvent();
    }

    public User getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(User eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
