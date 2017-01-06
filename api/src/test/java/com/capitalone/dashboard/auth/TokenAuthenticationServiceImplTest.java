package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TokenAuthenticationServiceImpl.class)
@TestPropertySource(locations="classpath:jwt.properties")
public class TokenAuthenticationServiceImplTest {

	private static final String USERNAME = "username";
	private static final String AUTHORIZATION = "Authorization";
	private static final String AUTH_PREFIX_W_SPACE = "Bearer ";
	private static final String AUTH_RESPONSE_HEADER = "X-Authentication-Token";
	
	@Autowired
	private TokenAuthenticationService service;
	
	private HttpServletResponse response;
	private HttpServletRequest request;
	
	private String secret;
	private long expirationTime;
	
	@Before
	public void setup() {
		SecurityContextHolder.clearContext();
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
		
		secret = (String) ReflectionTestUtils.getField(service, TokenAuthenticationServiceImpl.class, "secret");
		expirationTime = (long) ReflectionTestUtils.getField(service, TokenAuthenticationServiceImpl.class, "expirationTime");
	}
	
	@Test
	public void testAddAuthentication() {
		service.addAuthentication(response, AuthenticationFixture.getAuthentication(USERNAME));
		verify(response).addHeader(eq(AUTH_RESPONSE_HEADER), anyString());
	}
	
	@Test
	public void testGetAuthentication_noHeader() {
		when(request.getHeader(AUTHORIZATION)).thenReturn(null);
		assertNull(service.getAuthentication(request));
	}
	
	@Test
	public void testGetAuthentication_expiredToken() {
		when(request.getHeader(AUTHORIZATION)).thenReturn(AUTH_PREFIX_W_SPACE + AuthenticationFixture.getJwtToken(USERNAME, secret, 0));
		assertNull(service.getAuthentication(request));
	}
	
	@Test
	public void testGetAuthentication() {
		when(request.getHeader(AUTHORIZATION)).thenReturn(AUTH_PREFIX_W_SPACE + AuthenticationFixture.getJwtToken(USERNAME, secret, expirationTime));
		Authentication result = service.getAuthentication(request);
		
		assertNotNull(result);
		assertEquals(USERNAME, result.getName());
		assertEquals(2, result.getAuthorities().size());
		assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
		
		assertNotNull(result.getDetails());
		
		verify(request).getHeader(AUTHORIZATION);
	}

}
