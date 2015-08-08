package com.capitalone.dashboard.util;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class EncryptionTests {

	private final String THING_TO_BE_ENCRYPTED = "AKIAJ24MI4VLOIR72NVA";
	private final String A_GOOD_STRING_KEY = "5XBoZ7li2W5wzhOULEqtQzdkufjsVFs4";
	private final String A_BAD_STRING_KEY = "c/t/nuBFwTgvB+lwzS/q5W0ZkQhhxCB1";
	private static final String ALGO = "DESede";
	private static final SecretKey GOOD_KEY = getKey();
	private static final SecretKey BAD_KEY = getKey();

	private static SecretKey getKey() {
		SecretKey key = null;
		try {
			key = KeyGenerator.getInstance(ALGO).generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String stringKey = Base64.encodeBase64String(key.getEncoded());
		return key;
	}

	@Test
	public void testEncryptDecryptString() {
		String encryptedString = null;
		String decryptedString = null;
		try {
			encryptedString = Encryption.encryptString(THING_TO_BE_ENCRYPTED,
					GOOD_KEY);
			decryptedString = Encryption.decryptString(encryptedString,
					GOOD_KEY);
		} catch (EncryptionException e) {
			e.printStackTrace();
		}
		assertEquals(THING_TO_BE_ENCRYPTED, decryptedString);
	}

	@Test(expected = com.capitalone.dashboard.util.EncryptionException.class)
	public void testDecryptionWithBadKey() throws Exception {
		String encryptedString = Encryption.encryptString(THING_TO_BE_ENCRYPTED,
				GOOD_KEY);
		@SuppressWarnings("unused")
		String decryptedString = Encryption.decryptString(encryptedString, BAD_KEY);

	}

	@Test (expected = com.capitalone.dashboard.util.EncryptionException.class)
	public void testEncryptNullString() throws Exception {
		@SuppressWarnings("unused")
		String encryptedString = Encryption.encryptString(null, GOOD_KEY);
	}

	@Test (expected = com.capitalone.dashboard.util.EncryptionException.class)
	public void testDecryptNullString() throws Exception {
		@SuppressWarnings("unused")
		String decryptedString = Encryption.decryptString(null, GOOD_KEY);
	}
	
	@Test 
	public void testEncryptEmptyString() throws Exception {
		String encryptedString = Encryption.encryptString("", GOOD_KEY);
		String decryptedString = Encryption.decryptString(encryptedString, GOOD_KEY);
		assertEquals("", decryptedString);
	}

	@Test 
	public void testDecryptEmptyString() throws Exception {
		String decryptedString = Encryption.decryptString("", GOOD_KEY);
		assertEquals("", decryptedString);
	}
	
	@Test
	public void testEncryptString() {
		String encryptedString = null;
		String decryptedString = null;
		try {
			encryptedString = Encryption.encryptString(THING_TO_BE_ENCRYPTED,
					A_GOOD_STRING_KEY);
			decryptedString = Encryption.decryptString(encryptedString,
					A_GOOD_STRING_KEY);
		} catch (EncryptionException e) {
			e.printStackTrace();
		}
		assertEquals(THING_TO_BE_ENCRYPTED, decryptedString);
	}
	
	@Test(expected = com.capitalone.dashboard.util.EncryptionException.class)
	public void testDecryptionWithBadStringKey() throws Exception {
		String encryptedString = Encryption.encryptString(THING_TO_BE_ENCRYPTED,
				A_GOOD_STRING_KEY);
		@SuppressWarnings("unused")
		String decryptedString = Encryption.decryptString(encryptedString, A_BAD_STRING_KEY);

	}
}
