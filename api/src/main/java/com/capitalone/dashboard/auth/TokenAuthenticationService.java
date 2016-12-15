package com.capitalone.dashboard.auth;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;

import com.capitalone.dashboard.model.AuthenticatedUser;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {

	private static final String AUTHORIZATION = "Authorization";
	private static final String AUTH_PREFIX_W_SPACE = "Bearer ";
	private static final String AUTH_RESPONSE_HEADER = "X-Authentication-Token";
	
	private long EXPIRATIONTIME = 1000 * 60 * 60 * 24 * 10;
	private String secret = "ThisIsASecret";
	
	public void addAuthentication(HttpServletResponse response, String username) {
		String JWT = Jwts.builder().setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		response.addHeader(AUTH_RESPONSE_HEADER, JWT);
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
