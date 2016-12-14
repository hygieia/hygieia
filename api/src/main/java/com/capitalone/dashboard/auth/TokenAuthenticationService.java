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

     private long EXPIRATIONTIME = 1000 * 60 * 2;
     private String secret = "ThisIsASecret";
     private String tokenPrefix = "Bearer";
     private String headerString = "Authorization";
     public void addAuthentication(HttpServletResponse response, String username) {
         String JWT = Jwts.builder()
             .setSubject(username)
             .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
             .signWith(SignatureAlgorithm.HS512, secret)
             .compact();
         response.addHeader(headerString, tokenPrefix + " " + JWT);
     }

     public Authentication getAuthentication(HttpServletRequest request) {
         String authHeader = request.getHeader(headerString);
         if(authHeader == null) { return null; }
    	 String token = StringUtils.split(authHeader, " ")[1];
         if (token != null) {
             String username = Jwts.parser()
                 .setSigningKey(secret)
                 .parseClaimsJws(token)
                 .getBody()
                 .getSubject();
             if (username != null) {
                 return new AuthenticatedUser(username);
             }
         }
         return null;
     }
 }
