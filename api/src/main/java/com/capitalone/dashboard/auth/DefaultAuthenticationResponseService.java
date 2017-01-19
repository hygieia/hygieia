package com.capitalone.dashboard.auth;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.service.UserInfoService;

@Component
public class DefaultAuthenticationResponseService implements AuthenticationResponseService {
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Override
	public void handle(HttpServletResponse response, Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = userInfoService.getAuthorities(authentication.getName(), (AuthType)authentication.getDetails());
		UsernamePasswordAuthenticationToken authenticationWithAuthorities = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);
		authenticationWithAuthorities.setDetails(authentication.getDetails());
		
		tokenAuthenticationService.addAuthentication(response, authenticationWithAuthorities);

	}

}
