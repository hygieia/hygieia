package com.capitalone.dashboard.auth.standard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.service.AuthenticationService;

@RunWith(MockitoJUnitRunner.class)
public class StandardAuthenticationProviderTest {

	@Mock
	private AuthenticationService service;
	
	@InjectMocks
	private StandardAuthenticationProvider provider;
	
	@Test
	public void shouldAuthenticate() {
		String username = "username";
		String password = "password";
		Authentication auth = new StandardAuthenticationToken(username, password);
		when(service.authenticate(username, password)).thenReturn(auth);
		
		Authentication result = provider.authenticate(auth);
		
		assertSame(auth, result);
	}
	
	@Test
	public void shouldSupportStandardAuthToken() {
		assertTrue(provider.supports(StandardAuthenticationToken.class));
		assertFalse(provider.supports(UsernamePasswordAuthenticationToken.class));
	}

}
