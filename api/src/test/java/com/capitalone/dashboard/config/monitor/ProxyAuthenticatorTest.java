package com.capitalone.dashboard.config.monitor;

import static org.junit.Assert.assertEquals;

import java.net.PasswordAuthentication;

import org.junit.Test;

public class ProxyAuthenticatorTest {

	private ProxyAuthenticator proxyAuthenticator;
	
	@Test
	public void testGetPasswordAuthentication() {
		PasswordAuthentication expected = new PasswordAuthentication("username", "password".toCharArray());
		proxyAuthenticator = new ProxyAuthenticator(expected);
		
		assertEquals(expected, proxyAuthenticator.getPasswordAuthentication());
	}
	
}
