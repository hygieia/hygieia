package com.capitalone.dashboard.tools.utils;

import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;

public class EncryptionTool {

    public static String encrypt(String decrypt, String key)
            throws EncryptionException {
        return Encryption.encryptString(decrypt, key);
    }

    public static String genkey() throws EncryptionException {
        return Encryption.getStringKey();
    }
}
