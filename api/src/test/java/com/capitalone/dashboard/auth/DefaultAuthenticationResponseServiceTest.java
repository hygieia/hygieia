package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.UserInfoService;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthenticationResponseServiceTest {
	
	private static final String USERNAME = "user1";
	private static final Object PASSWORD = "password";

	@Mock
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Mock
	private UserInfoService userInfoService;
	
	@InjectMocks
	private DefaultAuthenticationResponseService service;
	
	private MockHttpServletResponse httpServletResponse;
	private Authentication authentication;
	
	@Before
	public void setup() {
		SecurityContextHolder.clearContext();
		httpServletResponse = new MockHttpServletResponse();
		authentication = createAuthentication();
		Collection<? extends GrantedAuthority> authorities = Sets.newHashSet(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()), new SimpleGrantedAuthority(UserRole.ROLE_USER.name()));
		Mockito.doReturn(authorities).when(userInfoService).getAuthorities(USERNAME, "", "", "", "", "", AuthType.STANDARD);
	}
	
	@Test
	public void shouldHandleResponse() throws Exception {
		ArgumentCaptor<UsernamePasswordAuthenticationToken> captorAuthentication = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
		service.handle(httpServletResponse, authentication);
		
		verify(tokenAuthenticationService).addAuthentication(any(HttpServletResponse.class), captorAuthentication.capture());
		
		UsernamePasswordAuthenticationToken capture = captorAuthentication.getValue();
		assertEquals(USERNAME, capture.getName());
		assertEquals(PASSWORD, capture.getCredentials().toString());
		
		Collection<GrantedAuthority> authorities = capture.getAuthorities();
		assertEquals(2, authorities.size());
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
		
		AuthType details = (AuthType) capture.getDetails();
		assertEquals(AuthType.STANDARD, details);
	}

	private Authentication createAuthentication() {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
		usernamePasswordAuthenticationToken.setDetails(AuthType.STANDARD);
		return usernamePasswordAuthenticationToken;
	}

}
