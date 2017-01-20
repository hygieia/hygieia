package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class AuthPropertiesTest {

	private AuthProperties tokenAuthProperties;
	
	@Before
	public void setup() {
		tokenAuthProperties = new AuthProperties();
	}
	
	@Test
	public void testApplyDefaultsIfNeeded() {
		tokenAuthProperties.setExpirationTime(null);
		tokenAuthProperties.setSecret(null);
		
		tokenAuthProperties.applyDefaultsIfNeeded();
		
		assertNotNull(tokenAuthProperties.getExpirationTime());
		assertNotNull(tokenAuthProperties.getSecret());
	}
	
	@Test
	public void testApplyDefaultsNotNeeded() {
		tokenAuthProperties.setExpirationTime(8L);
		tokenAuthProperties.setSecret("secret");
		tokenAuthProperties.setLdapServerUrl("url");
		tokenAuthProperties.setLdapUserDnPattern("pattern");
		
		tokenAuthProperties.applyDefaultsIfNeeded();
		
		assertEquals(Long.valueOf(8), tokenAuthProperties.getExpirationTime());
		assertEquals("secret", tokenAuthProperties.getSecret());
		assertEquals("url", tokenAuthProperties.getLdapServerUrl());
		assertEquals("pattern", tokenAuthProperties.getLdapUserDnPattern());
	}

}
