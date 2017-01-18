package com.capitalone.dashboard.auth.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.model.AuthType;

public interface TokenAuthenticationService {

	void addAuthentication(HttpServletResponse response, Authentication authentication, AuthType authType);
	Authentication getAuthentication(HttpServletRequest request);

}
