package org.davidd.connect.connection;

public interface MyConnectionListener {

    void onConnectionSuccess();

    void onConnectionFailed(ErrorMessage message);

    void onAuthenticationSuccess();

    void onAuthenticationFailed(ErrorMessage message);
}
