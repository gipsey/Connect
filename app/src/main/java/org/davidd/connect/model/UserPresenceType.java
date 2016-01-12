package org.davidd.connect.model;

public enum UserPresenceType {

    AVAILABLE("Available"), // R.string.user_status_online
    AWAY("Away"), // R.string.user_status_away
    DO_NOT_DISTURB("Do not disturb"), // R.string.user_status_doNotDisturb
    OFFLINE("Offline"); // R.string.user_status_offline

    private String status;

    UserPresenceType(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
