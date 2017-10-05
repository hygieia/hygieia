package com.capitalone.dashboard.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.model.AuthType;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {
	
	private AuthType standardAuthType = AuthType.STANDARD;
	private AuthType ldapAuthType = AuthType.LDAP;

	@Mock
	private AuthProperties authProperties;
	
	@InjectMocks
	private AuthenticationController authController;

	@Test
	public void multipleAuthTypes() throws Exception {
		List<AuthType> expectedReturn = new ArrayList<AuthType>();
		
		expectedReturn.add(standardAuthType);
		expectedReturn.add(ldapAuthType);

		when(authProperties.getAuthenticationProviders()).thenReturn(expectedReturn);
		
		List<AuthType> result = authController.getAuthenticationProviders();
		
		assertNotNull(result);
		assertTrue(result.equals(expectedReturn));
		verify(authProperties).getAuthenticationProviders();
	}
	
	@Test
	public void oneType() throws Exception {
		List<AuthType> expectedReturn = new ArrayList<AuthType>();

		expectedReturn.add(ldapAuthType);

		when(authProperties.getAuthenticationProviders()).thenReturn(expectedReturn);
		
		List<AuthType> result = authController.getAuthenticationProviders();
		
		assertNotNull(result);
		assertTrue(result.equals(expectedReturn));
		verify(authProperties).getAuthenticationProviders();
	}
	
	@Test
	public void zeroTypes() throws Exception {
		List<AuthType> expectedReturn = new ArrayList<AuthType>();

		when(authProperties.getAuthenticationProviders()).thenReturn(expectedReturn);
		
		List<AuthType> result = authController.getAuthenticationProviders();
		
		assertNotNull(result);
		assertTrue(result.equals(expectedReturn));
		verify(authProperties).getAuthenticationProviders();
	}
	
	
}
