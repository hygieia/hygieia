package com.capitalone.dashboard.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationSchemeExtractor {

	private static final String AUTHENTICATION_SCHEME_HEADER = "auth-scheme";
	
	public AuthenticationScheme extract(HttpServletRequest request) {
		String scheme = request.getHeader(AUTHENTICATION_SCHEME_HEADER);
		return AuthenticationScheme.valueOf(scheme.toUpperCase());
	}
	
}
