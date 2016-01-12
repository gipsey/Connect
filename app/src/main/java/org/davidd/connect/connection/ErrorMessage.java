package org.davidd.connect.connection;

import android.support.annotation.NonNull;

public class ErrorMessage {
    private String message;
    private Throwable throwable;

    public ErrorMessage(@NonNull String message) {
        this.message = message;
    }

    public ErrorMessage(@NonNull String message, @NonNull Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
