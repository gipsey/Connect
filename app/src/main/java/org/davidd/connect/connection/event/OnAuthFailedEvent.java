package org.davidd.connect.connection.event;

import org.davidd.connect.connection.ErrorMessage;

public class OnAuthFailedEvent {

    public ErrorMessage errorMessage;

    public OnAuthFailedEvent(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}
