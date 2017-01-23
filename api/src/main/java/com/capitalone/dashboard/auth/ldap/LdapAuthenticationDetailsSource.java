package com.capitalone.dashboard.auth.ldap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import com.capitalone.dashboard.model.AuthType;

public class LdapAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, AuthType> {

	@Override
	public AuthType buildDetails(HttpServletRequest context) {
		return AuthType.LDAP;
	}

}
