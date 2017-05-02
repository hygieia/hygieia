package com.capitalone.dashboard.auth.token;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public abstract class TokenAuthenticationResultHandler implements AuthenticationSuccessHandler {
    
    private final TokenAuthenticationService tokenService;
    
    public TokenAuthenticationResultHandler(TokenAuthenticationService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Authentication modifiedAuth = beforeTokenCreate(request, response, authentication);
        tokenService.addAuthentication(response, modifiedAuth);
    }
    
    protected abstract Authentication beforeTokenCreate(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

}
