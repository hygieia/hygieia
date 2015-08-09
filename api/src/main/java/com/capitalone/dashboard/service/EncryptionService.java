package com.capitalone.dashboard.service;

public interface EncryptionService {

    /**
     * Encrypts a string.
     *
     * @param message to encrypt
     * @return encrypted message
     */
    String encrypt(String message);
    
}
