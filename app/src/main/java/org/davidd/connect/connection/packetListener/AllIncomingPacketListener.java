package org.davidd.connect.connection.packetListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

public class AllIncomingPacketListener extends PrinterStanzaListener {

    public static final String TYPE = "INCOMING";

    private static AllIncomingPacketListener allIncomingPacketListener;

    private AllIncomingPacketListener() {
    }

    public static AllIncomingPacketListener instance() {
        if (allIncomingPacketListener == null) {
            allIncomingPacketListener = new AllIncomingPacketListener();
        }
        return allIncomingPacketListener;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        printStanza(TYPE, packet);
    }
}
