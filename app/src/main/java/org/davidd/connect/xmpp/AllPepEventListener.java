package org.davidd.connect.xmpp;

import org.davidd.connect.debug.L;
import org.davidd.connect.manager.LocationEventManager;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.pep.PEPListener;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jxmpp.jid.EntityBareJid;

/**
 * Listener for all the PEP events.
 */
public class AllPepEventListener implements PEPListener {

    private static AllPepEventListener allPepEventListener;

    private AllPepEventListener() {
    }

    public static AllPepEventListener getInstance() {
        if (allPepEventListener == null) {
            allPepEventListener = new AllPepEventListener();
        }
        return allPepEventListener;
    }

    @Override
    public void eventReceived(EntityBareJid from, EventElement event, Message message) {
        L.d(new Object() {}, "EVENT FROM = " + from.toString() + ", TYPE = " + event.getEventType() +
                ", NAMESPACE = " + event.getNamespace() + ", ELEMENT NAME = " + event.getElementName());
        L.d(new Object() {}, "EVENT = " + event.toXML().toString());
        L.d(new Object() {}, "MESSAGE = " + message.toString());


        if (event instanceof GeolocationEventElement) {
            GeolocationEventElement geolocationEventElement = (GeolocationEventElement) event;

            UserJIDProperties jid = new UserJIDProperties(from.toString());
            geolocationEventElement.setEventPublisher(new User(jid));

            LocationEventManager.instance().geolocationEventReceived(geolocationEventElement);
        }
    }
}
