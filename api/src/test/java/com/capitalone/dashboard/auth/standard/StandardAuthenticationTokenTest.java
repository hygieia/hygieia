package com.capitalone.dashboard.auth.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;

public class StandardAuthenticationTokenTest {

	private static final String CREDS = "creds";
	private static final String PRINCIPAL = "principal";

	@Test
	public void shouldCreateTokenNotAuthenticated() {
		Authentication auth = new StandardAuthenticationToken(PRINCIPAL, CREDS);
		assertEquals(PRINCIPAL, auth.getPrincipal());
		assertEquals(CREDS, auth.getCredentials());
		assertFalse(auth.isAuthenticated());
	}
	
	@Test
	public void shouldCreateTokenAuthenitcated() {
		Collection<? extends GrantedAuthority> authorities = Sets.newHashSet();
		Authentication auth = new StandardAuthenticationToken(PRINCIPAL, CREDS, authorities);
		assertEquals(PRINCIPAL, auth.getPrincipal());
		assertEquals(CREDS, auth.getCredentials());
		assertTrue(auth.isAuthenticated());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldSetAuthenticatedException() {
		Authentication auth = new StandardAuthenticationToken(PRINCIPAL, CREDS);
		auth.setAuthenticated(true);
	}
	
	@Test
	public void shouldSetAuthenticatedToFalse() {
		Collection<? extends GrantedAuthority> authorities = Sets.newHashSet();
		Authentication auth = new StandardAuthenticationToken(PRINCIPAL, CREDS, authorities);
		auth.setAuthenticated(false);
		assertFalse(auth.isAuthenticated());
	}
	
	@Test
	public void shouldEraseCreds() {
		Collection<? extends GrantedAuthority> authorities = Sets.newHashSet();
		StandardAuthenticationToken auth = new StandardAuthenticationToken(PRINCIPAL, CREDS, authorities);
		auth.eraseCredentials();
		assertNull(auth.getCredentials());
	}

}
