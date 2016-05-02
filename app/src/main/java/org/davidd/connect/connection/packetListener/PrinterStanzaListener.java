package org.davidd.connect.connection.packetListener;

import org.davidd.connect.debug.L;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;

public abstract class PrinterStanzaListener implements StanzaListener {

    protected void printStanza(String stanzaType, Stanza stanza) {
        String formatted = getFormatted(stanza.toString());
        L.d("-----> " + stanzaType + " stanza:" + formatted);
    }

    private String getFormatted(String original) {
        String formatted = "";
        int noOFTabs = -1;

        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);

            if (c == '<') {
                int next = i + 1;
                if (next < original.length()) {
                    if (original.charAt(next) == '/') {
                        formatted += "\n" + tabs(noOFTabs);
                        noOFTabs--;
                    } else {
                        noOFTabs++;
                        formatted += "\n" + tabs(noOFTabs);
                    }
                } else {
                    noOFTabs++;
                    formatted += "\n" + tabs(noOFTabs);
                }
            } else if (c == '/') {
                int next = i + 1;
                if (next < original.length()) {
                    if (original.charAt(next) == '>') {
                        noOFTabs--;
                    }
                }
            }

            formatted += c;
        }

        return formatted;
    }

    private String tabs(int noOFTabs) {
        String tabs = "";
        for (int i = 0; i < noOFTabs; i++) {
            tabs += "\t";
        }
        return tabs;
    }
}
