package com.capitalone.dashboard.auth.apitoken;

import com.capitalone.dashboard.service.ApiTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ApiTokenAuthenticationProvider implements AuthenticationProvider {

	private final ApiTokenService apiTokenService;

	@Autowired
	public ApiTokenAuthenticationProvider(ApiTokenService apiTokenService) {
		this.apiTokenService = apiTokenService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return apiTokenService.authenticate(authentication.getName(), (String)authentication.getCredentials());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return ApiTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
