package com.capitalone.dashboard.util;

public class EncryptionException extends Exception {
	private static final long serialVersionUID = -4472911532254883259L;

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
