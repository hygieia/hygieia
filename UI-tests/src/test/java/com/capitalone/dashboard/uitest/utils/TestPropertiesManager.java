package com.capitalone.dashboard.uitest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;


public class TestPropertiesManager {

	private static Properties properties;
	private final static String secret = System.getProperty("UITEST_SECRET");
	
	private static Properties getProperties() {
		if(properties == null) {
			properties = buildProperties();
		}
		return properties;
	}

	private static Properties buildProperties() {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(TestPropertiesManager.class.getClassLoader().getResource("uitest.properties").getFile()));
			properties.load(inputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return properties;
	}
	
	private static String getStringProperty(String propertyName){
	    String override = System.getProperty((String) propertyName);
	    if (StringUtils.isNotEmpty(override)) { return override; }
		return (String) getProperties().get(propertyName);
	}

	public static String getExistingUserUsername() {
		return getStringProperty("UITEST_EXISTING_USER");
	}

	public static String getExistingUserPassword() {
		String existingUserEncryptedPassword = getStringProperty("UITEST_EXISTING_USERS_PASSWORD");
		try {
			return Encryption.decryptString(existingUserEncryptedPassword, secret);
		} catch (EncryptionException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getProxyUrl() {
		return getStringProperty("UITEST_PROXY_URL");
	}

	public static String getProxyUsername() {
		return getStringProperty("UITEST_PROXY_USERNAME");
	}

	public static String getProxyPassword() {
		try {
			return Encryption.decryptString(getStringProperty("UITEST_PROXY_PASSWORD"), secret);
		} catch (EncryptionException e) {
			e.printStackTrace();
		}
		return "";
	}

}
