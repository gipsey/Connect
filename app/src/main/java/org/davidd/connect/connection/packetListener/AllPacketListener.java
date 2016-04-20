package org.davidd.connect.connection.packetListener;

import org.davidd.connect.debug.L;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;

import java.util.List;

public class AllPacketListener implements StanzaListener {

    private static AllPacketListener allPacketListener;

    private AllPacketListener() {
    }

    public static AllPacketListener instance() {
        if (allPacketListener == null) {
            allPacketListener = new AllPacketListener();
        }
        return allPacketListener;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        String allExtensions = "";
        List<ExtensionElement> extensionElementList = packet.getExtensions();
        for (ExtensionElement e : extensionElementList) {
            allExtensions += e.getNamespace() + "\n";
        }

        L.d(new Object() {}, packet.getStanzaId() + ", from " + packet.getFrom() + ", allExtensions " + allExtensions);
    }
}
