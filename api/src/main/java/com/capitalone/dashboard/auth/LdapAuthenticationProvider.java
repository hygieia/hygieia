package com.capitalone.dashboard.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.service.AuthenticationService;

@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {
	
	private AuthenticationService authenticationService;

	@Autowired
	public LdapAuthenticationProvider(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authenticationService.authenticate(authentication.getName(), authentication.getCredentials().toString());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(LdapAuthenticationToken.class);
	}

}
