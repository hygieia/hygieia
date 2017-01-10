package com.capitalone.dashboard.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
import com.capitalone.dashboard.util.AuthenticationUtil;

@Component
public class SecurityService {
	
	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserInfoService userInfoService;
	
	@Autowired
	public SecurityService(TokenAuthenticationService tokenAuthenticationService, UserInfoService userInfoService) {
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.userInfoService = userInfoService;
	}
	
	public void inflateResponse(HttpServletResponse response, Authentication authentication, AuthType authType) {
		Collection<UserRole> authorities = userInfoService.getAuthorities(authentication.getName(), authType);
		PreAuthenticatedAuthenticationToken inflatedAuthentication = new PreAuthenticatedAuthenticationToken(authentication.getName(), authentication.getCredentials().toString(), createAuthorities(authorities));
		inflatedAuthentication.setDetails(createDetails(authType));
        tokenAuthenticationService.addAuthentication(response, inflatedAuthentication);
	}
	
	private Map<Object, Object> createDetails(AuthType authType) {
		Map<Object, Object> details = new HashMap<>();
		details.put(AuthenticationUtil.AUTH_TYPE, authType);
		return details;
	}
	
	private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		authorities.forEach(authority -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())); 
		});
		
		return grantedAuthorities;
	}

}
