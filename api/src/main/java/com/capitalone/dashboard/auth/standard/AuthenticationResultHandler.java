package com.capitalone.dashboard.auth.standard;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.token.TokenAuthenticationResultHandler;
import com.capitalone.dashboard.auth.token.TokenAuthenticationService;

@Component
public class AuthenticationResultHandler extends TokenAuthenticationResultHandler {

    @Autowired
	public AuthenticationResultHandler(TokenAuthenticationService tokenService) {
        super(tokenService);
    }

    @Override
    protected Authentication beforeTokenCreate(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return authentication;
    }
	
}
