package com.capitalone.dashboard.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.capitalone.dashboard.model.LoginCredentials;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

	private static final String AUTHENTICATION_SCHEME_HEADER = "auth-scheme";
	private static final String LOGIN_PATH = "/login";
	
    private TokenAuthenticationService tokenAuthenticationService;
    
    public JwtLoginFilter(AuthenticationManager authenticationManager, TokenAuthenticationService tokenAuthenticationService) {
         super(new AntPathRequestMatcher(LOGIN_PATH));
         setAuthenticationManager(authenticationManager);
         this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    throws AuthenticationException, IOException, ServletException {
    	LoginCredentials credentials = new ObjectMapper().readValue(httpServletRequest.getInputStream(), LoginCredentials.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
        token.setDetails(extract(httpServletRequest));
        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
    throws IOException, ServletException {
        tokenAuthenticationService.addAuthentication(response, authentication);
    }
    
	private AuthenticationScheme extract(HttpServletRequest request) {
		String scheme = request.getHeader(AUTHENTICATION_SCHEME_HEADER);
		return AuthenticationScheme.valueOf(scheme.toUpperCase());
	}
}
