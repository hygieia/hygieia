package com.capitalone.dashboard.tools;

import static org.junit.Assert.*;

import org.junit.Test;

import com.capitalone.dashboard.tools.utils.EncryptionTool;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;

/**
 * Test Cases for Tools App
 */
public class AppTest {

    /**
     * Verify encryption / decryption work
     * 
     * @throws EncryptionException
     */
    @Test
    public void testEncryption() throws EncryptionException {
        String key = EncryptionTool.genkey();
        String original = new String("The Quick Brown Fox");
        String encrypted = EncryptionTool.encrypt(original, key);
        assertNotEquals(original, encrypted); // that would be bad

        String decrypted = Encryption.decryptString(encrypted, key);
        assertEquals(original, decrypted);

    }

    /**
     * GenKey should generate a 32 bit key
     * 
     * @throws EncryptionException
     */
    @Test
    public void testKeyGeneration() throws EncryptionException {
        String key = EncryptionTool.genkey();
        assertNotNull(key);
        assertEquals(32, key.getBytes().length);
    }

}
