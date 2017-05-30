package com.capitalone.dashboard.auth.ldap;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;

@RunWith(MockitoJUnitRunner.class)
public class LdapLoginRequestFilterTest {
	
	@Mock
	private AuthenticationManager manager;
	
	@Mock
	private AuthenticationResultHandler authenticationResultHandler;

	@Test
	public void test() {
		LdapLoginRequestFilter filter = new LdapLoginRequestFilter("/login/ldap", manager, authenticationResultHandler);
		assertNotNull(filter);
	}

}
