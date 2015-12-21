package org.davidd.connect.connection;

/**
 * @author David Debre
 *         on 2015/12/12
 */
public interface XmppConnectionListener {
    void onConnectionSuccess();
    void onConnectionFailed(ErrorMessage message);
    void onAuthenticationSuccess();
    void onAuthenticationFailed(ErrorMessage message);
}
