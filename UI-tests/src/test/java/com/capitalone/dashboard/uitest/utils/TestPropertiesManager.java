package com.capitalone.dashboard.uitest.utils;

public class TestPropertiesManager {

	public static String getExistingUserUsername() {
		return System.getProperty("UITEST_EXISTING_USERNAME");
	}

	public static String getExistingUserPassword() {
		return System.getProperty("UITEST_EXISTING_PASSWORD");
	}

}
