package com.capitalone.dashboard.auth;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.Sets;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

	private static final String ADMIN_CLAIM = "admin";
	private static final String AUTHORIZATION = "Authorization";
	private static final String AUTH_PREFIX_W_SPACE = "Bearer ";
	private static final String AUTH_RESPONSE_HEADER = "X-Authentication-Token";
	private final long expirationTime;
	private final String secret;

	public TokenAuthenticationServiceImpl(long expirationTime, String secret) {
		this.expirationTime = expirationTime;
		this.secret = secret;
	}

	@Override
	public void addAuthentication(HttpServletResponse response, Authentication authentication) {
		boolean admin = authentication.getAuthorities().contains(new SimpleGrantedAuthority(ADMIN_CLAIM));
		
		String jwt = Jwts.builder().setSubject(authentication.getName()).claim(ADMIN_CLAIM, admin)
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		response.addHeader(AUTH_RESPONSE_HEADER, jwt);
	}

	@Override
	public Authentication getAuthentication(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION);
		if (StringUtils.isBlank(header)) return null;
		
		Authentication authentication = null;
		String token = StringUtils.removeStart(header, AUTH_PREFIX_W_SPACE);
		try {
			Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			String username = claims.getSubject();
			boolean admin = (Boolean) claims.get(ADMIN_CLAIM);
			if (username != null) {
				Collection<GrantedAuthority> grantedAuths = Sets.newHashSet();
				if (admin) {
					grantedAuths.add(new SimpleGrantedAuthority(ADMIN_CLAIM));
				}
				authentication = new PreAuthenticatedAuthenticationToken(username, null, grantedAuths);
			}
		} catch (ExpiredJwtException e) {
			return null;
		}

		return authentication;

	}
}
