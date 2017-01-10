package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.service.AuthenticationService;

@RunWith(MockitoJUnitRunner.class)
public class StandardLoginFilterTest {
	
	private static final String USERNAME = "testUser";
	private static final String PASSWORD = "testPassword";
	private static final String LOGIN_JSON = "{\"username\": \"" + USERNAME + "\", \"password\": \"" + PASSWORD + "\"}";

	@Mock
	private AuthenticationService authenticationService;
	
	@Mock
	private SecurityService securityService;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	private StandardLoginFilter filter;

	private MockHttpServletRequest request;
	
	@Before
	public void setup() {
		SecurityContextHolder.clearContext();
		filter = new StandardLoginFilter("/login", authenticationManager, authenticationService, securityService);
		request = new MockHttpServletRequest();
		request.setContent(LOGIN_JSON.getBytes());
		
		when(authenticationService.authenticate(USERNAME, PASSWORD)).thenReturn(createAuthentication());

	}
	
	@Test
	public void shouldAuthenticateSuccessfully() throws Exception {
		Authentication result = filter.attemptAuthentication(request, null);
		
		assertEquals(UsernamePasswordAuthenticationToken.class, result.getClass());
		assertEquals(USERNAME, result.getName());
		assertEquals(PASSWORD, result.getCredentials().toString());
		
		verify(authenticationService).authenticate(USERNAME, PASSWORD);
	}
	
	@Test
	public void testGetAuthType() {
		assertEquals(AuthType.STANDARD, filter.getAuthType());
	}
	
	private Authentication createAuthentication() {
		return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
	}

	
}
