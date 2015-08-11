package com.capitalone.dashboard.service;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.util.DefaultPropertiesSupplier;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;

@Service
public class EncryptionServiceImpl implements EncryptionService {

	public EncryptionServiceImpl() {

	}

	@Override
	public String encrypt(String message) {
		Properties properties = new DefaultPropertiesSupplier().get();
		String key = properties.getProperty("key", "");
		String returnString = "";
		if (!"".equals(key)) {
			try {
				returnString = Encryption.encryptString(message, key);
			} catch (EncryptionException e) {
				returnString = "ERROR";
			}
		} else {
			returnString = "ERROR";
		}
		return returnString;
	}

}
