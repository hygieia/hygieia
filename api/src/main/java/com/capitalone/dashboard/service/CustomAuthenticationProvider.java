package com.capitalone.dashboard.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthenticationScheme;
import com.capitalone.dashboard.auth.AuthenticationSchemeExtractor;
import com.capitalone.dashboard.auth.AuthenticationServiceFactory;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private AuthenticationServiceFactory authenticationServiceFactory;
	private AuthenticationSchemeExtractor authenticationSchemeExtractor;

	@Autowired
	public CustomAuthenticationProvider(AuthenticationServiceFactory authenticationServiceFactory,
										AuthenticationSchemeExtractor authenticationSchemeExtractor) {
		this.authenticationServiceFactory = authenticationServiceFactory;
		this.authenticationSchemeExtractor = authenticationSchemeExtractor;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		AuthenticationScheme scheme = authenticationSchemeExtractor.extract((HttpServletRequest) authentication.getDetails());
		AuthenticationService authenticationService = authenticationServiceFactory.getAuthenticationService(scheme);
		return authenticationService.authenticate(authentication.getName(), authentication.getCredentials().toString());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
