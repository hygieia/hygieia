package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.AuthenticationService;
import com.capitalone.dashboard.service.UserInfoService;
import com.capitalone.dashboard.util.AuthenticationUtil;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class StandardLoginFilterTest {

	private static final String USERNAME = "testUser";
	private static final String PASSWORD = "testPassword";
	private static final String LOGIN_JSON = "{\"username\": \"" + USERNAME + "\", \"password\": \"" + PASSWORD + "\"}";
	
	@Mock
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Mock
	private UserInfoService userInfoService;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private AuthenticationService authenticationService;
	
	private MockHttpServletRequest request;
	private StandardLoginFilter filter;
	
	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		request.setContent(LOGIN_JSON.getBytes());
		filter = new StandardLoginFilter("/login", authenticationManager, authenticationService, tokenAuthenticationService, userInfoService);
		
		when(authenticationService.authenticate(USERNAME, PASSWORD)).thenReturn(createAuthentication());
		when(userInfoService.getAuthorities(USERNAME, AuthType.STANDARD)).thenReturn(Sets.newHashSet(UserRole.ROLE_ADMIN, UserRole.ROLE_USER));
	}

	@Test
	public void testGetAuthType() {
		assertEquals(AuthType.STANDARD, filter.getAuthType());
	}
	
	@Test
	public void testAttemptAuthentication() throws Exception {
		Authentication result = filter.attemptAuthentication(request, null);
		assertEquals(UsernamePasswordAuthenticationToken.class, result.getClass());
		assertEquals(USERNAME, result.getName());
		assertEquals(PASSWORD, result.getCredentials().toString());
		
		verify(authenticationService).authenticate(USERNAME, PASSWORD);
	}
	
	@Test
	public void testSuccessfulAuthentication() throws Exception {
		ArgumentCaptor<PreAuthenticatedAuthenticationToken> captorAuthentication = ArgumentCaptor.forClass(PreAuthenticatedAuthenticationToken.class);
		filter.successfulAuthentication(request, null, null, createAuthentication());
		
		verify(tokenAuthenticationService).addAuthentication(any(HttpServletResponse.class), captorAuthentication.capture());
		
		PreAuthenticatedAuthenticationToken capture = captorAuthentication.getValue();
		assertEquals(USERNAME, capture.getName());
		assertEquals(PASSWORD, capture.getCredentials().toString());
		
		Collection<GrantedAuthority> authorities = capture.getAuthorities();
		assertEquals(2, authorities.size());
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
		
		Map details = (Map<Object, Object>) capture.getDetails();
		assertEquals(HashMap.class, details.getClass());
		assertEquals(1, details.keySet().size());
		assertTrue(details.containsKey(AuthenticationUtil.AUTH_TYPE));
		assertEquals(AuthType.STANDARD, details.get(AuthenticationUtil.AUTH_TYPE));
	}

	private Authentication createAuthentication() {
		return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
	}
	
}
