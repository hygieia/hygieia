package com.capitalone.dashboard.util;

public class EncryptionException extends Exception {

    /**
     * Constructs a {@code EncrytionException} with no detail message.
     */
    public EncryptionException() {
        super();
    }

    /**
     * Constructs a {@code EncrytionException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public EncryptionException(String s) {
        super(s);
    }
}
