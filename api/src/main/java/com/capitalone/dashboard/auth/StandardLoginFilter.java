package com.capitalone.dashboard.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.model.LoginCredentials;

public class StandardLoginFilter extends AbstractLoginFilter {

	public StandardLoginFilter(String path) {
		super(path);
	}

	@Override
	public Authentication createAuthentication(HttpServletRequest httpServletRequest) {
		LoginCredentials credentials;
		try {
			credentials = new ObjectMapper().readValue(httpServletRequest.getInputStream(), LoginCredentials.class);
		} catch (IOException e) {
			throw new AuthenticationCredentialsNotFoundException(e.getMessage());
		}
		return new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
	}

}
