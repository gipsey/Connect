package org.davidd.connect.connection.event;

public class OnRegistrationProcessFinishedEvent {

    public Throwable throwable;

    public OnRegistrationProcessFinishedEvent(Throwable throwable) {
        this.throwable = throwable;
    }
}
