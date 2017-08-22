package com.capitalone.dashboard.auth;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;

public interface AuthenticationResponseService {
	
	void handle(HttpServletResponse response, Authentication authentication);

}
