package com.capitalone.dashboard.auth;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.google.common.collect.Sets;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFixture {

	private static final String ROLES_CLAIM = "roles";
	private static final String DETAILS_CLAIM = "details";
	
	public static void createAuthentication(String username) {
		Collection<GrantedAuthority> authorities = Sets.newHashSet(new SimpleGrantedAuthority("ROLE_ADMIN"));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, "password", authorities );
		auth.setDetails(AuthType.STANDARD.name());
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
	}

	public static Authentication getAuthentication(String username) {
		createAuthentication(username);
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static String getJwtToken(String username, String secret, long expirationTime) {
		Authentication authentication = getAuthentication(username);
		Collection<UserRole> authorities = Sets.newHashSet(UserRole.ROLE_ADMIN, UserRole.ROLE_USER);
		return Jwts.builder().setSubject(authentication.getName())
		.claim(DETAILS_CLAIM, authentication.getDetails())
		.claim(ROLES_CLAIM, authorities)
		.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
		.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
}
