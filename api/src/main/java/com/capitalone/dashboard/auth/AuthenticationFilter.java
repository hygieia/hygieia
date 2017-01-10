package com.capitalone.dashboard.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.capitalone.dashboard.model.AuthType;

public abstract class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	
	private final SecurityService securityService;
	
	protected AuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, SecurityService securityService) {
		super(requiresAuthenticationRequestMatcher);
		this.securityService = securityService;
	}

	@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
    throws IOException, ServletException {
		securityService.inflateResponse(response, authentication, getAuthType());
    }
	
	public abstract AuthType getAuthType();
	
}
