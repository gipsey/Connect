package org.davidd.connect.connection.event;

import org.davidd.connect.connection.ErrorMessage;

public class OnConnectionFailedEvent {

    public ErrorMessage errorMessage;

    public OnConnectionFailedEvent(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}
