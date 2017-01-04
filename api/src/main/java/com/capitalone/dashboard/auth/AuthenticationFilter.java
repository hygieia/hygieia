package com.capitalone.dashboard.auth;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.capitalone.dashboard.service.UserInfoService;

public abstract class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserInfoService userInfoService;
	
	protected AuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, TokenAuthenticationService tokenAuthenticationService, UserInfoService userInfoService) {
		super(requiresAuthenticationRequestMatcher);
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.userInfoService = userInfoService;
	}

	@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
    throws IOException, ServletException {
		Collection<? extends GrantedAuthority> authorities = userInfoService.getAuthorities(authentication.getName());
		Authentication inflatedAuthentication = new PreAuthenticatedAuthenticationToken(authentication.getName(), authentication.getCredentials().toString(), authorities);
        tokenAuthenticationService.addAuthentication(response, inflatedAuthentication);
    }
	
}
