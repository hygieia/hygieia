package com.capitalone.dashboard.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthProperties;
import com.google.common.collect.Sets;

@Component
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

	private static final String AUTHORIZATION = "Authorization";
	private static final String AUTH_PREFIX_W_SPACE = "Bearer ";
	private static final String AUTH_RESPONSE_HEADER = "X-Authentication-Token";
	private static final String ROLES_CLAIM = "roles";
	private static final String DETAILS_CLAIM = "details";

	private AuthProperties tokenAuthProperties;
	
	@Autowired
	public TokenAuthenticationServiceImpl(AuthProperties tokenAuthProperties) {
		this.tokenAuthProperties = tokenAuthProperties;
	}
	
	@Override
	public void addAuthentication(HttpServletResponse response, Authentication authentication) {
		String jwt = Jwts.builder().setSubject(authentication.getName())
				.claim(DETAILS_CLAIM, authentication.getDetails())
				.claim(ROLES_CLAIM, getRoles(authentication.getAuthorities()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenAuthProperties.getExpirationTime()))
				.signWith(SignatureAlgorithm.HS512, tokenAuthProperties.getSecret()).compact();
		response.addHeader(AUTH_RESPONSE_HEADER, jwt);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Authentication getAuthentication(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTHORIZATION);
		if (StringUtils.isBlank(authHeader)) return null;
		
		String token = StringUtils.removeStart(authHeader, AUTH_PREFIX_W_SPACE);
		try {
			Claims claims = Jwts.parser().setSigningKey(tokenAuthProperties.getSecret()).parseClaimsJws(token).getBody();
			String username = claims.getSubject();
			Collection<? extends GrantedAuthority> authorities = getAuthorities(claims.get(ROLES_CLAIM, Collection.class));
			PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(username, null, authorities);
			authentication.setDetails(claims.get(DETAILS_CLAIM));
			
			return authentication;
			
		} catch (ExpiredJwtException e) {
			return null;
		}
	}
	
	private Collection<String> getRoles(Collection<? extends GrantedAuthority> authorities) {
		Collection<String> roles = Sets.newHashSet();
		authorities.forEach(authority -> {
			roles.add(authority.getAuthority()); 
		});
		
		return roles;
	}
	
	private Collection<? extends GrantedAuthority> getAuthorities(Collection<String> roles) {
		Collection<GrantedAuthority> authorities = Sets.newHashSet();
		roles.forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});
		
		return authorities;
	}

}
