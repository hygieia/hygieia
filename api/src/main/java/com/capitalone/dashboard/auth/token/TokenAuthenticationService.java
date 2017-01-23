package com.capitalone.dashboard.auth.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface TokenAuthenticationService {

	void addAuthentication(HttpServletResponse response, Authentication authentication);
	Authentication getAuthentication(HttpServletRequest request);

}
