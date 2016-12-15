package com.capitalone.dashboard.auth;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.web.util.WebUtils;

import com.capitalone.dashboard.model.AuthenticatedUser;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {

	private long EXPIRATIONTIME = 1000 * 60 * 60 * 24 * 10;
	private String secret = "ThisIsASecret";
	private String headerString = "Authorization";
	private String cookieAuthString = "XSRF-TOKEN";
	private String requestCookieName = "XSRF-TOKEN";

	public void addAuthentication(HttpServletResponse response, String username) {
		String JWT = Jwts.builder().setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		Cookie tokenCookie = new Cookie(cookieAuthString, JWT);
		response.addCookie(tokenCookie);
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, requestCookieName);
		// TODO: verify cookie is constructed correctly
		
		if (cookie != null) {
			String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(cookie.getValue()).getBody().getSubject();
			if (username != null) {
				return new AuthenticatedUser(username);
			}
		}
		return null;
	}
}
