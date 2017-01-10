package com.capitalone.dashboard.auth;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.model.AuthType;

public interface SecurityService {

	void inflateResponse(HttpServletResponse response, Authentication authentication, AuthType authType);
	
}
