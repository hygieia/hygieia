package com.capitalone.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private AuthenticationService authenticationService;

	@Autowired
	public CustomAuthenticationProvider(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authenticationService.authenticate(authentication.getName(), authentication.getCredentials().toString());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
