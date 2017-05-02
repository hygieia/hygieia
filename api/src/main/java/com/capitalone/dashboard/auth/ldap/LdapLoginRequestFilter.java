package com.capitalone.dashboard.auth.ldap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LdapLoginRequestFilter extends UsernamePasswordAuthenticationFilter {

	public LdapLoginRequestFilter(String path, AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationResultHandler) {
		super();
		setAuthenticationSuccessHandler(authenticationResultHandler);
		setAuthenticationManager(authenticationManager);
		setFilterProcessesUrl(path);
		setAuthenticationDetailsSource(new LdapAuthenticationDetailsSource());
	}

}
