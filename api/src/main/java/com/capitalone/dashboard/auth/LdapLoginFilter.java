package com.capitalone.dashboard.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.capitalone.dashboard.model.LoginCredentials;

public class LdapLoginFilter extends AbstractAuthenticationProcessingFilter{
	
	private TokenAuthenticationService tokenAuthenticationService;

	public LdapLoginFilter(String path, AuthenticationManager authenticationManager, TokenAuthenticationService tokenAuthenticationService) {
		super(new AntPathRequestMatcher(path));
		setAuthenticationManager(authenticationManager);
		this.tokenAuthenticationService = tokenAuthenticationService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		LoginCredentials credentials = new ObjectMapper().readValue(request.getInputStream(), LoginCredentials.class);
		LdapAuthenticationToken token = new LdapAuthenticationToken(credentials.getUsername(), credentials.getPassword());
		return token;
	}
	
//	@Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
//    throws IOException, ServletException {
//        tokenAuthenticationService.addAuthentication(response, authentication);
//    }

}
