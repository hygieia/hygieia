package com.capitalone.dashboard.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LdapAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = -7538973620264383361L;

	public LdapAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

}
