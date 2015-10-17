package com.capitalone.dashboard.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    private static String ALGO = "DESede";


    public static String getStringKey() throws EncryptionException {
        SecretKey key = null;
        try {
            key = KeyGenerator.getInstance(ALGO).generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Cannot generate a secret key" + '\n' + e.getMessage());
        }
        return Base64.encodeBase64String(key.getEncoded());
    }

    public static SecretKey getSecretKey() throws EncryptionException {
        SecretKey key = null;
        try {
            key = KeyGenerator.getInstance(ALGO).generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Cannot generate a secret key" + '\n' + e.getMessage());
        }
        return key;
    }

    public static String encryptString(String message, SecretKey key)
            throws EncryptionException {
        String encryptedMessage = "";
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            encryptedMessage = Base64.encodeBase64String(encryptedBytes);

        } catch (IllegalBlockSizeException | BadPaddingException
                | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | NullPointerException e) {
            throw new EncryptionException("Cannot encrypt this message" + '\n'
                    + e.getMessage());
        }
        return encryptedMessage;
    }

    public static String decryptString(String encryptedMessage, SecretKey key)
            throws EncryptionException {
        String decryptedMessage = "";
        try {
            Cipher decipher = Cipher.getInstance(ALGO);
            decipher.init(Cipher.DECRYPT_MODE, key);
            byte[] messageToDecrypt = Base64.decodeBase64(encryptedMessage);
            byte[] decryptedBytes = decipher.doFinal(messageToDecrypt);
            decryptedMessage = new String(decryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NullPointerException
                | IllegalArgumentException e) {
            throw new EncryptionException("Cannot decrypt this message" + '\n'
                    + e.getMessage());
        }
        return decryptedMessage;
    }

    public static String encryptString(String message, String aKey)
            throws EncryptionException {
        String encryptedMessage = "";
        try {
            byte[] encodedKey = Base64.decodeBase64(aKey);
            SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length,
                    ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            encryptedMessage = Base64.encodeBase64String(encryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException
                | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | NullPointerException e) {
            throw new EncryptionException("Cannot encrypt this message" + '\n'
                    + e.getMessage());
        }
        return encryptedMessage;
    }

    public static String decryptString(String encryptedMessage, String aKey)
            throws EncryptionException {
        String decryptedMessage = "";
        try {
            byte[] encodedKey = Base64.decodeBase64(aKey);
            SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length,
                    ALGO);
            Cipher decipher = Cipher.getInstance(ALGO);
            decipher.init(Cipher.DECRYPT_MODE, key);
            byte[] messageToDecrypt = Base64.decodeBase64(encryptedMessage);
            byte[] decryptedBytes = decipher.doFinal(messageToDecrypt);
            decryptedMessage = new String(decryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NullPointerException
                | IllegalArgumentException e) {
            throw new EncryptionException("Cannot decrypt this message" + '\n'
                    + e.getMessage());
        }
        return decryptedMessage;
    }

    public static void main(String[] args) {
        try {
            String k = Encryption.getStringKey();
            System.out.println("Your secret key is:");
            System.out.println(k);
            System.out.println("Sample encrypted string with the above key for 'thisIsMyPassword' is:");
            System.out.println(Encryption.encryptString("thisIsMyPassword", k));
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
    }
}