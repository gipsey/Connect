package org.davidd.connect.connection.packetListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;

import java.util.List;

public class UserLocationPacketListener extends PrinterStanzaListener {

    private static UserLocationPacketListener userLocationPacketListener;

    private UserLocationPacketListener() {
    }

    public static UserLocationPacketListener instance() {
        if (userLocationPacketListener == null) {
            userLocationPacketListener = new UserLocationPacketListener();
        }
        return userLocationPacketListener;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        String allExtensions = "";
        List<ExtensionElement> extensionElementList = packet.getExtensions();
        for (ExtensionElement e : extensionElementList) {
            allExtensions += e.getNamespace() + "\n";
        }
    }
}
