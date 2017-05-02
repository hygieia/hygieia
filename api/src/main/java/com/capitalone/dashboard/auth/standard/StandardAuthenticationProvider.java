package com.capitalone.dashboard.auth.standard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.service.AuthenticationService;

@Component
public class StandardAuthenticationProvider implements AuthenticationProvider {
	
	private final AuthenticationService authService;
	
	@Autowired
	public StandardAuthenticationProvider(AuthenticationService authService) {
		this.authService = authService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authService.authenticate(authentication.getName(), (String)authentication.getCredentials());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return StandardAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
