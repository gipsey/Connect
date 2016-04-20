package org.davidd.connect.connection.packetListener;

import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;

public class AcceptAllStanzaFilter implements StanzaFilter {

    @Override
    public boolean accept(Stanza stanza) {
        return true;
    }
}
