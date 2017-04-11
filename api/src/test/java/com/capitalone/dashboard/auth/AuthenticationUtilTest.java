package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.model.AuthType;

public class AuthenticationUtilTest {
	
	private AuthenticationUtil util = new AuthenticationUtil();
	
	@Before
	public void setup() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", "password");
		authentication.setDetails(AuthType.STANDARD.name());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testConstructor() {
		assertNotNull(util);
	}
	
	@Test
	public void shouldGetUsername() {
		assertEquals("user", AuthenticationUtil.getUsernameFromContext());
	}
	
	@Test
	public void shouldGetAuthType() {
		assertEquals(AuthType.STANDARD, AuthenticationUtil.getAuthTypeFromContext());
	}
	
	@Test
	public void nullAuth() {
		SecurityContextHolder.clearContext();
		assertNull(AuthenticationUtil.getUsernameFromContext());
		assertNull(AuthenticationUtil.getAuthTypeFromContext());
	}

}
