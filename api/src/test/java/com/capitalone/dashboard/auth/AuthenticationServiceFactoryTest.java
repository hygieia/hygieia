package com.capitalone.dashboard.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.service.AuthenticationService;
import com.capitalone.dashboard.service.DefaultAuthenticationServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceFactoryTest {

	@InjectMocks
	private AuthenticationServiceFactory authenticationServiceFactory;
	
	@Mock
	private DefaultAuthenticationServiceImpl standardImpl;
	
	@Before
	public void setup() {
	}
	
	@Test
	public void testStandard() {
		AuthenticationService result = authenticationServiceFactory.getAuthenticationService(AuthenticationScheme.STANDARD);
		assertNotNull(result);
		assertEquals(standardImpl, result);
		assertTrue(DefaultAuthenticationServiceImpl.class.isAssignableFrom(result.getClass()));
	}

}
