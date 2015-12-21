package org.davidd.connect.connection;

import android.support.annotation.NonNull;

/**
 * @author David Debre
 *         on 2015/12/12
 */
public class ErrorMessage {
    private String mMessage;
    private Throwable mThrowable;

    public ErrorMessage(@NonNull String message) {
        mMessage = message;
    }

    public ErrorMessage(@NonNull String message, @NonNull Throwable throwable) {
        mMessage = message;
        mThrowable = throwable;
    }

    public String getMessage() {
        return mMessage;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
