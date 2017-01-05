package com.capitalone.dashboard.auth;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.UserInfoService;
import com.capitalone.dashboard.util.AuthenticationUtil;

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
		Collection<UserRole> authorities = userInfoService.getAuthorities(authentication.getName(), getAuthType());
		PreAuthenticatedAuthenticationToken inflatedAuthentication = new PreAuthenticatedAuthenticationToken(authentication.getName(), authentication.getCredentials().toString(), createAuthorities(authorities));
		inflatedAuthentication.setDetails(createDetails(getAuthType()));
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
	
	public abstract AuthType getAuthType();
	
}
