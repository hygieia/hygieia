package com.capitalone.dashboard.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface TokenAuthenticationService {

	public void addAuthentication(HttpServletResponse response, String username);

	public Authentication getAuthentication(HttpServletRequest request);

}
