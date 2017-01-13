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

/**
 * This class should be extended for any other forms of authentication required.
 * The AuthenticationFilter class handles retrieving application specific information
 * and turning it all into a token which it puts in the response.
 */
public abstract class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	
	private final SecurityService securityService;
	
	protected AuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, SecurityService securityService) {
		super(requiresAuthenticationRequestMatcher);
		this.securityService = securityService;
	}

	/**
	 * On a successful authentication, this method will inflate your authentication object with application specific roles,
	 * and turn them into a token to put in the response.
	 */
	@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
    throws IOException, ServletException {
		securityService.inflateResponse(response, authentication, getAuthType());
    }
	
	/**
	 * When adding new authentication types, add it to the @AuthType enum, and return that auth type from your authentication filter.
	 * @return The type of authentication this filter uses.
	 */
	public abstract AuthType getAuthType();
	
}
