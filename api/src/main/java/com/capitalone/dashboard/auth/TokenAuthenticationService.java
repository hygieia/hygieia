package com.capitalone.dashboard.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.model.AuthenticatedUser;

public class TokenAuthenticationService {

	private static final String AUTHORIZATION = "Authorization";
	private static final String AUTH_PREFIX_W_SPACE = "Bearer ";
	private static final String AUTH_RESPONSE_HEADER = "X-Authentication-Token";
	private final long expirationTime;
	private final String secret;
		
	public TokenAuthenticationService(long expirationTime, String secret){
		this.expirationTime = expirationTime;
		this.secret = secret;
	}

	public void addAuthentication(HttpServletResponse response, String username) {
		String jwt = Jwts.builder().setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		response.addHeader(AUTH_RESPONSE_HEADER, jwt);
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION);
		
		if (StringUtils.isNotBlank(header)) {
			String token = StringUtils.removeStart(header, AUTH_PREFIX_W_SPACE);
			String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
			if (username != null) {
				return new AuthenticatedUser(username);
			}
		}
		return null;
	}
}
