package com.capitalone.dashboard.misc;

public class HygieiaException extends Exception {

    public HygieiaException() {
    }

    public HygieiaException(String message) {
        super(message);
    }

    public HygieiaException(String message, Throwable cause) {
        super(message, cause);
    }

    public HygieiaException(Throwable cause) {
        super(cause);
    }

    public HygieiaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
