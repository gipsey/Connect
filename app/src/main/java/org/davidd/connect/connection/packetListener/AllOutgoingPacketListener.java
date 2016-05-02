package org.davidd.connect.connection.packetListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

public class AllOutgoingPacketListener extends PrinterStanzaListener {

    public static final String TYPE = "OUTGOING";

    private static AllOutgoingPacketListener allOutgoingPacketListener;

    private AllOutgoingPacketListener() {
    }

    public static AllOutgoingPacketListener instance() {
        if (allOutgoingPacketListener == null) {
            allOutgoingPacketListener = new AllOutgoingPacketListener();
        }
        return allOutgoingPacketListener;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        printStanza(TYPE, packet);
    }
}
