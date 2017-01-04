package com.capitalone.dashboard.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.LoginCredentials;
import com.capitalone.dashboard.service.AuthenticationService;
import com.capitalone.dashboard.service.UserInfoService;

public class StandardLoginFilter extends AuthenticationFilter {

	private final AuthenticationService authenticationService; 
    
    public StandardLoginFilter(String path, AuthenticationManager authenticationManager, AuthenticationService authenticationService, TokenAuthenticationService tokenAuthenticationService, UserInfoService userInfoService) {
         super(new AntPathRequestMatcher(path), tokenAuthenticationService, userInfoService);
         setAuthenticationManager(authenticationManager);
         this.authenticationService = authenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    throws AuthenticationException, IOException, ServletException {
    	LoginCredentials credentials = new ObjectMapper().readValue(httpServletRequest.getInputStream(), LoginCredentials.class);
        return authenticationService.authenticate(credentials.getUsername(), credentials.getPassword());
    }

	@Override
	public AuthType getAuthType() {
		return AuthType.STANDARD;
	}
    
}
