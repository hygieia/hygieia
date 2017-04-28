package com.capitalone.dashboard.auth.ldap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;

public class LdapLoginRequestFilter extends UsernamePasswordAuthenticationFilter {

	public LdapLoginRequestFilter(String path, AuthenticationManager authenticationManager, AuthenticationResultHandler authenticationResultHandler) {
		super();
		setAuthenticationSuccessHandler(authenticationResultHandler);
		setAuthenticationManager(authenticationManager);
		setFilterProcessesUrl(path);
		setAuthenticationDetailsSource(new LdapAuthenticationDetailsSource());
	}

}
