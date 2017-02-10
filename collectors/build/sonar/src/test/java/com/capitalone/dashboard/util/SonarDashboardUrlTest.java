package com.capitalone.dashboard.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SonarDashboardUrlTest {

	private static final String SLASH = "/";
	private static final String INSTANCE_URL = "http://your.company.org/sonar";
	private static final String PATH = "/dashboard/index/";
	private static final String PROJECT_ID = "8675309";
	
	private static final String EXPECTED = INSTANCE_URL + PATH + PROJECT_ID; 
	
	@Test
	public void testWithoutTrailingSlash() {
		assertEquals(EXPECTED, new SonarDashboardUrl(INSTANCE_URL, PROJECT_ID).toString());
	}
	
	@Test
	public void testWithTrailingSlash() {
		assertEquals(EXPECTED, new SonarDashboardUrl(INSTANCE_URL + SLASH, PROJECT_ID).toString());
	}
	
}
