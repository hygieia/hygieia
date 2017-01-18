package com.capitalone.dashboard.auth;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface AuthenticationResponseService {
	
	void handle(HttpServletResponse response, Authentication authentication);

}
