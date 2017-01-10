package com.capitalone.dashboard.auth;

import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.UserInfoService;

@Component
public class DefaultSecurityService implements SecurityService {
	
	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserInfoService userInfoService;
	
	@Autowired
	public DefaultSecurityService(TokenAuthenticationService tokenAuthenticationService, UserInfoService userInfoService) {
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.userInfoService = userInfoService;
	}
	
	@Override
	public void inflateResponse(HttpServletResponse response, Authentication authentication, AuthType authType) {
		Collection<UserRole> authorities = userInfoService.getAuthorities(authentication.getName(), authType);
		PreAuthenticatedAuthenticationToken inflatedAuthentication = new PreAuthenticatedAuthenticationToken(authentication.getName(), authentication.getCredentials().toString(), createAuthorities(authorities));
		inflatedAuthentication.setDetails(authType.name());
        tokenAuthenticationService.addAuthentication(response, inflatedAuthentication);
	}
	
	private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		authorities.forEach(authority -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())); 
		});
		
		return grantedAuthorities;
	}

}
