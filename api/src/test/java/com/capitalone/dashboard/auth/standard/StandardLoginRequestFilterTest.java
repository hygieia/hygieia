package com.capitalone.dashboard.auth.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.model.AuthType;

@RunWith(MockitoJUnitRunner.class)
public class StandardLoginRequestFilterTest {
	
	@Mock
	private AuthenticationManager manager;
	
	@Mock
	private AuthenticationResultHandler resultHandler;
	
	private String path;
	
	private StandardLoginRequestFilter filter;

	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@Before
	public void setup() {
		path = "/login";
		filter = new StandardLoginRequestFilter(path, manager, resultHandler);
	}
	
	@Test
	public void shouldCreateFilter() {
		assertNotNull(filter);
	}
	
	@Test(expected = AuthenticationServiceException.class)
	public void shouldThrowExceptionIfNoPost() {
		when(request.getMethod()).thenReturn("GET");
		filter.attemptAuthentication(request, response);
	}

	@Test
	public void shouldAuthenticate() {
		when(request.getMethod()).thenReturn("POST");
		String principal = "user1";
		String credentials = "password1";
		when(request.getParameter("username")).thenReturn(principal + " ");
		when(request.getParameter("password")).thenReturn(credentials);
		Authentication auth = new StandardAuthenticationToken(principal, credentials);
		ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
		when(manager.authenticate(argumentCaptor.capture())).thenReturn(auth);
		
		Authentication result = filter.attemptAuthentication(request, response);
		
		assertNotNull(result);
		Authentication authentication = argumentCaptor.getValue();
		assertEquals(principal, authentication.getPrincipal());
		assertEquals(credentials, authentication.getCredentials());
		assertEquals(AuthType.STANDARD, authentication.getDetails());
	}
	
	@Test
	public void shouldAuthenticateWithNullUsernamePassword() {
		when(request.getMethod()).thenReturn("POST");
		String principal = null;
		String credentials = null;
		when(request.getParameter("username")).thenReturn(principal);
		when(request.getParameter("password")).thenReturn(credentials);
		Authentication auth = new StandardAuthenticationToken(principal, credentials);
		ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
		when(manager.authenticate(argumentCaptor.capture())).thenReturn(auth);
		
		Authentication result = filter.attemptAuthentication(request, response);
		
		assertNotNull(result);
		Authentication authentication = argumentCaptor.getValue();
		assertEquals("", authentication.getPrincipal());
		assertEquals("", authentication.getCredentials());
		assertEquals(AuthType.STANDARD, authentication.getDetails());
	}
}
