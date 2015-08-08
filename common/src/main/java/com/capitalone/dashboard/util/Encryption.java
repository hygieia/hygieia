package com.capitalone.dashboard.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

public class Encryption {

	private static String ALGO = "DESede";

	public static String encryptString(String message, SecretKey key) throws EncryptionException {
		String encryptedMessage = "";
		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedBytes = cipher.doFinal(message.getBytes());
			encryptedMessage = Base64.encodeBase64String(encryptedBytes);

		} catch (IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | NullPointerException e) {
			throw new EncryptionException("Cannot encrypt this message" + '\n' + e.getMessage());
		}
		return encryptedMessage;
	}

	public static String decryptString(String encryptedMessage, SecretKey key) throws EncryptionException{
		String decryptedMessage = "";
		try {
			Cipher decipher = Cipher.getInstance(ALGO);
			decipher.init(Cipher.DECRYPT_MODE, key);
			byte[] messageToDecrypt = Base64.decodeBase64(encryptedMessage);
			byte[] decryptedBytes = decipher.doFinal(messageToDecrypt);
			decryptedMessage = new String(decryptedBytes);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NullPointerException | IllegalArgumentException e) {
			throw new EncryptionException("Cannot decrypt this message" + '\n' + e.getMessage());
		}
		return decryptedMessage;
	}
}