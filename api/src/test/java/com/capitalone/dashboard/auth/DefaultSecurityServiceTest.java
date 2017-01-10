package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.UserInfoService;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSecurityServiceTest {
	
	private static final String USERNAME = "user1";
	private static final Object PASSWORD = "password";

	@Mock
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Mock
	private UserInfoService userInfoService;
	
	@InjectMocks
	private DefaultSecurityService service;
	
	private MockHttpServletResponse httpServletResponse;
	private Authentication authentication;
	
	@Before
	public void setup() {
		SecurityContextHolder.clearContext();
		httpServletResponse = new MockHttpServletResponse();
		authentication = createAuthentication();
		when(userInfoService.getAuthorities(USERNAME, AuthType.STANDARD)).thenReturn(Sets.newHashSet(UserRole.ROLE_ADMIN, UserRole.ROLE_USER));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldInflateResponse() throws Exception {
		ArgumentCaptor<PreAuthenticatedAuthenticationToken> captorAuthentication = ArgumentCaptor.forClass(PreAuthenticatedAuthenticationToken.class);
		service.inflateResponse(httpServletResponse, authentication, AuthType.STANDARD);
		
		verify(tokenAuthenticationService).addAuthentication(any(HttpServletResponse.class), captorAuthentication.capture());
		
		PreAuthenticatedAuthenticationToken capture = captorAuthentication.getValue();
		assertEquals(USERNAME, capture.getName());
		assertEquals(PASSWORD, capture.getCredentials().toString());
		
		Collection<GrantedAuthority> authorities = capture.getAuthorities();
		assertEquals(2, authorities.size());
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
		
		String details = (String) capture.getDetails();
		assertEquals(AuthType.STANDARD, AuthType.valueOf(details));
	}

	private Authentication createAuthentication() {
		return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
	}

}
