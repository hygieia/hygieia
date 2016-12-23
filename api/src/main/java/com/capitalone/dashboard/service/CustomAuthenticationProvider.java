package com.capitalone.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthenticationScheme;
import com.capitalone.dashboard.auth.AuthenticationServiceFactory;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private AuthenticationServiceFactory authenticationServiceFactory;

	@Autowired
	public CustomAuthenticationProvider(AuthenticationServiceFactory authenticationServiceFactory) {
		this.authenticationServiceFactory = authenticationServiceFactory;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		AuthenticationService authenticationService = authenticationServiceFactory.getAuthenticationService((AuthenticationScheme) authentication.getDetails());
		return authenticationService.authenticate(authentication.getName(), authentication.getCredentials().toString());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
